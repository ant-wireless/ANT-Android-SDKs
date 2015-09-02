# Android ANT SDK v.C.B6 - 31 Aug 2015
This software development kit provides the resources needed to develop an Android application which uses ANT technology to communicate wirelessly between ANT enabled devices. It includes instructions, API and documentation, and reference sample applications. The PDF _Creating ANT Android Applications_ explains how to get started. 

If you are trying to develop an application to connect to ANT+ devices on the ANT+ network, you must use the ANT+ Android SDK instead.

This SDK is available from:

* http://www.thisisant.com/developer/resources/downloads/
* https://github.com/ant-wireless/ANT-Android-SDKs (subscribe as a watcher to be notified of updates)


## Quick Start Guide
<i> Note: Refer to the "Creating ANT Android Applications" (.pdf) file for more detailed info </i>

1. Ensure ANT Radio Service (ARS) is installed on device
2. Reference ANTLib library in your application project
3. Refer to the Acquire Channels Sample to see how to:
    1. Bind to ARS
    2. Acquire channels
    3. Configure and open channels
    4. Send and receive data
    5. Release channels


## Contents
<i> Note: Each top-level folder has a readme file which contains descriptions of the applications and tools and their intended purpose </i>

* Creating ANT Android Applications (.pdf)
* API
  * ANT Lib 4.14.0 (.jar and Javadoc)
* Services
  * ANT Radio Service 4.14.0 (.apk)
  * ANT USB Service 1.4.0 (.apk)
* Sample Applications
  * Acquire Channels Sample 1.2.0 (.apk and source)
  * Background Scan Sample 1.1.0 (.apk and source)
* Tools
  * ANT Service Settings App 2.0.0 (.apk)
  * Emulator Bridge Tool 2.0.0 (.apk and PC app)
  * ANT Support Checker 1.2.0 (.apk)
  
If you have any questions about developing ANT applications or need help visit the ANT developer forums at http://www.thisisant.com/forum/ or become an ANT+ Member for direct support.


## Changelog

Android ANT SDK Changelog
==========================================

<u>v.C.B6 - 31 Aug 2015</u>
-----------------------------------------
> ANT Radio Service and ANTLib - v.4.14.0
> -----------------------------------------------------
> * IMPORTANT BEHAVIOUR CHANGE: Add Channel Not Available Exception if getAdapterInfo is called before service is not initialized instead of returning blank list [ARS + AntLib]
> * IMPORTANT BEHAVIOUR CHANGE: Only perform error recovery channel release when channel closes with an ack in progress on adapters with firmware affected by this bug (see Javadoc for startSendAcknowledgedData) [ARS]
> * Added logic to ignore built-in ANT adapters which never properly initialize making legacy apps never able to access USB adapters [ARS]
> * Add support for fast channel initiation feature in extended assignment [ARS + AntLib]
> * Add support for search uplink optimization feature [ARS + AntLib]
> * Add finalizer to ANT channel to prevent apps from leaking channels [AntLib]
> * Improve Javadoc explanations of search time outs [AntLib]
> * Fix search priority capability being reported properly in capabilities [ARS + AntLib]
> * Fix some exceptions during service intialization and shutdown [ARS]
> * Always send ChannelsAvailable broadcast even if no apps are actively using channels [ARS]


<u>v.C.B5 - 26 Feb 2015</u>
-----------------------------------------
> ANT Radio Service and ANTLib - v.4.12.0
> -----------------------------------------------------
> * Fix support for 3rd party adapter providers relying on implicit bind [ARS]
> * Add capability to use continuous scan mode on adapters that support it [ARS + ANTLib]


<u>v.C.B4 - 6 Feb 2015</u>
-----------------------------------------
> ANT Radio Service and ANTLib - v.4.10.0
> -----------------------------------------------------
> * Fix Android 5.0 intent compatibility (*If you compile your app targeting API >=21 you must use ANTLib >=4.9.0 or you will see 'java.lang.IllegalArgumentException: Service Intent must be explicit'*) [ANTLib]
> * Fix Android 5.0 permissions compatibility (i.e. Play Store error 505) [ARS]
> * Add support for obtaining information about attached ANT adapters and their capabilities [ARS + ANTLib]
> * Add support to obtain a channel from a specific ANT adapter [ARS + ANTLib]
> * Add support for search priority control [ARS + ANTLib]
> * Add support to use multiple private networks simultaneously on adapters that support more network slots [ARS]
> * Add ANT_NOT_ENABLED channel not available reason for when phones start providing ANT as a system setting [ARS + ANTLib]
> * Add proper equals() support to ChannelID class to easily compare Channel IDs) [ANTLib]
> * Fix RSSI values incorrect on some Qualcomm based platforms [ARS]
> * Fix a rare deadlock when releasing channels) [ANTLib]
> * Fix an IllegalArgumentException when intitializing the ANT USB Service [ARS]
> * Fix a possible deadlock when shutting down with an active burst [ARS]
> * Fix a thread leak when shutting down when a channel is being cleaned up [ARS]
> * Fix global ref table resource leak when releasing channels [ARS]
> * Fix NEW_CHANNELS_AVAILABLE intent to be sent out everytime a new adapter is attached (i.e. when a USB stick is inserted) [ARS]
> * Fix hang when calling burst on a closed channel [ARS]
> * Fix NullPointerException occuring when USB Service is force stopped [ARS]
> * Cleaned up some logging and javadoc [ARS + ANTLib]
> * Add support for some new private network keys [ARS]
>
>
> ANT USB Service - v.1.4.0
> -----------------------------------------------------
> * Fix Android 5.0 permissions compatibility (i.e. Play Store error 505)
> * Change service icon to grey to show up better on both dark and light backgrounds
> * Fix an ANR caused by the removal of the USB stick is removed at the same time as acknowledging the USB device permission popup
> * Fix NullPointerException processing ANT events when the ARS is shutting down
> * Fix multiple USB device permission popups for the same device
> * Cleaned and fixed up some logging
>
>
> Acquire Channels Sample 1.2.0
> -----------------------------------------------------
> * Updated to AntLib 4.10.0
>
>
> Background Scan Sample 1.1.0
> -----------------------------------------------------
> * Updated to AntLib 4.10.0
> * Fix a possible IllegalStateException doing list updates


v.C.B3 - 6 Aug 2014
-----------------------------------------
> ANT Radio Service and ANTLib - v.4.7.0
> -----------------------------------------------------
> * Fix service to be visible on Play Store for phones without bluetooth again
> * Fix null pointer crash in ChannelCloseController.onChannelMessage() when adapter changes state
> * Fix burst function to return earlier when channel drops to search instead of waiting the full search timeout period
> * Fix required and desired capabilities to work again when requesting channels (was broken since 4.4.0)
> * Note: AntLib is unchanged since 4.6.0 but version number was updated to keep in sync with service


v.C.B2 - 9 July 2014
-----------------------------------------
> SDK Changes
> ------------------------------------------
> * Clarified some info in readmes
> * Renames to remove spaces
> 
> 
> ANT Radio Service and ANTLib - 7 July 2014 - v.4.6.0
> -----------------------------------------------------
> * Added workaround for some Wilink 7 based phones (such as the Xperia Ray) not sending close event after search timeout
> * Released to Play Store, removed 'RC' tag


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
> ANT USB Service - 18 June 2014 - v.1.3.0
> -------------------------------------------------------------
> * Fix USB access request repeatedly popping up
> * Fix some issues handling multiple sticks plugged in simultaneously
> * Fix some crashes
> * Change service icon to grey to show up better on both dark and light backgrounds
