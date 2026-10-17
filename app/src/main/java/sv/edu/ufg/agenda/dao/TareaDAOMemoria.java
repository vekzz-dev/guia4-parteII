package sv.edu.ufg.agenda.dao;

import java.util.ArrayList;
import java.util.List;

import sv.edu.ufg.agenda.modelo.Tarea;

/**
 * TareaDAO en memoria. Cumple el mismo contrato que {@link TareaDAOJson}.
 */
public class TareaDAOMemoria extends MemoriaDAOBase<Tarea> implements TareaDAO {

    @Override
    public List<Tarea> listarPorMateria(int materiaId) {
        List<Tarea> resultado = new ArrayList<>();
        for (Tarea t : datos()) {
            if (t.getMateriaId() == materiaId) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    @Override
    public List<Tarea> buscarPorTitulo(String texto) {
        List<Tarea> resultado = new ArrayList<>();
        String buscado = texto.toLowerCase();
        for (Tarea t : datos()) {
            if (t.getTitulo().toLowerCase().contains(buscado)) {
                resultado.add(t);
            }
        }
        return resultado;
    }
}
