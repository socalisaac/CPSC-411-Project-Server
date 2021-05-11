package org.csuf.cpsc.test1

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Users : Table(){
    val id = integer("UserID")
    val username = text("Username")
    val password = text("Password")
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

    }
}

