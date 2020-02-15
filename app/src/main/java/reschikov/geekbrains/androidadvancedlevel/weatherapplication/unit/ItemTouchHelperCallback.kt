package reschikov.geekbrains.androidadvancedlevel.weatherapplication.unit

import android.graphics.Canvas
import android.graphics.RectF
import android.util.SparseArray
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.RecyclerView
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.R
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.customView.PlugInButton
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.listplaces.Removable

private const val SWIPE_THRESHOLD = 0.75f

class ItemTouchHelperCallback(private val recyclerView: RecyclerView) : ItemTouchHelper.Callback() {

    private val buttons = SparseArray<PlugInButton>()
    private val removable: Removable = recyclerView.adapter as Removable
    private val gestureDetector : GestureDetectorCompat by lazy {
        GestureDetectorCompat(recyclerView.context, createGesture())
    }

    init {
        recyclerView.setOnTouchListener { v, event ->
                gestureDetector.onTouchEvent(event)
                false
            }
        attachSwipe(recyclerView)
    }

    private fun createGesture(): GestureDetector.SimpleOnGestureListener{
        return object : GestureDetector.SimpleOnGestureListener(){
            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                e?.let {
                    for (i in 0 until buttons.size()) {
                        val position = buttons.keyAt(i)
                        if (buttons.get(position).onClick(it.x, it.y)) {
                            removable.delete(position)
                            unfastenFromViewHolder(position)
                            return true
                        }
                    }
                }
                return false
            }

            override fun onDown(e: MotionEvent?): Boolean = true
        }
    }

    private fun attachSwipe(recyclerView: RecyclerView) {
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        val dragFlags = 0
        val swipeFlags = START or END
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return SWIPE_THRESHOLD
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        viewHolder?.let {
            if (hasButton(it)) {
                val position = it.adapterPosition
                if (isRemovePlugInButton(buttons.get(position).clickRegion)) {
                    unfastenFromViewHolder(position)
                }
            } else {
                attachToViewHolder(it)
            }
        }
    }

    private fun hasButton(viewHolder: RecyclerView.ViewHolder?): Boolean {
        return viewHolder != null && buttons.get(viewHolder.adapterPosition) != null
    }

    private fun attachToViewHolder(viewHolder: RecyclerView.ViewHolder) {
        val index = viewHolder.adapterPosition
        val button = createButton(viewHolder)
        buttons.append(index, button)
    }

    private fun createButton(viewHolder: RecyclerView.ViewHolder): PlugInButton {
        val context = viewHolder.itemView.context
        return PlugInButton.Builder(null)
                .setButtonColor(ContextCompat.getColor(context, R.color.white))
                .setIcon(getDrawable(context, R.drawable.ic_delete_forever_36dp))
                .build()
    }

    private fun unfastenFromViewHolder(position: Int) {
        buttons.get(position)?.let {
            buttons.remove(position)
        }
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                 dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (hasButton(viewHolder)) {
            val button = buttons.get(viewHolder.adapterPosition)
            val buttonRectF = button.clickRegion
            val currentView = viewHolder.itemView
            var deltaX = dX
            when {
                isThereAnOverlap(button, deltaX) -> deltaX = if (button.isRightSide) -buttonRectF.width() else buttonRectF.width()
                deltaX > 0 -> {
                    button.isRightSide = false
                    buttonRectF.set(0f, currentView.top.toFloat(), currentView.left + deltaX, currentView.bottom.toFloat())
                }
                deltaX < 0 -> {
                    button.isRightSide = true
                    buttonRectF.set(currentView.right + deltaX, currentView.top.toFloat(), currentView.width.toFloat(), currentView.bottom.toFloat())
                }
            }
            button.onDraw(c, buttonRectF)
            super.onChildDraw(c, recyclerView, viewHolder, deltaX, dY, actionState, isCurrentlyActive)
        }
    }

    private fun isRemovePlugInButton(buttonRectF: RectF): Boolean {
        return !buttonRectF.isEmpty && buttonRectF.width() < buttonRectF.height() * 0.1f
    }

    private fun isThereAnOverlap(button: PlugInButton, dX: Float): Boolean {
        val buttonWidth = button.getFullWidth()
        val buttonRectF = button.clickRegion
        return if (button.isRightSide){
                buttonWidth <= buttonRectF.width() && buttonRectF.width() > buttonWidth + dX
            } else {
                buttonWidth <= buttonRectF.width() && buttonWidth < buttonRectF.width() + dX
            }
    }
}
