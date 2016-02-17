package gr.plushost.prototypeapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.fragments.CheckoutStepOneAddressesFragment;
import gr.plushost.prototypeapp.fragments.SlidingTabsColorsFragment;

/**
 * Created by billiout on 18/4/2015.
 */
public class CheckoutActivity extends AppCompatActivity {
    Activity act = this;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.cart_screen_name));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        CheckoutStepOneAddressesFragment fragment_tabs = new CheckoutStepOneAddressesFragment();
        Bundle extras = new Bundle();
        fragment_tabs.setArguments(extras);
        transaction.replace(R.id.content_fragment, fragment_tabs);
        transaction.commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_checkout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    public void openTerms(View view) {
        Intent browserIntent = new Intent(act, WebViewActivity.class);
        browserIntent.putExtra("title", getResources().getString(R.string.txt_btn_terms));
        browserIntent.putExtra("url", act.getResources().getString(R.string.txt_url_terms));
        browserIntent.putExtra("user_agent", "pc");
        startActivity(browserIntent);
        act.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
