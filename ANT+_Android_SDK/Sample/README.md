# Android ANT+ SDK Samples
This folder contains samples on how to use the Android ANT+ API.

<i>Note about member-only features: As a benefit of ANT+ Membership, members get exclusive access to new profiles and features for a given time period. For more information see http://www.thisisant.com/business/go-ant/levels-and-benefits/. Once your ANT+ Membership is activated, the ANT+ Member-only version of the library can be obtained from http://www.thisisant.com/developer/resources/downloads/ in the member-only release section. </i>

## Contents
* Plugin Sampler (.apk and source) - shows how to connect to all device types through various mechanisms, subscribe to receive data from all events, send all commands, and shows how to handle device state changes and errors requesting access such as the scenario where the ANT Radio Service and/or ANT USB Service have not been installed yet. This is the de-facto reference code for using the ANT+ plugins. Refer to special note above for information concerning accessing member-only features. Note: We recommend all applications use the new multi device search to search for devices as it is more efficient, so start with the multi-device search sample first.
* ANT+ Demo (.apk) - shows an example of a very simple fitness monitoring application displaying heart rate, stride-based speed and distance monitor, and weight scale data. The demo code is not released for this app as some people were taking the source, rebreanding it and putting it on the play store. We hope for a higher quality app experience than that from developers and don't need a bunch of clones all over the play store.
* Heart Rate Grapher (.apk) - shows an example of monitoring heart rate data over long periods of time using a foreground service.

## Changelog
See changelog in ANT SDK root folder readme