EOBR
====

Research Project

This android application is to collect the data of drayage trucks in every interval to analyze the pollution and congestion.

The application contains 5 screens: initial screen, new trip screen, status screen, note screen, and detail status screen.

It uses GPS and GeoCoder to get the current location of truck and convert it to the address. 

The data is collected to sqlite in this format: type of trip, action, latitude, longitude, time, note, type of stamp, truck id, and trip id. Also, it records a voice during the trip when a truck driver wants to record. 

Collected data temporalily is in internal storage, a device itself. These data are sent after each trip. If the application fails to send, it saves in internal storage until it sends. These data are managed by a resource manager, so that device has enough space for future trips.













