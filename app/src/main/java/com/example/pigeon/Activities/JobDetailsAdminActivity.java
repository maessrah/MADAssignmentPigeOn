package com.example.pigeon.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pigeon.Adapters.pdfClass;
import com.example.pigeon.Model.MainActivity;
import com.example.pigeon.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class JobDetailsAdminActivity extends AppCompatActivity {

    TextView companyNameTxt, jobTitleTxt, jobDescriptionTxt, jobSalaryTxt, startDateTxt, joblocationTxt;
    TextView jobTransportTxt, requiredSkillsTxt, additionalInfoTxt,resume;

    DatabaseReference databaseReference;
    Button resumeLinkBtn;
    Button applyJobBtn;
    FloatingActionButton backBtn;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);


        Bundle bundle = getIntent().getExtras();

        String companyName = bundle.getString("companyName");
        String jobTitle = bundle.getString("jobTitle");
        String jobDescription = bundle.getString("jobDescription");
        String jobSalary = bundle.getString("jobSalary");
        String startDate = bundle.getString("startDate");
        String joblocation = bundle.getString("jobLocation");
        String jobTransport = bundle.getString("jobTransport");
        String requiredSkills = bundle.getString("requiredSkills");
        String additionalInfo = bundle.getString("additionalInfo");
        String adminId = bundle.getString("userId");
        String userId = GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getId();

        String userName = GoogleSignIn.getLastSignedInAccount(getApplicationContext()).getDisplayName();
        String workKey=bundle.getString("jobKey");
        String jobAvailability=bundle.getString("jobAvailability");
        String applicantSkills= bundle.getString("applicantSkills");
        String workExp=bundle.getString("workExp");
        String applicantTransport= bundle.getString("applicantTransport");
        String applicantAbout= bundle.getString("aboutMe");

        //assigning all the android components addresses to perform appropriate action
        companyNameTxt = (TextView) findViewById(R.id.CompanyNameTxt);
        jobTitleTxt = (TextView) findViewById(R.id.JobTitleTxt);
        jobDescriptionTxt = (TextView) findViewById(R.id.JobDescriptionTxt);
        jobSalaryTxt = (TextView) findViewById(R.id.SalaryTxt);
        startDateTxt = (TextView) findViewById(R.id.JobStartDateTxt);
        joblocationTxt = (TextView) findViewById(R.id.JobLocationTxt);
        jobTransportTxt = (TextView) findViewById(R.id.jobTransportTxt);
        requiredSkillsTxt = (TextView) findViewById(R.id.RequiredSkillsTxt);
        additionalInfoTxt = (TextView) findViewById(R.id.AdditionalDataTxt);
        //resume=(TextView) findViewById(R.id.resumePdfLink) ;
        backBtn=(FloatingActionButton) findViewById(R.id.backBtn);

        applyJobBtn = (Button) findViewById(R.id.ApplyJobBtn);
        resumeLinkBtn = (Button) findViewById(R.id.ResumeLink);

        //setting the text to textView
        companyNameTxt.setText(companyName);
        jobTitleTxt.setText(jobTitle);
        jobDescriptionTxt.setText(jobDescription);
        jobSalaryTxt.setText(jobSalary);
        startDateTxt.setText(startDate);
        joblocationTxt.setText(joblocation);
        jobTransportTxt.setText(jobTransport);
        requiredSkillsTxt.setText(requiredSkills);
        additionalInfoTxt.setText(jobAvailability);

        joblocationTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(JobDetailsAdminActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(JobDetailsAdminActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        resumeLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPDF();
            }
        });


        //On Click implementation to add the job application to the fireabase database
        applyJobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    applyForJob(jobTitle, userId, adminId, companyName, userName,workKey,jobAvailability,applicantSkills,applicantAbout
                            ,applicantTransport,workExp);
//                }

            }
        });


    }

    private void selectPDF() {
        Intent intent =  new Intent();
        intent.setType("application/pdf");
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"PDF FILE SELECT"),1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){

            UploadFiles(data.getData());
        }
    }

    private void UploadFiles(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference reference = storageReference.child("Uploads/"+System.currentTimeMillis()+".pdf");
        reference.putFile(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                databaseReference = FirebaseDatabase.getInstance().getReference("PigeonProfile");

                Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri url=uriTask.getResult();

                pdfClass pdfClass = new pdfClass(url.toString());
                databaseReference.child(databaseReference.push().getKey()).setValue(pdfClass);

                Toast.makeText(JobDetailsAdminActivity.this, "File Uploaded", Toast.LENGTH_SHORT).show();

                progressDialog.dismiss();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                double progress = (100.0* snapshot.getBytesTransferred()/ snapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded:"+(int)progress+"%");
            }
        });
    }

    private void applyForJob(String jobTitle, String userId, String adminId, String companyName
            , String userName,String workKey,String jobAvailability,String applicantSkills,String applicantAbout,String applicantTransport,String workExp) {

        //Hash map to store all the data in it
        HashMap<String, Object> applicaiton_Details = new HashMap<>();

        //key to generate unique value every time we apply for the job
        String key = FirebaseDatabase.getInstance().getReference().child("jobApplications").push().getKey();

        //adding all the details to hashmap
        applicaiton_Details.put("jobTitle", jobTitle);
        applicaiton_Details.put("adminId", adminId);
        applicaiton_Details.put("companyName", companyName);
        applicaiton_Details.put("userId", userId);
        applicaiton_Details.put("userName", userName);
       // applicaiton_Details.put("resumeLink", resumeLink);

        applicaiton_Details.put("jobAvailability",jobAvailability);
        applicaiton_Details.put("jobKey",workKey);
        applicaiton_Details.put("applicantSkills",applicantSkills);
        applicaiton_Details.put("aboutMe",applicantAbout);
        applicaiton_Details.put("applicantTransport",applicantTransport);
        applicaiton_Details.put("workExp",workExp);
        applicaiton_Details.put("specialKey",key);

        //adding the job applicaiton to firebase
        FirebaseDatabase.getInstance().getReference().child("jobApplications").child(adminId).child(key)
                .updateChildren(applicaiton_Details).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            FirebaseDatabase.getInstance().getReference().child("jobApplications").child(userId).child(key)
                                    .updateChildren(applicaiton_Details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                //shows this toast message after successfully adds the data to firabse
                                                Toast.makeText(getApplicationContext(), "Successfully Applied For Job", Toast.LENGTH_SHORT).show();

                                            }

                                        }
                                    });




                        }

                    }
                });



    }
}

