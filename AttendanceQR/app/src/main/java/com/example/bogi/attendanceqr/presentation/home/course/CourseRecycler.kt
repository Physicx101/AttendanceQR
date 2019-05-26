package com.example.bogi.attendanceqr.presentation.home.course

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.recyclerview.widget.*
import com.example.bogi.attendanceqr.R
import com.example.bogi.attendanceqr.data.model.Course
import kotlinx.android.synthetic.main.item_course.view.*

class CourseRecycler(ctx: Context, attrs: AttributeSet): RecyclerView(ctx, attrs) {

    val newAdapter = CourseCarouselAdapter()

    fun init(data: List<Course>, onSnap: (Pair<Int, Int>) -> Unit) {
        onFlingListener = null
        adapter = newAdapter
        layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
        val snap = PromotionSnapHelper { onSnap(it) }
        snap.attachToRecyclerView(this)
        newAdapter.setItem(data)
        onSnap(Pair(0, data.size))
    }

    inner class CourseCarouselAdapter: Adapter<CourseCarouselAdapter.ViewHolder>() {

        private var items = listOf<Course>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.item_course, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int = items.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        private fun getItem(position: Int): Course = items[position]
        fun setItem(item: List<Course>) {
            items = item
            notifyDataSetChanged()
        }

        @Suppress("IMPLICIT_CAST_TO_ANY")
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(data: Course) = with(itemView) {
                text_course.text = data.name
                text_lecturer.text = data.lecturer
                text_time.text = data.time
                text_room.text = data.room
            }
        }

    }

    inner class PromotionSnapHelper(val onSnap: (Pair<Int, Int>) -> Unit): LinearSnapHelper() {

        private var maxScrollDistance: Int = 0
        var scroller: Scroller? = null
        var helper = OrientationHelper.createHorizontalHelper(layoutManager)

        override fun findSnapView(layoutManager: LayoutManager?): View? {
            return findFirstView(layoutManager, OrientationHelper.createHorizontalHelper(layoutManager))
        }

        private fun findFirstView(
            layoutManager: LayoutManager?,
            helper: OrientationHelper
        ): View? {
            if(layoutManager == null) return null
            if(layoutManager.childCount == 0) return null
            var absClosest = Integer.MAX_VALUE
            var closestView: View? = null
            val start = helper.startAfterPadding
            for(i in 0 until layoutManager.childCount) {
                val child = layoutManager.getChildAt(i)!!
                val childStart = helper.getDecoratedStart(child)
                if(Math.abs(childStart-start) < absClosest ||
                    (getChildAdapterPosition(child) == (adapter?.itemCount?:0)-1 && childStart < layoutManager.width/2)) {
                    absClosest = Math.abs(childStart-start)
                    closestView = child
                }
            }
            onSnap(Pair(getChildAdapterPosition(closestView!!), adapter!!.itemCount))
            invalidate()
            return closestView
        }

        override fun createScroller(layoutManager: LayoutManager?): SmoothScroller? {
            if(layoutManager !is RecyclerView.SmoothScroller.ScrollVectorProvider) {
                return super.createScroller(layoutManager)
            }
            return object: LinearSmoothScroller(context) {
                override fun onTargetFound(targetView: View, state: State, action: Action) {
                    val snapDistance = calculateDistanceToFinalSnap(layoutManager, targetView)!!
                    val dx = snapDistance[0]
                    val dy = snapDistance[1]
                    val dt = calculateTimeForDeceleration(Math.abs(dx))
                    val time = Math.max(1, Math.min(100, dt))
                    action.update(dx, dy, time, mDecelerateInterpolator)
                }

                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    return 100f / displayMetrics.densityDpi
                }
            }
        }

        override fun calculateDistanceToFinalSnap(layoutManager: LayoutManager, targetView: View): IntArray? {
            val out = IntArray(2)
            out[0] = distanceToStart(targetView, OrientationHelper.createHorizontalHelper(layoutManager))
            return out
        }

        private fun distanceToStart(
            targetView: View, helper: OrientationHelper): Int {
            val childStart = helper.getDecoratedStart(targetView)
            val containerStart = helper.startAfterPadding
            return childStart - containerStart
        }

        override fun attachToRecyclerView(recyclerView: RecyclerView?) {
            if(recyclerView != null) {
                helper = OrientationHelper.createHorizontalHelper(layoutManager)
                scroller = Scroller(context, DecelerateInterpolator())
            } else {
                scroller = null
            }
            super.attachToRecyclerView(recyclerView)
        }

        override fun calculateScrollDistance(velocityX: Int, velocityY: Int): IntArray {
            val out = IntArray(2)
            if (maxScrollDistance == 0) {
                maxScrollDistance = (helper.endAfterPadding - helper.startAfterPadding) / 500
            }
            scroller?.fling(0, 0, velocityX, velocityY, -maxScrollDistance, maxScrollDistance, 0, 0)
            out[0] = scroller?.finalX ?: 0
            out[1] = scroller?.finalY ?: 0
            return out
        }

    }

}