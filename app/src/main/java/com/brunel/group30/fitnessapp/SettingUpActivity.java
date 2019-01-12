package com.brunel.group30.fitnessapp;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mcsoft.timerangepickerdialog.RangeTimePickerDialog;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SettingUpActivity extends AppCompatActivity implements RangeTimePickerDialog.ISelectedTime  {
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private CustomViewPager mViewPager;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseDatabase;

    Map<String, Object> userDataHashMap;

    private RangeTimePickerDialog rangeTimePickerDialog;
    private Calendar calendar;
    private Format dateFormat;
    private Locale defaultLocale;
    private EditText dobEditText;
    private Map<String, Map<String, String>> workOutDayTimes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_up);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (CustomViewPager) findViewById(R.id.view_pager_sign_up);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setSwipeable(false);

        this.firebaseDatabase = FirebaseFirestore.getInstance();
        this.mAuth = FirebaseAuth.getInstance();
        this.currentUser = this.mAuth.getCurrentUser();

        this.userDataHashMap = new HashMap<>();

        this.calendar = Calendar.getInstance();
        this.defaultLocale = Locale.getDefault();
        this.dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());

        this.workOutDayTimes = new HashMap<>();
    }

    public void nextFragment(View v) {
        switch (this.mViewPager.getCurrentItem() + 1) {
            case 1:
                EditText forenameEditText = (EditText) findViewById(R.id.edit_text_forename);
                EditText surnameEditText = (EditText) findViewById(R.id.edit_text_surname);
                EditText dobEditText = (EditText) findViewById(R.id.edit_text_dob);

                boolean forenameValid = false, surnameValid = false, dobValid = false;

                if (forenameEditText.getText().toString().isEmpty()) {
                    forenameEditText.setError(getString(R.string.error_field_empty));
                } else {
                    forenameEditText.setError(null);
                    forenameValid = true;
                }

                if (surnameEditText.getText().toString().isEmpty()) {
                    surnameEditText.setError(getString(R.string.error_field_empty));
                } else {
                    surnameEditText.setError(null);
                    surnameValid = true;
                }

                if (dobEditText.getText().toString().isEmpty()) {
                    dobEditText.setError(getString(R.string.error_field_empty));
                } else {
                    dobEditText.setError(null);
                    dobValid = true;
                }

                if (forenameValid && surnameValid && dobValid) {
                    userDataHashMap.put(DBFields.FORENAME, forenameEditText.getText());
                    userDataHashMap.put(DBFields.SURNAME, surnameEditText.getText());
                    userDataHashMap.put(DBFields.DOB, dobEditText.getText());
                    this.mViewPager.setCurrentItem(this.mViewPager.getCurrentItem() + 1);
                }

                break;

            case 2:
                RadioButton maleRadioButton = (RadioButton) findViewById(R.id.button_sex_male);
                RadioButton femaleRadioButton = (RadioButton) findViewById(R.id.button_sex_female);

                if (!maleRadioButton.isChecked() && !femaleRadioButton.isChecked()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_option_is_required), Toast.LENGTH_SHORT).show();
                } else {
                    userDataHashMap.put(DBFields.GENDER, maleRadioButton.isChecked());
                    this.mViewPager.setCurrentItem(this.mViewPager.getCurrentItem() + 1);
                }

                break;

            case 3:
                CustomNumberPicker ageNumberPicker = (CustomNumberPicker) findViewById(R.id.number_picker_height);

                userDataHashMap.put(DBFields.HEIGHT, ageNumberPicker.getValue());
                this.mViewPager.setCurrentItem(this.mViewPager.getCurrentItem() + 1);

                break;

            case 4:
                CustomNumberPicker weightNumberPicker = (CustomNumberPicker) findViewById(R.id.number_picker_weight);

                userDataHashMap.put(DBFields.WEIGHT, weightNumberPicker.getValue());
                this.mViewPager.setCurrentItem(this.mViewPager.getCurrentItem() + 1);

                break;

            case 5:
                RadioButton yesDisabilityRadioButton = (RadioButton) findViewById(R.id.button_disability_yes);
                RadioButton noDisabilityRadioButton = (RadioButton) findViewById(R.id.button_disability_no);

                if (!yesDisabilityRadioButton.isChecked() && !noDisabilityRadioButton.isChecked()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_option_is_required), Toast.LENGTH_SHORT).show();
                } else {
                    userDataHashMap.put(DBFields.DISABILITY, yesDisabilityRadioButton.isChecked());
                    this.mViewPager.setCurrentItem(this.mViewPager.getCurrentItem() + 1);
                }

                break;

            case 6:
                CheckBox gymLocationCheckBox = (CheckBox) findViewById(R.id.button_gym);
                CheckBox homeLocationCheckBox = (CheckBox) findViewById(R.id.button_home);
                CheckBox parkLocationCheckBox = (CheckBox) findViewById(R.id.button_park);


                if (!gymLocationCheckBox.isChecked() && !homeLocationCheckBox.isChecked() &&
                        !parkLocationCheckBox.isChecked()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_option_is_required), Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, Boolean> locationHashMap = new HashMap<>();
                    locationHashMap.put(DBFields.LOCATION_GYM, gymLocationCheckBox.isChecked());
                    locationHashMap.put(DBFields.LOCATION_HOME, homeLocationCheckBox.isChecked());
                    locationHashMap.put(DBFields.LOCATION_PARK, parkLocationCheckBox.isChecked());

                    userDataHashMap.put(DBFields.LOCATION, locationHashMap);
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
        this.dobEditText = v.findViewById(R.id.edit_text_dob);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SimpleDateFormat")

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                dobEditText.setText(new SimpleDateFormat(((SimpleDateFormat) dateFormat).toLocalizedPattern()).format(calendar.getTime()));
            }

        };

        new DatePickerDialog(this, date, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    public void createTimePickerFragment(View v) {
        CheckBox dayCheckBox = ((CheckBox)v);

        if (dayCheckBox.isChecked()) {
            this.rangeTimePickerDialog = new RangeTimePickerDialog();
            this.rangeTimePickerDialog.newInstance(R.color.colorAccent, R.color.White, R.color.colorPrimary, R.color.colorAccent, false);
            this.rangeTimePickerDialog.setValidateRange(true);
            this.rangeTimePickerDialog.setInitialOpenedTab(RangeTimePickerDialog.InitialOpenedTab.START_CLOCK_TAB);
            this.rangeTimePickerDialog.show(getFragmentManager(), String.valueOf(Days.valueOf(dayCheckBox.getText().toString().toUpperCase())));
        } else {
            this.workOutDayTimes.remove(String.valueOf(Days.valueOf(dayCheckBox.getText().toString().toLowerCase())));
        }
    }

    @Override
    public void onSelectedTime(int hourStart, int minuteStart, int hourEnd, int minuteEnd) {
        Map<String, String> times = new HashMap<>();
        times.put(DBFields.DAY_TIME_FROM, hourStart + ":" + minuteStart);
        times.put(DBFields.DAY_TIME_TO, hourEnd + ":" + minuteEnd);
        this.workOutDayTimes.put(this.rangeTimePickerDialog.getTag().toLowerCase(), times);
    }

    public void sendToDB(View view) {
        CheckBox mondayCheckBox = (CheckBox) findViewById(R.id.button_day_monday);
        CheckBox tuesdayCheckBox = (CheckBox) findViewById(R.id.button_day_tuesday);
        CheckBox wednesdayCheckBox = (CheckBox) findViewById(R.id.button_day_wednesday);
        CheckBox thursdayCheckBox = (CheckBox) findViewById(R.id.button_day_thursday);
        CheckBox fridayCheckBox = (CheckBox) findViewById(R.id.button_day_friday);
        CheckBox saturdayCheckBox = (CheckBox) findViewById(R.id.button_day_saturday);
        CheckBox sundayCheckBox = (CheckBox) findViewById(R.id.button_day_sunday);

        if (!mondayCheckBox.isChecked() && !tuesdayCheckBox.isChecked() && !wednesdayCheckBox.isChecked()
                && !thursdayCheckBox.isChecked() && !fridayCheckBox.isChecked()
                && !saturdayCheckBox.isChecked() && !sundayCheckBox.isChecked()) {
            Toast.makeText(getApplicationContext(), getString(R.string.error_option_is_required), Toast.LENGTH_SHORT).show();
        } else {
            this.userDataHashMap.put(DBFields.WORK_OUT_DAYS, this.workOutDayTimes);

            firebaseDatabase.collection("user-info").document(this.currentUser.getUid())
                    .set(this.userDataHashMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // TODO: go to dashboard
                            Toast.makeText(getApplicationContext(), "DocumentSnapshot successfully written!", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error writing document", Toast.LENGTH_LONG).show();
                        }
                    });
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(getArguments().getInt(ARG_SECTION_LAYOUT_RESOURCE_ID), container, false);

            TextView progressNumTextView = (TextView) rootView.findViewById(R.id.text_view_progress_num);
            String progressText = progressNumTextView.getText().toString();
            progressText = progressText.replaceFirst("X", String.valueOf(getArguments().getInt("SECTION_NUMBER")))
                                        .replaceFirst("X", String.valueOf(getArguments().getInt("TOTAL_FRAGMENTS")));
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