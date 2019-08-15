package com.example.giovanni.listadecompras3.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import com.example.giovanni.listadecompras3.Model.ShoplistItemModel
import com.example.giovanni.listadecompras3.R
import kotlinx.android.synthetic.main.item_shoplist.view.*
import android.view.inputmethod.InputMethodManager



class ShoplistAdapter(private val items: List<ShoplistItemModel>,
                      private val context: Context,
                      private val updateRealm: () -> Unit) : Adapter<ShoplistAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_shoplist, parent, false)
        return ViewHolder(view, PositionalTextListener(), PositionalCheckListener())
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (items.count() > position) {
            holder.textChange.updatePosition(position)
            holder.checkChange.updatePosition(position)
            holder.bindView(items[position])
            if (items[position].shouldFocus) {
                holder.requestFocus()
                items[position].shouldFocus = false
            }
        }
    }

    class ViewHolder(itemView: View,
                     val textChange: PositionalTextListener,
                     val checkChange: PositionalCheckListener) : RecyclerView.ViewHolder(itemView) {

        fun requestFocus() {
            itemView.item_editText.requestFocus()
        }

        fun bindView(item: ShoplistItemModel) {
            val checkBox: CheckBox? = itemView.item_checkBox
            val editText: EditText? = itemView.item_editText

            editText?.setText(item.text)
            editText?.addTextChangedListener(textChange)
            checkBox?.isChecked = item.isChecked
            checkBox?.setOnCheckedChangeListener(checkChange)
        }
    }

    inner class PositionalTextListener : TextWatcher {

        private var position: Int = 0

        fun updatePosition(position: Int) {
            this.position = position
        }

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}

        override fun afterTextChanged(editable: Editable) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
            items[position].text = charSequence.toString()
            updateRealm.invoke()
        }
    }

    inner class PositionalCheckListener : CompoundButton.OnCheckedChangeListener {

        private var position: Int = 0

        fun updatePosition(position: Int) {
            this.position = position
        }

        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            items[position].isChecked = isChecked
            updateRealm.invoke()
        }
    }
}

