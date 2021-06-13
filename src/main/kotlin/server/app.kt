package server

import spark.Spark.*

fun main() {

    staticFiles.location("/public")
    webSocket("/socket", ServerSocket::class.java)
    get("/") {req, res ->
        res.type("text/html")
        res.redirect("main.html")
    }

}