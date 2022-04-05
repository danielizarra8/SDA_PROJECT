package com.example.project_sda_2022;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.project_sda_2022.R;
import com.google.android.material.tabs.TabLayout;
import com.stripe.android.PaymentConfiguration;

/**
 * This class is the starting point of the project
 * creates a new custom intent that broadcasts a vibration to a local BroadcastReceiver<br>
 * when the user selects a button in it's associated UI.
 * @author  Rafael Izarra (rafael.izarra2@mail.dcu.ie)
 * @version 1.0
 * @since 30/03/2022
 */
public class MainActivity extends AppCompatActivity {
   public static final int BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT = 1;
    ViewPager viewPager;
    ImageView settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_live_51Kj6nfJhLsHwML0vh7tmG4JpsS5GKt77aueWiU4RWHAFK7WJ5Rih2ppPoOqGoqc5K92Nq7r0NmOqO3EtB3M3rnrG00O0SrmpGB"
        );

        settings = findViewById(R.id.iconSetting);
        viewPager = findViewById(R.id.pager);
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, getApplicationContext());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_baseline_home_24);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_baseline_store_24);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_baseline_shopping_cart_24);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Settings.class));
            }
        });
    }

}

