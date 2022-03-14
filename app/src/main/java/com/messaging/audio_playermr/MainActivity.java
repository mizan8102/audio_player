package com.messaging.audio_playermr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.android.material.tabs.TabLayout;
import com.messaging.audio_playermr.Model.MusicAdapter;
import com.messaging.audio_playermr.Model.SongFiles;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    static ArrayList<SongFiles> songFiles;
    public static final int REQUEST_CODE = 1;
    static boolean shuffleBoolean = false, repeatBoolean = false;
    static ArrayList<SongFiles> albums = new ArrayList<>();
    private String MY_Sort_Pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        petmission();

    }

    //////////////////  Ask to permission user start///////////////////
    private void petmission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    , REQUEST_CODE);
        } else {
            initeviewpager();
            songFiles = getAllAudion(this);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initeviewpager();
                songFiles = getAllAudion(this);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , REQUEST_CODE);
            }
        }
    }
    ///////////////////////////// End /////////////////////

    private void initeviewpager() {
        ViewPager viewPager = findViewById(R.id.view);
        TabLayout tableLayout = findViewById(R.id.tab_layout);
        ViewpagerAdapter viewpagerAdapter = new ViewpagerAdapter(getSupportFragmentManager());
        viewpagerAdapter.addFragments(new SongsFragment(), "Songs");
        viewpagerAdapter.addFragments(new AlbumFragment(), "Album");
        viewPager.setAdapter(viewpagerAdapter);
        tableLayout.setupWithViewPager(viewPager);

    }


    public static class ViewpagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewpagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        void addFragments(Fragment fragment, String title) {

            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public  ArrayList<SongFiles> getAllAudion(Context context) {

        SharedPreferences preferences=getSharedPreferences(MY_Sort_Pref,MODE_PRIVATE);
        String SortOrder=preferences.getString("sorting","sortByName");
        ArrayList<String> duplicate = new ArrayList<>();
        albums.clear();
        ArrayList<SongFiles> tempaudiolist = new ArrayList<>();
        String order=null;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        switch (SortOrder){

            case "sortByName":
                order=MediaStore.MediaColumns.DISPLAY_NAME+ " ASC";
                break;

            case "sortByDate":
                order=MediaStore.MediaColumns.DATE_ADDED+ " ASC";
                break;

            case "sortBySize":
                order=MediaStore.MediaColumns.SIZE +" DESC";
                break;
        }
        String[] projecton = {

                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID

        };
        Cursor cursor = context.getContentResolver().query(uri, projecton, null, null, order);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                String id = cursor.getString(5);
                SongFiles songFiles = new SongFiles(path, title, artist, album, duration, id);

                Log.e("path" + path, "album:" + album);
                tempaudiolist.add(songFiles);

                if (!duplicate.contains(album)) {
                    albums.add(songFiles);
                    duplicate.add(album);
                }
            }
            cursor.close();
        }
        return tempaudiolist;
    }

    ///////////////////////////////////// //// search work /////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search_option);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    //////////////////// after implement SearchView.OnQueryTextListener //then find two method
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput=newText.toLowerCase();
        //Toast.makeText(this,userInput,Toast.LENGTH_SHORT).show();
        ArrayList<SongFiles> myFiles=new ArrayList<>();
        for (SongFiles song: songFiles){
            if (song.getTitle().toLowerCase().contains(userInput)){
                myFiles.add(song);

            }
        }
        SongsFragment.musicAdapter.updateList(myFiles);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        SharedPreferences.Editor editor=getSharedPreferences(MY_Sort_Pref,MODE_PRIVATE).edit();
        switch (item.getItemId())
        {
            case R.id.by_name:
                editor.putString("sorting","sortByName");
                editor.apply();
                this.recreate();
                break;

            case R.id.by_date:
                editor.putString("sorting","sortByDate");
                editor.apply();
                this.recreate();
                break;

            case R.id.by_size:
                editor.putString("sorting","sortBySize");
                editor.apply();
                this.recreate();
                break;


        }
        return super.onOptionsItemSelected(item);
    }
}