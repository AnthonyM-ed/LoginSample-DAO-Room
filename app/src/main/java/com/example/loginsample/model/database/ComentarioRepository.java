package com.example.loginsample.model.database;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.example.loginsample.model.ent.ComentarioEntity;
import com.example.loginsample.model.ent.EdificioEntity;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ComentarioRepository {
    private final AppDatabase database;

    public ComentarioRepository(AppDatabase database) {
        this.database = database;
    }

    // Obtener comentarios por id del edificio en un hilo de fondo
    public void getCommentsByBuildingId(int buildingId, GetCommentsCallback callback) {
        // Usamos Executor para ejecutar la consulta en un hilo de fondo
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<ComentarioEntity> comments = database.comentarioDao().getComentariosByEdificioId(buildingId);
            // Llamamos al callback en el hilo principal
            new Handler(Looper.getMainLooper()).post(() -> {
                if (callback != null) {
                    callback.onCommentsLoaded(comments);
                }
            });
        });
    }

    // Interfaz para manejar los resultados de la consulta
    public interface GetCommentsCallback {
        void onCommentsLoaded(List<ComentarioEntity> comments);
    }

    // Obtener el id del edificio por nombre con un callback
    public void getBuildingByName(String buildingName, GetBuildingCallback callback) {
        // Usamos Executor para realizar la consulta en un hilo de fondo
        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            // Realizamos la consulta en segundo plano
            EdificioEntity edificio = database.edificioDao().getEdificioByName(buildingName);

            // Llamamos al callback en el hilo principal para pasar el resultado
            new Handler(Looper.getMainLooper()).post(() -> {
                if (callback != null) {
                    callback.onBuildingLoaded(edificio);
                }
            });
        });
    }

    // Interfaz para manejar los resultados de la consulta de edificio
    public interface GetBuildingCallback {
        void onBuildingLoaded(EdificioEntity edificio);
    }

    // Agregar un nuevo comentario
    public void addComment(ComentarioEntity comentario) {
        // Ejecutar la inserción en un hilo de fondo
        new Thread(() -> database.comentarioDao().insertComentario(comentario)).start();
    }

    // Método para obtener todos los edificios (si es necesario)
    public List<EdificioEntity> getAllBuildings() {
        return database.edificioDao().getAllEdificios();
    }
}
