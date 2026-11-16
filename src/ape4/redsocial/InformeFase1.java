package ape4.redsocial;

import java.util.List;

/**
 * Redacta las seis respuestas de la Fase 1 a partir del grafo,
 * sin copiar números a mano: grados, ciclo y consultas salen del objeto.
 */
public class InformeFase1 {

    public String generar(RedSocial red) {
        StringBuilder texto = new StringBuilder();
        linea(texto);
        texto.append(red.getNombre().toUpperCase()).append(" — Fase 1 · Grafos y vértices\n");
        texto.append("Red de estudio entre estudiantes de programación.\n");
        texto.append("Una amistad significa que las dos personas aceptaron verse los apuntes.\n");
        linea(texto);

        responderConjuntos(texto, red);
        responderTipoDeGrafo(texto);
        responderGrados(texto, red);
        responderCiclo(texto, red);
        responderConsultas(texto, red);
        responderRechazo(texto, red);
        return texto.toString();
    }

    private void responderConjuntos(StringBuilder texto, RedSocial red) {
        titulo(texto, 1, "Conjunto de vértices V y conjunto de aristas E");
        texto.append("Cada vértice es un usuario registrado. Cada arista es una amistad mutua.\n\n");
        texto.append("V = {");
        List<Usuario> usuarios = red.getUsuarios();
        for (int i = 0; i < usuarios.size(); i++) {
            if (i > 0) {
                texto.append(", ");
            }
            texto.append(usuarios.get(i).etiqueta());
        }
        texto.append("}\n\n");
        texto.append("E = {");
        List<Amistad> amistades = red.getAmistades();
        for (int i = 0; i < amistades.size(); i++) {
            if (i > 0) {
                texto.append(", ");
            }
            texto.append(amistades.get(i).comoPar());
        }
        texto.append("}\n\n");
        texto.append(red.representacionG()).append("\n\n");
        texto.append("Qué hace cada persona en Nexo:\n");
        for (Usuario usuario : usuarios) {
            texto.append("  - ").append(usuario.etiqueta()).append(": ").append(usuario.getDescripcion()).append("\n");
        }
        texto.append("\n");
    }

    private void responderTipoDeGrafo(StringBuilder texto) {
        titulo(texto, 2, "Por qué el modelo es simple, no dirigido y no ponderado");
        texto.append("Simple. No hay lazos ni amistades repetidas. Nadie queda registrado como amigo de sí mismo, ");
        texto.append("y una pareja de usuarios aporta una sola arista. Si alguien insiste, la red rechaza la solicitud.\n\n");
        texto.append("No dirigido. La amistad en Nexo es mutua: si Lucía comparte apuntes con Sofía, Sofía también los comparte con Lucía. ");
        texto.append("No hay una flecha de «sigue a» en un solo sentido. Por eso la arista se escribe {U02, U03} y no (U02, U03).\n\n");
        texto.append("No ponderado. El sistema solo responde si la amistad existe o no existe. ");
        texto.append("No guarda un número de cercanía, de mensajes ni de horas de estudio. La arista no lleva peso.\n\n");
    }

    private void responderGrados(StringBuilder texto, RedSocial red) {
        titulo(texto, 3, "Grado de cada vértice");
        texto.append("El grado de un usuario es su cantidad de amistades.\n\n");
        for (Usuario usuario : red.getUsuarios()) {
            int grado = red.grado(usuario.getId());
            texto.append("  ").append(usuario.etiqueta()).append(": grado ").append(grado);
            if (grado == 0) {
                texto.append("  <- aislado");
            }
            texto.append("\n");
            List<Usuario> vecinos = red.vecinos(usuario.getId());
            if (vecinos.isEmpty()) {
                texto.append("     Sin amigos todavía.\n");
            } else {
                texto.append("     Amigos: ").append(unir(vecinos)).append("\n");
            }
        }

        Usuario aislado = red.usuariosAislados().get(0);
        Usuario popular = red.usuarioConMayorGrado();
        texto.append("\nUsuario aislado: ").append(aislado.etiqueta());
        texto.append(". Creó la cuenta y todavía no tiene ninguna amistad, así que su grado es 0.\n");
        texto.append("Usuario con más amistades: ").append(popular.etiqueta());
        texto.append(", grado ").append(red.grado(popular.getId())).append(".\n\n");

        int suma = red.sumaDeGrados();
        int doble = 2 * red.cantidadAmistades();
        texto.append("Suma de los grados = ").append(suma).append("\n");
        texto.append("Doble del número de aristas = 2 x ").append(red.cantidadAmistades());
        texto.append(" = ").append(doble).append("\n");
        texto.append("La comprobación ").append(suma == doble ? "sí" : "no");
        texto.append(" se cumple. Cada amistad suma 1 al grado de cada extremo, ");
        texto.append("por eso la suma de los grados es el doble de |E|. ");
        texto.append("Ese resultado se conoce como lema del apretón de manos.\n\n");
    }

    private void responderCiclo(StringBuilder texto, RedSocial red) {
        titulo(texto, 4, "Ciclo y conexión entre usuarios");
        List<Usuario> ciclo = red.encontrarCiclo();
        texto.append("El grafo presenta un ciclo de ").append(ciclo.size()).append(" vértices:\n");
        texto.append("  ");
        for (int i = 0; i < ciclo.size(); i++) {
            if (i > 0) {
                texto.append(" — ");
            }
            texto.append(ciclo.get(i).etiqueta());
        }
        texto.append(" — ").append(ciclo.get(0).etiqueta()).append("\n\n");
        texto.append("Es el círculo de laboratorio: cada una de esas tres personas estudia con las otras dos, ");
        texto.append("y se puede volver al punto de partida sin repetir una amistad.\n\n");
        texto.append("No todos los usuarios están conectados entre sí.\n");
        texto.append("  - Hay ").append(red.cantidadComponentes()).append(" componentes. ");
        texto.append("El grafo no es conexo: desde el usuario aislado no existe un camino hacia los demás.\n");
        texto.append("  - Tampoco es completo. Hay pares dentro del grupo grande que no son amigos directos, ");
        texto.append("aunque algunos sí tengan un amigo en común.\n\n");
    }

    private void responderConsultas(StringBuilder texto, RedSocial red) {
        titulo(texto, 5, "Consultas de existencia, vecinos y grado");
        Amistad si = red.getAmistades().get(0);
        String idSiA = si.getPrimero().getId();
        String idSiB = si.getSegundo().getId();
        String[] parNo = parSinAmistadConocida(red);
        Usuario noA = buscar(red, parNo[0]);
        Usuario noB = buscar(red, parNo[1]);

        texto.append("Pareja con amistad: ").append(si.getPrimero().etiqueta());
        texto.append(" y ").append(si.getSegundo().etiqueta()).append(".\n");
        texto.append("  sonAmigos(\"").append(idSiA).append("\", \"").append(idSiB).append("\") = ");
        texto.append(red.sonAmigos(idSiA, idSiB)).append("\n");
        texto.append("  La consulta debe devolver verdadero: la arista está en E.\n\n");

        texto.append("Pareja sin amistad: ").append(noA.etiqueta()).append(" y ").append(noB.etiqueta()).append(".\n");
        texto.append("  sonAmigos(\"").append(noA.getId()).append("\", \"").append(noB.getId()).append("\") = ");
        texto.append(red.sonAmigos(noA.getId(), noB.getId())).append("\n");
        List<Usuario> comunes = red.vecinosEnComun(noA.getId(), noB.getId());
        if (!comunes.isEmpty()) {
            texto.append("  No existe la arista entre ellos. Aun así comparten amigo: ");
            texto.append(unir(comunes)).append(". Conocer a la misma persona no los hace amigos.\n\n");
        } else {
            texto.append("  La consulta debe devolver falso: esa pareja no está en E.\n\n");
        }

        texto.append("Vecinos y grado se consultan sobre un usuario ya registrado.\n");
        for (String id : new String[] {idSiA, red.usuariosAislados().get(0).getId()}) {
            Usuario usuario = buscar(red, id);
            texto.append("  vecinos(\"").append(id).append("\") = ");
            List<Usuario> vecinos = red.vecinos(id);
            texto.append(vecinos.isEmpty() ? "(nadie)" : unir(vecinos));
            texto.append("\n");
            texto.append("  grado(\"").append(id).append("\") = ").append(red.grado(id));
            texto.append("   // ").append(usuario.getNombre()).append("\n");
        }
        texto.append("\nEl grado coincide con el tamaño de la lista de vecinos, ");
        texto.append("porque cada amigo aporta exactamente una arista.\n\n");
    }

    private void responderRechazo(StringBuilder texto, RedSocial red) {
        titulo(texto, 6, "Relación que el sistema debe rechazar");
        int aristasAntes = red.cantidadAmistades();
        Usuario cualquiera = red.getUsuarios().get(0);
        Amistad existente = red.getAmistades().get(0);

        texto.append("Propuesta principal: un lazo, ").append(cualquiera.etiqueta());
        texto.append(" amigo de sí mismo.\n");
        texto.append(intentar(red, cualquiera.getId(), cualquiera.getId())).append("\n\n");

        texto.append("La misma regla de grafo simple cubre otros dos rechazos.\n\n");
        texto.append("Amistad duplicada, pidiendo al revés ");
        texto.append(existente.getSegundo().etiqueta()).append(" — ");
        texto.append(existente.getPrimero().etiqueta()).append(":\n");
        texto.append(intentar(red, existente.getSegundo().getId(), existente.getPrimero().getId())).append("\n\n");

        texto.append("Relación con un usuario que no existe, ");
        texto.append(cualquiera.etiqueta()).append(" — U99:\n");
        texto.append(intentar(red, cualquiera.getId(), "U99")).append("\n\n");

        texto.append("Después de los tres intentos, |E| sigue siendo ");
        texto.append(red.cantidadAmistades()).append(" (antes era ").append(aristasAntes).append("). ");
        texto.append("Rechazar no agrega vértices ni aristas.\n\n");
    }

    private String intentar(RedSocial red, String idA, String idB) {
        try {
            red.solicitarAmistad(idA, idB);
            return "  Resultado inesperado: la red aceptó la relación.";
        } catch (ReglaRedException ex) {
            return "  Resultado: RECHAZADA [" + ex.getMotivo() + "]\n  Razón: " + ex.getMessage();
        }
    }

    /**
     * Busca dos usuarios que no sean amigos y, si es posible,
     * que compartan al menos un amigo. Así la consulta enseña
     * que un camino de longitud 2 no es lo mismo que una arista.
     */
    private String[] parSinAmistadConocida(RedSocial red) {
        List<Usuario> lista = red.getUsuarios();
        String[] respaldo = null;
        for (int i = 0; i < lista.size(); i++) {
            for (int j = i + 1; j < lista.size(); j++) {
                String a = lista.get(i).getId();
                String b = lista.get(j).getId();
                if (red.sonAmigos(a, b)) {
                    continue;
                }
                if (respaldo == null) {
                    respaldo = new String[] {a, b};
                }
                if (!red.vecinosEnComun(a, b).isEmpty()) {
                    return new String[] {a, b};
                }
            }
        }
        return respaldo;
    }

    private Usuario buscar(RedSocial red, String id) {
        for (Usuario usuario : red.getUsuarios()) {
            if (usuario.getId().equals(id)) {
                return usuario;
            }
        }
        throw new IllegalArgumentException("No está " + id);
    }

    private String unir(List<Usuario> usuarios) {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < usuarios.size(); i++) {
            if (i > 0) {
                texto.append(", ");
            }
            texto.append(usuarios.get(i).etiqueta());
        }
        return texto.toString();
    }

    private void titulo(StringBuilder texto, int numero, String titulo) {
        texto.append(numero).append(". ").append(titulo).append("\n");
    }

    private void linea(StringBuilder texto) {
        texto.append("------------------------------------------------------------\n");
    }
}
