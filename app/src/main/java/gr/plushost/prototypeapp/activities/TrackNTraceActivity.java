package gr.plushost.prototypeapp.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


/**
 * Created by Billys on 21/11/2014.
 */
public class TrackNTraceActivity extends AppCompatActivity {
    WebView result;
    String txt;
    EditText editText;
    ProgressBar pb;

    /*@Override
    protected void onCreate(Bundle saved){
        super.onCreate(saved);
        setContentView(R.layout.activity_trackntrace);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Αναζήτηση δέματος");

        result = (WebView) findViewById(R.id.webRes);

        result.setVisibility(View.GONE);
        result.getSettings().setDefaultFontSize(11);
        //result.getSettings().setBuiltInZoomControls(true);
        result.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // return true; // will disable all links

                // disable phone and email links
                if(url.startsWith("mailto") || url.startsWith("tel")) {
                    return true;
                }

                // leave the decision to the webview
                return true;
            }
        });

        editText = (EditText) findViewById(R.id.elta_text);

        InputFilter filter = new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {



                //return null;
                if(source.equals("")){ // for backspace
                    return source;
                }
                if(source.toString().matches("[a-zA-Z0-9]+")){
                    return source;
                }
                return "";
            }
        };

        editText.setFilters(new InputFilter[] { filter, new InputFilter.LengthFilter(13) });

        editText.clearFocus();

        pb = (ProgressBar) findViewById(R.id.pbElta);

        pb.setVisibility(View.GONE);

        return;
    }

    public void searchTrace(View v){
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        if(!editText.getText().toString().equals(""))
            new GetResultElta().execute(editText.getText().toString());
        else{
            Toast.makeText(getApplicationContext(), "Μη έγκυρη αναζήτηση", Toast.LENGTH_SHORT).show();
        }

    }

    public class GetResultElta extends AsyncTask<String, Void, Void>{

        @Override
        protected void onPreExecute(){
            result.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);

        }

        @Override
        protected Void doInBackground(String... params) {
            ServiceHandler sh = StoreApplication.getServiceHandler(act);

            String txt1 = sh.makeServiceCall("http://www.wiz.gr/index.php?voucher="+params[0]+"&dispatch[wizer.elta]=%CE%91%CE%BD%CE%B1%CE%B6%CE%AE%CF%84%CE%B7%CF%83%CE%B7&source=androidwiz&passed=json", ServiceHandler.GET);

            txt = txt1;
            return null;
        }

        @Override
        protected void onPostExecute(Void v){

            //textView.setInitialScale(1);
            //textView.getSettings().setLoadWithOverviewMode(true);
            //textView.getSettings().setUseWideViewPort(true);



            txt = txt.substring(1, txt.length()-1).trim();

            result.setWebViewClient(new WebViewClient() {

                public void onPageFinished(WebView view, String url) {
                    result.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                }
            });
            result.loadDataWithBaseURL("", StringEscapeUtils.unescapeJava(txt), "text/html", "UTF-8", "");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }*/
}
