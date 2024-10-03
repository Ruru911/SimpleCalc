package com.example.simplecalc;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tvExpression, tvResult;
    MaterialButton btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btn0;
    MaterialButton btnDot, btnAddition, btnSubtract, btnMultiply, btnDivide;
    MaterialButton btnC, btnAC, btnOB, btnCB, btnEqual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvResult = findViewById(R.id.tvResult);
        tvExpression = findViewById(R.id.tvExpression);

        initButton(btn0, R.id.btn0);
        initButton(btn1, R.id.btn1);
        initButton(btn2, R.id.btn2);
        initButton(btn3, R.id.btn3);
        initButton(btn4, R.id.btn4);
        initButton(btn5, R.id.btn5);
        initButton(btn6, R.id.btn6);
        initButton(btn7, R.id.btn7);
        initButton(btn8, R.id.btn8);
        initButton(btn9, R.id.btn9);
        initButton(btnDot, R.id.btnDot);
        initButton(btnAC, R.id.btnAC);
        initButton(btnC, R.id.btnC);
        initButton(btnAC, R.id.btnAC);
        initButton(btnAddition, R.id.btnAddition);
        initButton(btnDivide, R.id.btnDivide);
        initButton(btnSubtract, R.id.btnSubtract);
        initButton(btnMultiply, R.id.btnMultiply);
        initButton(btnOB, R.id.btnOpenBrackets);
        initButton(btnCB, R.id.btnCloseBrackets);
        initButton(btnEqual, R.id.btnEqual);
    }

    void initButton(MaterialButton button, int id) {
        button = findViewById(id);
        button.setOnClickListener(this::onClick);
    }

    @Override
    public void onClick(View v) {
        MaterialButton button = (MaterialButton) v;
        String btnText = button.getText().toString();
        String data = tvExpression.getText().toString();

        if (btnText.equals("AC")) {
            tvExpression.setText("");
            tvResult.setText("");
            return;
        }
        if (btnText.equals("C")) {
            if (data.length() != 0) {
                data = data.substring(0, data.length() - 1);
            }
            tvExpression.setText(data);
            return;
        }
        if (btnText.equals("=")) {
            if (isValidExpression(data)) {
                tvExpression.setText(tvResult.getText());
            } else {
                tvResult.setText("Error: Incorrect brackets");
            }
            return;
        }

        // Deletes the initial zero if a number is entered
        if (data.equals("0")) {
            if (!btnText.equals(".")) {
                data = "";
            }
        }

        // Check for duplicate operators
        if (isOperator(btnText) && data.length() > 0 && isOperator(data.substring(data.length() - 1))) {
            return;
        }

        data += btnText;
        tvExpression.setText(data);

        String finalResult = evaluateExpression(data);
        if (!finalResult.equals("Error")) {
            tvResult.setText(finalResult);
        }
        Log.i("result", evaluateExpression(data) + "");
    }

    private String evaluateExpression(String expression) {
        Context rhino = Context.enter();
        rhino.setOptimizationLevel(-1);
        try {
            Scriptable scope = rhino.initStandardObjects();
            String result = rhino.evaluateString(scope, expression, "JavaScript", 1, null).toString();

            DecimalFormat decimalFormat = new DecimalFormat("#.###");

            return decimalFormat.format(Double.parseDouble(result));
        } catch (Exception e) {
            return "Error";
        } finally {
            Context.exit();
        }
    }

    // Parenthesis pairing check
    private boolean isValidExpression(String expression) {
        int openBrackets = 0;
        for (char ch : expression.toCharArray()) {
            if (ch == '(') {
                openBrackets++;
            } else if (ch == ')') {
                openBrackets--;
                if (openBrackets < 0) {
                    return false;
                }
            }
        }
        return openBrackets == 0;
    }

    // Operator check
    private boolean isOperator(String text) {
        return text.equals("+") || text.equals("-") || text.equals("*") || text.equals("/");
    }
}