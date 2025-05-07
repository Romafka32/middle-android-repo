package com.example.androidpracticumcustomview.ui.theme

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.view.children
import com.example.androidpracticumcustomview.R

/*
Задание:
Реализуйте необходимые компоненты;
Создайте проверку что дочерних элементов не более 2-х;
Предусмотрите обработку ошибок рендера дочерних элементов.
Задание по желанию:
Предусмотрите параметризацию длительности анимации.
 */

class CustomContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var alphaAnimationDuration: Long = 2000
    private var translationAnimationDuration: Long = 5000

    init {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomContainer)

            try {
                alphaAnimationDuration = typedArray.getInt(
                    R.styleable.CustomContainer_alphaAnimationDuration, alphaAnimationDuration.toInt()).toLong()
                translationAnimationDuration = typedArray.getInt(
                    R.styleable.CustomContainer_translationAnimationDuration, translationAnimationDuration.toInt()).toLong()
            } finally {
                typedArray.recycle()
            }
        }
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        for (child in children) {
            try {
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
            } catch (e: Exception) {
                Log.e("CustomContainer", "Measure failed for child: $e")
            }
        }

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val parentWidth = measuredWidth
        val parentHeight = measuredHeight
        try {
            for (child in children) {
                if (indexOfChild(child) == 0) {
                    val topChild = child
                    val childWidth = topChild.measuredWidth
                    val childHeight = topChild.measuredHeight

                    val left = (parentWidth - childWidth) / 2
                    val top = (parentHeight / 4) - (childHeight/2)
                    try {
                        topChild.layout(left,top, left + childWidth, top + childHeight)
                    } catch (e: Exception) {
                        Log.e("CustomContainer", "Layout failed for child: $e")
                    }
                }

                if (indexOfChild(child) == 1) {
                    val bottomChild = child
                    val childWidth = bottomChild.measuredWidth
                    val childHeight = bottomChild.measuredHeight

                    val left = (parentWidth - childWidth) / 2
                    val top = (3 * parentHeight / 4) - (childHeight/2)
                    try {
                        bottomChild.layout(left,top, left + childWidth, top + childHeight)
                    } catch (e: Exception) {
                        Log.e("CustomContainer", "Layout failed for child: $e")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("CustomContainer", "Layout failed: $e")
        }
    }

    override fun addView(child: View?) {
        if (child == null) return
        if (childCount < 2) {
            super.addView(child)
            child.alpha = 0f

            child.post {
                if (height == 0 || child.height == 0) {
                    Log.w("CustomContainer", "Skipping animation: view not measured")
                    child.alpha = 1f
                    return@post
                }
                val childY = child.y
                val centerY = height / 2f - (child.height / 2f)
                val offset = centerY - childY

                child.translationY = offset

                child.animate()
                    .alpha(1f)
                    .setDuration(alphaAnimationDuration)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()

                child.animate()
                    .translationY(0f)
                    .setDuration(translationAnimationDuration)
                    .setInterpolator(DecelerateInterpolator())
                    .start()
            }
        } else {
            throw IllegalStateException("There is no 3d option")
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount > 2) {
            throw IllegalStateException("There is no 3d option")
        }
    }
}