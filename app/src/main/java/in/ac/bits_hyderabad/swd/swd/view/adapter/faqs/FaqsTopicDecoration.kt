package `in`.ac.bits_hyderabad.swd.swd.view.adapter.faqs

import `in`.ac.bits_hyderabad.swd.swd.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

class FaqsTopicDecoration(
    private val context: Context,
    private val headerHeight: Int,
    private val callback: SectionCallback
) : RecyclerView.ItemDecoration() {

    private lateinit var headerView: View
    private lateinit var titleText: TextView

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val pos = parent.getChildAdapterPosition(view)
        if (callback.isSectionHeader(pos))
            outRect.top = headerHeight
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        if (!::headerView.isInitialized) {
            headerView = inflateHeader(parent)
            titleText = headerView.findViewById(R.id.text_header_name)
            fixLayoutSize(headerView, parent)
        }

        var prevTitle = ""
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val childPos = parent.getChildAdapterPosition(child)
            val title: String = callback.getSectionHeaderName(childPos)
            titleText.text = title
            if (!prevTitle.equals(title, ignoreCase = true) || callback.isSectionHeader(childPos)) {
                drawHeader(c, child, headerView)
                prevTitle = title
            }
        }
    }

    private fun drawHeader(c: Canvas, child: View, headerView: View) {
        c.save()
        c.translate(0f, max(0f, child.top.toFloat() - headerView.height.toFloat()))
        headerView.draw(c)
        c.restore()
    }

    private fun fixLayoutSize(view: View, viewGroup: ViewGroup) {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(viewGroup.width, View.MeasureSpec.EXACTLY)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(viewGroup.height, View.MeasureSpec.UNSPECIFIED)
        val childWidth = ViewGroup.getChildMeasureSpec(
            widthSpec,
            viewGroup.paddingLeft + viewGroup.paddingRight,
            view.layoutParams.width
        )
        val childHeight = ViewGroup.getChildMeasureSpec(
            heightSpec,
            viewGroup.paddingTop + viewGroup.paddingBottom,
            view.layoutParams.height
        )

        view.measure(childWidth, childHeight)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
    }

    private fun inflateHeader(recyclerView: RecyclerView): View {
        return LayoutInflater.from(context)
            .inflate(R.layout.list_item_main_topic_header, recyclerView, false)
    }

    interface SectionCallback {
        fun isSectionHeader(pos: Int): Boolean
        fun getSectionHeaderName(pos: Int): String
    }
}