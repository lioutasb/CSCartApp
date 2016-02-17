package gr.plushost.prototypeapp.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.adapters.viewpagers.FullScreenImageAdapter;
import gr.plushost.prototypeapp.indicators.UnderlinePageIndicator;

/**
 * Created by Billiout on 10/8/2014.
 */
public class FullScreenImageActivity extends AppCompatActivity {
    ViewPager viewPager;
    UnderlinePageIndicator underlinePageIndicator;
    TextView photosRem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_image);

        viewPager = (ViewPager) findViewById(R.id.pagerFull);
        underlinePageIndicator = (UnderlinePageIndicator) findViewById(R.id.indicatorimgFull);
        underlinePageIndicator.setFades(false);
        String jsonMyObject;
        Bundle extras = getIntent().getExtras();
        jsonMyObject = extras.getString("img_urls");
        final ArrayList<String> messages = new Gson().fromJson(jsonMyObject, new TypeToken<List<String>>() {
        }.getType());
        int pos = extras.getInt("pos");

        FullScreenImageAdapter adapter = new FullScreenImageAdapter(this, messages);
        photosRem = (TextView) findViewById(R.id.photosSum);
        photosRem.setText(String.format(getResources().getString(R.string.full_image_promo_text_count_photos), pos + 1, messages.size()));

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pos);


        if (messages.size() > 1) {
            underlinePageIndicator.setViewPager(viewPager, pos);
            underlinePageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    photosRem.setText(String.format(getResources().getString(R.string.full_image_promo_text_count_photos), position + 1, messages.size()));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_fullscreen_image);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
