package local.hal.st32.android.asahifeedreader45008;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * WebView表示
 */

public class ShowLinkPageActivity extends AppCompatActivity
{

    private WebView webView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_link_page);

        //戻る
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.webView);


        Intent intent = getIntent();

        String url = intent.getStringExtra("url");

        System.out.println("urlです"+url);

        webView.setWebViewClient(new WebViewClient());
        //URLを読み込み
        webView.loadUrl(url);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();

        switch (itemId)
        {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
