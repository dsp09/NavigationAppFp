package org.tracker.prashu.navigationApp;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MySettingsActivity extends AppCompatActivity {

    // initializing variables.
    int defaultColor;
    Button button;
    Button settingsSaveButton;
    NumberPicker numberPicker;
    String hexColor;
    public static final int SETTINGS_REQUEST = 102;
    Integer minDistanceChangeFromSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_settings);
        // setting the default colour to black and converting it to hex code format.
        defaultColor = ContextCompat.getColor(this, R.color.colorBlack);
        hexColor = String.format("#%06X", (0xFFFFFF & defaultColor));

        button = (Button) findViewById(R.id.changePathColorBtn);
        settingsSaveButton = (Button) findViewById(R.id.settingsSaveButton);
        // creating the object of the number picker and setting its min and max bounds.
        numberPicker = (NumberPicker) findViewById(R.id.settingsNumberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(500);
        numberPicker.setWrapSelectorWheel(true);

        // setting the listener to the value change to the number picker.
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                //Display the newly selected number from picker
                minDistanceChangeFromSettings = newVal;
            }
        });

        // on click of the colour button.
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling the method selectColorPick method
                selectColorPick();
            } // onclick ends
        });

        // setting the save button listener
        settingsSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // fire an intent back to the home activity and setting the result as same while sending the intent to the setting activity
                // passing extras as the path colour and the min update distance.
                // and then finish the settings activity.
                Intent intent = new Intent();
                intent.putExtra("selectedColorStringKey", hexColor);
                intent.putExtra("minDistanceChangeFromSettings", minDistanceChangeFromSettings);
                setResult(SETTINGS_REQUEST, intent);
                finish();
            }
        });
    }

    // method that creates a colour picker dialog and user selects the colour and set it to the hex colour code on pressing the ok button.
    public void selectColorPick() {
        final AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(
                this,
                defaultColor,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        defaultColor = color;

                        hexColor = String.format("#%06X", (0xFFFFFF & defaultColor));

                    }
                });
        colorPicker.show();
    }
}
