package com.example.pigeon.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pigeon.Model.Model;
import com.example.pigeon.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
;

import java.util.HashMap;


public class AdminAllApplicationsAdapter extends FirebaseRecyclerAdapter<Model, AdminAllApplicationsAdapter.Viewholder> {

    public AdminAllApplicationsAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {

        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull AdminAllApplicationsAdapter.Viewholder holder, @SuppressLint("RecyclerView") int position, @NonNull Model model) {

        Context context = holder.txtTitle.getContext();

        //for loading applicant names into recycler view
        holder.txtTitle.setText(model.getUserName());

        //for loading jobTitle into recyclerview
        holder.txtDesc.setText(model.getJobTitle());

        //for getting applicant resume
        holder.txtSkills.setText(model.getSkillsRequired());

        String adminId = GoogleSignIn.getLastSignedInAccount(holder.txtTitle.getContext()).getId();
        String userId = model.getUserId();
        String jobTitle = model.getJobTitle();
        String companyName = model.getCompanyName();
        String userName = model.getUserName();
        String jobStatus = model.getJobAvailability();

        String workKey = model.getJobKey();

        String specialKey=model.getSpecialKey();

        //Onclick implementation to view user Resume
        holder.txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.txtTitle.getContext())
                        .setContentHolder(new ViewHolder(R.layout.applicantdetails_popup))
                        .setExpanded(true, 1500)
                        .create();


                View view = dialogPlus.getHolderView();
                TextView jobtitle = view.findViewById(R.id.aboutJobsTxt);
                TextView applicantName = view.findViewById(R.id.ApplicantNameTxt);
                TextView aboutApplicant = view.findViewById(R.id.aboutApplicantTxt);
                TextView applicantLocation = view.findViewById(R.id.editTextLocationlInfo);
                TextView applicantTransport = view.findViewById(R.id.editTextskillsTransportInfo);
                TextView applicantSkills = view.findViewById(R.id.ApplicantSkilss);
                TextView applicantworkExp = view.findViewById(R.id.editTextworkingExperience);
//
                Button btnAccept = view.findViewById(R.id.acceptJobApplicationBtn);
                Button btnReject = view.findViewById(R.id.rejectJobApplicationBtn);

                jobtitle.setText(jobTitle);
                applicantName.setText(userName);
                aboutApplicant.setText(model.getaboutMe());
                applicantLocation.setText(model.getJobLocation());
                applicantTransport.setText(model.getApplicantTransport());
                applicantSkills.setText(model.getApplicantSkills());
                applicantworkExp.setText(model.getWorkExp());

                dialogPlus.show();
                btnAccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //calling the method to add the data to firebase database
                        FirebaseDatabase.getInstance().getReference().child("jobApplications").child(adminId).child(specialKey)
                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        FirebaseDatabase.getInstance().getReference().child("jobApplications").child(userId).child(specialKey)
                                                .removeValue();
                                        acceptJobApplication(adminId, userId, jobTitle, companyName, userName, context,workKey,jobStatus,specialKey);
                                    }
                                });

                    }
                });

                btnReject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase.getInstance().getReference().child("jobApplications").child(adminId).child(specialKey)
                                .removeValue();
//
                        Toast.makeText(context, "Application rejected", Toast.LENGTH_SHORT).show();
                    }
                });


            }


        });



        //implementing onclick Listener to accept the applicaiton
        holder.acceptJobApplicationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //calling the method to add the data to firebase database
                FirebaseDatabase.getInstance().getReference().child("jobApplications").child(adminId).child(specialKey)
                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseDatabase.getInstance().getReference().child("jobApplications").child(adminId).child(specialKey)
                                        .removeValue();
                                acceptJobApplication(adminId, userId, jobTitle, companyName, userName, context,workKey,jobStatus,specialKey);
                            }
                        });

            }
        });


        holder.rejectJobapplicationBtn.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("jobApplications").child(adminId).child(specialKey)
                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(context, "Application rejected", Toast.LENGTH_SHORT).show();
                            }
                        });
//                FirebaseDatabase.getInstance().getReference().child("jobApplications").child(userId).child(workKey).child("jobAvailability")
//                        .setValue("Rejected");
                Toast.makeText(context, "Application rejected", Toast.LENGTH_SHORT).show();
            }
        });




    }

    private void acceptJobApplication(String adminId, String userId, String jobTitle, String companyName, String userName, Context context
            ,String workkey,String jobStatus,String specialKey) {

        //hashMap to store data init
        HashMap<String, Object> applicaiton_Details = new HashMap<>();

        //Generating push key to add data to firebase with unique key
        String key = FirebaseDatabase.getInstance().getReference().child("selectedApplications").push().getKey();

        //adding the data to hashmap
        applicaiton_Details.put("jobTitle", jobTitle);
        applicaiton_Details.put("adminId", adminId);
        applicaiton_Details.put("companyName", companyName);
        applicaiton_Details.put("userId", userId);
        applicaiton_Details.put("userName", userName);
        applicaiton_Details.put("jobKey",workkey);
        applicaiton_Details.put("jobAvailability","Accepted");
        applicaiton_Details.put("specialKey",key);

        FirebaseDatabase.getInstance().getReference().child("jobApplications").child(userId).child(specialKey).removeValue();
        FirebaseDatabase.getInstance().getReference().child("jobApplications").child(adminId).child(specialKey).removeValue();


        //adding the data to firebase using hashmap under admin node
        FirebaseDatabase.getInstance().getReference().child("selectedApplications").child(adminId).child(key)
                .updateChildren(applicaiton_Details).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {

                        if (task.isSuccessful()) {



                            //adding the data to firebase using hashmap under user node
                            FirebaseDatabase.getInstance().getReference().child("selectedApplications").child(userId).child(key)
                                    .updateChildren(applicaiton_Details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                //shows this toast after accepting the job application
                                                Toast.makeText(context, "Application accepted", Toast.LENGTH_SHORT).show();

                                            }

                                        }
                                    });


                        }

                    }
                });
    }


    @NonNull
    @Override
    public AdminAllApplicationsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //the data objects are inflated into the xml file single_data_item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_job_application_accept_file, parent, false);
        return new AdminAllApplicationsAdapter.Viewholder(view);
    }

    //we need view holder to hold each objet form recyclerview and to show it in recyclerview
    class Viewholder extends RecyclerView.ViewHolder {


        TextView txtTitle;
        TextView txtDesc;
        TextView txtSkills;
        Button acceptJobApplicationBtn;
        Button rejectJobapplicationBtn;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            //asssiginig the address of the materials to show the job applications
            txtTitle = (TextView) itemView.findViewById(R.id.Title);
            txtDesc = (TextView) itemView.findViewById(R.id.Desc);
            txtSkills = (TextView) itemView.findViewById(R.id.skillsTxt);
            acceptJobApplicationBtn = (Button) itemView.findViewById(R.id.AcceptJobApplicationBtn);
            rejectJobapplicationBtn=(Button) itemView.findViewById(R.id.RejectJobApplicationBtn);
        }
    }

}