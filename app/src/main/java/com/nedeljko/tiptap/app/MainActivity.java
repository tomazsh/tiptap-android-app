package com.nedeljko.tiptap.app;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Locale;
import java.util.logging.Logger;

public class MainActivity extends Activity implements
        SplitPickerDialog.SplitPickerDialogListener,
        TipAmountDialog.TipAmountDialogListener {
    private EditText etCheckAmount;
    private TextView tvCurrency;
    private TextView tvTotalAmount;
    private TextView tvAmountPerPerson;
    private TextView tvSplitCount;
    private ToggleButton tbFirst;
    private ToggleButton tbSecond;
    private ToggleButton tbThird;
    private ToggleButton tbFourth;
    private ToggleButton tbFifth;
    private ToggleButton tbCustom;
    private ArrayList<ToggleButton> alToggleButtons;
    private int customTipValue;
    static int[] sTipValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (sTipValues == null) {
            sTipValues = new int[]{5, 10, 15, 20, 25};
        }

        tvTotalAmount = (TextView)findViewById(R.id.tvTotalAmount);
        tvAmountPerPerson = (TextView)findViewById(R.id.tvAmountPerPerson);
        tvSplitCount = (TextView)findViewById(R.id.tvSplitCount);

        tbFirst = (ToggleButton)findViewById(R.id.tbFirst);
        tbSecond = (ToggleButton)findViewById(R.id.tbSecond);
        tbThird = (ToggleButton)findViewById(R.id.tbThird);
        tbFourth = (ToggleButton)findViewById(R.id.tbFourth);
        tbFifth = (ToggleButton)findViewById(R.id.tbFifth);
        tbCustom = (ToggleButton)findViewById(R.id.tbCustom);

        alToggleButtons = new ArrayList<ToggleButton>(6);
        alToggleButtons.add(tbFirst);
        alToggleButtons.add(tbSecond);
        alToggleButtons.add(tbThird);
        alToggleButtons.add(tbFourth);
        alToggleButtons.add(tbFifth);
        alToggleButtons.add(tbCustom);

        int idx = 0;
        ListIterator<ToggleButton> li = alToggleButtons.listIterator();
        for (int i = 0; i < sTipValues.length; i++) {
            ToggleButton tb = alToggleButtons.get(i);
            String text = Integer.toString(sTipValues[i]) + "%";
            tb.setTextOff(text);
            tb.setTextOn(text);
            tb.setChecked(false);
        }

        tvCurrency = (TextView)findViewById(R.id.tvCurrency);
        tvCurrency.setText(Currency.getInstance(Locale.getDefault()).getSymbol());

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

        String[] displayValues = new String[20];
        for (int i = 0; i < displayValues.length; i++) {
            displayValues[i] = Integer.toString(i+1);
        }

        setDefaultValues();
        onAmountsChanged();
    }

    private SharedPreferences getSharedPreferences() {
        return this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
    }

    private void writeDefaults(SharedPreferences preferences, int tipButton, int customTip, int splitCount) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("tipButton", tipButton);
        editor.putInt("customTip", customTip);
        editor.putInt("splitCount", splitCount);
        editor.commit();
    }

    private int getDefaultTipButton(SharedPreferences preferences, int defaultTipButton) {
        return preferences.getInt("tipButton", defaultTipButton);
    }

    private int getDefaultCustomTip(SharedPreferences preferences, int defaultCustomTip) {
        return preferences.getInt("customTip", defaultCustomTip);
    }

    private int getDefaultSplitCount(SharedPreferences preferences, int defaultSplitCount) {
        return preferences.getInt("splitCount", defaultSplitCount);
    }

    private double calculateTotalAmount(double checkAmount, int tipPercentage) {
        return checkAmount*(100.0+(double)tipPercentage)/100.0;
    }

    private double calculateAmountPerPerson(double totalAmount, int splitCount) {
        return totalAmount/(double)splitCount;
    }

    private void setDefaultValues() {
        SharedPreferences preferences = getSharedPreferences();
        int tipButton = getDefaultTipButton(preferences, 2);
        customTipValue = getDefaultCustomTip(preferences, 30);
        int splitCount = getDefaultSplitCount(preferences, 2);

        String customTipString = Integer.toString(customTipValue) + "%";
        tbCustom.setTextOn(customTipString);
        tbCustom.setTextOff(customTipString);
        tbCustom.setChecked(false);

        tvSplitCount.setText(Integer.toString(splitCount));
        alToggleButtons.get(tipButton).setChecked(true);
    }

    private ToggleButton getCheckedTipToggleButton() {
        Iterator<ToggleButton> i = alToggleButtons.iterator();
        while (i.hasNext()) {
            ToggleButton tb = i.next();
            if (tb.isChecked()) {
                return tb;
            }
        }
        return null;
    }

    private void setCheckedTipToggleButton(ToggleButton tb) {
        Iterator<ToggleButton> i = alToggleButtons.iterator();
        while (i.hasNext()) {
            ToggleButton tbNext = i.next();
            boolean checked = tbNext.equals(tb);
            if (!checked)
                tbNext.setChecked(false);
        }
    }

    private int getTipPercentage() {
        ToggleButton tb = getCheckedTipToggleButton();
        int i = alToggleButtons.indexOf(tb);
        if (i < 5) {
            return sTipValues[i];
        } else {
            return customTipValue;
        }
    }

    public void onTipToggleButtonClicked(View v) {
        dismissKeyboard();
        ToggleButton tb = (ToggleButton)v;
        if (tb.equals(tbCustom) && !tb.isChecked()) {
            tb.setChecked(true);
            FragmentManager fm = getFragmentManager();
            TipAmountDialog tipAmountDialog = new TipAmountDialog(customTipValue);
            tipAmountDialog.show(fm, "fragment_tip_amount");
            return;
        }
        if (!tb.isChecked()) {
            tb.toggle(); // Toggle back if the button is already checked
        } else {
            setCheckedTipToggleButton(tb);
            onAmountsChanged();
        }
    }

    public void onSplitCountClicked(View v) {
        dismissKeyboard();
        FragmentManager fm = getFragmentManager();
        int splitCount = Integer.parseInt(tvSplitCount.getText().toString());
        SplitPickerDialog splitPickerDialog = new SplitPickerDialog(splitCount);
        splitPickerDialog.show(fm, "fragment_split_picker");
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etCheckAmount.getWindowToken(), 0);
    }

    @Override
    public void onFinishTipAmountDialog(int tipAmount) {
        customTipValue = tipAmount;

        String customTipString = Integer.toString(customTipValue) + "%";
        tbCustom.setTextOn(customTipString);
        tbCustom.setTextOff(customTipString);
        tbCustom.setChecked(true);

        onAmountsChanged();
    }

    @Override
    public void onFinishSplitPickerDialog(int splitCount) {
        tvSplitCount.setText(Integer.toString(splitCount));
        onAmountsChanged();
    }

    public void onAmountsChanged() {
        double checkAmount;
        if (etCheckAmount.getText().length() < 1) {
            checkAmount = 0.0;
        } else {
            checkAmount = Double.parseDouble(etCheckAmount.getText().toString());
        }
        int tipPercentage = getTipPercentage();
        int splitCount = Integer.parseInt(tvSplitCount.getText().toString());
        double totalAmount = calculateTotalAmount(checkAmount, tipPercentage);
        double amountPerPerson = calculateAmountPerPerson(totalAmount, splitCount);

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        numberFormat.setRoundingMode(RoundingMode.UP);
        numberFormat.setMaximumFractionDigits(2);

        tvTotalAmount.setText(numberFormat.format(totalAmount));
        tvAmountPerPerson.setText(numberFormat.format(amountPerPerson));

        SharedPreferences preferences = getSharedPreferences();
        writeDefaults(preferences, alToggleButtons.indexOf(getCheckedTipToggleButton()), customTipValue, splitCount);
    }

}
