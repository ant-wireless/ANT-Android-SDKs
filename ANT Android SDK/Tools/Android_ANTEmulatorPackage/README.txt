ANT Android Emulator Bridge README

------------------------------

CONTENTS:

1. OVERVIEW
2. REQUIREMENTS
3. PC APPLICATIONS
4. ANDROID APPLICATIONS
5. CREATE AN ANT ENABLED ANDROID EMULATOR
6. CONNECT TO THE PC USB STICK

------------------------------

1. OVERVIEW

   This readme provides the steps required to create an Android emulator with 
   ANT radio support by using the ANT USB stick on a Windows PC.  Developers 
   can then develop ANT enabled applications using the Android emulator and the
   ANT Emulator Configuration Application:
      http://www.thisisant.com/developer/resources/downloads/

   For further information on ANT, visit:
      http://www.thisisant.com

2. REQUIREMENTS

   * Android SDK (http://developer.android.com/sdk/installing.html)
   * .NET 3.5
   * Supported ANT USB stick
   * ANT Radio Service Android application (version 2.6 or newer)
   * ANT Emulator Configuration Application

3. PC APPLICATIONS

   * ANT Android Emulator Bridge
      - Listens for connections from the Android emulator and bridges the 
        traffic to/from an ANT USB stick.

4. ANDROID APPLICATIONS

   * ANT Radio Service
      - The same ANT Radio Service as is used on a physical device to communicate
        with the ANT hardware.
      - Available on the Android Market or as part of the Android API package 
        (http://www.thisisant.com/developer/resources/downloads/).

   * ANT Emulator Configuration utility
      - Configures what IP and what port the service should connect through. 
        Set this to the network-visible IP address of the machine running the 
        ANT Android Emulator Bridge, and the port it is listening on.
      - Available as part of the Android API package 
        (http://www.thisisant.com/developer/resources/downloads/).

5. CREATE AN ANT ENABLED ANDROID EMULATOR

   1. Create a new virtual device (or open an existing one).
         In Eclipse: 
           Menu > Window > AVD Manager > New > Target
   2. Install the ANT Radio Service APK with adb.
         <adb> install Android_AntRadioService_<VERSION>.apk
   3. Install the ANT Emulator Configuration APK with adb.
         <adb> install ANT_Emulator_Config.apk 

6. CONNECT TO THE PC USB STICK

   1. Initialize the ANT Android Emulator Bridge (see USERGUIDE.txt).
   2. Initialize an Android emulator with ANT radio support (above).
   3. Run the ANT Emulator Configuration app on the emulator and enter: 
        IP Address: Your computer's network visible IP address.
        Port: The same port number as the ANT Android Emulator Bridge.
   4. Click reconnect, and exit the program.
   5. You may then run your ANT enabled application in the Android emulator.
   6. The app will start the emulator service which will connect to the ANT
      USB through the Bridge app.  The status in the Bridge will display:
         "Found emulator"

