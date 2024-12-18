package com.example.loginsample.model.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.loginsample.model.ent.EdificioEntity;

import java.util.List;

@Dao
public interface EdificioDao {
    // Insertar un edificio
    @Insert
    void insertEdificio(EdificioEntity edificio);

    // Obtener todos los edificios
    @Query("SELECT * FROM Edificios")
    List<EdificioEntity> getAllEdificios();

    // Obtener un edificio por su ID
    @Query("SELECT * FROM Edificios WHERE idEdificio = :id")
    EdificioEntity getEdificioById(int id);

    // Obtener un edificio por su Nombre
    @Query("SELECT * FROM Edificios WHERE ediName = :name")
    EdificioEntity getEdificioByName(String name);

    // Actualizar un edificio
    @Update
    void updateEdificio(EdificioEntity edificio);

    // Eliminar un edificio
    @Delete
    void deleteEdificio(EdificioEntity edificio);

    @Query("SELECT COUNT(*) FROM Edificios")
    int countEdificios();
}