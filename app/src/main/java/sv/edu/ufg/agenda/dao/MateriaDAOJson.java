package sv.edu.ufg.agenda.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sv.edu.ufg.agenda.modelo.Materia;

/**
 * DAO de Materia. Todo el CRUD viene heredado de {@link JsonDAOBase};
 * esta clase solo agrega la búsqueda propia de la entidad.
 */
public class MateriaDAOJson extends JsonDAOBase<Materia> implements MateriaDAO {

    public MateriaDAOJson(File archivo) {
        super(archivo, Materia.class);
    }

    @Override
    public List<Materia> buscarPorNombre(String texto) {
        List<Materia> resultado = new ArrayList<>();
        String buscado = texto.toLowerCase();
        for (Materia m : cargar()) {
            if (m.getNombre().toLowerCase().contains(buscado)) {
                resultado.add(m);
            }
        }
        return resultado;
    }
}
