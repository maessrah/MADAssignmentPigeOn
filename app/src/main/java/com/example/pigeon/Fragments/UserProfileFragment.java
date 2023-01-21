package com.example.pigeon.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pigeon.Activities.Login_Page;
import com.example.pigeon.Activities.SetUpActivity;
import com.example.pigeon.Adapters.ProfileAdapter;
import com.example.pigeon.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {

    TextView userName;
    Button signOutBtn;
    Button editProfBtn;
    RecyclerView recyclerView;
    TextView name,address,bio,skills,language,workExp,transport;


    ProfileAdapter adapter;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

//        imageView = (CircleImageView) view.findViewById(R.id.UserProfileImg);
        userName = (TextView) view.findViewById(R.id.UserNameTxt);
        signOutBtn = (Button) view.findViewById(R.id.UserSignOutBtn);
        editProfBtn=(Button) view.findViewById(R.id.UserEditBtn);
        name=(TextView)view.findViewById(R.id.Nametxt);
        address=(TextView) view.findViewById(R.id.Addresstxt);
        bio=(TextView) view.findViewById(R.id.Biotxt);
        skills=(TextView) view.findViewById(R.id.Skillstxt);
        language=(TextView) view.findViewById(R.id.Languagestxt);
        workExp=(TextView)view.findViewById(R.id.Jobpreftxt);
        transport=(TextView) view.findViewById(R.id.Transporttxt);


        //Getting user detials from GoogleSignin
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());

        String id = GoogleSignIn.getLastSignedInAccount(getActivity()).getId();
        String userId= acct.getId();
        String email=acct.getEmail();
        if (acct != null) {
            userName.setText(acct.getDisplayName());
//            Picasso.get().load(acct.getPhotoUrl()).into(imageView);
        }

        editProfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),SetUpActivity.class);
                intent.putExtra("ID",userId);
                startActivity(intent);


            }
        });

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String applicantName = dataSnapshot.child("Name").getValue(String.class);
                String aboutApplicant =  dataSnapshot.child("aboutMe").getValue(String.class);
                String applicantTransport= dataSnapshot.child("applicantTransport").getValue(String.class);
                String applicantLocation = dataSnapshot.child("jobLocation").getValue(String.class);
                String workexp=dataSnapshot.child("workExp").getValue(String.class);
                String applicantSkills=dataSnapshot.child("applicantSkills").getValue(String.class);
                String email=dataSnapshot.child("Email").getValue(String.class);


                name.setText(applicantName);
                bio.setText(aboutApplicant);
                skills.setText(applicantSkills);
                transport.setText(applicantTransport);
                address.setText(applicantLocation);
                workExp.setText(workexp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //implementing onClickListener to make the user signOut
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoogleSignInOptions gso = new GoogleSignInOptions.
                        Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                        build();

                //GoogleSignInClient to access the current user
                GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
                googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //User Signout
                            FirebaseAuth.getInstance().signOut();

                            //Redirecting to starting Activity
                            Intent intent = new Intent(getContext(), Login_Page.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }

                    }
                });

            }
        });


        return view;
    }
}