# Android ANT+ Services
The included services are applications that run in the background which allow communication between apps and the ANT hardware. Services required by your ANT+ enabled application to run include ANT+ Plugins and ANT Radio Service. These services are available for download through the Play Store and may be pre-installed by device manufacturers, they are also available in the SDKs here and https://github.com/ant-wireless/ANT-Android-SDKs/tree/master/ANT_Android_SDK/Services, respectively.

For more information on these services and which Android devices support or can support ANT communication refer to http://www.thisisant.com/developer/ant/ant-in-android/.

## Contents
* ANT+ Plugins Service (.apk) - Must be installed to communicate with ANT+ devices. Controls searching, maintaining connections, decoding data, sending commands, and sharing data for all connected applications. Note: Requires the ANT Radio Service be installed (obtainable from Google Play or the Android ANT SDK).
* Plugin Manager Launcher (.apk) - Optional service to launch the plugin manager which currently provides functionality to modify default search settings and view and modify the ANT+ Plugins Service Device Database. External access to read and write to the database will most likely be provided in the future.

## Changelog
See changelog in ANT SDK root folder readme