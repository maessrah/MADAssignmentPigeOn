package com.example.pigeon.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pigeon.Activities.JobDetailsAdminActivity;
import com.example.pigeon.Activities.JobDetailsAdminActivity;
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

import java.util.HashMap;
import java.util.Map;

public class JobsPostedAdapter extends FirebaseRecyclerAdapter<Model, JobsPostedAdapter.Viewholder> {

    Model viewModel;
    public JobsPostedAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {

        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull Viewholder holder, @SuppressLint("RecyclerView") int position, @NonNull Model model) {

        Context context=holder.txtTitle.getContext();

        //for loading all job titles into recycler view
        holder.txtTitle.setText(model.getJobTitle());

        //for loading all the job salaries into recyclerview
        holder.txtDesc.setText(model.getJobSalary());

        holder.txtJobDetails.setText(model.getJobLocation());
        String jobKey=model.getJobKey();
        String adminId = GoogleSignIn.getLastSignedInAccount(holder.txtTitle.getContext()).getId();


        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.txtTitle.getContext());
                builder.setTitle("Are you sure to delete this job ads?");
                builder.setMessage("Deleted data can't be undone");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseDatabase.getInstance().getReference().child("JobsPosted").child(adminId)
                                .child(jobKey).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("JobsPosted").child("JobsAds")
                                .child(jobKey).removeValue();
                        Toast.makeText(holder.txtTitle.getContext(),"Succesfully Deleted",Toast.LENGTH_SHORT).show();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(holder.txtTitle.getContext(),"Cancelled",Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
            }
        });


        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus= DialogPlus.newDialog(holder.txtTitle.getContext())
                        .setContentHolder(new ViewHolder(R.layout.update_jobdetails_popup))
                        .setExpanded(true,1500)
                        .create();



                View view = dialogPlus.getHolderView();
                EditText editCompanyName=view.findViewById(R.id.editTextCompanyName);
                EditText editJobTitle=view.findViewById(R.id.editTextjobTitle);
                EditText editJobSalary=view.findViewById(R.id.editTextjobSalary);
                EditText editstartDate=view.findViewById(R.id.editTextstartDate);
                EditText editLocation=view.findViewById(R.id.editTextjobLocation);
                EditText editJobTransport=view.findViewById(R.id.editTexttotalOpening);
                EditText editAboutJobs=view.findViewById(R.id.editTextaboutJobs);
                EditText editSkills=view.findViewById(R.id.editTextSkills);
                EditText editAdditionalInfo=view.findViewById(R.id.editTextadditionalInfo);

                Button btnUpdate=view.findViewById(R.id.updateBtn);

                editCompanyName.setText(model.getCompanyName());
                editJobTitle.setText(model.getJobTitle());
                editJobSalary.setText(model.getJobSalary());
                editstartDate.setText(model.getJobStartDate());
                editLocation.setText(model.getJobLocation());
                editJobTransport.setText(model.getJobTransport());
                editAboutJobs.setText(model.getAboutJob());
                editSkills.setText(model.getSkillsRequired());
                editAdditionalInfo.setText(model.getAdditionalInfo());

                dialogPlus.show();

                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String,Object> job_details = new HashMap<>();
                        job_details.put("companyName", editCompanyName.getText().toString());
                        job_details.put("jobTitle", editJobTitle.getText().toString());
                        job_details.put("jobSalary", editJobSalary.getText().toString());
                        job_details.put("jobStartDate", editstartDate.getText().toString());
                        job_details.put("jobLocation", editLocation.getText().toString());
                        job_details.put("jobTransport", editJobTransport.getText().toString());
                        job_details.put("aboutJob", editAboutJobs.getText().toString());
                        job_details.put("skillsRequired", editSkills.getText().toString());
                        job_details.put("addationalInfo", editAdditionalInfo.getText().toString());
                        job_details.put("adminId", model.getAdminId().toString());
                        job_details.put("jobKey",model.getJobKey().toString());

                        FirebaseDatabase.getInstance().getReference().child("JobsPosted").child(adminId)
                                .child(getRef(position).getKey())
                                .updateChildren(job_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {


                                            FirebaseDatabase.getInstance().getReference().child("JobsPosted").child("JobsAds")
                                                    .child(getRef(position).getKey())
                                                    .updateChildren(job_details);
                                            Toast.makeText(holder.txtTitle.getContext(), "Job Details Updated Successfully", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(holder.txtTitle.getContext(), "There are problems. Please try again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });

            }
        });





    }


    @NonNull
    @Override
    public JobsPostedAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //the data ojects are inflated into the xml file single_data_item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_posted_template, parent, false);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

               String adminId= viewModel.getId().toString();
               deleteJobsAdds(adminId);

            }
        });

        return new JobsPostedAdapter.Viewholder(view);
    }

    //we need view holder to hold each objet form recyclerview and to show it in recyclerview
    class Viewholder extends RecyclerView.ViewHolder {


        Button deleteButton,editButton;
        TextView txtTitle;
        TextView txtDesc;
        TextView txtJobDetails;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            editButton=(Button) itemView.findViewById(R.id.editJobBtn);
            deleteButton=(Button) itemView.findViewById(R.id.DeleteJobBtn);
            //asssiginig the address of the materials to show the available jobs
            txtTitle = (TextView) itemView.findViewById(R.id.jobTitlePosted);
            txtDesc = (TextView) itemView.findViewById(R.id.jobDescPosted);
            txtJobDetails=(TextView) itemView.findViewById(R.id.jobDetailsPosted);

        }
}

    public void deleteJobsAdds(String JobTitle){


    }

}
