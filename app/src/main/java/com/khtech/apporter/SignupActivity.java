package com.khtech.apporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Random;

//This is signup for both user(buyer) and rider
public class SignupActivity extends AppCompatActivity {

    EditText ed_username,ed_email,ed_phone,ed_password,ed_city;
    Button Register;
    CheckBox rider;
    String type;
    ProgressDialog loadingBar;

   private FirebaseAuth mAuth;
   private DatabaseReference rootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ed_username=findViewById(R.id.txtusername);
        ed_email=findViewById(R.id.txtemail);
        ed_phone=findViewById(R.id.txtphone);
        ed_city=findViewById(R.id.txtcity);
        ed_password=findViewById(R.id.txtpassword);
        Register=findViewById(R.id.register);
        rider=findViewById(R.id.ridercheckBox);

        loadingBar=new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        rootRef= FirebaseDatabase.getInstance().getReference();

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=ed_username.getText().toString().toLowerCase();
                String email=ed_email.getText().toString().toLowerCase();
                String phone=ed_phone.getText().toString().toLowerCase();
                String city=ed_city.getText().toString().toLowerCase();
                String password=ed_password.getText().toString().toLowerCase();

                if (TextUtils.isEmpty(name)){
                    ed_username.requestFocus();
                    ed_username.setError("Empty");
                }else
                if (TextUtils.isEmpty(email)){
                    ed_email.requestFocus();
                    ed_email.setError("Empty");
                }else
                if (TextUtils.isEmpty(phone)){
                    ed_phone.requestFocus();
                    ed_phone.setError("Empty");
                }else if(TextUtils.isEmpty(city)) {
                        ed_city.requestFocus();
                        ed_city.setError("Empty");
                }else if (TextUtils.isEmpty(password)){
                    ed_password.requestFocus();
                    ed_password.setError("Empty");
                }else if(rider.isChecked()){

                    //this code verify validate that only these 3(warsa,krakow,gdansk) cities users or riders can register
                    if (city.equals("warsaw") || city.equals("krakow") || city.equals("gdansk")) {
                        type = "Riders";
                        loadingBar.setTitle("Creating Account");
                        loadingBar.setMessage("Please wait,while we are checking the credentials...");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        CreateAccount(name, phone, email,  city, password);
                    }else{
                        Toast.makeText(SignupActivity.this, "Sorry service not available in this city", Toast.LENGTH_SHORT).show();
                        ed_city.requestFocus();
                        ed_city.setError("Service not available in this city");
                    }
                }
                else {
                    if (city.equals("warsaw") || city.equals("krakow") || city.equals("gdansk")) {
                        type = "Users";
                        loadingBar.setTitle("Creating Account");
                        loadingBar.setMessage("Please wait,while we are checking the credentials...");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        CreateAccount(name, phone, email, city, password);
                    } else {
                        Toast.makeText(SignupActivity.this, "Sorry service not available in this city", Toast.LENGTH_SHORT).show();
                        ed_city.requestFocus();
                        ed_city.setError("Service not available in this city");
                    }
                }
            }
        });

    }

    //This code vreate new user/rider
    private void CreateAccount(final String name,final String phone, final String email, final String city, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        if (task.isSuccessful()) {

                            FirebaseUser user=mAuth.getCurrentUser();
                            final String userid=user.getUid();
                            HashMap<String,Object> usermap=new HashMap<>();
                            usermap.put("name",name.toLowerCase());
                            usermap.put("email",email.toLowerCase());
                            usermap.put("phone",phone.toLowerCase());
                            usermap.put("city",city.toLowerCase());
                            usermap.put("uid",userid);
                            rootRef.child(type).child(userid).updateChildren(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){
                                        loadingBar.dismiss();
                                        Toast.makeText(SignupActivity.this, type+" Registered Successfully", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(SignupActivity.this, ""+userid, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        finish();
                                    }
                                }
                            });

                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(SignupActivity.this, "This "+email+" Already Exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

      //This is only for moving register screen to login
    public void signuptologin(View view) {
        startActivity(new Intent(this,LoginActivity.class));
        finish();
    }
}
