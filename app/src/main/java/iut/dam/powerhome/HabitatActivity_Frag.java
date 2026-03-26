package iut.dam.powerhome;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

public class HabitatActivity_Frag extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences appPrefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        float scale = appPrefs.getFloat("fontScale", 1.0f);
        Configuration config = getResources().getConfiguration();
        config.fontScale = scale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.habitat_activity);

        toolbar        = findViewById(R.id.toolbar);
        drawerLayout   = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        SharedPreferences sessionPrefs = getSharedPreferences("SESSIONS", MODE_PRIVATE);
        int userId = sessionPrefs.getInt("user_id", -1);

        applyAccentColor(ColorManager.getColor(this, userId));

        if (savedInstanceState == null) {
            loadFragment(new HabitatFragment());
            navigationView.setCheckedItem(R.id.nav_habitats);
        }
    }

    public void applyAccentColor(int color) {
        if (toolbar != null) {
            toolbar.setBackgroundColor(color);
        }
        android.view.View header = navigationView.getHeaderView(0);
        if (header != null) {
            header.setBackgroundColor(color);
        }
        getWindow().setStatusBarColor(color);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        int id = item.getItemId();

        if      (id == R.id.nav_habitats)     fragment = new HabitatFragment();
        else if (id == R.id.nav_home)         fragment = new HomeFragment();
        else if (id == R.id.nav_requests)     fragment = new RequestFragment();
        else if (id == R.id.nav_settings)     fragment = new SettingsFragment();
        else if (id == R.id.nav_Log_out)      fragment = new LogOutFragment();
        else if (id == R.id.nav_calendar)     fragment = new CalendarFragment();
        else if (id == R.id.nav_my_bookings)  fragment = new MyBookingsFragment();

        if (fragment != null) loadFragment(fragment);
        drawerLayout.closeDrawers();
        return true;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}