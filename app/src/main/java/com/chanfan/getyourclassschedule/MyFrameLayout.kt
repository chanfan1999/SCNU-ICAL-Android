package com.chanfan.getyourclassschedule

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.NestedScrollingParent2

class MyFrameLayout :
    FrameLayout, NestedScrollingParent2 {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )


    private fun getN2(): NestedScrollingParent2 {
        return parent as NestedScrollingParent2
    }


    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
        getN2().onNestedScrollAccepted(child, target, axes, type)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return getN2().onStartNestedScroll(
            child,
            target,
            axes,
            type
        )
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
        getN2().onNestedPreScroll(target, dx, dy, consumed, type)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        getN2().onNestedScroll(
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type
        )
    }

    override fun onStopNestedScroll(target: View, type: Int) {
        getN2().onStopNestedScroll(target, type)
    }

}