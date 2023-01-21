package com.example.pigeon.Activities;

import static com.example.pigeon.Adapters.UserJobCompletedAdapter.SELECT_IMAGE_REQUEST_CODE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pigeon.Fragments.DisplayJobFragment;
import com.example.pigeon.Fragments.UserJobCompletedApplicationsFragment;
import com.example.pigeon.Model.MainActivity;
import com.example.pigeon.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AddImageActivity extends AppCompatActivity {



    ImageView proofImg;
    Button uploadProofBtn;
    StorageReference storageReference;
    FloatingActionButton backBtn;

    Uri selectedImageUri;
    ActivityResultLauncher<Intent>activityResultLauncherForSelectImage;

    private Bitmap selectedImage;
    private Bitmap scaledImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        Bundle bundle = getIntent().getExtras();
        String userId=bundle.getString("userID");
        String adminId=bundle.getString("adminID");
        String jobKey=bundle.getString("workKey");

        proofImg=findViewById(R.id.proofImage);
        uploadProofBtn=findViewById(R.id.uploadProofBtn);
        backBtn=findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddImageActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        uploadProofBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ByteArrayOutputStream outputStream= new ByteArrayOutputStream();
                //scaledImage=makeSmall(selectedImage,300);
                selectedImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
                byte[] image=outputStream.toByteArray();
                Intent intent = new Intent();
                intent.putExtra("Id",userId);
                intent.putExtra("adminID",adminId);
                intent.putExtra("image",image);
                setResult(RESULT_OK,intent);
                finish();
                storageReference= FirebaseStorage.getInstance().getReference().child("Users");
                storageReference.child(adminId).child(jobKey).putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.child(adminId).child(jobKey).putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                proofImg.setImageURI(null);
                                Toast.makeText(AddImageActivity.this,"Succesfully uploaded",Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(AddImageActivity.this, MainActivity.class);
                                startActivity(intent);

                            }
                        });

                    }
                });

            }
        });

        proofImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // registerActivityForSelectImage();
                Intent intent = new Intent(Intent.ACTION_PICK);

                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, SELECT_IMAGE_REQUEST_CODE);
            }
        });

    }

    public void registerActivityForSelectImage(){

        activityResultLauncherForSelectImage=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode = result.getResultCode();
                Intent data=result.getData();

                if (resultCode == RESULT_OK && data!=null){

                    try {

                        selectedImage= MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
                        proofImg.setImageBitmap(selectedImage);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
             selectedImageUri = data.getData();
            ImageView imageView = findViewById(R.id.proofImage);
            try {
                selectedImage= MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
                proofImg.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imageView.setImageURI(selectedImageUri);
        }
    }

    public Bitmap makeSmall(Bitmap image,int maxSize){

        int width=image.getWidth();
        int height=image.getHeight();

        float ratio = (float) width/ (float) height;

        if(ratio>1){

            width =maxSize;
            height= (int) (width/ratio);

        }else{

            height = maxSize;
            width=(int)(height * ratio);

        }

        return Bitmap.createScaledBitmap(image,width,height,true);
    }
}