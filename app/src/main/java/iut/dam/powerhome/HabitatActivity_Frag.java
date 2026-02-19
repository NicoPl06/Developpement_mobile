package iut.dam.powerhome;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.os.Bundle;
import android.view.LayoutInflater;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.habitat_activity);

        // 1. Récupérer les vues
        toolbar       = findViewById(R.id.toolbar);
        drawerLayout  = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);

        // 2. Définir la Toolbar comme ActionBar
        setSupportActionBar(toolbar);

        // 3. Créer le bouton hamburger ☰
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 4. Écouter les clics du menu
        navigationView.setNavigationItemSelectedListener(this);

        // 5. Fragment affiché au démarrage
        if (savedInstanceState == null) {
            loadFragment(new HabitatFragment());
            navigationView.setCheckedItem(R.id.nav_habitats);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;

        int id = item.getItemId();
        if      (id == R.id.nav_habitats) fragment = new HabitatFragment();
        else if (id == R.id.nav_stats)    fragment = new StatsFragment();
        else if (id == R.id.nav_settings) fragment = new SettingsFragment();

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