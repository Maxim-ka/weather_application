package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.mainactivity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.*
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.WeatherViewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.showMessage
import timber.log.Timber

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener{

    private var isNoPermission: Boolean = false
    private val navController: NavController by lazy { Navigation.findNavController(this, R.id.frame_master) }
    private val model: WeatherViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setNavController()
//        PreferenceManager.setDefaultValues(baseContext, R.xml.setting, false)

//        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(baseContext) == ConnectionResult.SUCCESS){
//            FirebaseMessaging.getInstance().isAutoInitEnabled = true
//        }
        Timber.i("create activity")
    }

    private fun setNavController(){
        NavigationUI.setupWithNavController(bottom_navigation, navController)
        setupActionBarWithNavController(navController, AppBarConfiguration.Builder(navController.graph).build())
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        if(destination.id == R.id.fragmentWeather && arguments == null){
            model.getStateLastPlace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return true
    }

    override fun onStart() {
        super.onStart()
        navController.addOnDestinationChangedListener(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_MY_PERMISSIONS_ACCESS_LOCATION -> if (permissions.size == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                            grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                showMessage(bottom_navigation,"Permits received, please repeat the request", ContextCompat.getColor(baseContext, R.color.colorPrimaryDark))
            } else isNoPermission = true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        takeIf { requestCode == REQUEST_GOOGLE_COORDINATE && resultCode == RESULT_OK } ?.run {
            showMessage(bottom_navigation, "retry requesting current location", ContextCompat.getColor(baseContext, R.color.colorPrimaryDark))
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        if (isNoPermission) {
            navController.navigate(R.id.action_global_warningDialog2, Bundle().apply {
                putInt(KEY_TITLE, R.string.disabling_location)
                putInt(KEY_MESSAGE, R.string.no_permission_determine_location)
            })
            isNoPermission = false
        }
    }

    override fun onStop() {
        super.onStop()
        navController.removeOnDestinationChangedListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy")
    }
}
