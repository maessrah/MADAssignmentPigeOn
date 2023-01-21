package com.example.pigeon.Adapters;

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

public class UserAllApplicationsAdapter extends FirebaseRecyclerAdapter<Model, UserAllApplicationsAdapter.Viewholder> {

    public UserAllApplicationsAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {

        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull UserAllApplicationsAdapter.Viewholder holder, int position, @NonNull Model model) {

        Context context = holder.txtTitle.getContext();

        //for loading applications of a user into recycler view
        holder.txtTitle.setText(model.getCompanyName());
        holder.txtDesc.setText(model.getJobTitle());
        holder.txtPlace.setText(model.getJobLocation());

        holder.txtStatus.setText(model.getJobAvailability());
        String userId = GoogleSignIn.getLastSignedInAccount(holder.txtTitle.getContext()).getId();
        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child("jobApplications").child(userId).child(model.getSpecialKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseDatabase.getInstance().getReference().child("jobApplications").child(model.getAdminId().toString()).child(model.getSpecialKey()).removeValue();
                        //FirebaseDatabase.getInstance().getReference().child("selectedApplications").child(model.getAdminId()).child(model.getJobKey()).child(model.getJobAvailability()).setValue("Cancelled");

                        Toast.makeText(context, "Application deleted", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



    }


    @NonNull
    @Override
    public UserAllApplicationsAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //the data ojects are inflated into the xml file single_data_item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_data_file, parent, false);
        return new UserAllApplicationsAdapter.Viewholder(view);
    }

    //we need view holder to hold each objet form recyclerview and to show it in recyclerview
    class Viewholder extends RecyclerView.ViewHolder {


        Button cancel;
        TextView txtTitle;
        TextView txtDesc;
        TextView txtPlace;
        TextView txtStatus;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            txtPlace=(TextView) itemView.findViewById(R.id.jobLocation);
            cancel=(Button)itemView.findViewById(R.id.CancelApplication);
            //assigning the address of the materials
            txtTitle = (TextView) itemView.findViewById(R.id.Title);
            txtDesc = (TextView) itemView.findViewById(R.id.Desc);
            txtStatus=(TextView) itemView.findViewById(R.id.Status);
        }
    }
}
