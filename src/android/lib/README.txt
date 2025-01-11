We offer several ways to use the librarys in Android. Choose the way you like.

1. AAR 
You can just add the aar file to a directory such as /libAars. This needs gradle support. 
And add following content to your build.gradle file :  
  repositories {
    flatDir {
        dirs 'libAars'
    }
  }

  dependencies {
	compile(name: 'PAX_POSLinkAndroid_20XXXXXX', ext: 'aar')
  }

If you use this aar in your library project, you need to add 
	flatDir {
			project(':your_lib_project_name').file('libs')
	}
to your app project, too.

2. JAR
Traditional. Just use all the jars as librarys. And the so file under armeabi should be imported too to support custom scan codec.
After v1.01.03_20180409, the PaxCommomLib_V20170524.jar was removed and GLComm_V1.02.00_20180402.jar is used.

3. Parts of The JAR Files.(Deprecated after 2017/11/23)

In new version, the logback-android-classic-1.1.1-6.jar, logback-android-core-1.1.1-6.jar and slf4j-api-1.7.21.jar dependencies are removed.
Please remove them from your dependencies if you don't need them.


------------------
External Lib

1. The external lib Commonlib_20150420_dex has been removed!
You do not need to add it to your application anymore, just remove it. --2017/05/25

2. casio_vr7000_addon_1_0_3/casioregdevicelibrary_dex.jar
This for casio device vr7000. Only when you use this device that copy it to your asset dir of Android project.

3. casio_vx100_addon_3_8/caiosdevice_dex.jar
This for casio device vx100. Only when you use this device that copy it to your asset dir of Android project.
------------------
Now in the Android some other librarys are used, the lib initialization must be called on the start of the Android application.

The directory will be created automatically.

Call LogSetting.setOutputPath() and LogSetting.setLogFileName() first.
And then call the 
	POSLinkAndroid.init(getApplicationContext());
	
For example:
public class MainApplication extends Application{
	@Override
	public void onCreate() {
		super.onCreate();
		POSLinkAndroid.init(getApplicationContext());
	}
}
------------------
To use USB 

1. Because the USB implementation is in Android, the initialization way of POSLink changes.

Just call 
	POSLink poslink = POSLinkCreator.createPoslink(context);
	// Or POSLink poslink = new POSLink(context);
	
to replace 
	POSLink poslink = new POSLink();
	

2. 
Add
    <uses-feature android:name="android.hardware.usb.host"/>
to your AndroidManifest.xml 