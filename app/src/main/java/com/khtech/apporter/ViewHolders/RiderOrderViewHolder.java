package com.khtech.apporter.ViewHolders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khtech.apporter.Models.OrdersModel;
import com.khtech.apporter.R;
import com.khtech.apporter.RiderOrderDetailsActivity;

import java.util.List;

public class RiderOrderViewHolder extends RecyclerView.Adapter<RiderOrderViewHolder.ViewHolder> {

    Context context;
    List<OrdersModel> ordersModelList;

    public RiderOrderViewHolder(Context context, List<OrdersModel> ordersModelList) {
        this.context = context;
        this.ordersModelList = ordersModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orderslayout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final OrdersModel riderOrdersModel=ordersModelList.get(position);

        holder.tv_orderid.setText(riderOrdersModel.getOrderid());
        holder.tv_orderto.setText(riderOrdersModel.getOrderto());
        holder.tv_amount.setText("$"+riderOrdersModel.getTotalAmount());
        holder.tv_status.setText(riderOrdersModel.getOrderstatus());
        holder.tv_date.setText(riderOrdersModel.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, RiderOrderDetailsActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("orderby",riderOrdersModel.getOrderby());
                bundle.putString("orderid",riderOrdersModel.getOrderid());
                bundle.putString("orderstatus",riderOrdersModel.getOrderstatus());
                bundle.putString("address",riderOrdersModel.getAddress());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

       public TextView tv_orderid,tv_orderto,tv_amount,tv_status,tv_date;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_orderid=itemView.findViewById(R.id.txtorderid);
            tv_orderto=itemView.findViewById(R.id.txtstorename);
            tv_amount=itemView.findViewById(R.id.txtamount);
            tv_status=itemView.findViewById(R.id.orderstatus);
            tv_date=itemView.findViewById(R.id.txtdate);

        }
    }
}
