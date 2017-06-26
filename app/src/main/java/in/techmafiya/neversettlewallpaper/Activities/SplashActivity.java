package in.techmafiya.neversettlewallpaper.Activities;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import in.techmafiya.neversettlewallpaper.Permission.MarshMallowPermission;
import in.techmafiya.neversettlewallpaper.R;

public class SplashActivity extends AppCompatActivity {

    MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initMarshmallowPermission();
        textView = (TextView ) findViewById(R.id.textView1);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WallpaperManager myWallpaperManager
                        = WallpaperManager.getInstance(getApplicationContext());
                try {

                    if(marshMallowPermission.checkPermissionForWallpaper()){
                        Bitmap bitmap = BitmapFactory.decodeResource(SplashActivity.this.getResources(), R.drawable.placeholder);
                        myWallpaperManager.setBitmap(bitmap);
                        Toast.makeText(SplashActivity.this,"Wall paper changed ",Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

    }

    void initMarshmallowPermission() {


        if (!marshMallowPermission.checkPermissionForWallpaper()) {
            marshMallowPermission.requestPermissionForWallpaper();
        }

    }

}
