package com.example.pigeon.Activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pigeon.Model.MainActivity;
import com.example.pigeon.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;

public class Login_Page extends AppCompatActivity {

    EditText email;
    EditText pass;
    Button login;
    SignInButton sgBtn;
    TextView forgotPass;
    TextView signUp;

    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    ActivityResultLauncher <Intent> activityResultLauncher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        //register

        registerActivityforGoogleSignIn();

        email=findViewById(R.id.userEmail);
        pass=findViewById(R.id.userPass);
        login=findViewById(R.id.LoginBtn);
        sgBtn=findViewById(R.id.siginBtn);
        forgotPass=findViewById(R.id.textForgotPass);
        signUp=findViewById(R.id.textSignUp);

        auth = FirebaseAuth.getInstance();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userEmail=email.getText().toString();
                String userPassword=pass.getText().toString();

                if (!Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
                    // Show an error message
                    Toast.makeText(Login_Page.this, "Please Enter A Valid Email Address", Toast.LENGTH_SHORT).show();
                    return;
                }else{

                signInwithfirebase(userEmail,userPassword);}
            }
        });

        sgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signinGoogle();
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login_Page.this, Forgot_Password.class);
                startActivity(i);


            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login_Page.this, SignUp_Page.class);
                startActivity(i);


            }
        });


    }

    private void signinGoogle() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("88808211080-uf451i89td4gkel93jokcnft6j7gb2qm.apps.googleusercontent.com")
                .requestEmail().build();
        googleSignInClient= GoogleSignIn.getClient(this,gso);
        signin();
    }

    private void signin() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activityResultLauncher.launch(signInIntent);
    }

    public void registerActivityforGoogleSignIn(){

        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        int resultCode = result.getResultCode();
                        Intent data = result.getData();
                        if(resultCode == RESULT_OK && data !=null){

                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                            firebaseSignInWithgoogle(task);
                        }
                    }
                });

    }

    private void firebaseSignInWithgoogle(Task<GoogleSignInAccount> task) {

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Toast.makeText(this, "Successfully", Toast.LENGTH_SHORT).show();


            firebaseFGoogleAccount (account);

        } catch (ApiException e) {
            e.printStackTrace();
            Toast.makeText(this,e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseFGoogleAccount(GoogleSignInAccount account) {

        AuthCredential authCredential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        auth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference().child("Users");

                    //Hashmap to store the userdetails and setting it to fireabse
                    HashMap<String, Object> user_details = new HashMap<>();

                    //Accessing the user details from gmail
                    String id = account.getId().toString();
                    String name = account.getDisplayName().toString();
                    String mail = account.getEmail().toString();

                    //String pic = account.getPhotoUrl().toString();
                    user_details.put("ID", id);
                    user_details.put("Name", name);
                    user_details.put("Email", mail);
                    user_details.put("Role", "empty");
                    user_details.put("aboutMe","");
                    user_details.put("Skills","");
                    user_details.put("applicantTransport","");
                    user_details.put("jobLocation","");
                    user_details.put("workExp","");
                    user_details.put("applicantSkills","");
                    user_details.put("Rating","");


                    //updating the user details in firebase
                    myRef.child(id).updateChildren(user_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {


                                //navigating to the main activity after user successfully registers
                                Intent intent = new Intent(getApplicationContext(), RoleActivity.class);

                                //Clears older activities and tasks
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }
        });
    }}
        });}


    public void signInwithfirebase(String emel,String password) {
        auth.signInWithEmailAndPassword(emel, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(Login_Page.this, RoleActivity.class);
                            startActivity(i);
                            finish();



                            Toast.makeText(Login_Page.this, "Sign in is succesful", Toast.LENGTH_SHORT).show();

                        } else {

                            Toast.makeText(Login_Page.this, "Sign in is unsuccessful. Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                    }


                    protected void onStart() {

                        super.onStart();

                        FirebaseUser user= auth.getCurrentUser();

                        if(user!=null){
                            Intent i = new Intent(Login_Page.this, RoleActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
}




