package in.techmafiya.neversettlewallpaper.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import in.techmafiya.neversettlewallpaper.FirebaseInfo.FirebaseInfo;
import in.techmafiya.neversettlewallpaper.Models.ImageModel;
import in.techmafiya.neversettlewallpaper.R;
import io.paperdb.Paper;

/**
 * Created by sai on 7/1/17.
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.MyViewHolder> {

    private List<ImageModel> imageList;
    private Context activity;
    private ImageAdapterCallback callback;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView image, like;

        public MyViewHolder(View view)  {
            super(view);
            image = (ImageView) view.findViewById(R.id.image);
            like = (ImageView) view.findViewById(R.id.likeButton);
            like.setOnClickListener(this);
            image.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (v.getId() == like.getId()) {
                if("unlike".equals(like.getTag())){
                    like.setImageResource(R.drawable.ic_action_like);
                    like.setTag("like");
                    List<String> likedString = new ArrayList<>();
                    likedString = Paper.book().read(FirebaseInfo.likedStringArray,new ArrayList<String>());
                    likedString.add(imageList.get(getAdapterPosition()).getUid());
                    Paper.book().write(FirebaseInfo.likedStringArray,likedString);

                    List<ImageModel> likedImagesList = new ArrayList<>();
                    likedImagesList = Paper.book().read(FirebaseInfo.likedImagesArray,new ArrayList<ImageModel>());
                    likedImagesList.add(imageList.get(getAdapterPosition()));
                    Paper.book().write(FirebaseInfo.likedImagesArray,likedImagesList);

                }else if("like".equals(like.getTag())){
                    like.setImageResource(R.drawable.ic_action_unlike);
                    like.setTag("unlike");
                    List<String> likedString = new ArrayList<>();
                    likedString = Paper.book().read(FirebaseInfo.likedStringArray,new ArrayList<String>());
                    likedString.remove(imageList.get(getAdapterPosition()).getUid());
                    Paper.book().write(FirebaseInfo.likedStringArray,likedString);

                    List<ImageModel> likedImagesList = new ArrayList<>();
                    likedImagesList = Paper.book().read(FirebaseInfo.likedImagesArray,new ArrayList<ImageModel>());
                    likedImagesList.remove(imageList.get(getAdapterPosition()));
                    Paper.book().write(FirebaseInfo.likedImagesArray,likedImagesList);

                }
            }else
                if(v.getId() == image.getId()){
                    if(callback != null) {
                        callback.wallPaperImagePressed(getAdapterPosition(),image.getDrawable());
                    }
                }
        }
    }

    public ImagesAdapter(Context activity, List<ImageModel> imageList) {
        this.imageList = imageList;
        this.activity = activity;
        Paper.init(activity);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wallpaper, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ImageModel mImageModel = imageList.get(position);
        Glide.with(activity)
                .load(mImageModel.getS())
                .placeholder(R.drawable.placeholder1)
                .thumbnail(0.1f)
                .centerCrop()
                .into(holder.image);

        List<String> likedStringList = new ArrayList<>();

        likedStringList = Paper.book().read(FirebaseInfo.likedStringArray,new ArrayList<String>());

            if(likedStringList.contains(mImageModel.getUid())){
                holder.like.setImageResource(R.drawable.ic_action_like);
                holder.like.setTag("like");
            }else{
                holder.like.setImageResource(R.drawable.ic_action_unlike);
                holder.like.setTag("unlike");
            }
    }

    @Override
    public void onViewRecycled(MyViewHolder holder) {
        holder.image.setScaleType(ImageView.ScaleType.FIT_XY);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public void setCallback(ImageAdapterCallback callback){

        this.callback = callback;
    }

    public interface ImageAdapterCallback {

        public void wallPaperImagePressed(int position, Drawable previewBitmap);
    }


}
