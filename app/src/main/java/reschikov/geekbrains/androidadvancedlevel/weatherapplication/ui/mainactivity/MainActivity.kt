package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.mainactivity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuCompat
import androidx.core.view.iterator
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather.WeatherViewModel
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit.showMessage
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(),
        NavController.OnDestinationChangedListener,
        CoroutineScope{

    override val coroutineContext: CoroutineContext by lazy {
        Dispatchers.Main + SupervisorJob()
    }
    private val navController: NavController by lazy { Navigation.findNavController(this, R.id.frame_master) }
    @ExperimentalCoroutinesApi
    private val model: WeatherViewModel by viewModel()

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setNavController()
    }

    private fun setNavController() {
        NavigationUI.setupWithNavController(toolbar, navController)
    }

    @ExperimentalCoroutinesApi
    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        if (destination.id != bottom_navigation.selectedItemId){
            markItemBottomMenu(destination.id)
        }
        if(destination.id == R.id.fragmentWeather && arguments == null){
            launch {
                if (model.getStateLastPlace().receive() && navController.currentDestination?.id == R.id.fragmentWeather){
                    navController.navigate(R.id.action_fragmentWeather_to_fragmentOfListOfPlaces)
                    bottom_navigation.menu.findItem(R.id.nav_places).isChecked = true
                }
            }
        }
        title = destination.label
    }

    /*костыль*/
    private fun markItemBottomMenu(id : Int){
        for (item: MenuItem in bottom_navigation.menu.iterator()){
            if (item.itemId == id){
                item.isChecked = true
                return
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)
        return true
    }

    @ExperimentalCoroutinesApi
    override fun onStart() {
        super.onStart()
        setListenerBottomNavigation()
        navController.addOnDestinationChangedListener(this)
    }

    @ExperimentalCoroutinesApi
    private fun setListenerBottomNavigation(){
        bottom_navigation.setOnNavigationItemReselectedListener {item ->
            when(item.itemId){
                R.id.fragmentWeather ->  model.getStateLastPlace()
            }
        }
        bottom_navigation.setOnNavigationItemSelectedListener {item: MenuItem ->
            when(item.itemId){
                R.id.feedback -> toSendComment()
                else -> NavigationUI.onNavDestinationSelected(item, navController)
            }
        }
    }

    private fun toSendComment() : Boolean{
        return Intent(Intent.ACTION_SENDTO).apply {
            type = getString(R.string.type_text_plain)
            data = Uri.parse(getString(R.string.mailto))
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.subject))
        }.run {
            resolveActivity(packageManager)?.let {
                startActivity(Intent.createChooser(this, getString(R.string.title_how_to_send)))
            } ?: run {
                showMessage(bottom_navigation, getString(R.string.warning_no_application), Color.BLACK)
            }
            false
        }
    }

    override fun onStop() {
        super.onStop()
        navController.removeOnDestinationChangedListener(this)
        bottom_navigation.setOnNavigationItemReselectedListener(null)
        bottom_navigation.setOnNavigationItemSelectedListener(null)
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancelChildren()
        coroutineContext.cancel()
    }
}