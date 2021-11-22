package com.khtech.apporter.ViewHolders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khtech.apporter.Models.ProductsModel;
import com.khtech.apporter.ProductDetailsActivity;
import com.khtech.apporter.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class ProductViewHolder extends RecyclerView.Adapter<ProductViewHolder.ViewHolder> {

    Context context;
    List<ProductsModel> productsList;

    public ProductViewHolder(Context context, List<ProductsModel> productsList) {
        this.context = context;
        this.productsList = productsList;
    }

    @NonNull
    @Override
    public ProductViewHolder.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.productslayout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder.ViewHolder holder, int position) {

        final ProductsModel products=productsList.get(position);
        holder.pname.setText(products.getPname());
        holder.pdescription.setText(products.getPdescription());
        holder.pprice.setText("$"+products.getPprice());
        Picasso.get().load(products.getPimage()).into(holder.pimageview);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pn=products.getPname();
                String pdesc=products.getPdescription();
                String pp=products.getPprice();
                String pi=products.getPimage();
                String pid=products.getPid();
                String pstr=products.getStorename();

                Intent intent=new Intent(context, ProductDetailsActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("pname",pn);
                bundle.putString("pdescription",pdesc);
                bundle.putString("pprice",pp);
                bundle.putString("pimage",pi);
                bundle.putString("pid",pid);
                bundle.putString("pstr",pstr);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView pname,pdescription,pprice;
        public ImageView pimageview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            pname=itemView.findViewById(R.id.pname);
            pdescription=itemView.findViewById(R.id.pdescription);
            pprice=itemView.findViewById(R.id.pprice);
            pimageview=itemView.findViewById(R.id.pimage);
        }
    }

    //This function recieve list of products which are searching from search products activity.
    public void SetSearchItems(List<ProductsModel> searchList){
        productsList=new ArrayList<>();
        productsList.addAll(searchList);
        notifyDataSetChanged();
    }
}
