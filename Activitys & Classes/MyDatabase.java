package com.example.finalproject;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Contact.class}, version = 17)
public abstract class MyDatabase extends RoomDatabase {
    public abstract contactDao contactDao();

}
