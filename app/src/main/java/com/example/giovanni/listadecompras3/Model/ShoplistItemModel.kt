package com.example.giovanni.listadecompras3.Model

import android.os.Parcel
import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ShoplistItemModel(var isChecked: Boolean = false,
                             var text: String = "",
                             var shouldFocus: Boolean = false) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readByte() != 0.toByte(),
            parcel.readString())

    fun toRealm(index: Int) : ShoplistItemRealm {
        return ShoplistItemRealm(index, isChecked, text)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isChecked) 1 else 0)
        parcel.writeString(text)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShoplistItemModel> {
        override fun createFromParcel(parcel: Parcel): ShoplistItemModel {
            return ShoplistItemModel(parcel)
        }

        override fun newArray(size: Int): Array<ShoplistItemModel?> {
            return arrayOfNulls(size)
        }
    }
}

open class ShoplistItemRealm(@PrimaryKey var index: Int = -1,
                         var isChecked: Boolean = false,
                         var text: String = "") : RealmObject() {

    fun toModel() : ShoplistItemModel {
        return ShoplistItemModel(isChecked, text)
    }
}
