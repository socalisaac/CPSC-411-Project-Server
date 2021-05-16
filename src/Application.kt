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
    val userID = integer("UserID").autoIncrement()
    val username = text("Username").uniqueIndex()
    val password = text("Password")

    override val primaryKey = PrimaryKey(userID)
}

object Items : Table(){
    val itemID = integer("itemId").autoIncrement()
    val itemName = text("itemName")
    val itemQty = integer("itemQty")
    val itemPrice = integer("itemPrice")

    override val primaryKey = PrimaryKey(itemID)
}

object Transactions : Table(){
    val transactionID = integer("transactionId").autoIncrement()
    val itemSoldName = text("itemSoldName")
    val itemSoldQty = integer("itemSoldQty")
    val revenue = integer("revenue")
    val dateOfTransaction = text("dateOfTransaction")

    override val primaryKey = PrimaryKey(transactionID)
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    Database.connect("jdbc:sqlite:./CPSC-411-Project-DB.db", "org.sqlite.JDBC")
    transaction {
        SchemaUtils.createMissingTablesAndColumns(Users)
        SchemaUtils.createMissingTablesAndColumns(Items)
        SchemaUtils.createMissingTablesAndColumns(Transactions)
    }

    routing{

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

        post("/Database/checkLoginInfo"){
            println("Checking Login Info")

            val paramsJsonStr = call.receiveText()

            val nObj = Json.decodeFromString<User>(paramsJsonStr)

            var idValue = -1

            try {
                transaction {
                    idValue = Users.select {
                        (Users.username.eq(nObj.username) and
                         Users.password.eq(nObj.password))
                    }.single()[Users.userID]
                }
            }
            catch (ex:Exception){
                println("Error in register")
                idValue = -1
            }

            call.respondText("$idValue",
                    status = HttpStatusCode.OK, contentType = ContentType.Text.Plain)

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

        post("/Database/updateLoginInfo"){
            println("Updating Info")
            val paramsJsonStr = call.receiveText()

            val nObj = Json.decodeFromString<User>(paramsJsonStr)

            var t = true
            try {
                transaction {
                    Users.update({Users.userID eq nObj.id}) {
                        it[username] = nObj.username
                        it[password] = nObj.password
                    }
                }
            }
            catch (ex:Exception){
                println("Error in Updating Info")
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
                    }.single()[Items.itemID]
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
            println("Updating Item")
            val paramsJsonStr = call.receiveText()

            val nObj = Json.decodeFromString<Item>(paramsJsonStr)

            var t = true
            try {
                transaction {
                    Items.update({Items.itemID eq nObj.itemId}) {
                        it[itemName] = nObj.itemName
                        it[itemQty] = nObj.itemQty
                        it[itemPrice] = nObj.itemPrice
                    }
                }
            }
            catch (ex:Exception){
                println("Error in Updating Info")
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
                    itemList.add(Item(it[Items.itemID], it[Items.itemName], it[Items.itemQty], it[Items.itemPrice]))
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
                    (Items.itemID eq nObj.itemId)
                }.forEach {
                    itemList.add(Item(it[Items.itemID], it[Items.itemName], it[Items.itemQty], it[Items.itemPrice]))
                }
            }

            val itemListStr = Json.encodeToString(itemList)

            call.respondText("$itemListStr",
                    status = HttpStatusCode.OK, contentType = ContentType.Application.Json)

        }

        post("/Database/clearItemsTable") {
            println("clearing Items Table")

            var t = true
            try{
                transaction {
                    SchemaUtils.drop(Items)
                    SchemaUtils.createMissingTablesAndColumns(Items)
                }
            }
            catch (ex:Exception){
                println("Error in clearing Items Table")
                t = false
            }

            call.respondText("$t",
                    status = HttpStatusCode.OK, contentType = ContentType.Text.Plain)
        }

        post("/Database/getTransactionTable"){
            println("Getting Items")

            var transactionList: MutableList<Transaction> = mutableListOf()

            transaction {
                Transactions.selectAll().forEach {
                      transactionList.add(Transaction(it[Transactions.transactionID], it[Transactions.itemSoldName], it[Transactions.itemSoldQty], it[Transactions.revenue], it[Transactions.dateOfTransaction]))
                }
            }

            val transactionListStr = Json.encodeToString(transactionList)

            call.respondText("$transactionListStr",
                    status = HttpStatusCode.OK, contentType = ContentType.Application.Json)
        }

        post("/Database/addTransaction"){
            println("Adding Item")
            val paramsJsonStr = call.receiveText()

            val nObj = Json.decodeFromString<Transaction>(paramsJsonStr)

            var t = -1
            try {
                transaction {
                    Transactions.insert {
                        it[itemSoldName] = nObj.itemSoldName
                        it[itemSoldQty] = nObj.itemSoldQty
                        it[revenue] = nObj.revenue
                        it[dateOfTransaction] = nObj.dateOfTransaction
                    }

                    t = Transactions.select {
                       (Transactions.itemSoldName.eq(nObj.itemSoldName) and
                        Transactions.itemSoldQty.eq(nObj.itemSoldQty) and
                        Transactions.revenue.eq(nObj.revenue) and
                        Transactions.dateOfTransaction.eq(nObj.dateOfTransaction))
                    }.single()[Transactions.transactionID]
                }
            }
            catch (ex:Exception){
                println("Error in Add Transaction")
                t = -1
            }

            call.respondText("$t",
                    status = HttpStatusCode.OK, contentType = ContentType.Text.Plain)
        }

    }
}

