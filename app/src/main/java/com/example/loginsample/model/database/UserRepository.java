package com.example.loginsample.model.database;

import android.os.Handler;
import android.os.Looper;

import com.example.loginsample.model.ent.UsuarioEntity;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserRepository {
    private final AppDatabase database;

    // El repositorio ahora recibe la base de datos ya inicializada
    public UserRepository(AppDatabase database) {
        this.database = database;  // Usamos la base de datos proporcionada
    }

    // Obtener un usuario por ID en segundo plano
    public void getUsuarioById(int userId, GetUserCallback callback) {
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            // Realizamos la consulta en un hilo de fondo
            UsuarioEntity usuario = database.usuarioDao().getUsuarioById(userId);

            // Llamamos al callback en el hilo principal para actualizar la UI
            new Handler(Looper.getMainLooper()).post(() -> {
                if (callback != null) {
                    callback.onUserLoaded(usuario);
                }
            });
        });
    }

    // Interfaz para manejar el resultado de la consulta
    public interface GetUserCallback {
        void onUserLoaded(UsuarioEntity usuario);
    }
}
