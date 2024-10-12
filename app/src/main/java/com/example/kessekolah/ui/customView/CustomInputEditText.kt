package com.example.kessekolah.ui.customView

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import com.example.kessekolah.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class CustomInputEditText: TextInputEditText {

    private lateinit var textInputLayout: TextInputLayout

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context,attrs,defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        textInputLayout = parent.parent as TextInputLayout
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            val delay: Long = 1000
            val handler: Handler = Handler(Looper.getMainLooper())

            private val inputFinishChecker = Runnable {
                when (inputType) {
//                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS -> {
//                        if (!Patterns.EMAIL_ADDRESS.matcher(text.toString()).matches()) {
//                            textInputLayout.error = context.getString(R.string.email_wrong)
//                        }
//                        else if (text.isNullOrEmpty()) {
//                            textInputLayout.error = context.getString(R.string.email_empty)
//                        }else {
//                            textInputLayout.isErrorEnabled = false
//                        }
//                    }
                    InputType.TYPE_TEXT_VARIATION_PASSWORD -> {
                        if (text.toString().length < 6) {
                            textInputLayout.error = context.getString(R.string.password_wrong)
                        } else {
                            textInputLayout.isErrorEnabled = false
                        }
                    }
                    InputType.TYPE_TEXT_VARIATION_PERSON_NAME -> {
                        if (text.toString().trim().isEmpty()) {
                            textInputLayout.error = context.getString(R.string.name_invalid)
                        } else {
                            textInputLayout.isErrorEnabled = false
                        }
                    }
                    InputType.TYPE_CLASS_TEXT -> {
                        if (text.toString().trim().isEmpty()) {
                            textInputLayout.error = context.getString(R.string.username_empty)
                        } else {
                            textInputLayout.isErrorEnabled = false
                        }
                    }
//                    InputType.TYPE_CLASS_NUMBER -> {
//                        if (text.toString().length >= 13 || !text.toString().startsWith("8")) {
//                            textInputLayout.error = context.getString(R.string.format_phone_wrong)
//                        } else {
//                            textInputLayout.isErrorEnabled = false
//                        }
//                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                textInputLayout.isErrorEnabled = false
                handler.removeCallbacks(inputFinishChecker)
            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString().isNotEmpty()) {
                    handler.postDelayed(inputFinishChecker, delay)
                }
            }

        })
    }
}