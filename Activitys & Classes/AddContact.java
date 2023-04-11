package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;
import androidx.room.Room;

import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.Toast;



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddContact extends AppCompatActivity {

    MyDatabase db;

    EditText etName;

    EditText etNum;

    Button save;

    Button cancel;

    ImageButton add_img;

    Uri selectedImageUri;


    int SELECT_PICTURE = 200;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        save = findViewById(R.id.btn_add);
        cancel = findViewById(R.id.btn_cancel);
        etName = findViewById(R.id.name);
        etNum = findViewById(R.id.number);
        add_img = findViewById(R.id.add_image);

        db = Room.databaseBuilder(AddContact.this, MyDatabase.class, "contacts")
                .fallbackToDestructiveMigration().build();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                Intent intent = new Intent(AddContact.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivity(intent);

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddContact.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivity(intent);
            }
        });

        add_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

    }

    public void saveData(){

        String name = etName.getText().toString();
        String num = etNum.getText().toString();

        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(num)){
            Toast.makeText(this, "Enter all the values", Toast.LENGTH_SHORT);
        }
        else{
            Contact user = new Contact();
            user.setName(name);
            user.setPhoneNumber(num);
            String test = selectedImageUri.toString();
            user.setImage(test);
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    long l = db.contactDao().insertData(user);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(l>0){
                                Toast.makeText(AddContact.this, "The value was inserted", Toast.LENGTH_SHORT);
                            }
                            else{
                                Toast.makeText(AddContact.this, "The value insertion FAILED!!!!", Toast.LENGTH_SHORT);
                            }
                        }
                    });
                }
            });
        }
    }

    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_OPEN_DOCUMENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    add_img.setImageURI(selectedImageUri);
                    getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

                }
            }
        }
    }

}