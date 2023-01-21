package com.example.pigeon.Fragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.pigeon.Activities.AdminActivity;
import com.example.pigeon.Activities.JobDetailsAdminActivity;
import com.example.pigeon.Activities.SignUp_Page;
import com.example.pigeon.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.common.internal.GetServiceRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddJobFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddJobFragment extends Fragment {

    EditText companyNameEditTxt, jobTitleEditTxt, jobSalaryEditTxt, jobStartDateEditTxt, jobLastDateEditTxt;
    EditText jobTransportEditTxt, aboutJobEditTxt, skillsRequiredEditTxt, addationalInfoEditTxt;
    Button addJobBtn,cancelJobBtn,backBtn;
    private RadioButton radioButton1,radioButton2;
    private RadioGroup radioGroup;
    public final String CHANNEL_ID ="1";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddJobFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddJobFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddJobFragment newInstance(String param1, String param2) {
        AddJobFragment fragment = new AddJobFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

//    public void checkButton(Bundle savedInstanceState){
//        int radioId = radioGroup.getCheckedRadioButtonId();
//
//
//        Toast.makeText(getActivity(),"Selected " + radioButton,Toast.LENGTH_SHORT);
//
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_job, container, false);

        //Assigning all the Addresses of the Android Materials to get  Job Details
        companyNameEditTxt = (EditText) view.findViewById(R.id.CompanyNameEditTxt);
        jobTitleEditTxt = (EditText) view.findViewById(R.id.JobTitleEditTxt);
        jobSalaryEditTxt = (EditText) view.findViewById(R.id.JobSalaryEditTxt);
        jobStartDateEditTxt = (EditText) view.findViewById(R.id.JobStartDateEditTxt);
        jobLastDateEditTxt = (EditText) view.findViewById(R.id.LocationEditTxt);
        jobTransportEditTxt = (EditText) view.findViewById(R.id.TotalOpeningsEditTxt);
        aboutJobEditTxt = (EditText) view.findViewById(R.id.AboutJobEditTxt);
        skillsRequiredEditTxt = (EditText) view.findViewById(R.id.SkillsRequiredEditTxt);
        addationalInfoEditTxt = (EditText) view.findViewById(R.id.AddationalInfoEditTxt);
        radioGroup=(RadioGroup) view.findViewById(R.id.radioGroup);
        cancelJobBtn=(Button) view.findViewById(R.id.CancelJobBtn);
        backBtn=(Button) view.findViewById(R.id.backBtn);

        String type="";




        //AddJob onClick Implementation to add Job Details To Firebase
        addJobBtn = (Button) view.findViewById(R.id.AddJobBtn);
        addJobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String CompanyName = companyNameEditTxt.getText().toString();
                String JobTitle = jobTitleEditTxt.getText().toString();
                String JobSalary = jobSalaryEditTxt.getText().toString();
                String JobStartDate = jobStartDateEditTxt.getText().toString();
                String JobLocation = jobLastDateEditTxt.getText().toString();
                String jobTransport = jobTransportEditTxt.getText().toString();
                String AboutJob = aboutJobEditTxt.getText().toString();
                String SkillsRequired = skillsRequiredEditTxt.getText().toString();
                String AddationalInfo = addationalInfoEditTxt.getText().toString();
//                String type=radioButton.getText().toString();
                if (CompanyName.isEmpty() || JobTitle.isEmpty() || JobSalary.isEmpty() || JobStartDate.isEmpty() || JobLocation.isEmpty() || jobTransport.isEmpty() || AboutJob.isEmpty() || SkillsRequired.isEmpty()) {
                    Toast.makeText(getContext(), "Please,Enter All Details", Toast.LENGTH_SHORT).show();
                } else {
                    addJobDetailsToDatabase(CompanyName, JobTitle, JobSalary, JobStartDate, JobLocation, jobTransport, AboutJob, SkillsRequired, AddationalInfo);
                }
            }
        });
        cancelJobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                companyNameEditTxt.setText("");
                jobTitleEditTxt.setText("");
                jobSalaryEditTxt.setText("");
                jobStartDateEditTxt.setText("");
                jobLastDateEditTxt.setText("");
                jobTransportEditTxt.setText("");
                aboutJobEditTxt.setText("");
                skillsRequiredEditTxt.setText("");
                addationalInfoEditTxt.setText("");

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent= new Intent(getActivity(), AdminActivity.class);
               startActivity(intent);
            }
        });
        return view;
    }



    private void addJobDetailsToDatabase(String companyName, String jobTitle, String jobSalary, String jobStartDate, String jobLocation, String jobTransport, String aboutJob, String skillsRequired, String addationalInfo) {


        //Hashmap to store job details
        HashMap<String, Object> job_details = new HashMap<>();
        //Getting admin id
        String adminId = GoogleSignIn.getLastSignedInAccount(getContext()).getId();
        //Generating unique key
        String key = FirebaseDatabase.getInstance().getReference().child("JobsPosted").push().getKey();
        //Adding job details to hashmap
        job_details.put("companyName", companyName);
        job_details.put("jobTitle", jobTitle);
        job_details.put("jobSalary", jobSalary);
        job_details.put("jobStartDate", jobStartDate);
        job_details.put("jobLocation", jobLocation);
        job_details.put("jobTransport", jobTransport);
        job_details.put("aboutJob", aboutJob);
        job_details.put("skillsRequired", skillsRequired);
        job_details.put("additionalInfo", addationalInfo);
        job_details.put("adminId", adminId);
        job_details.put("jobKey",key);
        job_details.put("jobAvailability","Available");

        //Adding job details to fireabase
        FirebaseDatabase.getInstance().getReference().child("JobsPosted").child(adminId)
                .child(key)
                .updateChildren(job_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {

                        if (task.isSuccessful()) {


                            FirebaseDatabase.getInstance().getReference().child("JobsPosted").child("JobsAds")
                                    .child(key)
                                    .updateChildren(job_details);
                            //Setting the edit text to blank  after successfully adding the data to fireabse
                            Toast.makeText(getContext(), "Job Details Added Successfully", Toast.LENGTH_SHORT).show();
                            companyNameEditTxt.setText("");
                            jobTitleEditTxt.setText("");
                            jobSalaryEditTxt.setText("");
                            jobStartDateEditTxt.setText("");
                            jobLastDateEditTxt.setText("");
                            jobTransportEditTxt.setText("");
                            aboutJobEditTxt.setText("");
                            skillsRequiredEditTxt.setText("");
                            addationalInfoEditTxt.setText("");
                            
                            startNotification();

                        }

                    }
                });



    }

    private void startNotification() {

//        NotificationChannel channel=new NotificationChannel(CHANNEL_ID,"1",
//                NotificationManager.IMPORTANCE_DEFAULT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(),"My notification").setContentTitle("New Job Added")
        .setContentText("meow mai")
       .setSmallIcon(R.drawable.pige_on_logo_latest)
        .setAutoCancel(true);

        NotificationManagerCompat managerCompact=NotificationManagerCompat.from(getContext());
        managerCompact.notify(1,builder.build());
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel =new NotificationChannel(CHANNEL_ID,"Notification",NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getContext().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

    }

}
