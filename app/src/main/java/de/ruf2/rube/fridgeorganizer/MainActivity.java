package de.ruf2.rube.fridgeorganizer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import io.realm.Realm;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements AddFridgeFragment.OnFragmentInteractionListener, FridgeFragment.OnFragmentInteractionListener,
        SearchResultFragment.OnFragmentInteractionListener, ExpiringProductsFragment.OnFragmentInteractionListener,
        SearchDetailFragment.OnFragmentInteractionListener, ScanProductFragment.OnFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private Activity mContext;

    private Realm mRealm;

    public final static String FRIDGE_KEY = "fridgekey";

    public final static String NOTIFICATION_EXTRA = "notification";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.plant(new Timber.DebugTree());
        mContext = this;
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRealm = Realm.getDefaultInstance();

        //TODO: disabled for now
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        assert fab != null;
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent addIntent = new Intent();
//                addIntent.setClass(getApplicationContext(), AddProductActivity.class);
//                startActivity(addIntent);
//            }
//        });

        mTitle = mDrawerTitle = getTitle().toString();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Utilities.hideKeyboard(mContext);
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        LinearLayout linearLayout = (LinearLayout)headerView.findViewById(R.id.nav_header);
        linearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Timber.d("onclick header");
                MainActivityFragment newFragment = new MainActivityFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                mTitle = getString(R.string.app_name);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                }
        });

        navigationView.setNavigationItemSelectedListener(this);

        Intent notifyIntent = getIntent();
        Bundle extras = notifyIntent.getExtras();
        if (extras != null && extras.getBoolean(NOTIFICATION_EXTRA)){
            ExpiringProductsFragment newFragment = new ExpiringProductsFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            mTitle = getString(R.string.title_expiring_products);
        }else {
            initFrameContainer(savedInstanceState);
        }


        // Load an ad into the AdMob banner view.
        final AdView adView = (AdView) findViewById(R.id.adView);
        final AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }


    private void initFrameContainer(Bundle savedInstanceState) {
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            MainActivityFragment firstFragment = new MainActivityFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
        }
    }


//    /* Called whenever we call invalidateOptionsMenu() */
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        // If the nav drawer is open, hide action items related to the content view
//        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
//        return super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SettingsFragment newFragment = new SettingsFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            mTitle = getString(R.string.title_settings);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void onFragmentInteraction(Uri uri){

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        View v = item.getActionView();

        if (id == R.id.nav_search_product) {
            Timber.d("navi search product");
            SearchDetailFragment newFragment = new SearchDetailFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            mTitle = getString(R.string.title_search_product);
        } else if (id == R.id.nav_expiring) {
            Timber.d("navi expiring products");
            ExpiringProductsFragment newFragment = new ExpiringProductsFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            mTitle = getString(R.string.title_expiring_products);

        } else if (id == R.id.nav_scan) {
            Timber.d("navi scan product");
            ScanProductFragment newFragment = new ScanProductFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            mTitle = getString(R.string.title_scan_product);

        } else if (id == R.id.nav_add_fridge) {
            AddFridgeFragment newFragment = new AddFridgeFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            mTitle = getString(R.string.title_add_fridge);
        } else if (id == R.id.nav_settings) {
            SettingsFragment newFragment = new SettingsFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            mTitle = getString(R.string.title_settings);
        } else if (id == R.id.nav_share) {
            Snackbar.make(v, "navigation share is not yet implemented", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        } else if (id == R.id.nav_send) {
            Snackbar.make(v, "navigation send is not yet implemented" , Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        }
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }


}
