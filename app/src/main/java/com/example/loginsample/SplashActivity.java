package com.example.loginsample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loginsample.model.database.AppDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SplashActivity extends AppCompatActivity {

    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Inicializar la base de datos
        AppDatabase database = AppDatabase.getInstance(this);

        startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(v -> {
            // Deshabilitar el botón para evitar múltiples clics
            startButton.setEnabled(false);

            // Cargar datos en segundo plano
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try {
                    database.cargarUsuarios(this);
                    database.cargarEdificios(this);

                    // Volver al hilo principal para navegar
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Datos cargados correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Cerrar SplashActivity
                    });
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        startButton.setEnabled(true);
                        Toast.makeText(this, "Error al cargar datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
                }
            });
        });
    }
}
