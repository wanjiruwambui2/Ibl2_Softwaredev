package com.example.admin.printqr.user_ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.admin.printqr.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    ImageView profilePic;
    Button profile;
    EditText etFirstName, etSecondName, etEmail, etPhoneNumber;
    String FirstName, SecondName, Email, PhoneNumber, UserID;
    String First_Name, Second_Name, E_mail, Phone_Number;
    DatabaseReference dbRefProfile, dbRefRetrieveProfile;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        etFirstName = root.findViewById(R.id.etFirstName);
        etSecondName = root.findViewById(R.id.etSecondName);
        etEmail = root.findViewById(R.id.etEmail);
        etPhoneNumber = root.findViewById(R.id.etPhoneNumber);

        UserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

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

                FirstName = etFirstName.getText().toString().trim();
                SecondName = etSecondName.getText().toString().trim();
                Email = etEmail.getText().toString().trim();
                PhoneNumber = etPhoneNumber.getText().toString();

                if (FirstName.isEmpty() || SecondName.isEmpty() || Email.isEmpty() || PhoneNumber.isEmpty())
                {
                    Toast.makeText(getContext(), "Enter all details", Toast.LENGTH_SHORT).show();
                }else
                {
                    saveProfileToDB();
                }


            }
        });

        retrieveProfileFromDB();

        return root;
    }

    private void retrieveProfileFromDB() {

        dbRefRetrieveProfile = FirebaseDatabase.getInstance().getReference("users").child(UserID);
        dbRefRetrieveProfile.keepSynced(true);
        dbRefRetrieveProfile.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                {
                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();
                    if (map.get("FirstName")!=null)
                    {
                       First_Name = map.get("FirstName").toString();
                       etFirstName.setText(First_Name);
                    }
                    if (map.get("SecondName")!=null)
                    {
                        Second_Name = map.get("SecondName").toString();
                        etSecondName.setText(Second_Name);
                    }
                    if (map.get("email")!=null)
                    {
                        E_mail = map.get("email").toString();
                        etEmail.setText(E_mail);
                    }
                    if (map.get("PhoneNumber")!=null)
                    {
                        Phone_Number = map.get("PhoneNumber").toString();
                        etPhoneNumber.setText(Phone_Number);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void saveProfileToDB() {

        dbRefProfile = FirebaseDatabase.getInstance().getReference("users").child(UserID);
        HashMap map = new HashMap();
        map.put("FirstName", FirstName);
        map.put("SecondName",SecondName);
        map.put("email", Email);
        map.put("PhoneNumber", PhoneNumber);
        dbRefProfile.updateChildren(map)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(getContext(), "Profile Updated Successfully!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}