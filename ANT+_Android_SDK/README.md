# Android ANT+ SDK v.P.B5 - 31 Aug 2015
This software development kit provides the resources needed to develop an Android application which uses the defined ANT+ profiles to communicate wirelessly with the millions of existing ANT+ devices. It includes instructions, API and documentation, and reference sample applications. The PDF _Creating ANT+ Android Applications_ explains how to get started. 

If you are trying to develop an application to use ANT wireless technology freely outside of the defined ANT+ profiles, you must use the ANT Android SDK instead.

This SDK is available from:

* http://www.thisisant.com/developer/resources/downloads/
* https://github.com/ant-wireless/ANT-Android-SDKs (subscribe as a watcher to be notified of updates)

<i>Note about member-only features: As a benefit of ANT+ Membership, members get exclusive access to new profiles and features for a given time period. For more information see http://www.thisisant.com/business/go-ant/levels-and-benefits/. Once your ANT+ Membership is activated, the ANT+ Member-only version of the library can be obtained from http://www.thisisant.com/developer/resources/downloads/ in the member-only release section. </i>


## Quick Start Guide
<i> Note: Refer to the "Creating ANT+ Android Applications" (.pdf) file for more detailed info </i>

1. Ensure ANT Radio Service (ARS) is installed on device
2. Ensure ANT Plugins Service is installed on device
2. Reference ANT+ PluginLib library in your application project
3. Refer to the PluginSampler sample application to see how to:
    1. Search for ANT+ devices using MultiDeviceSearch
    2. Request access to device
    3. Subscribe to events
    4. Receive data and send commands
    5. Release device access using PccDeviceHandle


## Contents
<i> Note: Each top-level folder has a readme file which contains descriptions of the applications and tools and their intended purpose </i>

* Creating ANT+ Android Applications
* API
  * PluginLib 3.6.0 (.jar and Javadoc)
  * FIT 16.00 (.jar)
* Services
  * ANT+ Plugins Service 3.6.0 (.apk)
  * Plugin Manager Launcher 1.1.0 (.apk)
* Sample Applications
  * Plugin Sampler 3.6.0 (.apk and source)
  * ANT+ Demo 3.2.0 (.apk)
  * Heart Rate Grapher 4.0.0 (.apk)


Android ANT+ SDK Changelog
=============================================

<u>v.P.B5 - 31 Aug 2015</u>
--------------------------------------------------

> Ant+ Plugins Service and ANT+ PluginLib Changelog - v.3.6.0
> -----------------------------------------------------------------------------------
> * Fix some issues preventing connecting to combined speed and cadence bike sensors [Lib + Service]
> * Fix some crashes during searching [Service]
> * Add ability to send manufacturer specific pages on profiles that support it [Lib + Service]
> * Fix a crash when old apps request combined speed and cadence bike sensors [Service] 
> * Improve Javadoc relating to SEARCHING and DEAD states [Lib]
> * Update to AntLib 4.14.0 [Lib + Service]
> * Update to FitLib 16.00 [Lib + Service]
> 
> Ant+ Plugin Sampler - v.3.6.0
> -----------------------------------------------------------------------------------
> * Update to PluginLib v.3.6.0


<u>v.P.B4 - 20 Mar 2015</u>
--------------------------------------------------

> Ant+ Plugins Service and ANT+ PluginLib Changelog - v.3.5.0
> -----------------------------------------------------------------------------------
> * Fitness Equipment Controls released to public API [Lib]
> * Update to FitLib 14.10 (fixes date inconsistency in BPM) [Lib + Service]
> * Fix BPM resetDataAndSetTime to work properly [Service]
> * Fix a couple causes of crashes in the search UI activity [Service]
> * Fix an error connecting to bike S&C devices when saved as the preferred device [Service]
> 
> Ant+ Plugin Sampler - v.3.5.0
> -----------------------------------------------------------------------------------
> * Fitness Equipment Controls released to public API
> * Update to use PluginLib v.3.5.0


<u>v.P.B3 - 11 Feb 2015</u>
--------------------------------------------------

> Ant+ Plugins Service and ANT+ PluginLib Changelog - v.3.4.0
> -----------------------------------------------------------------------------------
> * Support for providing ANT+ sensor data to the Google FIT sensor framework. ANT+ sensors will now show up in the Google Fit API available sensor list [Service]
> * Update to AntLib 4.10.0 library (w/ Android 5.0 Lollipop intent fix) [Lib + Service]
> * Moved the MultiSearch class to the PCC package for clear discoverability (Note: This means you will need to correct your imports) [Lib]
> * Add an RSSI event to most devices [Lib + Service]
> * Add support for video remote control picture commands [Lib + Service]
> * Add a callback to determine when MultiSearch has started and if RSSI support is available [Lib + Service]
> * Add support to watch downloader for Health and Life Watch [Service]
> * Add ability to connect to establish remote controls for more than one device [Service]
> * Update to FIT 14.00 library [Lib]
> * Fix a bug causing connecting to devices to timeout sometimes [Service]
> * Fix several instances of releasing a PCC during the acquistion phase that could cause resource leaks and 'message to handler on a dead thread' logs [Lib + Service]
> * Fix an error where manufacturer specific data events can falsely trigger the request finished event [Lib]
> * Fix an error that could cause some commands to never receive a response in rare cases [Lib]
> * Fix missing progress updates on large ANTFS downloads using small block sizes [Service]
> * Fix missing requestAccess results when calling requestAccess several times in quick succession [Service]
> * Fix some errors with requesting bike speed or cadence accesses in quick succession returning non-functioning PCCs [Service]
> * Fix an error causing bike speed and cadence async scan closing to produce more than one result message [Service]
> * Fix some crashes and errors when trying to connect to a controllable device with more than one type [Service]
> * Fix controllable device dying in connection phase sometimes on Sony Ray phones [Service]
> * Fix some errors with remote control plugin preventing connecting to remotes with multiple types supported [Service]
> * Fix an error causing non-shareable already connected devices to show in the MultiDeviceSearch results [Service]
> * Fix crashing when external sources send null intents to public service endpoints [Service]
> * Fix missing return ALREADY_SUBSCRIBED when trying to connect to a device already connected to a given app when requesting access to specific device number [Service]
> * Cleaned up some logging and javadoc [Lib + Service]
>
>
> Ant+ Plugin Sampler - v.3.4.0
> -----------------------------------------------------------------------------------
> * Add example usage of new RSSI event to heart rate sample
> * Update to use PluginLib v.3.3.0
>
>
> ANT+ Heart Rate Grapher - v.4.0.0
> -----------------------------------------------------------------------------------
> * Add ability to use Google FIT framework as sensor source to demo Google FIT ANT+ sensor support and provide support for other sensor types such as BLE heart rate monitors and Android Wear devices
>
>
> ANT+ Demo - v.3.2.0
> -----------------------------------------------------------------------------------
> * Prevent searching for sensors when application is in background to improve power and resource usage


<u>v.P.B2 - 8 Aug 2014</u>
---------------------------------------------

> Ant+ Plugins Service and ANT+ PluginLib Changelog - v.3.1.0
> -----------------------------------------------------------
> * Added Remote Control profile (Audio, Video, and Generic support)
> * Added support for requesting common data pages
> * Added support for supplementary software version on common manufacturer info page
> * Moved controllable device PCC to a different package to avoid confusion with the actual PCCs(Will require fixing imports and references in old projects)
> * Changed Bike Power and Trainer commands to send result failed response instead of throwing exception
> * Updated to FitLib 12.00
> * Updated to AntLib 4.7.0
> * Updated some Javadoc to be more clear
> * Removed blood pressure requestDownloadAllHistory (replaced by DownloadMeasurements)
> * Fixed weight scale crashing with certain brands of weight scale
> * Fixed a ConcurrentModificationException occurring when using multiple async scan controllers
> * Fixed a Bike Power divide-by-zero error
> * Fixed several causes of sending messages to dead handler logs
> * Fixed missing dependency info to be consistent and more user friendly
> * Fixed async scan controllers to not log known result codes as unknown
> * Fixed BikePower logging about subscribing and unsubscribing to events
> * Fixed Request Access UI Activity from displaying if the pcc release handle was already closed
>
>
> Plugin Sampler - v.3.1.0
> ------------------------------
> * Update to new pluginLib
> * Add Remote Control support
> * Fixed crash selecting watch list item after search is closed
> * Fixed async scan demo leaking a channel in some cases
>
>
> Heart Rate Grapher - v.3.0.0
> -----------------------------
> * New modern UI
> * Added keep-screen-on feature
> * Now detects HR strap connection issues


<u>v.P.B1 RC1 - 20 June 2014</u>
---------------------------------------------

> SDK Changes
> ------------------------------------------
> * Updated:
>  * ANT+ Plugins Service (details below)
>  * ANT+ Plugin Lib (details below)
>  * ANT+ Plugin Sampler (implemented new plugin features)
>  * Creating ANT+ Android Applications (added info about multi device search)
>  * Fit Lib (update to public version, no functional changes relating to plugins)
> * Added:
>  * ANT+ Demo sample app
>  * Heart Rate Grapher demo
>  * Plugin Manager Launcher demo
> * Added readme's and changelog
> * Now releasing through GitHub in addition to thisisant.com (https://github.com/ant-wireless/ANT-Android-SDKs)
> 
> 
> Ant+ Plugins Service and ANT+ PluginLib Changelog - 18 June 2014 - v.3.0.0 RC1
> -------------------------------------------------------------
> 
> ###New Features and Improvements
> 
> * Add Multi Device Search capability. Multi Device Search is now the preferred method to do asynchronous searches. It allows searching for multiple device types at once and can retrieve additional information during the scan (This first version provides RSSI proximity values).
> * Add PccReleaseHandle object as a return value to all request access calls. The release handle allows you to release the device at any time in a deterministic way, including before the intial result is received. We recommend that all apps keep the release handle of every PCC access request made, then call close on them whenever the Pcc is no longer required or before the app shuts down. It is safe to call close at any time and multiple times.
> * Add blood pressure DownloadMeasurements function that allows live monitoring capability and downloading only 'new' files (Note: This function replaces requestDownloadAllHistory function)
> * Add blood pressure requestResetDataAndSetTime function to reset measurements and optionally set time
> * Add event to receive manufacturer specific pages on supporting profiles
> * Refactored Fitness Equipment to have a cleaner interface after all the FE-Controls additions
> * Refactored Heart Rate profile in response to developer feedback and to be more consistent with profile documentation
> * Add zero detection to heart rate data
> * Add R-R interval event to Heart Rate profile
> * Change service title to "ANT+ Plugins Service'
> * Change service icon to grey to show up better on both dark and light backgrounds
> * Add command burst functionality to bike power
> * Changed calling new functions on old versions of the service to return FAIL_PLUGINS_SERVICE_VERSION instead of UNRECOGNIZED so apps can notify users appropriately
> * Minor improvements to some function signatures and names to match profiles and common usages
> <br>
> <br>
> ###Exclusive New Features for ANT+ Members 
> ####<i>(ANT+ Members get exclusive access to new profiles for a given period of time, will be released publically at a later time)</i>
> 
> * Add Fitness Equipment Controls (including trainer, stationary bike, and open broadcast connections) support
> * Add support for bike speed and cadence new pages 4 and 5
> * Add support for battery identifier and number of batteries information to battery status event
> <br>
> <br>
> ###Bug Fixes
> 
> * Fix Bike cadence value occasionally negative and cumulative distance value resetting
> * Fix crash when closing AsyncScanController during requestDeviceAccess()
> * Fix AsyncScanController gets in bad state if it is closed quickly after opening
> * Fix several problems with accumulated values that can cause them to be out of sync with each other or reset needlessly
> * Fix several cases that could cause shutdown to leak service connections or ANR
> * Fix some cases where releasing a PCC may not release the resource in the service
> * Fix ANTFS download progress to show progress correctly
> * Fix heart rate timestamps (previous and current) to be properly synced to the same reference point so they can be used for calculations
> * Fix bike power to fire initial events
> * Fix bike power requests taking over a second to start
> * Fix bike power cadence being reported with wrong values in some cases
> * Fix bike power crank parameters request to send correct request
> * Fix watch downloader to detect FR 910XT and FR 610 regional variants
> * Fix opening more than one controllable device not working
> * Fix setting null state receiver causing exceptions (null state receivers are no longer allowed)
> * Fix SDM health status defining warning as error and vice versa
> * Fix 'legacy' profiles to always decode legacy information regardless of page number to support profile page additions
> * Fix Bike Power torque frequency sensors to perform auto-calibration more reliably
> * Fix some timestamp issues for ANTFS directories that don't keep time properly
> * Fix watch plugin multi-app access contention issues
> * Fix PluginLib and PluginService leaking service connections in certain scenarios
> * Fix some other crashes and ANRs reported on the forums and the play store
> * Fix invalid device IDs in request access from crashing service
> * Fix crashing when ARS has been 'disabled' by user in Android settings
> * Fix removed some extraneous logging
> * Other bug fixes
