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
import android.view.ViewGroup;
import android.widget.NumberPicker;

public class SplitPickerDialog extends DialogFragment {
    int splitCount;
    NumberPicker npSplitCount;
    SplitPickerDialogListener mListener;

    public interface SplitPickerDialogListener {
        void onFinishSplitPickerDialog(int splitCount);
    }

    public SplitPickerDialog(int sc) {
        splitCount = sc;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_split_picker, null);
        npSplitCount = (NumberPicker)v.findViewById(R.id.npSplitCount);


        String[] values = new String[9];
        for (int i = 0; i < values.length; i++) {
            values[i] = Integer.toString(i+2);
        }
        npSplitCount.setMaxValue(values.length-1);
        npSplitCount.setMinValue(0);
        npSplitCount.setWrapSelectorWheel(false);
        npSplitCount.setDisplayedValues(values);
        npSplitCount.setValue(Math.min(Math.max(0, splitCount-2), 8));

        ContextThemeWrapper context = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Dialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Split Bill")
                .setView(v)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int count = Integer.parseInt(npSplitCount.getDisplayedValues()[npSplitCount.getValue()]);
                        mListener.onFinishSplitPickerDialog(count);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
            mListener = (SplitPickerDialogListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SplitPickerDialogListener");
        }
    }
}
