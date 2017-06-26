package in.techmafiya.neversettlewallpaper.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import in.techmafiya.neversettlewallpaper.Models.ImageModel;
import in.techmafiya.neversettlewallpaper.R;


/**
 * Created by saiso on 03-02-2017.
 */
public class ImageAdapter extends ArrayAdapter<ImageModel> {


    public int x=1;
    public int a;
    private static final String TAG = "Veggies";

    private final Activity activity;
    List<ImageModel> wallPaperList = new ArrayList<ImageModel>();
    public ViewHolder holder;
    public ImageAdapter(Activity activity,
                        List<ImageModel> wallPaperList) {
        super(activity, R.layout.item_wallpaper, wallPaperList);
        this.activity = activity;
        this.wallPaperList = wallPaperList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            view = inflater.inflate(R.layout.item_wallpaper, null, true);
            holder = new ViewHolder();

            holder.wallpaperImageView = (ImageView) view.findViewById(R.id.image);
            holder.likeImageView = (ImageView) view.findViewById(R.id.likeButton);
            holder.idTextView = (TextView) view.findViewById(R.id.textid);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        holder.likeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "Save Wallpapers! - under build", Toast.LENGTH_SHORT).show();
            }
        });




        Glide.with(activity)
                .load(wallPaperList.get(position).getS())
                .placeholder(R.drawable.placeholder1)
                .thumbnail(0.1f)
                .into(holder.wallpaperImageView);

//        Ion.with(activity)
//                .load(wallPaperList.get(position).getS())
//                .withBitmap()
//                .placeholder(R.drawable.placeholder1)
//                .intoImageView(holder.wallpaperImageView);


        holder.idTextView.setText("" + wallPaperList.get(position).getUid());

        return view;
    }

    static class ViewHolder {
        ImageView wallpaperImageView;
        ImageView likeImageView;
        TextView idTextView;
    }



}
