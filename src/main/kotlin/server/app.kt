package server

import spark.Spark.*

fun main() {

    staticFiles.location("/public")
    webSocket("/socket", ServerSocket::class.java)
    port(getHerokuPort())
    get("/") {req, res ->
        res.type("text/html")
        res.redirect("index.html")
    }

}

fun getHerokuPort(): Int {
    val processBuilder = ProcessBuilder()
    return if (processBuilder.environment()["PORT"] != null) {
        processBuilder.environment()["PORT"]!!.toInt()
    } else 5000
}