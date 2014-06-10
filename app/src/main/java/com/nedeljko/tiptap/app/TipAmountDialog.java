package com.nedeljko.tiptap.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class TipAmountDialog extends DialogFragment {
    int tipAmount;
    SeekBar sbTipAmount;
    TextView tvTipAmount;
    TipAmountDialogListener mListener;

    public interface TipAmountDialogListener {
        void onFinishTipAmountDialog(int tipAmount);
    }

    public TipAmountDialog(int sc) {
        tipAmount = sc;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ContextThemeWrapper context = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Dialog);

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.fragment_tip_amount, null);

        tvTipAmount = (TextView)v.findViewById(R.id.tvTipAmount);
        tvTipAmount.setText(Integer.toString(tipAmount) + "%");

        sbTipAmount = (SeekBar)v.findViewById(R.id.sbTipAmount);
        sbTipAmount.setProgress(tipAmount);
        sbTipAmount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tipAmount = i;
                tvTipAmount.setText(Integer.toString(i) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.adjust_tip_amount)
                .setView(v)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onFinishTipAmountDialog(tipAmount);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (TipAmountDialogListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement TipAmountDialogListener");
        }
    }
}
