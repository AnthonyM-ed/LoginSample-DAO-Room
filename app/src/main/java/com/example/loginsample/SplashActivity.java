package com.example.loginsample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loginsample.model.database.AppDatabase;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Inicializar la base de datos
        AppDatabase database = AppDatabase.getInstance(this);

        Button startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(v -> {
            // Cargar usuarios y edificios en un hilo separado
            new Thread(() -> {
                database.cargarUsuarios(this);
                database.cargarEdificios(this);
                runOnUiThread(() -> Toast.makeText(this, "Usuarios y edificios cargados", Toast.LENGTH_SHORT).show());
            }).start();

            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Cerrar SplashActivity
        });
    }
}
