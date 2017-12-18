package com.rabor.burnedcaloriescalculator;

// import statements
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.text.NumberFormat;

public class BurnedCaloriesCalculatorActivity extends AppCompatActivity {

    // define widget variables
    private EditText weightET;
    private EditText nameET;
    private TextView milesRanTV;
    private TextView caloriesBurnedTV;
    private TextView bmiTV;
    private SeekBar milesRanSeekBar;
    private Spinner feetSpinner;
    private Spinner inchesSpinner;

    // define instance variables
    private int miles = 0;
    private int feetPosition = 1;
    private int inchesPosition = 1;
    private String weightString = "";
    private String nameString = "";
    private SharedPreferences savedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_burned_calories_calculator);

        // get reference to the widgets
        weightET = (EditText) findViewById(R.id.weightET);
        nameET = (EditText) findViewById(R.id.nameET);
        milesRanTV = (TextView) findViewById(R.id.milesRanTV);
        caloriesBurnedTV = (TextView) findViewById(R.id.caloriesBurnedTV);
        bmiTV = (TextView) findViewById(R.id.bmiTV);
        milesRanSeekBar = (SeekBar) findViewById(R.id.milesRanSeekBar);
        feetSpinner = (Spinner) findViewById(R.id.feetSpinner);
        inchesSpinner = (Spinner) findViewById(R.id.inchesSpinner);

        // set the feet array adapter
        ArrayAdapter<CharSequence> feetAdapter = ArrayAdapter.createFromResource(
                this, R.array.feet_array, R.layout.spinner_item
         );
        feetAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        feetSpinner.setAdapter(feetAdapter);

        // set the inches array adapter
        ArrayAdapter<CharSequence> inchesAdapter = ArrayAdapter.createFromResource(
                this, R.array.inches_array, R.layout.spinner_item
        );
        inchesAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        inchesSpinner.setAdapter(inchesAdapter);

        // set the listener for the Feet Spinner widget
        feetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position,
                                       long id) {
                feetPosition = position + 1;
                calculateAndDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // set the listener for the Inches Spinner widget
        inchesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position,
                                       long id) {
                inchesPosition = position + 1;
                calculateAndDisplay();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // set the Weight EditText Listener
        weightET.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

                    calculateAndDisplay();
                }

                // hide soft keyboard
                return false;
            }
        });

        // set the Name EditText Listener
        nameET.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_DONE ||
                        actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {

                    calculateAndDisplay();
                }

                // hide soft keyboard
                return false;
            }
        });

        // set the Miles Ran SeekBar Listener
        milesRanSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                milesRanTV.setText(progress + "mi");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                calculateAndDisplay();
            }
        });

        // get the shared preference
        savedValues = getSharedPreferences("savedValues", MODE_PRIVATE);
    }

    private void calculateAndDisplay() {
        // get weight from user
        weightString = weightET.getText().toString();
        int weight;
        if(weightString.equals("")) {
            weight = 0;
        } else {
            weight = Integer.parseInt(weightString);
        }

        // get name from user
        nameString = nameET.getText().toString();

        // get the miles ran
        miles = milesRanSeekBar.getProgress();

        // get the height information
        String feetString = feetSpinner.getSelectedItem().toString();
        int feet;
        if (feetString.equals("")) {
            feet = 0;
        }
        else {
            feet = Integer.parseInt(feetString);
        }

        String inchesString = inchesSpinner.getSelectedItem().toString();
        int inches;
        if (inchesString.equals("")) {
            inches = 0;
        }
        else {
            inches = Integer.parseInt(inchesString);
        }

        // calculate the calories burned and the bmi
        float bmi = 0;
        double caloriesBurned = 0.75 * weight * miles;
        bmi = (weight * 703) / ((12 * feet + inches) * (12 * feet + inches));

        // display the calories burned and bmi on the widget
        caloriesBurnedTV.setText(String.valueOf(caloriesBurned));
        bmiTV.setText(String.valueOf(bmi));
    }

    @Override
    protected void onPause() {

        // save the instance variables
        SharedPreferences.Editor editor = savedValues.edit();
        editor.putString("weightString", weightString);
        editor.putString("nameString", nameString);
        editor.putInt("miles", miles);
        editor.putInt("feetPosition", feetPosition);
        editor.putInt("inchesPosition", inchesPosition);
        editor.commit();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // get the instance variables
        weightString = savedValues.getString("weightString", "");
        nameString = savedValues.getString("nameString", "");
        miles = savedValues.getInt("miles", 0);
        feetPosition = savedValues.getInt("feetPosition", 1);
        inchesPosition = savedValues.getInt("inchesPosition", 1);

        // set the weight on its widget
        weightET.setText(weightString);
        nameET.setText(nameString);

        // set the miles ran on its widget
        milesRanSeekBar.setProgress(miles);

        // NOTE: this executes the onItemSelected method,
        // which executes the calculateAndDisplay method
        int feetPos = feetPosition - 1;
        feetSpinner.setSelection(feetPos);

        int inchPos = inchesPosition;
        inchesSpinner.setSelection(inchPos);
    }

}
