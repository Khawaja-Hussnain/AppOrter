package com.khtech.apporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtech.apporter.Models.OrdersModel;
import com.khtech.apporter.ViewHolders.RiderOrderViewHolder;
import com.khtech.apporter.ViewHolders.UserOrdersViewHolder;

import java.util.ArrayList;
import java.util.List;


//This is user orders screen

public class UserOrdersActivity extends AppCompatActivity {

    private RecyclerView ordersRV;
    private OrdersModel ordersModel;
    private UserOrdersViewHolder userOrdersViewHolder;
    private List<OrdersModel> ordersList;
    private DatabaseReference ordersRef;

    TextView txtRiders;
    String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userorders);

        ordersRV=findViewById(R.id.userordersRV);
        ordersRV.setLayoutManager(new LinearLayoutManager(this));
        ordersRV.setHasFixedSize(true);
        ordersList=new ArrayList<>();
        userId= FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    //orders display here
    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "Check", Toast.LENGTH_SHORT).show();
        ordersList.clear();
        ordersRef= FirebaseDatabase.getInstance().getReference("Orders").child("User View");
        ordersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ordersSnapshot : dataSnapshot.getChildren()) {
                       ordersModel=ordersSnapshot.getValue(OrdersModel.class);
                       ordersList.add(ordersModel);
                       userOrdersViewHolder=new UserOrdersViewHolder(UserOrdersActivity.this,ordersList);
                       ordersRV.setAdapter(userOrdersViewHolder);
                    }
                }
                else{
                    Toast.makeText(UserOrdersActivity.this, "You have 0 order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
