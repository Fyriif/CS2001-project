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

import com.brunel.group30.fitnessapp.Enums.Day;
import com.brunel.group30.fitnessapp.Enums.Location;
import com.brunel.group30.fitnessapp.Models.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SettingUpActivity extends AppCompatActivity
        implements RangeTimePickerDialog.ISelectedTime {
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private CustomViewPager mViewPager;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseDatabase;
    private FirebaseUser currentUser;

    private UserInfo userInfo;
    private HashMap<String, List<String>> workOutTimes;

    private RangeTimePickerDialog rangeTimePickerDialog;
    private Calendar calendar;

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

        this.firebaseDatabase = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.currentUser = this.mAuth.getCurrentUser();

        Toast.makeText(getApplicationContext(),getString(R.string.info_user_logged_in) + ": "
                        + this.currentUser.getEmail(), Toast.LENGTH_LONG).show();

        this.userInfo = new UserInfo();
        this.calendar = Calendar.getInstance();

        this.workOutTimes = new HashMap<>();
    }

    public void nextFragment(View v) {
        switch (this.mViewPager.getCurrentItem() + 1) {
            case 1:
                EditText forenameEditText = findViewById(R.id.edit_text_forename);
                EditText surnameEditText = findViewById(R.id.edit_text_surname);
                EditText dobEditText = findViewById(R.id.edit_text_dob);

                if (forenameEditText.getText().toString().isEmpty()) {
                    forenameEditText.setError(getString(R.string.error_field_empty));
                } else {
                    forenameEditText.setError(null);
                }

                if (surnameEditText.getText().toString().isEmpty()) {
                    surnameEditText.setError(getString(R.string.error_field_empty));
                } else {
                    surnameEditText.setError(null);
                }

                if (dobEditText.getText().toString().isEmpty()) {
                    dobEditText.setError(getString(R.string.error_field_empty));
                } else {
                    dobEditText.setError(null);
                }

                if (forenameEditText.getError() == null &&
                        surnameEditText.getError() == null &&
                        dobEditText.getError() == null) {

                    this.userInfo.setForename(forenameEditText.getText().toString());
                    this.userInfo.setSurname(surnameEditText.getText().toString());

                    this.mViewPager.setCurrentItem(this.mViewPager.getCurrentItem() + 1);
                }

                break;

            case 2:
                RadioButton maleRadioButton = findViewById(R.id.button_sex_male);
                RadioButton femaleRadioButton = findViewById(R.id.button_sex_female);
                RadioButton preferNotToSayRadioButton = findViewById(R.id.button_disability_prefer_not_to_say);

                if (!maleRadioButton.isChecked()
                        && !femaleRadioButton.isChecked()
                        && !preferNotToSayRadioButton.isChecked()) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.error_option_is_required),
                            Toast.LENGTH_SHORT).show();
                } else {
                    this.userInfo.setIsMale(maleRadioButton.isChecked());
                    this.mViewPager.setCurrentItem(this.mViewPager.getCurrentItem() + 1);
                }

                break;

            case 3:
                CustomNumberPicker heightNumberPicker = findViewById(R.id.number_picker_height);

                this.userInfo.setHeight(heightNumberPicker.getValue());
                this.mViewPager.setCurrentItem(this.mViewPager.getCurrentItem() + 1);

                break;

            case 4:
                CustomNumberPicker weightNumberPicker = findViewById(R.id.number_picker_weight);
                preferNotToSayRadioButton = findViewById(R.id.button_disability_prefer_not_to_say);

                this.userInfo.setWeight(preferNotToSayRadioButton.isChecked() ?
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
            this.rangeTimePickerDialog = new RangeTimePickerDialog();
            this.rangeTimePickerDialog.newInstance(R.color.colorAccent, R.color.White,
                    R.color.colorPrimary, R.color.colorAccent, false);
            this.rangeTimePickerDialog.setValidateRange(true);
            this.rangeTimePickerDialog.setInitialOpenedTab(
                    RangeTimePickerDialog.InitialOpenedTab.START_CLOCK_TAB);
            DateTime endTime = new DateTime().plusHours(1);
            this.rangeTimePickerDialog.setInitialEndClock(endTime.getHourOfDay(),
                    endTime.getMinuteOfDay());
            this.rangeTimePickerDialog.show(getFragmentManager(), String.valueOf(Day.valueOf(
                    dayCheckBox.getText().toString().toUpperCase())));
        } else {
            this.workOutTimes.remove(dayCheckBox.getText().toString().toUpperCase().toLowerCase());
        }
    }

    @Override
    public void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd) {
        this.workOutTimes.put(this.rangeTimePickerDialog.getTag().toLowerCase(),
                Arrays.asList(hourStart + ":" + minuteStart, hourEnd + ":" + minuteEnd));
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

                firebaseDatabase.collection(UserInfo.COLLECTION_NAME)
                        .document(currentUser.getUid())
                        .set(this.userInfo)
                        .addOnSuccessListener(aVoid ->
                                startActivity(new Intent(getApplicationContext(),
                                        DashboardActivity.class)))
                        // TODO: improve error here
                        .addOnFailureListener(e -> Toast.makeText(getApplicationContext(),
                                "Error writing document", Toast.LENGTH_LONG).show());
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

            TextView progressNumTextView = rootView.findViewById(R.id.text_view_progress_num);
            String progressText = progressNumTextView.getText().toString();
            progressText = progressText.replaceFirst("X",
                    String.valueOf(getArguments().getInt("SECTION_NUMBER")))
                    .replaceFirst("X", String.valueOf(
                            getArguments().getInt("TOTAL_FRAGMENTS")));
            progressNumTextView.setText(progressText);

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