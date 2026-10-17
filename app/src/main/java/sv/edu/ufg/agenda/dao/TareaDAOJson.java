package sv.edu.ufg.agenda.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sv.edu.ufg.agenda.modelo.Tarea;

/**
 * DAO de Tarea. Todo el CRUD viene heredado de {@link JsonDAOBase};
 * esta clase solo agrega el filtro por materia.
 */
public class TareaDAOJson extends JsonDAOBase<Tarea> implements TareaDAO {

    public TareaDAOJson(File archivo) {
        super(archivo, Tarea.class);
    }

    @Override
    public List<Tarea> listarPorMateria(int materiaId) {
        List<Tarea> resultado = new ArrayList<>();
        for (Tarea t : cargar()) {
            if (t.getMateriaId() == materiaId) {
                resultado.add(t);
            }
        }
        return resultado;
    }
}
