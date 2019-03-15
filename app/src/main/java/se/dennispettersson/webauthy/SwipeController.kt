package se.dennispettersson.webauthy

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.*
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_UP

@Suppress("NON_EXHAUSTIVE_WHEN")
class SwipeController : Callback() {
    fun addActions(actions: SwipeControllerActions) {
        swipesActions = actions
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int = ItemTouchHelper.Callback.makeMovementFlags(0, LEFT or RIGHT)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (!swipeBack) {
            return super.convertToAbsoluteDirection(flags, layoutDirection)
        }

        swipeBack = false
        return 0
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ACTION_STATE_SWIPE) {
            setTouchListener(recyclerView, viewHolder, dX)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float
    ) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack = event.action == ACTION_CANCEL || event.action == ACTION_UP

            val itemView = viewHolder.itemView

            when (event.action) {
                ACTION_STATE_DRAG ->
                    buttonShowedState = when {
                        dX < -eventStateWidth -> {
                            itemView.setBackgroundColor(Color.TRANSPARENT)
                            // ContextCompat.getColor(MainActivity.instance, R.color.colorDeny)
                            ActionState.SWIPE_LEFT
                        }
                        dX > eventStateWidth -> {
                            itemView.setBackgroundColor(Color.TRANSPARENT)
                            ActionState.SWIPE_RIGHT
                        }
                        else -> {
                            itemView.setBackgroundColor(Color.WHITE)
                            ActionState.NONE
                        }
                    }
                ACTION_UP -> {
                    itemView.setBackgroundColor(Color.TRANSPARENT)
                    when (buttonShowedState) {
                        ActionState.SWIPE_RIGHT -> swipesActions?.onSwipeRight(viewHolder.getAdapterPosition())
                        ActionState.SWIPE_LEFT -> swipesActions?.onSwipeLeft(viewHolder.getAdapterPosition())
                    }
                }
            }

            false
        }
    }

    internal enum class ActionState {
        NONE,
        SWIPE_LEFT,
        SWIPE_RIGHT
    }

    companion object {
        private var eventStateWidth = 250.0f
        private var swipeBack = false
        private var buttonShowedState = ActionState.NONE
        private var swipesActions: SwipeControllerActions? = null
    }
}