package com.example.rhythmixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rhythmixmobile.adapter.SearchPlaylistAdapter;
import com.example.rhythmixmobile.adapter.SearchSongAdapter;
import com.example.rhythmixmobile.api.MusicApi;
import com.example.rhythmixmobile.api.RetrofitClient;
import com.example.rhythmixmobile.model.Playlist;
import com.example.rhythmixmobile.model.Song;
import com.example.rhythmixmobile.api.PlaylistApi;
import com.example.rhythmixmobile.model.ApiResponse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryFragment extends Fragment {

    public static final String ARG_TYPE = "type";
    public static final int TYPE_PLAYLIST = 0;
    public static final int TYPE_FAVORITE = 1;
    public static final int TYPE_HISTORY = 2;

    private int type;
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    private MusicApi musicApi;
    private PlaylistApi playlistApi;

    private final ActivityResultLauncher<Intent> createPlaylistLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    loadData(); // Refresh list after creating
                }
            }
    );

    public static LibraryFragment newInstance(int type) {
        LibraryFragment fragment = new LibraryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getInt(ARG_TYPE);
        }
        musicApi = RetrofitClient
                .getInstance(requireContext())
                .create(MusicApi.class);

        playlistApi = RetrofitClient
                .getInstance(requireContext())
                .create(PlaylistApi.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library_list, container, false);
        recyclerView = view.findViewById(R.id.rvLibraryList);
        tvEmpty = view.findViewById(R.id.tvEmptyMessage);
        fabAdd = view.findViewById(R.id.fabAddPlaylistFragment);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        
        if (type == TYPE_PLAYLIST) {
            fabAdd.setVisibility(View.VISIBLE);
            fabAdd.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), CreatePlaylistActivity.class);
                createPlaylistLauncher.launch(intent);
            });
        } else {
            fabAdd.setVisibility(View.GONE);
        }
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
    }

    private void loadData() {
        if (type == TYPE_PLAYLIST) {
            fetchMyPlaylists();
        } else if (type == TYPE_FAVORITE) {
            fetchFavorites();
        } else {
            fetchHistory();
        }
    }

    private void fetchMyPlaylists() {
        playlistApi.getMyPlaylists().enqueue(new Callback<ApiResponse<List<Playlist>>>() {
            @Override
            public void onResponse(
                    Call<ApiResponse<List<Playlist>>> call,
                    Response<ApiResponse<List<Playlist>>> response) {

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()
                        && response.body().getData() != null) {

                    List<Playlist> list = response.body().getData();

                    if (list.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setAdapter(null);
                    } else {
                        tvEmpty.setVisibility(View.GONE);

                        SearchPlaylistAdapter adapter =
                                new SearchPlaylistAdapter(list, playlist -> {
                                    openPlaylistDetail(playlist);
                                });

                        recyclerView.setAdapter(adapter);
                    }

                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("Không tải được playlist");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Playlist>>> call, Throwable t) {
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Lỗi tải dữ liệu");
            }
        });
    }

    private void openPlaylistDetail(Playlist playlist) {
        Intent intent = new Intent(getActivity(), PlaylistDetailActivity.class);
        intent.putExtra("playlist", playlist);
        startActivity(intent);
    }

    private void fetchFavorites() {
        musicApi.getFavoriteSongs().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displaySongs(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fetchHistory() {
        musicApi.getListeningHistory().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    displaySongs(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displaySongs(List<Song> songs) {
        if (songs.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setAdapter(null);
        } else {
            tvEmpty.setVisibility(View.GONE);
            SearchSongAdapter adapter = new SearchSongAdapter(songs, song -> {
                Intent intent = new Intent(getActivity(), SongDetailActivity.class);
                intent.putExtra("song", song);
                startActivity(intent);
            });
            recyclerView.setAdapter(adapter);
        }
    }
}
