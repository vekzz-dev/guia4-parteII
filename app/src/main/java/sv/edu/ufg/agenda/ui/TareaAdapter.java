package sv.edu.ufg.agenda.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sv.edu.ufg.agenda.R;
import sv.edu.ufg.agenda.modelo.Tarea;

/**
 * Conecta la lista de tareas de una materia con cada fila del layout.
 * Igual que {@link MateriaAdapter}, no conoce los DAO: avisa a la Activity
 * mediante {@link Acciones}.
 */
public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.Fila> {

    public interface Acciones {
        void editar(Tarea tarea);

        void eliminar(Tarea tarea);

        void cambiarCompletada(Tarea tarea, boolean completada);
    }

    private final List<Tarea> datos = new ArrayList<>();
    private final Acciones acciones;

    public TareaAdapter(Acciones acciones) {
        this.acciones = acciones;
    }

    public void mostrar(List<Tarea> nuevos) {
        datos.clear();
        datos.addAll(nuevos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fila onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        View vista = LayoutInflater.from(padre.getContext())
                .inflate(R.layout.item_tarea, padre, false);
        return new Fila(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull Fila fila, int posicion) {
        Tarea t = datos.get(posicion);

        // Se quita el listener antes de fijar el estado para que el reciclado
        // de filas no dispare un cambio de "completada" que nadie pidió.
        fila.cbCompletada.setOnCheckedChangeListener(null);
        fila.cbCompletada.setChecked(t.isCompletada());

        fila.tvTexto.setText(t.getTitulo() + "\n" + t.getFechaEntrega());
        fila.tvPrioridad.setText("Prioridad: " + t.getPrioridad().etiqueta());
        fila.cbCompletada.setOnCheckedChangeListener(
                (vista, marcada) -> acciones.cambiarCompletada(t, marcada));
        fila.itemView.setOnClickListener(v -> acciones.editar(t));
        fila.btnEliminar.setOnClickListener(v -> acciones.eliminar(t));
    }

    @Override
    public int getItemCount() {
        return datos.size();
    }

    static class Fila extends RecyclerView.ViewHolder {
        final CheckBox cbCompletada;
        final TextView tvTexto;
        final TextView tvPrioridad;
        final Button btnEliminar;

        Fila(@NonNull View vista) {
            super(vista);
            cbCompletada = vista.findViewById(R.id.cbCompletada);
            tvTexto = vista.findViewById(R.id.tvTexto);
            tvPrioridad = vista.findViewById(R.id.tvPrioridad);
            btnEliminar = vista.findViewById(R.id.btnEliminar);
        }
    }
}
