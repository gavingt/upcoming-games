package com.gavinsappcreations.upcominggames.ui.sort

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import java.text.ParseException
import java.text.SimpleDateFormat


class DateInputMask(val editText: TextInputEditText) {

    fun listen() {
        editText.addTextChangedListener(mDateEntryWatcher)
    }

    private val mDateEntryWatcher = object : TextWatcher {

        var edited = false
        val dividerCharacter = "/"

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (edited) {
                edited = false
                return
            }

            var working = getInputText()

            working = manageDateDivider(working, 2, start, before)
            working = manageDateDivider(working, 5, start, before)

            edited = true
            editText.setText(working)
            editText.setSelection(working.length)

            try {
                val month: Int? = if (working.length >= 2) working.substring(0, 2).toInt() else null
                val day: Int? = if (working.length >= 5) working.substring(3, 5).toInt() else null

                if ((month != null && month > 12) || (day != null && day > 31)) {
                    throw java.lang.NumberFormatException()
                }
            } catch (nfe: NumberFormatException) {
                editText.error = "Invalid date. Use MM/DD/YYYY format."
            }

            if (working.length == 10) {
                try {
                    val df = SimpleDateFormat("MM/dd/yyyy")
                    df.isLenient = false
                    df.parse(working)
                } catch (e: ParseException) {
                    editText.error = "Non-existent date. Use MM/DD/YYYY format."
                }
            }
        }

        private fun manageDateDivider(
            working: String,
            position: Int,
            start: Int,
            before: Int
        ): String {
            if (working.length == position) {
                return if (before <= position && start < position)
                    working + dividerCharacter
                else
                    working.dropLast(1)
            }
            return working
        }

        private fun getInputText(): String {
            val inputText = editText.text
            return if (inputText != null && inputText.length >= 10)
                editText.text.toString().substring(0, 10)
            else
                editText.text.toString()
        }

        override fun afterTextChanged(s: Editable) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    }
}