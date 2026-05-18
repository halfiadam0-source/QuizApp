package com.example.quizapp_halfi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quizapp_halfi.network.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Score extends AppCompatActivity {
    Button bLogout, bTry;
    ProgressBar progressBar;
    TextView tvScore;
    int score;

    // Variables pour la localisation
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        tvScore = findViewById(R.id.tvScore);
        progressBar = findViewById(R.id.progressBar);
        bLogout = findViewById(R.id.bLogout);
        bTry = findViewById(R.id.bTry);

        Intent intent = getIntent();
        score = intent.getIntExtra("score", 0);
        progressBar.setProgress(100 * score / 5);
        tvScore.setText(100 * score / 5 + " %");

        // Récupérer la position GPS et envoyer le score
        checkLocationAndSendScore();

        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Merci de votre Participation !", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        bTry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Score.this, QuizActivity.class));
            }
        });
    }

    private void checkLocationAndSendScore() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocationAndSendScore();
        }
    }

    private void getLocationAndSendScore() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        double lat = 0, lng = 0;
                        if (location != null) {
                            lat = location.getLatitude();
                            lng = location.getLongitude();
                        }
                        sendScoreToBackend(score, 5, lat, lng);
                    });
        } catch (SecurityException e) {
            sendScoreToBackend(score, 5, 0, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationAndSendScore();
            } else {
                sendScoreToBackend(score, 5, 0, 0);
            }
        }
    }

    private void sendScoreToBackend(int score, int total, double lat, double lng) {
        String userEmail = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        } else {
            userEmail = "anonymous@quizapp.com";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("total", total);
        result.put("userId", userEmail);
        result.put("percentage", (score * 100 / total));
        result.put("latitude", lat);
        result.put("longitude", lng);

        RetrofitClient.getApiService().submitResult(result).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(Score.this, "✅ Score sauvegardé avec position !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Score.this, "⚠️ Erreur sauvegarde", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(Score.this, "❌ Erreur: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}