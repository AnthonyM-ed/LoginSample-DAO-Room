package com.example.loginsample.model.ent;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Edificios")
public class EdificioEntity {
    @PrimaryKey(autoGenerate = true)
    private int idEdificio;

    private String ediName;
    private String description;
    private int ediImagen; // ID del recurso
    private double ediLatitud;
    private double ediLongitud;
    private int edAudId;

    // Constructor vacío requerido por Room
    public EdificioEntity() {}

    public int getIdEdificio() {
        return idEdificio;
    }

    public void setIdEdificio(int idEdificio) {
        this.idEdificio = idEdificio;
    }

    public String getEdiName() {
        return ediName;
    }

    public void setEdiName(String ediName) {
        this.ediName = ediName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEdiImagen() {
        return ediImagen;
    }

    public void setEdiImagen(int ediImagen) {
        this.ediImagen = ediImagen;
    }

    public double getEdiLatitud() {
        return ediLatitud;
    }

    public void setEdiLatitud(double ediLatitud) {
        this.ediLatitud = ediLatitud;
    }

    public double getEdiLongitud() {
        return ediLongitud;
    }

    public void setEdiLongitud(double ediLongitud) {
        this.ediLongitud = ediLongitud;
    }

    public int getEdAudId() {
        return edAudId;
    }

    public void setEdAudId(int edAudId) {
        this.edAudId = edAudId;
    }
}
