package sv.edu.ufg.agenda.modelo;

public class Tarea implements Entidad {

    private int id;
    private int materiaId;
    private String titulo;
    private String fechaEntrega; // texto con formato aaaa-mm-dd
    private boolean completada;
    private Prioridad prioridad = Prioridad.MEDIA;

    public Tarea() { }

    public Tarea(int id, int materiaId, String titulo, String fechaEntrega) {
        this(id, materiaId, titulo, fechaEntrega, Prioridad.MEDIA);
    }

    public Tarea(int id, int materiaId, String titulo, String fechaEntrega, Prioridad prioridad) {
        this.id = id;
        this.materiaId = materiaId;
        this.titulo = titulo;
        this.fechaEntrega = fechaEntrega;
        this.completada = false;
        this.prioridad = prioridad;
    }

    @Override public int getId() { return id; }

    @Override public void setId(int id) { this.id = id; }

    public int getMateriaId() { return materiaId; }

    public void setMateriaId(int materiaId) { this.materiaId = materiaId; }

    public String getTitulo() { return titulo; }

    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getFechaEntrega() { return fechaEntrega; }

    public void setFechaEntrega(String fechaEntrega) { this.fechaEntrega = fechaEntrega; }

    public boolean isCompletada() { return completada; }

    public void setCompletada(boolean completada) { this.completada = completada; }

    /**
     * Un archivo tareas.json escrito antes de existir este atributo no trae
     * "prioridad"; se devuelve MEDIA en lugar de null para que la pantalla no
     * tenga que defenderse de un valor ausente.
     */
    public Prioridad getPrioridad() {
        return (prioridad != null) ? prioridad : Prioridad.MEDIA;
    }

    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }
}
