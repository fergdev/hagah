# Common template for KMP app
Template to set up a KMP app.

## Setup
### Config steps

Change the config to suite your app name.
### Update package
In your source folders rename your packages to be the same as your artifact id.

### IOS
Change the file to suite your configuration.

`iosApp/Configuration/Config.xconfig`

### Generate app icons
Goto https://icon.kitchen/ and generate icons

# About Libraries
Run this command to generate libs
./gradlew composeApp:exportLibraryDefinitions -PaboutLibraries.exportPath=src/commonMain/composeResources/files


