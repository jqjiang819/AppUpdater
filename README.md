# AppUpater (WIP)
[![](https://img.shields.io/badge/API-21%2B-blue.svg)]() [![](https://jitpack.io/v/CharmeRyl/AppUpdater.svg)](https://jitpack.io/#CharmeRyl/AppUpdater)

This is a simple and light-weight appupdater library for Android. Now it only supports JSON format.

## How to include
**Step 1.** Add the JitPack repository to your project `build.gradle` file
```javascript
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
**Step 2.** Add the dependency to your module `build.gradle` file
```javascript
	dependencies {
	        compile 'com.github.CharmeRyl:AppUpdater:v1.0.1'
	}
```

## How to use
First add **INTERNET**, **ACCESS_NETWORK_STATE** and **WRITE_EXTERNAL_STORAGE** permissions to `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET"/>
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```
### Server side
#### JSON format
```javascript
{
  "latestVersion": "1.0.1",
  "url": "https://github.com/charmeryl/AppUpdater/releases-v1.0.1.apk",
  "releaseNotes": [
    "- Fix minor bugs",
    "- UI optimaization",
    "- Import translations"
  ]
}
```

### App side
#### Basic usage
```java
    new AppUpdater(CONTEXT)
           .setUpdateUrl(YOUR_UPDATE_SCRIPT_URL_STRING)
           .start();
```

#### Options
**setTriggerMethod(int method)**

This is to set whether the updater is started by user action or by program.

Params:
- `AppUpdater.AUTOMATIC`: Updater triggered by program, if set:
    - The update checking progress dialog will not show
    - `Hide Update` button in update dialog will function
- `AppUpdater.MANUALLY`: Updater triggered by user, if set:
    - The update checking progress dialog will show
    - No matter the version is hide by user or not, the updater dialog will show if update available.

**setDownDir(String dir)**

This is to set the save path of the downloaded `.apk` file, if not set, the `.apk` will be saved to `Download` folder in the external storage.

Format:
- `AppName/download/`: No slash at the beginning, but a slash at the end.

**setSnackbarView(View view)**

This is to set the parent view of the Snackbar.


## License
	Copyright 2016 Hugh Jiang
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	   http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
