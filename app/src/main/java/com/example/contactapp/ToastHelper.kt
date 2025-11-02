package com.example.contactapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

object ToastHelper {

    enum class ToastType {
        SUCCESS,
        ERROR,
        INFO
    }

    fun showCustomToast(context: Context, message: String, type: ToastType = ToastType.INFO) {
        val inflater = LayoutInflater.from(context)
        val layout: View = inflater.inflate(R.layout.custom_toast, null)

        val icon = layout.findViewById<ImageView>(R.id.toast_icon)
        val text = layout.findViewById<TextView>(R.id.toast_message)

        // Set icon based on type
        when (type) {
            ToastType.SUCCESS -> {
                icon.setImageResource(android.R.drawable.ic_menu_info_details)
            }
            ToastType.ERROR -> {
                icon.setImageResource(android.R.drawable.ic_dialog_alert)
            }
            ToastType.INFO -> {
                icon.setImageResource(android.R.drawable.ic_dialog_info)
            }
        }

        text.text = message

        with(Toast(context)) {
            duration = Toast.LENGTH_SHORT
            view = layout
            show()
        }
    }
}
