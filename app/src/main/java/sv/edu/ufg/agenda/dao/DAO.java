package sv.edu.ufg.agenda.dao;

import java.util.List;

/**
 * Contrato común de acceso a datos. No menciona JSON, archivos ni Android:
 * ese es el punto del patrón DAO.
 */
public interface DAO<T> {

    void insertar(T entidad);

    T buscar(int id);

    List<T> listar();

    boolean actualizar(T entidad);

    boolean eliminar(int id);
}
