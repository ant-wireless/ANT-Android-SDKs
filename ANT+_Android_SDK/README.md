# Android ANT+ SDK v.P.B1 RC1 - 20 June 2014
This software development kit provides the resources needed to develop an Android application which uses the defined ANT+ profiles to communicate wirelessly with the millions of existing ANT+ devices. It includes instructions, API and documentation, and reference sample applications. The PDF _Creating ANT+ Android Applications_ explains how to get started. 

If you are trying to develop an application to use ANT wireless technology freely outside of the defined ANT+ profiles, you must use the ANT Android SDK instead.

This SDK is available from:

* http://www.thisisant.com/developer/resources/downloads/
* https://github.com/ant-wireless/ANT-Android-SDKs (subscribe as a watcher to be notified of updates)

<i>Note about member-only features: As a benefit of ANT+ Membership, members get exclusive access to new profiles and features for a given time period. For more information see http://www.thisisant.com/business/go-ant/levels-and-benefits/. Once your ANT+ Membership is activated, the ANT+ Member-only version of the library can be obtained from http://www.thisisant.com/developer/resources/downloads/ in the member-only release section. </i>

## Contents
* Creating ANT+ Android Applications
* API
  * PluginLib 3.0.0 RC1 (.jar and Javadoc)
  * FIT 11.0.0 (.jar)
* Services
  * ANT+ Plugins Service 3.0.0 RC1 (.apk)
  * Plugin Manager Launcher 1.1.0 (.apk)
* Sample Applications
  * Plugin Sampler 3.0.0 RC1(.apk and source)
  * ANT+ Demo 3.1.0 (.apk)
  * Heart Rate Grapher 2.1.0 (.apk)


## Changelog

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
