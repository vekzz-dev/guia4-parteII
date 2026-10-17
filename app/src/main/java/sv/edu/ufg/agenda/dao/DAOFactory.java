package sv.edu.ufg.agenda.dao;

import java.io.File;

/**
 * Punto único de construcción de los DAO.
 *
 * <p>Las pantallas piden aquí sus DAO y nunca usan {@code new} con una clase
 * JSON concreta. Cambiar el almacenamiento (JSON, SQLite, Room) se reduce a
 * modificar este constructor.</p>
 */
public class DAOFactory {

    private final MateriaDAO materiaDAO;
    private final TareaDAO tareaDAO;

    public DAOFactory(File carpetaDatos) {
        this.materiaDAO = new MateriaDAOJson(new File(carpetaDatos, "materias.json"));
        this.tareaDAO = new TareaDAOJson(new File(carpetaDatos, "tareas.json"));
    }

    public MateriaDAO materias() {
        return materiaDAO;
    }

    public TareaDAO tareas() {
        return tareaDAO;
    }
}
