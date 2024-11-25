package com.example.loginsample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loginsample.model.dao.UsuarioDao;
import com.example.loginsample.model.database.AppDatabase;
import com.example.loginsample.model.ent.UsuarioEntity;
import com.google.gson.Gson;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AccountActivity extends AppCompatActivity {
    public final static String ACCOUNT_RECORD = "ACCOUNT_RECORD";
    public final static Integer ACCOUNT_ACEPTAR = 100;
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
            UsuarioEntity usuarioEntity = new UsuarioEntity();
            usuarioEntity.setUserFirstName(edtFirstname.getText().toString());
            usuarioEntity.setUserLastName(edtLastname.getText().toString());
            usuarioEntity.setUserEmail(edtEmail.getText().toString());
            usuarioEntity.setUserPhone(edtPhone.getText().toString());
            usuarioEntity.setUserName(edtUsername2.getText().toString());
            usuarioEntity.setUserPassword(edtPassword2.getText().toString());

            // Insertamos el usuario en la base de datos en segundo plano
            executor.execute(() -> {
                // Accedemos a la base de datos
                AppDatabase db = AppDatabase.getInstance(AccountActivity.this);
                UsuarioDao usuarioDao = db.usuarioDao();
                usuarioDao.insertUsuario(usuarioEntity); // Insertamos el nuevo usuario
            });

            // Convierte el objeto UsuarioEntity a JSON
            Gson gson = new Gson();
            String usuarioJson = gson.toJson(usuarioEntity);

            // Creamos el Intent con el objeto serializado
            Intent data = new Intent();
            data.putExtra(ACCOUNT_RECORD, usuarioJson);
            setResult(ACCOUNT_ACEPTAR, data);
            finish();
        });

        btnCancelar.setOnClickListener(v -> {
            setResult(ACCOUNT_CANCELAR);
            finish();
        });
    }
}
