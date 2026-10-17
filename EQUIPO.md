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
- Custodia de las cuatro reglas de negocio del caso de estudio.

### Nelson Hernandez — rol B (Pasos 9, 10 y 14)

- `dao/JsonDAOBase.java` — `cargar()`, `guardar()` y `siguienteId()` escritos una
  sola vez para cualquier entidad. Usa `java.io` (no `java.nio.file`) y recibe un
  `File`, no un `Context`, para poder probarse sin emulador.
- `dao/MateriaDAOJson.java` y `dao/TareaDAOJson.java` — heredan el CRUD completo
  y solo agregan su método propio.
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

> El árbol de archivos de la guía no incluye un adapter ni un layout de fila para
> las tareas, pero un `RecyclerView` no funciona sin ambos, así que el rol C los
> agregó.

---

## Historial de commits

Los merges los hizo el rol C como integrador.

```
* 1d769aa merge: integra la interfaz movil del rol C
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

## Estado

| Parte | Estado |
|---|---|
| **Parte I** (`../parte1-consola`, sin Git) | Compilada y ejecutada con Maven. `datos/estudiantes.json` coincide con el resultado del Paso 6 y la persistencia entre ejecuciones está verificada. |
| **Parte II** (este repositorio) | Código completo según los Pasos 7 a 13. **No se compiló ni se ejecutó**: no hay toolchain de Android instalado. |

Para retomarlo: abrir esta carpeta en Android Studio, dejar que genere el
*Gradle wrapper* y sincronizar, y después recorrer el flujo del Paso 15 en un
emulador.

### Actividades de la guía pendientes

- [ ] Capturas de `materias.json` y `tareas.json` desde *Device Explorer* (rol B)
- [ ] Atributo `prioridad` en `Tarea` (rol A)
- [ ] Campo de búsqueda con `buscarPorNombre` y `buscarPorTitulo` (rol C con B)
- [ ] Contador de tareas pendientes bajo cada materia (equipo)
- [ ] `MateriaDAOMemoria` / `TareaDAOMemoria` y el cambio de una línea en la
      fábrica (rol C)
- [ ] Prueba JUnit de `TareaDAOJson` con `TemporaryFolder` (rol B)
