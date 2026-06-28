package com.example.rhythmixmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.database.Cursor;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.rhythmixmobile.api.AlbumApi;
import com.example.rhythmixmobile.api.MusicApi;
import com.example.rhythmixmobile.api.RetrofitClient;
import com.example.rhythmixmobile.model.AddTrackRequest;
import com.example.rhythmixmobile.model.Album;
import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.UploadMediaResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadMusicActivity extends AppCompatActivity {

    private String playlistId;
    private EditText etTitle, etArtistName, etDescription;
    private Button btnChooseAudio, btnChooseVideo, btnChooseCover, btnUpload;
    private TextView tvAudioFileName, tvVideoFileName, tvCoverFileName;
    private Spinner spAlbum;
    private AlbumApi albumApi;
    private List<Album> albumList = new ArrayList<>();
    private String selectedAlbumId = "";
    private Uri audioUri, videoUri, coverUri;
    private MusicApi musicApi;

    private static final int PICK_AUDIO = 1;
    private static final int PICK_VIDEO = 2;
    private static final int PICK_COVER = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_music);

        initViews();

        playlistId = getIntent().getStringExtra("playlistId");

        musicApi = RetrofitClient
                .getInstance(this)
                .create(MusicApi.class);

        albumApi = RetrofitClient
                .getInstance(this)
                .create(AlbumApi.class);

        loadAlbums();

        btnChooseAudio.setOnClickListener(v -> pickFile("audio/*", PICK_AUDIO));
        btnChooseVideo.setOnClickListener(v -> pickFile("video/*", PICK_VIDEO));
        btnChooseCover.setOnClickListener(v -> pickFile("image/*", PICK_COVER));

        btnUpload.setOnClickListener(v -> uploadMusic());
    }

    private void initViews() {
        etTitle = findViewById(R.id.etTitle);
        etArtistName = findViewById(R.id.etArtistName);
        etDescription = findViewById(R.id.etDescription);
        spAlbum = findViewById(R.id.spAlbum);

        btnChooseAudio = findViewById(R.id.btnChooseAudio);
        btnChooseVideo = findViewById(R.id.btnChooseVideo);
        btnChooseCover = findViewById(R.id.btnChooseCover);
        btnUpload = findViewById(R.id.btnUpload);

        tvAudioFileName = findViewById(R.id.tvAudioFileName);
        tvVideoFileName = findViewById(R.id.tvVideoFileName);
        tvCoverFileName = findViewById(R.id.tvCoverFileName);
    }

    private void pickFile(String type, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Chọn tệp"), requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null || data.getData() == null) return;

        Uri uri = data.getData();

        if (requestCode == PICK_AUDIO) {
            audioUri = uri;
            tvAudioFileName.setText(getFileName(uri));
        } else if (requestCode == PICK_VIDEO) {
            videoUri = uri;
            tvVideoFileName.setText(getFileName(uri));
        } else if (requestCode == PICK_COVER) {
            coverUri = uri;
            tvCoverFileName.setText(getFileName(uri));
        }
    }

    private void uploadMusic() {
        String title = etTitle.getText().toString().trim();
        String artistName = etArtistName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên bài hát", Toast.LENGTH_SHORT).show();
            return;
        }

        if (artistName.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên nghệ sĩ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (audioUri == null) {
            Toast.makeText(this, "Vui lòng chọn tệp âm thanh", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody titleBody = textBody(title);
        RequestBody artistBody = textBody(artistName);
        RequestBody descBody = textBody(description);
        RequestBody publicBody = textBody("true");
        RequestBody albumBody;

        if (spAlbum.getSelectedItemPosition() == 0) {
            albumBody = textBody("");
        } else {
            albumBody = textBody(
                    albumList
                            .get(spAlbum.getSelectedItemPosition() - 1)
                            .getAlbumId()
            );
        }

        MultipartBody.Part audioPart = createFilePart("File", audioUri);
        MultipartBody.Part coverPart = coverUri != null ? createFilePart("CoverImage", coverUri) : null;
        MultipartBody.Part videoPart = videoUri != null ? createFilePart("VideoFile", videoUri) : null;

        btnUpload.setEnabled(false);
        btnUpload.setText("Đang upload...");

        musicApi.uploadMedia(
                titleBody,
                artistBody,
                descBody,
                albumBody,
                publicBody,
                audioPart,
                coverPart,
                videoPart
        ).enqueue(new Callback<ApiResponse<UploadMediaResponse>>() {

            @Override
            public void onResponse(
                    Call<ApiResponse<UploadMediaResponse>> call,
                    Response<ApiResponse<UploadMediaResponse>> response) {

                btnUpload.setEnabled(true);
                btnUpload.setText("Xuất bản bài hát");

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()
                        && response.body().getData() != null) {

                    String mediaId = response.body().getData().getMediaId();

                    if (playlistId != null && !playlistId.isEmpty()) {
                        addSongToPlaylist(mediaId);
                    } else {
                        Toast.makeText(
                                UploadMusicActivity.this,
                                "Upload thành công",
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                    }

                } else {
                    Toast.makeText(
                            UploadMusicActivity.this,
                            "Upload thất bại: " + response.code(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<ApiResponse<UploadMediaResponse>> call,
                    Throwable t) {

                btnUpload.setEnabled(true);
                btnUpload.setText("Xuất bản bài hát");

                Toast.makeText(
                        UploadMusicActivity.this,
                        "Lỗi: " + t.getMessage(),
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private RequestBody textBody(String value) {
        return RequestBody.create(
                MediaType.parse("text/plain"),
                value
        );
    }

    private MultipartBody.Part createFilePart(String partName, Uri uri) {
        try {
            File file = uriToFile(uri);
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse(getContentResolver().getType(uri)),
                    file
            );

            return MultipartBody.Part.createFormData(
                    partName,
                    file.getName(),
                    requestBody
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File uriToFile(Uri uri) throws Exception {
        String fileName = getFileName(uri);
        File file = new File(getCacheDir(), fileName);

        InputStream inputStream = getContentResolver().openInputStream(uri);
        FileOutputStream outputStream = new FileOutputStream(file);

        byte[] buffer = new byte[4096];
        int read;

        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }

        outputStream.close();
        inputStream.close();

        return file;
    }

    private String getFileName(Uri uri) {
        String result = "file";

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (cursor.moveToFirst() && index >= 0) {
                result = cursor.getString(index);
            }
            cursor.close();
        }

        return result;
    }
    private void addSongToPlaylist(String mediaId) {

        AddTrackRequest request =
                new AddTrackRequest(mediaId, 0);

        musicApi.addSongToPlaylist(
                playlistId,
                request
        ).enqueue(new Callback<ApiResponse<Object>>() {

            @Override
            public void onResponse(
                    Call<ApiResponse<Object>> call,
                    Response<ApiResponse<Object>> response) {

                Toast.makeText(
                        UploadMusicActivity.this,
                        "Đã thêm bài hát vào Playlist",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            }

            @Override
            public void onFailure(
                    Call<ApiResponse<Object>> call,
                    Throwable t) {

                Toast.makeText(
                        UploadMusicActivity.this,
                        "Không thể thêm vào Playlist",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
    private void loadAlbums() {

        albumApi.getMyAlbums().enqueue(
                new Callback<ApiResponse<List<Album>>>() {

                    @Override
                    public void onResponse(
                            Call<ApiResponse<List<Album>>> call,
                            Response<ApiResponse<List<Album>>> response) {

                        if (!response.isSuccessful()
                                || response.body() == null
                                || !response.body().isSuccess()) {
                            return;
                        }

                        albumList = response.body().getData();

                        List<String> titles = new ArrayList<>();

                        titles.add("Không chọn Album");

                        for (Album album : albumList) {
                            titles.add(album.getTitle());
                        }

                        ArrayAdapter<String> adapter =
                                new ArrayAdapter<>(
                                        UploadMusicActivity.this,
                                        android.R.layout.simple_spinner_item,
                                        titles
                                );

                        adapter.setDropDownViewResource(
                                android.R.layout.simple_spinner_dropdown_item
                        );

                        spAlbum.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(
                            Call<ApiResponse<List<Album>>> call,
                            Throwable t) {

                    }
                });
    }
}