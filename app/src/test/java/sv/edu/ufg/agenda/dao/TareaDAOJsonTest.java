package sv.edu.ufg.agenda.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.util.List;

import sv.edu.ufg.agenda.modelo.Prioridad;
import sv.edu.ufg.agenda.modelo.Tarea;

/**
 * Pruebas de la capa de datos sobre un archivo temporal.
 *
 * <p>{@link TareaDAOJson} recibe un {@link File} y no un {@code Context},
 * precisamente para poder probarse como Java normal, sin emulador.</p>
 */
public class TareaDAOJsonTest {

    @Rule
    public TemporaryFolder carpeta = new TemporaryFolder();

    private TareaDAOJson dao;

    @Before
    public void crearDao() {
        dao = new TareaDAOJson(new File(carpeta.getRoot(), "tareas.json"));
    }

    @Test
    public void insertarAsignaIdsCorrelativosYGuardaElArchivo() {
        dao.insertar(new Tarea(0, 1, "Guía práctica 3", "2026-10-20"));
        dao.insertar(new Tarea(0, 1, "Examen parcial", "2026-10-25"));

        List<Tarea> tareas = dao.listar();

        assertEquals(2, tareas.size());
        assertEquals(1, tareas.get(0).getId());
        assertEquals(2, tareas.get(1).getId());

        // El archivo existe: guardar() se llamó en cada inserción.
        assertTrue(new File(carpeta.getRoot(), "tareas.json").exists());
    }

    @Test
    public void listarPorMateriaDevuelveSoloLasDeEsaMateria() {
        dao.insertar(new Tarea(0, 1, "De la materia uno", "2026-10-20"));
        dao.insertar(new Tarea(0, 2, "De la materia dos", "2026-10-21"));
        dao.insertar(new Tarea(0, 1, "Otra de la materia uno", "2026-10-22"));

        List<Tarea> deLaUno = dao.listarPorMateria(1);

        assertEquals(2, deLaUno.size());
        assertEquals("De la materia uno", deLaUno.get(0).getTitulo());
        assertEquals("Otra de la materia uno", deLaUno.get(1).getTitulo());
    }

    @Test
    public void eliminarQuitaLaTareaDelArchivo() {
        dao.insertar(new Tarea(0, 1, "Se queda", "2026-10-20"));
        dao.insertar(new Tarea(0, 1, "Se va", "2026-10-21"));

        assertTrue(dao.eliminar(2));
        assertFalse(dao.eliminar(2)); // ya no existe

        // Un DAO nuevo sobre el mismo archivo confirma que se escribió.
        TareaDAOJson otro = new TareaDAOJson(new File(carpeta.getRoot(), "tareas.json"));
        List<Tarea> tareas = otro.listar();

        assertEquals(1, tareas.size());
        assertEquals("Se queda", tareas.get(0).getTitulo());
    }

    @Test
    public void buscarPorTituloIgnoraMayusculasYDevuelveCoincidencias() {
        dao.insertar(new Tarea(0, 1, "Guía práctica 3", "2026-10-20"));
        dao.insertar(new Tarea(0, 2, "Guía práctica 4", "2026-10-27"));
        dao.insertar(new Tarea(0, 2, "Examen parcial", "2026-11-02"));

        assertEquals(2, dao.buscarPorTitulo("guía").size());
        assertEquals(1, dao.buscarPorTitulo("EXAMEN").size());
        assertTrue(dao.buscarPorTitulo("no existe").isEmpty());
    }

    @Test
    public void prioridadPorDefectoEsMediaYSeConservaEnElArchivo() {
        dao.insertar(new Tarea(0, 1, "Sin prioridad indicada", "2026-10-20"));
        dao.insertar(new Tarea(0, 1, "Urgente", "2026-10-21", Prioridad.ALTA));

        TareaDAOJson otro = new TareaDAOJson(new File(carpeta.getRoot(), "tareas.json"));
        List<Tarea> tareas = otro.listar();

        assertEquals(Prioridad.MEDIA, tareas.get(0).getPrioridad());
        assertEquals(Prioridad.ALTA, tareas.get(1).getPrioridad());
    }
}
