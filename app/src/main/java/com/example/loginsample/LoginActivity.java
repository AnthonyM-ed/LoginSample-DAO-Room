package com.example.loginsample;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.loginsample.databinding.ActivityMainBinding;
import com.example.loginsample.model.dao.UsuarioDao;
import com.example.loginsample.model.database.AppDatabase;
import com.example.loginsample.model.ent.UsuarioEntity;

import java.util.List;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private ActivityMainBinding binding;

    private UsuarioDao usuarioDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        EditText edtUsername = binding.edtUsername;
        EditText edtPassword = binding.edtPassword;
        Button btnLogin = binding.btnLogin;
        Button btnAddAccount = binding.btnAddAccount;

        // Obtener la instancia del DAO y la base de datos
        AppDatabase database = AppDatabase.getInstance(this);
        usuarioDao = database.usuarioDao();

        // Funcionalidad de login
        btnLogin.setOnClickListener(v -> {
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Usuario y contraseña son obligatorios", Toast.LENGTH_LONG).show();
                return;
            }

            // Cargar comentarios antes de validar el login
            new Thread(() -> {
                database.cargarComentarios(this);
                runOnUiThread(() -> Toast.makeText(this, "Comentarios cargados", Toast.LENGTH_SHORT).show());
                validarLogin(username, password);
            }).start();
        });

        // Funcionalidad de crear cuenta nueva
        btnAddAccount.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
            startActivity(intent);
        });
    }

    private void validarLogin(String username, String password) {
        // Hacer la búsqueda del usuario en la base de datos
        new Thread(() -> {
            UsuarioEntity usuario = usuarioDao.getUsuarioByUserName(username);

            if (usuario != null && usuario.getUserPassword().equals(password)) {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Bienvenido a mi app", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Bienvenido a mi app");

                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    // Pasar datos del usuario autenticado a la siguiente actividad (opcional)
                    intent.putExtra("USER_NAME", usuario.getUserFirstName());

                    startActivity(intent);
                });
            } else {
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Error en la autenticación", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Error en la autenticación");
                });
            }
        }).start();
    }
}
