package com.example.admin.printqr.reports;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.admin.printqr.R;
import com.example.admin.printqr.productreports.Products;
import com.example.admin.printqr.productreports.ProductsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;


public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.myViewHolder>{


    Context context;
    List<Reports> reports;

    public ReportsAdapter(Context context, List<Reports> reports) {
        this.context = context;
        this.reports = reports;
    }




    @NonNull
    @Override
    public ReportsAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.reports_recycler_layout, parent, false);

        ReportsAdapter.myViewHolder viewHolder = new ReportsAdapter.myViewHolder(view);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, int position) {

        final Reports reportsList  = reports.get(position);

        DatabaseReference productsDBRef = FirebaseDatabase.getInstance().getReference("Reports").child(reportsList.getReportID());


        productsDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.getChildrenCount() > 0){

                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();

                    if (map.get("productID") != null){

                        String product_name = map.get("productID").toString();
                        holder.nameTV.setText(product_name);

                    }

                    if (map.get("reporter") != null){
                        String userID = map.get("reporter").toString();

                        DatabaseReference UsersDBRef = FirebaseDatabase.getInstance().getReference("users").child(userID);
                        UsersDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                if (snapshot.exists() && snapshot.getChildrenCount() > 0){

                                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();

                                    if (map.get("email") != null){

                                        String email = map.get("email").toString();
                                        holder.userTV.setText(email);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }




    @Override
    public int getItemCount() {
        return reports.size();
    }


    public static class myViewHolder extends RecyclerView.ViewHolder{


        TextView nameTV,userTV;


        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTV = itemView.findViewById(R.id.productID);
            userTV = itemView.findViewById(R.id.user);


        }
    }

}
