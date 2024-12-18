package com.example.loginsample.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Insert;
import androidx.room.Update;

import com.example.loginsample.model.ent.UsuarioEntity;

import java.util.List;

@Dao
public interface UsuarioDao {
    // Insertar un usuario
    @Insert
    void insertUsuario(UsuarioEntity usuario);

    // Obtener todos los usuarios
    @Query("SELECT * FROM Usuarios")
    List<UsuarioEntity> getAllUsuarios();

    // Obtener un usuario por su ID
    @Query("SELECT * FROM Usuarios WHERE idUser = :id")
    UsuarioEntity getUsuarioById(int id);

    // Obtener un usuario por su nombre de usuario
    @Query("SELECT * FROM Usuarios WHERE userName = :username")
    UsuarioEntity getUsuarioByUserName(String username);

    // Actualizar un usuario
    @Update
    void updateUsuario(UsuarioEntity usuario);

    // Eliminar un usuario
    @Delete
    void deleteUsuario(UsuarioEntity usuario);

    @Query("SELECT COUNT(*) FROM Usuarios")
    int countUsuarios();
}