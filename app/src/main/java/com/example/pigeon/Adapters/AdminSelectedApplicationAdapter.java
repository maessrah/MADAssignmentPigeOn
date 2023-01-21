package com.example.pigeon.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pigeon.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;


import com.example.pigeon.Model.Model;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdminSelectedApplicationAdapter extends FirebaseRecyclerAdapter<Model, AdminSelectedApplicationAdapter.Viewholder> {

    public AdminSelectedApplicationAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {

        super(options);

    }

    @Override
    protected void onBindViewHolder(@NonNull AdminSelectedApplicationAdapter.Viewholder holder, int position, @NonNull Model model) {
        //StorageReference storageReference;
        Context context=holder.txtTitle.getContext();
        //for loading selected application user name into recycler view
        holder.txtTitle.setText(model.getUserName());

        //for loading the selected application job title
        holder.txtDesc.setText(model.getJobTitle());

//        holder.txtApplicantName.setText(model.getUserName());
        String adminId = GoogleSignIn.getLastSignedInAccount(holder.txtTitle.getContext()).getId();
        holder.txtApplicantName.setText(model.getJobAvailability());

        String jobKey=model.getJobKey();
        holder.txtTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DialogPlus dialogPlus= DialogPlus.newDialog(holder.txtTitle.getContext())
                        .setContentHolder(new ViewHolder(R.layout.proof_image_popup))
                        .setExpanded(true,1500)
                        .create();

                View view = dialogPlus.getHolderView();
                ImageView proofImg;
                proofImg=(ImageView) view.findViewById(R.id.imgProof);
                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Users").child(adminId).child(jobKey);

                try {
                    File localfile=File.createTempFile("tempfile","jpeg");

                    storageReference.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                            Bitmap bitmap= BitmapFactory.decodeFile(localfile.getAbsolutePath());
                            proofImg.setImageBitmap(bitmap);

                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                dialogPlus.show();

            }
        });
        holder.rateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DialogPlus dialogPlus= DialogPlus.newDialog(holder.txtTitle.getContext())
                        .setContentHolder(new ViewHolder(R.layout.rate_popup))
                        .setExpanded(true,500)
                        .create();


                View view = dialogPlus.getHolderView();
                RatingBar rateBar=view.findViewById(R.id.ratingbar);
                Button submitRate=view.findViewById(R.id.submitRate);
                dialogPlus.show();

                rateBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                        //Set selected rating on text view

                    }
                });
                //Getting admin id



                //Generating unique key
                String key = FirebaseDatabase.getInstance().getReference().child("jobs").push().getKey();

                submitRate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Map<String,Object> map = new HashMap<>();

                        String userId=model.getUserId().toString();
                        String workKey=model.getJobKey();
                        String rate= String.valueOf(rateBar.getRating());

                        FirebaseDatabase.getInstance().getReference().child("Users").child(userId)
                                .child("Rating").child(adminId).setValue(rate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            //Setting the edit text to blank  after successfully adding the data to fireabse
                                            Toast.makeText(holder.txtTitle.getContext(), "Rated Successfully", Toast.LENGTH_SHORT).show();

                                            dialogPlus.dismiss();;

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
    public AdminSelectedApplicationAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //the data objects are inflated into the xml file single_data_item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_records, parent, false);
        return new AdminSelectedApplicationAdapter.Viewholder(view);
    }

    //we need view holder to hold each objet form recyclerview and to show it in recyclerview
    class Viewholder extends RecyclerView.ViewHolder {


        Button rateUser;
        TextView txtTitle;
        TextView txtDesc;
        TextView txtApplicantName;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            //assigning the address of the materials
            rateUser=(Button) itemView.findViewById(R.id.rate);
            txtTitle = (TextView) itemView.findViewById(R.id.Title);
            txtDesc = (TextView) itemView.findViewById(R.id.Desc);
            txtApplicantName=(TextView) itemView.findViewById(R.id.jobStatusadmin);
        }
    }

}
