package se.dennispettersson.webauthy

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.support.v4.content.ContextCompat
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

        drawButtons(c, viewHolder)
    }

    private fun drawButtons(c: Canvas, viewHolder: RecyclerView.ViewHolder) {
        val v = object {
            val left = viewHolder.itemView.left.toFloat()
            val top = viewHolder.itemView.top.toFloat()
            val right = viewHolder.itemView.right.toFloat()
            val bottom = viewHolder.itemView.bottom.toFloat()
        }

        val width = (v.right - v.left) / 2

        c.drawRect(RectF(v.left, v.top, v.left + width, v.bottom), Paint().apply {
            color = ContextCompat.getColor(MainActivity.instance as Context, R.color.colorAllow)
        })

        c.drawRect(RectF(v.right - width, v.top, v.right, v.bottom), Paint().apply {
            color = ContextCompat.getColor(MainActivity.instance as Context, R.color.colorDeny)
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float
    ) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack = event.action == ACTION_CANCEL || event.action == ACTION_UP

            when (event.action) {
                ACTION_STATE_DRAG ->
                    buttonShowedState = when {
                        dX < -eventStateWidth -> ActionState.SWIPE_LEFT
                        dX > eventStateWidth -> ActionState.SWIPE_RIGHT
                        else -> ActionState.NONE
                    }
                ACTION_UP -> {
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