package in.techmafiya.neversettlewallpaper.Activities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.koushikdutta.ion.Ion;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import at.favre.lib.dali.Dali;
import at.favre.lib.dali.builder.live.LiveBlurWorker;
import in.techmafiya.neversettlewallpaper.Adapter.ImageAdapter;
import in.techmafiya.neversettlewallpaper.Adapter.ImagesAdapter;
import in.techmafiya.neversettlewallpaper.AndroidBmpUtil;
import in.techmafiya.neversettlewallpaper.FirebaseInfo.FirebaseDataBaseCheck;
import in.techmafiya.neversettlewallpaper.FirebaseInfo.FirebaseInfo;
import in.techmafiya.neversettlewallpaper.Permission.MarshMallowPermission;
import in.techmafiya.neversettlewallpaper.Models.ImageModel;
import in.techmafiya.neversettlewallpaper.R;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements ImagesAdapter.ImageAdapterCallback {


    private LiveBlurWorker blurWorker, blurWorker1;
    private ImagesAdapter adapter;
    private boolean firstCheck = false;
    private ArrayList<ImageModel> wallpaperList = new ArrayList<ImageModel>();

    boolean imageLoaded = false, setImage = false;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView toolbarTextView;
    private int a = 0, height, width, positionMain;
    private RelativeLayout parentLayout;
    private View blurview, blurView1;
    private MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);
    private ImageView imageForPromt, setAsWallPaperButton;
    private SimpleDraweeView mSimpleDraweeView;
    private Bitmap bitmap;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Paper.init(this);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window w = getWindow();
//            w.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//            w.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }

        initMarshmallowPermission();

        initUiElements();

        GetDisplaySize();

        fabButton();

        adapter = new ImagesAdapter(MainActivity.this, wallpaperList);
        adapter.setCallback(this);
        UpdateFromDatabase();
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.hasFixedSize();
        recyclerView.addItemDecoration(new SpacesItemDecoration(15));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


    }

    public void FullScreencall() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    void initUiElements() {

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
        blurWorker = Dali.create(MainActivity.this).liveBlur(parentLayout, blurview).downScale(8).assemble(true);
        blurWorker1 = Dali.create(MainActivity.this).liveBlur(parentLayout, blurView1).blurRadius(3).downScale(3).assemble(true);
        blurWorker.updateBlurView();
        blurWorker1.updateBlurView();
//        setSupportActionBar(toolbar);
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_oneplus_white);
//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create global configuration and initialize ImageLoader with this config

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                UpdateFromDatabase();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


//        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this,
//                recyclerView, new ClickListener() {
//            @Override
//            public void onClick(View view, final int position) {
//                //Values are passing to activity & to fragment as well
//                Toast.makeText(MainActivity.this, "Single Click on position :"+position,
//                        Toast.LENGTH_SHORT).show();
//                listViewCallingMethods(position);
//            }
//
//            @Override
//            public void onLongClick(View view, int position) {
//                Toast.makeText(MainActivity.this, "Long press on position :"+position,
//                        Toast.LENGTH_LONG).show();
//            }
//        }));.


        if(Build.VERSION.SDK_INT > 22) {
            recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    blurWorker1.updateBlurView();
                    blurWorker.updateBlurView();
                }
            });
        }else{
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

    public void listViewCallingMethods(int position, Drawable previewImage) {
        blurWorker1.updateBlurView();
        blurView1.setVisibility(View.VISIBLE);

        positionMain = position;
        AlertDialog.Builder mWallpaperDialog = wallPaperPromt();

        imageLoaded = false;
        setImage = false;




        GetDisplaySize();
        imageForPromt.setImageDrawable(previewImage);
        Glide.with(MainActivity.this.getApplicationContext())
                .load(wallpaperList.get(position).getF())
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        imageForPromt.setImageBitmap(resource);
                        imageLoaded = true;
                        bitmap = resource;
                        imageForPromt.setImageBitmap(resource);
                        if (setImage) {
                            setWallpaper();
                        }
                    }
                });


//                Ion.with(MainActivity.this)
//                        .load(wallpaperList.get(position).getF())
//                        .withBitmap()
//                        .placeholder(previewImage)
//                        .intoImageView(imageForPromt);


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

    public void UpdateFromDatabase() {
        blurWorker.updateBlurView();
        Query query;
        if (firstCheck == false) {
            query = FirebaseDataBaseCheck.getDatabase().getReference().child(FirebaseInfo.NodeUsing).limitToFirst(Paper.book().read(FirebaseInfo.howManyNodes, 10));
            if (Paper.book().read(FirebaseInfo.howManyNodes, 0) == 0) {
                Paper.book().write(FirebaseInfo.howManyNodes, 10);
            }
            firstCheck = true;
        } else if (Paper.book().read(FirebaseInfo.lastNodeFetched, "").equals("")) {
            query = FirebaseDataBaseCheck.getDatabase().getReference().child(FirebaseInfo.NodeUsing).limitToFirst(10);
        } else {
            query = FirebaseDataBaseCheck.getDatabase().getReference().child(FirebaseInfo.NodeUsing).orderByKey().startAt(Paper.book().read(FirebaseInfo.lastNodeFetched, "") + 1).limitToFirst(10);
            int count = Paper.book().read(FirebaseInfo.howManyNodes, 0);
            Paper.book().write(FirebaseInfo.howManyNodes, count + 10);
        }

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ImageModel wallpapaer = dataSnapshot.getValue(ImageModel.class);
                try {
                    if (wallpapaer.getS() != null) {
                        wallpapaer.setUid(dataSnapshot.getKey());
                        wallpaperList.add(0,wallpapaer);
                        adapter.notifyDataSetChanged();
                        Paper.book().write(FirebaseInfo.lastNodeFetched, dataSnapshot.getKey());
                        a++;
                        if (a % 10 == 0) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }

                } catch (Exception e) {

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void fabButton() {

        final ImageView fabIconNew = new ImageView(this);
        fabIconNew.setImageDrawable(getResources().getDrawable(R.drawable.ic_plus));
        final FloatingActionButton rightLowerButton = new FloatingActionButton.Builder(this)
                .setContentView(fabIconNew)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_CENTER)
                .build();

        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this);
        ImageView rlIcon1 = new ImageView(this);
        ImageView rlIcon2 = new ImageView(this);
        ImageView rlIcon3 = new ImageView(this);


        rlIcon1.setImageDrawable(getResources().getDrawable(R.drawable.ic_credits));
        rlIcon2.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
        rlIcon3.setImageDrawable(getResources().getDrawable(R.drawable.ic_share));

        // Build the menu with default options: light theme, 90 degrees, 72dp radius.
        // Set 4 default SubActionButtons
        final FloatingActionMenu rightLowerMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(rLSubBuilder.setContentView(rlIcon1).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon2).build())
                .addSubActionView(rLSubBuilder.setContentView(rlIcon3).build())
                .attachTo(rightLowerButton)
                .setStartAngle(200)
                .setEndAngle(340)
                .build();

        rlIcon1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Credits Wilferd - under build", Toast.LENGTH_SHORT).show();

            }
        });

        rlIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Saved WallPapers - under build", Toast.LENGTH_SHORT).show();
            }
        });

        rlIcon3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Share! - under build", Toast.LENGTH_SHORT).show();
            }
        });


        // Listen menu open and close events to animate the button content view
        rightLowerMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees clockwise
                fabIconNew.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu menu) {
                // Rotate the icon of rightLowerButton 45 degrees counter-clockwise
                fabIconNew.setRotation(45);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(fabIconNew, pvhR);
                animation.start();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this, "About us under build", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private AlertDialog.Builder wallPaperPromt() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.item_promt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(promptsView);

        mSimpleDraweeView = (SimpleDraweeView) findViewById(R.id.progressive_jpeg);

        imageForPromt = (ImageView) promptsView
                .findViewById(R.id.promtImageView);



        imageForPromt.setDrawingCacheEnabled(true);


        setAsWallPaperButton = (ImageView) promptsView
                .findViewById(R.id.setWallpaper);

        setAsWallPaperButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setImage = true;
                if (imageLoaded) {
                    setWallpaper();
                } else {
                    Toast.makeText(MainActivity.this, "Image Loading", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setAsWallPaperButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "Set as Wallpaper", Toast.LENGTH_SHORT).show();
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        Log.d("ImageSize", inImage.getAllocationByteCount() + " byteCount " + inImage.getByteCount() + " density - " + inImage.getDensity());


        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);

        return Uri.parse(path);

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
            Toast.makeText(MainActivity.this, "Error setting Wallpaper", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "sorry for incontinence our developers will fix this issue soon", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void wallPaperImagePressed(int position, Drawable previewImage) {
        listViewCallingMethods(position,previewImage);

    }


    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }


}

class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

    private MainActivity.ClickListener clicklistener;
    private GestureDetector gestureDetector;

    public RecyclerTouchListener(Context context, final RecyclerView recycleView, final MainActivity.ClickListener clicklistener) {

        this.clicklistener = clicklistener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clicklistener != null) {
                    clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
            clicklistener.onClick(child, rv.getChildAdapterPosition(child));
        }

        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}

class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = space;
        } else {
            outRect.top = 0;
        }
    }
}

