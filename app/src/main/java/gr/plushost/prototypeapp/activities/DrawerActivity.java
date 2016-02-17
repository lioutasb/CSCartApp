package gr.plushost.prototypeapp.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.exceptionhandlers.StoreExceptionHandler;
import gr.plushost.prototypeapp.fragments.NavigationDrawerFragment;
import gr.plushost.prototypeapp.view.BlurActionBarDrawerToggle;

/**
 * Created by billiout on 16/2/2015.
 */
public class DrawerActivity extends AppCompatActivity {

    private Activity act = this;
    protected DrawerLayout drawerLayout;
    protected BlurActionBarDrawerToggle drawerToggle;
    private boolean needToolbarIcon = false;
    private CharSequence activity_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
    }

    public void set(Toolbar toolbar, boolean needToolbarIcon, final NavigationDrawerFragment fragment) {
        this.needToolbarIcon = needToolbarIcon;
        if (needToolbarIcon) {
            drawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);
            fragment.setDrawerLayout(drawerLayout);
            drawerToggle = new BlurActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    View view = act.getCurrentFocus();
                    if (view != null) {
                        /*if(act.findViewById(R.id.nav_drawer_fragment_right) != null) {
                            if (drawerView == act.findViewById(R.id.nav_drawer_fragment_right)) {
                                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, act.findViewById(R.id.nav_drawer_fragment));
                                drawerLayout.setFocusableInTouchMode(false);
                                drawerToggle.syncState();
                                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                                //getSupportActionBar().setHomeButtonEnabled(false);
                                activity_name = getSupportActionBar().getTitle();
                                getSupportActionBar().setTitle("Φίλτρα");
                            } else if (drawerView == act.findViewById(R.id.nav_drawer_fragment)) {
                                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, act.findViewById(R.id.nav_drawer_fragment_right));
                                drawerLayout.setFocusableInTouchMode(false);
                            }
                        }*/
                        InputMethodManager inputManager = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    fragment.update();
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    View view = act.getCurrentFocus();
                    if (view != null) {
                        /*if(act.findViewById(R.id.nav_drawer_fragment_right) != null) {
                            if (drawerView == act.findViewById(R.id.nav_drawer_fragment_right)) {
                                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, act.findViewById(R.id.nav_drawer_fragment));
                                drawerLayout.setFocusableInTouchMode(false);
                                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                                drawerToggle.syncState();
                                //getSupportActionBar().setHomeButtonEnabled(true);
                                getSupportActionBar().setTitle(activity_name);
                            } else if (drawerView == act.findViewById(R.id.nav_drawer_fragment)) {
                                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, act.findViewById(R.id.nav_drawer_fragment_right));
                                drawerLayout.setFocusableInTouchMode(false);
                            }
                        }*/
                        InputMethodManager inputManager = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    fragment.update();
                }

                /*@Override
                public void onDrawerSlide (View drawerView, float slideOffset){
                    super.onDrawerSlide(drawerView,slideOffset);
                    /if(act.findViewById(R.id.nav_drawer_fragment_right) != null){
                        if(drawerView == act.findViewById(R.id.nav_drawer_fragment_right)){
                            drawerToggle.syncState();
                        }
                    }
                }*/

            };
            drawerLayout.setDrawerListener(drawerToggle);
            drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            /*if(act.findViewById(R.id.nav_drawer_fragment_right) != null) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, act.findViewById(R.id.nav_drawer_fragment_right));
                drawerLayout.setDrawerShadow(R.drawable.drawer_shadow_right, GravityCompat.END);
                drawerLayout.setFocusableInTouchMode(false);
            }*/
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);

        } else {
            drawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);
            //drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
            //drawerLayout.setDrawerListener(drawerToggle);
            drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportActionBar().setHomeButtonEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (needToolbarIcon)
            return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
        else
            return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (needToolbarIcon) {
            drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (needToolbarIcon) {
            drawerToggle.onConfigurationChanged(newConfig);
        }
    }
}
