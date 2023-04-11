package com.example.finalproject;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface contactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertData(Contact contact);

    @Query("SELECT * FROM contacts")
    List<Contact> getAllUser();


    @Update
    int updateData(Contact contact);

    @Delete
    int deleteData(Contact contact);


}