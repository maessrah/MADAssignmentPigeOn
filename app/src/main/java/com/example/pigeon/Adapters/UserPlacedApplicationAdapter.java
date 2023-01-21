package com.example.pigeon.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class UserPlacedApplicationAdapter extends FirebaseRecyclerAdapter<Model, UserPlacedApplicationAdapter.Viewholder> {

    public UserPlacedApplicationAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {

        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull UserPlacedApplicationAdapter.Viewholder holder, int position, @NonNull Model model) {

        Context context = holder.txtTitle.getContext();

        String adminId = model.getAdminId();
        String userId= GoogleSignIn.getLastSignedInAccount(holder.txtTitle.getContext()).getId();
        String jobTitle = model.getJobTitle();
        String companyName = model.getCompanyName();
        String userName = model.getUserName();
        String jobStatus=model.getJobAvailability();

        String workKey = model.getJobKey();
        String specialKey=model.getSpecialKey();

        //for loading selected applications into recycler view
        holder.txtTitle.setText(model.getJobTitle());
        holder.txtDesc.setText(model.getCompanyName());

        holder.jobStatus.setText(model.getJobAvailability());

        holder.jobLocation.setText(model.getJobLocation());

        holder.txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.cancelWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.txtTitle.getContext());
                builder.setTitle("Are you sure you want to cancel your application?");
                builder.setMessage("Cancelling application data can't be undone");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseDatabase.getInstance().getReference().child("selectedApplications").child(userId).child(model.getSpecialKey())
                                .removeValue();
//                        FirebaseDatabase.getInstance().getReference().child("selectedApplications").child(model.getAdminId().toString()).child(model.getSpecialKey().toString()).child(model.getSpecialKey())
//                                .child("jobAvailability").setValue("Cancelled");

                        FirebaseDatabase.getInstance().getReference().child("selectedApplications").child(adminId).child(model.getSpecialKey()).child("jobAvailability")
                                .setValue("Cancelled");
                        Toast.makeText(holder.txtTitle.getContext(),"Succesfully Cancel",Toast.LENGTH_SHORT).show();

                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Toast.makeText(holder.txtTitle.getContext(),"Ok",Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
            }
        });

        holder.completeOfWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                completedJobWork(adminId, userId, jobTitle, companyName, userName, context,workKey,jobStatus,specialKey);
            }
        });

    }

    private void completedJobWork(String adminId, String userId, String jobTitle, String companyName, String userName, Context context
            ,String workkey,String jobStatus,String specialKey) {
        HashMap<String, Object> applicaiton_Details = new HashMap<>();

        //Generating push key to add data to firebase with unique key
        String key = FirebaseDatabase.getInstance().getReference().child("CompletedApplications").push().getKey();

        //adding the data to hashmap
        applicaiton_Details.put("jobTitle", jobTitle);
        applicaiton_Details.put("adminId", adminId);
        applicaiton_Details.put("companyName", companyName);
        applicaiton_Details.put("userId", userId);
        applicaiton_Details.put("userName", userName);
        applicaiton_Details.put("jobKey",workkey);
        applicaiton_Details.put("jobAvailability","Completed");
        applicaiton_Details.put("specialKey",key);

        FirebaseDatabase.getInstance().getReference().child("selectedApplications").child(userId).child(specialKey).removeValue();
        FirebaseDatabase.getInstance().getReference().child("selectedApplications").child(adminId).child(specialKey).child("jobAvailability").setValue("Completed");

//        FirebaseDatabase.getInstance().getReference().child("jobApplications").child(adminId).child(workkey).removeValue();
//        FirebaseDatabase.getInstance().getReference().child("jobApplications").child(userId).child(workkey).updateChildren(applicaiton_Details);


        //adding the data to firebase using hashmap under admin node
        FirebaseDatabase.getInstance().getReference().child("CompletedApplications").child(userId).child(key)
                .updateChildren(applicaiton_Details).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Work is completed", Toast.LENGTH_SHORT).show();


                        }

                    }
                });
    }


    @NonNull
    @Override
    public UserPlacedApplicationAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //the data objects are inflated into the xml file single_data_item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_started_template, parent, false);
        return new UserPlacedApplicationAdapter.Viewholder(view);
    }

    //we need view holder to hold each objet form recyclerview and to show it in recyclerview
    class Viewholder extends RecyclerView.ViewHolder {


        Button cancelWork,completeOfWork;
        TextView txtTitle;
        TextView txtDesc;
        TextView jobStatus;
        TextView jobLocation;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            //assigning the address of the materials
            cancelWork=(Button)itemView.findViewById(R.id.cancelWorkBtn);
            completeOfWork=(Button) itemView.findViewById(R.id.completeWorkBtn);
            txtTitle = (TextView) itemView.findViewById(R.id.Title);
            txtDesc = (TextView) itemView.findViewById(R.id.Desc);
            jobStatus=(TextView) itemView.findViewById(R.id.StatusStarted);
            jobLocation=(TextView) itemView.findViewById(R.id.jobDetails);
        }
    }

}
