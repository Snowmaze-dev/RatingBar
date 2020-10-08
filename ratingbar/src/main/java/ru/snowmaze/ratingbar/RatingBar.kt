package ru.snowmaze.ratingbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import kotlin.math.ceil

open class RatingBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private var mActiveStarDrawable: Drawable
    private var mInactiveStarDrawable: Drawable
    private var mStarsCount: Int
    private var mStarSize: Int
    private var mActiveTint: Int
    private var mInactiveTint: Int
    private var oldRating = 0
    private var mStarsPadding: Int
    var starsCount
        get() = mStarsCount
        set(value) {
            mStarsCount = value
            invalidate()
        }
    var rating = 0
        set(value) {
            field = value
            invalidate()
        }
    var activeTint: Int
        get() = mActiveTint
        set(value) {
            mActiveTint = value
            mActiveStarDrawable.setTint(value)
            invalidate()
        }
    var inactiveTint: Int
        get() = mInactiveTint
        set(value) {
            mInactiveTint = value
            mInactiveStarDrawable.setTint(value)
            invalidate()
        }
    var starsPadding: Int
        get() = mStarsPadding
        set(value) {
            mStarsPadding = value
            invalidate()
        }
    var starSize: Int
        get() = mStarSize
        set(value) {
            mStarSize = value
            invalidate()
        }
    var activeStarDrawable: Drawable
        get() = mActiveStarDrawable
        set(value) {
            mActiveStarDrawable = value
            value.setTint(mActiveTint)
            invalidate()
        }
    var inactiveStarDrawable: Drawable
        get() = mInactiveStarDrawable
        set(value) {
            mInactiveStarDrawable = value
            value.setTint(mInactiveTint)
            invalidate()
        }

    private var mWidth = 0

    var onRatingChangeListener: OnRatingChangeListener? = null

    fun setActiveStarDrawable(@DrawableRes res: Int) {
        activeStarDrawable = ContextCompat.getDrawable(context, res) ?: ContextCompat.getDrawable(context, R.drawable.ic_star)!!
    }

    fun setInactiveStarDrawable(@DrawableRes res: Int) {
        inactiveStarDrawable = ContextCompat.getDrawable(context, res) ?: ContextCompat.getDrawable(context, R.drawable.ic_inactive_star)!!
    }

    init {
        with(context.obtainStyledAttributes(attrs, R.styleable.RatingBar)) {
            mStarsCount = getInteger(R.styleable.RatingBar_starsCount, 5)
            mStarSize = getDimensionPixelSize(R.styleable.RatingBar_starSize, 100)
            mActiveTint = getColor(
                R.styleable.RatingBar_activeStarTint,
                ContextCompat.getColor(context, R.color.activeStarColor)
            )
            mInactiveTint = getColor(
                R.styleable.RatingBar_inactiveStarTint,
                ContextCompat.getColor(context, R.color.inactiveStarColor)
            )
            mActiveStarDrawable =
                getDrawable(R.styleable.RatingBar_activeStarDrawable) ?: ContextCompat.getDrawable(
                    context, R.drawable.ic_star
                )!!
            mInactiveStarDrawable = getDrawable(R.styleable.RatingBar_inactiveStarDrawable)
                ?: ContextCompat.getDrawable(context, R.drawable.ic_inactive_star)!!
            mStarsPadding = getDimensionPixelSize(R.styleable.RatingBar_starsPadding, 0)
            recycle()
        }
        applyStarSize()
        DrawableCompat.setTint(mInactiveStarDrawable, mInactiveTint)
        DrawableCompat.setTint(mActiveStarDrawable, mActiveTint)
    }

    private fun calcRating(x: Float) {
        val rt = ceil(x / (mWidth / starsCount)).toInt()
        rating = if (rt > starsCount) starsCount else rt
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_MOVE -> calcRating(event.x)
            MotionEvent.ACTION_DOWN -> oldRating = rating
            MotionEvent.ACTION_UP -> {
                calcRating(event.x)
                if (oldRating != rating) onRatingChangeListener?.onRatingChanged(rating)
            }
        }
        return true
    }

    private fun applyStarSize() {
        mActiveStarDrawable.setBounds(0, 0, mStarSize, mStarSize)
        mInactiveStarDrawable.setBounds(0, 0, mStarSize, mStarSize)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mWidth = mStarSize * starsCount + starsPadding * (starsCount - 1) + paddingLeft + paddingRight
        val desiredWidth = suggestedMinimumWidth + mWidth
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom
        setMeasuredDimension(
            measureDimension(desiredWidth, widthMeasureSpec),
            measureDimension(desiredHeight + mStarSize, heightMeasureSpec)
        )
    }


    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return if (specMode == MeasureSpec.EXACTLY) specSize
        else {
            return if (specMode == MeasureSpec.AT_MOST) desiredSize.coerceAtMost(specSize)
            else desiredSize
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.translate(paddingLeft.toFloat(), paddingTop.toFloat())
        var active = true
        for (i in 0 until starsCount) {
            if (active && rating <= i) {
                active = false
            }
            if (active) mActiveStarDrawable.draw(canvas)
            else mInactiveStarDrawable.draw(canvas)
            canvas.translate(mStarSize.toFloat() + starsPadding, 0F)
        }
    }

    fun interface OnRatingChangeListener {

        fun onRatingChanged(rating: Int)

    }
}