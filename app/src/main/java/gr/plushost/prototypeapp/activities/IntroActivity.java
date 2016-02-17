package gr.plushost.prototypeapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import gr.plushost.prototypeapp.R;

/**
 * Created by billiout on 5/8/2015.
 */
public class IntroActivity extends AppIntro {
    @Override
    public void init(Bundle bundle) {
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.page_1_title, getResources().getString(R.string.store_name)), getResources().getString(R.string.page_1_description), R.drawable.page_1, getResources().getColor(R.color.color_1)));
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.page_2_title), getResources().getString(R.string.page_2_description), R.drawable.page_2, getResources().getColor(R.color.color_2)));
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.page_3_title), getResources().getString(R.string.page_3_description), R.drawable.page_3, getResources().getColor(R.color.color_3)));
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.page_4_title), getResources().getString(R.string.page_4_description), R.drawable.page_4, getResources().getColor(R.color.color_4)));
        addSlide(AppIntroFragment.newInstance(getResources().getString(R.string.page_5_title), getResources().getString(R.string.page_5_description), R.drawable.page_5, getResources().getColor(R.color.color_5)));

        showSkipButton(false);

        setCustomTransformer(new FadePageTransformer());

        setVibrate(true);
        setVibrateIntensity(45);
        setProgressIndicator();
        setDoneText(getResources().getString(R.string.btn_done_txt));
    }

    @Override
    public void onSkipPressed() {

    }

    @Override
    public void onDonePressed() {
        if(getSharedPreferences("ShopPrefs", 0).getBoolean("first_time", true)) {
            getSharedPreferences("ShopPrefs", 0).edit().putBoolean("first_time", false).apply();
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            overridePendingTransition(R.anim.zoom_out, R.anim.zoom_in);
            finish();
        }
        else{
            finish();
        }
    }

    public class FadePageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {
            view.setTranslationX(view.getWidth() * -position);
            if(position <= -1.0F || position >= 1.0F) {
                view.setAlpha(0.0F);
            } else if( position == 0.0F ) {
                view.setAlpha(1.0F);
            } else {
                 // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                view.setAlpha(1.0F - Math.abs(position));
            }
        }
    }
}
