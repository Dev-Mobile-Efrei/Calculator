package fr.android.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

public class LastResultActivity extends AppCompatActivity {

    public static final String WEB_LINK = "web_link";

    private Button searchButton;
    private AppCompatEditText textInputEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_last_result2);
        Intent intent = this.getIntent();
        String operation = intent.getStringExtra(MainActivity.LAST_OPERATION_KEY);
        String result = intent.getStringExtra(MainActivity.LAST_RESULT_KEY);

        TextView textOperation = this.findViewById(R.id.oldOperation);
        TextView textResult = this.findViewById(R.id.oldResult);

        this.searchButton =  this.findViewById(R.id.serachButton);
        this.textInputEditText =  this.findViewById(R.id.urlEdittext);

        textOperation.setText(MainActivity.lastOperation);
        textResult.setText(" = "+MainActivity.lastResult);
    }

    public void goToWebView(){
        if(this.textInputEditText.getText().equals(""))
            return;
        Intent goWeb = new Intent(this, WebViewActivity.class);
        goWeb.putExtra(WEB_LINK,this.textInputEditText.getText().toString());
        this.startActivity(goWeb);
    }

    public void onClickHandler(View view) {

        if(view.getId() == R.id.serachButton)
        {
            this.goToWebView();
        }
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
        if(item.getItemId() == R.id.inputCounter){
            Intent intent = new Intent(this, TextCounterActivity.class);
            this.startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}