package com.khtech.apporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtech.apporter.Models.OrderDetailsModel;
import com.khtech.apporter.ViewHolders.OrderDetailsViewHolder;

import java.util.ArrayList;
import java.util.List;

//This is user order details screen
public class UserOrderDetailsActivity extends AppCompatActivity {

    RecyclerView orderdetailsRV;
    List<OrderDetailsModel> itemsList;
    OrderDetailsModel orderDetailsModel;
    OrderDetailsViewHolder orderDetailsViewHolder;

    TextView tv_orderstatus;
    ImageView call;

    String orderid,orderstatus,acceptedby,uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order_details);

        Bundle bundle=getIntent().getExtras();
        orderid=bundle.getString("orderid");
        orderstatus=bundle.getString("orderstatus");
        acceptedby=bundle.getString("acceptedby");
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        tv_orderstatus=findViewById(R.id.userorderstatus);
        call=findViewById(R.id.call);

        orderdetailsRV=findViewById(R.id.userorderdetailsRV);
        orderdetailsRV.setLayoutManager(new LinearLayoutManager(this));
        orderdetailsRV.setHasFixedSize(true);
        itemsList=new ArrayList<>();

        if (orderstatus.equals("In Progress")){
            call.setVisibility(View.GONE);
        }else if (orderstatus.equals("Accepted")){
            call.setVisibility(View.VISIBLE);
            tv_orderstatus.setText("Order Accepted,Soon you will receive at your current address");
            tv_orderstatus.setTextSize(15);
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseReference riderRef=FirebaseDatabase.getInstance().getReference("Riders");
                    riderRef.child(acceptedby).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String riderphone=dataSnapshot.child("phone").getValue().toString();
                            callRider(riderphone);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            });
        }else if (orderstatus.equals("Delivered")){
            call.setVisibility(View.GONE);
            tv_orderstatus.setText("Order Completed");
        }

    }


    //Here User(buyer) can call rider after order accept and befare order delivered.
    private void callRider(String riderphone) {
        Intent intentcall=new Intent(Intent.ACTION_CALL);
        intentcall.setData(Uri.parse("tel:"+riderphone));
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Please Grant permissions", Toast.LENGTH_SHORT).show();
            RequestionPermissions();
        }else
            startActivity(intentcall);
    }
    private void RequestionPermissions(){
        ActivityCompat.requestPermissions(UserOrderDetailsActivity.this,new String[]{Manifest.permission.CALL_PHONE},1);
    }

    //Order details load here
    @Override
    protected void onStart() {
        super.onStart();

        itemsList.clear();
        final DatabaseReference itemsRef= FirebaseDatabase.getInstance().getReference("Orders").child("User View");
        itemsRef.child(uid).child(orderid).child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemsSnapshot:dataSnapshot.getChildren()){
                    //Toast.makeText(UserOrderDetailsActivity.this, ""+itemsSnapshot.getKey(), Toast.LENGTH_SHORT).show();
                    orderDetailsModel=itemsSnapshot.getValue(OrderDetailsModel.class);
                    itemsList.add(orderDetailsModel);
                    orderDetailsViewHolder=new OrderDetailsViewHolder(UserOrderDetailsActivity.this,itemsList);
                    orderdetailsRV.setAdapter(orderDetailsViewHolder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}