package com.example.pigeon.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pigeon.Activities.JobDetailsAdminActivity;
import com.example.pigeon.Model.Model;
import com.example.pigeon.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class JobsAdapter extends FirebaseRecyclerAdapter<Model, JobsAdapter.Viewholder> {

    public JobsAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {

        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull Viewholder holder, int position, @NonNull Model model) {

        Context context=holder.txtTitle.getContext();

        //for loading all job titles into recycler view
        holder.txtTitle.setText(model.getJobTitle());

        holder.txtJobLocation.setText(model.getJobLocation());
        //for loading all the job salaries into recyclerview
        holder.txtDesc.setText(model.getJobSalary());
        String companyName = model.getCompanyName();
        String jobTitle = model.getJobTitle();
        String jobDescription = model.getAboutJob();
        String jobSalary = model.getJobSalary();
        String startDate = model.getJobStartDate();
        String location = model.getJobLocation();
        String jobTransport = model.getJobTransport();
        String requiredSkills = model.getSkillsRequired();
        String additionalInfo = model.getAdditionalInfo();
        String adminId = model.getAdminId();
        String userId = GoogleSignIn.getLastSignedInAccount(holder.txtTitle.getContext()).getId();
        String userName = model.getUserName();
        String workKey= model.getJobKey();
        String jobAvailability=model.getJobAvailability();
        String applicantSkills= model.getApplicantSkills();
        String workExp=model.getWorkExp();
        String applicantTransport= model.getApplicantTransport();
        String applicantAbout= model.getaboutMe();

        holder.sharejobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,model.getJobTitle().toString());
                intent.putExtra(Intent.EXTRA_TEXT,model.getJobSalary().toString());
                intent.putExtra(Intent.EXTRA_TEXT,model.getJobLocation().toString());
                String body="Apply this Job through our app";
                String applink="https://play.google.com/PigeOnApplyThisJob";
                intent.putExtra(Intent.EXTRA_TEXT,body);
                intent.putExtra(Intent.EXTRA_TEXT,applink);

                context.startActivity(Intent.createChooser(intent,"Share using"));

            }
        });

        holder.savedJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> applicaiton_Details = new HashMap<>();

                //key to generate unique value every time we apply for the job
                String key = FirebaseDatabase.getInstance().getReference().child("savedJobs").push().getKey();

                applicaiton_Details.put("companyName",companyName);
                applicaiton_Details.put("jobTitle",jobTitle);
                applicaiton_Details.put("aboutJob",jobDescription);
                applicaiton_Details.put("jobSalary",jobSalary);
                applicaiton_Details.put("jobStartDate",startDate);
                applicaiton_Details.put("jobLocation",location);
                applicaiton_Details.put("jobTransport",jobTransport);
                applicaiton_Details.put("skillsRequired",requiredSkills);
                applicaiton_Details.put("additionalInfo",additionalInfo);
                applicaiton_Details.put("userId",userId);
                applicaiton_Details.put("jobKey",workKey);
                applicaiton_Details.put("jobAvailability",jobAvailability);
                applicaiton_Details.put("applicantSkills",applicantSkills);
                applicaiton_Details.put("workExp",workExp);
                applicaiton_Details.put("applicantTransport",applicantTransport);
                applicaiton_Details.put("aboutMe",applicantAbout);

                //adding the job applicaiton to firebase
                //adding the job applicaiton to firebase
                FirebaseDatabase.getInstance().getReference().child("savedJobs").child(userId).child(workKey)
                        .updateChildren(applicaiton_Details).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    Toast.makeText(holder.txtTitle.getContext(), "Added to Saved", Toast.LENGTH_SHORT).show();

                                }

                            }
                        });

            }
        });

        holder.txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Getting all the job Details and assigning it to a string
                String companyName=model.getCompanyName().toString();
                String jobTitle=model.getJobTitle().toString();
                String jobDescription=model.getAboutJob().toString();
                String jobSalary=model.getJobSalary().toString();
                String startDate=model.getJobStartDate().toString();
                String joblocation=model.getJobLocation().toString();
                String jobTransport=model.getJobTransport().toString();
                String requiredSkills=model.getSkillsRequired().toString();
                String additionalInfo=model.getAdditionalInfo();
                String userId=model.getAdminId().toString();
                String workKey=model.getJobKey().toString();
                String jobAvailability= model.getJobAvailability();
                String applicantSkills= model.getApplicantSkills();
                String workExp=model.getWorkExp();
                String applicantTransport= model.getApplicantTransport();
                String applicantAbout= model.getaboutMe();

                //Intent to show all the details of a particular job
                Intent intent=new Intent(context, JobDetailsAdminActivity.class);

                //passing the job details to the next intent
                intent.putExtra("companyName",companyName);
                intent.putExtra("jobTitle",jobTitle);
                intent.putExtra("jobDescription",jobDescription);
                intent.putExtra("jobSalary",jobSalary);
                intent.putExtra("startDate",startDate);
                intent.putExtra("jobLocation",joblocation);
                intent.putExtra("jobTransport",jobTransport);
                intent.putExtra("requiredSkills",requiredSkills);
                intent.putExtra("additionalInfo",additionalInfo);
                intent.putExtra("userId",userId);
                intent.putExtra("jobKey",workKey);
                intent.putExtra("jobAvailability",jobAvailability);
                intent.putExtra("applicantSkills",applicantSkills);
                intent.putExtra("workExp",workExp);
                intent.putExtra("applicantTransport",applicantTransport);
                intent.putExtra("aboutMe",applicantAbout);

                context.startActivity(intent);
            }
        });


    }


    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //the data ojects are inflated into the xml file single_data_item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_jobs, parent, false);
        return new Viewholder(view);
    }

    //we need view holder to hold each objet form recyclerview and to show it in recyclerview
    class Viewholder extends RecyclerView.ViewHolder {


        FloatingActionButton sharejobs,savedJobs;
        TextView txtTitle;
        TextView txtDesc;
        TextView txtJobLocation;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            savedJobs=(FloatingActionButton)itemView.findViewById(R.id.savedActionButton);
            sharejobs=(FloatingActionButton) itemView.findViewById(R.id.shareActionButton);
            //asssiginig the address of the materials to show the available jobs
            txtTitle = (TextView) itemView.findViewById(R.id.Title);
            txtDesc = (TextView) itemView.findViewById(R.id.Desc);
            txtJobLocation=(TextView) itemView.findViewById(R.id.joblocationTxt);
        }
    }}

