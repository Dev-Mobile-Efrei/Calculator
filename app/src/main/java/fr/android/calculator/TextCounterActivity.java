package fr.android.calculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class TextCounterActivity extends AppCompatActivity {
    private AppCompatEditText textInputEditText;

    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_chrono_activity);

        this.textInputEditText = this.findViewById(R.id.textToCount);

        this.textView = this.findViewById(R.id.counter);


        this.textInputEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                TextCounterActivity.this.textView.setText(String.valueOf(s.length()));
            }
        });



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
        if(item.getItemId() == R.id.inputCounter){
            Intent intent = new Intent(this, TextCounterActivity.class);
            this.startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }
}