package se.dennispettersson.webauthy

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.*
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_UP
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout

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
        parent = (viewHolder.itemView.parent as ViewGroup).parent as LinearLayout

        if (actionState == ACTION_STATE_SWIPE) {
            setTouchListener(recyclerView, viewHolder, dX)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun reset() {
        parent?.setBackgroundColor(Color.WHITE)
        prevActionState = ActionState.NONE
    }

    private fun setParentColor(color: Int, action: ActionState) {
        if (action == prevActionState || animating) {
            return
        }

        prevActionState = action
        animating = true

        ValueAnimator.ofArgb(prevAnimatedColor, color).apply {
            duration = animationDuration
            interpolator = LinearInterpolator()
            addUpdateListener { anim ->
                run {
                    val bg = anim.animatedValue as Int
                    parent?.setBackgroundColor(bg)

                    if (bg == color) {
                        animating = false
                        prevAnimatedColor = color
                    }
                }
            }
            start()
        }
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
                    actionState = when {
                        dX < -eventStateWidth -> {
                            setParentColor(colorAllow, ActionState.SWIPE_LEFT)
                            ActionState.SWIPE_LEFT
                        }
                        dX > eventStateWidth -> {
                            setParentColor(colorDeny, ActionState.SWIPE_RIGHT)
                            ActionState.SWIPE_RIGHT
                        }
                        else -> {
                            setParentColor(Color.WHITE, ActionState.NONE)
                            ActionState.NONE
                        }
                    }
                ACTION_UP -> {
                    when (actionState) {
                        ActionState.SWIPE_RIGHT -> {
                            swipesActions?.onSwipeRight(viewHolder.getAdapterPosition())
                            reset()
                        }
                        ActionState.SWIPE_LEFT -> {
                            swipesActions?.onSwipeLeft(viewHolder.getAdapterPosition())
                            reset()
                        }
                        else -> {
                        }
                    }

                }
                else -> {
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
        private const val TAG = "SC"

        private var eventStateWidth = 250.0f
        private var swipeBack = false
        private var actionState = ActionState.NONE
        private var prevActionState = ActionState.NONE
        private var swipesActions: SwipeControllerActions? = null

        private val context: Context
            get() = MainActivity.instance as Context

        private val colorAllow = ContextCompat.getColor(context, R.color.colorAllow)
        private val colorDeny = ContextCompat.getColor(context, R.color.colorDeny)

        @SuppressLint("StaticFieldLeak")
        private var parent: LinearLayout? = null

        private var prevAnimatedColor = Color.WHITE
        private var animating = false
        private const val animationDuration = 250.toLong()
    }
}