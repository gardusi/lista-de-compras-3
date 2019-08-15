package com.example.giovanni.listadecompras3.Activities

import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.PagerAdapter
import com.example.giovanni.listadecompras3.Fragments.ShoplistFragment
import com.example.giovanni.listadecompras3.R
import com.leinardi.android.speeddial.SpeedDialActionItem

import kotlinx.android.synthetic.main.activity_shoplist.*
import android.view.ViewGroup
import com.example.giovanni.listadecompras3.Model.ShoplistItemModel
import com.example.giovanni.listadecompras3.Model.ShoplistModel
import com.example.giovanni.listadecompras3.Model.ShoplistRealm
import io.realm.Realm
import io.realm.kotlin.where


class ShoplistActivity : AppCompatActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    var lists = mutableListOf<ShoplistModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shoplist)

        setSupportActionBar(null)

        loadRealm()

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        container.adapter = mSectionsPagerAdapter

        fab.addAllActionItems(listOf(
                //SpeedDialActionItem.Builder(R.id.fab_item_delete_list, R.drawable.ic_delete_list)
                //        .setFabBackgroundColor(ResourcesCompat.getColor(resources, android.R.color.holo_red_dark, theme))
                //        .create(),
                //SpeedDialActionItem.Builder(R.id.fab_item_add_list, R.drawable.ic_add_list)
                //        .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorAccent, theme))
                //        .create(),
                SpeedDialActionItem.Builder(R.id.fab_item_delete_item, R.drawable.ic_delete_item)
                        .setFabBackgroundColor(ResourcesCompat.getColor(resources, android.R.color.holo_red_light, theme))
                        .create(),
                SpeedDialActionItem.Builder(R.id.fab_item_add_item, R.drawable.ic_add_item)
                        .setFabBackgroundColor(ResourcesCompat.getColor(resources, android.R.color.holo_green_light, theme))
                        .create()
        ))

        fab.setOnActionSelectedListener {
            var keepDialOpen = false
            when(it.id) {
                R.id.fab_item_add_list -> {
                    mSectionsPagerAdapter?.let {
                        lists.add(ShoplistModel(it.getCurrentIndex(), "Nova Lista", listOf(ShoplistItemModel())))
                        it.notifyDataSetChanged()
                    }
                    keepDialOpen = false
                }
                R.id.fab_item_delete_list -> {
                    mSectionsPagerAdapter?.let {
                        lists.removeAt(it.getCurrentIndex() + 1)
                        it.notifyDataSetChanged()
                    }
                    keepDialOpen = false
                }
                R.id.fab_item_add_item -> {
                    addItem()
                    keepDialOpen = true
                }
                R.id.fab_item_delete_item -> {
                    deleteItem()
                    keepDialOpen = true
                }
            }
            keepDialOpen
        }
    }

    private fun addItem() {
        val fragment = mSectionsPagerAdapter?.getCurrentFragment()
        (fragment as ShoplistFragment).let {
            it.items.add(ShoplistItemModel(shouldFocus = true))
            it.scrollToLastPosition()
            it.adapter?.notifyDataSetChanged()
        }
    }

    private fun deleteItem() {
        val fragment = mSectionsPagerAdapter?.getCurrentFragment()
        (fragment as ShoplistFragment).let {
            if (it.items.count() > 0) {
                it.items.removeAt(it.items.lastIndex)
                if (it.items.count() > 0) {
                    it.scrollToLastPosition()
                    it.items.last().shouldFocus = true
                }
                it.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun loadRealm() {
        val realm = Realm.getDefaultInstance()
        try {
            realm.executeTransaction {
                val results = it.where<ShoplistRealm>().findAll()
                lists = results.toTypedArray().map { it.toModel() }.toMutableList()
                if (lists.isEmpty()) {
                    lists = mutableListOf(ShoplistModel(0, "Minha Lista", listOf(ShoplistItemModel())))
                }
                mSectionsPagerAdapter?.notifyDataSetChanged()
            }
        } finally {
            realm.close()
        }
    }

    inner class SectionsPagerAdapter(var fm: FragmentManager) : FragmentPagerAdapter(fm) {

        private var mCurrentFragment: Fragment? = null

        fun getCurrentFragment(): Fragment? {
            return mCurrentFragment
        }

        fun getCurrentIndex(): Int {
            mCurrentFragment?.let {
                return getItemPosition(it)
            }
            return 0
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            if (getCurrentFragment() !== `object`) {
                mCurrentFragment = `object` as Fragment
            }
            super.setPrimaryItem(container, position, `object`)
        }

        override fun getItem(position: Int): Fragment {
            val title = lists[position].title
            val items = lists[position].items.toTypedArray()
            return ShoplistFragment.newInstance(position + 1, title, items)
        }

        override fun getCount(): Int {
            return 1
        }

        override fun getItemPosition(`object`: Any): Int {
            if (fm.fragments.contains(`object`)) {
                return fm.fragments.indexOf(`object`)
            }
            return PagerAdapter.POSITION_NONE
        }
    }
}