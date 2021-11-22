package com.khtech.apporter.ViewHolders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khtech.apporter.Models.OrderDetailsModel;
import com.khtech.apporter.R;

import java.util.List;

public class OrderDetailsViewHolder extends RecyclerView.Adapter<OrderDetailsViewHolder.ViewHolder> {

    Context context;
    List<OrderDetailsModel> itemsList;

    public OrderDetailsViewHolder(Context context, List<OrderDetailsModel> itemsList) {
        this.context = context;
        this.itemsList = itemsList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orderdetailslayout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderDetailsModel orderDetailsModel=itemsList.get(position);
        holder.pname.setText(orderDetailsModel.getPname());
        holder.pprice.setText("$"+orderDetailsModel.getPprice());
        holder.pquantity.setText("Quantity: "+orderDetailsModel.getPquantity());
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView pname,pprice,pquantity;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pname=itemView.findViewById(R.id.items_product_name);
            pprice=itemView.findViewById(R.id.items_product_price);
            pquantity=itemView.findViewById(R.id.items_product_quantity);
        }
    }
}
