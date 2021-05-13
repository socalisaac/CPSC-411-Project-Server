package org.csuf.cpsc.test1

import kotlinx.serialization.Serializable

@Serializable
class Item {
    var itemId : Int = 0
    var itemName : String = "Default_Name"
    var itemQty : Int = 0
    // itemPrice is stored as an Int: smallest unit of currency (cost in cents)
    var itemPrice : Int = 0

    constructor(itemId: Int, itemName: String, itemQty: Int, itemPrice: Int ){
        this.itemId = itemId
        this.itemName = itemName
        this.itemQty = itemQty
        this.itemPrice = itemPrice
    }

    constructor(itemName: String, itemQty: Int, itemPrice: Int){
        this.itemName = itemName
        this.itemQty = itemQty
        this.itemPrice = itemPrice
    }

    constructor(){

    }

}