package sv.edu.ufg.agenda.modelo;

/**
 * Prioridad de una tarea. Se guarda en el JSON por su nombre
 * ("ALTA", "MEDIA", "BAJA") y se muestra al usuario en forma legible.
 */
public enum Prioridad {

    ALTA,
    MEDIA,
    BAJA;

    /** Texto para la interfaz: "Alta", "Media", "Baja". */
    public String etiqueta() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
