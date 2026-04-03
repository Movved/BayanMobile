package com.example.bayan;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Configuration de la Toolbar pour le bouton retour
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        //Initialisation des vues
        TextView tvTitle = findViewById(R.id.detailTitle);
        TextView tvDesc = findViewById(R.id.detailDescription);
        TextView tvCat = findViewById(R.id.detailCategorie);
        TextView tvDate = findViewById(R.id.detailDate);

        //Recuperer les donnees de l'Intent (envoyees par l'Adapter)
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String category = getIntent().getStringExtra("category");
        String date = getIntent().getStringExtra("date");

        //Affichage des donnees
        tvTitle.setText(title);
        tvDesc.setText(description);
        tvCat.setText(category);
        tvDate.setText(date);
    }

    //Gestion du bouton retour
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); //Ferme cette activite et retourne à MainActivity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}