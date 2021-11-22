package com.khtech.apporter.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtech.apporter.MainActivity;
import com.khtech.apporter.Models.ProductsModel;
import com.khtech.apporter.Models.Users;
import com.khtech.apporter.R;
import com.khtech.apporter.ViewHolders.ProductViewHolder;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    DatabaseReference ProductsRef;
    RecyclerView ProductsRV;
    List<ProductsModel> productsList;
    ProductsModel products;
    ProductViewHolder productViewHolder;
    //String city="";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_home2, container, false);

        ProductsRV=view.findViewById(R.id.PRV);
        ProductsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        ProductsRV.setHasFixedSize(true);
        productsList=new ArrayList<>();
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference cityRef=FirebaseDatabase.getInstance().getReference("Users");
               cityRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) { // for city name fetch

                     String city=dataSnapshot.child("city").getValue().toString();

                       ProductsRef= FirebaseDatabase.getInstance().getReference().child("Products");
                       ProductsRef.child("Grocery").orderByChild("city").equalTo(city).addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot dataSnapshot) { // for Products fetch fetch
                               productsList.clear();
                               if (dataSnapshot.exists()){
                                   for(DataSnapshot mydataSnapshot:dataSnapshot.getChildren()){
                                       products=mydataSnapshot.getValue(ProductsModel.class);
                                       productsList.add(products);
                                       productViewHolder=new ProductViewHolder(getContext(),productsList);
                                       ProductsRV.setAdapter(productViewHolder);
                                   }
                               }
                           }

                           @Override
                           public void onCancelled(@NonNull DatabaseError databaseError) {

                           }
                       });
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });

        return view;
    }
}