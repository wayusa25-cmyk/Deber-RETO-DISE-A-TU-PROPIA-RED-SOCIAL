# Nexo — Fase 1 de la APE 4 (grafos y vértices)

Programa en Java, con programación orientada a objetos, de una red social original. Modela amistades como un grafo simple, no dirigido y no ponderado, y responde las seis preguntas de la actividad.

La red se llama **Nexo**. Sirve para que un grupo pequeño de programación comparta apuntes. Una amistad es mutua: si una persona acepta a la otra, las dos pueden verse el material.


## Qué pide la actividad

Documentar en el Padlet, por grupos, la Fase 1:

1. Conjuntos V y E, y la representación G = (V, E).
2. Por qué el modelo es un grafo simple, no dirigido y no ponderado.
3. El grado de cada vértice, el usuario aislado, el de mayor grado, y la comprobación de que la suma de los grados es el doble de las aristas.
4. Un ciclo: qué vértices lo forman y si todos los usuarios están conectados entre sí.
5. Una consulta de amistad que existe, otra que no existe, y cómo se consultan vecinos y grado.
6. Una relación que el sistema rechaza (lazo, duplicada o usuario inexistente) y por qué.

El reto del diseño exige, además:

| Condición | Nexo |
| --- | --- |
| 6 usuarios, con nombre e id distintos | U01 a U06 |
| 5 amistades, contadas una sola vez | las cinco de la tabla de abajo |
| Grafo simple, no dirigido y no ponderado | la clase `RedSocial` no guarda lazos, duplicados, flechas ni pesos |
| Al menos un ciclo de 3 o más vértices | Mateo — Lucía — Sofía |
| Al menos un usuario de grado 0 | Camilo Herrera (U06) |
| Al menos un usuario de grado 3 o más | Mateo Salazar (U01), grado 4 |

Los nombres no son los del caso base (Ana, Bruno, Carla, Diego y Elena).

## La red que se diseñó

Seis estudiantes. Cinco ya se aceptaron como contactos de estudio. Camilo se registró y todavía no agrega a nadie: por eso queda aislado, y el grafo tiene sentido dentro de la aplicación. No es un vértice suelto puesto solo para cumplir el número.

```
                 Andrés (U04)
                     |
 Valentina (U05) — Mateo (U01) — Lucía (U02)
                     |               |
                     +----------- Sofía (U03)

 Camilo (U06) queda fuera del grupo. Grado 0.
```

| Id | Usuario | Papel en Nexo | Grado |
| --- | --- | --- | --- |
| U01 | Mateo Salazar | Organiza el grupo y concentra los contactos | 4 |
| U02 | Lucía Paredes | Laboratorio con Mateo y con Sofía | 2 |
| U03 | Sofía Andrade | Cierra ese círculo de laboratorio | 2 |
| U04 | Andrés Villacís | Pide ayuda a Mateo | 1 |
| U05 | Valentina Ruiz | Intercambia apuntes con Mateo | 1 |
| U06 | Camilo Herrera | Tiene cuenta y ninguna solicitud aceptada | 0 |

Amistades (cada pareja es una sola arista, sin dirección):

| Arista | Por qué existe en la aplicación |
| --- | --- |
| {U01, U02} | Mateo y Lucía hacen laboratorio juntos |
| {U02, U03} | Lucía y Sofía también |
| {U01, U03} | Sofía y Mateo cierran el círculo |
| {U01, U04} | Andrés resuelve dudas con Mateo |
| {U01, U05} | Valentina repasa con Mateo |

Grados: 4 + 2 + 2 + 1 + 1 + 0 = 10. El doble de 5 aristas también es 10.

## Respuestas para el Padlet

El programa imprime este mismo contenido al iniciar

### 1. V, E y G = (V, E)

- **V** = {Mateo Salazar (U01), Lucía Paredes (U02), Sofía Andrade (U03), Andrés Villacís (U04), Valentina Ruiz (U05), Camilo Herrera (U06)}
- **E** = {{U01, U02}, {U02, U03}, {U01, U03}, {U01, U04}, {U01, U05}}
- **G = (V, E)**

Un vértice es un usuario registrado. Una arista es una amistad mutua entre dos usuarios distintos. Camilo está en V aunque no aparezca en ninguna arista: tiene cuenta, pero ninguna amistad.

### 2. Simple, no dirigido y no ponderado

- **Simple.** No hay lazos ni aristas repetidas. Nadie es amigo de sí mismo, y Mateo–Lucía se guarda una sola vez. Si llega una solicitud inválida, `solicitarAmistad` la rechaza y no toca el grafo.
- **No dirigido.** Compartir apuntes es mutuo. Si Lucía es amiga de Sofía, Sofía es amiga de Lucía. Por eso la arista se escribe como conjunto `{U02, U03}` y no como par ordenado.
- **No ponderado.** Nexo solo distingue «son amigos» de «no lo son». No hay un peso de mensajes, de horas ni de cercanía.

### 3. Grados

| Usuario | Grado | Amigos |
| --- | --- | --- |
| Mateo Salazar (U01) | 4 | Lucía, Sofía, Andrés, Valentina |
| Lucía Paredes (U02) | 2 | Mateo, Sofía |
| Sofía Andrade (U03) | 2 | Lucía, Mateo |
| Andrés Villacís (U04) | 1 | Mateo |
| Valentina Ruiz (U05) | 1 | Mateo |
| Camilo Herrera (U06) | 0 | nadie |

- **Aislado:** Camilo Herrera (U06), grado 0.
- **Mayor número de amistades:** Mateo Salazar (U01), grado 4.
- **Suma de grados** = 10.
- **Doble de aristas** = 2 × 5 = 10.

La igualdad se cumple. Cada amistad cuenta una vez en el grado de cada extremo, así que la suma de los grados es siempre el doble de |E| (lema del apretón de manos).

### 4. Ciclo

El ciclo es de tres vértices:

**Mateo (U01) — Lucía (U02) — Sofía (U03) — Mateo (U01)**

Son el grupo de laboratorio: cada uno estudia con los otros dos y el recorrido vuelve al inicio sin repetir una amistad.

**No, no todos los usuarios están conectados entre sí.**

- El grafo **no es conexo**. Hay dos componentes: el grupo de cinco y Camilo solo. Desde Camilo no hay camino hacia nadie.
- El grafo **tampoco es completo**. Andrés y Valentina, por ejemplo, no son amigos, aunque los dos sí lo son de Mateo.

El ciclo solo dice que tres usuarios se alcanzan entre ellos por un circuito. No dice que los seis estén unidos.

### 5. Consultas

**Sí son amigos:** Mateo (U01) y Lucía (U02).

`sonAmigos("U01", "U02")` devuelve `true`, porque `{U01, U02}` está en E.

**No son amigos:** Lucía (U02) y Andrés (U04).

`sonAmigos("U02", "U04")` devuelve `false`. No hay arista entre ellos. Los dos son amigos de Mateo, y eso no crea una amistad directa: un camino de longitud 2 no es una arista.

**Vecinos y grado**

- `vecinos("U01")` devuelve a Lucía, Sofía, Andrés y Valentina. `grado("U01")` devuelve 4.
- `vecinos("U06")` no devuelve a nadie. `grado("U06")` devuelve 0.

El grado es el tamaño de la lista de vecinos. En el menú, las opciones 3 y 4 hacen estas mismas consultas con los id que se escriban.

### 6. Relación rechazada

**Propuesta:** Mateo Salazar (U01) amigo de sí mismo. Es un lazo.

**Resultado:** rechazada.

**Razón:** un grafo simple no tiene lazos. Una amistad une a dos usuarios distintos. `solicitarAmistad("U01", "U01")` lanza `ReglaRedException` con motivo `LAZO` y no agrega ninguna arista.

El mismo método cubre los otros dos casos que cita la pregunta:

| Intento | Motivo | Por qué se rechaza |
| --- | --- | --- |
| Lucía — Mateo, que ya son amigos, aunque se pidan al revés | `AMISTAD_DUPLICADA` | La arista ya está. Al no tener dirección, cambiar el orden no crea otra. |
| Mateo — U99 | `USUARIO_INEXISTENTE` | U99 no está en V. No se puede tender una arista hacia alguien que no tiene cuenta. |

Después de los tres intentos siguen existiendo exactamente 5 amistades.

## Cómo está armado el programa

Cada clase tiene una responsabilidad. La red no es un montón de arreglos sueltos en `main`.

| Clase | Responsabilidad |
| --- | --- |
| `Usuario` | Vértice: id, nombre y qué hace esa persona en Nexo. Dos usuarios son el mismo si tienen el mismo id. |
| `Amistad` | Arista no dirigida. Ordena los id al construirse, así `{U01, U02}` y `{U02, U01}` son iguales. |
| `RedSocial` | El grafo. Registra usuarios, acepta o rechaza amistades, y responde grado, vecinos, ciclo y conexión. |
| `MotivoRechazo` | Las tres reglas que una solicitud puede violar. |
| `ReglaRedException` | Avisa el rechazo sin modificar las aristas que ya eran válidas. |
| `ValidadorReto` | Revisa que el grafo cargado cumpla las condiciones del Padlet. |
| `InformeFase1` | Escribe las seis respuestas con los datos del grafo. |
| `AplicacionNexo` | Carga la red, imprime el informe y abre el menú. |

La estructura interna de `RedSocial` es una **lista de adyacencia**: para cada id, el conjunto de id de sus amigos. Guardar el amigo en los dos extremos hace que la relación quede sin dirección. No hay matriz de pesos porque la amistad no se mide con un número.

Operaciones que usa el informe y el menú:

- `solicitarAmistad(idA, idB)` crea la arista o lanza `ReglaRedException`.
- `sonAmigos(idA, idB)` responde si esa arista existe.
- `vecinos(id)` devuelve a los usuarios unidos por una arista.
- `grado(id)` devuelve cuántos vecinos tiene.
- `encontrarCiclo()` recorre el grafo en profundidad. Si una arista vuelve a alguien que ya está en el camino actual, y no es el usuario del que se acaba de llegar, ahí hay un ciclo.
- `esConexo()` y `cantidadComponentes()` dicen si desde un usuario se puede llegar a todos los demás.

`ValidadorReto` se ejecuta antes de imprimir. Si alguien cambia la red inicial y rompe una condición (por ejemplo, quita el ciclo o conecta a Camilo), el programa lo dice y no presenta el informe como si el reto estuviera cumplido.

## Qué no hay que cambiar si se va a entregar este grafo

El menú deja pedir una amistad nueva. Si la solicitud es válida, la red la acepta y **deja de tener exactamente 5 aristas**. Para la evidencia de la Fase 1 hay que usar el grafo del arranque, el de 5 amistades. Cerrar y volver a abrir el programa lo restaura.

La opción 6 del menú sirve para mostrar en vivo el lazo, la amistad duplicada y el usuario inexistente. Esas tres no modifican E.
