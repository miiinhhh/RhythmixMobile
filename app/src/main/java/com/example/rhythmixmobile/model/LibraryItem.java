package com.example.rhythmixmobile.model;

public class LibraryItem {

    public static final int TYPE_PLAYLIST = 0;
    public static final int TYPE_SONG = 1;
    public static final int TYPE_ALBUM = 2;

    private int type;
    private Playlist playlist;
    private Song song;
    private Album album;

    public LibraryItem(Playlist playlist) {
        this.type = TYPE_PLAYLIST;
        this.playlist = playlist;
    }

    public LibraryItem(Song song) {
        this.type = TYPE_SONG;
        this.song = song;
    }

    public LibraryItem(Album album) {
        this.type = TYPE_ALBUM;
        this.album = album;
    }

    public int getType() {
        return type;
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    public Song getSong() {
        return song;
    }

    public Album getAlbum() {
        return album;
    }
}