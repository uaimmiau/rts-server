package server

import spark.kotlin.Http
import spark.kotlin.ignite

fun main() {
    val http: Http = ignite()

    with(http) {
        staticFiles.location("/public")
        get("/") {
            response.type("text/html")
            response.redirect("main.html")
        }
    }
}