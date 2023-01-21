package com.example.pigeon.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pigeon.Activities.RoleActivity;
import com.example.pigeon.Model.Model;
import com.example.pigeon.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;

public class ProfileAdapter  extends FirebaseRecyclerAdapter<Model, ProfileAdapter.Viewholder> {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference().child("Users");

    public ProfileAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {

        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull ProfileAdapter.Viewholder holder, int position, @NonNull Model model) {

        holder.nameTxt.setText(model.getUserName());
        holder.aboutTxt.setText(model.getaboutMe());
        holder.workingTxt.setText(model.getWorkExp());
        holder.locationTxt.setText(model.getJobLocation());
        holder.transportTxt.setText(model.getApplicantTransport());
        holder.skillsTxt.setText(model.getApplicantSkills());
        String userId= GoogleSignIn.getLastSignedInAccount(holder.nameTxt.getContext()).getId();
        //Hashmap to store the userdetails and setting it to fireabse

        holder.updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> user_details = new HashMap<>();

                //Accessing the user details from gmail

                String name=holder.nameTxt.getText().toString();
                String role=model.getRole();
                String mail=model.getMail();
                String about=holder.aboutTxt.getText().toString();
                String applicantSk=holder.skillsTxt.getText().toString();
                String applicantTransport=holder.transportTxt.getText().toString();
                String jobLocation=holder.locationTxt.getText().toString();
                String workExp=holder.workingTxt.getText().toString();

                user_details.put("ID", userId);
                user_details.put("Name", name);
                user_details.put("Email", mail);
                user_details.put("Role", role);
                user_details.put("aboutMe",about);
                user_details.put("Skills","");
                user_details.put("applicantTransport",applicantTransport);
                user_details.put("jobLocation",jobLocation);
                user_details.put("workExp",workExp);
                user_details.put("applicantSkills",applicantSk);

                myRef.child(userId).updateChildren(user_details).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(holder.nameTxt.getContext(), RoleActivity.class);
                            Toast.makeText(holder.nameTxt.getContext(),"Succesfully Update",Toast.LENGTH_SHORT).show();
                            holder.nameTxt.getContext().startActivity(intent);
                        }
                    }
                });
            }
        });

    }


    @NonNull
    @Override
    public ProfileAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //the data objects are inflated into the xml file single_data_item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_profile_layout, parent, false);
        return new ProfileAdapter.Viewholder(view);
    }
    //we need view holder to hold each objet form recyclerview and to show it in recyclerview
    class Viewholder extends RecyclerView.ViewHolder {


        EditText nameTxt,aboutTxt,skillsTxt,transportTxt,locationTxt,workingTxt;

        Button updateProfile;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

           nameTxt=(EditText) itemView.findViewById(R.id.editNameTitle);
           aboutTxt=(EditText) itemView.findViewById(R.id.editTextaboutApplicant);
           skillsTxt=(EditText) itemView.findViewById(R.id.editTextskillsApplicant);
           transportTxt=(EditText) itemView.findViewById(R.id.editTexttransport);
           locationTxt=(EditText) itemView.findViewById(R.id.editTextApplicantLocation);
           workingTxt=(EditText) itemView.findViewById(R.id.editTextworkingExperience);
           updateProfile=(Button)itemView.findViewById(R.id.SetUpBtn);
        }
    }


}
