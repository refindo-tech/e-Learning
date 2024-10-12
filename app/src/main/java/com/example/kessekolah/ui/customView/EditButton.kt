package com.example.kessekolah.ui.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.kessekolah.R

class EditButton : AppCompatButton {

    private lateinit var enabledBackground: Drawable
    private lateinit var disabledBackground: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val textColor = if (isEnabled) ContextCompat.getColor(context, R.color.white) else ContextCompat.getColor(context, R.color.white)
        setTextColor(textColor)
        background = if (isEnabled) enabledBackground else disabledBackground
    }

    private fun init() {
        enabledBackground = ContextCompat.getDrawable(context, R.color.font200) as Drawable
        disabledBackground = ContextCompat.getDrawable(context, R.color.primary500) as Drawable
    }

}