package fr.android.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.jetbrains.annotations.NotNull;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.text.DecimalFormat;
import java.util.Arrays;

@SuppressWarnings("ClassIndependentOfModule")
public class MainActivity extends AppCompatActivity {

    private final DecimalFormat resultFormat = new DecimalFormat("##.##");

    public String operation = "";

    public String resultStr = "";

    public boolean isOperand(String value) {
        String[] operands = { this.getResources().getString(R.string.textBtnDivide),
                              this.getResources().getString(R.string.textBtnMinus),
                              this.getResources().getString(R.string.textBtnPlus),
                              this.getResources().getString(R.string.textBtnMultiply) };

        return Arrays.asList(operands).contains(value);
    }

    public void onClick(View view) {
        if (view instanceof Button) {
            Button viewAsButton = (Button) view;
            String text = viewAsButton.getText().toString();
            String lastValue = this.operation.isEmpty()
                               ? ""
                               : this.operation.substring(this.operation.length() - 1);

            TextView editTextOperation = this.findViewById(R.id.editTextOperation);
            TextView editTextResult = this.findViewById(R.id.editTextResult);
            Button btnEquals = this.findViewById(R.id.btnEquals);

            String equalValue = this.getResources().getString(R.string.textBtnEquals);
            if (text.equals(equalValue)) {
                try {
                    double result = this.evaluateOperation(this.operation);

                    this.resultStr = this.resultFormat.format(result);
                    this.operation = "";
                } catch (IllegalArgumentException | ArithmeticException ignored) {
                    this.resultStr = "";
                    editTextResult.setBackground(this.getResources().getDrawable(R.drawable.textview_red_border, null));
                    editTextResult.setText(R.string.string_error);

                    return;
                }
            } else {
                boolean isOperand = this.isOperand(text);

                if (isOperand) {
                    boolean lastIsOperand = this.isOperand(lastValue);

                    boolean operandIsTheFirstValue = this.operation.isEmpty();

                    if (lastIsOperand) {
                        this.operation = this.operation.substring(0, this.operation.length() - 1);
                    }

                    if (operandIsTheFirstValue) {
                        return;
                    }
                }

                this.operation += text;
            }

            btnEquals.setEnabled(!this.operation.isEmpty() && !this.isOperand(text));

            editTextResult.setBackground(this.getResources().getDrawable(R.drawable.textview_purple_border, null));
            editTextOperation.setText(this.operation);
            editTextResult.setText(this.resultStr);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
    }

    /**
     * @param operationToEvaluate operation to be evaluated
     *
     * @return result of the operation
     *
     * @throws IllegalArgumentException throw if operation is empty or ends in an operation
     * @throws ArithmeticException throw if the operation is invalid
     * @throws NumberFormatException throw if the result can't be parsed to a double
     */
    private double evaluateOperation(@NotNull String operationToEvaluate) {
        String lastValue = operationToEvaluate.isEmpty()
                           ? ""
                           : operationToEvaluate.substring(operationToEvaluate.length() - 1);

        if (this.isOperand(lastValue) || operationToEvaluate.isEmpty()) {
            throw new IllegalArgumentException("Operation is invalid");
        }

        Context context = Context.enter();
        context.setOptimizationLevel(-1);
        Scriptable scope = context.initStandardObjects();
        String result = context.evaluateString(scope, operationToEvaluate, "<cmd>", 1, null).toString();

        if (result.contains("Infinity")) {
            throw new ArithmeticException("Unable to divide by zero");
        } else {
            return Double.parseDouble(result);
        }
    }
}