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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtech.apporter.Models.OrderDetailsModel;
import com.khtech.apporter.Models.Riders;
import com.khtech.apporter.ViewHolders.OrderDetailsViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

//This is riders order details screen where he/she can accept order/cancel order or delivered and also see details of order.

public class RiderOrderDetailsActivity extends AppCompatActivity {

    TextView orderby,orderbyphone,orderbyaddress,ordercompleted;
    ImageView call;
    Button accept,cancel,delivered;
    LinearLayout linearLayout;

    String orderbyid,orderid,orderstatus,address,uid,userphone;

    RecyclerView orderdetailsRV;
    List<OrderDetailsModel> itemsList;
    OrderDetailsModel orderDetailsModel;
    OrderDetailsViewHolder riderOrderDetailsViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_order_details);

        Bundle bundle=getIntent().getExtras();
        orderbyid=bundle.getString("orderby");
        orderid=bundle.getString("orderid");
        orderstatus=bundle.getString("orderstatus");
        address=bundle.getString("address");
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        orderby=findViewById(R.id.txtorderby);
        orderbyphone=findViewById(R.id.txtorderbyphone);
        orderbyaddress=findViewById(R.id.txtorderbyaddress);

        accept=findViewById(R.id.acceptbtn);
        cancel=findViewById(R.id.canceltbtn);
        call=findViewById(R.id.call);
        delivered=findViewById(R.id.delivertbtn);

        ordercompleted=findViewById(R.id.ordercompleted);
        linearLayout=findViewById(R.id.linearlayout);


        orderdetailsRV=findViewById(R.id.riderorderdetailsRV);
        orderdetailsRV.setLayoutManager(new LinearLayoutManager(this));
        orderdetailsRV.setHasFixedSize(true);
        itemsList=new ArrayList<>();

        orderByInfo();

        if (orderstatus.equals("In Progress")){
            call.setVisibility(View.GONE);
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderAccepted();
                }
            });
            
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase.getInstance().getReference("Orders").child("Rider View")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(orderid).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RiderOrderDetailsActivity.this, "Order cancelled", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RiderOrderDetailsActivity.this,RidersMainActivity.class));
                                    finish();
                                }
                            });
                }
            });

        }else if(orderstatus.equals("Accepted")){
            call.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            delivered.setVisibility(View.VISIBLE);
            delivered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Deliver();
                }
            });

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callUser();
                }
            });
        }
        else if (orderstatus.equals("Delivered")){
            ordercompleted.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.GONE);
            delivered.setVisibility(View.GONE);
            call.setVisibility(View.GONE);
        }

    }

   // This function delivered order
    private void Deliver() {

        final DatabaseReference acceptedRef=FirebaseDatabase.getInstance().getReference("Orders").child("Rider View");
        final HashMap<String,Object> statusMap=new HashMap<>();
        statusMap.put("orderstatus","Delivered");
        acceptedRef.child(uid).child(orderid).updateChildren(statusMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final DatabaseReference acceptedRef1=FirebaseDatabase.getInstance().getReference("Orders").child("User View");
                acceptedRef1.child(orderbyid).child(orderid).updateChildren(statusMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RiderOrderDetailsActivity.this, "Order Delivered", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RiderOrderDetailsActivity.this,RidersMainActivity.class));
                        finish();
                    }
                });

            }
        });


    }

    //This is order accepted and delete from other riders
    private void orderAccepted() {

        String saveCurrentDate, saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM,dd,yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final DatabaseReference acceptedRef=FirebaseDatabase.getInstance().getReference("Orders").child("Rider View");
        final HashMap<String,Object> statusMap=new HashMap<>();
        statusMap.put("orderstatus","Accepted");
        statusMap.put("date",saveCurrentDate);
        statusMap.put("time",saveCurrentTime);
        acceptedRef.child(uid).child(orderid).updateChildren(statusMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                final DatabaseReference acceptedRef1=FirebaseDatabase.getInstance().getReference("Orders").child("User View");
                acceptedRef1.child(orderbyid).child(orderid).updateChildren(statusMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        HashMap<String,Object> accepterInfoMap=new HashMap<>();
                        accepterInfoMap.put("acceptedby",uid);//put rider uid to userview
                        acceptedRef1.child(orderbyid).child(orderid).updateChildren(accepterInfoMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                final DatabaseReference removeRef=FirebaseDatabase.getInstance().getReference("Orders").child("Rider View");
                                removeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String key;
                                        for (DataSnapshot removeSnapshot:dataSnapshot.getChildren()){
                                            key=removeSnapshot.getKey();
                                            if (key.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                                                Toast.makeText(RiderOrderDetailsActivity.this, "Order Accepted", Toast.LENGTH_SHORT).show();
                                            }else{
                                                removeRef.child(key).child(orderid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()){
                                                            dataSnapshot.getRef().removeValue();
                                                        }
                                                        else{
                                                            Toast.makeText(RiderOrderDetailsActivity.this, "Rider Not Exist", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

    }

    //Collecting order creater info
    private void orderByInfo() {
        DatabaseReference orderByRef=FirebaseDatabase.getInstance().getReference("Users");
        orderByRef.child(orderbyid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.child("name").getValue().toString();
                 userphone=dataSnapshot.child("phone").getValue().toString();
                orderby.setText(name);
                orderbyphone.setText(userphone);
                orderbyaddress.setText(address);//this address is comes from RiderOrdrViewHolder through bundle.
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //Load order items
    @Override
    protected void onStart() {
        super.onStart();
        itemsList.clear();
        final DatabaseReference itemsRef= FirebaseDatabase.getInstance().getReference("Orders").child("User View");
        itemsRef.child(orderbyid).child(orderid).child("Items").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemsSnapshot:dataSnapshot.getChildren()){
                    orderDetailsModel=itemsSnapshot.getValue(OrderDetailsModel.class);
                    itemsList.add(orderDetailsModel);
                    riderOrderDetailsViewHolder=new OrderDetailsViewHolder(RiderOrderDetailsActivity.this,itemsList);
                    orderdetailsRV.setAdapter(riderOrderDetailsViewHolder);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

   //This will call user when after order accept and before delivered
    private void callUser() {
        String Phone=orderbyphone.getText().toString();
        Intent intentcall=new Intent(Intent.ACTION_CALL);
        intentcall.setData(Uri.parse("tel:"+userphone));
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Please Grant permissions", Toast.LENGTH_SHORT).show();
            RequestionPermissions();
        }else
            startActivity(intentcall);
    }
   private void RequestionPermissions(){
        ActivityCompat.requestPermissions(RiderOrderDetailsActivity.this,new String[]{Manifest.permission.CALL_PHONE},1);
    }
}