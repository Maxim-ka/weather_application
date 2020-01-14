package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.base

import android.Manifest
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.BaseException
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain.Success
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.showMessage
import kotlin.coroutines.CoroutineContext

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
            renderSuccess(model.getSuccessChannel().receive())
        }
        errorJob = launch{
            renderError(model.getErrorChannel().receive())
        }
    }

    abstract fun renderSuccess (success: Success)

    private fun renderError (error: Throwable) {
        error.run {
            when (this) {
                is ResolvableApiException ->{
                    activity?.let { startResolutionForResult(it, REQUEST_GOOGLE_COORDINATE) }
                    null
                }
                is BaseException.NoPermission ->{
                    activity?.let { ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_MY_PERMISSIONS_ACCESS_LOCATION)
                    }
                    showErrorNotification(getString(R.string.warning)+"\n"+getString(R.string.no_permission_determine_location))
                    null
                }
                is BaseException.NoNetwork -> {
                    createBundle(R.string.warning, R.string.no_network)
                }
                is TimeoutCancellationException -> {
                    createBundle(R.string.caution, R.string.unable_determine_coordinates)
                }
                is BaseException.Saved -> {
                    showErrorNotification(message)
                    createBundle(R.string.caution, R.string.error_writing_database)
                }
                is BaseException.Deleted -> {
                    showErrorNotification(message)
                    createBundle(R.string.caution, R.string.error_deleting_database)
                }
                is BaseException.Database -> {
                    createBundle(R.string.caution, R.string.database_error)
                }
                else -> {
                    showErrorNotification(message)
                    null
                }
            } ?.let {
                navController.navigate(R.id.action_global_warningDialog2, it)
            }
        }
    }

    private fun createBundle(title: Int, message: Int): Bundle {
        return Bundle().apply {
            putInt(KEY_TITLE, title)
            putInt(KEY_MESSAGE, message)
        }
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