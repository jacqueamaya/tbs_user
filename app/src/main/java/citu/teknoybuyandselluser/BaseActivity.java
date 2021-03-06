package citu.teknoybuyandselluser;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 ** Created by Jacquelyn on 9/24/2015.
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private SharedPreferences mSharedPreferences;

    protected void setupUI(){
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        SimpleDraweeView imgUser = (SimpleDraweeView) findViewById(R.id.imgUser);

        mSharedPreferences = getSharedPreferences(Constants.MY_PREFS_NAME, MODE_PRIVATE);
        String mPicture = mSharedPreferences.getString(Constants.User.PICTURE, null);
        if(null == mPicture || mPicture.isEmpty() || mPicture.equals("")) {
            imgUser.setImageResource(Constants.USER_IMAGES[Constants.INDEX_USER_IMAGE]);
        } else {
            imgUser.setImageURI(Uri.parse(mPicture));
        }

        if(null == toolbar) {
            throw new RuntimeException("No toolbar found");
        }

        if(null == mDrawerLayout) {
            throw new RuntimeException("No drawer layout found");
        }

        if(null == navigationView) {
            throw new RuntimeException("No navigation view found");
        }

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, 0, 0);
        mDrawerLayout.setDrawerListener(drawerToggle);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(this);
        drawerToggle.syncState();

        TextView txtUser = (TextView) findViewById(R.id.txtUserName);
        txtUser.setText(getUserFullName());

        imgUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        Intent intent;
        intent = new Intent(this, UserProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Intent intent = null;

        if(checkItemClicked(menuItem)) {
            switch(menuItem.getItemId()) {
                case R.id.nav_notifications:
                    intent = new Intent(this, NotificationsActivity.class);
                    break;
                case R.id.nav_my_items:
                    intent = new Intent(this, MyItemsActivity.class);
                    break;
                case R.id.nav_reserved_items:
                    intent = new Intent(this, ReservedItemsActivity.class);
                    break;
                case R.id.nav_make_transactions:
                    intent = new Intent(this, MakeTransactionsActivity .class);
                    break;
                case R.id.nav_stars_collected:
                    intent = new Intent(this, StarsCollectedActivity.class);
                    break;
                case R.id.nav_logout:
                    mSharedPreferences.edit().clear().apply();
                    intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
                default:
                    intent = new Intent(this, NotificationsActivity.class);

            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }

        mDrawerLayout.closeDrawers();

        if(intent != null) {
            startActivity(intent);
            this.finish();
            overridePendingTransition(0, 0);
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    public String getUserFullName() {
        return mSharedPreferences.getString(Constants.User.FIRST_NAME, "FirstName") + " " + mSharedPreferences.getString(Constants.User.LAST_NAME, "LastName");
    }

    public String getUserName() {
        return mSharedPreferences.getString(Constants.User.USERNAME, "");
    }

    public int getUserStarsCollected() {
        return mSharedPreferences.getInt(Constants.User.STARS_COLLECTED, 0);
    }

    public String getUserPicture() {
        return mSharedPreferences.getString(Constants.User.PICTURE, "");
    }

    public abstract boolean checkItemClicked(MenuItem menuItem);
}
