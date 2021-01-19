package com.example.admin.printqr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class SplashScreen extends AppCompatActivity {

    FirebaseAuth.AuthStateListener firebaseAuthStateListenner;
    FirebaseAuth mAuth;
    DatabaseReference usersDbRef;
    String accountType;
    String TAG = "calculateWorkTime";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();
        usersDbRef = FirebaseDatabase.getInstance().getReference("users");






        firebaseAuthStateListenner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null){

                    // dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
                    String userId = mAuth.getUid();

                    usersDbRef.child(userId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    if (snapshot.exists() && snapshot.getChildrenCount() > 0){
                                        Map<String, Object> map = (Map<String, Object>) snapshot.getValue();


                                        if (map.get("accType") != null) {
                                            accountType = map.get("accType").toString();

                                            if (accountType.equals("user")){

                                                Intent userIntent = new Intent(getBaseContext(),UserHomeActivity.class);
                                                startActivity(userIntent);
                                                SplashScreen.this.finish();

                                            }else if (accountType.equals("Admin")){

                                                Intent adminIntent = new Intent(getBaseContext(),AdminHomeActivity.class);
                                                startActivity(adminIntent);
                                                SplashScreen.this.finish();
                                            }

                                        }else {

                                            Toast.makeText(getBaseContext(), "Account not Found ", Toast.LENGTH_SHORT).show();
                                        }

                                    }else {

                                        Toast.makeText(getBaseContext(), "Account not Found", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                    Toast.makeText(getBaseContext(), error.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }else{

                    Intent loginIntent = new Intent(getBaseContext(),LoginActivity.class);
                    startActivity(loginIntent);
                    SplashScreen.this.finish();

                }

            }
        };



        Thread splashThread = new Thread(){
            @Override
            public void run() {
                try {


                    sleep(3000);
                   mAuth.addAuthStateListener(firebaseAuthStateListenner);
                } catch (InterruptedException e) {

                    e.printStackTrace();

                }
            }
        };

        splashThread.start();

    }



    @Override
    protected void onStart() {
        super.onStart();

        // mAuth.addAuthStateListener(firebaseAuthStateListenner);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuthStateListenner != null) {

            mAuth.removeAuthStateListener(firebaseAuthStateListenner);

        }
    }
}