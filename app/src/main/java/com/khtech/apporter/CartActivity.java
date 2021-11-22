package com.khtech.apporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtech.apporter.Models.Cart;
import com.khtech.apporter.Models.ProductsModel;
import com.khtech.apporter.Models.Riders;
import com.khtech.apporter.Models.Users;
import com.khtech.apporter.ViewHolders.CartViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

//This is cart activity where user(buyer) see selected items and confirm order

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Button NextProcessBtn;
    private TextView txtTotalAmount,txtmsg1;

    private int overTotalPrice=0;

    String userphone,useraddress="",usercity,storename,paymentmethod="";
    DatabaseReference userdata,ridersdata;
    List<Cart> cartList;
    List<String> ridersList;
    Random random;
    int rnNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView=findViewById(R.id.carRV);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        NextProcessBtn=findViewById(R.id.next_proces_btn);
        txtTotalAmount=findViewById(R.id.total_price);

        txtmsg1=findViewById(R.id.msg1);
        cartList=new ArrayList<>();
         random=new Random();
         rnNo=random.nextInt(1000+1);


        CheckOrderState();

        userdata=FirebaseDatabase.getInstance().getReference("Users");
        userdata.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userphone=dataSnapshot.child("phone").getValue().toString();
                if (dataSnapshot.child("address").exists()) {
                    useraddress = dataSnapshot.child("address").getValue().toString();
                }else
                {
                    Toast.makeText(CartActivity.this, "Please update your address in profile", Toast.LENGTH_SHORT).show();

                }
                usercity=dataSnapshot.child("city").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Riders data Getting.All riders that match with current user(buyer) city
        ridersdata=FirebaseDatabase.getInstance().getReference().child("Riders");
        ridersdata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ridersList=new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String city=ds.child("city").getValue().toString();
                        String uid=ds.getKey();
                        if (city.equals(usercity)) {
                            ridersList.add(uid);
                        }
                    }
                }else {
                    Toast.makeText(CartActivity.this, "key not exist", Toast.LENGTH_SHORT).show();
                    Toast.makeText(CartActivity.this, "User city: "+usercity, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //Cart items getting that selected for order
        DatabaseReference cartref=FirebaseDatabase.getInstance().getReference("Cart List");
        cartref.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()){
                    Toast.makeText(CartActivity.this, "Empty Cart", Toast.LENGTH_SHORT).show();
                }else
                {

                    for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        Cart cart=new Cart();
                        cart=dataSnapshot1.getValue(Cart.class);
                        cartList.add(cart);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showCart() {

        final DatabaseReference cartListRef= FirebaseDatabase.getInstance().getReference("Cart List");

        FirebaseRecyclerOptions<Cart> options=new FirebaseRecyclerOptions.Builder<Cart>().setQuery(cartListRef
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Products"),Cart.class).build();


        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter=new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull final Cart model) {

                holder.txtProductName.setText(model.getPname());
                holder.txtProductQuantity.setText(" Quantity "+model.getPquantity());
                holder.txtProductprice.setText("Price "+model.getPprice()+"$");

                int oneTypeProductionTPrice=((Integer.valueOf(model.getPprice())) * Integer.valueOf(model.getPquantity()));
                overTotalPrice=overTotalPrice + oneTypeProductionTPrice;
                txtTotalAmount.setText(String.valueOf("Total Price= $"+overTotalPrice));

                NextProcessBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        storename = model.getStorename();//this is created for product supplier details
                        if (useraddress.equals("")) {
                            Toast.makeText(CartActivity.this, "Please update you address in profile", Toast.LENGTH_SHORT).show();
                        } else if(paymentmethod.equals("")){
                          showpaymentDialog();
                        }else{
                            ConfirmOrder(rnNo, storename);
                        }
                    }
                });

                //This code update/edit/remove cart item
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        CharSequence options[]=new CharSequence[]{"Edit","Remove"};
                        final AlertDialog.Builder builder=new AlertDialog.Builder(CartActivity.this);
                        builder.setTitle("Cart Options");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if (i==0){

                                    startActivity(new Intent(CartActivity.this, ProductDetailsActivity.class).putExtra("pid",model.getPid()));
                                }
                                if (i==1){

                                    cartListRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Products")
                                            .child(model.getPid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                Toast.makeText(CartActivity.this, "Item remove Successfully", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(CartActivity.this, NavigationDrawerActivity.class));
                                            }
                                        }
                                    });
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items_layout,parent,false);
                CartViewHolder holder=new CartViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    //Alert dialog for payment method
    private void showpaymentDialog() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialoglayout, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        final RelativeLayout cash,paypal,cardnolayout;
        cardnolayout=dialogView.findViewById(R.id.cardnolayout);
        cash= (RelativeLayout) dialogView.findViewById(R.id.cash);
        final EditText cardno=dialogView.findViewById(R.id.cardno);
        final Button done=dialogView.findViewById(R.id.donebtn);
        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentmethod="cash";
                Toast.makeText(CartActivity.this, "Cash on delivery added", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
        paypal=(RelativeLayout) dialogView.findViewById(R.id.paypal);
        paypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cash.setVisibility(View.GONE);
                cardnolayout.setVisibility(View.VISIBLE);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(cardno.getText().toString())){
                           cardno.requestFocus();
                           cardno.setError("Card no. mandatory");
                        }else {
                            paymentmethod="Paypal";
                            Toast.makeText(CartActivity.this, paymentmethod + " " + " added", Toast.LENGTH_SHORT).show();
                            final AlertDialog alertDialog1 = dialogBuilder.create();
                            alertDialog.dismiss();
                        }
                    }
                });

            }
        });

        alertDialog.show();


    }

    //This is for checking placed ordr state
    private void CheckOrderState(){

        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference ordrRef;

        ordrRef=FirebaseDatabase.getInstance().getReference().child("Orders").child(userId);

        ordrRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ordrkey = "";
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    ordrkey=ds.getKey();
                    Toast.makeText(CartActivity.this, "orderkey= "+ordrkey, Toast.LENGTH_SHORT).show();
                }
                ordrRef.child(ordrkey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){

                            String shippingState=dataSnapshot.child("orderstatus").getValue().toString();

                            if (shippingState.equals("In Progress")){

                                txtTotalAmount.setText("Dear "+"user+"+"\n order is shipped successfully");
                                recyclerView.setVisibility(View.GONE);
                                txtmsg1.setVisibility(View.VISIBLE);
                                txtmsg1.setText("Conguratulation,your final order has been shipped successflly,soon you will receive your order at your door step");
                                NextProcessBtn.setVisibility(View.GONE);
                                Toast.makeText(CartActivity.this, "You can purchase more products,once you received your first order", Toast.LENGTH_SHORT).show();
                            }else if (shippingState.equals("Accepted")){

                                txtTotalAmount.setText("Order Accepted");
                                recyclerView.setVisibility(View.GONE);
                                txtmsg1.setVisibility(View.VISIBLE);
                                NextProcessBtn.setVisibility(View.GONE);
                                Toast.makeText(CartActivity.this, "You can purchase more products,once you received your first order", Toast.LENGTH_SHORT).show();
                            }if (shippingState.equals("Completed")){
                                txtTotalAmount.setText("Order Accepted");
                                recyclerView.setVisibility(View.VISIBLE);
                                txtmsg1.setVisibility(View.GONE);
                                NextProcessBtn.setVisibility(View.VISIBLE);
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

    @Override
    protected void onStart() {
        super.onStart();
        showCart();
    }

    //This is confirm order code
    private void ConfirmOrder(final int randomNo, String storename) {

        final String randomstr="GYte1"+String.valueOf(randomNo)+"dsdhj";

        if (cartList.size() == 0) {
            Toast.makeText(this, "No items in cart", Toast.LENGTH_SHORT).show();
        } else{

            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Placing order");
            dialog.setMessage("Please wait,white we are placing your order");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            String saveCurrentDate, saveCurrentTime;
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM,dd,yyyy");
            saveCurrentDate = currentDate.format(calForDate.getTime());
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            saveCurrentTime = currentTime.format(calForDate.getTime());

            //Order Data
            final HashMap<String, Object> orderMap = new HashMap<>();

            orderMap.put("totalAmount", String.valueOf(overTotalPrice));
            orderMap.put("orderid", String.valueOf(randomstr));
            orderMap.put("address", useraddress);
            orderMap.put("phone", userphone);
            //orderMap.put("city",users.getCity());
            orderMap.put("date", saveCurrentDate);
            orderMap.put("time", saveCurrentTime);
            orderMap.put("orderstatus", "In Progress");
            orderMap.put("orderby", FirebaseAuth.getInstance().getCurrentUser().getUid());
            orderMap.put("orderto", storename);

            final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child("User View")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            orderRef.child(randomstr).setValue(orderMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    for (int i = 0; i < cartList.size(); i++) {

                        String pid = cartList.get(i).getPid();
                        String pname = cartList.get(i).getPname();
                        String pprice = cartList.get(i).getPprice();
                        String pquantity = cartList.get(i).getPquantity();

                        HashMap<String, Object> itemsMap = new HashMap<>();

                        itemsMap.put("pid", pid);
                        itemsMap.put("pname", pname);
                        itemsMap.put("pprice", String.valueOf(pprice));
                        itemsMap.put("pquantity", pquantity);

                        orderRef.child(randomstr).child("Items").child(pid).setValue(itemsMap);

                    }
                    dialog.dismiss();
                    Toast.makeText(CartActivity.this, "Order " + randomstr+"\n"+ " placed successfully...", Toast.LENGTH_SHORT).show();

                   //This code send order to riders match with current user(buyer) city
                    DatabaseReference riderRef = FirebaseDatabase.getInstance().getReference("Orders").child("Rider View");
                    for (int i = 0; i < ridersList.size(); i++) {
                        riderRef.child(ridersList.get(i)).child(String.valueOf(randomstr)).setValue(orderMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        FirebaseDatabase.getInstance().getReference("Cart List")
                                      .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent=new Intent(CartActivity.this, NavigationDrawerActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CartActivity.this, "Order not send to riders due to: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                    Toast.makeText(CartActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}