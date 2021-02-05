package com.example.admin.printqr.user_ui.home;

import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.bumptech.glide.Glide;
import com.example.admin.printqr.MapsActivity;
import com.example.admin.printqr.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Map;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private ImageView imageView;
    private Button scan_code,share_code;
    private IntentIntegrator qrScan;
    private EditText nameET, prodDateET, expDate;
    private LinearLayout productsLL;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        scan_code= (Button) root.findViewById(R.id.button_scan);
        imageView = root.findViewById(R.id.productImageView1);
        nameET = root.findViewById(R.id.nameET);
        prodDateET = root.findViewById(R.id.manDatET);
        expDate = root.findViewById(R.id.expDateET);
        productsLL = root.findViewById(R.id.productLL);



        //scan QR
        qrScan =  IntentIntegrator.forSupportFragment(HomeFragment.this);
        scan_code.setOnClickListener(this);

       return root;
    }


    String productID;

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {

                Toast.makeText(getContext(), "Result Not Found", Toast.LENGTH_LONG).show();

            } else {
                //if qr contains data
                productID = result.getContents();
                fetchScannedProductData(productID);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private DatabaseReference productsDBRef;
    private void fetchScannedProductData(final String productID) {

        productsLL.setVisibility(View.VISIBLE);
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
                        Glide.with(getContext()).load(profileUrl).into(imageView);
                    }
                }else{

                    Snackbar.make(getView(),"Product not found in the database", BaseTransientBottomBar.LENGTH_LONG).show();
                   if (productID!=null) {

                       Intent MapsIntent = new Intent(getContext(), MapsActivity.class);
                       MapsIntent.putExtra("ProductID", productID);
                       startActivity(MapsIntent);

                   }else{


                   }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onClick(View v) {


        if (v == scan_code){

            qrScan.initiateScan();
        }
    }
}