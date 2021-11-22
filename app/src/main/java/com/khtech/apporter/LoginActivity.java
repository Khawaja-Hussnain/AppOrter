package com.khtech.apporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtech.apporter.Models.Riders;

//This this login screen for for User(Buyer) and rider

public class LoginActivity extends AppCompatActivity {

    EditText ed_email,ed_password;
    TextView Rider,NotRider,ForgotPassword;
    Button Login;
    LinearLayout linearLayout;

    ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference riderRef;

    String checker="User";

    boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final String newUserName=getIntent().getStringExtra("username");

        ed_email=findViewById(R.id.loginusername);
        ed_password=findViewById(R.id.loginpassword);
        Login=findViewById(R.id.login_btn);

        Rider=findViewById(R.id.iamrider);
        NotRider=findViewById(R.id.iamnotrider);
        ForgotPassword=findViewById(R.id.forgotpassword);
        linearLayout=findViewById(R.id.linearlayout);

        loadingBar=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();

        //when user click on I am rider then this code run
        Rider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login.setText("Login Rider");
                Rider.setVisibility(View.GONE);
                NotRider.setVisibility(View.VISIBLE);
                ed_email.setText("");
                ed_password.setText("");
                checker="Rider";
                Drawable img =getResources().getDrawable(R.drawable.ic_action_rider);
                ed_email.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null);
            }
        });

         //when user click on I am not rider then this code run,by default user login perform
        NotRider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login.setText("Login");
                NotRider.setVisibility(View.GONE);
                Rider.setVisibility(View.VISIBLE);
                ed_email.setText("");
                ed_password.setText("");
                ed_email.setHint("Email");
                Drawable img =getResources().getDrawable(R.drawable.ic_action_email);
                ed_email.setCompoundDrawablesWithIntrinsicBounds(img,null,null,null);
            }
        });

        //this is redirect to fofgot password acitvity
        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));
            }
        });

        //login perform with validations checking
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=ed_email.getText().toString();
                String password=ed_password.getText().toString();

                if (TextUtils.isEmpty(email)){
                    ed_email.requestFocus();
                    ed_email.setError("Empty");

                }else if (TextUtils.isEmpty(password)){
                    ed_password.requestFocus();
                    ed_password.setError("Empty");

                }else {
                        loadingBar.setTitle("Login");
                        loadingBar.setMessage("Please Wait,While we are checking credentials.");
                        loadingBar.setCanceledOnTouchOutside(false);
                        loadingBar.show();
                        ValidateLogin(email, password);
                    }
                }
            //}
        });
        ed_email.setText(newUserName);
    }

    private void ValidateLogin(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            loadingBar.dismiss();
                            if (checker.equals("Rider")) {
                                startActivity(new Intent(LoginActivity.this, RidersMainActivity.class));
                                finish();
                            }else {
                                startActivity(new Intent(LoginActivity.this, NavigationDrawerActivity.class));
                                finish();
                            }
                        } else {
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Incorrect Email Or Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //This code run only for moving login to sign up

    public void logintosignup(View view) { startActivity(new Intent(LoginActivity.this,SignupActivity.class)); }

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
