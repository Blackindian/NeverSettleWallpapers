package in.techmafiya.neversettlewallpaper.Activities;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;

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

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

import android.graphics.Bitmap;

import at.favre.lib.dali.Dali;
import at.favre.lib.dali.builder.live.LiveBlurWorker;
import in.techmafiya.neversettlewallpaper.Adapter.ImagesAdapter;
import in.techmafiya.neversettlewallpaper.FirebaseInfo.FirebaseDataBaseCheck;
import in.techmafiya.neversettlewallpaper.FirebaseInfo.FirebaseInfo;
import in.techmafiya.neversettlewallpaper.Permission.MarshMallowPermission;
import in.techmafiya.neversettlewallpaper.Models.ImageModel;
import in.techmafiya.neversettlewallpaper.R;
import io.paperdb.Paper;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MainActivity extends AppCompatActivity implements ImagesAdapter.ImageAdapterCallback {

    private LiveBlurWorker blurWorker, blurWorker1;
    private ImagesAdapter adapter;
    private boolean firstCheck = false;
    private ArrayList<ImageModel> wallpaperList = new ArrayList<ImageModel>();
    private MaterialProgressBar indeterminatProgressBar;
    boolean imageLoaded = false, setImage = false;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView toolbarTextView;
    private int a = 0, height, width, positionMain;
    private RelativeLayout parentLayout;
    private View blurview, blurView1;
    private MarshMallowPermission marshMallowPermission = new MarshMallowPermission(this);
    private ImageView imageForPromt, setAsWallPaperButton, placeholderImage;
    private Bitmap bitmap;
    private RecyclerView recyclerView;
    public FloatingActionMenu rightLowerMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar();

        Paper.init(this);

        initMarshmallowPermission();

        initUiElements();

        GetDisplaySize();

        fabButton();

        adapter = new ImagesAdapter(MainActivity.this, wallpaperList);
        adapter.setCallback(this);
        UpdateFromDatabase();
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.addItemDecoration(new SpacesItemDecoration(10));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }

    void initToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        View smallImageView = toolbar.findViewById(R.id.logo);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            smallImageView.setTransitionName(getString(R.string.activity_image_trans));
        }

        toolbarTextView = (TextView) toolbar.findViewById(R.id.toolbarTV);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Lato-Bold.ttf");
        toolbarTextView.setTypeface(custom_font);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");

    }
    void initUiElements() {

        parentLayout = (RelativeLayout) findViewById(R.id.blurlayout);
        blurview = findViewById(R.id.blurView);
        blurView1 =  findViewById(R.id.blurView2);

        blurWorker = Dali.create(MainActivity.this).liveBlur(parentLayout, blurview).downScale(8).assemble(true);
        blurWorker1 = Dali.create(MainActivity.this).liveBlur(parentLayout, blurView1).blurRadius(3).downScale(3).assemble(true);
        blurWorker.updateBlurView();
        blurWorker1.updateBlurView();

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

        if (Build.VERSION.SDK_INT > 22) {
            recyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    blurWorker1.updateBlurView();
                    blurWorker.updateBlurView();
                    if(rightLowerMenu.isOpen()){
                        rightLowerMenu.close(true);
                    }
                }
            });
        } else {
            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    blurWorker1.updateBlurView();
                    blurWorker.updateBlurView();
                    if(rightLowerMenu.isOpen()){
                        rightLowerMenu.close(true);
                    }
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
        placeholderImage.setImageDrawable(previewImage);


//        Ion.with(MainActivity.this)
//                .load(wallpaperList.get(position).getF())
//                .asBitmap()
//                .setCallback(new FutureCallback<Bitmap>() {
//                    @Override
//                    public void onCompleted(Exception e, Bitmap result) {
//                        imageLoaded = true;
//                        imageForPromt.setImageBitmap(result);
//                        indeterminatProgressBar.setVisibility(View.GONE);
//                        placeholderImage.setVisibility(View.GONE);
//                        setAsWallPaperButton.setVisibility(View.VISIBLE);
//                        bitmap = result;
//                        if (setImage) {
//                            setWallpaper();
//                        }
//                    }
//                });

        Glide.with(MainActivity.this).
                load(wallpaperList.get(position).getF())
                .asBitmap()
                .fitCenter()
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

    public void UpdateFromDatabase() {
        blurWorker.updateBlurView();
        Query query;
        if (firstCheck == false) {
            query = FirebaseDataBaseCheck.getDatabase().getReference().child(FirebaseInfo.NodeUsing).limitToFirst(Paper.book().read(FirebaseInfo.howManyNodes+10, 10));
            if (Paper.book().read(FirebaseInfo.howManyNodes, 0) == 0) {
                Paper.book().write(FirebaseInfo.howManyNodes, 10);
            }else {
                Paper.book().write(FirebaseInfo.howManyNodes, Paper.book().read(FirebaseInfo.howManyNodes,10)+10);
            }
            firstCheck = true;
        } else if (Paper.book().read(FirebaseInfo.lastNodeFetched, "").equals("")) {
            query = FirebaseDataBaseCheck.getDatabase().getReference().child(FirebaseInfo.NodeUsing).limitToFirst(10);
        } else {
            query = FirebaseDataBaseCheck.getDatabase().getReference().child(FirebaseInfo.NodeUsing).orderByKey().startAt(Paper.book().read(FirebaseInfo.lastNodeFetched, "") + 1).limitToFirst(6);
            int count = Paper.book().read(FirebaseInfo.howManyNodes, 0);
            Paper.book().write(FirebaseInfo.howManyNodes, count + 6);
        }
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ImageModel wallpapaer = dataSnapshot.getValue(ImageModel.class);
                try {
                    if (wallpapaer.getS() != null) {
                        wallpapaer.setUid(dataSnapshot.getKey());
                        wallpaperList.add(0, wallpapaer);
                        adapter.notifyDataSetChanged();
                        Paper.book().write(FirebaseInfo.lastNodeFetched, dataSnapshot.getKey());
                        a++;
                        mSwipeRefreshLayout.setRefreshing(false);
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
        rightLowerMenu = new FloatingActionMenu.Builder(this)
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
                Toast.makeText(MainActivity.this, "Credits - photos by Unsplash", Toast.LENGTH_SHORT).show();

            }
        });

        rlIcon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rightLowerMenu.isOpen()){
                    rightLowerMenu.close(true);
                }
                Intent intent = new Intent(MainActivity.this,LikedBoard.class);
                startActivity(intent);
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
        if (id == R.id.action_credits) {
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
        listViewCallingMethods(position, previewImage);

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

    }
}

