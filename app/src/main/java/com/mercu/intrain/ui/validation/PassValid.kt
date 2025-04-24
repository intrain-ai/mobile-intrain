package com.mercu.intrain.ui.validation

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import com.mercu.intrain.R

class PassValid @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        hint = context.getString(R.string.pass_input_hint)
        isFocusableInTouchMode = true
        setupInputType()
        setupValidation()
    }

    private fun setupInputType() {
        inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD
    }

    private fun setupValidation() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                validatePassword(s.toString())
            }
        })
    }

    private fun validatePassword(input: String) {
        val errorMessage = when {
            input.isEmpty() -> context.getString(R.string.pass_isEmpty)
            input.length < 6 -> context.getString(R.string.pass_min_character)
            else -> null
        }

        error = errorMessage
    }
}
