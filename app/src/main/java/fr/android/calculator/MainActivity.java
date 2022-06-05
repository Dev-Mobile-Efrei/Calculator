package fr.android.calculator;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.jetbrains.annotations.NotNull;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Arrays;

@SuppressWarnings("ClassIndependentOfModule")
public class MainActivity extends AppCompatActivity {

    protected final DecimalFormat resultFormat = new DecimalFormat("##.##");

    protected String operation = "";

    protected String resultStr = "";

    private Button btnEquals = null;

    private Handler handler;

    public boolean isOperand(String value) {
        String[] operands = { this.getResources().getString(R.string.textBtnDivide),
                              this.getResources().getString(R.string.textBtnMinus),
                              this.getResources().getString(R.string.textBtnPlus),
                              this.getResources().getString(R.string.textBtnMultiply) };

        return Arrays.asList(operands).contains(value);
    }

    public void onClick(View view) {
        // this.onClickProcedural(view);
        this.onClickHandler(view);
        //this.onClickAsync(view);
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
    protected double evaluateOperation(@NotNull String operationToEvaluate) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        this.handler = new Handler();

        LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 4);
        layoutParam.setMargins(5, 12, 5, 12);

        this.btnEquals = new Button(this);
        this.btnEquals.setText(R.string.textBtnEquals);
        this.btnEquals.setTextAppearance(R.style.TextAppearance_AppCompat_Display1);
        this.btnEquals.setOnClickListener(this::onClick);
        this.btnEquals.setBackgroundColor(this.getResources().getColor(R.color.gray, null));
        this.btnEquals.setTextColor(this.getResources().getColor(R.color.black, null));
        this.btnEquals.setEnabled(false);

        LinearLayout linearLayout = this.findViewById(R.id.layoutButtons);
        linearLayout.addView(this.btnEquals, layoutParam);
    }

    void setEnabledBtnEquals(boolean enabled) {
        if (enabled) {
            this.btnEquals.setEnabled(true);
            this.btnEquals.setBackgroundColor(this.getResources().getColor(R.color.purple_200, null));
        } else {
            this.btnEquals.setEnabled(false);
            this.btnEquals.setBackgroundColor(this.getResources().getColor(R.color.gray, null));
        }
    }

    private void onClickAsync(View view) {
        ActivityTask task = new ActivityTask(view);
        task.execute();
    }

    private void onClickHandler(View view) {
        if (view instanceof Button) {
            Button viewAsButton = (Button) view;
            String text = viewAsButton.getText().toString();
            String lastValue = this.operation.isEmpty()
                               ? ""
                               : this.operation.substring(this.operation.length() - 1);

            TextView editTextOperation = this.findViewById(R.id.editTextOperation);
            TextView editTextResult = this.findViewById(R.id.editTextResult);

            String equalValue = this.getResources().getString(R.string.textBtnEquals);
            if (text.equals(equalValue)) {
                this.btnEquals.setEnabled(false);
                double[] result = new double[1];

                String operand = this.operation.replaceAll("[0-9]", "");
                System.out.println(this.operation + " and " + operand);
                char operandAtChar = operand.charAt(0);

                String[] values = this.operation.split("[^0-9]");

                double leftValue = Double.parseDouble(values[0]);
                double rightValue = Double.parseDouble(values[1]);


                this.operation.split("[^0-9]");

                Thread thread = new Thread(() -> {
                    try {
                        Socket socket = new Socket(this.getResources().getString(R.string.ipAddress), this.getResources().getInteger(R.integer.port));
                        DataOutputStream outputStream;
                        DataInputStream inputStream;
                        outputStream = new DataOutputStream(socket.getOutputStream());
                        inputStream = new DataInputStream(socket.getInputStream());

                        outputStream.writeDouble(leftValue);
                        outputStream.writeChar(operandAtChar);
                        outputStream.writeDouble(rightValue);
                        outputStream.flush();

                        result[0] = inputStream.readDouble();

                        inputStream.close();
                        outputStream.close();
                        socket.close();
                    } catch (IOException ignored) {
                        System.err.println(ignored);
                    }

                    try {
                        System.out.println(Arrays.toString(result));
                        this.handler.post(() -> this.resultStr = this.resultFormat.format(result[0]));
                        this.handler.post(() -> this.operation = "");
                    } catch (IllegalArgumentException | ArithmeticException ignored) {
                        this.handler.post(() -> this.resultStr = "");
                        this.handler.post(() -> editTextResult.setBackground(this.getResources().getDrawable(R.drawable.textview_red_border, null)));
                        this.handler.post(() -> editTextResult.setText(R.string.string_error));
                    }

                    this.handler.post(() -> this.setEnabledBtnEquals(!this.operation.isEmpty() && !this.isOperand(text)));

                    this.handler.post(() -> editTextResult.setBackground(this.getResources()
                                                                             .getDrawable(R.drawable.textview_purple_border, null)));
                    this.handler.post(() -> editTextOperation.setText(this.operation));
                    this.handler.post(() -> editTextResult.setText(this.resultStr));
                });
                thread.start();

                return;
            } else {
                boolean isOperand = this.isOperand(text);

                if (isOperand) {
                    boolean lastIsOperand = this.isOperand(lastValue);

                    boolean operandIsTheFirstValue = this.operation.isEmpty();

                    if (lastIsOperand) {
                        this.handler.post(() -> this.operation = this.operation.substring(0,
                                                                                          this.operation.length() - 1));
                    }

                    if (operandIsTheFirstValue) {
                        return;
                    }
                }

                this.handler.post(() -> this.operation += text);
            }

            this.handler.post(() -> this.setEnabledBtnEquals(!this.operation.isEmpty() && !this.isOperand(text)));

            this.handler.post(() -> editTextResult.setBackground(this.getResources()
                                                                     .getDrawable(R.drawable.textview_purple_border, null)));
            this.handler.post(() -> editTextOperation.setText(this.operation));
            this.handler.post(() -> editTextResult.setText(this.resultStr));
        }
    }

    private void onClickProcedural(View view) {
        if (view instanceof Button) {
            Button viewAsButton = (Button) view;
            String text = viewAsButton.getText().toString();
            String lastValue = this.operation.isEmpty()
                               ? ""
                               : this.operation.substring(this.operation.length() - 1);

            TextView editTextOperation = this.findViewById(R.id.editTextOperation);
            TextView editTextResult = this.findViewById(R.id.editTextResult);

            String equalValue = this.getResources().getString(R.string.textBtnEquals);
            if (text.equals(equalValue)) {

                this.btnEquals.setEnabled(false);
                double[] result = new double[1];

                String operand = this.operation.replaceAll("[0-9]", "");
                System.out.println(this.operation + " and " + operand);
                char operandAtChar = operand.charAt(0);

                String[] values = this.operation.split("[^0-9]");

                double leftValue = Double.parseDouble(values[0]);
                double rightValue = Double.parseDouble(values[1]);


                this.operation.split("[^0-9]");

                Thread thread = new Thread(() -> {
                    try {
                        Socket socket = new Socket("10.0.2.2", 9876);
                        DataOutputStream outputStream;
                        DataInputStream inputStream;
                        outputStream = new DataOutputStream(socket.getOutputStream());
                        inputStream = new DataInputStream(socket.getInputStream());

                        outputStream.writeDouble(leftValue);
                        outputStream.writeChar(operandAtChar);
                        outputStream.writeDouble(rightValue);
                        outputStream.flush();

                        result[0] = inputStream.readDouble();

                        inputStream.close();
                        outputStream.close();
                        socket.close();

                        this.resultStr = this.resultFormat.format(result[0]);
                        this.operation = "";
                    } catch (IOException ignored) {
                        System.err.println(ignored);
                    } catch (IllegalArgumentException | ArithmeticException ignored) {
                        this.resultStr = "";
                        editTextResult.setBackground(this.getResources().getDrawable(R.drawable.textview_red_border, null));
                        editTextResult.setText(R.string.string_error);
                    }
                });

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

            this.setEnabledBtnEquals(!this.operation.isEmpty() && !this.isOperand(text));

            editTextResult.setBackground(this.getResources().getDrawable(R.drawable.textview_purple_border, null));
            editTextOperation.setText(this.operation);
            editTextResult.setText(this.resultStr);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ActivityTask extends AsyncTask<Void, Void, String> {

        private final View view;

        private boolean error = false;

        ActivityTask(View view) {
            this.view = view;
        }

        @Override
        protected @NotNull String doInBackground(Void... voids) {
            this.error = false;

            if (this.view instanceof Button) {
                Button viewAsButton = (Button) this.view;
                String text = viewAsButton.getText().toString();
                String lastValue = MainActivity.this.operation.isEmpty()
                                   ? ""
                                   : MainActivity.this.operation.substring(MainActivity.this.operation.length() - 1);

                String equalValue = MainActivity.this.getResources().getString(R.string.textBtnEquals);
                if (text.equals(equalValue)) {
                    try {
                        double result = MainActivity.this.evaluateOperation(MainActivity.this.operation);

                        MainActivity.this.resultStr = MainActivity.this.resultFormat.format(result);
                        MainActivity.this.operation = "";
                    } catch (IllegalArgumentException | ArithmeticException ignored) {
                        this.error = true;
                        return "";
                    }
                } else {
                    boolean isOperand = MainActivity.this.isOperand(text);

                    if (isOperand) {
                        boolean lastIsOperand = MainActivity.this.isOperand(lastValue);

                        boolean operandIsTheFirstValue = MainActivity.this.operation.isEmpty();

                        if (lastIsOperand) {
                            MainActivity.this.operation = MainActivity.this.operation.substring(0,
                                                                                                MainActivity.this.operation.length() -
                                                                                                1);
                        }

                        if (operandIsTheFirstValue) {
                            return "";
                        }
                    }

                    MainActivity.this.operation += text;
                }

                return text;
            }

            return "";
        }

        @Override
        protected void onPostExecute(String text) {
            TextView editTextOperation = MainActivity.this.findViewById(R.id.editTextOperation);
            TextView editTextResult = MainActivity.this.findViewById(R.id.editTextResult);

            if (this.error) {
                MainActivity.this.resultStr = "";
                editTextResult.setBackground(MainActivity.this.getResources()
                                                              .getDrawable(R.drawable.textview_red_border, null));
                editTextResult.setText(R.string.string_error);
            } else {
                MainActivity.this.setEnabledBtnEquals(
                        !MainActivity.this.operation.isEmpty() && !MainActivity.this.isOperand(text));

                editTextResult.setBackground(MainActivity.this.getResources()
                                                              .getDrawable(R.drawable.textview_purple_border, null));
                editTextOperation.setText(MainActivity.this.operation);
                editTextResult.setText(MainActivity.this.resultStr);
            }
        }
    }
}