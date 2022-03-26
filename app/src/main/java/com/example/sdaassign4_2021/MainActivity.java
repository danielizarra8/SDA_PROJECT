package com.example.sdaassign4_2021;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

/**
 * This class is the starting point of the project
 * creates a new custom intent that broadcasts a vibration to a local BroadcastReceiver<br>
 * when the user selects a button in it's associated UI.
 * @author      @author Chris Coughlan 2019
 * @author      Adapted by Rafael Izarra (rafael.izarra2@mail.dcu.ie)
 * @version     1              <current version number of program>
 * @since       2021-12-06          <the version of the package this class was first added to>
 * @since       2022-01-26          last updated
 */
public class MainActivity extends AppCompatActivity {
    public static final int BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT = 1;
    ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set the toolbar we have overridden
/*        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
*/
        viewPager = findViewById(R.id.pager);
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, getApplicationContext());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_home_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_store_24);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_baseline_settings_24);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_baseline_shopping_cart_24);


    }

}

