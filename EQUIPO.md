# Reparto de trabajo — Parte II · Agenda Académica

Tres integrantes, un solo patrón DAO, dos entidades (`Materia` y `Tarea`).
La regla que hace posible el paralelismo: **el contrato se escribe antes que la
implementación**. Mientras las interfaces no cambien, nadie depende de nadie.

---

## Paso 0 — una sola persona crea el proyecto Android

La guía lo menciona de pasada, pero es el paso que más equipos rompe: si cada
integrante crea su propio proyecto en Android Studio, terminan con tres
proyectos incompatibles y los PR no se pueden fusionar.

**Solo A lo hace, al inicio de la sesión 1, y lo sube a `main` antes de que nadie
más escriba una línea:**

1. Android Studio → *Empty Views Activity*, lenguaje Java, paquete
   `sv.edu.ufg.agenda`, minSdk 24.
2. Agregar en `app/build.gradle.kts`:
   ```kotlin
   implementation("com.google.code.gson:gson:2.11.0")
   implementation("androidx.recyclerview:recyclerview:1.3.2")
   ```
3. *Sync Now*.
4. `git init`, commit inicial, push a `main`.
5. Crear las ramas `rol-a`, `rol-b`, `rol-c` y subirlas.

Después de eso, B y C clonan el repositorio. **No crean proyecto nuevo.**

---

## Propiedad de archivos

Cada archivo tiene **un solo dueño**. Esa es la regla anti-conflicto. Si necesitas
cambiar un archivo ajeno, se pide por el chat del equipo, no se edita.

### Rol A — Modelo y contrato

```
app/src/main/java/sv/edu/ufg/agenda/modelo/Entidad.java
app/src/main/java/sv/edu/ufg/agenda/modelo/Materia.java
app/src/main/java/sv/edu/ufg/agenda/modelo/Tarea.java
app/src/main/java/sv/edu/ufg/agenda/dao/DAO.java
app/src/main/java/sv/edu/ufg/agenda/dao/MateriaDAO.java
app/src/main/java/sv/edu/ufg/agenda/dao/TareaDAO.java
app/src/main/java/sv/edu/ufg/agenda/dao/DAOFactory.java
app/src/main/java/sv/edu/ufg/agenda/AgendaApp.java
app/src/main/AndroidManifest.xml
```

Responsabilidad extra: **custodiar las cuatro reglas de negocio**. Es quien
revisa que ningún PR las viole.

### Rol B — Persistencia

```
app/src/main/java/sv/edu/ufg/agenda/dao/JsonDAOBase.java
app/src/main/java/sv/edu/ufg/agenda/dao/MateriaDAOJson.java
app/src/main/java/sv/edu/ufg/agenda/dao/TareaDAOJson.java
app/src/test/java/sv/edu/ufg/agenda/dao/TareaDAOJsonTest.java   (actividad reto)
```

Responsabilidad extra: verificar los JSON reales en el *Device Explorer*
(`/data/data/sv.edu.ufg.agenda/files/datos/`) y entregar esas capturas.

### Rol C — Interfaz móvil

```
app/src/main/res/layout/activity_materias.xml
app/src/main/res/layout/item_materia.xml
app/src/main/res/layout/activity_tareas.xml
app/src/main/java/sv/edu/ufg/agenda/ui/MateriaAdapter.java
app/src/main/java/sv/edu/ufg/agenda/ui/MateriasActivity.java
app/src/main/java/sv/edu/ufg/agenda/ui/TareasActivity.java
```

Responsabilidad extra: las validaciones de formulario (campos obligatorios y
fecha `aaaa-mm-dd`) y que **ninguna pantalla importe `MateriaDAOJson` ni
`TareaDAOJson`**.

---

## Orden real de trabajo

La guía reparte por pasos, pero los pasos tienen dependencias. Este es el orden
que evita que dos personas se queden esperando.

### Sesión 1 (2 h)

| Momento | Rol A | Rol B | Rol C |
|---|---|---|---|
| 0:00–0:15 | Crea el proyecto Android y sube `main` | Espera el clone | Espera el clone |
| 0:15–0:50 | **Entidad + Materia + Tarea + las 3 interfaces** → commit y PR | Lee la guía, prepara su rama | Diseña los 3 layouts en papel/XML |
| 0:50–1:30 | `DAOFactory` + `AgendaApp` + manifiesto | `JsonDAOBase<T>` | `activity_materias.xml` + `item_materia.xml` + `MateriaAdapter` |
| 1:30–2:00 | Revisa el PR de B | `MateriaDAOJson` + `TareaDAOJson` | `MateriasActivity` |

**El hito de las 0:50 es el contrato fusionado en `main`.** Antes de eso nadie
puede compilar contra las interfaces; después de eso los tres avanzan solos.

### Sesión 2 (2 h)

| Momento | Rol A | Rol B | Rol C |
|---|---|---|---|
| 0:00–1:00 | Verifica las 4 reglas en el código y en la app | Corre el test JUnit + verifica JSON en el dispositivo | `TareasActivity` (Paso 13) |
| 1:00–1:30 | Revisa PR de B | Revisa PR de C | Revisa PR de A |
| 1:30–2:00 | Todos: merge, emulador limpio, recorrido completo del flujo | | |

---

## Protocolo de Git

```bash
# B y C, una sola vez
git clone <url> && cd agenda-academica
git checkout rol-b

# ciclo diario
git pull origin main          # antes de empezar
# ... trabajar ...
git add <archivos propios>
git commit -m "feat(dao): agrega JsonDAOBase generica"
git push origin rol-b
```

Reglas:

- **Una rama por rol.** Nunca se hace commit directo a `main`.
- **`git pull origin main` antes de cada `push`.** Los conflictos se resuelven
  en la rama del rol, no en `main`.
- **Commits con tu nombre en la configuración de Git.** El historial es la
  evidencia de participación:
  ```bash
  git config user.name "Tu Nombre"
  git config user.email "tu.correo@ufg.edu.sv"
  ```
- **Revisión cruzada obligatoria:** A revisa a B, B revisa a C, C revisa a A.
  Nadie aprueba su propio PR.
- **Cambio de contrato = aviso previo.** Si hay que tocar una interfaz o una
  entidad, se avisa al equipo **antes** del commit, porque rompe el trabajo de
  los otros dos. Se hace en un solo PR pequeño y rápido.

---

## Definición de terminado por rol

- **A:** las pantallas compilan sin importar ninguna clase `*DAOJson`; las cuatro
  reglas se cumplen; `AgendaApp` está registrada en el manifiesto.
- **B:** `JsonDAOBase<T>` no repite lectura ni escritura; los dos DAO concretos
  tienen menos de 30 líneas cada uno; el test JUnit pasa sin emulador; los dos
  JSON existen en el dispositivo con datos correctos.
- **C:** crear, editar, eliminar y marcar completada funcionan; la fecha inválida
  se rechaza; el mensaje de "materia con tareas" aparece; el encabezado muestra
  «N de M completadas».

---

## Dónde suele romperse el equipo

| Síntoma | Causa | Prevención |
|---|---|---|
| Tres proyectos distintos | Nadie hizo el Paso 0 | A sube `main` antes de las 0:15 |
| `ClassCastException` al abrir | `AgendaApp` sin `android:name` en el manifiesto | Rol A lo revisa en su PR |
| Conflictos en cada merge | Dos editaron el mismo archivo | Propiedad de archivos estricta |
| C bloqueado una hora | Esperó a que B terminara en vez de programar contra la interfaz | Contrato primero |
| Los datos no persisten | Faltó `guardar()` en alguna operación | Test JUnit de B lo detecta |
