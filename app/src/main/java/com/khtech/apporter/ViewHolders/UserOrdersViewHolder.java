package com.khtech.apporter.ViewHolders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.khtech.apporter.Models.OrdersModel;
import com.khtech.apporter.R;
import com.khtech.apporter.RiderOrderDetailsActivity;
import com.khtech.apporter.UserOrderDetailsActivity;

import java.util.List;

public class UserOrdersViewHolder extends RecyclerView.Adapter<UserOrdersViewHolder.ViewHolder>{

    Context context;
    List<OrdersModel> ordersList;

    public UserOrdersViewHolder(Context context, List<OrdersModel> ordersList) {
        this.context = context;
        this.ordersList = ordersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.orderslayout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final OrdersModel OrdersModel=ordersList.get(position);
        holder.tv_orderid.setText(OrdersModel.getOrderid());
        holder.tv_orderto.setText(OrdersModel.getOrderto());
        holder.tv_amount.setText("$"+OrdersModel.getTotalAmount());
        holder.tv_status.setText(OrdersModel.getOrderstatus());
        holder.tv_date.setText(OrdersModel.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, UserOrderDetailsActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("acceptedby",OrdersModel.getAcceptedby());
                bundle.putString("orderstatus",OrdersModel.getOrderstatus());
                bundle.putString("orderid",OrdersModel.getOrderid());
                bundle.putString("orderby",OrdersModel.getOrderby());
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ordersList.size();
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
