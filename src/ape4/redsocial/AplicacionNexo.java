package ape4.redsocial;

import java.util.List;
import java.util.Scanner;

/**
 * Nexo es una red pequeña para un grupo de programación.
 * Seis estudiantes tienen cuenta. Cinco amistades ya fueron aceptadas.
 * Mateo concentra los contactos, tres de ellos forman un círculo de
 * laboratorio y Camilo todavía no agrega a nadie.
 *
 * Desde la terminal del editor, en la carpeta del proyecto:
 *   javac -encoding UTF-8 -d out src\ape4\redsocial\*.java
 *   java -cp out ape4.redsocial.AplicacionNexo
 *
 * Con el argumento --informe solo imprime las respuestas y termina.
 */
public class AplicacionNexo {

    public static void main(String[] args) {
        RedSocial nexo = crearNexo();
        List<String> fallos = new ValidadorReto().incumplimientos(nexo);
        if (!fallos.isEmpty()) {
            System.out.println("La red no cumple el reto:");
            for (String fallo : fallos) {
                System.out.println(" - " + fallo);
            }
            return;
        }

        System.out.println(new InformeFase1().generar(nexo));
        System.out.println("Condiciones del reto: CUMPLIDAS");
        System.out.println("6 usuarios, 5 amistades, ciclo de 3, un aislado y un grado mayor o igual que 3.");
        System.out.println();

        boolean soloInforme = args.length > 0 && "--informe".equals(args[0]);
        if (!soloInforme) {
            menu(nexo);
        }
    }

    /**
     * Grafo de la actividad. No usa el caso Ana, Bruno, Carla, Diego y Elena.
     *
     *     Andrés (U04)
     *         |
     *     Mateo (U01) —— Lucía (U02)
     *      |    |            |
     *      |    +—— Sofía (U03)
     *      |
     *  Valentina (U05)
     *
     *  Camilo (U06) queda fuera, con grado 0.
     */
    static RedSocial crearNexo() {
        RedSocial nexo = new RedSocial("Nexo");
        nexo.registrarUsuario(new Usuario("U01", "Mateo Salazar",
                "Organiza el grupo de estudio y es el contacto de casi todos."));
        nexo.registrarUsuario(new Usuario("U02", "Lucía Paredes",
                "Hace el laboratorio con Mateo y con Sofía."));
        nexo.registrarUsuario(new Usuario("U03", "Sofía Andrade",
                "Cierra el círculo de laboratorio con Mateo y Lucía."));
        nexo.registrarUsuario(new Usuario("U04", "Andrés Villacís",
                "Le pide ayuda a Mateo cuando se atasca en las prácticas."));
        nexo.registrarUsuario(new Usuario("U05", "Valentina Ruiz",
                "Intercambia apuntes con Mateo antes de los parciales."));
        nexo.registrarUsuario(new Usuario("U06", "Camilo Herrera",
                "Abrió su cuenta esta semana y aún no envió ninguna solicitud."));

        try {
            nexo.solicitarAmistad("U01", "U02");
            nexo.solicitarAmistad("U02", "U03");
            nexo.solicitarAmistad("U03", "U01");
            nexo.solicitarAmistad("U01", "U04");
            nexo.solicitarAmistad("U01", "U05");
        } catch (ReglaRedException ex) {
            throw new IllegalStateException("La red inicial no debería rechazar estas amistades.", ex);
        }
        return nexo;
    }

    private static void menu(RedSocial nexo) {
        Scanner entrada = new Scanner(System.in);
        boolean seguir = true;
        while (seguir) {
            System.out.println("------------------------------------------------------------");
            System.out.println("MENÚ DE " + nexo.getNombre().toUpperCase());
            System.out.println("1. Ver usuarios y grados");
            System.out.println("2. Ver amistades");
            System.out.println("3. Consultar si dos usuarios son amigos");
            System.out.println("4. Consultar vecinos y grado de un usuario");
            System.out.println("5. Volver a imprimir el informe de la Fase 1");
            System.out.println("6. Solicitar una amistad");
            System.out.println("7. Salir");
            System.out.print("Opción: ");
            String opcion = entrada.nextLine().trim();
            System.out.println();

            switch (opcion) {
                case "1":
                    mostrarUsuarios(nexo);
                    break;
                case "2":
                    mostrarAmistades(nexo);
                    break;
                case "3":
                    consultarAmistad(nexo, entrada);
                    break;
                case "4":
                    consultarVecinos(nexo, entrada);
                    break;
                case "5":
                    System.out.println(new InformeFase1().generar(nexo));
                    break;
                case "6":
                    solicitar(nexo, entrada);
                    break;
                case "7":
                    seguir = false;
                    System.out.println("Sesión cerrada.");
                    break;
                default:
                    System.out.println("Elige una opción del 1 al 7.");
                    break;
            }
        }
    }

    private static void mostrarUsuarios(RedSocial nexo) {
        for (Usuario usuario : nexo.getUsuarios()) {
            System.out.println(usuario.etiqueta() + "  grado " + nexo.grado(usuario.getId()));
            System.out.println("   " + usuario.getDescripcion());
        }
        System.out.println();
    }

    private static void mostrarAmistades(RedSocial nexo) {
        System.out.println(nexo.representacionG());
        int numero = 1;
        for (Amistad amistad : nexo.getAmistades()) {
            System.out.println(numero + ". " + amistad);
            numero++;
        }
        System.out.println("Total de amistades: " + nexo.cantidadAmistades());
        System.out.println();
    }

    private static void consultarAmistad(RedSocial nexo, Scanner entrada) {
        System.out.println("Identificadores: U01, U02, U03, U04, U05, U06");
        System.out.print("Primer id: ");
        String a = entrada.nextLine().trim().toUpperCase();
        System.out.print("Segundo id: ");
        String b = entrada.nextLine().trim().toUpperCase();
        try {
            boolean amigos = nexo.sonAmigos(a, b);
            System.out.println(amigos
                    ? "Sí existe la amistad. La consulta devuelve true."
                    : "No existe la amistad. La consulta devuelve false.");
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println();
    }

    private static void consultarVecinos(RedSocial nexo, Scanner entrada) {
        System.out.print("Id del usuario: ");
        String id = entrada.nextLine().trim().toUpperCase();
        try {
            List<Usuario> vecinos = nexo.vecinos(id);
            System.out.println("Grado: " + nexo.grado(id));
            if (vecinos.isEmpty()) {
                System.out.println("No tiene vecinos. Es un usuario aislado.");
            } else {
                System.out.println("Vecinos:");
                for (Usuario vecino : vecinos) {
                    System.out.println(" - " + vecino.etiqueta());
                }
            }
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println();
    }

    private static void solicitar(RedSocial nexo, Scanner entrada) {
        System.out.println("Escribe los dos id. Prueba U01 con U01 (lazo), U02 con U01 (duplicada) o U01 con U99.");
        System.out.print("Primer id: ");
        String a = entrada.nextLine().trim().toUpperCase();
        System.out.print("Segundo id: ");
        String b = entrada.nextLine().trim().toUpperCase();
        try {
            nexo.solicitarAmistad(a, b);
            System.out.println("Amistad aceptada. Ahora hay " + nexo.cantidadAmistades() + " aristas.");
            System.out.println("La red del informe inicial tenía exactamente 5. Reinicia el programa para volver a ese grafo.");
        } catch (ReglaRedException ex) {
            System.out.println("RECHAZADA [" + ex.getMotivo() + "]");
            System.out.println(ex.getMessage());
            System.out.println("Las amistades siguen siendo " + nexo.cantidadAmistades() + ".");
        }
        System.out.println();
    }
}
