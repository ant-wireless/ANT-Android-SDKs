# Android ANT SDK v.C.B1 RC1 - 20 June 2014
This software development kit provides the resources needed to develop an Android application which uses ANT technology to communicate wirelessly between ANT enabled devices. It includes instructions, API and documentation, and reference sample applications. The pdf _Creating ANT Android Applications_ explains how to get started. 

If you are trying to develop an application to connect to ANT+ devices on the ANT+ network, you must use the ANT+ Android SDK instead.

This SDK is available from:

* http://www.thisisant.com/developer/resources/downloads/
* https://github.com/ant-wireless/ANT-Android-SDKs (subscribe as a watcher to be notified of updates)


## Contents
* Creating ANT Android Applications (.pdf)
* API
  * ANT Lib 4.5.0 RC4 (.jar and javaDoc)
* Services
  * ANT Radio Service 4.5.0 RC7 (.apk)
  * ANT USB Service 1.3.0 (.apk)
* Sample Applications
  * Acquire Channels Sample 1.1.0 (.apk and source)
  * Background Scan Sample 1.0.0 (.apk and source)
* Tools
  * ANT Service Settings App 2.0.0 (.apk)
  * Emulator Bridge Tool 2.0.0 (.apk and PC app)
  * ANT Support Checker 1.2.0 (.apk)
  
If you have any questions about developing ANT applications or need help visit the ANT developer forums at http://www.thisisant.com/forum/ or become an ANT+ Member for direct support.


## Changelog

Android ANT+ SDK Changelog
==========================================
v.C.B1 RC1 - 20 June 2014
------------------------------------------

> SDK Changes
> ------------------------------------------
> * Updated:
>  * ANT Radio Service (details below)
>  * ANTLib (details below)
>  * ANT USB Service (details below)
> * Added:
>  * ANT Service Settings App
>  * Emulator Bridge Tool
>  * ANT Support Checker
> * Added readme's and changelog
> * Now releasing through GitHub in addition to thisisant.com (https://github.com/ant-wireless/ANT-Android-SDKs)
> 
> 
> ANT Radio Service and ANTLib - 18 June 2014 - v.4.5.0 (RC7, RC4)
> -------------------------------------------------------------
> ###New Features and Improvements
>
> * Added a new ChannelNotAvailableReason - ANT_DISABLED_AIRPLANE_MODE_ON to indicate that airplane mode must be disabled before ANT can be used
> * Channels interface will now attempt to automatically enable disabled adapters
> * Add support for using private network keys
> * Added Bluetooth permissions to service package based on feedback from Google since some phones require interacting with bluetooth stack for ANT communication
> * Change service icon to grey to show up better on both dark and light backgrounds
> * ANT+ channel RF frequency of 2457 is now reserved only for use by channels on the ANT+ network key
> * Removed requestSerialNumber() as it is unused by current multi-mode implementations
> <br>
> <br>
> ###Bug Fixes
>
> * Fix channel spontaneously closing when re-opening channels quickly
> * Fix sending multiple channel death messages when channel dies
> * Fix respect airplane mode on Android 4.2+ devices where manufacturer has not explicitly specified airplane behaviour
> * Fix error recovery looping indefinitely
> * Fix service is properly disabled when airplane mode is enabled suring service intializing
> * Fix hang during service startup when airplane mode is enabled on some devices
> * Fix certain devices shut down or refuse to enable ANT if bluetooth is on when airplane mode is enabled
> * Fix premature erroneous burst result can be received before burst is finished when acknowledged messages are being processed on other channels
> * Fix channel messages still being sent to callback after calling release
> * Fix possible exception processing concurrent bursts on multiple channels
> * Fix burst message error on some devices that causes only partial part of message to be sent but still reports success
> * Fix channel not able to be acquired again if it is closed when an ack or burst transfer is in progress
> * Fix hasExtendedData returning true in some cases where extended data does not exist
> * Fix some adapters with non-standard power levels could change to non-default power levels when channels are assigned
> * Fix extraneous debug logging whenever bursts occur
> * Fix exception using legacy interface when interface is not claimed
> * Fix some issues with old applications using legacy interface crashing or hanging
> * Fix legacy enable/disable requests sent to shutdown adapter leave adter in error state permanently
> * Fix legacy calls to enable and disable not being processed during service initializing state
> * Fix exception that could occur with multiple USB sticks plugged in
> * Fix exception when rapidly unplugging and plugging-in USB sticks
> * Fix eventBuffering calls failing in legacy interface
> 
> ANT USB Service - 18 June 2014 - v.4.5.0 (RC7, RC4)
> -------------------------------------------------------------
> * Fix USB access request repeatedly popping up
> * Fix some issues handling multiple sticks plugged in simultaneously
> * Fix some crashes
> * Change service icon to grey to show up better on both dark and light backgrounds