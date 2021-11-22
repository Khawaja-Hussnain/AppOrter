package com.khtech.apporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtech.apporter.Models.OrdersModel;
import com.khtech.apporter.ViewHolders.RiderOrderViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

//This is riders main screen

public class RidersMainActivity extends AppCompatActivity {

    private RecyclerView ordersRV;
    private OrdersModel riderOrdersModel;
    private RiderOrderViewHolder riderOrderViewHolder;
    private List<OrdersModel> ordersList;
    private DatabaseReference ordersRef;
    private String Riderid;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riders_main);

        CircleImageView imageView = findViewById(R.id.riderimage);
        TextView txtUsername =findViewById(R.id.ridername);
        RiderInfoDisplay(imageView, txtUsername);

        //Orders display work start here
        ordersRV=findViewById(R.id.riderordersRV);
        ordersRV.setLayoutManager(new LinearLayoutManager(this));
        ordersRV.setHasFixedSize(true);
        ordersList=new ArrayList<>();
        Riderid=FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ordersList.clear();
        ordersRef=FirebaseDatabase.getInstance().getReference("Orders").child("Rider View");
        ordersRef.child(Riderid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ordersSnapshot : dataSnapshot.getChildren()) {
                        riderOrdersModel = ordersSnapshot.getValue(OrdersModel.class);
                        ordersList.add(riderOrdersModel);
                        riderOrderViewHolder = new RiderOrderViewHolder(RidersMainActivity.this, ordersList);
                        ordersRV.setAdapter(riderOrderViewHolder);
                    }
                }else {
                    Toast.makeText(RidersMainActivity.this, "You don't have orders right now", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //This function get logedin rider image and name for display above
    private void RiderInfoDisplay(final CircleImageView imageView, final TextView txtUsername) {

        DatabaseReference riderRef= FirebaseDatabase.getInstance().getReference().child("Riders");
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String riderid=firebaseUser.getUid();
        Toast.makeText(RidersMainActivity.this, "this is "+riderid, Toast.LENGTH_SHORT).show();
        riderRef.child(riderid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                        String name=dataSnapshot.child("name").getValue().toString();
                        txtUsername.setText(name);
                       /* String phone=dataSnapshot.child("phone").getValue().toString();
                        String address=dataSnapshot.child("address").getValue().toString();*/
                       if (dataSnapshot.child("image").exists()) {
                           String image = dataSnapshot.child("image").getValue().toString();
                           if (!image.equals("")) {
                               Picasso.get().load(image).into(imageView);
                           }
                           else{
                               Toast.makeText(RidersMainActivity.this, "Image Not Updated", Toast.LENGTH_SHORT).show();
                           }
                       }
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //This will send rider to his profile screen
    public void RiderProfileFunc(View view) {
        startActivity(new Intent(RidersMainActivity.this,RidersProfileActivity.class));
    }

    //This is logout funtction
    public void RederLogoutFunc(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(RidersMainActivity.this,LoginActivity.class));
        finish();
    }

    //Code For Double Tap To Exit
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();return; }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click again to exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() { doubleBackToExitPressedOnce=false;
            }
        }, 2000); }
}