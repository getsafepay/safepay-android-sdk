package com.android.safepay.network.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.android.safepay.R
import com.android.safepay.network.interfaces.ItemClick
import com.android.safepay.network.model.paymentmethod.PaymentData


class SavedCardAdapter (private val items: List<PaymentData>,private val onItemClick: ItemClick) : RecyclerView.Adapter<SavedCardAdapter.MyViewHolder>() {

    private var selectedPosition = 0  // To track selected RadioButton

    // ViewHolder to bind the view for each item
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.tvCardNumber)
        val image: AppCompatImageView = itemView.findViewById(R.id.cardType)
        val radio:RadioButton=itemView.findViewById(R.id.radio)
        val root:LinearLayout=itemView.findViewById(R.id.root)
    }

    // Inflate the layout for each item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_layout_payment_saved_card, parent, false)
        return MyViewHolder(view)
    }

    // Bind data to the view
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.radio.isChecked=(position== selectedPosition)


        if(items[position].instrumentType=="Visa")
            holder.image.setImageResource(R.drawable.ic_visa)
        else
            holder.image.setImageResource(R.drawable.ic_master)

        holder.textView.text = "**** "+ items[position].last4

        holder.radio.isChecked = (holder.absoluteAdapterPosition == selectedPosition)

        holder.root.setOnClickListener {
            // Update selected position
            selectedPosition = holder.absoluteAdapterPosition

            // Call listener with the selected item's token
            onItemClick.onClick(items[position].token)
            notifyDataSetChanged()

        }


       /* holder.radio.setOnCheckedChangeListener(
            CompoundButton.OnCheckedChangeListener { compoundButton, b ->
                // check condition
                if (b) {
                    holder.radio.isChecked = true
                    notifyDataSetChanged()
                }
                else
                {
                    holder.radio.isChecked = false
                    notifyDataSetChanged()
                }
            })
*/
    }

    // Return the total count of items
    override fun getItemCount(): Int = items.size

}