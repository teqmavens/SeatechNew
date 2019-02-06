package teq.development.seatech.Utils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import teq.development.seatech.R;

/**
 * Created by vibrantappz on 4/26/2017.
 */

public class PdfActivity extends AppCompatActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdf_activity);

        WebView webview = (WebView) findViewById(R.id.webview);
        //webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setPluginState(WebSettings.PluginState.ON);

        //---you need this to prevent the webview from
        // launching another browser when a url
        // redirection occurs---
        webview.setWebViewClient(new Callback());
        //  HandyObjects.showAlert(PdfActivity.this, getIntent().getStringExtra("url"));
        String url = "http://maven.apache.org/maven-1.x/maven.pdf";
        webview.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);
        // webview.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url="+"http://vibrantappz.com/demo/frelimo/Admin/upload/1493106756Boas notícias.pdf");
    }

    private class Callback extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(
                WebView view, String url) {
            return (false);
        }
    }
}
