package com.example.foodknight_with_firebase

import android.os.Parcel
import android.os.Parcelable

data class restaurant(var shopname:String?=null, var shopaddress:String?=null,var uid:String?=null):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(shopname)
        parcel.writeString(shopaddress)
        parcel.writeString(uid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<restaurant> {
        override fun createFromParcel(parcel: Parcel): restaurant {
            return restaurant(parcel)
        }

        override fun newArray(size: Int): Array<restaurant?> {
            return arrayOfNulls(size)
        }
    }
}