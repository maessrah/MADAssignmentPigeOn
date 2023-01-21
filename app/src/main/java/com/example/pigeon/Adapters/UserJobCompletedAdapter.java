package com.example.pigeon.Adapters;



import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
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

import com.example.pigeon.Activities.AddImageActivity;
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

public class UserJobCompletedAdapter extends FirebaseRecyclerAdapter<Model, UserJobCompletedAdapter.Viewholder> {
    private static final int REQUEST_CODE = 1;
    public static final int SELECT_IMAGE_REQUEST_CODE = 1;
    public UserJobCompletedAdapter(@NonNull FirebaseRecyclerOptions<Model> options) {

        super(options);

    }


    @Override
    protected void onBindViewHolder(@NonNull UserJobCompletedAdapter.Viewholder holder, int position, @NonNull Model model) {

        Context context = holder.txtTitle.getContext();

        holder.txtjobDetails.setText(model.getJobLocation());
    //for loading selected applications into recycler view
        holder.txtTitle.setText(model.getCompanyName());
        holder.txtDesc.setText(model.getJobTitle());
        String adminId = model.getAdminId();
       holder.txtJobStatus.setText(model.getJobAvailability());
        String userId = GoogleSignIn.getLastSignedInAccount(holder.txtTitle.getContext()).getId();
        String workKey=model.getJobKey();
        holder.rateBtn.setOnClickListener(new View.OnClickListener() {
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

                        //String userId=model.getAdminId()Id().toString();
                        String workKey=model.getJobKey().toString();
                        String rate= String.valueOf(rateBar.getRating());

                        FirebaseDatabase.getInstance().getReference().child("Users").child(adminId)
                                .child("Rating").child(userId).setValue(rate)
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

        holder.proofofWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, AddImageActivity.class);
                intent.putExtra("adminID",adminId);
                intent.putExtra("userID",userId);
                intent.putExtra("workKey",workKey);
                context.startActivity(intent);



            }
        });

}



    @NonNull
    @Override
    public UserJobCompletedAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //the data objects are inflated into the xml file single_data_item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_completed_template, parent, false);
        return new UserJobCompletedAdapter.Viewholder(view);
    }



//we need view holder to hold each objet form recyclerview and to show it in recyclerview
class Viewholder extends RecyclerView.ViewHolder {


    Button rateBtn,proofofWork;
    TextView txtTitle;
    TextView txtDesc;
    TextView txtJobStatus;
    TextView txtjobDetails;

    public Viewholder(@NonNull View itemView) {
        super(itemView);

        //assigning the address of the materials

        rateBtn=(Button) itemView.findViewById(R.id.rateBtn);

        txtJobStatus=(TextView)itemView.findViewById(R.id.jobStatusCompleted);
        proofofWork=(Button)itemView.findViewById(R.id.proofBtn);
        txtTitle = (TextView) itemView.findViewById(R.id.Title);
        txtDesc = (TextView) itemView.findViewById(R.id.Desc);
        txtjobDetails=(TextView) itemView.findViewById(R.id.jobDetails);
    }
}}
