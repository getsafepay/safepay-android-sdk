package com.android.safepay.network.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.android.safepay.network.model.address.Option

class SpinnerDialogAdapter(
    context: Context,
    private val items: List<Option>,
    private val label:String
) : ArrayAdapter<Option>(context, android.R.layout.simple_spinner_item, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = if (position == 0) label else items[position].name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = if (position == 0) label else items[position].name
        return view
    }

    override fun isEnabled(position: Int): Boolean {
        return position != 0 // Disable the hint item
    }
}
