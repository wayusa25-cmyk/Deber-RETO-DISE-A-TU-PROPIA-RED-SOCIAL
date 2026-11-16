package ape4.redsocial;

import java.util.Objects;

/**
 * Arista no dirigida: una amistad mutua entre dos usuarios distintos.
 * El orden en que se nombran no importa. Mateo-Lucía y Lucía-Mateo
 * son la misma amistad, por eso los identificadores se ordenan al guardar.
 */
public class Amistad {

    private final Usuario primero;
    private final Usuario segundo;

    public Amistad(Usuario a, Usuario b) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("Una amistad necesita dos usuarios.");
        }
        if (a.getId().equals(b.getId())) {
            throw new IllegalArgumentException("Una amistad no puede unir a un usuario consigo mismo.");
        }
        if (a.getId().compareTo(b.getId()) <= 0) {
            this.primero = a;
            this.segundo = b;
        } else {
            this.primero = b;
            this.segundo = a;
        }
    }

    public Usuario getPrimero() {
        return primero;
    }

    public Usuario getSegundo() {
        return segundo;
    }

    public boolean une(String idA, String idB) {
        return (primero.getId().equals(idA) && segundo.getId().equals(idB))
                || (primero.getId().equals(idB) && segundo.getId().equals(idA));
    }

    /** Notación de conjunto: la arista no tiene dirección. */
    public String comoPar() {
        return "{" + primero.getId() + ", " + segundo.getId() + "}";
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (!(otro instanceof Amistad)) {
            return false;
        }
        Amistad amistad = (Amistad) otro;
        return primero.getId().equals(amistad.primero.getId())
                && segundo.getId().equals(amistad.segundo.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(primero.getId(), segundo.getId());
    }

    @Override
    public String toString() {
        return primero.etiqueta() + " — " + segundo.etiqueta();
    }
}
