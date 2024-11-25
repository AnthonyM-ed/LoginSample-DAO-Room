package com.example.loginsample.model.ent;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity(tableName = "Usuarios")
public class UsuarioEntity {
    @PrimaryKey(autoGenerate = true)
    private int idUser;

    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userPhone;
    private String userName;
    private String userPassword;

    public UsuarioEntity() {}

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        if (userFirstName == null || userFirstName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        if (userLastName == null || userLastName.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }
        this.userLastName = userLastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio.");
        }
        // Validar formato de correo electrónico
        Pattern pattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
        Matcher matcher = pattern.matcher(userEmail);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("El correo electrónico no es válido.");
        }
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        if (userPhone == null || userPhone.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de teléfono es obligatorio.");
        }
        this.userPhone = userPhone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        if (userPassword == null || userPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
        if (userPassword.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }
        this.userPassword = userPassword;
    }
}
