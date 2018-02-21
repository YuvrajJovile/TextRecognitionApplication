package sd.com.validate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class InAppSearchActivity extends AppCompatActivity implements View.OnClickListener {


    private WebView web_view;
    private String searchData = null;
    private String baseUrl = "http://www.google.com/search?q=";
    private ImageButton ivBack;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_search);


        getActionBar().setHomeButtonEnabled(true);

        web_view = (WebView) findViewById(R.id.web_view);
        ivBack.setOnClickListener(this);
        web_view.setWebViewClient(new WebViewClient());
        web_view.getSettings().setSupportZoom(true);
        web_view.getSettings().setBuiltInZoomControls(true);
        web_view.getSettings().setDisplayZoomControls(true);
        web_view.clearCache(true);
        web_view.clearHistory();
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.setHorizontalScrollBarEnabled(false);
        Bundle bundle = getIntent().getExtras();
        searchData = bundle.getString("google");
        init();
    }

    private void init() {
        String searchUrl = null;

        String query = "oops 400 error";
        try {
            if (searchData != null) {
                query = URLEncoder.encode(searchData, "utf-8");
                searchUrl = baseUrl + query;
            } else {
                searchUrl = baseUrl + query;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        web_view.loadUrl(searchUrl);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){


        }
    }
}
