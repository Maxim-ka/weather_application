package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.google.android.gms.common.api.ResolvableApiException
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.AppException
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.showAlertDialog
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.showMessage
import kotlin.coroutines.CoroutineContext

private const val REQUEST_GOOGLE_COORDINATE = 1
private const val REQUEST_MY_PERMISSIONS_ACCESS_LOCATION = 10

@ExperimentalCoroutinesApi
abstract class BaseFragment : Fragment(), CoroutineScope {

    override val coroutineContext : CoroutineContext by lazy {
        Dispatchers.Main + SupervisorJob()
    }
    abstract val viewModel : BaseViewModel
    protected var navController : NavController? = null
    private lateinit var successJob : Job
    private lateinit var errorJob : Job

    @ExperimentalCoroutinesApi
    override fun onStart() {
        super.onStart()
        successJob = launch {
            viewModel.getBooleanChannel()?.consumeEach {
                renderHaveCities(it)
            }
        }
        errorJob = launch{
            viewModel.getErrorChannel()?.consumeEach { e ->
                e?.let { renderError(it) }
            }
        }
    }

    abstract fun renderHaveCities (hasCity: Boolean)

    private fun renderError (error: Throwable) {
        error.run {
            when (this) {
                is ResolvableApiException ->{
                    activity?.let { startResolutionForResult(it, REQUEST_GOOGLE_COORDINATE) }
                }
                is AppException.NoPermission ->{
                    activity?.let { ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_MY_PERMISSIONS_ACCESS_LOCATION)
                    }
                }
                is AppException.Saved -> {
                    showErrorNotification(message)
                    showAlertDialog(R.string.attention, getString(R.string.error_writing_database))
                }
                is AppException.Database -> {
                    showErrorNotification(message)
                    showAlertDialog(R.string.attention, getString(R.string.err_database_error))
                }
                else -> {
                    message?.let { showAlertDialog(R.string.warning, it)
                    } ?: showErrorNotification(cause?.message)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_MY_PERMISSIONS_ACCESS_LOCATION ->
                if (permissions.size == 2 && (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                    viewModel.addStateOfCurrentPlace()
                } else {
                    showAlertDialog(R.string.disabling_location, getString(R.string.no_permission_determine_location))
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_GOOGLE_COORDINATE -> {
                if (resultCode == AppCompatActivity.RESULT_OK){
                    viewModel.addStateOfCurrentPlace()
                } else {
                    context?.let { showMessage(bottom_navigation, getString(R.string.refusal_google),
                            ContextCompat.getColor(it, R.color.colorPrimaryDark)) }
                }
            }
        }
    }

    private fun showAlertDialog(title: Int, message: String) {
        activity?.supportFragmentManager?.fragments?.last()?.showAlertDialog(title, message)
    }

    private fun showErrorNotification(message: String?){
        message?.let {msg ->
            activity?.let {
                showMessage(it.bottom_navigation, msg, ContextCompat.getColor(it, R.color.colorAccent))
            }
        }
    }

    override fun onStop () {
        super .onStop()
        successJob.cancel()
        errorJob.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        coroutineContext.cancelChildren()
        coroutineContext.cancel()
        navController = null
    }
}