package com.example.aitopenday2019;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
//   https://github.com/LarsWerkman/HoloColorPicker
//    ColorPicker picker = (ColorPicker) findViewById(R.id.picker);
//
//    OpacityBar opacityBar = (OpacityBar) findViewById(R.id.opacitybar);
//    SaturationBar saturationBar = (SaturationBar) findViewById(R.id.saturationbar);
//
//
//        picker.addSVBar(svBar);
//        picker.addOpacityBar(opacityBar);
//        picker.addSaturationBar(saturationBar);
//        picker.addValueBar(valueBar);
//
//        //To get the color
//        picker.getColor();
//
//        //To set the old selected color u can do it like this
//        picker.setOldCenterColor(picker.getColor());
//        // adds listener to the colorpicker which is implemented
//        //in the activity
//        picker.setOnColorChangedListener(this);
//
//        //to turn of showing the old color
//        picker.setShowOldCenterColor(false);
//
//        //adding onChangeListeners to bars
//        opacitybar.setOnOpacityChangeListener(new OnOpacityChangeListener …)
//                    valuebar.setOnValueChangeListener(new OnValueChangeListener …)
//                    saturationBar.setOnSaturationChangeListener(new OnSaturationChangeListener …)

    //Seekbar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
