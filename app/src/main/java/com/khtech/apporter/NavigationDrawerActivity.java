package com.khtech.apporter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khtech.apporter.Fragments.HomeFragment;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;

/*This is navigation class for user(buyer) main screen after login
where he will perform products selection and check order details by clicking on product*/

public class NavigationDrawerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    DrawerLayout drawer;

    //Code For Double tap to Exit
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame, new HomeFragment()).commit();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle;
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        // DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();

        View headerView = navigationView.getHeaderView(0);
        CircleImageView imageView = headerView.findViewById(R.id.header_profile_image);
        TextView txtUsername = headerView.findViewById(R.id.header_username);
        TextView txtuserphonenumber = headerView.findViewById(R.id.header_userphone);
        userInfoDisplay(imageView, txtUsername, txtuserphonenumber);

        FloatingActionButton floatingActionButton=findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NavigationDrawerActivity.this,CartActivity.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }
    //This code validate that which option select from navigation drawer
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int itemid = item.getItemId();

        if (itemid == R.id.nav_home) {
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.frame, new HomeFragment()).commit();
        } else if (itemid == R.id.nav_profile) {
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (itemid == R.id.nav_search) {
            Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, SearchProductsActivity.class));
        } else   if (itemid == R.id.nav_cart) {
            Toast.makeText(this, "Cart", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, CartActivity.class));
        } else if (itemid == R.id.nav_orders) {
            Toast.makeText(this, "Orders", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, UserOrdersActivity.class));
        } else if (itemid == R.id.nav_logout) {
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
            FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
            if (user!=null){
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(NavigationDrawerActivity.this,LoginActivity.class));
                finish();
            }
        }
        drawer.closeDrawers();
        return false;
    }

    //For fetching current user data for header display
    private void userInfoDisplay(final CircleImageView imageView, final TextView txtUsername, final TextView txtuserphonenumber) {
        DatabaseReference userRef1 = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        uid = user.getUid();
        userRef1.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    if (dataSnapshot.child("image").exists()) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String image = dataSnapshot.child("image").getValue().toString();
                        if (!image.equals("")) {
                            Picasso.get().load(image).into(imageView);
                        }
                        txtUsername.setText(name);
                        txtuserphonenumber.setText(phone);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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