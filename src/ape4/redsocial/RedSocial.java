package ape4.redsocial;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Grafo simple, no dirigido y no ponderado.
 * Los vértices son usuarios y las aristas son amistades mutuas.
 * La lista de adyacencia guarda, para cada id, los ids de sus amigos.
 */
public class RedSocial {

    private final String nombre;
    private final Map<String, Usuario> usuarios = new LinkedHashMap<String, Usuario>();
    private final Map<String, Set<String>> adyacencia = new LinkedHashMap<String, Set<String>>();
    private final List<Amistad> amistades = new ArrayList<Amistad>();

    public RedSocial(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("La red social necesita un nombre.");
        }
        this.nombre = nombre.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public void registrarUsuario(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        if (usuarios.containsKey(usuario.getId())) {
            throw new IllegalArgumentException("Ya existe un usuario con el id " + usuario.getId() + ".");
        }
        for (Usuario existente : usuarios.values()) {
            if (existente.getNombre().equalsIgnoreCase(usuario.getNombre())) {
                throw new IllegalArgumentException("Ya existe un usuario llamado " + usuario.getNombre() + ".");
            }
        }
        usuarios.put(usuario.getId(), usuario);
        adyacencia.put(usuario.getId(), new LinkedHashSet<String>());
    }

    /**
     * Intenta crear una amistad. Si rompe una regla del grafo simple,
     * no modifica la red y explica el rechazo.
     */
    public void solicitarAmistad(String idA, String idB) throws ReglaRedException {
        if (idA == null || idB == null) {
            throw new ReglaRedException(
                    MotivoRechazo.USUARIO_INEXISTENTE,
                    "Se rechaza la relación porque falta uno de los usuarios. Una arista une dos vértices de V."
            );
        }

        Usuario a = usuarios.get(idA.trim());
        Usuario b = usuarios.get(idB.trim());
        if (a == null || b == null) {
            String detalle;
            if (a == null && b == null) {
                detalle = "ninguno de los dos usuarios (" + idA + " y " + idB + ") está registrado";
            } else if (a == null) {
                detalle = "el usuario " + idA + " no está registrado";
            } else {
                detalle = "el usuario " + idB + " no está registrado";
            }
            throw new ReglaRedException(
                    MotivoRechazo.USUARIO_INEXISTENTE,
                    "Se rechaza la relación porque " + detalle
                            + ". Solo se puede unir a personas que ya pertenecen a la red."
            );
        }

        if (a.getId().equals(b.getId())) {
            throw new ReglaRedException(
                    MotivoRechazo.LAZO,
                    "Se rechaza " + a.etiqueta() + " — " + a.etiqueta()
                            + " porque sería un lazo. En un grafo simple una persona no es amiga de sí misma."
            );
        }

        Amistad candidata = new Amistad(a, b);
        if (amistades.contains(candidata)) {
            throw new ReglaRedException(
                    MotivoRechazo.AMISTAD_DUPLICADA,
                    "Se rechaza la relación porque " + a.getNombre() + " y " + b.getNombre()
                            + " ya son amigos. La amistad no tiene dirección, así que invertir el orden no crea otra arista."
            );
        }

        amistades.add(candidata);
        adyacencia.get(a.getId()).add(b.getId());
        adyacencia.get(b.getId()).add(a.getId());
    }

    public boolean sonAmigos(String idA, String idB) {
        exigirRegistrado(idA);
        exigirRegistrado(idB);
        return adyacencia.get(idA).contains(idB);
    }

    public List<Usuario> vecinos(String id) {
        exigirRegistrado(id);
        List<Usuario> lista = new ArrayList<Usuario>();
        for (String vecinoId : adyacencia.get(id)) {
            lista.add(usuarios.get(vecinoId));
        }
        return lista;
    }

    public int grado(String id) {
        exigirRegistrado(id);
        return adyacencia.get(id).size();
    }

    public List<Usuario> getUsuarios() {
        return List.copyOf(usuarios.values());
    }

    public List<Amistad> getAmistades() {
        return List.copyOf(amistades);
    }

    public int cantidadUsuarios() {
        return usuarios.size();
    }

    public int cantidadAmistades() {
        return amistades.size();
    }

    public List<Usuario> usuariosAislados() {
        List<Usuario> aislados = new ArrayList<Usuario>();
        for (Usuario usuario : usuarios.values()) {
            if (grado(usuario.getId()) == 0) {
                aislados.add(usuario);
            }
        }
        return aislados;
    }

    public Usuario usuarioConMayorGrado() {
        Usuario elegido = null;
        int mayor = -1;
        for (Usuario usuario : usuarios.values()) {
            int grado = grado(usuario.getId());
            if (grado > mayor) {
                mayor = grado;
                elegido = usuario;
            }
        }
        return elegido;
    }

    public int sumaDeGrados() {
        int suma = 0;
        for (Usuario usuario : usuarios.values()) {
            suma += grado(usuario.getId());
        }
        return suma;
    }

    public boolean cumpleLemaDeGrados() {
        return sumaDeGrados() == 2 * amistades.size();
    }

    /**
     * Devuelve los vértices de un ciclo, en el orden en que se recorren.
     * Si no hay ciclo, la lista queda vacía. No repite el vértice inicial al final:
     * un ciclo de 3 vértices devuelve exactamente 3 usuarios.
     */
    public List<Usuario> encontrarCiclo() {
        Set<String> visitados = new LinkedHashSet<String>();
        for (String inicio : usuarios.keySet()) {
            if (visitados.contains(inicio)) {
                continue;
            }
            List<String> cicloIds = new ArrayList<String>();
            if (buscarCiclo(inicio, null, visitados, new ArrayList<String>(), cicloIds)) {
                List<Usuario> ciclo = new ArrayList<Usuario>();
                for (String id : cicloIds) {
                    ciclo.add(usuarios.get(id));
                }
                return ciclo;
            }
        }
        return List.of();
    }

    public boolean esConexo() {
        if (usuarios.isEmpty()) {
            return true;
        }
        Set<String> visitados = new LinkedHashSet<String>();
        String inicio = usuarios.keySet().iterator().next();
        recorrer(inicio, visitados);
        return visitados.size() == usuarios.size();
    }

    public int cantidadComponentes() {
        Set<String> visitados = new LinkedHashSet<String>();
        int componentes = 0;
        for (String id : usuarios.keySet()) {
            if (!visitados.contains(id)) {
                recorrer(id, visitados);
                componentes++;
            }
        }
        return componentes;
    }

    public List<Usuario> vecinosEnComun(String idA, String idB) {
        exigirRegistrado(idA);
        exigirRegistrado(idB);
        List<Usuario> comunes = new ArrayList<Usuario>();
        for (String vecinoId : adyacencia.get(idA)) {
            if (adyacencia.get(idB).contains(vecinoId)) {
                comunes.add(usuarios.get(vecinoId));
            }
        }
        return comunes;
    }

    public String representacionG() {
        StringBuilder vertices = new StringBuilder();
        StringBuilder aristas = new StringBuilder();
        for (Usuario usuario : usuarios.values()) {
            if (vertices.length() > 0) {
                vertices.append(", ");
            }
            vertices.append(usuario.getId());
        }
        for (Amistad amistad : amistades) {
            if (aristas.length() > 0) {
                aristas.append(", ");
            }
            aristas.append(amistad.comoPar());
        }
        return "G = (V, E) con V = {" + vertices + "} y E = {" + aristas + "}";
    }

    private boolean buscarCiclo(String actual, String padre, Set<String> visitados,
                                List<String> camino, List<String> ciclo) {
        visitados.add(actual);
        camino.add(actual);
        for (String vecino : adyacencia.get(actual)) {
            if (vecino.equals(padre)) {
                continue;
            }
            int posicion = camino.indexOf(vecino);
            if (posicion >= 0) {
                ciclo.addAll(camino.subList(posicion, camino.size()));
                return true;
            }
            if (!visitados.contains(vecino) && buscarCiclo(vecino, actual, visitados, camino, ciclo)) {
                return true;
            }
        }
        camino.remove(camino.size() - 1);
        return false;
    }

    private void recorrer(String actual, Set<String> visitados) {
        visitados.add(actual);
        for (String vecino : adyacencia.get(actual)) {
            if (!visitados.contains(vecino)) {
                recorrer(vecino, visitados);
            }
        }
    }

    private void exigirRegistrado(String id) {
        if (id == null || !usuarios.containsKey(id)) {
            throw new IllegalArgumentException("No existe un usuario con id " + id + ".");
        }
    }
}
