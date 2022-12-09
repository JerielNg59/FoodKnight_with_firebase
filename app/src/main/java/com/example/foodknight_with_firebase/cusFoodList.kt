package com.example.foodknight_with_firebase

import android.os.Parcel
import android.os.Parcelable

data class cusFoodList(var foodName:String?=null, var foodDesc:String?=null,var foodLink:String?=null,var foodPrice:String?=null,var uid:String?=null):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(foodName)
        parcel.writeString(foodDesc)
        parcel.writeString(foodLink)
        parcel.writeString(foodPrice)
        parcel.writeString(uid)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<cusFoodList> {
        override fun createFromParcel(parcel: Parcel): cusFoodList {
            return cusFoodList(parcel)
        }

        override fun newArray(size: Int): Array<cusFoodList?> {
            return arrayOfNulls(size)
        }
    }
}