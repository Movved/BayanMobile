package com.example.bayan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CodeActivity extends AppCompatActivity {

    Button btnContin;
    EditText ScCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_code);

        btnContin = findViewById(R.id.btnContin);
        ScCode = findViewById(R.id.ScCode);

        btnContin.setOnClickListener(v -> {
            String code = ScCode.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "SVP Enter un code", Toast.LENGTH_SHORT).show();
                return;
            }

            //Validation avant enregistrement
            SupabaseApi api = SupabaseClient.getApi();
            api.getOrganisationByCode("eq." + code).enqueue(new Callback<List<Organisation>>() {

                @Override
                public void onResponse(Call<List<Organisation>> call, Response<List<Organisation>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        // Code exist dans BD -> enregistrer puis startActivity new intent
                        SharedPreferences prefs = getSharedPreferences("BayanPref", MODE_PRIVATE);
                        prefs.edit().putString("school_code", code).apply();

                        startActivity(new Intent(CodeActivity.this, MainActivity.class));
                        finish();
                    } else {
                        //Code introuvable n'enregistrer pas et efficher error
                        Toast.makeText(CodeActivity.this, "Code invalide", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Organisation>> call, Throwable t) {
                    android.util.Log.d("BAYANE", "FAILURE: " + t.getMessage());
                    android.util.Log.d("BAYANE", "CAUSE: " + t.getCause());
                    Toast.makeText(CodeActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}