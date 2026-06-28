package com.example.rhythmixmobile.model;

public class MediaItem {

    private String mediaId;
    private String title;
    private String description;
    private String mediaType;
    private int duration;
    private String filePath;
    private String thumbnailUrl;
    private String mimeType;
    private long fileSize;

    private String artistId;
    private String artistName;
    private String albumId;
    private String albumTitle;
    private String genreId;
    private String ownerId;

    private boolean isPublic;
    private int viewCount;
    private String createdAt;

    private String videoFilePath;
    private String videoMimeType;
    private Long videoFileSize;

    public String getMediaId() { return mediaId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getMediaType() { return mediaType; }
    public int getDuration() { return duration; }
    public String getFilePath() { return filePath; }
    public String getThumbnailUrl() { return thumbnailUrl; }
    public String getMimeType() { return mimeType; }
    public long getFileSize() { return fileSize; }

    public String getArtistId() { return artistId; }
    public String getArtistName() { return artistName; }
    public String getAlbumId() { return albumId; }
    public String getAlbumTitle() { return albumTitle; }
    public String getGenreId() { return genreId; }
    public String getOwnerId() { return ownerId; }

    public boolean isPublic() { return isPublic; }
    public int getViewCount() { return viewCount; }
    public String getCreatedAt() { return createdAt; }

    public String getVideoFilePath() { return videoFilePath; }
    public String getVideoMimeType() { return videoMimeType; }
    public Long getVideoFileSize() { return videoFileSize; }
}
