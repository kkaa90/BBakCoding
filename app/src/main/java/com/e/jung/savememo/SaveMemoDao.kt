package com.e.jung.savememo

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SaveMemoDao {
    @Query("SELECT * FROM SaveMemo")
    fun getAll(): List<SaveMemo>

    @Query("SELECT * FROM SaveMemo WHERE num = :num")
    fun getMemo(num: Int) : SaveMemo

    @Query("SELECT * FROM SaveMemo WHERE contents LIKE '%' || :word || '%'")
    fun getSearch(word : String) : Flow<List<SaveMemo>>

    @Insert
    fun insert(saveMemo: SaveMemo)

    @Update
    fun update(saveMemo: SaveMemo)

    @Query("Update SaveMemo set contents =:contents,password = :pwd where num=:num")
    fun updateMemo(contents :String, pwd : String, num: Int)

    @Query("Delete from SaveMemo")
    fun deleteAll()

    @Query("DELETE FROM SaveMemo where num = :num")
    fun delete(vararg num: Int)

    @Query("DELETE FROM SaveMemo where num IN (:nums)")
    fun deleteM(nums: Array<Int>)

    @Query("SELECT * FROM SaveMemo")
    fun getAllasFlow(): Flow<List<SaveMemo>>

    @Query("SELECT * FROM SaveMemo WHERE num = :num")
    fun getMemoAsFlow(num: Int) : Flow<SaveMemo>
}