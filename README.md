Simple Android MQTT Client
======

A simple wrapper that allows you to publish and subscribe to a topic on an MQTT Server.

Include
-----

Include jcenter in your repo list:
```
    
    buildscript {
        repositories {
            jcenter()
        }
    }
```
Add to your dependencies:

```

    compile 'com.shredder:mqtt:0.0.+'
    
```

Usage
-----

Create your configuration:

```java
        MqttManagerConfig mqttConfiguration = new MqttManagerConfig() {
            @Override
            public String getHost() {
                //Hive provides a free brokering service, best change this before going live!
                return "tcp://broker.hivemq.com:1883";
            }

            @Override
            public QualityOfService getQualityOfService() {
                //RTFM for the meaning of these. FireandForget is best to start with
                return QualityOfService.FireAndForget;
                //return QualityOfService.GuaranteedDelivery;
                //return QualityOfService.GuaranteedOnceOnlyDelivery;
            }

            @Override
            public String getUniqueId() {
                //Each client that connects to the MQTT Server must have their own unique ID. 
                // Otherwise they are kicked off by the next connection with the same ID
                return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }
        };
        
```
Next create a MQTT Manager and subscribe to a topic:

```java
        
           this.mqttManager = new MqttManager(config, new MqttManager.Listener() {
                    @Override
                    public void onMessageReceived(String message, String topic) {
                        // Do something with the message.
                    }
                });
           this.mqttManager.subscribe("myinterestingtopic");

                
```

or if you are only interested in publishing and not subscribing


```java
        
           this.mqttManager = new MqttManager(config, null);
           
                
```

now you can publish to your heart's content:

```java
        
        this.mqttManager.publish("Is there anyone out there?", "myinterestingtopic");
           
                
```
