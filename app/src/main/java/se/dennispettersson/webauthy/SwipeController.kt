package se.dennispettersson.webauthy

import android.graphics.Canvas
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.*
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_UP
import android.view.View


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

    private fun setTouchListener(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float
    ) {
        recyclerView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                swipeBack = event.action == ACTION_CANCEL || event.action == ACTION_UP

                if (swipeBack) {
                    if (dX < -buttonWidth) buttonShowedState = ButtonsState.RIGHT_VISIBLE
                    else if (dX > buttonWidth) buttonShowedState = ButtonsState.LEFT_VISIBLE
                }

                if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
                    swipesActions?.onLeftDragged(viewHolder.getAdapterPosition());
                }
                else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                    swipesActions?.onRightDragged(viewHolder.getAdapterPosition());
                }

                return false
            }
        })
    }

    internal enum class ButtonsState {
        GONE,
        LEFT_VISIBLE,
        RIGHT_VISIBLE
    }

    companion object {
        private var buttonWidth: Float = 500.0f
        private var swipeBack: Boolean = false
        private var buttonShowedState: ButtonsState = ButtonsState.GONE
        private var swipesActions: SwipeControllerActions? = null
    }
}