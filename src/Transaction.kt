package org.csuf.cpsc.test1

import kotlinx.serialization.Serializable

@Serializable
class Transaction {
    var id : Int = 0
    var itemSoldName : String = ""
    var itemSoldQty : Int = 0
    var revenue : Long = 0
    //Date stored using System.currentTimeMillis()
    var date : Long = 0

    constructor(itemSoldName: String, itemSoldQty: Int, revenue : Long, date : Long){
        this.itemSoldName = itemSoldName
        this.itemSoldQty = itemSoldQty
        this.revenue = revenue
        this.date = date
    }

    constructor(id : Int, itemSoldName: String, itemSoldQty: Int, revenue : Long, date : Long){
        this.id = id
        this.itemSoldName = itemSoldName
        this.itemSoldQty = itemSoldQty
        this.revenue = revenue
        this.date = date
    }

}