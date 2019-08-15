package com.example.giovanni.listadecompras3.Fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import com.example.giovanni.listadecompras3.Adapters.ShoplistAdapter
import com.example.giovanni.listadecompras3.Model.ShoplistItemModel
import com.example.giovanni.listadecompras3.Model.ShoplistModel
import com.example.giovanni.listadecompras3.R
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_shoplist.*
import kotlinx.android.synthetic.main.fragment_shoplist.view.*
import java.text.FieldPosition

/**
 * A placeholder fragment containing a simple view.
 */
class ShoplistFragment : Fragment() {

    lateinit var realm: Realm
    var adapter: ShoplistAdapter? = null
    var items = mutableListOf<ShoplistItemModel>()
    var index = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_shoplist, container, false)

        var array = arguments?.getParcelableArray(ARG_ITEMS)
        var title = arguments?.getString(ARG_TITLE)
        index = arguments?.getInt(ARG_NUMBER) ?: 0
        if (array == null) {
            array = savedInstanceState?.getParcelableArray(ARG_ITEMS)
            title = savedInstanceState?.getString(ARG_TITLE)
            index = savedInstanceState?.getInt(ARG_NUMBER) ?: 0
        }
        array?.map { it as ShoplistItemModel }?.toMutableList()?.let { items = it }

        configureRecyclerView(rootView)
        configureTitleEditText(rootView, title ?: "")

        return rootView
    }

    private fun configureRecyclerView(rootView: View) {
        val recyclerView = rootView.fragment_list
        realm = Realm.getDefaultInstance()
        context?.let {
            adapter = ShoplistAdapter(items, it, {
                realm.executeTransaction {
                    val title = rootView.fragment_title.text.toString()
                    it.copyToRealmOrUpdate(ShoplistModel(index - 1, title, items).toRealm())
                }
            })
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(it, LinearLayout.VERTICAL, false)
        }
    }

    private fun configureTitleEditText(rootView: View, title: String) {
        val titleEditText = rootView.fragment_title
        titleEditText.setText(title)
        titleEditText.addTextChangedListener(TitleTextListener(rootView))
    }

    fun scrollToLastPosition() {
        view?.fragment_list?.smoothScrollToPosition(items.lastIndex)
    }

    companion object {

        private val ARG_TITLE = "arg_title"
        private val ARG_NUMBER = "arg_number"
        private val ARG_ITEMS = "arg_items"

        fun newInstance(position: Int, title: String, items: Array<ShoplistItemModel>): ShoplistFragment {
            val fragment = ShoplistFragment()
            val args = Bundle()
            args.putInt(ARG_NUMBER, position)
            args.putString(ARG_TITLE, title)
            args.putParcelableArray(ARG_ITEMS, items)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }

    inner class TitleTextListener(val rootView: View) : TextWatcher {

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {}

        override fun afterTextChanged(editable: Editable) {}

        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
            realm.executeTransaction {
                val title = rootView.fragment_title.text.toString()
                it.copyToRealmOrUpdate(ShoplistModel(index - 1, title, items).toRealm())
            }
        }
    }
}
