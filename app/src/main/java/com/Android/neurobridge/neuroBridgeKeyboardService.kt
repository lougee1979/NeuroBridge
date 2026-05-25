package com.Android.neurobridge

import android.graphics.Color
import android.inputmethodservice.InputMethodService
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

/**
 * A basic QWERTY input method with a compact NeuroBridge Clarity action row.
 */
class NeuroBridgeKeyboardService : InputMethodService() {
    private var isShifted = false
    private lateinit var root: LinearLayout

    override fun onCreateInputView(): View {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(8, 8, 8, 8)
            setBackgroundColor(Color.rgb(238, 238, 238))
        }

        buildKeyboard()
        return root
    }

    private fun buildKeyboard() {
        root.removeAllViews()

        root.addView(TextView(this).apply {
            text = "NeuroBridge Clarity"
            textSize = 13f
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 4)
        })

        addActionRow()
        addLetterRow("qwertyuiop")
        addLetterRow("asdfghjkl")
        addBottomLetterRow()
        addUtilityRow()
    }

    private fun addActionRow() {
        val row = horizontalRow()
        row.addView(keyButton("Rewrite", weight = 1.2f) {
            commitText("[Rewrite this for clearer neurodivergent-friendly communication]")
        })
        row.addView(keyButton("Shorter", weight = 1f) {
            commitText("[Rewrite this shorter while keeping the same meaning]")
        })
        row.addView(keyButton("Warmer", weight = 1f) {
            commitText("[Rewrite this warmer and less abrupt while keeping the same meaning]")
        })
        row.addView(keyButton("Direct", weight = 1f) {
            commitText("[Rewrite this more direct and specific while staying respectful]")
        })
        root.addView(row)
    }

    private fun addLetterRow(letters: String) {
        val row = horizontalRow()
        letters.forEach { letter ->
            row.addView(keyButton(displayLetter(letter.toString())) {
                commitText(displayLetter(letter.toString()))
                if (isShifted) {
                    isShifted = false
                    buildKeyboard()
                }
            })
        }
        root.addView(row)
    }

    private fun addBottomLetterRow() {
        val row = horizontalRow()
        row.addView(keyButton(if (isShifted) "SHIFT" else "Shift", weight = 1.4f) {
            isShifted = !isShifted
            buildKeyboard()
        })
        "zxcvbnm".forEach { letter ->
            row.addView(keyButton(displayLetter(letter.toString())) {
                commitText(displayLetter(letter.toString()))
                if (isShifted) {
                    isShifted = false
                    buildKeyboard()
                }
            })
        }
        row.addView(keyButton("⌫", weight = 1.4f) {
            currentInputConnection?.deleteSurroundingText(1, 0)
        })
        root.addView(row)
    }

    private fun addUtilityRow() {
        val row = horizontalRow()
        row.addView(keyButton("123", weight = 1.1f) {
            commitText("123")
        })
        row.addView(keyButton(",", weight = 0.8f) {
            commitText(",")
        })
        row.addView(keyButton("Space", weight = 4f) {
            commitText(" ")
        })
        row.addView(keyButton(".", weight = 0.8f) {
            commitText(".")
        })
        row.addView(keyButton("Enter", weight = 1.3f) {
            commitText("\n")
        })
        root.addView(row)
    }

    private fun displayLetter(letter: String): String {
        return if (isShifted) letter.uppercase() else letter.lowercase()
    }

    private fun horizontalRow(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
    }

    private fun keyButton(label: String, weight: Float = 1f, onClick: () -> Unit): Button {
        return Button(this).apply {
            text = label
            textSize = if (label.length > 6) 11f else 15f
            isAllCaps = false
            minHeight = 0
            minimumHeight = 0
            setPadding(2, 0, 2, 0)
            layoutParams = LinearLayout.LayoutParams(
                0,
                48,
                weight
            ).apply {
                setMargins(3, 3, 3, 3)
            }
            setOnClickListener { onClick() }
        }
    }

    private fun commitText(text: String) {
        currentInputConnection?.commitText(text, 1)
    }
}
