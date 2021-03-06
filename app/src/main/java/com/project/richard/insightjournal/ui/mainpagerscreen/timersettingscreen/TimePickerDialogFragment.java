package com.project.richard.insightjournal.ui.mainpagerscreen.timersettingscreen;


import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.project.richard.insightjournal.R;
import com.project.richard.insightjournal.database.LogsProvider;
import com.project.richard.insightjournal.database.PresetsColumns;
import com.project.richard.insightjournal.utils.SharedPrefUtils;
import com.project.richard.insightjournal.utils.TimerUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimePickerDialogFragment extends DialogFragment {

    private static final String TAG = TimePickerDialogFragment.class.getSimpleName();
    public static final String DURATION_FRAGMENT_TAG = "duration_fragment_tag";
    public static final String PREP_FRAGMENT_TAG = "prep_fragment_tag";

    private Unbinder unbinder;
    @BindView(R.id.spinner_second) Spinner secSpinner;
    @BindView(R.id.spinner_minute) Spinner minSpinner;
    @BindView(R.id.spinner_hour) Spinner hourSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_picker_dialog, container, false);
        unbinder = ButterKnife.bind(this, view);

        setSpinners();
        return view;
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }

    private void setSpinners(){
        ArrayAdapter<String> secAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, TimerUtils.createSecondsArrayList(getContext()));
        ArrayAdapter<String> minAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, TimerUtils.createMinutesArrayList(getContext()));
        ArrayAdapter<String> hourAdapter = new ArrayAdapter<String>(getContext(),
                R.layout.support_simple_spinner_dropdown_item, TimerUtils.createHoursArrayList(getContext()));


        Cursor cursor = getContext().getContentResolver().query(
                LogsProvider.Presets.PRESETS,
                null,
                PresetsColumns.TYPE + " = ?",
                new String[]{SharedPrefUtils.getTypePref(getContext())},
                null
        );
        Long  millis;
        cursor.moveToFirst();
        if(getTag().equals(DURATION_FRAGMENT_TAG)){
            millis = cursor.getLong(cursor.getColumnIndex(PresetsColumns.DURATION));
        }
        else{
            millis = cursor.getLong(cursor.getColumnIndex(PresetsColumns.PREPARATION_TIME));
        }
        secSpinner.setAdapter(secAdapter);
        minSpinner.setAdapter(minAdapter);
        hourSpinner.setAdapter(hourAdapter);
        secSpinner.setSelection(((int)TimerUtils.millisToSeconds(millis)));
        minSpinner.setSelection(((int)TimerUtils.millisToMinutes(millis)));
        hourSpinner.setSelection(((int)TimerUtils.millisToHours(millis)));
        cursor.close();

    }

    @OnClick(R.id.confirm_button_time_picker)
    public void confirm() {
        long second = secSpinner.getSelectedItemPosition(), minute = minSpinner.getSelectedItemPosition(),
                hour = hourSpinner.getSelectedItemPosition();
        long duration = (second + minute * 60 + hour * 3600) * 1000;
        String column = null;

        if (getTag().equals(DURATION_FRAGMENT_TAG)) {
            column = PresetsColumns.DURATION;
        } else if (getTag().equals(PREP_FRAGMENT_TAG)) {
            column = PresetsColumns.PREPARATION_TIME;
        }
        ContentValues cv = new ContentValues();
        cv.put(column, duration);
        getContext().getContentResolver().update(LogsProvider.Presets.PRESETS, cv,
                PresetsColumns.TYPE + " = ?", new String[]{SharedPrefUtils.getTypePref(getContext())});
        getDialog().dismiss();
    }

    @OnClick(R.id.cancel_button_time_picker)
    public void cancel() {
        getDialog().cancel();
    }
}
