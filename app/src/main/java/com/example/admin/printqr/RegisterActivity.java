package com.example.admin.printqr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {


    private Button registerBT;
    private EditText emailET,passwordET,cpasswordET;
    private TextView loginTV;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        registerBT = findViewById(R.id.registerBt);

        registerBT.setOnClickListener(this);

        emailET = findViewById(R.id.registerEmail);
        passwordET = findViewById(R.id.registerPass);
        cpasswordET = findViewById(R.id.regcPass);

        progressBar = findViewById(R.id.registerProgress);

        loginTV = findViewById(R.id.registerTV);
        loginTV.setOnClickListener(this);
    }




    String email,password,confirmPass;
    @Override
    public void onClick(View v) {

        if (v == registerBT){
            email = emailET.getText().toString().trim();
            password = passwordET.getText().toString().trim();
            confirmPass = cpasswordET.getText().toString().trim();

            if (email.isEmpty()){

                emailET.setError("Invalid Email!");
                emailET.requestFocus();


            }else if (password.isEmpty()){

                passwordET.setError("Invalid password!");
                passwordET.requestFocus();

            }else if (confirmPass.isEmpty()){

                cpasswordET.setError("Invalid password!");
                cpasswordET.requestFocus();

            }else if (!confirmPass.equals(password)){

                cpasswordET.setError("Passwords do not match!!");
                cpasswordET.requestFocus();
                passwordET.setError("Passwords do not match!!");
                passwordET.requestFocus();

            } else{

                registerUser(email,password);
            }
        }else if (v == loginTV){

            Intent intent =  new Intent(getBaseContext(),LoginActivity.class);
            startActivity(intent);
        }

    }



    private FirebaseAuth mAuth;
    private DatabaseReference usersdbRef;

    private void registerUser(final String email, String password) {

        mAuth = FirebaseAuth.getInstance();
        usersdbRef = FirebaseDatabase.getInstance().getReference("users");

        progressBar.setVisibility(View.VISIBLE);
        registerBT.setVisibility(View.INVISIBLE);


        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressBar.setVisibility(View.INVISIBLE);
                        registerBT.setVisibility(View.VISIBLE);

                        Toast.makeText(getBaseContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                String userId =  mAuth.getUid();

                HashMap map = new HashMap();
                map.put("email",email);
                map.put("accType","user");

                usersdbRef.child(userId)
                        .setValue(map)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                progressBar.setVisibility(View.GONE);
                                registerBT.setVisibility(View.VISIBLE);
                                Toast.makeText(getBaseContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        progressBar.setVisibility(View.GONE);
                        registerBT.setVisibility(View.VISIBLE);

                        Toast.makeText(getBaseContext(), "Account Registered successfully!!", Toast.LENGTH_SHORT).show();


                        Intent intent =  new Intent(getBaseContext(),LoginActivity.class);
                        startActivity(intent);

                    }
                });
            }
        });
    }


}
