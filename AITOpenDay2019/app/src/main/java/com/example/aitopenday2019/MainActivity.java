package com.example.aitopenday2019;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import com.larswerkman.holocolorpicker.ColorPicker;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements MqttCallback {
    public final static String TAG = "DebugMQTT";

    private int selectedColor, servoPosition;
    private boolean isRedChecked,isYellowChecked,isGreenChecked;
    private ToggleButton toggleRed,toggleYellow,toggleGreen;
    private SeekBar simpleSeekBar;

    // https://www.eclipse.org/paho/clients/java/
    private String topic        = "test/message";
    private String content      = "Message from MqttPublishSample";
    private int qos             = 2;
    private String broker       = "tcp://192.168.4.1:1883";
    private String clientId;
    private MemoryPersistence persistence = new MemoryPersistence();
    private  MqttClient mqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Remove notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        clientId = generateClientID();

        //   https://github.com/LarsWerkman/HoloColorPicker
        ColorPicker picker = (ColorPicker) findViewById(R.id.picker);

        //To set the old selected color u can do it like this
        picker.setOldCenterColor(picker.getColor());
        // adds listener to the colorpicker which is implemented
        //in the activity
        picker.setOnColorChangedListener(new ColorPicker.OnColorChangedListener() {
            @Override
            public void onColorChanged(final int color) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        selectedColor = color;
                    }
                });
            }
        });

        //to turn of showing the old color
        picker.setShowOldCenterColor(false);


        toggleRed = (ToggleButton) findViewById(R.id.toggleRed);
        toggleRed.setText("RED");
        toggleRed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setText("RED");
                isRedChecked = isChecked;
            }
        });

        toggleYellow = (ToggleButton) findViewById(R.id.toggleYellow);
        toggleYellow.setText("Yellow");
        toggleYellow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setText("Yellow");
                isYellowChecked = isChecked;
            }
        });

        toggleGreen = (ToggleButton) findViewById(R.id.toggleGreen);
        toggleGreen.setText("Green");
        toggleGreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setText("GREEN");
                isGreenChecked = isChecked;
            }
        });


        simpleSeekBar=(SeekBar)findViewById(R.id.simpleSeekBar);

        //Seekbar
        mqttConnect();
    }

    private String generateClientID(){

        byte[] array = new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));

        return "AndroidClient_" +generatedString;
    }

    private void mqttConnect() {
        try {
            mqttClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
//            connOpts.setCleanSession(true);
            connOpts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
//            connOpts.setAutomaticReconnect(true);

            connOpts.setAutomaticReconnect(true);
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(10);
            connOpts.setCleanSession(false);

            Log.d(TAG,"connOpts: "+connOpts.toString());

            mqttClient.setCallback(this);
            Log.d(TAG,"Connecting to broker: "+broker);
            mqttClient.connect(connOpts);

            Log.d(TAG,"Connected");

//            Log.d(TAG,"Publishing message: "+content);
//
//
//            MqttMessage message = new MqttMessage(content.getBytes());
//            message.setQos(qos);
//            mqttClient.publish(topic, message);
//            Log.d(TAG,"Message published");

            mqttClient.subscribe("st_esp");
//            mqttClient.subscribe("cm_esp", this);

//            mqttClient.disconnect();
//            Log.d(TAG,"Disconnected");
        } catch(MqttException me) {
            Log.e(TAG,"reason "+me.getReasonCode());
            Log.e(TAG,"msg "+me.getMessage());
            Log.e(TAG,"loc "+me.getLocalizedMessage());
            Log.e(TAG,"cause "+me.getCause());
            Log.e(TAG,"excep "+me);
            me.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG,cause.getMessage());

//        try {
//            Thread.sleep(2000);
//
//            Log.d(TAG,"Restarting connection.... ");
//
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    mqttConnect();
//                }
//            }).start();
//
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        final String incomingMessage = new String(message.getPayload());
        Log.d(TAG,incomingMessage);
//        tv.setText(incomingMessage);
//        runOnUiThread(new Runnable() {
//            public void run() {
//                tv.setText(incomingMessage);
//            }
//        });
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    private String preparePayload(){


        int seekBarValue= simpleSeekBar.getProgress();

//                https://developer.android.com/reference/android/graphics/Color

        int alpha = (selectedColor >> 24) & 0xff; // or color >>> 24
        int red = (selectedColor >> 16) & 0xff;
        int green = (selectedColor >>  8) & 0xff;
        int blue = (selectedColor      ) & 0xff;



        try {
            JSONObject payloadJson = new JSONObject();

            payloadJson.put("servo",seekBarValue);
            payloadJson.put("redLed",isRedChecked);
            payloadJson.put("yellowLed",isYellowChecked);
            payloadJson.put("greenLed",isGreenChecked);
            payloadJson.put("rgb1",red);
            payloadJson.put("rgb2",blue);
            payloadJson.put("rgb3",green);


            Log.d("DEBUGG",payloadJson.toString());

            return  payloadJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  null;
    }

    public void submitCommand(View view) {

        if(mqttClient !=null){
            try{
                String payload = preparePayload();

                MqttMessage msg = new MqttMessage(payload.getBytes());
                msg.setQos(0);
                msg.setRetained(true);

                mqttClient.publish("cm_esp",msg);
            }catch (MqttException e){
                Log.e(TAG,e.getMessage());
            }
        }



    }
}
