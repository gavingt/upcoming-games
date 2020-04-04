package com.gavinsappcreations.upcominggames.ui.sort

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.regex.Pattern

/**
 * Checks the date as it's being typed, and provides feedback if it's typed in the wrong format.
 */
class DateInputTextWatcher(val editText: TextInputEditText) {

    fun listen() {
        editText.addTextChangedListener(dateEntryWatcher)
    }

    private val dateEntryWatcher = object : TextWatcher {

        @SuppressLint("SimpleDateFormat")
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.length in 1..9) {
                editText.error = "Incomplete date. Use MM/DD/YYYY format."
            } else if (s.length == 10) {
                try {
                    val df = SimpleDateFormat("MM/dd/yyyy")
                    df.isLenient = false
                    df.parse(s.toString())
                } catch (e: ParseException) {
                    editText.error = "Non-existent date. Use MM/DD/YYYY format."
                }
            }

            val pattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4}")
            val matcher = pattern.matcher(s)

            if (!matcher.matches() && !matcher.hitEnd()) {
                editText.error = "Bad formatting. Use MM/DD/YYYY format."
            } else {
                val month: Int? = if (s.length >= 2) s.substring(0, 2).toInt() else null
                val day: Int? = if (s.length >= 5) s.substring(3, 5).toInt() else null
                if ((month != null && (month == 0 || month > 12)) || (day != null && (day == 0 || day > 31))) {
                    editText.error = "Invalid date. Use MM/DD/YYYY format."
                }
            }
        }

        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    }
}