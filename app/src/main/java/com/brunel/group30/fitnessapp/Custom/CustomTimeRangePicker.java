package com.brunel.group30.fitnessapp.Custom;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TimePicker;

import com.brunel.group30.fitnessapp.R;

import java.util.Calendar;

public class CustomTimeRangePicker extends Dialog {
    public Button setTimeRangeBtn;
    public TimePicker startTimePicker, endTimePicker;

    public CustomTimeRangePicker(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_time_range_picker);

        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button cancelBtn = findViewById(R.id.button_cancel);
        setTimeRangeBtn = findViewById(R.id.button_set_time);

        cancelBtn.setOnClickListener(v -> cancel());
        setTimeRangeBtn.setOnClickListener(v -> dismiss());

        startTimePicker = findViewById(R.id.time_picker_start);
        endTimePicker = findViewById(R.id.time_picker_end);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        startTimePicker.setVisibility(View.VISIBLE);
                        endTimePicker.setVisibility(View.GONE);
                        break;
                    case 1:
                        startTimePicker.setVisibility(View.GONE);
                        endTimePicker.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);

        endTimePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
    }
}
