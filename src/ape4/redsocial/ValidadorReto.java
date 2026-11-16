package ape4.redsocial;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Comprueba las condiciones del Padlet sobre el grafo ya construido.
 * No modifica la red: solo informa qué regla falla, si alguna falla.
 */
public class ValidadorReto {

    public List<String> incumplimientos(RedSocial red) {
        List<String> fallos = new ArrayList<String>();

        if (red.cantidadUsuarios() != 6) {
            fallos.add("Se piden exactamente 6 usuarios y hay " + red.cantidadUsuarios() + ".");
        }
        if (red.cantidadAmistades() != 5) {
            fallos.add("Se piden exactamente 5 amistades y hay " + red.cantidadAmistades() + ".");
        }

        Set<String> ids = new HashSet<String>();
        Set<String> nombres = new HashSet<String>();
        for (Usuario usuario : red.getUsuarios()) {
            if (!ids.add(usuario.getId())) {
                fallos.add("El identificador " + usuario.getId() + " está repetido.");
            }
            if (!nombres.add(usuario.getNombre().toLowerCase())) {
                fallos.add("El nombre " + usuario.getNombre() + " está repetido.");
            }
        }

        if (red.usuariosAislados().isEmpty()) {
            fallos.add("Falta un usuario de grado 0.");
        }

        Usuario popular = red.usuarioConMayorGrado();
        if (popular == null || red.grado(popular.getId()) < 3) {
            fallos.add("Falta un usuario con grado 3 o mayor.");
        }

        if (red.encontrarCiclo().size() < 3) {
            fallos.add("Falta un ciclo de 3 o más vértices.");
        }

        if (!red.cumpleLemaDeGrados()) {
            fallos.add("La suma de los grados no es el doble de las aristas.");
        }

        return fallos;
    }
}
