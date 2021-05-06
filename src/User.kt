package org.csuf.cpsc.test1

import kotlinx.serialization.Serializable

@Serializable
class User {
    var id : Int = 0
    var username : String = ""
    var password : String = ""

    constructor(userName: String, password: String ){
        this.username = userName
        this.password = password
    }

}