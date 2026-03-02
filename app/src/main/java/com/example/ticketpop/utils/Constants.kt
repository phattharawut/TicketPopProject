package com.example.ticketpop.utils

object Constants {
    // เปลี่ยน IP ตามเครื่องที่รัน XAMPP
    const val BASE_URL = "http://10.0.2.2:8080"   // Android Emulator
    // const val BASE_URL = "http://192.168.x.x:8080" // อุปกรณ์จริง

    // SharedPreferences
    const val PREF_NAME     = "ticketpop_prefs"
    const val KEY_JWT_TOKEN = "jwt_token"
    const val KEY_USER_ID   = "user_id"
    const val KEY_USER_ROLE = "user_role"

    // Navigation routes
    const val ROUTE_LOGIN          = "login"
    const val ROUTE_REGISTER       = "register"
    const val ROUTE_HOME           = "home"
    const val ROUTE_SEARCH         = "search"
    const val ROUTE_SPLASH         = "splash"
    const val ROUTE_CONCERT_DETAIL = "concert/{concertId}"
    const val ROUTE_VENUE_INFO     = "venue/{concertId}"
    const val ROUTE_ARTIST_INFO    = "artist/{concertId}"
    const val ROUTE_ZONE_SELECT    = "zone/{concertId}"
    const val ROUTE_SEAT_MAP       = "seat/{zoneId}"
    const val ROUTE_STANDING       = "standing/{zoneId}"
    const val ROUTE_ORDER_SUMMARY  = "order_summary"
    const val ROUTE_PAYMENT        = "payment"
    const val ROUTE_SUCCESS        = "success/{bookingId}"
    const val ROUTE_MY_TICKETS     = "my_tickets"
    const val ROUTE_TICKET_QR      = "ticket/{ticketId}"
    const val ROUTE_TICKET_HISTORY = "ticket_history"
    const val ROUTE_PROFILE        = "profile"
    const val ROUTE_ADMIN_DASH     = "admin/dashboard"
    const val ROUTE_ADMIN_CREATE   = "admin/create_concert"
    const val ROUTE_ADMIN_SCAN     = "admin/scan"
}
