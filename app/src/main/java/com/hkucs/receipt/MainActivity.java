package com.hkucs.receipt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Notification;
import android.content.ClipData;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    DatabaseHelper db;
    Button add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        add = findViewById(R.id.new_add_record);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText new_name = (EditText) findViewById(R.id.new_name);
                String name = new_name.getText().toString();
                EditText new_date = (EditText) findViewById(R.id.new_purchase_date);
                System.out.println(new_date);
                String date = new_date.getText().toString();
                EditText new_price = (EditText) findViewById(R.id.new_price);
                String price = new_price.getText().toString();
                EditText new_warranty = (EditText) findViewById(R.id.new_warranty_period);
                String warranty = new_warranty.getText().toString();
                AutoCompleteTextView new_category = (AutoCompleteTextView)findViewById(R.id.new_category);
                String category = new_category.getText().toString();
                Spinner new_spinner = (Spinner)findViewById(R.id.spinner1);
                String spinner = new_spinner.getSelectedItem().toString();
                ImageView new_imageView = (ImageView)findViewById(R.id.imageView);
                if (new_imageView.getDrawable()!=null && name!=null && date!=null && price!=null && warranty!=null && category!=null){
                    Bitmap bitmap = ((BitmapDrawable) new_imageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] Image = baos.toByteArray();try{
                        baos.close();
                        boolean x = db.insertData(name,date,price,Image,warranty+' '+spinner, category);
                        if (!x){
                            Toast.makeText(MainActivity.this, "False", Toast.LENGTH_SHORT).show();
                        }
                        add.setVisibility(View.INVISIBLE);
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_left,R.anim.exit_to_right,R.anim.enter_from_right,R.anim.exit_to_left).addToBackStack(null).replace(R.id.fragment_container, new DashboardFragment()).commit();
                        Toast.makeText(MainActivity.this, "Record Successfully Added!", Toast.LENGTH_SHORT).show();
                    }catch (Exception e ){
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();

                    }

                }


            }
        });
        add.setVisibility(View.INVISIBLE);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            add.setVisibility(View.INVISIBLE);
            getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, new DashboardFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_dashboard);
        }

    }

    public void add_vis(){
        add.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                add.setVisibility(View.INVISIBLE);
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, new DashboardFragment()).commit();
                break;
            case R.id.nav_record:
                add.setVisibility(View.INVISIBLE);
                getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(0,0,R.anim.enter_from_left,R.anim.exit_to_right).replace(R.id.fragment_container, new RecordFragment()).commit();
                break;
            case R.id.nav_settings:
                add.setVisibility(View.INVISIBLE);
                getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(0,0,R.anim.enter_from_left,R.anim.exit_to_right).replace(R.id.fragment_container, new SettingsFragment()).commit();
                break;
            case R.id.nav_add:
                add.setVisibility(View.VISIBLE);
                getSupportFragmentManager().beginTransaction().addToBackStack(null).setCustomAnimations(0,0,R.anim.enter_from_left,R.anim.exit_to_right).replace(R.id.fragment_container, new NewFragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();

            if (count == 1) {
                System.exit(0);
            } else {
                getSupportFragmentManager().popBackStack();
                add.setVisibility(View.INVISIBLE);
            }
        }
    }
}