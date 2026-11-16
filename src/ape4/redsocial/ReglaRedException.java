package ape4.redsocial;

/**
 * Se lanza cuando una solicitud de amistad viola una regla del grafo.
 * El programa no la agrega y conserva las aristas que ya eran válidas.
 */
public class ReglaRedException extends Exception {

    private final MotivoRechazo motivo;

    public ReglaRedException(MotivoRechazo motivo, String mensaje) {
        super(mensaje);
        this.motivo = motivo;
    }

    public MotivoRechazo getMotivo() {
        return motivo;
    }
}
