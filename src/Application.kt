package org.csuf.cpsc.test1

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Users : Table(){
    val id = integer("UserID")
    val username = text("Username")
    val password = text("Password")
}

object Items : Table(){
    val id = integer("itemId")
    val itemName = text("itemName")
    val itemQty = integer("itemQty")
    val itemPrice = integer("itemPrice")
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    Database.connect("jdbc:sqlite:./CPSC-411-Project-DB.db", "org.sqlite.JDBC")
//    transaction {
//        SchemaUtils.createMissingTablesAndColumns(Users)
//    }

    routing{

        //Template
//        post("/MathService/add"){
//            println("HERE")
//            val paramsJsonStr = call.receiveText()
//
//            val nObj = Json.decodeFromString<NumbersToAdd>(paramsJsonStr)
//
//            var sum = nObj.NumOne + nObj.NumTwo
//
//            call.respondText("${sum}",
//                    status = HttpStatusCode.OK, contentType = ContentType.Text.Plain)
//
//        }

        post("/Database/login"){
            println("Login")
            val paramsJsonStr = call.receiveText()

            val nObj = Json.decodeFromString<User>(paramsJsonStr)

            var isLogin = false

            transaction {
                isLogin = Users.select {
                                             (Users.username.eq(nObj.username) and
                                              Users.password.eq(nObj.password))
                                        }.any()
            }

           if(isLogin)
           {
               var t = true
               call.respondText("$t",
                       status = HttpStatusCode.OK, contentType = ContentType.Text.Plain)
           }
            else
           {
               var f = false
               call.respondText("$f",
                       status = HttpStatusCode.OK, contentType = ContentType.Text.Plain)
           }
        }

        post("/Database/register"){
            println("Register")
            val paramsJsonStr = call.receiveText()

            val nObj = Json.decodeFromString<User>(paramsJsonStr)

            var t = true
            try {
                transaction {
                    Users.insert {
                        it[username] = nObj.username
                        it[password] = nObj.password
                    }
                }
            }
            catch (ex:Exception){
                println("Error in register")
                t = false
            }

            call.respondText("$t",
                    status = HttpStatusCode.OK, contentType = ContentType.Text.Plain)
        }

        post("/Database/addItem"){
            println("Adding Item")
            val paramsJsonStr = call.receiveText()

            val nObj = Json.decodeFromString<Item>(paramsJsonStr)

            var t = -1
            try {
                transaction {
                    Items.insert {
                        it[itemName] = nObj.itemName
                        it[itemQty] = nObj.itemQty
                        it[itemPrice] = nObj.itemPrice
                    }

                    t = Items.select {
                        (Items.itemName.eq(nObj.itemName) and
                        Items.itemQty.eq(nObj.itemQty) and
                        Items.itemPrice.eq(nObj.itemPrice))
                    }.single()[Items.id]
                }

            }
            catch (ex:Exception){
                println("Error in Add Item")
                t = -1
            }

            call.respondText("$t",
                    status = HttpStatusCode.OK, contentType = ContentType.Text.Plain)
        }

        post("/Database/editItem"){
            println("Register")
            val paramsJsonStr = call.receiveText()

            val nObj = Json.decodeFromString<User>(paramsJsonStr)

            var t = true
            try {
                transaction {
                    Users.insert {
                        it[username] = nObj.username
                        it[password] = nObj.password
                    }
                }
            }
            catch (ex:Exception){
                println("Error in register")
                t = false
            }

            call.respondText("$t",
                    status = HttpStatusCode.OK, contentType = ContentType.Text.Plain)
        }

        post("/Database/getItemsTable"){
            println("Getting Items")

            var itemList: MutableList<Item> = mutableListOf()

            transaction {
                Items.selectAll().forEach {
                    itemList.add(Item(it[Items.id], it[Items.itemName], it[Items.itemQty], it[Items.itemPrice]))
                }
            }

            val itemListStr = Json.encodeToString(itemList)

            call.respondText("$itemListStr",
                    status = HttpStatusCode.OK, contentType = ContentType.Application.Json)
        }

        post("/Database/getItem") {
            println("Getting Item")
            val paramsJsonStr = call.receiveText()

            val nObj = Json.decodeFromString<Item>(paramsJsonStr)

            var itemList = mutableListOf<Item>()

            transaction {
                Items.select {
                    (Items.id eq nObj.itemId)
                }.forEach {
                    itemList.add(Item(it[Items.id], it[Items.itemName], it[Items.itemQty], it[Items.itemPrice]))
                }
            }

            val itemListStr = Json.encodeToString(itemList)

            call.respondText("$itemListStr",
                    status = HttpStatusCode.OK, contentType = ContentType.Application.Json)

//            if(itemList.isNotEmpty())
//            {
//
//            }
//            else
//            {
//                var f = false
//                call.respondText("$f",
//                        status = HttpStatusCode.OK, contentType = ContentType.Text.Plain)
//            }
        }

    }
}

