package ape4.redsocial;

/**
 * Reglas que impiden crear una arista. Cada valor corresponde a algo
 * que un grafo simple no puede representar.
 */
public enum MotivoRechazo {
    LAZO("Lazo: un usuario no puede ser amigo de sí mismo"),
    AMISTAD_DUPLICADA("Amistad duplicada: esa relación ya existe"),
    USUARIO_INEXISTENTE("Usuario inexistente: la arista saldría de V");

    private final String descripcion;

    MotivoRechazo(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
