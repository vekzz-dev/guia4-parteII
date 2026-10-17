# Agenda Académica — reparto de trabajo

Práctica de la guía **«Patrón DAO con archivos JSON, de la consola a una aplicación móvil»**, Parte II.
Proyecto Android en Java, paquete `sv.edu.ufg.agenda`, minSdk 24.

## Integrantes

| Integrante | Rol | Rama | Responsabilidad |
|---|---|---|---|
| **Diego Vega** | A — Modelo y contrato | `rol-a` | Entidades, interfaces DAO, fábrica, clase `Application`, reglas de integridad |
| **Nelson Hernandez** | B — Persistencia | `rol-b` | `JsonDAOBase<T>`, los DAO JSON y la verificación de los archivos |
| **Kevin Flores** | C — Interfaz móvil | `rol-c` | Layouts, adapters, Activities y validaciones de formulario |

---

## Qué hizo cada uno

### Diego Vega — rol A (Pasos 7, 8 y 11)

- Creó el proyecto Android con la plantilla *Empty Views Activity*, Java, paquete
  `sv.edu.ufg.agenda`, minSdk 24, y agregó Gson 2.11.0 y RecyclerView 1.3.2
  (Paso 0 y Paso 7 de la guía).
- `modelo/Entidad.java` — contrato mínimo `getId()` / `setId()` para que la base
  genérica asigne identificadores sin conocer el tipo.
- `modelo/Materia.java` y `modelo/Tarea.java` — entidades con constructor vacío
  (requisito de Gson), getters y setters.
- `dao/DAO.java` — contrato genérico con las cinco operaciones comunes.
- `dao/MateriaDAO.java` (`buscarPorNombre`) y `dao/TareaDAO.java`
  (`listarPorMateria`).
- `dao/DAOFactory.java` — punto único de construcción de los DAO.
- `AgendaApp.java` — `Application` que mantiene viva la fábrica sobre
  `getFilesDir()/datos`.
- `AndroidManifest.xml` — `android:name=".AgendaApp"` y las dos pantallas.
- `modelo/Prioridad.java` y el atributo `prioridad` en `Tarea` (Alta, Media,
  Baja). Los `tareas.json` escritos antes de este cambio no traen el campo y se
  leen como `MEDIA` en lugar de `null`.
- `buscarPorTitulo(String)` en el contrato `TareaDAO`. El cambio de contrato se
  avisó antes de implementarlo, como manda el acuerdo.
- `testImplementation("junit:junit:4.13.2")` en `app/build.gradle.kts`, para que
  el rol B pueda escribir su prueba sin emulador.
- El interruptor `EN_MEMORIA` de `DAOFactory`: una sola línea pasa toda la
  aplicación del JSON al almacenamiento en memoria.
- Custodia de las cuatro reglas de negocio del caso de estudio.

### Nelson Hernandez — rol B (Pasos 9, 10 y 14)

- `dao/JsonDAOBase.java` — `cargar()`, `guardar()` y `siguienteId()` escritos una
  sola vez para cualquier entidad. Usa `java.io` (no `java.nio.file`) y recibe un
  `File`, no un `Context`, para poder probarse sin emulador.
- `dao/MateriaDAOJson.java` y `dao/TareaDAOJson.java` — heredan el CRUD completo
  y solo agregan su método propio.
- `TareaDAOJson.buscarPorTitulo(String)` — búsqueda por título sin distinguir
  mayúsculas de minúsculas.
- `app/src/test/java/sv/edu/ufg/agenda/dao/TareaDAOJsonTest.java` — cinco pruebas
  con `TemporaryFolder` sobre `insertar`, `listarPorMateria`, `eliminar`,
  `buscarPorTitulo` y la prioridad por defecto. **Escritas, no ejecutadas:** el
  equipo no instaló el toolchain de Android.
- Verificación de `materias.json` y `tareas.json` en el almacenamiento interno
  mediante *Device Explorer* (Paso 14). **Pendiente:** esta verificación requiere
  ejecutar la app en un emulador, cosa que todavía no se hizo (ver *Estado*).

### Kevin Flores — rol C (Pasos 12 y 13)

- `res/layout/activity_materias.xml` y `res/layout/item_materia.xml`.
- `ui/MateriaAdapter.java` — con la interfaz `Acciones` para no acoplar el
  adapter a los DAO.
- `ui/MateriasActivity.java` — alta, edición, eliminación y salto a tareas, con
  el `Toast` que impide borrar una materia que tiene tareas.
- `ui/TareaAdapter.java` y `res/layout/item_tarea.xml` — fila de tarea con
  `CheckBox` de completada y botón de eliminar.
- `ui/TareasActivity.java` — encabezado «N de M completadas», lista ordenada por
  fecha de entrega, alta/edición/eliminación y validación de fecha real con
  `SimpleDateFormat.setLenient(false)`.
- `res/layout/activity_tareas.xml`.
- `dao/MemoriaDAOBase.java`, `dao/MateriaDAOMemoria.java` y
  `dao/TareaDAOMemoria.java` — el reto de DAO en memoria, con una base genérica
  para no repetir el CRUD, igual que la versión JSON.
- Búsqueda en las dos pantallas: `etBuscarMateria` usa `buscarPorNombre()` y
  `etBuscarTarea` usa `buscarPorTitulo()`.
- `Spinner` de prioridad en el formulario de tareas y su lectura en cada fila.
- Contador de tareas pendientes bajo el nombre de cada materia, actualizado
  también al volver de la pantalla de tareas.

> El árbol de archivos de la guía no incluye un adapter ni un layout de fila para
> las tareas, pero un `RecyclerView` no funciona sin ambos, así que el rol C los
> agregó.

---

## Historial de commits

Los merges los hizo el rol C como integrador.

```
*   45e9b0f merge: integra las pantallas ampliadas del rol C
|\
| * 9f71250 feat(ui): agrega busqueda, prioridad y contador de pendientes en las pantallas
|/
*   f0aa021 merge: integra el interruptor de almacenamiento del rol A
|\
| * a1fb466 feat(dao): permite alternar la fabrica entre JSON y memoria con una linea
|/
*   ef7b07f merge: integra los DAO en memoria del rol C
|\
| * e05c35c feat(dao): agrega los DAO en memoria con base generica
|/
*   29ed28d merge: integra la busqueda por titulo y la prueba del rol B
|\
| * 0e14e66 test(dao): agrega prueba de TareaDAOJson con TemporaryFolder
| * 1857f42 feat(dao): implementa buscarPorTitulo en TareaDAOJson
|/
*   569b98d merge: integra prioridad y el contrato de busqueda del rol A
|\
| * 6c24a3b chore(build): agrega JUnit para las pruebas locales de la capa de datos
| * 6cbe227 feat(dao): agrega buscarPorTitulo al contrato de TareaDAO
| * 7560eb4 feat(modelo): agrega Prioridad y el atributo prioridad a Tarea
|/
* ad95414 docs: simplifica el estado del proyecto en EQUIPO.md
* 1acdfc7 docs: documenta el reparto, los commits y el estado de cada rol
*   1d769aa merge: integra la interfaz movil del rol C
|\
| * c613819 feat(ui): completa la pantalla de tareas con orden por fecha y contador
| * 0ab5036 feat(ui): agrega la pantalla de materias con adapter y layouts
|/
* 9071263 merge: integra la fabrica y la clase Application del rol A
|\
| * 2ffd9e8 feat(app): agrega DAOFactory, AgendaApp y registra las pantallas
|/
* 024e712 merge: integra la persistencia JSON del rol B
|\
| * 46f506a feat(dao): implementa MateriaDAOJson y TareaDAOJson sobre la base generica
| * 90270e2 feat(dao): agrega JsonDAOBase generica con lectura y escritura unicas
|/
* 98aba50 merge: integra el contrato del rol A
|\
| * 7809f8c feat(modelo): agrega Entidad, Materia y Tarea con el contrato DAO generico
| * 27ae163 chore(project): crea el proyecto Android con Gson y RecyclerView
|/
* 68b3d59 chore: inicializa repositorio del proyecto Agenda Academica
```

El orden no es casual: `DAOFactory` necesita las implementaciones JSON del rol B,
y las pantallas del rol C necesitan la fábrica del rol A. Cada merge deja `main`
compilable.

---

## Acuerdos del equipo

- **Contrato primero:** las interfaces se escribieron antes que las
  implementaciones. Mientras no cambien, nadie bloquea a nadie.
- **Una rama por rol**, integrada a `main` con un merge, sin commits directos.
- **Un dueño por archivo.** Si hay que tocar un archivo ajeno, se avisa antes.
- **Cambios al contrato se anuncian** al equipo, porque rompen el trabajo de los
  otros dos.
- Las pantallas solo importan la fábrica y las interfaces: **ninguna** referencia
  a `MateriaDAOJson` ni `TareaDAOJson`.

---

## Decisiones de diseño que el equipo tuvo que justificar

### ¿En qué capa se calcula el conteo de tareas pendientes?

En la **pantalla** (`MateriasActivity.contarPendientes()`), no en el DAO. El
conteo combina dos entidades: si `MateriaDAO` tuviera que saber cuántas tareas
tiene una materia, la capa de datos dejaría de tener un contrato independiente
por entidad y el día que se cambie el almacén habría que arrastrar esa lógica.
El adapter solo recibe un `Map<materiaId, pendientes>` y lo muestra; tampoco
consulta los DAO, que es la regla que ya seguía con `Acciones`.

### `buscarPorTitulo()` devuelve coincidencias de todas las materias

El contrato de `TareaDAO` no conoce la pantalla, así que la búsqueda es global y
`TareasActivity` acota el resultado al `materiaId` abierto antes de pasarlo al
adapter. La alternativa —un `buscarPorTitulo(int materiaId, String texto)`— metía
la noción de pantalla dentro del contrato.

### Los DAO en memoria heredan de `MemoriaDAOBase<T>`

Mismo razonamiento que `JsonDAOBase<T>`: el CRUD se escribe una vez. Las dos
implementaciones cumplen exactamente el mismo contrato, así que alternar entre
ellas no toca ni una línea de las pantallas.

### El interruptor `EN_MEMORIA` de `DAOFactory`

Es la prueba práctica de la sección 5 de la guía: cambiar una sola constante
pasa toda la aplicación del archivo JSON a la lista en memoria. Sirve además
para que el equipo pueda avanzar en la interfaz aunque la persistencia todavía
no esté lista.

---

## Pregunta de reflexión de la guía

> Si mañana la institución exige guardar los datos en una base de datos,
> ¿cuántas clases tendrías que modificar en tu proyecto de consola y cuántos
> archivos de la carpeta `ui` en tu app móvil?

**Cero archivos de `ui`.** Y no es una promesa: hay prueba dentro del repositorio.

### Por qué cero

`ui/` importa exactamente esto:

```
sv.edu.ufg.agenda.AgendaApp
sv.edu.ufg.agenda.dao.DAOFactory
sv.edu.ufg.agenda.dao.MateriaDAO
sv.edu.ufg.agenda.dao.TareaDAO
sv.edu.ufg.agenda.modelo.Materia
sv.edu.ufg.agenda.modelo.Prioridad
sv.edu.ufg.agenda.modelo.Tarea
sv.edu.ufg.agenda.R
```

Ni una implementación concreta. Se comprueba con:

```bash
grep -rn "DAOJson\|DAOMemoria" app/src/main/java/sv/edu/ufg/agenda/ui/
# sin resultados
```

Los DAO se construyen en **un solo lugar** del proyecto, el constructor de
`DAOFactory`:

```java
this.materiaDAO = new MateriaDAOJson(new File(carpetaDatos, "materias.json"));
this.tareaDAO = new TareaDAOJson(new File(carpetaDatos, "tareas.json"));
```

### La prueba ya está hecha, no hay que imaginarla

El reto de los DAO en memoria **es** ese cambio. Se agregaron
`MateriaDAOMemoria` y `TareaDAOMemoria`, se tocó una sola constante y ninguna
pantalla, adapter ni layout se enteró. Cambiar a SQLite es el mismo movimiento
con otras dos clases.

### Qué habría que hacer para pasar a SQLite

| Acción | Alcance |
|---|---|
| **Crear** | `MateriaDAOSqlite implements MateriaDAO` y `TareaDAOSqlite implements TareaDAO` |
| **Modificar** | las dos líneas del constructor de `DAOFactory` |
| **No tocar** | todo `ui/`, los cuatro layouts, los adapters, `AgendaApp` y `AndroidManifest.xml` |

### El proyecto de consola

El razonamiento es idéntico: ya existe una interfaz que declara el contrato, así
que basta con una clase nueva que la implemente y cambiar la línea donde se
construye el DAO. El menú, las validaciones y la impresión no se tocan.

### Conclusión

La respuesta es **«una»** y **«ninguno»**: el patrón se aplicó bien en las dos
partes. Si hubiera que tocar una Activity para cambiar de almacén, el DAO no
estaría abstraiendo nada: sería un envoltorio con pasos extra.
