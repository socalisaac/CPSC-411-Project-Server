package org.csuf.cpsc.test1

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
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

           if(nObj.userName.toString() == "test" && nObj.password == "123")
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

    }
}

