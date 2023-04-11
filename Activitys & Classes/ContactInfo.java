package com.example.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.os.HandlerCompat;
import androidx.room.Room;

import android.Manifest;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.text.TextUtils;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContactInfo extends AppCompatActivity {

    TextView name;

    TextView p_num;

    ImageView icon;

    ImageButton call;

    ImageButton msg, back, edit, delete;

    Contact user = new Contact();

    int id;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());

    MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        name = findViewById(R.id.name2);
        p_num = findViewById(R.id.number2);
        back = findViewById(R.id.btn_back1);
        edit = findViewById(R.id.btn_edit);
        icon = findViewById(R.id.user_icon);
        call = findViewById(R.id.call_icon);
        msg = findViewById(R.id.mssg_icon);
        delete = findViewById(R.id.btn_delete);
        db = Room.databaseBuilder(ContactInfo.this, MyDatabase.class, "contacts")
                .fallbackToDestructiveMigration().build();
        Intent intent = getIntent();



        String db_name = intent.getStringExtra("Name");
        String db_p_num = intent.getStringExtra("PhoneNumber");
        id = intent.getIntExtra("ID", 0);
        icon.setImageURI(intent.getData());
        user.setImage(intent.getData().toString());

        name.setText(db_name);
        p_num.setText(db_p_num);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactInfo.this, EditContact.class);
                intent.putExtra("Name", name.getText());
                intent.putExtra("PhoneNumber", p_num.getText());
                intent.putExtra("ID", id);
                intent.setData(intent.getData());
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivity(intent);
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactInfo.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivity(intent);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeCall(p_num.getText().toString());
            }
        });

        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage(p_num.getText().toString());
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteClicked();
            }
        });

    }

    private void sendMessage(String contactNumber) {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + contactNumber));
        intent.putExtra("sms_body", "Enter your message");
        startActivity(intent);
    }

    private void makeCall(String contactNumber) {

        Intent callIntent = new Intent(Intent.ACTION_CALL);

        callIntent.setData(Uri.parse("tel:" + contactNumber));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(ContactInfo.this, new String[]{Manifest.permission.CALL_PHONE},  101);

            return;
        }


        if (ActivityCompat.checkSelfPermission(ContactInfo.this,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        startActivity(callIntent);
    }

    public void DeleteData(){

        String p_name = name.getText().toString();
        String num = p_num.getText().toString();
        int db_id = id;

        if(TextUtils.isEmpty(p_name) || TextUtils.isEmpty(num)){
            Toast.makeText(this, "Enter all the values", Toast.LENGTH_SHORT);
        }
        else{
            user.setName(p_name);
            user.setPhoneNumber(num);
            user.setId(db_id);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    long l = db.contactDao().deleteData(user);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(l>0){
                                Toast.makeText(ContactInfo.this, "The value was inserted", Toast.LENGTH_SHORT);
                            }
                            else{
                                Toast.makeText(ContactInfo.this, "The value insertion FAILED!!!!", Toast.LENGTH_SHORT);
                            }
                        }
                    });
                }
            });
        }
    }

    public void DeleteClicked(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ContactInfo.this);


        builder.setMessage("Are you sure you want to Delete this contact?");

        builder.setTitle("Warning!");


        builder.setCancelable(false);


        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {

            DeleteData();
            Intent intent = new Intent(ContactInfo.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivity(intent);
        });


        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {

            dialog.cancel();
        });


        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

}
