package com.example.pigeon.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pigeon.R;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserDashFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDashFragment extends Fragment {

    TabLayout tabLayout;
    TabItem startedJobs;
    TabItem completedJobs;
    TabItem cancelledJobs;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserDashFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserDashFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserDashFragment newInstance(String param1, String param2) {
        UserDashFragment fragment = new UserDashFragment();
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
        // Inflate the layout for this fragment

        View view=  inflater.inflate(R.layout.fragment_user_dash, container, false);

        tabLayout = (TabLayout)view.findViewById(R.id.tablayout);
        startedJobs=(TabItem) view.findViewById(R.id.started);
        cancelledJobs=(TabItem)view.findViewById(R.id.cancelled);
        completedJobs=(TabItem)view.findViewById(R.id.completed);
        getFragmentManager().beginTransaction().replace(R.id.UserDashBoardContainer, new UserPlacedStartedApplicationsFragment()).commit();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()) {
                    case 0: //started
                        getFragmentManager().beginTransaction().replace(R.id.UserDashBoardContainer, new UserPlacedStartedApplicationsFragment()).commit();
                        break;
                    case 1: //completed
                        getFragmentManager().beginTransaction().replace(R.id.UserDashBoardContainer, new UserJobCompletedApplicationsFragment()).commit();
                        break;//applied
                    case 2: getFragmentManager().beginTransaction().replace(R.id.UserDashBoardContainer, new JobUserAllAplicationsFragment()).commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return view;

    }
}