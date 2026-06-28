package com.example.rhythmixmobile;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.database.Cursor;
import android.provider.OpenableColumns;
import android.util.Log;

import com.example.rhythmixmobile.api.ProfileApi;
import com.example.rhythmixmobile.api.RetrofitClient;
import com.example.rhythmixmobile.model.ApiResponse;
import com.example.rhythmixmobile.model.AvatarUploadResponse;
import com.example.rhythmixmobile.model.UpdateProfileRequest;
import com.example.rhythmixmobile.model.UserProfile;
import com.example.rhythmixmobile.utils.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView imgAvatar;
    private EditText edtBio;
    private Uri selectedImageUri;

    private ProfileApi profileApi;
    private UserProfile currentProfile;

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imgAvatar.setImageURI(uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        imgAvatar = findViewById(R.id.imgAvatar);
        edtBio = findViewById(R.id.edtBio);

        Button btnChooseAvatar = findViewById(R.id.btnChooseAvatar);
        Button btnSave = findViewById(R.id.btnSave);

        profileApi = RetrofitClient
                .getInstance(this)
                .create(ProfileApi.class);

        loadMyProfile();

        btnChooseAvatar.setOnClickListener(v -> {
            imagePicker.launch("image/*");
        });

        btnSave.setOnClickListener(v -> {
            saveProfile();
        });
    }

    private void loadMyProfile() {
        profileApi.getMyProfile().enqueue(new Callback<ApiResponse<UserProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfile>> call, Response<ApiResponse<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentProfile = response.body().getData();

                    edtBio.setText(currentProfile.bio);
                    if (currentProfile.avatarUrl != null && !currentProfile.avatarUrl.isEmpty()) {
                        String imageUrl = Constants.BASE_URL + currentProfile.avatarUrl;

                        Glide.with(EditProfileActivity.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_user)
                                .error(R.drawable.ic_user)
                                .circleCrop()
                                .into(imgAvatar);
                    } else {
                        imgAvatar.setImageResource(R.drawable.ic_user);
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Không tải được hồ sơ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserProfile>> call, Throwable t) {
                Log.e("PROFILE_ERROR", "Load profile failed", t);
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối hồ sơ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveProfile() {
        if (currentProfile == null) {
            Toast.makeText(this, "Chưa tải xong hồ sơ", Toast.LENGTH_SHORT).show();
            return;
        }

        UpdateProfileRequest request = new UpdateProfileRequest(
                currentProfile.id,
                currentProfile.userName,
                currentProfile.displayName,
                edtBio.getText().toString().trim(),
                currentProfile.avatarUrl
        );

        profileApi.updateProfile(request).enqueue(new Callback<ApiResponse<UserProfile>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfile>> call, Response<ApiResponse<UserProfile>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    currentProfile = response.body().getData();

                    if (selectedImageUri != null) {
                        uploadAvatar();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Đã lưu hồ sơ", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Lưu hồ sơ thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserProfile>> call, Throwable t) {
                Log.e("PROFILE_ERROR", "Update profile failed", t);
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối khi lưu hồ sơ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadAvatar() {
        try {
            File file = createFileFromUri(this, selectedImageUri);

            RequestBody requestFile = RequestBody.create(
                    file,
                    MediaType.parse("image/*")
            );

            MultipartBody.Part body = MultipartBody.Part.createFormData(
                    "File",
                    file.getName(),
                    requestFile
            );

            profileApi.uploadAvatar(body).enqueue(new Callback<ApiResponse<AvatarUploadResponse>>() {
                @Override
                public void onResponse(Call<ApiResponse<AvatarUploadResponse>> call, Response<ApiResponse<AvatarUploadResponse>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(EditProfileActivity.this, "Đã lưu hồ sơ", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Upload avatar thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<AvatarUploadResponse>> call, Throwable t) {
                    Log.e("PROFILE_ERROR", "Upload avatar failed", t);
                    Toast.makeText(EditProfileActivity.this, "Lỗi kết nối khi upload avatar", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e("PROFILE_ERROR", "Create avatar file failed", e);
            Toast.makeText(this, "Không đọc được ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private File createFileFromUri(Context context, Uri uri) throws Exception {
        String fileName = getFileName(uri);

        File file = new File(context.getCacheDir(), fileName);

        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        FileOutputStream outputStream = new FileOutputStream(file);

        byte[] buffer = new byte[4096];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        inputStream.close();

        return file;
    }

    private String getFileName(Uri uri) {
        String result = "avatar.jpg";

        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);

            if (cursor.moveToFirst() && nameIndex >= 0) {
                result = cursor.getString(nameIndex);
            }

            cursor.close();
        }

        return result;
    }
}