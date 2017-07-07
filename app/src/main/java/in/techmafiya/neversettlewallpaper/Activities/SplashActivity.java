package in.techmafiya.neversettlewallpaper.Activities;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.IOException;

import in.techmafiya.neversettlewallpaper.FirebaseInfo.FirebaseDataBaseCheck;
import in.techmafiya.neversettlewallpaper.Permission.MarshMallowPermission;
import in.techmafiya.neversettlewallpaper.R;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
import tyrantgit.explosionfield.ExplosionField;

import static in.techmafiya.neversettlewallpaper.R.id.contact;
import static in.techmafiya.neversettlewallpaper.R.id.logo;
import static in.techmafiya.neversettlewallpaper.R.id.progressbar;

public class SplashActivity extends AppCompatActivity {

    protected boolean _active = true;
    protected int _splashTime = 3000; // time to display the splash screen in ms

    public static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    View imageView;
    private ExplosionField mExplosionField;
    LinearLayout layout;
    MaterialProgressBar mpb;
    String TAG = "Splash";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);



        mExplosionField = ExplosionField.attach2Window(this);
        layout = (LinearLayout) findViewById(R.id.linearlayout);
        mpb = (MaterialProgressBar) findViewById(R.id.progressbar);
        TextView textView = (TextView) findViewById(R.id.textView);
        TextView textView1 = (TextView) findViewById(R.id.textView1);
        TextView textView2 = (TextView) findViewById(R.id.textView2);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Lato-Bold.ttf");
        textView.setText("-By ");
        textView2.setText(" Fan ");
        textView.setTypeface(custom_font);
        textView1.setTypeface(custom_font);
        textView2.setTypeface(custom_font);

        FirebaseDataBaseCheck.getDatabase();
        imageView= findViewById(R.id.logo);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                mExplosionField.explode(layout);
                mExplosionField.explode(mpb);
            }
        }, 2000);

        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(1000);
                        if (_active) {
                            waited += 1000;
                            if(waited>_splashTime-1000)
                            {

                            }
                        }
                    }
                } catch (Exception e) {

                } finally {                    {

                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                    finish();
                }
            };
        };

        splashTread.start();
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.e(TAG, "EL DISPOSITIVO NO SOPORTA GOOGLE PLAY SERVICES");
                finish();
            }
            return false;
        }
        return true;
    }

}
