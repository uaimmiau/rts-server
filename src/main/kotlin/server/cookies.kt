package server

import org.eclipse.jetty.websocket.api.Session
import java.util.*

fun Session.getCookie(cookieName: String): String? = this.upgradeRequest.cookies?.find { it.name == cookieName }?.value
fun Session.getCookie(cookie: Cookies) = this.getCookie(cookie.cookieName)

enum class Cookies (val cookieName: String) {
    SESSION_ID("RTS_SESSID")
}

fun randomSessionId() = UUID.randomUUID().toString().replace('-', '_')