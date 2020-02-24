package com.ccbfm.music.player.database;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.ccbfm.music.player.database.entity.Playlist;
import com.ccbfm.music.player.database.entity.Song;

import java.util.List;

public final class SongLoader {

    private static String[] PROJECTION = new String[]{
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.DURATION,
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.IS_MUSIC
    };

    private static final String SELECTION = MediaStore.Audio.AudioColumns.DURATION + " >60000 AND " +
            MediaStore.Audio.AudioColumns.IS_MUSIC + " =1 AND " +
            MediaStore.Audio.AudioColumns.TITLE + " != ''";
    private static final String SELECTION_DATA = MediaStore.Audio.AudioColumns.DATA + " LIKE ";

    public static Cursor makeSongCursor(Context context, String path) {
        if (context != null) {
            String selectionStr = TextUtils.isEmpty(path) ? SELECTION : SELECTION + " AND " + SELECTION_DATA + "'%" + path + "%'";
            return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, PROJECTION,
                    selectionStr, null, null);
        }
        return null;
    }

    public static boolean loadAudioSong(Context context, String path) {
        Cursor cursor = makeSongCursor(context, path);
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                //long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
                int album_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
                int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                Song song = new Song(title, artist, data);
                song.setDuration(duration);
                song.setAlbum(album);
                song.setAlbumId(album_id);
                song.saveOrUpdate("songName=?", title);
            }

            sPlaylists = null;
            return true;
        }
        return false;
    }

    private static List<Playlist> sPlaylists;
    private static LoadDBSong sLoadDBSong;

    public static List<Playlist> getSongData(LoadSongCallBack callBack) {
        if (sPlaylists != null) {
            return sPlaylists;
        }
        if (sLoadDBSong != null && !sLoadDBSong.isCancelled()) {
            sLoadDBSong.cancel(true);
        }
        sLoadDBSong = new LoadDBSong(callBack);
        sLoadDBSong.execute();
        return null;
    }

    private static List<Playlist> loadDBSong() {
        List<Song> allSong = DBDao.queryAllSong();
        Playlist playlist = new Playlist();
        playlist.setName("全部歌曲");
        playlist.setSongList(allSong);
        List<Playlist> allPlaylist = DBDao.queryAllPlaylist();
        allPlaylist.add(0, playlist);
        return allPlaylist;
    }

    private static class LoadDBSong extends AsyncTask<Void, Integer, List<Playlist>> {
        private LoadSongCallBack mCallBack;

        private LoadDBSong(LoadSongCallBack callBack) {
            mCallBack = callBack;
        }

        @Override
        protected List<Playlist> doInBackground(Void... voids) {
            sPlaylists = null;
            return SongLoader.loadDBSong();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (mCallBack != null) {
                mCallBack.onPreExecute();
            }
        }

        @Override
        protected void onPostExecute(List<Playlist> playlists) {
            super.onPostExecute(playlists);
            sPlaylists = playlists;
            if (mCallBack != null) {
                mCallBack.onPostExecute(playlists);
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (mCallBack != null) {
                mCallBack.onCancelled();
            }
        }
    }

    public interface LoadSongCallBack {
        void onPreExecute();

        void onPostExecute(List<Playlist> playlists);

        void onCancelled();
    }
}
