package fr.android.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_web_view);

        this.webView = this.findViewById(R.id.webView);
        Intent intent = this.getIntent();
        String link = intent.getStringExtra(LastResultActivity.WEB_LINK);
        System.out.println("The link is: " + link);
        this.webView.loadUrl(link);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.calculator){
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
        }
        if(item.getItemId() == R.id.lastResult){
            Intent intent = new Intent(this, LastResultActivity.class);
            this.startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}