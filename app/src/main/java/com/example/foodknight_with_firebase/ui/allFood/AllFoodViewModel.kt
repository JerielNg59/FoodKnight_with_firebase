package com.example.foodknight_with_firebase.ui.allFood

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AllFoodViewModel : ViewModel() {
    private var _foodDesc = MutableLiveData<String>()
    var foodDesc : LiveData<String> = _foodDesc

    fun foodDesc(foodDesc : String){
        _foodDesc.value = foodDesc
    }

    private var _foodName = MutableLiveData<String>()
    var foodName : LiveData<String> = _foodName

    fun foodName(foodName: String){
        _foodName.value = foodName
    }

    private var _foodPrice = MutableLiveData<String>()
    var foodPrice : LiveData<String> = _foodPrice

    fun foodPrice(foodPrice:String){
        _foodPrice.value = foodPrice
    }

    private var _foodLink = MutableLiveData<String>()
    var foodLink : LiveData<String> = _foodLink

    fun foodLink(foodLink:String){
        _foodLink.value = foodLink
    }

    private var _foodQty = MutableLiveData<String>()
    var foodQty : LiveData<String> = _foodQty

    fun foodQty(foodQty:String){
        _foodQty.value = foodQty
    }

}