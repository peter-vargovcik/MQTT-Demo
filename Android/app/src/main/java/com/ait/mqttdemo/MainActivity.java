package com.ait.mqttdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity implements  MqttCallback,IMqttMessageListener{
    public final static String TAG = "DebugMQTT";
    private TextView tv;

    // https://www.eclipse.org/paho/clients/java/
    private String topic        = "test/message";
    private String content      = "Message from MqttPublishSample";
    private int qos             = 2;
    private String broker       = "tcp://192.168.4.1:1883";
    private String clientId     = "JavaSample";
    private MemoryPersistence persistence = new MemoryPersistence();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //Remove notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        tv = findViewById(R.id.tv);

        mqttConnect();

    }

    private void mqttConnect() {
        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            connOpts.setAutomaticReconnect(true);

            Log.d(TAG,"connOpts: "+connOpts.toString());

            sampleClient.setCallback(this);
            Log.d(TAG,"Connecting to broker: "+broker);
            sampleClient.connect(connOpts);

            Log.d(TAG,"Connected");

            Log.d(TAG,"Publishing message: "+content);


            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);
            Log.d(TAG,"Message published");

            sampleClient.subscribe("st_esp", this);
            sampleClient.subscribe("cm_esp", this);

//            sampleClient.disconnect();
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
        Log.d(TAG,"Connection Lost "+ cause.getMessage());
//        mqttConnect();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        final String incomingMessage = new String(message.getPayload());
        Log.d(TAG,incomingMessage);
//        tv.setText(incomingMessage);
        runOnUiThread(new Runnable() {
            public void run() {
                tv.setText(incomingMessage);
            }
        });
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
