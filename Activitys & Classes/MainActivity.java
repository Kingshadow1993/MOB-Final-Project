package com.example.finalproject;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.room.Room;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import android.widget.Button;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class MainActivity extends AppCompatActivity {


    RecyclerView recyclerView;
    MyDatabase db;

    ImageButton btn_add;

    MyAdapter adapter;

    HeaderAdapter pAdapter;


    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


            btn_add = findViewById(R.id.AddButton);
            db = Room.databaseBuilder(MainActivity.this, MyDatabase.class, "contacts")
                    .fallbackToDestructiveMigration().build();
            recyclerView = findViewById(R.id.recyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);


            showData();


            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, AddContact.class);
                    startActivity(intent);
                }
            });

    }

    public void showData(){
        executorService.execute(new Runnable() {
            Contact contact;
            ArrayList<Contact> contactArray = new ArrayList<>();
            ArrayList<header> h = new ArrayList<>();

            @Override
            public void run() {
                List<Contact> userList = db.contactDao().getAllUser();
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                            for(char alphabet = 'A'; alphabet <='Z'; alphabet++ ) {
                                for (Contact user : userList) {
                                    if (alphabet == user.getName().charAt(0)) {
                                        contact = user;
                                        contactArray.add(contact);
                                    }
                                }
                                if(contactArray.isEmpty()){

                                }
                                else {
                                    ArrayList<Contact> temp = new ArrayList<>();
                                    temp.addAll(contactArray);
                                    header e = new header(String.valueOf(alphabet), temp);
                                    h.add(e);
                                    contactArray.clear();
                                }

                            }
                        pAdapter = new HeaderAdapter(h, MainActivity.this);
                        recyclerView.setAdapter(pAdapter);
                    }
                });
            }
        });
  }


}