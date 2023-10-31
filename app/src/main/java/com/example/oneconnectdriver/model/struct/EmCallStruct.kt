package com.example.oneconnectdriver.model.struct

data class EmCallStruct(
    val em_call_id:String,
    val em_call_status_id:String,
    val em_pvd_id:String,
    val em_transport_id:String,
    val uid:String,
    val user_lat:String,
    val user_long:String,
    val user_phone_number:String,
    val created_at:Long
)
