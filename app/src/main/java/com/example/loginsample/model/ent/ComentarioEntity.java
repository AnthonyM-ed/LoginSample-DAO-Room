package com.example.loginsample.model.ent;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "Comentarios",
        foreignKeys = {
                @ForeignKey(
                        entity = UsuarioEntity.class,
                        parentColumns = "idUser",
                        childColumns = "idUser",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = EdificioEntity.class,
                        parentColumns = "idEdificio",
                        childColumns = "idEdificio",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class ComentarioEntity {
    @PrimaryKey(autoGenerate = true)
    private int idComentario;

    private int idUser; // Foreign key
    private int idEdificio; // Foreign key
    private int calificacion; // Por ejemplo, de 1 a 5
    private String comentario;

    // Constructor vacío requerido por Room
    public ComentarioEntity() {}

    public int getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(int idComentario) {
        this.idComentario = idComentario;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdEdificio() {
        return idEdificio;
    }

    public void setIdEdificio(int idEdificio) {
        this.idEdificio = idEdificio;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
