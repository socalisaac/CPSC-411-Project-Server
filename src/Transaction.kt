package org.csuf.cpsc.test1

import kotlinx.serialization.Serializable

@Serializable
class Transaction {
    var id : Int = 0
    var itemSoldName : String = ""
    var itemSoldQty : Int = 0
    var revenue : Int = 0
    var dateOfTransaction : String = ""

    constructor(itemSoldName: String, itemSoldQty: Int, revenue : Int, dateOfTransaction : String){
        this.itemSoldName = itemSoldName
        this.itemSoldQty = itemSoldQty
        this.revenue = revenue
        this.dateOfTransaction = dateOfTransaction
    }

    constructor(id : Int, itemSoldName: String, itemSoldQty: Int, revenue : Int, dateOfTransaction : String){
        this.id = id
        this.itemSoldName = itemSoldName
        this.itemSoldQty = itemSoldQty
        this.revenue = revenue
        this.dateOfTransaction = dateOfTransaction
    }
}