package com.messaging.audio_playermr.Model;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.messaging.audio_playermr.PlayerActivity;
import com.messaging.audio_playermr.R;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {
    private Context context;
    public static ArrayList<SongFiles> mFiles;

    public MusicAdapter(Context context, ArrayList<SongFiles> songFiles) {
        this.context = context;
        this.mFiles = songFiles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       // LayoutInflater layoutInflater=new
        View view=LayoutInflater.from(context).inflate(R.layout.music_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        SongFiles so=mFiles.get(position);
        holder.textView.setText(so.getTitle());

        final byte[] image=getAlbumArt(so.getPath());
        if (image!=null){
            Glide.with(context).asBitmap()
                    .load(image).into(holder.imageView);
        }
        else {
            Glide.with(context)
                    .load(R.drawable.applogomr).into(holder.imageView);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, PlayerActivity.class);
                intent.putExtra("position",position);
               context.startActivity(intent);

            }
        });
        holder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popupMenu=new PopupMenu(context,view);
                popupMenu.getMenuInflater().inflate(R.menu.menu,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()){
                            case R.id.delete:
                               // Toast.makeText(context,"Delete clicked",Toast.LENGTH_SHORT).show();
                                delete(position,view);
                        }

                        return true;
                    }
                });
            }
        });

    }

    private void delete(int position, View view) {


        Uri contentUri= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                Long.parseLong(mFiles.get(position).getId()));

        File file=new File(mFiles.get(position).getPath());
        boolean deleted=file.delete();
        if (deleted){
            context.getContentResolver().delete(contentUri,null,null);
            mFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,mFiles.size());
            Snackbar.make(view,"File Deleted",Snackbar.LENGTH_SHORT).show();
        }
        else {
            Snackbar.make(view,"Can't deleted file",Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView,menuMore;
        TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
          imageView=itemView.findViewById(R.id.music_img);
          textView=itemView.findViewById(R.id.music_file_name);
          menuMore=itemView.findViewById(R.id.menuDelete);
        }
    }
    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever mmtr=new MediaMetadataRetriever();
        mmtr.setDataSource(uri);
        byte[] art=mmtr.getEmbeddedPicture();
        mmtr.release();
        return art;

    }
    public void updateList(ArrayList<SongFiles> songFilesArrayList){
        mFiles=new ArrayList<>();
        mFiles.addAll(songFilesArrayList);
        notifyDataSetChanged();
    }
}
