package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.dialogs

import android.app.Dialog
import android.graphics.RectF
import android.os.Bundle
import android.view.*
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import kotlinx.android.synthetic.main.button.*
import kotlinx.android.synthetic.main.choice_dialog.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.android.ext.android.get
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces.ListPlaceModelFactory
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces.ListPlaceViewModel

private const val KEY_SET = "key set"
private const val KEY_LOCATION = "key location"

class ChoiceOfActionDialog : DialogFragment(){

    private val rectLocation : RectF = RectF()
    private val rectCity : RectF = RectF()
    @ExperimentalCoroutinesApi
    private val model: ListPlaceViewModel by navGraphViewModels(R.id.nav_places){get<ListPlaceModelFactory>()}
    private val navController : NavController by lazy { findNavController() }
    @ExperimentalCoroutinesApi
    private val gestureDetector : GestureDetectorCompat by lazy { GestureDetectorCompat(context, createGesture()) }
    private var choiceSet = false
    private var choiceLocation = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
        dialog.setTitle(R.string.determine_location)
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.choice_dialog, container, false)
        isCancelable = false
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.also {
                    rectLocation.set(current_location.left.toFloat(), current_location.top.toFloat(), current_location.right.toFloat(), current_location.bottom.toFloat())
                    rectCity.set(set_city.left.toFloat(), set_city.top.toFloat(), set_city.right.toFloat(), set_city.bottom.toFloat())
                    it.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exit.setText(R.string.exit)
        exit.setOnClickListener { activity?.finish() }
    }

    @ExperimentalCoroutinesApi
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.let {
            choiceLocation = it.getBoolean(KEY_LOCATION, false)
            choiceSet = it.getBoolean(KEY_SET, false)
        }
        radio.setOnTouchListener{ v, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    override fun onStart() {
        super.onStart()
        current_location.isChecked = choiceLocation
        set_city.isChecked = choiceSet
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_LOCATION, choiceLocation)
        outState.putBoolean(KEY_SET, choiceSet)
    }

    @ExperimentalCoroutinesApi
    private fun createGesture(): GestureDetector.SimpleOnGestureListener{
        return object : GestureDetector.SimpleOnGestureListener(){
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                e?.let {
                    if (rectLocation.contains(it.x, it.y) || current_location.isChecked){
                        determineCurrentLocation()
                        return true
                    }
                    if (rectCity.contains(it.x, it.y) || set_city.isChecked) {
                        addCity()
                        return true
                    }
                }
                return super.onSingleTapUp(e)
            }

            override fun onDown(e: MotionEvent?): Boolean {
                e?.let {
                    markSelectionBox(it)
                    return true
                }
                return super.onDown(e)
            }

            override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
                e1?.let {markSelectionBox(it)}
                e2?.let {markSelectionBox(it)}
                return true
            }
        }
    }

    private fun markSelectionBox(e: MotionEvent){
        current_location.isChecked = rectLocation.contains(e.x, e.y)
        choiceLocation = current_location.isChecked
        set_city.isChecked = rectCity.contains(e.x, e.y)
        choiceSet = set_city.isChecked
    }

    @ExperimentalCoroutinesApi
    private fun determineCurrentLocation(){
        model.addStateOfCurrentPlace()
    }

    private fun addCity(){
        navController.navigate(R.id.action_choiceOfActionDialog_to_placeNameInputDialog)
    }
}
