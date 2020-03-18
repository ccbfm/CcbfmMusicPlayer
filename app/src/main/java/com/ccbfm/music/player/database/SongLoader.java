package com.ccbfm.music.player.database;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.LongSparseArray;

import com.ccbfm.music.player.App;
import com.ccbfm.music.player.callback.Callback;
import com.ccbfm.music.player.control.MusicControl;
import com.ccbfm.music.player.database.entity.Playlist;
import com.ccbfm.music.player.database.entity.Song;
import com.ccbfm.music.player.tool.Constants;
import com.ccbfm.music.player.tool.Executors;
import com.ccbfm.music.player.tool.LiveDataBus;
import com.ccbfm.music.player.tool.LogTools;
import com.ccbfm.music.player.tool.SPTools;
import com.ccbfm.music.player.tool.ToastTools;

import org.litepal.LitePal;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

public final class SongLoader {
    private static final String TAG = "SongLoader";
    private static final boolean DEBUG = false;

    private static final Executor EXECUTOR = Executors.EXECUTOR;
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

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

    public static int[] loadAudioSong(Context context, String path, boolean playlistChecked) {
        LogTools.d(TAG, "loadAudioSong", "path=" + path + ",playlistChecked=" + playlistChecked);
        Cursor cursor = makeSongCursor(context, path);
        if (cursor != null) {
            int count = cursor.getCount();
            int skip = 0;
            int playlistCount = 0;
            if (count > 0) {
                LongSparseArray<Playlist> playlistArray = null;
                if (playlistChecked) {
                    playlistArray = new LongSparseArray<>();
                }
                while (cursor.moveToNext()) {
                    //long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM));
                    int album_id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
                    int duration = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));

                    List<Song> songs = LitePal.where("songPath=?", data).find(Song.class, true);
                    Song song = null;
                    if (songs != null && songs.size() > 0) {
                        song = songs.get(0);
                        if (song.getStatus() != 0) {
                            skip++;
                            continue;
                        }
                    }
                    if (song == null) {
                        song = new Song();
                    }
                    song.setSongName(title);
                    song.setSingerName(artist);
                    song.setSongPath(data);
                    song.setDuration(duration);
                    song.setAlbum(album);
                    song.setAlbumId(album_id);
                    song.saveOrUpdate("id=?", song.getId() + "");

                    if (playlistArray != null) {
                        Playlist playlist = playlistArray.get(album_id);
                        if (playlist == null) {
                            playlist = new Playlist();
                            playlist.setName(album);
                            playlistArray.put(album_id, playlist);
                        }
                        List<Song> songList = playlist.getSongList();
                        if (songList == null) {
                            songList = new LinkedList<>();
                            playlist.setSongList(songList);
                        }
                        songList.add(song);
                    }
                }
                LogTools.d(TAG, "loadAudioSong", "playlistArray=" + playlistArray);
                if (playlistArray != null) {
                    int size = playlistArray.size();
                    playlistCount = size;
                    if (size > 0) {
                        for (int i = 0; i < size; i++) {
                            Playlist playlist = playlistArray.valueAt(i);
                            playlist.saveOrUpdate("name=?", playlist.getName());
                        }
                    }
                }
                sPlaylists = null;
            }
            return new int[]{count, skip, playlistCount};
        }
        return null;
    }

    private static List<Playlist> sPlaylists;
    private static LoadDBSong sLoadDBSong;

    public static List<Playlist> getSongData(LoadSongCallBack callBack) {
        if (DEBUG) {
            LogTools.d(TAG, "getSongData", "sPlaylists=" + (sPlaylists != null));
        }
        if (sPlaylists != null) {
            return sPlaylists;
        }
        if (DEBUG) {
            LogTools.d(TAG, "getSongData", "--->---");
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
        sPlaylists = allPlaylist;
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
            if (DEBUG) {
                LogTools.d(TAG, "onPostExecute", "playlists=" + playlists);
            }
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

    public static void deleteAllSongAsync() {
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DBDao.deleteAllPlaylist();
                int rowsAffected = DBDao.deleteAllSong("0");
                ToastTools.showToast(App.getApp(), "清除" + rowsAffected + "个");
                if (rowsAffected > 0) {
                    sPlaylists = null;
                    LiveDataBus.get().<Boolean>with(Constants.SCAN_SUCCESS_NOTIFICATION).postValue(true);
                    clearData();
                }
            }
        };
        EXECUTOR.execute(runnable);
    }

    private static void clearData() {
        SPTools.putIntValue(SPTools.KEY_INIT_PLAYLIST_INDEX, 0);
        SPTools.putIntValue(SPTools.KEY_INIT_SONG_INDEX, 0);
        SPTools.putIntValue(SPTools.KEY_INIT_SONG_MSEC, 0);
        MusicControl.getInstance().release();
    }

    public static boolean addOrUpdatePlaylist(Playlist oldPlaylist, String name, List<Song> songs) {
        if (songs == null || songs.size() == 0) {
            return false;
        }
        if (oldPlaylist == null) {
            oldPlaylist = new Playlist();
        }
        oldPlaylist.setName(name);
        oldPlaylist.setSongList(songs);
        boolean flag = oldPlaylist.saveOrUpdate("id=?", oldPlaylist.getId() + "");
        if (flag) {
            sPlaylists = null;
        }
        return flag;
    }

    public static void deleteSong(final int groupPosition, final int childPosition) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Playlist playlist = sPlaylists.get(groupPosition);
                Song song = playlist.getSongList().remove(childPosition);
                if (groupPosition == 0) {
                    song.setStatus(1);
                    song.save();
                } else {
                    playlist.save();
                }
                sPlaylists = null;
                loadDBSong();
                LiveDataBus.get().<Boolean>with(Constants.SCAN_SUCCESS_NOTIFICATION).postValue(true);
            }
        };
        EXECUTOR.execute(runnable);
    }

    public static void deletePlaylist(final int groupPosition) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Playlist playlist = sPlaylists.get(groupPosition);
                if (groupPosition == 0) {
                    return;
                } else {
                    playlist.delete();
                }
                sPlaylists = null;
                loadDBSong();
                LiveDataBus.get().<Boolean>with(Constants.SCAN_SUCCESS_NOTIFICATION).postValue(true);
            }
        };
        EXECUTOR.execute(runnable);
    }

    public static void loadBlacklist(final CallbackSongList callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<Song> songList = DBDao.queryAllSong("1");
                MAIN_HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.callback(songList);
                        }
                    }
                });

            }
        };
        EXECUTOR.execute(runnable);
    }

    public static void restoreBlacklist(final Song song, final Callback callback) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                song.setStatus(0);
                song.save();

                MAIN_HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            callback.callback();
                        }
                    }
                });

                sPlaylists = null;
                loadDBSong();
                LiveDataBus.get().<Boolean>with(Constants.SCAN_SUCCESS_NOTIFICATION).postValue(true);
            }
        };
        EXECUTOR.execute(runnable);
    }

    public interface CallbackSongList {
        void callback(List<Song> songList);
    }

}
