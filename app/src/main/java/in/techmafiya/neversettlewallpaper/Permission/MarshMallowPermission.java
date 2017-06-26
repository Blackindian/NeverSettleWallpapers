package in.techmafiya.neversettlewallpaper.Permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by saiso on 03-02-2017.
 */

public class MarshMallowPermission {


    public static final int STORAGE_Phone_PERMISSION_REQUEST_CODE = 4;
    public static final int INTERNET_PERMISSION_REQUEST_CODE = 5;
    public static final int WALLPAPER_PERMISSION_REQUEST_CODE = 6;
    Activity activity;

    public MarshMallowPermission(Activity activity) {
        this.activity = activity;
    }


    public boolean checkPermissionForStorage() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkPermissionForWallpaper() {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.SET_WALLPAPER);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    public void requestPermissionForWallpaper(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.SET_WALLPAPER)){
            Toast.makeText(activity, "set Wallpaper permission needed. Please allow in App Settings for calling veggiefactory.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.SET_WALLPAPER},WALLPAPER_PERMISSION_REQUEST_CODE);
        }
    }

    public void requestPermissionForStorage(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(activity, "Internet permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},STORAGE_Phone_PERMISSION_REQUEST_CODE);
        }
    }
}
