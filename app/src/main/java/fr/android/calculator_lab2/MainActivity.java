package fr.android.calculator_lab2;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
            String lastValue = "";

            TextView editTextOperation = findViewById(R.id.editTextOperation);
            TextView editTextResult = findViewById(R.id.editTextResult);
            Button btnEquals = findViewById(R.id.btnEquals);

            if(operationScreenValue.length() > 0)
            {
                lastValue = String.valueOf(operationScreenValue.charAt(operationScreenValue.length() - 1));
            }

            String equalValue = getResources().getString(R.string.textBtnEquals);
            if(text.equals(equalValue))
            {
                boolean isInvalid = isOperand(lastValue) || this.operationScreenValue.isEmpty();
                if(isInvalid)
                {
                    return;
                }

                Context context = Context.enter(); //
                context.setOptimizationLevel(-1); // this is required[2]
                Scriptable scope = context.initStandardObjects();
                Object result = context.evaluateString(scope, operationScreenValue, "<cmd>", 1, null);
                resultScreenValue = result.toString();
                operationScreenValue = "";
            } else {

                boolean isOperand = isOperand(text);

                if(isOperand)
                {
                    boolean lastIsOperand = isOperand(lastValue);

                    boolean operandIsTheFirstValue = operationScreenValue.isEmpty();

                    if(lastIsOperand)
                    {
                        operationScreenValue = operationScreenValue.substring(0, operationScreenValue.length() - 1);

                    }

                    if(operandIsTheFirstValue)
                    {
                        return;
                    }
                }

                operationScreenValue += text;
            }

            if (resultScreenValue.contains("Infinity"))
            {
                resultScreenValue = "";
                editTextResult.setBackground(getResources().getDrawable(R.drawable.textview_redborder, null));
                editTextResult.setText(R.string.string_error);
                return;
            }

            if(operationScreenValue.length() > 0)
            {
                lastValue = String.valueOf(operationScreenValue.charAt(operationScreenValue.length() - 1));
            }

            btnEquals.setEnabled(!operationScreenValue.isEmpty() && !isOperand(lastValue));

            editTextResult.setBackground(getResources().getDrawable(R.drawable.textview_purpleborder, null));
            editTextOperation.setText(operationScreenValue);
            editTextResult.setText(resultScreenValue);
        }
    }

    public boolean isOperand(String value)
    {
        String[] operands = {getResources().getString(R.string.textBtnDivide)
                , getResources().getString(R.string.textBtnMinus)
                , getResources().getString(R.string.textBtnPlus)
                , getResources().getString(R.string.textBtnMultiply)};

        return Arrays.asList(operands).contains(value);
    }
}