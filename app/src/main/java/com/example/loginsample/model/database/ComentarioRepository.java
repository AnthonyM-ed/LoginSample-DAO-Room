package com.example.loginsample.model.database;

import android.os.Handler;
import android.os.Looper;

import com.example.loginsample.model.ent.ComentarioEntity;
import com.example.loginsample.model.ent.EdificioEntity;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ComentarioRepository {
    private final AppDatabase database;
    private final Executor executor;
    private final Handler mainHandler;

    public ComentarioRepository(AppDatabase database) {

        this.database = database;
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    // Obtener comentarios por id del edificio en un hilo de fondo
    public void getCommentsByBuildingId(int buildingId, GetCommentsCallback callback) {
        executor.execute(() -> {
            try {
                List<ComentarioEntity> comments = database.comentarioDao().getComentariosByEdificioId(buildingId);
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onCommentsLoaded(comments);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onCommentsLoaded(null); // Manejo básico de errores
                    }
                });
            }
        });
    }

    // Interfaz para manejar los resultados de la consulta
    public interface GetCommentsCallback {
        void onCommentsLoaded(List<ComentarioEntity> comments);
    }

    // Obtener el id del edificio por nombre con un callback
    public void getBuildingByName(String buildingName, GetBuildingCallback callback) {
        executor.execute(() -> {
            try {
                EdificioEntity edificio = database.edificioDao().getEdificioByName(buildingName);
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onBuildingLoaded(edificio);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    if (callback != null) {
                        callback.onBuildingLoaded(null); // Manejo básico de errores
                    }
                });
            }
        });
    }

    // Interfaz para manejar los resultados de la consulta de edificio
    public interface GetBuildingCallback {
        void onBuildingLoaded(EdificioEntity edificio);
    }

    // AgregFFFar un nuevo comentario
    public void addComment(ComentarioEntity comentario) {
        executor.execute(() -> {
            try {
                database.comentarioDao().insertComentario(comentario);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Método para obtener todos los edificios (si es necesario)
    public List<EdificioEntity> getAllBuildings() {
        return database.edificioDao().getAllEdificios();
    }

    // Liberar recursos del executor
    public void shutdown() {
        if (executor instanceof ExecutorService) {
            ((ExecutorService) executor).shutdown();
        }
    }
}
