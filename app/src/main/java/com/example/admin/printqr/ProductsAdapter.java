package com.example.admin.printqr;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.myViewHolder>{

    Context context;
    List<Products> products;

    public ProductsAdapter(Context context, List<Products> products) {
        this.context = context;
        this.products = products;
    }


    @NonNull
    @Override
    public ProductsAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.products_recycler, parent, false);

        ProductsAdapter.myViewHolder viewHolder=new ProductsAdapter.myViewHolder(view);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull final myViewHolder holder, int position) {

        final Products productsList = products.get(position);

        DatabaseReference productsDBRef = FirebaseDatabase.getInstance().getReference("Products").child(productsList.getProductID());


        productsDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.getChildrenCount() > 0){

                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();

                    if (map.get("Product Name") != null){
                        String product_name = map.get("Product Name").toString();
                        holder.nameTV.setText(product_name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,ProductItems.class);
                intent.putExtra("productID",productsList.getProductID());
                holder.relativeLayout.getContext().startActivity(intent);
            }
        });

    }



    @Override
    public int getItemCount() {
        return products.size();
    }






    public static class myViewHolder extends RecyclerView.ViewHolder{


        TextView nameTV;
        RelativeLayout relativeLayout;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTV = itemView.findViewById(R.id.nameTV);
            relativeLayout = itemView.findViewById(R.id.productRL);

        }
    }
}
