package fr.android.calculator_lab2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public String operationScreenValue = "";

    public String resultScreenValue = "";

    public void onClick(View view) {
        if(view instanceof Button)
        {
            Button viewAsButton = (Button) view;
            String text = viewAsButton.getText().toString();

            String equalValue = getResources().getString(R.string.textBtnEquals);
            if(text.equals(equalValue))
            {
                Context context = Context.enter(); //
                context.setOptimizationLevel(-1); // this is required[2]
                Scriptable scope = context.initStandardObjects();
                Object result = context.evaluateString(scope, operationScreenValue, "<cmd>", 1, null);
                resultScreenValue = result.toString();
            }else{
                String[] operands = {getResources().getString(R.string.textBtnDivide)
                        , getResources().getString(R.string.textBtnMinus)
                        , getResources().getString(R.string.textBtnPlus)
                        , getResources().getString(R.string.textBtnMultiply)};

                boolean isOperand = Arrays.asList(operands).contains(text);

                if(isOperand)
                {
                    boolean lastIsOperand = Arrays.asList(operands).contains(
                            String.valueOf(operationScreenValue.charAt(operationScreenValue.length() - 1)));

                    if(lastIsOperand)
                    {
                        return;
                    }
                }

                operationScreenValue += text;
            }

        }

    }
}