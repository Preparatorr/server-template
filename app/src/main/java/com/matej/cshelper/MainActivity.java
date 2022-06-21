package com.matej.cshelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.Navigation;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.matej.cshelper.db.DBDataManager;
import com.matej.cshelper.redmine.RedmineServices;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.menu_open, R.string.menu_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                drawerLayout.closeDrawer(GravityCompat.START);
                switch (id) {
                    case R.id.nav_home:
                        Navigation.findNavController(findViewById(R.id.fragmentContainerView)).navigate(R.id.homeFragment,new Bundle());
                        break;
                    case R.id.nav_build:
                        Navigation.findNavController(findViewById(R.id.fragmentContainerView)).navigate(R.id.buildFragment);
                        break;
                    case R.id.nav_control:
                        Navigation.findNavController(findViewById(R.id.fragmentContainerView)).navigate(R.id.controlFragment);
                        break;
                    case R.id.nav_saved:
                        Navigation.findNavController(findViewById(R.id.fragmentContainerView)).navigate(R.id.savedDrafts);
                        break;
                    case R.id.nav_orders:
                        Navigation.findNavController(findViewById(R.id.fragmentContainerView)).navigate(R.id.ordersFragment);
                        break;
                }
                return true;
            }
        });
        DBDataManager.getInstance().Init();
        RedmineServices.getInstance().Init();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void setActionBarTitle(String title){
        setTitle(title);
    }

    public static Context getContext() {
        return context;
    }
}