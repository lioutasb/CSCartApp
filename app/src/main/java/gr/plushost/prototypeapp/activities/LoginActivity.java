package gr.plushost.prototypeapp.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.exceptionhandlers.StoreExceptionHandler;
import gr.plushost.prototypeapp.fragments.SlidingTabsColorsFragment;

/**
 * Created by billiout on 5/3/2015.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        SlidingTabsColorsFragment fragment_tabs = new SlidingTabsColorsFragment();
        Bundle extras = new Bundle();
        extras.putInt("page", getIntent().getExtras().getInt("page"));
        fragment_tabs.setArguments(extras);
        transaction.replace(R.id.content_fragment, fragment_tabs);
        transaction.commit();
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
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_login);
    }
}
