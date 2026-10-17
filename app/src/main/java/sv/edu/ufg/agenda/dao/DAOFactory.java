package sv.edu.ufg.agenda.dao;

import java.io.File;

/**
 * Punto único de construcción de los DAO.
 *
 * <p>Las pantallas piden aquí sus DAO y nunca usan {@code new} con una clase
 * concreta. Cambiar el almacenamiento (JSON, SQLite, Room, memoria) se reduce
 * a modificar este constructor.</p>
 */
public class DAOFactory {

    /**
     * Poner esto en {@code true} pasa toda la aplicación al almacenamiento en
     * memoria: los datos se pierden al cerrarla, pero sirve para trabajar o
     * demostrar el patrón sin archivos. Es la única línea que hay que cambiar.
     */
    private static final boolean EN_MEMORIA = false;

    private final MateriaDAO materiaDAO;
    private final TareaDAO tareaDAO;

    public DAOFactory(File carpetaDatos) {
        if (EN_MEMORIA) {
            this.materiaDAO = new MateriaDAOMemoria();
            this.tareaDAO = new TareaDAOMemoria();
        } else {
            this.materiaDAO = new MateriaDAOJson(new File(carpetaDatos, "materias.json"));
            this.tareaDAO = new TareaDAOJson(new File(carpetaDatos, "tareas.json"));
        }
    }

    public MateriaDAO materias() {
        return materiaDAO;
    }

    public TareaDAO tareas() {
        return tareaDAO;
    }
}
