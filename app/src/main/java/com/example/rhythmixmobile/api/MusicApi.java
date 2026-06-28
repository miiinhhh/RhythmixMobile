package com.example.rhythmixmobile.api;

import com.example.rhythmixmobile.model.AddTrackRequest;
import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.Artist;
import com.example.rhythmixmobile.model.MediaResponse;
import com.example.rhythmixmobile.model.MediaDiscoveryResponse;
import com.example.rhythmixmobile.model.Playlist;
import com.example.rhythmixmobile.model.PlaylistDetailResponse;
import com.example.rhythmixmobile.model.RegisterRequest;
import com.example.rhythmixmobile.model.SearchResponse;
import com.example.rhythmixmobile.model.Song;
import com.example.rhythmixmobile.model.UploadMediaResponse;

import java.util.List;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MusicApi {
    @GET("api/Songs/new")
    Call<List<Song>> getNewSongs();

    @GET("api/Songs/popular")
    Call<List<Song>> getPopularSongs();

    @GET("api/Playlists/public")
    Call<ApiResponse<List<Playlist>>> getPublicPlaylists();

    @GET("api/Playlists/my-playlists")
    Call<ApiResponse<List<Playlist>>> getMyPlaylists();

    @GET("api/Songs/ai-suggested")
    Call<List<Song>> getAiSuggestedSongs();

    @GET("api/Search")
    Call<ApiResponse<SearchResponse>> search(
            @Query("query") String query
    );
    @GET("api/Artists/search")
    Call<ApiResponse<List<Artist>>> searchArtists(
            @Query("q") String query
    );
    @GET("api/Playlists/{playlistId}")
    Call<PlaylistDetailResponse> getPlaylistDetail(
            @Path("playlistId") String playlistId
    );

    @POST("api/Playlists/{playlistId}/tracks")
    Call<ApiResponse<Object>> addSongToPlaylist(
            @Path("playlistId") String playlistId,
            @Body AddTrackRequest request
    );

    @DELETE("api/Playlists/{playlistId}/tracks/{mediaId}")
    Call<ApiResponse<Object>> removeSongFromPlaylist(
            @Path("playlistId") String playlistId,
            @Path("mediaId") String mediaId
    );

    @GET("api/Library/favorites")
    Call<List<Song>> getFavoriteSongs();

    @GET("api/Library/history")
    Call<List<Song>> getListeningHistory();

    @GET("api/Songs")
    Call<List<Song>> getAllSongs();

    @POST("api/Auth/register")
    Call<Void> register(@Body RegisterRequest request);

    @GET("api/Media/{mediaId}")
    Call<MediaResponse> getMediaById(@Path("mediaId") String mediaId);

    @GET("api/Media/discovery")
    Call<MediaDiscoveryResponse> getDiscoveryMedia(
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );
    @Multipart
    @POST("api/Media/upload")
    Call<ApiResponse<UploadMediaResponse>> uploadMedia(
            @Part("Title") RequestBody title,
            @Part("ArtistName") RequestBody artistName,
            @Part("Description") RequestBody description,
            @Part("IsPublic") RequestBody isPublic,
            @Part MultipartBody.Part File,
            @Part MultipartBody.Part CoverImage,
            @Part MultipartBody.Part VideoFile
    );
    @Multipart
    @POST("api/Media/upload")
    Call<ApiResponse<UploadMediaResponse>> uploadMedia(
            @Part("Title") RequestBody title,
            @Part("ArtistName") RequestBody artistName,
            @Part("Description") RequestBody description,
            @Part("AlbumId") RequestBody albumId,
            @Part("IsPublic") RequestBody isPublic,
            @Part MultipartBody.Part File,
            @Part MultipartBody.Part CoverImage,
            @Part MultipartBody.Part VideoFile
    );
    @GET("api/Media/my-media")
    Call<MediaDiscoveryResponse> getMyMedia(
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );
}
