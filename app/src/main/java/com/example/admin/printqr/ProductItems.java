package com.example.admin.printqr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ProductItems extends AppCompatActivity {
    
    String productID;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_items);


        productID = getIntent().getStringExtra("productID");
        imageView = findViewById(R.id.productImageView1);
        imageView1 = findViewById(R.id.productImageView2);
        nameET = findViewById(R.id.nameET);
        prodDateET = findViewById(R.id.manDatET);
        expDate = findViewById(R.id.expDateET);
        
        fetchScannedProductData(productID);
    }




    private EditText nameET, prodDateET, expDate;
    private ImageView imageView,imageView1;
    private DatabaseReference productsDBRef;
    private void fetchScannedProductData(String productID) {

        
        productsDBRef = FirebaseDatabase.getInstance().getReference("Products").child(productID);

        productsDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.getChildrenCount() > 0){

                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();

                    if (map.get("Product Name") != null){
                        String product_name = map.get("Product Name").toString();
                        nameET.setText(product_name);
                    }

                    if (map.get("Expiry Date") != null){

                        String expiry_date = map.get("Expiry Date").toString();
                        expDate.setText(expiry_date);
                    }


                    if (map.get("Manufacture Date") != null){
                        String manufacture_date = map.get("Manufacture Date").toString();
                        prodDateET.setText(manufacture_date);
                    }



                    if (map.get("productImageURL") != null){
                        String profileUrl = map.get("productImageURL").toString();
                        Glide.with(getBaseContext()).load(profileUrl).into(imageView);
                    }

                    if (map.get("productQRImageURL") != null){
                        String qrUrl = map.get("productQRImageURL").toString();
                        Glide.with(getBaseContext()).load(qrUrl).into(imageView1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}