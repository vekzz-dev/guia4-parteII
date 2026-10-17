package sv.edu.ufg.agenda.dao;

import java.util.ArrayList;
import java.util.List;

import sv.edu.ufg.agenda.modelo.Entidad;

/**
 * Implementación del contrato DAO que guarda los datos en memoria.
 *
 * <p>Sirve para dos cosas: trabajar en equipo sin depender de archivos (por
 * ejemplo mientras el rol B todavía no termina la persistencia) y demostrar
 * que el cliente no cambia cuando cambia el almacén. Los datos se pierden al
 * cerrar la aplicación.</p>
 */
public abstract class MemoriaDAOBase<T extends Entidad> implements DAO<T> {

    private final List<T> datos = new ArrayList<>();

    @Override
    public void insertar(T entidad) {
        entidad.setId(siguienteId());
        datos.add(entidad);
    }

    @Override
    public T buscar(int id) {
        for (T e : datos) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    /** Devuelve una copia: la lista interna no se expone al cliente. */
    @Override
    public List<T> listar() {
        return new ArrayList<>(datos);
    }

    @Override
    public boolean actualizar(T nuevo) {
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).getId() == nuevo.getId()) {
                datos.set(i, nuevo);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean eliminar(int id) {
        return datos.removeIf(e -> e.getId() == id);
    }

    /** Acceso para las clases hijas que necesitan filtrar por su cuenta. */
    protected List<T> datos() {
        return datos;
    }

    private int siguienteId() {
        int mayor = 0;
        for (T e : datos) {
            mayor = Math.max(mayor, e.getId());
        }
        return mayor + 1;
    }
}
