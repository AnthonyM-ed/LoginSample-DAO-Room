package com.example.loginsample.model.database;

import android.content.Context;
import android.content.res.AssetManager;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
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
                                public void onCreate(@androidx.annotation.NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    // Cargar los edificios desde el archivo txt al crear la base de datos
                                    new Thread(() -> {
                                        AppDatabase database = AppDatabase.getInstance(context);
                                        loadBuildingsFromFile(context, database.edificioDao());
                                        loadUsersFromFile(context, database.usuarioDao());
                                    }).start();
                                }
                                // Se puede agregar onOpen() para actualizar datos cuando la base de datos se abre
                                @Override
                                public void onOpen(@androidx.annotation.NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    new Thread(() -> {
                                        AppDatabase database = AppDatabase.getInstance(context);
                                        // Aquí puedes poner lógica para actualizar los edificios si es necesario
                                        // Por ejemplo, verificar la fecha o alguna bandera en la base de datos
                                        Log.d("AppDatabase", "Actualizando registros si es necesario...");
                                        loadBuildingsFromFile(context, database.edificioDao());
                                        loadUsersFromFile(context, database.usuarioDao());
                                    }).start();
                                }
                            }).build();
                }
            }
        }
        return INSTANCE;
    }

    // Migración entre la versión 1 y 2 (si hubo cambios en el esquema)
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Realiza las migraciones necesarias, por ejemplo:
            // database.execSQL("ALTER TABLE edificio ADD COLUMN nueva_columna TEXT");
        }
    };


    // Método para cargar los edificios desde el archivo txt
    private static void loadBuildingsFromFile(Context context, EdificioDao edificioDao) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                AssetManager assetManager = context.getAssets();
                BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("Edificios.txt")));
                String line;

                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 5) { // Ahora hay 5 elementos: nombre, descripción, imagen, latitud y longitud
                        // Verificar si el edificio ya existe
                        EdificioEntity existing = edificioDao.getEdificioByName(parts[0]);
                        if (existing == null) {
                            // Si el edificio no existe, insertamos un nuevo registro
                            EdificioEntity edificio = new EdificioEntity();
                            edificio.setEdiName(parts[0]);
                            edificio.setDescription(parts[1]);

                            // Obtener el ID del recurso de imagen desde el nombre
                            int imageResId = context.getResources().getIdentifier(parts[2], "drawable", context.getPackageName());
                            if (imageResId == 0) {
                                imageResId = R.drawable.baseline_portrait_24;
                                Log.e("AppDatabase", "Imagen no encontrada: " + parts[2]);
                            }
                            edificio.setEdiImagen(imageResId);

                            // Obtener coordenadas desde el archivo (ya están en parts[3] y parts[4])
                            try {
                                double latitud = Double.parseDouble(parts[3]);
                                double longitud = Double.parseDouble(parts[4]);
                                edificio.setEdiLatitud(latitud);
                                edificio.setEdiLongitud(longitud);

                                Log.d("AppDatabase", "Edificio: " +
                                        "Nombre: " + parts[0] + ", " +
                                        "Latitud: " + latitud + ", " +
                                        "Longitud: " + longitud);
                            } catch (NumberFormatException e) {
                                Log.e("AppDatabase", "Error al parsear coordenadas para: " + parts[0]);
                            }

                            // Insertar el edificio en la base de datos
                            edificioDao.insertEdificio(edificio);
                            Log.d("AppDatabase", "Edificio insertado: " + parts[0]);
                        } else {
                            // Si el edificio ya existe, actualizar sus datos
                        }
                    }
                }
                reader.close();
            } catch (IOException e) {
                Log.e("AppDatabase", "Error leyendo Edificios.txt", e);
            }
        });
    }

    // Método para cargar los usuarios desde un archivo txt
    private static void loadUsersFromFile(Context context, UsuarioDao usuarioDao) {
        Executors.newSingleThreadExecutor().execute(() -> {
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
                            Log.d("AppDatabase", "Usuario insertado: " + parts[0]);
                        }else {
                            // Si el usuario ya existe, actualizar sus datos
                        }
                    }
                }
                reader.close();
            } catch (IOException e) {
                Log.e("AppDatabase", "Error leyendo Usuarios.txt", e);
            }
        });
    }

    private static void loadComentariosFromFile(Context context, UsuarioDao usuarioDao, EdificioDao edificioDao, ComentarioDao comentarioDao) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                AssetManager assetManager = context.getAssets();
                BufferedReader reader = new BufferedReader(new InputStreamReader(assetManager.open("Comentarios.txt")));
                String line;

                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length == 4) { // 4 elementos: username, edificioNombre, calificación, comentario
                        String username = parts[0];
                        String edificioNombre = parts[1];
                        int calificacion = Integer.parseInt(parts[2]);
                        String comentarioTexto = parts[3];

                        // Resolver IDs de usuario y edificio
                        int usuarioId = resolveUsuarioId(username, usuarioDao);
                        int edificioId = resolveEdificioId(edificioNombre, edificioDao);

                        // Validar IDs antes de insertar
                        if (usuarioId != -1 && edificioId != -1) {
                            ComentarioEntity comentario = new ComentarioEntity();
                            comentario.setIdUser(usuarioId);
                            comentario.setIdEdificio(edificioId);
                            comentario.setCalificacion(calificacion);
                            comentario.setComentario(comentarioTexto);

                            // Insertar el comentario
                            comentarioDao.insertComentario(comentario);
                            Log.d("AppDatabase", "Comentario insertado: " + comentarioTexto);
                        } else {
                            Log.e("AppDatabase", "Comentario no insertado debido a IDs inválidos");
                        }
                    }
                }
                reader.close();
            } catch (IOException e) {
                Log.e("AppDatabase", "Error leyendo Comentarios.txt", e);
            }
        });
    }


    private static int resolveUsuarioId(String username, UsuarioDao usuarioDao) {
        UsuarioEntity usuario = usuarioDao.getUsuarioByUserName(username);
        if (usuario != null) {
            return usuario.getIdUser();
        } else {
            Log.e("AppDatabase", "Usuario no encontrado: " + username);
            return -1; // ID inválido
        }
    }

    private static int resolveEdificioId(String edificioNombre, EdificioDao edificioDao) {
        EdificioEntity edificio = edificioDao.getEdificioByName(edificioNombre);
        if (edificio != null) {
            return edificio.getIdEdificio();
        } else {
            Log.e("AppDatabase", "Edificio no encontrado: " + edificioNombre);
            return -1; // ID inválido
        }
    }

}
