package com.khtech.apporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

//This is reset password screen.when user forgot password click on login screen,comes this screen.

public class ResetPasswordActivity extends AppCompatActivity {

    Button reset;
    EditText ed_reset;
    private FirebaseAuth mAuth;

    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        reset=findViewById(R.id.btnreset);
        ed_reset=findViewById(R.id.txtresetpassword);
        mAuth=FirebaseAuth.getInstance();
        loadingBar=new ProgressDialog(this);

        //When user/rider click on reset he recieve link for reset password on email which he enter for register in apporter.
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email=ed_reset.getText().toString();
                if (TextUtils.isEmpty(Email)){
                    ed_reset.requestFocus();
                    ed_reset.setError("Empty");
                }else{
                    loadingBar.setTitle("Sending");
                    loadingBar.setMessage("Please Wait,While we are sending email.");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();
                    mAuth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                loadingBar.dismiss();
                                startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class));
                                finish();
                                Toast.makeText(ResetPasswordActivity.this, "Please Check Your Email To Reset Password", Toast.LENGTH_SHORT).show();
                            }else {
                                loadingBar.dismiss();
                                String error=task.getException().getMessage();
                                Toast.makeText(ResetPasswordActivity.this, "Error: "+error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
