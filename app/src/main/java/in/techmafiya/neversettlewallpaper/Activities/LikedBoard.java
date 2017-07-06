package in.techmafiya.neversettlewallpaper.Activities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import at.favre.lib.dali.Dali;
import at.favre.lib.dali.builder.live.LiveBlurWorker;
import in.techmafiya.neversettlewallpaper.Adapter.ImagesAdapter;
import in.techmafiya.neversettlewallpaper.FirebaseInfo.FirebaseDataBaseCheck;
import in.techmafiya.neversettlewallpaper.FirebaseInfo.FirebaseInfo;
import in.techmafiya.neversettlewallpaper.Models.ImageModel;
import in.techmafiya.neversettlewallpaper.Permission.MarshMallowPermission;
import in.techmafiya.neversettlewallpaper.R;
import io.paperdb.Paper;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class LikedBoard extends AppCompatActivity implements  ImagesAdapter.ImageAdapterCallback{

    private LiveBlurWorker blurWorker, blurWorker1;
    private ImagesAdapter adapter;
    public CardView cardView;
    private boolean firstCheck = false;
    private ArrayList<ImageModel> wallpaperList = new ArrayList<ImageModel>();
    private MaterialProgressBar indeterminatProgressBar;
    boolean imageLoaded = false, setImage = false;
    private TextView toolbarTextView,noSavedTextView;
    private int a = 0, height, width, positionMain;
    private RelativeLayout parentLayout;
    private View blurview, blurView1;
    private MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);
    private ImageView nosavedImage,imageForPromt, setAsWallPaperButton, placeholderImage;
    private Bitmap bitmap;
    private RecyclerView recyclerView;
    private  List<ImageModel> LikedImageList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_board);

        Paper.init(this);

        initMarshmallowPermission();
        InitUiElement();
        GetDisplaySize();

        adapter = new ImagesAdapter(LikedBoard.this, wallpaperList);
        adapter.setCallback(this);
        getDataFromDB();
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new SpacesPaddingItemDecoration(10));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }

    public  void InitUiElement(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        parentLayout = (RelativeLayout) findViewById(R.id.blurlayout);
        blurview = (View) findViewById(R.id.blurView);
        blurView1 = (View) findViewById(R.id.blurView2);

        toolbarTextView = (TextView) toolbar.findViewById(R.id.toolbarTV);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Lato-Bold.ttf");
        toolbarTextView.setTypeface(custom_font);


        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        blurWorker = Dali.create(LikedBoard.this).liveBlur(parentLayout, blurview).downScale(8).assemble(true);
        blurWorker1 = Dali.create(LikedBoard.this).liveBlur(parentLayout, blurView1).blurRadius(3).downScale(3).assemble(true);
        blurWorker.updateBlurView();
        blurWorker1.updateBlurView();

        cardView = (CardView) findViewById(R.id.noSavedLayout);
        nosavedImage = (ImageView ) findViewById(R.id.imageview);
        noSavedTextView = (TextView) findViewById(R.id.textview);

        noSavedTextView.setTypeface(custom_font);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        if (Build.VERSION.SDK_INT > 22) {
            recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    blurWorker1.updateBlurView();
                    blurWorker.updateBlurView();
                }
            });
        } else {
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    blurWorker1.updateBlurView();
                    blurWorker.updateBlurView();
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
        }

    }

    public void getDataFromDB(){
        LikedImageList = Paper.book().read(FirebaseInfo.likedImagesArray,new ArrayList<ImageModel>());

        if(!LikedImageList.isEmpty()){
            for(ImageModel imageModel : LikedImageList){
                if(firstCheck == false){
                    firstCheck= true;
                    cardView.setVisibility(View.GONE);
                }
                wallpaperList.add(imageModel);
                adapter.notifyDataSetChanged();
            }

        }
    }

    public void listViewCallingMethods(int position, Drawable previewImage) {
        blurWorker1.updateBlurView();
        blurView1.setVisibility(View.VISIBLE);

        positionMain = position;
        AlertDialog.Builder mWallpaperDialog = wallPaperPromt();

        imageLoaded = false;
        setImage = false;

        GetDisplaySize();
        placeholderImage.setImageDrawable(previewImage);
        Glide.with(LikedBoard.this).
                load(wallpaperList.get(position).getF())
                .asBitmap()
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        imageLoaded = true;
                        indeterminatProgressBar.setVisibility(View.GONE);
                        placeholderImage.setVisibility(View.GONE);
                        setAsWallPaperButton.setVisibility(View.VISIBLE);
                        bitmap = resource;
                        if (setImage) {
                            setWallpaper();
                        }
                        return false;
                    }
                })
                .into(imageForPromt);



        mWallpaperDialog
                .setCancelable(true)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        blurView1.setVisibility(View.GONE);
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        blurView1.setVisibility(View.GONE);
                    }
                });


        AlertDialog alertDialog = mWallpaperDialog.create();
        alertDialog.show();

        alertDialog.getWindow().setLayout(width - (width / 100) * 20, height - (height / 100) * 20);

    }

    void initMarshmallowPermission() {


        if (!marshMallowPermission.checkPermissionForStorage()) {
            marshMallowPermission.requestPermissionForStorage();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case android.R.id.home : onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private AlertDialog.Builder wallPaperPromt() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.item_promt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(promptsView);


        imageForPromt = (ImageView) promptsView
                .findViewById(R.id.promtImageView);

        placeholderImage = (ImageView) promptsView.findViewById(R.id.placeHolderImage);

        setAsWallPaperButton = (ImageView) promptsView
                .findViewById(R.id.setWallpaper);

        indeterminatProgressBar = (MaterialProgressBar) promptsView.findViewById(R.id.progressbar);

        setAsWallPaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage = true;
                if (imageLoaded) {
                    setWallpaper();
                } else {
                    Toast.makeText(LikedBoard.this, "Image Loading", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setAsWallPaperButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(LikedBoard.this, "Set as Wallpaper", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        return alertDialogBuilder;
    }

    public void GetDisplaySize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
    }

    private Uri SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/NeverSettleWalls");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".png";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setWallpaper() {
        try {
            imageForPromt.buildDrawingCache();

            Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
            intent.setDataAndType(SaveImage(bitmap), "image/png");
            intent.putExtra("mimeType", "image/png");
            startActivity(Intent.createChooser(intent, getString(R.string.menu_wallpaper)));

        } catch (Exception e) {
            Log.d("Wallpaper", "" + e);
            String reqString = Build.MANUFACTURER
                    + " " + Build.MODEL + " " + Build.VERSION.RELEASE
                    + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();
            FirebaseDataBaseCheck.getDatabase().getReference().child("Logs").push().setValue(System.currentTimeMillis() + "  " + reqString + " " + e);
            Toast.makeText(LikedBoard.this, "Error setting Wallpaper", Toast.LENGTH_SHORT).show();
            Toast.makeText(LikedBoard.this, "sorry for incontinence our developers will fix this issue soon", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void wallPaperImagePressed(int position, Drawable previewImage) {
        listViewCallingMethods(position, previewImage);

    }

}

class SpacesPaddingItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesPaddingItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

    }
}



