package com.example.loginsample.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.loginsample.model.ent.ComentarioEntity;

import java.util.List;

@Dao
public interface ComentarioDao {
    // Insertar un comentario
    @Insert
    void insertComentario(ComentarioEntity comentario);

    // Obtener todos los comentarios
    @Query("SELECT * FROM Comentarios")
    List<ComentarioEntity> getAllComentarios();

    // Obtener comentarios por ID de usuario
    @Query("SELECT * FROM Comentarios WHERE idUser = :userId")
    List<ComentarioEntity> getComentariosByUserId(int userId);

    // Obtener comentarios por ID de edificio
    @Query("SELECT * FROM Comentarios WHERE idEdificio = :edificioId")
    List<ComentarioEntity> getComentariosByEdificioId(int edificioId);

    // Actualizar un comentario
    @Update
    void updateComentario(ComentarioEntity comentario);

    // Eliminar un comentario
    @Delete
    void deleteComentario(ComentarioEntity comentario);
}
