package in.techmafiya.neversettlewallpaper.Activities;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
    protected int _splashTime = 3000; // time to display the splash screen in ms


    View imageView;
    ActivityOptionsCompat options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Pair<View, String> pair1 = Pair.create(imageView, imageView.getTransitionName());

            options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(SplashActivity.this, pair1);

        }


//        Thread splashTread = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    int waited = 0;
//                    while (_active && (waited < _splashTime)) {
//                        sleep(100);
//                        if (_active) {
//                            waited += 100;
//                            if(waited>_splashTime-200)
//                            {
//
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//
//                } finally {                    {
//
//                    startIntent();
//                }
//                    finish();
//                }
//            };
//        };
//
//        splashTread.start();
    }

    public void startIntent(){


        Intent intent = new Intent(SplashActivity.this, MainActivity.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Pair<View, String> pair1 = Pair.create(imageView, imageView.getTransitionName());
//
//            ActivityOptionsCompat options = ActivityOptionsCompat.
//                    makeSceneTransitionAnimation(SplashActivity.this, pair1);
            startActivity(intent, options.toBundle());

        }
        else {
            startActivity(intent);
        }
    }


}
