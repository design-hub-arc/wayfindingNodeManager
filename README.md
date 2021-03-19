# wayfindingNodeManager

A data management program for the Wayfinding project.
The Google Drive API credentials used by this program have been invalidated, so any Drive API functionality will fail.

## TODO
* Do we still want to see the node move as we are moving the mouse, choosing a new place for it?

## Important Notice

The application will not allow you to download or upload data if you log in using a Google account without access to the Wayfinding folder on the ARCDH Google Drive.

## Prerequisites

Users wishing to run the application need just Java installed for the app to work.
If your computer doesn't have Java, you can download it [here](https://www.java.com/en/).

If you want to edit the application, you will need an IDE that supports Java. This project is built using Netbeans 8.2,
which you can download [here](https://netbeans.org/downloads/8.2/).

## Using the application

To use the Node Manager, simply download and unzip the project to your computer. 
The application is located in the build/libs folder of the project.
Double click on the WayfindingNodeManager.jar file to launch the application.
You can safely move the JAR file from this folder.

You can pan the map image by clicking and dragging it around.
You can also zoom in and out using the mouse scroll wheel.

Exit the current mode by using the ESC key

Be sure to look in the text console after you choose where to save your data set

## Editting the application

As stated above, this application was built using Netbeans 8.2 IDE, and can easily be opened in that version.
The Netbeans project this uses is built using Gradle.

## Built With

* [Netbeans 8.2](https://netbeans.org/downloads/8.2/) - IDE
* [Gradle](https://gradle.org/) - Build Tool
* [Google Drive API](https://developers.google.com/api-client-library/java/apis/drive/v3) - Used in leiu of a database to store data

## Contributing

This project is the property of the American River College Design Hub, and as such, is not open to accept contributions from people outside the company.
