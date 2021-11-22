package com.khtech.apporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
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

//This is search screen where user search products form their city

public class SearchProductsActivity extends AppCompatActivity {

    private RecyclerView searchRV;
    List<ProductsModel> productsList;
    ProductsModel products;
    ProductViewHolder productViewHolder;

    Toolbar toolbar;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        toolbar=findViewById(R.id.r5);
        setSupportActionBar(toolbar);
        searchRV=findViewById(R.id.search_list);
        searchRV.setLayoutManager(new LinearLayoutManager(this));
        productsList=new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();

        DatabaseReference ProductsRef= FirebaseDatabase.getInstance().getReference().child("Products");
        ProductsRef.child("Grocery").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productsList.clear();
                if (dataSnapshot.exists()){
                    for(DataSnapshot mydataSnapshot:dataSnapshot.getChildren()){
                        products=mydataSnapshot.getValue(ProductsModel.class);
                        productsList.add(products);
                        productViewHolder=new ProductViewHolder(SearchProductsActivity.this,productsList);
                        searchRV.setAdapter(productViewHolder);
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //This is linking searchview to menu item in resource folder.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search,menu);
        SearchManager searchManager=(SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView= (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search Products");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
           @Override
           public boolean onQueryTextSubmit(String query) {
               return false;
           }

           @Override
           public boolean onQueryTextChange(String newText) {
               List<ProductsModel> list = new ArrayList<>();
               for (ProductsModel productsModel : productsList) {
                   String ProductName = productsModel.getPname();
                   if (ProductName.contains(newText))
                       list.add(productsModel);
                       productViewHolder.SetSearchItems(list);

               }


               return false;
           }
       });
        return true;
    }
}