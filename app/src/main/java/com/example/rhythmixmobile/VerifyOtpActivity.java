package com.example.rhythmixmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rhythmixmobile.api.AuthApi;
import com.example.rhythmixmobile.api.RetrofitClient;
import com.example.rhythmixmobile.model.LoginResponse;
import com.example.rhythmixmobile.model.RegisterRequest;
import com.example.rhythmixmobile.model.VerifyOtpRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOtpActivity extends AppCompatActivity {

    private EditText etOtp;
    private Button btnVerifyOtp;
    private TextView tvResendOtp, tvLogin, tvOtpMessage;

    private String email, username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        email = getIntent().getStringExtra("email");
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");

        etOtp = findViewById(R.id.etOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        tvLogin = findViewById(R.id.tvLogin);
        tvOtpMessage = findViewById(R.id.tvOtpMessage);

        if (email != null && !email.isEmpty()) {
            tvOtpMessage.setText("OTP đã được gửi về " + email);
        }

        btnVerifyOtp.setOnClickListener(v -> verifyOtp());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(VerifyOtpActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void verifyOtp() {
        String otp = etOtp.getText().toString().trim();

        if (otp.isEmpty()) {
            etOtp.setError("Vui lòng nhập OTP");
            return;
        }

        if (otp.length() < 6) {
            etOtp.setError("OTP phải có 6 số");
            return;
        }

        AuthApi authApi = RetrofitClient
                .getInstance(this)
                .create(AuthApi.class);
        VerifyOtpRequest request = new VerifyOtpRequest(email, otp);

        authApi.verifyRegisterOtp(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    createAccount();
                } else {
                    Toast.makeText(VerifyOtpActivity.this, "OTP không đúng hoặc đã hết hạn", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(VerifyOtpActivity.this, "Lỗi kết nối khi xác thực OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createAccount() {
        AuthApi authApi = RetrofitClient
                .getInstance(this)
                .create(AuthApi.class);

        RegisterRequest request = new RegisterRequest(
                email,
                username,
                password
        );

        authApi.register(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(VerifyOtpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(VerifyOtpActivity.this, LoginActivity.class));
                    finish();
                } else {
                    try {
                        String error = response.errorBody() != null
                                ? response.errorBody().string()
                                : "No error body";

                        Log.e("REGISTER_ERROR", "Code: " + response.code());
                        Log.e("REGISTER_ERROR", "Body: " + error);
                    } catch (Exception e) {
                        Log.e("REGISTER_ERROR", "Read error body failed", e);
                    }

                    Toast.makeText(
                            VerifyOtpActivity.this,
                            "Tạo tài khoản thất bại: " + response.code(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("REGISTER_ERROR", "Network error", t);
                Toast.makeText(
                        VerifyOtpActivity.this,
                        "Lỗi kết nối khi tạo tài khoản",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}