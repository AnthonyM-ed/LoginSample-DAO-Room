package com.example.loginsample;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginsample.model.dao.UsuarioDao;
import com.example.loginsample.model.database.AppDatabase;
import com.example.loginsample.model.ent.UsuarioEntity;
import com.google.gson.Gson;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountActivity extends AppCompatActivity {
    public final static Integer ACCOUNT_CANCELAR = 200;

    private Executor executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializamos el executor para operaciones en segundo plano
        executor = Executors.newSingleThreadExecutor();

        Button btnAceptar = findViewById(R.id.btnAceptar);
        Button btnCancelar = findViewById(R.id.btnCancelar);

        EditText edtFirstname = findViewById(R.id.edtFirstname);
        EditText edtLastname = findViewById(R.id.edtLastname);
        EditText edtEmail = findViewById(R.id.edtEmail);
        EditText edtPhone = findViewById(R.id.edtPhone);
        EditText edtUsername2 = findViewById(R.id.edtUsername2);
        EditText edtPassword2 = findViewById(R.id.edtPassword2);

        btnAceptar.setOnClickListener(v -> {
            // Recuperamos los datos del formulario
            String firstName = edtFirstname.getText().toString().trim();
            String lastName = edtLastname.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String username = edtUsername2.getText().toString().trim();
            String password = edtPassword2.getText().toString().trim();

            // Validamos los campos
            if (firstName.isEmpty()) {
                Toast.makeText(this, "El nombre es obligatorio.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (lastName.isEmpty()) {
                Toast.makeText(this, "El apellido es obligatorio.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.isEmpty()) {
                Toast.makeText(this, "El correo electrónico es obligatorio.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Validamos el formato del correo electrónico
            Pattern pattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                Toast.makeText(this, "El correo electrónico no es válido.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (phone.isEmpty()) {
                Toast.makeText(this, "El número de teléfono es obligatorio.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (username.isEmpty()) {
                Toast.makeText(this, "El nombre de usuario es obligatorio.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "La contraseña es obligatoria.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Si todas las validaciones son correctas, creamos el objeto UsuarioEntity
            UsuarioEntity usuarioEntity = new UsuarioEntity();
            usuarioEntity.setUserFirstName(firstName);
            usuarioEntity.setUserLastName(lastName);
            usuarioEntity.setUserEmail(email);
            usuarioEntity.setUserPhone(phone);
            usuarioEntity.setUserName(username);
            usuarioEntity.setUserPassword(password);

            // Insertamos el usuario en la base de datos en segundo plano
            executor.execute(() -> {
                try {
                    // Accedemos a la base de datos
                    AppDatabase db = AppDatabase.getInstance(AccountActivity.this);
                    UsuarioDao usuarioDao = db.usuarioDao();
                    usuarioDao.insertUsuario(usuarioEntity); // Insertamos el nuevo usuario
                    runOnUiThread(() -> Toast.makeText(this, "Usuario guardado exitosamente.", Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(this, "Error al guardar el usuario", Toast.LENGTH_LONG).show());
                }
            });

            setResult(RESULT_OK);
            finish();
        });

        btnCancelar.setOnClickListener(v -> {
            setResult(ACCOUNT_CANCELAR);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Apagar el Executor para evitar fugas de memoria
        if (executor instanceof ExecutorService) {
            ((ExecutorService) executor).shutdown();
        }
    }
}
