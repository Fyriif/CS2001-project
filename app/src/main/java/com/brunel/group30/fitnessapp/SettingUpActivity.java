package com.brunel.group30.fitnessapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.appizona.yehiahd.fastsave.FastSave;
import com.brunel.group30.fitnessapp.Custom.CustomNumberPicker;
import com.brunel.group30.fitnessapp.Custom.CustomTimeRangePicker;
import com.brunel.group30.fitnessapp.Custom.CustomViewPager;
import com.brunel.group30.fitnessapp.Enums.Day;
import com.brunel.group30.fitnessapp.Enums.Location;
import com.brunel.group30.fitnessapp.Models.Goals;
import com.brunel.group30.fitnessapp.Models.UserInfo;
import com.brunel.group30.fitnessapp.Services.CustomFirebaseFirestoreService;
import com.brunel.group30.fitnessapp.Services.GoogleFitApi;
import com.brunel.group30.fitnessapp.Utils.Exceptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SettingUpActivity extends AppCompatActivity {
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private CustomViewPager mViewPager;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private UserInfo userInfo;
    private HashMap<String, List<String>> workOutTimes;

    private CustomTimeRangePicker rangeTimePickerDialog;
    private Calendar calendar;

    CustomNumberPicker heightNumberPicker;
    CustomNumberPicker weightNumberPicker;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_up);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.view_pager_sign_up);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setSwipeable(false);

        this.mAuth = FirebaseAuth.getInstance();
        this.currentUser = this.mAuth.getCurrentUser();

        Toast.makeText(getApplicationContext(),getString(R.string.msg_user_already_logged_in) + ": "
                + this.currentUser.getEmail(), Toast.LENGTH_LONG).show();

        this.userInfo = new UserInfo();
        this.calendar = Calendar.getInstance();

        this.workOutTimes = new HashMap<>();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (this.currentUser == null) {
            this.mAuth.signOut();
            startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_sign_out))
                .setMessage(getString(R.string.msg_confirm_sign_out))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .setPositiveButton(getString(R.string.action_confirm_sign_out), (arg0, arg1) -> {
                    mAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
                }).create().show();
    }

    public void nextFragment(View v) {
        switch (this.mViewPager.getCurrentItem() + 1) {
            case 1:
                EditText dobEditText = findViewById(R.id.edit_text_dob);

                if (dobEditText.getText().toString().isEmpty()) {
                    dobEditText.setError(getString(R.string.error_field_empty));
                } else {
                    dobEditText.setError(null);
                }

                if (dobEditText.getError() == null) {
                    this.mViewPager.setCurrentItem(this.mViewPager.getCurrentItem() + 1);
                }

                break;

            case 2:
                RadioButton maleRadioButton = findViewById(R.id.button_sex_male);
                RadioButton femaleRadioButton = findViewById(R.id.button_sex_female);
                RadioButton preferNotToSayRadioButton = findViewById(R.id.button_sex_prefer_not_to_say);

                if (!maleRadioButton.isChecked()
                        && !femaleRadioButton.isChecked()
                        && !preferNotToSayRadioButton.isChecked()) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.error_option_is_required),
                            Toast.LENGTH_SHORT).show();
                } else {
                    this.userInfo.setIsMale(maleRadioButton.isChecked());

                    GoogleFitApi.getHeight(this, GoogleSignIn.getLastSignedInAccount(this)).addOnSuccessListener(dataReadResponse -> {
                        List<DataPoint> dataPoints = dataReadResponse.getDataSets().get(0).getDataPoints();
                        userInfo.setHeight(dataPoints.isEmpty() ? 175 : dataPoints.get(0).getValue(Field.FIELD_HEIGHT).asInt());

                        heightNumberPicker = findViewById(R.id.number_picker_height);
                        heightNumberPicker.setValue(userInfo.getHeight());

                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                    });
                }

                break;

            case 3:
                this.userInfo.setHeight(heightNumberPicker.getValue());

                GoogleFitApi.getWeight(this, GoogleSignIn.getLastSignedInAccount(this)).addOnSuccessListener(dataReadResponse -> {
                    List<DataPoint> dataPoints = dataReadResponse.getDataSets().get(0).getDataPoints();
                    userInfo.setWeight(dataPoints.isEmpty() ? 75 : dataPoints.get(0).getValue(Field.FIELD_WEIGHT).asInt());

                    weightNumberPicker = findViewById(R.id.number_picker_weight);
                    weightNumberPicker.setValue(userInfo.getWeight());

                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                });

                break;

            case 4:
                CheckBox preferNotToSayCheckBox = findViewById(R.id.button_weight_prefer_not_to_say);

                this.userInfo.setWeight(preferNotToSayCheckBox.isChecked() ?
                        0 : weightNumberPicker.getValue());
                this.mViewPager.setCurrentItem(this.mViewPager.getCurrentItem() + 1);

                break;

            case 5:
                RadioButton yesDisabilityRadioButton = findViewById(R.id.button_disability_yes);
                RadioButton noDisabilityRadioButton = findViewById(R.id.button_disability_no);
                preferNotToSayRadioButton = findViewById(R.id.button_disability_prefer_not_to_say);

                if (!yesDisabilityRadioButton.isChecked()
                        && !noDisabilityRadioButton.isChecked()
                        && !preferNotToSayRadioButton.isChecked()) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.error_option_is_required),
                            Toast.LENGTH_SHORT).show();
                } else {
                    this.userInfo.setIsDisabled(yesDisabilityRadioButton.isChecked());
                    this.mViewPager.setCurrentItem(this.mViewPager.getCurrentItem() + 1);
                }

                break;

            case 6:
                CheckBox gymLocationCheckBox = findViewById(R.id.button_gym);
                CheckBox homeLocationCheckBox = findViewById(R.id.button_home);
                CheckBox parkLocationCheckBox = findViewById(R.id.button_park);


                if (!gymLocationCheckBox.isChecked() && !homeLocationCheckBox.isChecked() &&
                        !parkLocationCheckBox.isChecked()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_option_is_required),
                            Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, Boolean> locations = new HashMap<>();
                    locations.put(String.valueOf(Location.GYM).toLowerCase(),
                            gymLocationCheckBox.isChecked());
                    locations.put(String.valueOf(Location.HOME).toLowerCase(),
                            homeLocationCheckBox.isChecked());
                    locations.put(String.valueOf(Location.PARK).toLowerCase(),
                            parkLocationCheckBox.isChecked());

                    this.userInfo.setLocations(locations);
                    this.mViewPager.setCurrentItem(this.mViewPager.getCurrentItem() + 1);
                }

                break;

            default:
                this.mViewPager.setCurrentItem(this.mViewPager.getCurrentItem() + 1);
                break;
        }
    }

    public void previousFragment(View v) {
        this.mViewPager.setCurrentItem(this.mViewPager.getCurrentItem() - 1);
    }

    public void enterDateOfBirth(View v) {
        EditText dobEditText = v.findViewById(R.id.edit_text_dob);
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            dobEditText.setText(DateFormat.getDateInstance(DateFormat.LONG)
                    .format(calendar.getTime()));

            this.userInfo.setDob(new SimpleDateFormat("yyyy-MM-dd",
                    Locale.getDefault()).format(calendar.getTime()));
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    public void createTimePickerFragment(View v) {
        CheckBox dayCheckBox = ((CheckBox) v);

        if (dayCheckBox.isChecked()) {
            this.rangeTimePickerDialog = new CustomTimeRangePicker(this);
            this.rangeTimePickerDialog.show();

            this.rangeTimePickerDialog.setOnCancelListener(dialog -> dayCheckBox.setChecked(false));
            this.rangeTimePickerDialog.getSetTimeRangeBtn().setOnClickListener(v1 -> {
                int startTimeHour = rangeTimePickerDialog.getStartTimePicker().getCurrentHour();
                int startTimeMin = rangeTimePickerDialog.getStartTimePicker().getCurrentMinute();
                int endTimeHour = rangeTimePickerDialog.getEndTimePicker().getCurrentHour();
                int endTimeMin = rangeTimePickerDialog.getEndTimePicker().getCurrentMinute();

                if (endTimeHour > startTimeHour) {
                    addToWorkOutTimes(Day.valueOf(dayCheckBox.getText().toString().toUpperCase()),
                            startTimeHour, startTimeMin, endTimeHour, endTimeMin);
                    rangeTimePickerDialog.dismiss();
                } else if (endTimeHour == startTimeHour && endTimeMin > startTimeMin) {
                    addToWorkOutTimes(Day.valueOf(dayCheckBox.getText().toString().toUpperCase()),
                            startTimeHour, startTimeMin, endTimeHour, endTimeMin);
                    rangeTimePickerDialog.dismiss();
                } else {
                    Toast.makeText(SettingUpActivity.this,
                            R.string.error_time_greater,
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            this.workOutTimes.remove(Day.valueOf(dayCheckBox.getText().toString().toUpperCase())
                    .toString().toLowerCase());
        }
    }

    void addToWorkOutTimes(Day day, int startTimeHour, int startTimeMin,
                           int endTimeHour, int endTimeMin) {
        workOutTimes.put(day.toString().toLowerCase(), Arrays.asList(startTimeHour + ":" + startTimeMin,
                endTimeHour + ":" + endTimeMin));
    }

    /**
     * Send data off to Firebase Firestore
     */
    public void sendToDB(View v) {
        CheckBox mondayCheckBox = findViewById(R.id.button_day_monday);
        CheckBox tuesdayCheckBox = findViewById(R.id.button_day_tuesday);
        CheckBox wednesdayCheckBox = findViewById(R.id.button_day_wednesday);
        CheckBox thursdayCheckBox = findViewById(R.id.button_day_thursday);
        CheckBox fridayCheckBox = findViewById(R.id.button_day_friday);
        CheckBox saturdayCheckBox = findViewById(R.id.button_day_saturday);
        CheckBox sundayCheckBox = findViewById(R.id.button_day_sunday);

        if (!mondayCheckBox.isChecked() && !tuesdayCheckBox.isChecked()
                && !wednesdayCheckBox.isChecked()
                && !thursdayCheckBox.isChecked() && !fridayCheckBox.isChecked()
                && !saturdayCheckBox.isChecked() && !sundayCheckBox.isChecked()) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_option_is_required),
                    Toast.LENGTH_SHORT).show();
        } else {
            if (this.workOutTimes != null && !this.workOutTimes.isEmpty()) {
                this.userInfo.setWorkOutDays(this.workOutTimes);

                Task<Void> sendUserDataTask = CustomFirebaseFirestoreService.INSTANCE.sendDocument(
                        UserInfo.COLLECTION_NAME,
                        currentUser.getUid(),
                        this.userInfo
                );

                sendUserDataTask.addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Exceptions.FirestoreExceptions.errorFailedToWriteDocument(
                                UserInfo.COLLECTION_NAME,
                                currentUser.getUid()
                        );
                    }
                });

                Goals goals = new Goals(10000,3*1000,100);
                Task<Void> sendUserGoalsTask = CustomFirebaseFirestoreService.INSTANCE.sendDocument(
                        Goals.COLLECTION_NAME,
                        currentUser.getUid(),
                        goals
                );

                sendUserGoalsTask.addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Exceptions.FirestoreExceptions.errorFailedToWriteDocument(
                                Goals.COLLECTION_NAME,
                                currentUser.getUid()
                        );
                    }
                });

                this.userInfo.setGoals(goals);

                FastSave.init(getApplicationContext());
                FastSave.getInstance().saveObject(UserInfo.COLLECTION_NAME, userInfo);

                startActivity(new Intent(getApplicationContext(), DashboardActivity.class)
                        .putExtra(UserInfo.COLLECTION_NAME,
                                new Gson().toJson(userInfo)));
            }
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_LAYOUT_RESOURCE_ID = "layout_resource_id";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, int totalFragments) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();

            switch (sectionNumber) {
                case 1:
                    args.putInt(ARG_SECTION_LAYOUT_RESOURCE_ID, R.layout.fragment_sign_up);
                    args.putInt("SECTION_NUMBER", sectionNumber);
                    break;
                case 2:
                    args.putInt(ARG_SECTION_LAYOUT_RESOURCE_ID, R.layout.fragment_sign_up2);
                    args.putInt("SECTION_NUMBER", sectionNumber);
                    break;
                case 3:
                    args.putInt(ARG_SECTION_LAYOUT_RESOURCE_ID, R.layout.fragment_sign_up3);
                    args.putInt("SECTION_NUMBER", sectionNumber);
                    break;
                case 4:
                    args.putInt(ARG_SECTION_LAYOUT_RESOURCE_ID, R.layout.fragment_sign_up4);
                    args.putInt("SECTION_NUMBER", sectionNumber);
                    break;
                case 5:
                    args.putInt(ARG_SECTION_LAYOUT_RESOURCE_ID, R.layout.fragment_sign_up5);
                    args.putInt("SECTION_NUMBER", sectionNumber);
                    break;
                case 6:
                    args.putInt(ARG_SECTION_LAYOUT_RESOURCE_ID, R.layout.fragment_sign_up6);
                    args.putInt("SECTION_NUMBER", sectionNumber);
                    break;
                case 7:
                    args.putInt(ARG_SECTION_LAYOUT_RESOURCE_ID, R.layout.fragment_sign_up7);
                    args.putInt("SECTION_NUMBER", sectionNumber);
                    break;
                default:
                    // If all fails, default to the first sign up layout
                    args.putInt(ARG_SECTION_LAYOUT_RESOURCE_ID, R.layout.fragment_sign_up);
                    args.putInt("SECTION_NUMBER", 1);
                    break;
            }

            args.putInt("TOTAL_FRAGMENTS", totalFragments);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(getArguments() != null ?
                            getArguments().getInt(ARG_SECTION_LAYOUT_RESOURCE_ID) : 0,
                    container, false);

            TextView progressNumTextView = rootView.findViewById(R.id.text_view_setting_up_progress_num);
            progressNumTextView.setText(String.format(getString(R.string.setting_up_progress_number_count),
                    getArguments().getInt("SECTION_NUMBER"),
                    getArguments().getInt("TOTAL_FRAGMENTS"))
            );

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, getCount());
        }

        @Override
        public int getCount() {
            return 7;
        }
    }
}