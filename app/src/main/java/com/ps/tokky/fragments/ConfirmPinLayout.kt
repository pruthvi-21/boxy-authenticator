package com.ps.tokky.fragments

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ps.tokky.databinding.SheetConfirmPinBinding
import com.ps.tokky.views.KeypadLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmPinLayout(
    @StringRes private val titleRes: Int,
    val callback: (passcode: String) -> Boolean
) : BottomSheetDialogFragment() {

    private val binding by lazy { SheetConfirmPinBinding.inflate(LayoutInflater.from(context)) }

    private val passcode = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.title.setText(titleRes)

        binding.keypad.keypadKeyClickListener = object : KeypadLayout.OnKeypadKeyClickListener {
            override fun onDigitClick(digit: String) {
                if (passcode.size >= 4 || digit.contains("0123456789")) return
                passcode.add(digit)
                updateUI()

                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main) {
                        if (passcode.size == 4) {
                            delay(300)
                            val status = callback(passcode.joinToString(separator = ""))
                            if (status) {
                                dismiss()
                            } else {
                                binding.errorMessageHolder.alpha = 1f
                                Handler().postDelayed(
                                    { binding.errorMessageHolder.animate().setDuration(300).alpha(0f) },
                                    1000
                                )
                                passcode.clear()
                                updateUI()
                            }
                        }
                    }
                }
            }

            override fun onBackspaceClick() {
                if (passcode.size <= 0) return
                passcode.removeAt(passcode.size - 1)
                updateUI()
            }
        }
    }

    private fun updateUI() {
        binding.pinField.currentLength = passcode.size
    }
}
