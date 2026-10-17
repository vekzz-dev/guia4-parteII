package sv.edu.ufg.agenda.dao;

import java.util.ArrayList;
import java.util.List;

import sv.edu.ufg.agenda.modelo.Materia;

/**
 * MateriaDAO en memoria. Cumple el mismo contrato que {@link MateriaDAOJson},
 * así que una pantalla no distingue una implementación de la otra.
 */
public class MateriaDAOMemoria extends MemoriaDAOBase<Materia> implements MateriaDAO {

    @Override
    public List<Materia> buscarPorNombre(String texto) {
        List<Materia> resultado = new ArrayList<>();
        String buscado = texto.toLowerCase();
        for (Materia m : datos()) {
            if (m.getNombre().toLowerCase().contains(buscado)) {
                resultado.add(m);
            }
        }
        return resultado;
    }
}
