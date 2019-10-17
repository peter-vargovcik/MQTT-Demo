#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <ArduinoJson.h>
#include <Servo.h>

const char* ssid = "MQTTDemo";
const char* password = "AitMqttDemo";
const char* mqtt_server = "192.168.4.1";

WiFiClient espClient;
PubSubClient client(espClient);

Servo myservo;

long lastMsg = 0;
// char msg[50];
int value = 0;

int pos; //servo test

// initializes or defines the output pin of the LM35 temperature sensor
int outputPin = A0;
int servoPin = D0;
int redLedPin = D1;
int yellowLedPin = D2;
int greenLedPin = D3;
int rgbPin1 = D5;
int rgbPin2 = D6;
int rgbPin3 = D7;


byte redLed = 0;
byte yellowLed = 0;
byte greenLed = 0;
int rgbPWM1 = 0;
int rgbPWM2 = 0;
int rgbPWM3 = 0;

const size_t capacity = JSON_OBJECT_SIZE(10);

void setup_wifi() {

  delay(10);
  // We start by connecting to a WiFi network
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  randomSeed(micros());

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

void callback(char* topic, byte* payload, unsigned int length) {
  Serial.print("Message arrived ");
  Serial.println(topic);

  if (strcmp(topic, "cm_esp") == 0) {
    char chars[length];

    for (int i = 0; i < length; i++) {
      chars[i] = (char)payload[i];
    }

    DynamicJsonDocument doc(JSON_OBJECT_SIZE(7) + 60);
    deserializeJson(doc, chars);

    // int counterVal = doc["counter"];
    // int potVal = doc["pot"];
    int servoVal = doc["servo"];
    bool redLedVal = doc["redLed"];
    bool yellowLedVal = doc["yellowLed"];
    bool greenLedVal = doc["greenLed"];
    // bool btn1Val = doc["btn1"];
    // bool btn2Val = doc["btn2"];
    // bool switchVal = doc["switch"];
    int rgb1Val = doc["rgb1"];
    int rgb2Val = doc["rgb2"];
    int rgb3Val = doc["rgb3"];

    serializeJson(doc, Serial);
    Serial.println();

    pos = servoVal;
    redLed = redLedVal;
    yellowLed = yellowLedVal;
    greenLed = greenLedVal;
    rgbPWM1 = rgb1Val;
    rgbPWM2 = rgb2Val;
    rgbPWM3 = rgb3Val;
  }
}

void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    // Create a random client ID
    String clientId = "ESP8266Client-";
    clientId += String(random(0xffff), HEX);
    // Attempt to connect
    if (client.connect(clientId.c_str())) {
      Serial.println("connected");
//      client.subscribe("status/espnode");
      client.subscribe("cm_esp");
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 5 seconds before retrying
      delay(5000);
    }
  }
}

void setup() {
  // pinMode(BUILTIN_LED, OUTPUT);     // Initialize the BUILTIN_LED pin as an output
  Serial.begin(115200);
  setup_wifi();
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);

  myservo.attach(servoPin);

  pinMode(redLedPin, OUTPUT);
  pinMode(yellowLedPin, OUTPUT);
  pinMode(greenLedPin, OUTPUT);

  pinMode(rgbPin1, OUTPUT);
  pinMode(rgbPin2, OUTPUT);
  pinMode(rgbPin3, OUTPUT);
}

void loop() {
  myservo.write(pos);

  digitalWrite(redLedPin, redLed);
  digitalWrite(yellowLedPin, yellowLed);
  digitalWrite(greenLedPin, greenLed);

  analogWrite(rgbPin1, map(rgbPWM1,0,255,1024,0));
  analogWrite(rgbPin2, map(rgbPWM2,0,255,1024,0));
  analogWrite(rgbPin3, map(rgbPWM3,0,255,1024,0));

  if (!client.connected()) {
    reconnect();
  }
  
  client.loop();

  long now = millis();
  if (now - lastMsg > 2000) {
    lastMsg = now;
    ++value;

    int analogValue = map(analogRead(outputPin), 0, 1024, 0, 100);

    DynamicJsonDocument doc(capacity);

    doc["counter"] = value;
    doc["pot"] = analogValue;
    doc["servo"] = pos;
    doc["redLed"] = digitalRead(redLedPin) == HIGH;
    doc["yellowLed"] = digitalRead(yellowLedPin) == HIGH;
    doc["greenLed"] = digitalRead(greenLedPin) == HIGH;
    // doc["btn1"] = false;
    // doc["btn2"] = true;
//     doc["switch"] = true;
    doc["rgb1"] = rgbPWM1;
    doc["rgb2"] = rgbPWM2;
    doc["rgb3"] = rgbPWM3;

    size_t outputSize = measureJson(doc)+1;
    char payload[outputSize];

    serializeJson(doc, payload, outputSize);

    if(client.publish("st_esp", payload)){
      Serial.print("Publish message: ");
      Serial.println(payload);
    }else{
        Serial.println("Something went wrong...");
    }
    
  }
}
