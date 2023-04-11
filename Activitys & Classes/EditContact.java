package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.HandlerCompat;
import androidx.room.Room;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.text.TextUtils;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditContact extends AppCompatActivity {

    EditText name;

    EditText p_num;

    Button save;

    int id;

    int SELECT_PICTURE = 200;

    Uri selectedImageUri;

    ImageButton icon, back;
    Contact user = new Contact();

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());

    MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);
        name = findViewById(R.id.name3);
        p_num = findViewById(R.id.number3);
        save = findViewById(R.id.btn_save);
        back = findViewById(R.id.btn_back2);
        icon = findViewById(R.id.Icon_user);
        db = Room.databaseBuilder(EditContact.this, MyDatabase.class, "contacts")
                .fallbackToDestructiveMigration().build();
        Intent intent = getIntent();

        String db_name = intent.getStringExtra("Name");
        String db_p_num = intent.getStringExtra("PhoneNumber");
        id = intent.getIntExtra("ID", 0);
        icon.setImageURI(intent.getData());
        selectedImageUri = intent.getData();

        name.setText(db_name);
        p_num.setText(db_p_num);

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                Intent intent = new Intent(EditContact.this, ContactInfo.class);
                intent.putExtra("Name", user.getName());
                intent.putExtra("PhoneNumber", user.getPhoneNumber());
                intent.putExtra("ID", user.getId());
                intent.setData(selectedImageUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditContact.this, ContactInfo.class);
                intent.putExtra("Name", user.getName());
                intent.putExtra("PhoneNumber", user.getPhoneNumber());
                intent.putExtra("ID", user.getId());
                intent.setData(selectedImageUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                startActivity(intent);
            }
        });

    }

    public void saveData(){

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
            user.setImage(selectedImageUri.toString());
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    long l = db.contactDao().updateData(user);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(l>0){
                                Toast.makeText(EditContact.this, "The value was modified", Toast.LENGTH_SHORT);
                            }
                            else{
                                Toast.makeText(EditContact.this, "The value modification FAILED!!!!", Toast.LENGTH_SHORT);
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
                    icon.setImageURI(selectedImageUri);
                    getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }
        }
    }
}