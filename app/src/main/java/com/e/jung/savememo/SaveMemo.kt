package com.e.jung.savememo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SaveMemo(
    @ColumnInfo(name = "contents") val contents : String,
    @ColumnInfo(name = "password") val password : String,
){
    @PrimaryKey(autoGenerate = true) var num : Int = 0
}
