package com.Android.neurobridge

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.inputmethodservice.InputMethodService
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

/**
 * A basic QWERTY input method with iOS-style keys and a compact NeuroBridge Clarity action row.
 */
class NeuroBridgeKeyboardService : InputMethodService() {
    private var isShifted = false
    private lateinit var root: LinearLayout

    override fun onCreateInputView(): View {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(6), dp(6), dp(6), dp(8))
            setBackgroundColor(Color.rgb(207, 211, 217))
        }

        buildKeyboard()
        return root
    }

    private fun buildKeyboard() {
        root.removeAllViews()
        addClarityToolbar()
        addLetterRow("qwertyuiop", sideInsetWeight = 0f)
        addLetterRow("asdfghjkl", sideInsetWeight = 0.45f)
        addBottomLetterRow()
        addUtilityRow()
    }

    private fun addClarityToolbar() {
        val row = horizontalRow(heightDp = 36)
        row.addView(toolbarKey("Rewrite", weight = 1.2f) {
            commitText("[Rewrite this for clearer neurodivergent-friendly communication]")
        })
        row.addView(toolbarKey("Shorter") {
            commitText("[Rewrite this shorter while keeping the same meaning]")
        })
        row.addView(toolbarKey("Warmer") {
            commitText("[Rewrite this warmer and less abrupt while keeping the same meaning]")
        })
        row.addView(toolbarKey("Direct") {
            commitText("[Rewrite this more direct and specific while staying respectful]")
        })
        root.addView(row)
    }

    private fun addLetterRow(letters: String, sideInsetWeight: Float) {
        val row = horizontalRow(heightDp = 48)
        if (sideInsetWeight > 0f) row.addView(spacer(sideInsetWeight))
        letters.forEach { letter ->
            row.addView(letterKey(displayLetter(letter.toString())) {
                commitText(displayLetter(letter.toString()))
                if (isShifted) {
                    isShifted = false
                    buildKeyboard()
                }
            })
        }
        if (sideInsetWeight > 0f) row.addView(spacer(sideInsetWeight))
        root.addView(row)
    }

    private fun addBottomLetterRow() {
        val row = horizontalRow(heightDp = 48)
        row.addView(utilityKey("⇧", weight = 1.35f) {
            isShifted = !isShifted
            buildKeyboard()
        })
        row.addView(spacer(0.15f))
        "zxcvbnm".forEach { letter ->
            row.addView(letterKey(displayLetter(letter.toString())) {
                commitText(displayLetter(letter.toString()))
                if (isShifted) {
                    isShifted = false
                    buildKeyboard()
                }
            })
        }
        row.addView(spacer(0.15f))
        row.addView(utilityKey("⌫", weight = 1.35f) {
            currentInputConnection?.deleteSurroundingText(1, 0)
        })
        root.addView(row)
    }

    private fun addUtilityRow() {
        val row = horizontalRow(heightDp = 48)
        row.addView(utilityKey("123", weight = 1.25f) { commitText("123") })
        row.addView(utilityKey(",", weight = 0.8f) { commitText(",") })
        row.addView(letterKey("space", weight = 4.4f) { commitText(" ") })
        row.addView(utilityKey(".", weight = 0.8f) { commitText(".") })
        row.addView(utilityKey("return", weight = 1.45f) { commitText("\n") })
        root.addView(row)
    }

    private fun displayLetter(letter: String): String {
        return if (isShifted) letter.uppercase() else letter.lowercase()
    }

    private fun horizontalRow(heightDp: Int): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(heightDp)
            )
        }
    }

    private fun spacer(weight: Float): View {
        return View(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, weight)
        }
    }

    private fun letterKey(label: String, weight: Float = 1f, onClick: () -> Unit): TextView {
        return keyView(label, weight, Color.WHITE, Color.rgb(28, 28, 30), if (label == "space") 15f else 22f, 9f, onClick)
    }

    private fun utilityKey(label: String, weight: Float = 1f, onClick: () -> Unit): TextView {
        return keyView(label, weight, Color.rgb(172, 179, 188), Color.rgb(28, 28, 30), 15f, 9f, onClick)
    }

    private fun toolbarKey(label: String, weight: Float = 1f, onClick: () -> Unit): TextView {
        return keyView(label, weight, Color.rgb(245, 245, 247), Color.rgb(54, 54, 57), 13f, 16f, onClick)
    }

    private fun keyView(
        label: String,
        weight: Float,
        backgroundColor: Int,
        textColor: Int,
        textSize: Float,
        radius: Float,
        onClick: () -> Unit
    ): TextView {
        return TextView(this).apply {
            text = label
            gravity = Gravity.CENTER
            setTextColor(textColor)
            setTextSize(textSize)
            typeface = Typeface.DEFAULT
            background = roundedBackground(backgroundColor, radius)
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, weight).apply {
                setMargins(dp(2), dp(3), dp(2), dp(3))
            }
        }
    }

    private fun roundedBackground(color: Int, radiusDp: Float): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(color)
            cornerRadius = radiusDp * resources.displayMetrics.density
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    private fun commitText(text: String) {
        currentInputConnection?.commitText(text, 1)
    }
}
