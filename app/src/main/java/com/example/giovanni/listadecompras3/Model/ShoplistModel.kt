package com.example.giovanni.listadecompras3.Model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ShoplistModel(var index: Int,
                         var title: String,
                         var items: List<ShoplistItemModel>) {

    fun toRealm() : ShoplistRealm {

        val realmList = RealmList<ShoplistItemRealm>()
        items.forEachIndexed { index, model ->
            realmList.add(model.toRealm(index))
        }

        return ShoplistRealm(index, title, realmList)
    }
}

open class ShoplistRealm(@PrimaryKey var index: Int = -1,
                         var title: String = "",
                         var items: RealmList<ShoplistItemRealm> = RealmList()) : RealmObject() {

    fun toModel() : ShoplistModel {

        val list = mutableListOf<ShoplistItemModel>()
        items.forEach { list.add(it.toModel()) }

        return ShoplistModel(index, title, list)
    }
}
