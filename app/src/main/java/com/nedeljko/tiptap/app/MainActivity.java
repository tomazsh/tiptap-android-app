package com.nedeljko.tiptap.app;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;


public class MainActivity extends Activity {
    private EditText etCheckAmount;
    private SeekBar sbTipPercentage;
    private TextView tvCurrency;
    private TextView tvTipPercentage;
    private TextView tvTotalAmount;
    private TextView tvAmountPerPerson;
    private NumberPicker npSplitCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTipPercentage = (TextView)findViewById(R.id.tvTipPercentage);
        tvTotalAmount = (TextView)findViewById(R.id.tvTotalAmount);
        tvAmountPerPerson = (TextView)findViewById(R.id.tvAmountPerPerson);

        tvCurrency = (TextView)findViewById(R.id.tvCurrency);
        tvCurrency.setText(Currency.getInstance(Locale.getDefault()).getCurrencyCode());

        etCheckAmount = (EditText)findViewById(R.id.etCheckAmount);
        etCheckAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                onAmountsChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sbTipPercentage = (SeekBar)findViewById(R.id.sbTipPercentage);
        sbTipPercentage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                onAmountsChanged();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        String[] displayValues = new String[20];
        for (int i = 0; i < displayValues.length; i++) {
            displayValues[i] = Integer.toString(i+1);
        }

        npSplitCount = (NumberPicker)findViewById(R.id.npSplitCount);
        npSplitCount.setMinValue(1);
        npSplitCount.setMaxValue(20);
        npSplitCount.setValue(1);
        npSplitCount.setDisplayedValues(displayValues);
        npSplitCount.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i2) {
                onAmountsChanged();
            }
        });

        onAmountsChanged();
    }

    private double calculateTotalAmount(double checkAmount, int tipPercentage) {
        return checkAmount*(100.0+(double)tipPercentage)/100.0;
    }

    private double calculateAmountPerPerson(double totalAmount, int splitCount) {
        return totalAmount/(double)splitCount;
    }

    public void onAmountsChanged() {
        double checkAmount;
        if (etCheckAmount.getText().length() < 1) {
            checkAmount = 0.0;
        } else {
            checkAmount = Double.parseDouble(etCheckAmount.getText().toString());
        }
        int tipPercentage = sbTipPercentage.getProgress();
        int splitCount = npSplitCount.getValue();
        double totalAmount = calculateTotalAmount(checkAmount, tipPercentage);
        double amountPerPerson = calculateAmountPerPerson(totalAmount, splitCount);

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        numberFormat.setRoundingMode(RoundingMode.UP);
        numberFormat.setMaximumFractionDigits(2);

        tvTotalAmount.setText(numberFormat.format(totalAmount));
        tvAmountPerPerson.setText(numberFormat.format(amountPerPerson));
        tvTipPercentage.setText(String.format("%d%%", tipPercentage));
    }

}
