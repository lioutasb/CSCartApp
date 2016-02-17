package gr.plushost.prototypeapp.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.psdev.licensesdialog.LicensesDialog;
import gr.plushost.prototypeapp.R;
import gr.plushost.prototypeapp.aplications.StoreApplication;
import gr.plushost.prototypeapp.exceptionhandlers.StoreExceptionHandler;
import gr.plushost.prototypeapp.items.StoreCurrencyItem;
import gr.plushost.prototypeapp.items.StoreLanguageItem;
import gr.plushost.prototypeapp.providers.RecentSuggestionProvider;

/**
 * Created by billiout on 10/3/2015.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.settings_screen_name));

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);

            Preference languages = findPreference("language");
            List<StoreLanguageItem> languageItemList = StoreApplication.getInstance().getLanguageItemList();
            final List<StoreLanguageItem> languageItemList_temp = new ArrayList<>();
            final String[] langs = new String[languageItemList.size()];
            final String[] avail_langs = getResources().getStringArray(R.array.app_available_languages);
            String current_lang = "";
            Arrays.sort(avail_langs);
            for (int i = 0; i < languageItemList.size(); i++) {
                if (Arrays.binarySearch(avail_langs, languageItemList.get(i).getLanguage_code()) >= 0) {
                    langs[i] = languageItemList.get(i).getLanguage_name();
                    languageItemList_temp.add(languageItemList.get(i));
                }
                if (languageItemList.get(i).getLanguage_code().equals(getActivity().getSharedPreferences("ShopPrefs", 0).getString("store_language", ""))) {
                    current_lang = languageItemList.get(i).getLanguage_name();
                }
            }
            languages.setSummary(current_lang);
            languages.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getResources().getString(R.string.settings_langs_title))
                            .setItems(langs, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (!getActivity().getSharedPreferences("ShopPrefs", 0).getString("store_language", "").equals(languageItemList_temp.get(which).getLanguage_code())) {
                                        StoreApplication.updateLanguage(getActivity(), languageItemList_temp.get(which).getLanguage_code());
                                        Intent i = getActivity().getBaseContext().getPackageManager()
                                                .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        getActivity().finish();
                                        startActivity(i);
                                    }
                                }
                            }).create().show();
                    return true;
                }
            });

            Preference currencies = findPreference("currency");
            final List<StoreCurrencyItem> currencyItemList = StoreApplication.getInstance().getCurrencyItemList();
            final String[] currenc = new String[currencyItemList.size()];
            String current_curr = "";
            for (int i = 0; i < currencyItemList.size(); i++) {
                currenc[i] = currencyItemList.get(i).getDescription();
                if (currencyItemList.get(i).getCurrency_code().equals(getActivity().getSharedPreferences("ShopPrefs", 0).getString("store_currency", ""))) {
                    current_curr = currencyItemList.get(i).getDescription();
                }
            }
            currencies.setSummary(current_curr);
            currencies.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getResources().getString(R.string.settings_curr_name))
                            .setItems(currenc, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (!getActivity().getSharedPreferences("ShopPrefs", 0).getString("store_currency", "").equals(currencyItemList.get(which).getCurrency_code())) {
                                        StoreApplication.updateCurrency(getActivity(), currencyItemList.get(which).getCurrency_code());
                                        Intent i = getActivity().getBaseContext().getPackageManager()
                                                .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        getActivity().finish();
                                        startActivity(i);
                                    }
                                }
                            }).create().show();
                    return true;
                }
            });

            Preference stylePref = findPreference("about");
            String body = "";
            try {
                PackageInfo pinfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
                stylePref.setSummary(getPreferenceManager().getSharedPreferences().getString("about", pinfo.versionName));
                body += getResources().getString(R.string.settings_about_version) + ": " + getPreferenceManager().getSharedPreferences().getString("about", pinfo.versionName) + "\n";
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            body += "\n" + getResources().getString(R.string.settings_about_developed_by) + ":\nLioutas Vasileios\n(lioutasb@gmail.com)\n" + getResources().getString(R.string.settings_about_developed_for) + "\n\n© 2015 Lioutas Vasileios";

            final String f_body = body;
            stylePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.CUSTOM_IMAGE_TYPE, true)
                            .setCustomImage(R.drawable.ic_launcher)
                            .setTitleText(getResources().getString(R.string.store_name))
                            .setContentText(f_body)
                            .setConfirmText("OK")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismiss();
                                }
                            }).show();
                    return true;
                }
            });

            stylePref = findPreference("search_history");
            stylePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getActivity(), RecentSuggestionProvider.AUTHORITY, RecentSuggestionProvider.MODE);
                    suggestions.clearHistory();
                    SuperToast.create(getActivity(), getResources().getString(R.string.settings_clear_history_msg), SuperToast.Duration.SHORT, Style.getStyle(Style.BLUE, SuperToast.Animations.POPUP)).show();
                    return true;
                }
            });

            stylePref = findPreference("intro_screen");
            stylePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(getActivity(), IntroActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(R.anim.zoom_out, R.anim.zoom_in);
                    return true;
                }
            });

            stylePref = findPreference("open_libs");
            stylePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new LicensesDialog.Builder(getActivity())
                            .setNotices(R.raw.notices)
                            .setIncludeOwnLicense(true)
                            .build()
                            .showAppCompat();
                    return true;
                }
            });

            stylePref = findPreference("feedback");
            stylePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "lioutasb@gmail.com", null));
                    intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.store_name));

                    startActivity(Intent.createChooser(intent, getResources().getString(R.string.settings_feedback_item_name)));
                    return true;
                }
            });
        }
    }
}
