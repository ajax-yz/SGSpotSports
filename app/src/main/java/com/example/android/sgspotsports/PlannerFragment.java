package com.example.android.sgspotsports;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class PlannerFragment extends DialogFragment implements View.OnClickListener {

    private View view;
    private Button btnDatePicker, btnStartTimePicker, btnEndTimePicker, btnSaveToCalendar;
    private EditText txtDate, txtStartTime, txtEndTime, mEditActivityTitle, mEditActivityInfo;

    private int mYear, mMonth, mDay, mStartHour, mStartMinute, mEndHour, mEndMinute;

    private Calendar beginTime, endTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_planner, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnDatePicker = (Button) view.findViewById(R.id.btn_date);
        btnStartTimePicker = (Button) view.findViewById(R.id.btn_start_time);
        btnEndTimePicker = (Button) view.findViewById(R.id.btn_end_time);
        btnSaveToCalendar = (Button) view.findViewById(R.id.save_date_time);
        txtDate = (EditText) view.findViewById(R.id.in_date);
        txtStartTime = (EditText) view.findViewById(R.id.in_start_time);
        txtEndTime = (EditText) view.findViewById(R.id.in_end_time);
        mEditActivityTitle = (EditText) view.findViewById(R.id.edit_activity_title);
        mEditActivityInfo = (EditText) view.findViewById(R.id.edit_activity_information);

        // So that the users cannot manually key in any input
        disableEditText(txtDate);
        disableEditText(txtStartTime);
        disableEditText(txtEndTime);

        btnDatePicker.setOnClickListener(this);
        btnStartTimePicker.setOnClickListener(this);
        btnEndTimePicker.setOnClickListener(this);
        btnSaveToCalendar.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_date:

                // Get Current Date
                final Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                //txtDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                                PlannerFragment.this.mYear = year;
                                PlannerFragment.this.mMonth = monthOfYear;
                                PlannerFragment.this.mDay = dayOfMonth;

                                // To extract the string
                                Calendar c = Calendar.getInstance();
                                c.set(Calendar.YEAR, year);
                                c.set(Calendar.MONTH, monthOfYear);
                                c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                String currentDateString = DateFormat.getDateInstance(DateFormat.LONG).format(c.getTime());
                                txtDate.setText(currentDateString);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

                break;

            case R.id.btn_start_time:

                // Get Current Time
                Calendar c = Calendar.getInstance();
                mStartHour = c.get(Calendar.HOUR_OF_DAY);
                mStartMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                txtStartTime.setText(hourOfDay + ":" + minute);

                                PlannerFragment.this.mStartHour = hourOfDay;
                                PlannerFragment.this.mStartMinute = minute;
                            }
                        }, mStartHour, mStartMinute, false);
                timePickerDialog.show();

                break;

            case R.id.btn_end_time:

                // Get Current Time
                c = Calendar.getInstance();
                mEndHour = c.get(Calendar.HOUR_OF_DAY);
                mEndMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                txtEndTime.setText(hourOfDay + ":" + minute);

                                PlannerFragment.this.mEndHour = hourOfDay;
                                PlannerFragment.this.mEndMinute = minute;
                            }
                        }, mEndHour, mEndMinute, false);
                timePickerDialog.show();

                break;

            case R.id.save_date_time:

                saveToCalendar();

                break;
        }
    }

    private void saveToCalendar() {

        beginTime = Calendar.getInstance();
        beginTime.set(this.mYear, this.mMonth, this.mDay, this.mStartHour, this.mStartMinute);

        endTime = Calendar.getInstance();
        endTime.set(this.mYear, this.mMonth, this.mDay, this.mEndHour, this.mEndMinute);

        String title = mEditActivityTitle.getText().toString();
        String description = mEditActivityInfo.getText().toString();

        // End time must be later than start time
        int sTime = (this.mStartHour * 60) + this.mStartMinute;
        int eTime = (this.mEndHour * 60) + this.mEndMinute;

        if (eTime - sTime >= 0) {

            Intent calendarIntent = new Intent(Intent.ACTION_INSERT);
            calendarIntent.setType("vnd.android.cursor.item/event");
            //calendarIntent.setData(CalendarContract.Events.CONTENT_URI);
            calendarIntent.putExtra(CalendarContract.Events.TITLE, title);
            calendarIntent.putExtra(CalendarContract.Events.DESCRIPTION, description);
            calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
            calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());


            // Possible to send invitations to friends for the event by using:
            // *Need to extract the email of the person to invite*
            //calendarIntent.putExtra(Intent.EXTRA_EMAIL, "mavewonders@gmail.com, testing@gmail.com");

            // Able to add location as well
            //calendarIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "My Guest House");

            startActivity(calendarIntent);

        } else {
            Toast.makeText(getActivity(), "Please enter a valid Start and End time, *End time should be later than Start time*", Toast.LENGTH_LONG).show();
        }
    }

    private void disableEditText(EditText editText) {
        editText.setFocusable(false);
        editText.setEnabled(false);
        editText.setCursorVisible(false);
        editText.setKeyListener(null);
        editText.setBackgroundColor(Color.TRANSPARENT);

        //editText.setFocusableInTouchMode(true)
    }

    /*
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getContext(), year, month, day );
    }
    */
}
