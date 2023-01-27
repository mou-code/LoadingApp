package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.content.Context
import android.graphics.*
import android.os.Trace.isEnabled
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /** All custom colors used for the loading button */
    private var Custom_BackgroundColor: Int             =0
    private var Custom_BackgroundColor_Downloading: Int =0
    private var Custom_TextColor: Int                   =0
    private var Custom_CircleColor: Int                 =0

    /** All Dimensions used for the loading button */
    private var button_width: Int                       =0
    private var button_height: Int                      =0
    private var downloading: Int                        =0 // the progress of the download (from 0 to 360)

    /** Creating a paint object */
    private val paint = Paint().apply {
        isAntiAlias = true // Smoothing the edges
        style = Paint.Style.FILL_AND_STROKE
        textAlign = Paint.Align.CENTER
        textSize = 48f
        color = Color.WHITE
        typeface = Typeface.create("roboto", Typeface.NORMAL)
    }

    /** Button Objects */
    private var rectF = RectF(0f, 0f, 72f, 72f)
    private var ButtonRect= Rect()
    private var ButtonText: String =""
    private var valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Completed -> {
                valueAnimator.cancel()      //Cancelling the current animation
                downloading=0               //Setting the progress to 0
                invalidate()
                ButtonText = context.getString(R.string.Ready_Button_State) //Changing the text
                isEnabled = true        //Enabling the button again
            }

            ButtonState.Loading -> {
                valueAnimator = ValueAnimator.ofInt(0, 360).apply {
                    duration = 1000
                    addUpdateListener {
                        downloading = it.animatedValue as Int
                        invalidate()
                    }
                    start()
                }
                valueAnimator.repeatCount = INFINITE
                valueAnimator.repeatMode = ObjectAnimator.REVERSE
                ButtonText = context.getString(R.string.Downloading_Button_State)
            }

            is ButtonState.Clicked -> {
                buttonState = ButtonState.Loading
            }
        }
    }


    init {
        isClickable = true
        //Initializing custom attributes
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            Custom_BackgroundColor = getColor(R.styleable.LoadingButton_CustomBackgroundColor, 0)
            Custom_TextColor = getColor(R.styleable.LoadingButton_CustomTextColor, 0)
            Custom_CircleColor = getColor(R.styleable.LoadingButton_CustomCircleColor,0)
        }
        ButtonText = context.getString(R.string.Ready_Button_State)
    }



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //Setting up the variables for the rectangle button drawing
        paint.color=Custom_BackgroundColor
        ButtonRect.set(0, 0, width * downloading / 360, height)
        canvas?.drawRect(ButtonRect, paint)

        //Setting attributes of the text and Drawing it inside the button
        paint.color=Custom_TextColor
        canvas?.drawText(ButtonText, width / 2.toFloat(),
            (button_height + 30) / 2.toFloat(),
            paint
        )

        //Setting attributes of the circle and Drawing it inside the button
        paint.color = Custom_CircleColor
        rectF.offsetTo((width - 300).toFloat(), 50f)
        canvas?.drawArc(
            rectF,
            0f,
            downloading.toFloat(),
            true,
            paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        button_width = w
        button_height = h
        setMeasuredDimension(w, h)
    }

    fun StartDownload() {
        buttonState = ButtonState.Clicked
    }
    fun EndDownload() {
        buttonState = ButtonState.Completed
    }

}
