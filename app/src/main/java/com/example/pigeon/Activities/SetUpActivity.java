package com.example.pigeon.Activities;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.pigeon.Adapters.ProfileAdapter;
import com.example.pigeon.Model.MainActivity;
import com.example.pigeon.Model.Model;
import com.example.pigeon.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SetUpActivity extends AppCompatActivity {

    EditText name,about,skilset,transport,locate,workingExp;
    Button setUp;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("Users");

    ProfileAdapter adapter;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);


        Bundle bundle = getIntent().getExtras();

        String id = GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getId().toString();
        String applicantID=bundle.getString("ID");
        String skills= "";
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        String adminID=acct.getId().toString();

        name=(EditText) findViewById(R.id.editNameTitle);
        about=(EditText) findViewById(R.id.editTextaboutApplicant);
        skilset=(EditText) findViewById(R.id.editTextskillsApplicant);
        transport=(EditText) findViewById(R.id.editTexttransport);
        locate=(EditText) findViewById(R.id.editTextApplicantLocation);
        workingExp=(EditText) findViewById(R.id.editTextworkingExperience);

        final String[] emel = {""};
        Query checkUser=myRef.orderByChild(applicantID);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users").child(adminID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String applicantName = dataSnapshot.child("Name").getValue(String.class);
                String aboutApplicant =  dataSnapshot.child("aboutMe").getValue(String.class);
                String applicantTransport= dataSnapshot.child("applicantTransport").getValue(String.class);
                String applicantLocation = dataSnapshot.child("jobLocation").getValue(String.class);
                String workExp=dataSnapshot.child("workExp").getValue(String.class);
                String applicantSkills=dataSnapshot.child("applicantSkills").getValue(String.class);
                String email=dataSnapshot.child("Email").getValue(String.class);

                emel[0] =email;
                name.setText(applicantName);
                about.setText(aboutApplicant);
                skilset.setText(applicantSkills);
                transport.setText(applicantTransport);
                locate.setText(applicantLocation);
                workingExp.setText(workExp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        setUp=(Button)findViewById(R.id.SetUpBtn);


        setUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> user_details = new HashMap<>();

                //Accessing the user details from gmail
                // String id = account.getId().toString();
                String firstname =name.getText().toString();
               String aboutName=about.getText().toString();

                String skilsApplicant=skilset.getText().toString();
                String transportApplicant=transport.getText().toString();
                String location=locate.getText().toString();
                String wExp=workingExp.getText().toString();

                // user_details.put("id", id);
                user_details.put("ID", applicantID);
                user_details.put("Name", firstname);
                user_details.put("Email", emel[0]);
                user_details.put("Role", "jobseeker");
                user_details.put("aboutMe",aboutName);
                user_details.put("Skills",skilsApplicant);
                user_details.put("applicantTransport",transportApplicant);
                user_details.put("jobLocation",location);
                user_details.put("workExp",wExp);
                user_details.put("applicantSkills",skilsApplicant);

                myRef.child(id).updateChildren(user_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(SetUpActivity.this, MainActivity.class);
                            Toast.makeText(SetUpActivity.this, "Profile is ready to go", Toast.LENGTH_SHORT).show();
                            startActivity(i);
                        }else{
                            Toast.makeText(SetUpActivity.this, "Set Up is Unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }
}