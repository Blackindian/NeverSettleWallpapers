package in.techmafiya.neversettlewallpaper.Activities;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import in.techmafiya.neversettlewallpaper.FirebaseInfo.FirebaseDataBaseCheck;
import in.techmafiya.neversettlewallpaper.Permission.MarshMallowPermission;
import in.techmafiya.neversettlewallpaper.R;

import static in.techmafiya.neversettlewallpaper.R.id.contact;
import static in.techmafiya.neversettlewallpaper.R.id.logo;

public class SplashActivity extends AppCompatActivity {

    protected boolean _active = true;
    protected int _splashTime = 1000; // time to display the splash screen in ms

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        FirebaseDataBaseCheck.getDatabase();



        Thread splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    while (_active && (waited < _splashTime)) {
                        sleep(100);
                        if (_active) {
                            waited += 100;
                            if(waited>_splashTime-200)
                            {

                            }
                        }
                    }
                } catch (Exception e) {

                } finally {                    {

                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
// Pass data object in the bundle and populate details activity.
                    View logo = findViewById(R.id.logo);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(SplashActivity.this, logo, "logo");
                    startActivity(intent, options.toBundle());
                }

                    finish();
                }
            };
        };

        splashTread.start();
    }



}
