package com.example.admin.printqr.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.printqr.Products;
import com.example.admin.printqr.ProductsAdapter;
import com.example.admin.printqr.R;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView productsRecycler;
    private DatabaseReference productsREF;
    private List<Products> list=new ArrayList<>();
    private RecyclerView.Adapter mAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_admin_home, container, false);


        productsRecycler = root.findViewById(R.id.recycler);
        productsREF = FirebaseDatabase.getInstance().getReference("Products");



        productsRecycler.setHasFixedSize(true);
        productsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        if (!list.isEmpty()){
            list.clear();
        }

        productsREF
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){

                            for (DataSnapshot dataSnapshot :snapshot.getChildren()){


                                Products products = new Products(dataSnapshot.getKey());
                                list.add(products);

                            }

                            mAdapter=new ProductsAdapter(getContext(),list);
                            productsRecycler.setAdapter(mAdapter);


                        }else {

                            Snackbar.make(getView(),"No products in the Database", BaseTransientBottomBar.LENGTH_LONG).show();

                        }
                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        return root;
    }
}