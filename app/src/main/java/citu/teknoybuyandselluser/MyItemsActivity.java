package citu.teknoybuyandselluser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;

import citu.teknoybuyandselluser.adapters.MyItemsAdapter;
import citu.teknoybuyandselluser.fragments.DonateFragment;
import citu.teknoybuyandselluser.fragments.PendingFragment;
import citu.teknoybuyandselluser.fragments.RentFragment;
import citu.teknoybuyandselluser.fragments.RentedFragment;
import citu.teknoybuyandselluser.fragments.SellFragment;
/**
 ** 0.01 initially created by J. Pedrano on 12/24/15
 */


public class MyItemsActivity extends BaseActivity {
    SharedPreferences prefs;
    private static final String TAG = "MyItemsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_my_items);
        setupUI();

        prefs = getSharedPreferences(Constants.MY_PREFS_NAME, Context.MODE_PRIVATE);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        MyItemsAdapter adapter = new MyItemsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean checkItemClicked(MenuItem menuItem) {
        return menuItem.getItemId() != R.id.nav_my_items;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent service = new Intent(MyItemsActivity.this, ExpirationCheckerService.class);
        service.putExtra("username", prefs.getString(Constants.USERNAME,""));
        startService(service);
    }
}