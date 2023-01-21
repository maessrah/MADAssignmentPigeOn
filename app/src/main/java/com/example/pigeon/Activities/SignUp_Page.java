package com.example.pigeon.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pigeon.Model.MainActivity;
import com.example.pigeon.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SignUp_Page extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText pass;
    private EditText address;
    private EditText noPhone;
    private EditText age;
    private RadioButton radioButton;
    private RadioGroup radioGroup;
    Button signup;
     CheckBox agreement;
    TextView loginForm;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storage;
    StorageReference storageReference;



    InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; ++i)
            {
                if (!Pattern.compile("[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz]*").matcher(String.valueOf(source.charAt(i))).matches())
                {
                    return "";
                }
            }

            return null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();

        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        email = findViewById(R.id.editTextEmail);
        pass = findViewById(R.id.editTextPassword);
        address = findViewById(R.id.editTextAddress);
        noPhone = findViewById(R.id.editTextPhone2);
        age = findViewById(R.id.editTextAge);
//        t1 = findViewById(R.id.employee);
//        t2 = findViewById(R.id.employer);

        loginForm = findViewById(R.id.logInform);
        signup = findViewById(R.id.signUpButton);
        radioGroup = findViewById(R.id.radiog);

        firstName.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(15)});
        lastName.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(15)});


        loginForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUp_Page.this, Login_Page.class);
                startActivity(i);

                finish();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (firstName.getText().toString().equals("")) {
                    firstName.setError("Empty first name");
                } else if (lastName.getText().toString().equals("")) {
                    lastName.setError("Empty last name");
                } else if (email.getText().toString().equals("")) {
                    email.setError("Empty email address");
                } else if (!email.getText().toString().trim().matches(emailPattern)) {
                    email.setError("Invalid email address");
                } else if (pass.getText().toString().equals("")) {
                    pass.setError("Select date of birth");
                } else if (address.getText().toString().equals("")) {
                    address.setError("Empty password");
                } else if (noPhone.getText().toString().equals("")) {
                    noPhone.setError("Empty password");
                } else if (!agreement.isChecked()) {
                    agreement.setError("Policy must be accepted");
                } else {
                    String userEmail=email.getText().toString();
                    String userPassword=pass.getText().toString();

                    signup(userEmail,userPassword);

                }
            }
        });


    }
    public void signup(String userEmail,String password) {
        if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            // Show an error message
            Toast.makeText(SignUp_Page.this,"Please Enter A Valid Email Addresss",Toast.LENGTH_SHORT).show();
            return;
        } else {
            auth.createUserWithEmailAndPassword(userEmail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        int radioId = radioGroup.getCheckedRadioButtonId();
                        radioButton.findViewById(radioId);
                        String type=radioButton.getText().toString();
                        if (type.equals("Seek A Job")) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference().child("Users");
                            //Hashmap to store the userdetails and setting it to fireabse
                            HashMap<String, Object> user_details = new HashMap<>();

                            //Accessing the user details from gmail
                           // String id = account.getId().toString();
                            String firstname =firstName.getText().toString();
                            String lastname=lastName.getText().toString();
                            //String mail = account.getEmail().toString();
                          //  String pic = account.getPhotoUrl().toString();
                            String addressUser=address.getText().toString();
                            String noPhoneUser=noPhone.getText().toString();
                            String typeUser=radioButton.getText().toString();
                            String mail=email.getText().toString();
                            String password=pass.getText().toString();
                            String id = auth.getUid();

                           // user_details.put("id", id);
                            user_details.put("ID", id);
                            user_details.put("Name", firstname);
                            user_details.put("Email", mail);
                            user_details.put("Role", "jobseeker");
                            user_details.put("aboutMe","");
                            user_details.put("Skills","");
                            user_details.put("applicantTransport","");
                            user_details.put("jobLocation","");
                            user_details.put("workExp","");
                            user_details.put("applicantSkills","");


//                            reference.child("Employer").child(auth.getUid()).child("First Name").setValue(firstName.getText().toString());
//                            reference.child("Employer").child(auth.getUid()).child("Last Name").setValue(lastName.getText().toString());
//                            reference.child("Employer").child(auth.getUid()).child("Address").setValue(address.getText().toString());
//                            reference.child("Employer").child(auth.getUid()).child("Age").setValue(age.getText().toString());
//                            reference.child("Employer").child(auth.getUid()).child("No Phone").setValue(noPhone.getText().toString());
//                            reference.child("Employer").child(auth.getUid()).child("Type").setValue(radioButton.getText().toString());

                            myRef.child(id).updateChildren(user_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent i = new Intent(SignUp_Page.this, SetUpActivity.class);

                                        i.putExtra("ID",id);
                                        i.putExtra("Name",firstname);
                                        i.putExtra("Email",mail);
                                        i.putExtra("Role","jobseeker");
                                        i.putExtra("aboutMe","");
                                        i.putExtra("Skills","");
                                        i.putExtra("applicantTransport","");
                                        i.putExtra("jobLocation","");
                                        i.putExtra("workExp","");
                                        i.putExtra("applicantSkills","");

                                        Toast.makeText(SignUp_Page.this, "Sign Up is Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(i);
                                    }else{
                                        Toast.makeText(SignUp_Page.this, "Sign Up is Unsuccessful", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            Toast.makeText(SignUp_Page.this, "Sign Up is Successful", Toast.LENGTH_SHORT).show();


                        }else{
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference().child("Users");

                            HashMap<String, Object> user_details = new HashMap<>();

                            //Accessing the user details from gmail
                            // String id = account.getId().toString();
                            String firstname =firstName.getText().toString();
                            String lastname=lastName.getText().toString();
                            //String mail = account.getEmail().toString();
                            //  String pic = account.getPhotoUrl().toString();
                            String addressUser=address.getText().toString();
                            String noPhoneUser=noPhone.getText().toString();
                            String typeUser=radioButton.getText().toString();
                            String mail=email.getText().toString();
                            String password=pass.getText().toString();
                            String id = auth.getUid();

                            // user_details.put("id", id);
                            user_details.put("ID", id);
                            user_details.put("Name", firstname);
                            user_details.put("Email", mail);
                            user_details.put("Role", "Admin");
                            user_details.put("aboutMe","");
                            user_details.put("Skills","");
                            user_details.put("applicantTransport","");
                            user_details.put("jobLocation","");
                            user_details.put("workExp","");
                            user_details.put("applicantSkills","");

                            //user_details.put("Type User","admin");
//                            reference.child("Employee").child(auth.getUid()).child("First Name").setValue(firstName.getText().toString());
//                            reference.child("Employee").child(auth.getUid()).child("Last Name").setValue(lastName.getText().toString());
//                            reference.child("Employee").child(auth.getUid()).child("Address").setValue(address.getText().toString());
//                            reference.child("Employee").child(auth.getUid()).child("Age").setValue(age.getText().toString());
//                            reference.child("Employee").child(auth.getUid()).child("No Phone").setValue(noPhone.getText().toString());
//                            reference.child("Employee").child(auth.getUid()).child("Type").setValue(radioButton.getText().toString());

                            myRef.child(id).updateChildren(user_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent i = new Intent(SignUp_Page.this, AdminActivity.class);
                                        Toast.makeText(SignUp_Page.this, "Sign Up is Successful", Toast.LENGTH_SHORT).show();
                                        startActivity(i);
                                    }else{
                                        Toast.makeText(SignUp_Page.this, "Sign Up is Unsuccessful", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    } else {

                        Toast.makeText(SignUp_Page.this, "Sign Up is Unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void checkButton(View v){
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton=findViewById(radioId);

        Toast.makeText(SignUp_Page.this,"Selected " + radioButton,Toast.LENGTH_SHORT);
    }
}


