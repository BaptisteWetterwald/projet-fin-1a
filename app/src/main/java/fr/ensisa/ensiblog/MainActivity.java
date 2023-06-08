package fr.ensisa.ensiblog;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import fr.ensisa.ensiblog.databinding.ActivityMain2Binding;
import fr.ensisa.ensiblog.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private MaterialToolbar topAppBar;
    private ActivityMainBinding binding;
    private AppBarConfiguration mAppBarConfigurationLeft;
    private AppBarConfiguration mAppBarConfigurationRight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.nameapp)
                .build();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationViewleft = binding.leftNavView.leftNavView;
        NavigationView navigationViewright = binding.rightNavView.navRightView;
        mAppBarConfigurationLeft = new AppBarConfiguration.Builder(
                R.id.nav_home/*, R.id.nav_gallery, R.id.nav_slideshow*/)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfigurationLeft);
        NavigationUI.setupWithNavController(navigationViewleft, navController);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfigurationLeft)
                || super.onSupportNavigateUp();
    }

}