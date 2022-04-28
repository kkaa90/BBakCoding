package com.e.jung.dataclass

data class Memo(
    val contents : String,
    val password : String,
    val num : Int
){
    val count : Int = contents.length
}
