package com.khtech.apporter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.internal.FastSafeIterableMap;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.khtech.apporter.Fragments.HomeFragment;
import com.khtech.apporter.Models.Users;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

//This is profile screen where user can update their personal info except city

public class ProfileActivity extends AppCompatActivity {

    private EditText fullNameEditText,userPhoneEditText,addressEditText;
    private CircleImageView ProfileImageView;
    private TextView ProfileChangeTextbtn,closeTextbtn,saveTextbtn;
    Button update;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference profileRef;
    private DatabaseReference userRef;

    private Uri uri;
    private Uri myUri;
    private StorageTask storageTask;
    private String checker="";
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ProfileImageView=findViewById(R.id.profile_image);
        fullNameEditText=findViewById(R.id.change_name);
        userPhoneEditText=findViewById(R.id.change_phone);
        addressEditText=findViewById(R.id.change_address);
        ProfileChangeTextbtn=findViewById(R.id.profile_image_change_txt);
        update=findViewById(R.id.update_profile_btn);
        mAuth=FirebaseAuth.getInstance();

        profileRef= FirebaseStorage.getInstance().getReference().child("Profile Images");

        userInfoDisplay(ProfileImageView,fullNameEditText,userPhoneEditText,addressEditText);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")){
                    userInfoSaved();
                }else {
                    updateOnlyData();
                }
            }
        });

        ProfileChangeTextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="clicked";
                CropImage.activity(uri).setAspectRatio(1,1).start(ProfileActivity.this);
            }
        });

  user=mAuth.getCurrentUser();
  String userid=user.getUid();
        Toast.makeText(this, "Another "+userid, Toast.LENGTH_SHORT).show();

    }

    //Only data updated not image.
    private void updateOnlyData() {
        HashMap<String,Object> userData=new HashMap<>();
        userData.put("name",fullNameEditText.getText().toString());
        userData.put("phone",userPhoneEditText.getText().toString());
        userData.put("address",addressEditText.getText().toString());
        userRef= FirebaseDatabase.getInstance().getReference("Users");
        userRef.child(uid).updateChildren(userData);
        Toast.makeText(ProfileActivity.this, "Profile Info Update Successfully", Toast.LENGTH_SHORT).show();

    }

    private void userInfoSaved() {

        if (TextUtils.isEmpty(fullNameEditText.getText().toString())){
            Toast.makeText(ProfileActivity.this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        }
        else  if (TextUtils.isEmpty(addressEditText.getText().toString())){
            Toast.makeText(ProfileActivity.this, "Addresss is mandatory", Toast.LENGTH_SHORT).show();
        }
        else  if (TextUtils.isEmpty(userPhoneEditText.getText().toString())){
            Toast.makeText(ProfileActivity.this, "Phone number is mandatory", Toast.LENGTH_SHORT).show();
        }else  if (checker.equals("clicked")){
            uploadImage();
        }

    }

    //Update data when user select image
    private void uploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        String filePathAndName = "Profile_Images/" + "" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);

        progressDialog.setTitle("Updating");
        progressDialog.setMessage("Please Wait,While we are updating profile");
        progressDialog.setCanceledOnTouchOutside(false);

        Users users = new Users();

        if (uri != null) {
            progressDialog.show();
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    myUri = uriTask.getResult();
                    if (uriTask.isSuccessful()) {

                        HashMap<String, Object> userData = new HashMap<>();
                        userData.put("name", fullNameEditText.getText().toString());
                        userData.put("phone", userPhoneEditText.getText().toString());
                        userData.put("address", addressEditText.getText().toString());
                        userData.put("image", "" + myUri);
                        userRef = FirebaseDatabase.getInstance().getReference("Users");
                        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(userData);
                        progressDialog.dismiss();

                        Toast.makeText(ProfileActivity.this, "Profile Info Update Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ProfileActivity.this,LoginActivity.class));
                        finish();

                    } else {
                        Toast.makeText(ProfileActivity.this, "Error..try again", Toast.LENGTH_SHORT).show();
                    }

                }


            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(ProfileActivity.this, "Image not uploaded", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    //This is for user existing info display on profile
    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText,
                                 final EditText userPhoneEditText, final EditText addressEditText) {
        DatabaseReference userRef1= FirebaseDatabase.getInstance().getReference().child("Users");
        user=FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        Toast.makeText(ProfileActivity.this, "this id= "+user.getUid(), Toast.LENGTH_SHORT).show();
        userRef1.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    if (dataSnapshot.child("image").exists()){
                        String name=dataSnapshot.child("name").getValue().toString();
                        String phone=dataSnapshot.child("phone").getValue().toString();
                        if (dataSnapshot.child("address").exists()) {
                            String address = dataSnapshot.child("address").getValue().toString();
                            addressEditText.setText(address);
                        }
                        String image=dataSnapshot.child("image").getValue().toString();
                        if (!image.equals("")){
                            Picasso.get().load(image).into(profileImageView);
                        }
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
                CropImage.ActivityResult result=CropImage.getActivityResult(data);
                uri=result.getUri();
                ProfileImageView.setImageURI(uri);
            }else {
                Toast.makeText(ProfileActivity.this, "Error,try again", Toast.LENGTH_SHORT).show();
    }
    }
}