package sv.edu.ufg.agenda.modelo;

/**
 * Contrato mínimo que necesita la capa de datos para asignar y leer
 * el identificador sin conocer el tipo concreto de la entidad.
 */
public interface Entidad {

    int getId();

    void setId(int id);
}
