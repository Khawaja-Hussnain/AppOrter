package com.khtech.apporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtech.apporter.Models.ProductsModel;
import com.khtech.apporter.ViewHolders.ProductViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DatabaseReference ProductsRef;
    RecyclerView ProductsRV;
    List<ProductsModel> productsList;
    ProductsModel products;
    ProductViewHolder productViewHolder;
    Button settings;

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings=findViewById(R.id.settingbtn);
        ProductsRV=findViewById(R.id.PRV);
        ProductsRV.setLayoutManager(new LinearLayoutManager(this));
        ProductsRV.setHasFixedSize(true);
        productsList=new ArrayList<>();

        ProductsRef= FirebaseDatabase.getInstance().getReference().child("Products");
        ProductsRef.child("Grocery").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productsList.clear();
                if (dataSnapshot.exists()){
                    for(DataSnapshot mydataSnapshot:dataSnapshot.getChildren()){
                        products=mydataSnapshot.getValue(ProductsModel.class);
                        productsList.add(products);
                        productViewHolder=new ProductViewHolder(MainActivity.this,productsList);
                        ProductsRV.setAdapter(productViewHolder);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ProfileActivity.class));
            }
        });

    }



    //Code For Double Tap To Exit
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
