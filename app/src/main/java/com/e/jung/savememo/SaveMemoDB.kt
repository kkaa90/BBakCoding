package com.e.jung.savememo

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SaveMemo::class], version = 1)
abstract class SaveMemoDB : RoomDatabase(){
    abstract fun dao(): SaveMemoDao
}