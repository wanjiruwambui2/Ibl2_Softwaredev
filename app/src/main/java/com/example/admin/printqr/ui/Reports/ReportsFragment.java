package com.example.admin.printqr.ui.Reports;

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

import com.example.admin.printqr.R;
import com.example.admin.printqr.productreports.Products;
import com.example.admin.printqr.productreports.ProductsAdapter;
import com.example.admin.printqr.reports.Reports;
import com.example.admin.printqr.reports.ReportsAdapter;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReportsFragment extends Fragment {

    private RecyclerView reportsRecycler;
    private DatabaseReference reportsREF;
    private List<Reports> list = new ArrayList<>();
    private RecyclerView.Adapter mAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_reports, container, false);

        reportsRecycler = root.findViewById(R.id.reportsRecycler);
        reportsREF = FirebaseDatabase.getInstance().getReference("Reports");


        reportsRecycler.setHasFixedSize(true);
        reportsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        if (!list.isEmpty()){
            list.clear();
        }


        reportsREF
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){

                            for (DataSnapshot dataSnapshot :snapshot.getChildren()){


                                Reports reports = new Reports(dataSnapshot.getKey());
                                list.add(reports);

                            }

                            mAdapter=new ReportsAdapter(getContext(),list);
                            reportsRecycler.setAdapter(mAdapter);


                        }else {

                            Snackbar.make(getView(),"No Reports in the Database yet.", BaseTransientBottomBar.LENGTH_LONG).show();

                        }
                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        return root;
    }

}