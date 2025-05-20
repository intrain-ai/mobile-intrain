package com.mercu.intrain.ui.validation

import android.R.id.input
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText

class UsernameValid @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        hint = "Masukan Username anda"
        isFocusableInTouchMode = true
        requestFocus()
    }

    private fun setupInputType() {
        inputType = EditorInfo.TYPE_CLASS_TEXT
    }

    private fun setupValidation() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                validateUsername(s.toString())
            }
        })
    }

    private fun validateUsername(input: String) {
        val errorMessage = when {
            input.isEmpty() -> "Username tidak boleh kosong"
            !isValidUsername(input) -> "Username tidak valid"
            else -> null
        }

        error = errorMessage
    }

    private fun isValidUsername(username: String): Boolean {
        val usernameRegex = "^[a-zA-Z0-9]+$".toRegex()
        return usernameRegex.matches(username)
    }
}