package com.maxtauro.monopolywallet.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
  * A file containing extension functions for validating user input
  */


fun EditText.validate(validator: (String) -> Boolean, message: String): Boolean {
    this.afterTextChanged {
        this.error = if (validator(it)) null else message
    }
    this.error = if (validator(this.text.toString())) null else message

    return (validator(this.text.toString()))
}

fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            afterTextChanged.invoke(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
    })
}