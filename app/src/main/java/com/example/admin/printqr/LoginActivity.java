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
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button loginBt;
    private EditText emailET,passwordET;
    TextView registerTV;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBt = findViewById(R.id.loginBt);

        loginBt.setOnClickListener(this);
        progressBar = findViewById(R.id.loginProgress);

        emailET = findViewById(R.id.loginEmail);
        passwordET = findViewById(R.id.loginPass);

        registerTV = findViewById(R.id.registerTV);
        registerTV.setOnClickListener(this);


    }


    String email,password;
    @Override
    public void onClick(View v) {
        if (v == loginBt){
            email = emailET.getText().toString().trim();
            password = passwordET.getText().toString().trim();

            if (email.isEmpty()){

                emailET.setError("Invalid Email!");
                emailET.requestFocus();


            }else if (password.isEmpty()){

                passwordET.setError("Invalid Password!");
                passwordET.requestFocus();

            }else{

                loginUser(email,password);
            }
        }else if (v == registerTV){

            Intent intent =  new Intent(getBaseContext(),RegisterActivity.class);
            startActivity(intent);
        }
    }





    private FirebaseAuth mAuth;
    private DatabaseReference usersdbRef;

    private void loginUser(String email,String password) {


        mAuth = FirebaseAuth.getInstance();
        usersdbRef = FirebaseDatabase.getInstance().getReference("users");




        progressBar.setVisibility(View.VISIBLE);
        loginBt.setVisibility(View.INVISIBLE);

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressBar.setVisibility(View.INVISIBLE);
                        loginBt.setVisibility(View.VISIBLE);

                        Snackbar.make(getCurrentFocus(),"Error: " + e.getMessage(), BaseTransientBottomBar.LENGTH_LONG).show();

                    }
                }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                String userId = mAuth.getCurrentUser().getUid();
                checkAccType(userId);
            }
        });

    }






    private void checkAccType(String userId) {

        usersdbRef.child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String accountType;
                        if (snapshot.exists() && snapshot.getChildrenCount() > 0){

                            Map<String, Object> map = (Map<String, Object>) snapshot.getValue();

                            if (map.get("accType") != null){
                                accountType = map.get("accType").toString();

                                if (accountType.equals("user")) {

                                    loginBt.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);

                                    Intent intent = new Intent(getBaseContext(), UserHomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    LoginActivity.this.finish();


                                }else if (accountType.equals("Admin")) {

                                    loginBt.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);

                                    Intent intent = new Intent(getBaseContext(), AdminHomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    LoginActivity.this.finish();

                                }

                            }


                        }else{

                            Snackbar.make(getCurrentFocus(),"Account not Found!!",BaseTransientBottomBar.LENGTH_LONG).show();

                            loginBt.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);

                            FirebaseAuth.getInstance().signOut();
                            Intent logout = new Intent(getBaseContext(), LoginActivity.class);
                            logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(logout);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(getBaseContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}