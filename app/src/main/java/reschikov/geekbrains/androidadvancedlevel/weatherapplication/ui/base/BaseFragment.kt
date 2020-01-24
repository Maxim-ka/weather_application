package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base

import android.Manifest
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.REQUEST_GOOGLE_COORDINATE
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.REQUEST_MY_PERMISSIONS_ACCESS_LOCATION
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.AppException
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.showAlertDialog
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.showMessage
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
abstract class BaseFragment : Fragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Main + SupervisorJob()
    }

    abstract val model: BaseViewModel
    protected val navController : NavController by lazy { findNavController() }
    private lateinit var successJob: Job
    private lateinit var errorJob: Job

    @ExperimentalCoroutinesApi
    override fun onStart() {
        super.onStart()
        successJob = launch {
            model.getBooleanChannel().consumeEach {
                renderHaveCities(it)
            }
        }
        errorJob = launch{
            model.getErrorChannel().consumeEach {
                renderError(it)
            }
        }
    }

    abstract fun renderHaveCities (hasCity: Boolean)

    private fun renderError (error: Throwable) {
        error.run {
            when (this) {
                is AppException.Response ->{
                    showAlertDialog(R.string.warning, "server response\n ${(error as AppException.Response).error}")
                }
                is ResolvableApiException ->{
                    activity?.let { startResolutionForResult(it, REQUEST_GOOGLE_COORDINATE) }
                }
                is AppException.NoPermission ->{
                    activity?.let { ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_MY_PERMISSIONS_ACCESS_LOCATION)
                    }
                    showErrorNotification(getString(R.string.warning)+"\n"+getString(R.string.no_permission_determine_location))
                }
                is AppException.NoNetwork -> {
                    showAlertDialog(R.string.warning, getString(R.string.no_network))
                }
                is TimeoutCancellationException -> {
                    showAlertDialog(R.string.caution, getString(R.string.unable_determine_coordinates))
                }
                is AppException.Saved -> {
                    showErrorNotification(message)
                    showAlertDialog(R.string.caution, getString(R.string.error_writing_database))
                }
                is AppException.Deleted -> {
                    showErrorNotification(message)
                    showAlertDialog(R.string.caution, getString(R.string.error_deleting_database))
                }
                is AppException.Database -> {
                    showAlertDialog(R.string.caution, getString(R.string.database_error))
                }
                else -> {
                    showErrorNotification(message)
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

    override fun onDestroy () {
        super .onDestroy()
        coroutineContext.cancel()
    }
}