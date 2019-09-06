
# Trust Technologies
![image](https://avatars2.githubusercontent.com/u/42399326?s=200&v=4)


# Description

Trust is a platform that allows building trust and security between people and technology.

**Trust-device-info** allows you to obtain a unique universal ID for each device from a set of characteristics of this device. It also allows the monitoring of device status changes, to have knowledge of the device status at all times.


# Implementation

```java
dependencies {
	implementation 'com.trust.audit:audit:1.0.1'
}

```
> See the actual version [here]([https://bintray.com/fcarotrust/trust/trustaudit](https://bintray.com/fcarotrust/trust/trustaudit)).

# Initialize

This initiation establishes by default that automatic audits are not initiated
```java
import ...
public class TestApp extends Application {
  @Override
  public void onCreate() {
        super.onCreate();
		AuditTrust.init(this);  //this enabled default audits.

  }
}
```
You can establish what type of automatic audits are what you need in your application in the following way:
```java
import ...
public class TestApp extends Application {
  @Override
  public void onCreate() {
      super.onCreate();
	  AuditTrust.init(this);
	  AuditConfiguration.setAudits(new String[]{
	      AuditConfiguration.AUDIT_SIM,      //optional
		  AuditConfiguration.AUDIT_SMS,      //optional
		  AuditConfiguration.AUDIT_CALL,     //optional
		  AuditConfiguration.AUDIT_NETWORK,  //optional
		  AuditConfiguration.AUDIT_BOOT,     //optional
		  AuditConfiguration.AUDIT_ALARM     //optional
	  });
```

> In the audits section, the types of audits that exist are reported.
  # trust-service.json file

All our services are protected by access tokens, which is why in order to generate a trust id or an audit either online or offline it is necessary to add a .json file called trust-service inside the assets folder of your android studio project. In order to obtain this file it is necessary to send the following data of your application: bundle id app name redirect uri (ex: bundle id: //auth.id) these data must be sent to app@trust.lat


# Permissions
In order for the library to work without problems, the following permissions must be added to the application.
 **Remember: This permissions are granted from user directly, additionally to write at manifest**:

```java
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.CAMERA"/>
```
> Permission of SMS Optional
> If in your application you need automatic SMS audits, it is necessary to add the following permissions
> ``` java
> <uses-permission android:name="android.permission.RECEIVE_SMS"/>
> <uses-permission android:name="android.permission.READ_SMS"/>
> <uses-permission android:name="android.permission.SEND_SMS"/>
> ```
 ## Permissions that the library owns

These are the permissions that the library currently uses:

```java
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```
## Create custom audit
### Create audit Custom Overload 1: Normal

```java
AuditCustom.createAudit("operation", "method", "result",context)
```

 * **Parameters:**
   * `operation` — Operation of the context ex: "login operation"
   * `method` — Method of the current context or operation ex: "onLoginSuccess"
   * `result` — Result of the current method  ex: "success login with Trust."
   * `context` — Context.

### Create audit Custom Overload 2: Object return Audit id

```java
AuditCustom.createAudit("operation", "method", "result", context, listener)
```

 * **Parameters:**
   * `operation` — Operation of the context ex: "login operation"
   * `method` — Method of the current context or operation ex: "onLoginSuccess"
   * `result` — Result of the current method  ex: "success login with Trust."
   * `context` — Context.
   * `listener` — listener to get the audit id of the audit.

### Create audit Custom Overload 3: Object return Audit id

```java
AuditCustom.createAudit("operation", "method", "result", object, context)
```

 * **Parameters:**
   * `operation` — Operation of the context ex: "login operation"
   * `method` — Method of the current context or operation ex: "onLoginSuccess"
   * `result` — Result of the current method  ex: "success login with Trust."
   * `object` — Object for add to result ex: new Person();
   * `context` — Context.




### Create audit Custom Overload 4: Object return Audit id

```java
AuditCustom.createAudit("operation", "method", "result", object, context, listener)
```

 * **Parameters:**
   * `operation` — Operation of the context ex: "login operation"
   * `method` — Method of the current context or operation ex: "onLoginSuccess"
   * `result` — Result of the current method  ex: "success login with Trust."
   * `object` — Object for add to result ex: new Person();
   * `context` — Context.
   * `listener` — listener to get the audit id of the audit.


### Create audit Custom offline Overload 1: Object return Audit id

```java
AuditCustom.createAuditOffline("operation", "method", "result", context)
```

 * **Parameters:**
   * `operation` — Operation of the context ex: "login operation"
   * `method` — Method of the current context or operation ex: "onLoginSuccess"
   * `result` — Result of the current method  ex: "success login with Trust."
   * `context` — Context.

### Create audit Custom offline Overload 4: Object return Audit id


```java
AuditCustom.createAuditOffline("operation", "method", "result", object, context)
```

 * **Parameters:**
   * `operation` — Operation of the context ex: "login operation"
   * `method` — Method of the current context or operation ex: "onLoginSuccess"
   * `result` — Result of the current method  ex: "success login with Trust."
   * `object` — Object for add to result ex: new Person();
   * `context` — Context.


  ### Types of automatic audits

Automatic audits are performed with device events, these are:

| Audit   | Operation   | Method  | Result  |
|---------|-------------|---------|---------|
| SMS*    |AUTOMATIC SMS AUDIT|SMS RECEIVER|RECIVED FROM NUMBER: number BODY: body of sms|
| CALL    |AUTOMATIC CALL AUDIT|CALL STATE RECEIVER|CALL IN, CALL OUT, CALL IDDLE, all with number|
| SIM     |AUTOMATIC SIM CHANGE AUDIT|SIM RECEIVER|SIM IN, SIM OUT|
| BOOT    |AUTOMATIC BOOT AUDIT|BOOT RECEIVER|DEVICE WAS TURN ON|
| REMOTE  |REMOTE AUDIT BY FIREBASE|REMOTE SERVICE|AUDIT DONE|
| NETWORK*|REMOTE NETWORK AUDIT|NETWORK CONNECTION RECEIVER |NAME CONNECTION: name , IP: ip|
| DAILY   |AUTOMATIC DAILY AUDIT|DIARY RECEIVER|DAILY AUDIT DONE|

>  *If you need to use automatic SMS audits, you need to add the necessary permissions in the application's manifest.

>  *Network audit may not be available on some devices
