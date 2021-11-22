package com.khtech.apporter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.khtech.apporter.Models.Riders;
import com.khtech.apporter.Models.Users;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

//This is rider profile screen where he can update personal info
public class RidersProfileActivity extends AppCompatActivity {

    private EditText fullNameEditText,riderPhoneEditText,cityEditText;
    private CircleImageView ProfileImageView;
    private TextView ProfileChangeTextbtn,closeTextbtn,saveTextbtn;
    Button update;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private StorageReference profileRef;
    private DatabaseReference riderRef;

    private Uri uri;
    private Uri myUri;
    private StorageTask storageTask;
    private String checker="";
    private String riderid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riders_profile);

        ProfileImageView=findViewById(R.id.profile_image);
        fullNameEditText=findViewById(R.id.change_name);
        riderPhoneEditText=findViewById(R.id.change_phone);
        cityEditText=findViewById(R.id.change_city);
        ProfileChangeTextbtn=findViewById(R.id.profile_image_change_txt);
        update=findViewById(R.id.update_profile_btn);
        mAuth=FirebaseAuth.getInstance();

        profileRef= FirebaseStorage.getInstance().getReference().child("Riders Profile Images");

        RidersInfoDisplay(ProfileImageView,fullNameEditText,riderPhoneEditText,cityEditText);

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")){
                    RiderInfoSaved();
                }else {
                    updateOnlyData();
                }
            }
        });

        ProfileChangeTextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker="clicked";
                CropImage.activity(uri).setAspectRatio(1,1).start(RidersProfileActivity.this);
            }
        });

        firebaseUser=mAuth.getCurrentUser();
        riderid=firebaseUser.getUid();
        Toast.makeText(this, "Another "+riderid, Toast.LENGTH_SHORT).show();
    }



    private void RiderInfoSaved() {

        if (TextUtils.isEmpty(fullNameEditText.getText().toString())){
            Toast.makeText(RidersProfileActivity.this, "Name is mandatory", Toast.LENGTH_SHORT).show();
        }
        else  if (TextUtils.isEmpty(cityEditText.getText().toString())){
            Toast.makeText(RidersProfileActivity.this, "Address is mandatory", Toast.LENGTH_SHORT).show();
        }
        else  if (TextUtils.isEmpty(cityEditText.getText().toString())){
            Toast.makeText(RidersProfileActivity.this, "Phone number is mandatory", Toast.LENGTH_SHORT).show();
        }else  if (checker.equals("clicked")){
            uploadImage();
        }

    }

    //Here personal info updated with image
    private void uploadImage() {

        final String name=fullNameEditText.getText().toString();
        final String phone=riderPhoneEditText.getText().toString();
        final String city=cityEditText.getText().toString();

        final ProgressDialog progressDialog=new ProgressDialog(RidersProfileActivity.this);
        String filePathAndName = "Profile_Images/" + "" + FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathAndName);

        progressDialog.setTitle("Updating");
        progressDialog.setMessage("Please Wait,While we are updating profile");
        progressDialog.setCanceledOnTouchOutside(false);


        Riders riders=new Riders();

        if (uri!=null){
            progressDialog.show();

            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    myUri=uriTask.getResult();

                    if (uriTask.isSuccessful()) {

                        HashMap<String,Object> riderData=new HashMap<>();
                        riderData.put("name",name.toLowerCase());
                        riderData.put("phone",phone);
                        riderData.put("city",city.toLowerCase());
                        riderData.put("image",""+myUri);
                        riderRef= FirebaseDatabase.getInstance().getReference("Riders");
                        riderRef.child(riderid).updateChildren(riderData);

                        progressDialog.dismiss();

                        Toast.makeText(RidersProfileActivity.this, "Profile Info Update Successfully"+myUri, Toast.LENGTH_SHORT).show();

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(RidersProfileActivity.this, "Error..try again", Toast.LENGTH_SHORT).show();
                    }

                }


            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                 progressDialog.dismiss();
                    Toast.makeText(RidersProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            progressDialog.dismiss();
            Toast.makeText(RidersProfileActivity.this, "Image Not Selected", Toast.LENGTH_SHORT).show();
        }
    }

    //This is rider info display function
    private void RidersInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText riderPhoneEditText, final EditText addressEditText) {

        DatabaseReference riderRef= FirebaseDatabase.getInstance().getReference().child("Riders");
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        riderid=firebaseUser.getUid();
        riderRef.child(riderid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                        String name=dataSnapshot.child("name").getValue().toString();
                        String phone=dataSnapshot.child("phone").getValue().toString();
                        if (dataSnapshot.child("city").exists()) {
                            String city = dataSnapshot.child("city").getValue().toString();
                            cityEditText.setText(city);
                        }
                    fullNameEditText.setText(name);
                    riderPhoneEditText.setText(phone);
                    if (dataSnapshot.child("image").exists()){
                        String image=dataSnapshot.child("image").getValue().toString();
                        if (!image.equals("")){
                            Picasso.get().load(image).into(profileImageView);
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //Here only data updated without image
    private void updateOnlyData() {

        String name=fullNameEditText.getText().toString();
        String phone=riderPhoneEditText.getText().toString();
        String address=cityEditText.getText().toString();

        HashMap<String,Object> riderData=new HashMap<>();
        riderData.put("name",name.toLowerCase());
        riderData.put("phone",phone);
        riderData.put("city",address.toLowerCase());

        DatabaseReference riderRef= FirebaseDatabase.getInstance().getReference("Riders");
        riderRef.child(riderid).updateChildren(riderData);
        Toast.makeText(RidersProfileActivity.this, "Profile Info Update Successfully", Toast.LENGTH_SHORT).show();

    }

    //Hre image select and crop
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            uri=result.getUri();
            ProfileImageView.setImageURI(uri);
        }else {
            Toast.makeText(RidersProfileActivity.this, "Error,try again", Toast.LENGTH_SHORT).show();
        }
    }
}