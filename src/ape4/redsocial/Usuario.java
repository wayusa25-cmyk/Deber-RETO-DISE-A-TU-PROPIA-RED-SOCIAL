package ape4.redsocial;

import java.util.Objects;

/**
 * Vértice de la red: una persona registrada, con identificador propio.
 * La amistad no se guarda aquí; la guarda la red, porque es una relación
 * entre dos usuarios y no un dato de uno solo.
 */
public class Usuario {

    private final String id;
    private final String nombre;
    private final String descripcion;

    public Usuario(String id, String nombre, String descripcion) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador del usuario no puede estar vacío.");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario no puede estar vacío.");
        }
        this.id = id.trim();
        this.nombre = nombre.trim();
        this.descripcion = descripcion == null ? "" : descripcion.trim();
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String etiqueta() {
        return nombre + " (" + id + ")";
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Usuario)) {
            return false;
        }
        Usuario usuario = (Usuario) otro;
        return id.equals(usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return etiqueta();
    }
}
