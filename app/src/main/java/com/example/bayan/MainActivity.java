package com.example.bayan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.messaging.FirebaseMessaging;

import android.view.View;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerNews;
    private TextView tvSchoolTitle;
    private TextView tvDate;
    private SwipeRefreshLayout swipeRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);
        EdgeToEdge.enable(this);



        //1. On récupère les SharedPreferences
        SharedPreferences prefs = getSharedPreferences("BayanPref", MODE_PRIVATE);
        String code = prefs.getString("school_code", null);

        //Vérification
        if (code == null) {
            startActivity(new Intent(this, CodeActivity.class));
            finish();
            return;
        }

        //connecter au canal firebase de notification
        setupNotifications(code);

        //Initialisation de l'interface Seulement si le code existe
        setContentView(R.layout.activity_main);

        //Initialisation des vues
        recyclerNews = findViewById(R.id.recyclerNews);
        MaterialButton btnDisconnect = findViewById(R.id.btnDisconnect);
        tvSchoolTitle = findViewById(R.id.tvSchoolTitle);
        tvDate = findViewById(R.id.tvDate);

        //La date en haut dans main activity
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEEE d MMMM", java.util.Locale.FRENCH);
        String currentDate = sdf.format(new java.util.Date());

        //L'afficher en majiscule
        tvDate.setText(currentDate.toUpperCase());

        recyclerNews.setLayoutManager(new LinearLayoutManager(this));
        //swipe to refresh
        swipeRefresh = findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeColors(Color.parseColor("#007AFF"));
        swipeRefresh.setOnRefreshListener(() -> {
            //Reprendre le code from Prefs
            String schoolCode = prefs.getString("school_code", null);
            //Appel de le methode fetchNewsByCode
            fetchNewsByCode(schoolCode);
        });

        //Chargement des données
            fetchNewsByCode(code);

        //Gestion de la deconnexion
        btnDisconnect.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Déconnexion")
                    .setMessage("Voulez-vous vraiment quitter cette école ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        // On reutilise l'objet prefs dejà declaree en haut pour le supprimer de sharedPref
                        prefs.edit().remove("school_code").apply();

                        Intent intent = new Intent(MainActivity.this, CodeActivity.class);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }
    private void fetchNewsByCode(String code) {
        SupabaseApi api = SupabaseClient.getApi();

        //1: get org_id from access_code
        api.getOrganisationByCode("eq." + code).enqueue(new Callback<List<Organisation>>() {

            @Override
            public void onResponse(Call<List<Organisation>> call, Response<List<Organisation>> response) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    int orgId = response.body().get(0).getId();
                    String schoolName = response.body().get(0).getName();
                    tvSchoolTitle.setText(schoolName);
                    //2: get news par org_id
                    fetchNews(orgId);
                } else {
                    Toast.makeText(MainActivity.this, "Organisation introuvable", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Organisation>> call, Throwable t) {
                if (swipeRefresh != null) swipeRefresh.setRefreshing(false);
                Toast.makeText(MainActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchNews(int orgId) {
        SupabaseApi api = SupabaseClient.getApi();

        api.getNewsByOrgId("eq." + orgId, "created_at.desc").enqueue(new Callback<List<News>>() {

            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                if (swipeRefresh != null) {
                    swipeRefresh.setRefreshing(false);
                }
                android.util.Log.d("SUPABASE", "Code: " + response.code());
                android.util.Log.d("SUPABASE", "Body: " + response.body());
                android.util.Log.d("SUPABASE", "Error: " + response.errorBody());

                if (response.isSuccessful() && response.body() != null) {
                    List<News> newsList = response.body();
                    NewsAdapter adapter = new NewsAdapter(newsList);
                    recyclerNews.setAdapter(adapter);
                } else {
                    Toast.makeText(MainActivity.this, "Aucune actualité", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                if (swipeRefresh != null) {
                    swipeRefresh.setRefreshing(false);
                }
                android.util.Log.d("BAYANE", "FAILURE: " + t.getMessage());
                android.util.Log.d("BAYANE", "CAUSE: " + t.getCause());
                Toast.makeText(MainActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setupNotifications(String schoolCode) {
        //Remplacant les espaces par '_' pour firebase
        String topic = schoolCode.toLowerCase().trim().replace(" ", "_");

        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("FCM", "Inscrit au canal : " + topic);
                    } else {
                        Log.e("FCM", "Échec de l'abonnement");
                    }
                });
    }
}