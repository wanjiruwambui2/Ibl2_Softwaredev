package com.example.admin.printqr.user_ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.admin.printqr.R;

public class ProfileFragment extends Fragment {

    ImageView profilePic;
    Button profile;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        profilePic = root.findViewById(R.id.ivProfilePicture);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profilePic = new Intent(Intent.ACTION_PICK);
                profilePic.setType("image/*");
                startActivityForResult(profilePic, 99);
            }
        });

        profile = root.findViewById(R.id.btnProfile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }
}