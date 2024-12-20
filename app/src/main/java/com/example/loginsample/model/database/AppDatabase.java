package com.example.loginsample.model.database;

import android.content.Context;
import android.content.res.AssetManager;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.loginsample.R;
import com.example.loginsample.model.dao.ComentarioDao;
import com.example.loginsample.model.dao.EdificioDao;
import com.example.loginsample.model.dao.UsuarioDao;
import com.example.loginsample.model.ent.ComentarioEntity;
import com.example.loginsample.model.ent.EdificioEntity;
import com.example.loginsample.model.ent.UsuarioEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.concurrent.Executors;

@Database(
        entities = {
                UsuarioEntity.class,
                EdificioEntity.class,
                ComentarioEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UsuarioDao usuarioDao();
    public abstract EdificioDao edificioDao();
    public abstract ComentarioDao comentarioDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "app_database"
                            )
                            .addCallback(new RoomDatabase.Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    // Eliminar la carga automática aquí
                                    Log.d("AppDatabase", "Base de datos creada.");
                                }
                            }).build();
                }
            }
        }
        return INSTANCE;
    }

    // Método para cargar los edificios desde el archivo txt
    private static void loadBuildingsFromFile(Context context, EdificioDao edificioDao) {
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("Edificios.txt")));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 6) { // nombre|descripción|imagen|latitud|longitud|audio
                    EdificioEntity existing = edificioDao.getEdificioByName(parts[0]);
                    if (existing == null) {
                        EdificioEntity edificio = new EdificioEntity();
                        edificio.setEdiName(parts[0]);
                        edificio.setDescription(parts[1]);

                        int imageResId = context.getResources().getIdentifier(parts[2], "drawable", context.getPackageName());
                        if (imageResId == 0) {
                            imageResId = R.drawable.baseline_portrait_24;
                            Log.e("AppDatabase", "Imagen no encontrada: " + parts[2]);
                        }
                        edificio.setEdiImagen(imageResId);

                        try {
                            double latitud = Double.parseDouble(parts[3]);
                            double longitud = Double.parseDouble(parts[4]);
                            edificio.setEdiLatitud(latitud);
                            edificio.setEdiLongitud(longitud);
                        } catch (NumberFormatException e) {
                            Log.e("AppDatabase", "Error al parsear coordenadas para: " + parts[0]);
                        }

                        // Obtener el ID del recurso de audio
                        int audioResId = context.getResources().getIdentifier(parts[5], "raw", context.getPackageName());
                        if (audioResId == 0) {
                            Log.e("AppDatabase", "Audio no encontrado: " + parts[5]);
                        }
                        edificio.setEdAudId(audioResId);

                        edificioDao.insertEdificio(edificio);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            Log.e("AppDatabase", "Error leyendo Edificios.txt", e);
        }
    }


    // Método para cargar los usuarios desde un archivo txt
    private static void loadUsersFromFile(Context context, UsuarioDao usuarioDao) {
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("Usuarios.txt")));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 6) { // username|password|firstName|lastName|email|phone
                    UsuarioEntity existingUser = usuarioDao.getUsuarioByUserName(parts[0]);
                    if (existingUser == null) {
                        UsuarioEntity usuario = new UsuarioEntity();
                        usuario.setUserName(parts[0]);
                        usuario.setUserPassword(parts[1]);
                        usuario.setUserFirstName(parts[2]);
                        usuario.setUserLastName(parts[3]);
                        usuario.setUserEmail(parts[4]);
                        usuario.setUserPhone(parts[5]);

                        usuarioDao.insertUsuario(usuario);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            Log.e("AppDatabase", "Error leyendo Usuarios.txt", e);
        }
    }

    private static void loadComentariosFromFile(Context context, UsuarioDao usuarioDao, EdificioDao edificioDao, ComentarioDao comentarioDao) {
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("Comentarios.txt")));
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) { // username|edificioNombre|calificación|comentario
                    String username = parts[0];
                    String edificioNombre = parts[1];
                    int calificacion = Integer.parseInt(parts[2]);
                    String comentarioTexto = parts[3];

                    int usuarioId = resolveUsuarioId(username, usuarioDao);
                    int edificioId = resolveEdificioId(edificioNombre, edificioDao);

                    if (usuarioId != -1 && edificioId != -1) {
                        ComentarioEntity comentario = new ComentarioEntity();
                        comentario.setIdUser(usuarioId);
                        comentario.setIdEdificio(edificioId);
                        comentario.setCalificacion(calificacion);
                        comentario.setComentario(comentarioTexto);

                        comentarioDao.insertComentario(comentario);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            Log.e("AppDatabase", "Error leyendo Comentarios.txt", e);
        }
    }


    private static int resolveUsuarioId(String username, UsuarioDao usuarioDao) {
        UsuarioEntity usuario = usuarioDao.getUsuarioByUserName(username);
        return usuario != null ? usuario.getIdUser() : -1;
    }

    private static int resolveEdificioId(String edificioNombre, EdificioDao edificioDao) {
        EdificioEntity edificio = edificioDao.getEdificioByName(edificioNombre);
        return edificio != null ? edificio.getIdEdificio() : -1;
    }

    public void cargarEdificios(Context context) {
        Executors.newSingleThreadExecutor().execute(() -> loadBuildingsFromFile(context, edificioDao()));
    }

    public void cargarUsuarios(Context context) {
        Executors.newSingleThreadExecutor().execute(() -> loadUsersFromFile(context, usuarioDao()));
    }

    public void cargarComentarios(Context context) {
        Executors.newSingleThreadExecutor().execute(() -> loadComentariosFromFile(context, usuarioDao(), edificioDao(), comentarioDao()));
    }

}
