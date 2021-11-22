package com.khtech.apporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtech.apporter.Models.Cart;
import com.khtech.apporter.Models.ProductsModel;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

//This is product details activity.User(nuyer) can set quantity here and add to cart

public class ProductDetailsActivity extends AppCompatActivity {

    private Button addToCartBtn,add,minus;
    private ImageView productImage;
    private ElegantNumberButton numberButton;
    private TextView productName,productPrice,productDescription;
    String state="normal",Uid;

    String pname;
    String pdesc;
    String pprice;
    String pimage;
    String pid;
    String pstr;
    String pcity;

    String inpid;

    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        //This Comes from ProductViewHolder that is HomeFragment products recyclerview
        Bundle extras = getIntent().getExtras();
        pname=extras.getString("pname");
        pdesc=extras.getString("pdescription");
        pprice=extras.getString("pprice");
        pimage=extras.getString("pimage");
        pid=extras.getString("pid");
        pstr=extras.getString("pstr");
        Toast.makeText(this, "Storename=", Toast.LENGTH_SHORT).show();

        //Comes from cartActivity
        inpid=getIntent().getStringExtra("pid");


        addToCartBtn=findViewById(R.id.pd_add_to_cart);
        productImage=findViewById(R.id.product_image_detail);
        numberButton=findViewById(R.id.numberbtn);
        productName=findViewById(R.id.product_name);
        productDescription=findViewById(R.id.product_descripion_details);
        productPrice=findViewById(R.id.product_price_details);

        user= FirebaseAuth.getInstance().getCurrentUser();
        Uid=user.getUid();


        productName.setText(pname);
        productDescription.setText(pdesc);
        productPrice.setText(pprice);
        Picasso.get().load(pimage).into(productImage);

        CheckOrderState();

        addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckOrderState();

                if (state.equals("Order Placed") || state.equals("Order Shipped")){
                    Toast.makeText(ProductDetailsActivity.this, "You can buy more things once your order delivered to you", Toast.LENGTH_SHORT).show();
                }else{
                    addingToCart();
                }
            }
        });

        getProductDetails(inpid);
    }

    //This will add product to cart
    private void addingToCart() {

        String saveCurrentDate,saveCurrentTime;
        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat currentdate=new SimpleDateFormat("MM,dd,yyyy");
        saveCurrentDate=currentdate.format(calForDate.getTime());
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calForDate.getTime());

        final DatabaseReference cartRef= FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String,Object> cartMap=new HashMap<>();
        if (!inpid.equals("")) {
            pid=inpid;
            pname=productName.getText().toString();
            pprice=productPrice.getText().toString();
            pname=productName.getText().toString();
        }
        cartMap.put("pid", pid);
        cartMap.put("pname",pname);
        cartMap.put("pprice",pprice);
        cartMap.put("storename",pstr);
        cartMap.put("pquantity",numberButton.getNumber());
        cartRef.child(Uid).child("Products").child(pid).updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ProductDetailsActivity.this, "Product Added to Cart", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ProductDetailsActivity.this,NavigationDrawerActivity.class));
                    finish();
                }
            }
        });

    }

    //This will get product details with product id
    private void getProductDetails(String inpid) {

        DatabaseReference productRef= FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child("Grocery").child(inpid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    ProductsModel products=dataSnapshot.getValue(ProductsModel.class);
                    productName.setText(products.getPname());
                    productDescription.setText(products.getPdescription());
                    productPrice.setText(products.getPprice());
                    pstr=products.getStorename();
                    Picasso.get().load(products.getPimage()).into(productImage);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //This code check order state
    private void CheckOrderState(){

        final DatabaseReference ordrRef;
        ordrRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        ordrRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String orderkey = "";
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    orderkey=ds.getKey();
                }
                ordrRef.child(orderkey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){

                            String shippingState=dataSnapshot.child("state").getValue().toString();

                            if (shippingState.equals("In Progress")){

                                state="Order Shipped";

                            }else if (shippingState.equals("Accepted")){

                                state="Order Placed";

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
    }
}