package sv.edu.ufg.agenda.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sv.edu.ufg.agenda.R;
import sv.edu.ufg.agenda.modelo.Materia;

/**
 * Conecta la lista de materias con cada fila del layout. Avisa a la Activity
 * qué se pulsó mediante la interfaz {@link Acciones}: el adapter no conoce
 * los DAO ni la persistencia.
 *
 * <p>El conteo de tareas pendientes llega ya calculado; el adapter solo lo
 * muestra.</p>
 */
public class MateriaAdapter extends RecyclerView.Adapter<MateriaAdapter.Fila> {

    public interface Acciones {
        void editar(Materia materia);

        void verTareas(Materia materia);

        void eliminar(Materia materia);
    }

    private final List<Materia> datos = new ArrayList<>();
    private Map<Integer, Integer> pendientes = new HashMap<>();
    private final Acciones acciones;

    public MateriaAdapter(Acciones acciones) {
        this.acciones = acciones;
    }

    public void mostrar(List<Materia> nuevos, Map<Integer, Integer> pendientesPorMateria) {
        datos.clear();
        datos.addAll(nuevos);
        pendientes = (pendientesPorMateria != null) ? pendientesPorMateria : new HashMap<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fila onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        View vista = LayoutInflater.from(padre.getContext())
                .inflate(R.layout.item_materia, padre, false);
        return new Fila(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull Fila fila, int posicion) {
        Materia m = datos.get(posicion);
        fila.tvTexto.setText(m.getNombre() + "\n" + m.getDocente());

        Integer pendientesDeLaMateria = pendientes.get(m.getId());
        fila.tvPendientes.setText(textoPendientes(
                (pendientesDeLaMateria != null) ? pendientesDeLaMateria : 0));

        fila.itemView.setOnClickListener(v -> acciones.editar(m));
        fila.btnTareas.setOnClickListener(v -> acciones.verTareas(m));
        fila.btnEliminar.setOnClickListener(v -> acciones.eliminar(m));
    }

    private String textoPendientes(int cantidad) {
        if (cantidad == 0) {
            return "Sin tareas pendientes";
        }
        if (cantidad == 1) {
            return "1 tarea pendiente";
        }
        return cantidad + " tareas pendientes";
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    static class Fila extends RecyclerView.ViewHolder {
        final TextView tvTexto;
        final TextView tvPendientes;
        final Button btnTareas;
        final Button btnEliminar;

        Fila(@NonNull View vista) {
            super(vista);
            tvTexto = vista.findViewById(R.id.tvTexto);
            tvPendientes = vista.findViewById(R.id.tvPendientes);
            btnTareas = vista.findViewById(R.id.btnTareas);
            btnEliminar = vista.findViewById(R.id.btnEliminar);
        }
    }
}
