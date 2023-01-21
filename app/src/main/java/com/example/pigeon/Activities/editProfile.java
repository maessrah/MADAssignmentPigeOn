package com.example.pigeon.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pigeon.Adapters.pdfClass;
import com.example.pigeon.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class editProfile extends AppCompatActivity {

    FirebaseFirestore db;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    Button saveChanges, uploadResume;
    EditText Nametxt,Addresstxt,Biotxt,Skillstxt,Languagestxt,Jobpreftxt,Transporttxt;
    CircleImageView imageViewCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Bundle bundle=getIntent().getExtras();
        String id=bundle.getString("ID");
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("PigeonProfile");
        imageViewCircle = findViewById(R.id.imageViewCircle);

        saveChanges = findViewById(R.id.SaveChangesbtn);
        Nametxt = findViewById(R.id.Nametxt);
        Addresstxt = findViewById(R.id.Addresstxt);
        Biotxt = findViewById(R.id.Biotxt);
        Skillstxt = findViewById(R.id.Skillstxt);
        Languagestxt = findViewById(R.id.Languagestxt);
        Jobpreftxt = findViewById(R.id.Jobpreftxt);
        Transporttxt = findViewById(R.id.Transporttxt);



        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = Nametxt.getText().toString();
                String address = Addresstxt.getText().toString();
                String bio = Biotxt.getText().toString();
                String skills = Skillstxt.getText().toString();
                String languages = Languagestxt.getText().toString();
                String jobpref = Jobpreftxt.getText().toString();
                String transport = Transporttxt.getText().toString();

                Map<String,Object> users = new HashMap<>();
                users.put("Name", name);
                users.put("Address",address);
                users.put("Bio",bio);
                users.put("Skills",skills);
                users.put("Languages",languages);
                users.put("Job Preference",jobpref);
                users.put("Transport",transport);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference().child("Users");


                String userId = GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getId();
                db.collection("users").add(users).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        myRef.child(userId).updateChildren(users);
                        Toast.makeText(getApplicationContext(), "Changes Saved", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), RoleActivity.class);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Failed to save changes",Toast.LENGTH_LONG).show();
                    }
                });


            }
        });}




    }