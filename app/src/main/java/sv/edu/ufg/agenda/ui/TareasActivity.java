package sv.edu.ufg.agenda.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import sv.edu.ufg.agenda.AgendaApp;
import sv.edu.ufg.agenda.R;
import sv.edu.ufg.agenda.dao.DAOFactory;
import sv.edu.ufg.agenda.dao.TareaDAO;
import sv.edu.ufg.agenda.modelo.Materia;
import sv.edu.ufg.agenda.modelo.Tarea;

/**
 * Pantalla de tareas de una materia: alta, edición, eliminación y marcado
 * como completada. Recibe el id de la materia por Intent y aplica las
 * validaciones de las reglas de negocio.
 */
public class TareasActivity extends AppCompatActivity implements TareaAdapter.Acciones {

    private TareaDAO tareaDAO;
    private Materia materia;
    private TareaAdapter adapter;
    private TextView tvEncabezado;
    private EditText etTitulo;
    private EditText etFecha;
    private Button btnGuardarTarea;
    private Tarea enEdicion; // null = se está agregando una tarea nueva

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas);

        DAOFactory daos = ((AgendaApp) getApplication()).daos();
        tareaDAO = daos.tareas();

        int materiaId = getIntent().getIntExtra("materiaId", -1);
        materia = daos.materias().buscar(materiaId);

        // Regla 2: una tarea solo existe para una materia que existe.
        if (materia == null) {
            finish();
            return;
        }

        tvEncabezado = findViewById(R.id.tvEncabezado);
        etTitulo = findViewById(R.id.etTitulo);
        etFecha = findViewById(R.id.etFecha);
        btnGuardarTarea = findViewById(R.id.btnGuardarTarea);

        adapter = new TareaAdapter(this);
        RecyclerView lista = findViewById(R.id.rvTareas);
        lista.setLayoutManager(new LinearLayoutManager(this));
        lista.setAdapter(adapter);

        btnGuardarTarea.setOnClickListener(v -> guardar());
        refrescar();
    }

    private void guardar() {
        String titulo = etTitulo.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();

        // Regla 1: el título es obligatorio.
        if (titulo.isEmpty()) {
            etTitulo.setError("El título es obligatorio");
            return;
        }

        // Regla 4: la fecha debe ser válida y con formato aaaa-mm-dd.
        if (!fechaValida(fecha)) {
            etFecha.setError("Escribe una fecha válida con formato aaaa-mm-dd");
            return;
        }

        if (enEdicion == null) {
            tareaDAO.insertar(new Tarea(0, materia.getId(), titulo, fecha));
        } else {
            enEdicion.setTitulo(titulo);
            enEdicion.setFechaEntrega(fecha);
            tareaDAO.actualizar(enEdicion);
        }
        limpiarFormulario();
        refrescar();
    }

    private void limpiarFormulario() {
        enEdicion = null;
        etTitulo.setText("");
        etFecha.setText("");
        btnGuardarTarea.setText("Agregar tarea");
    }

    private void refrescar() {
        List<Tarea> tareas = tareaDAO.listarPorMateria(materia.getId());

        // El formato aaaa-mm-dd se ordena como texto igual que como fecha.
        Collections.sort(tareas, (a, b) -> a.getFechaEntrega().compareTo(b.getFechaEntrega()));

        int completadas = 0;
        for (Tarea t : tareas) {
            if (t.isCompletada()) {
                completadas++;
            }
        }
        tvEncabezado.setText(materia.getNombre() + " · "
                + completadas + " de " + tareas.size() + " completadas");

        adapter.mostrar(tareas);
    }

    private boolean fechaValida(String texto) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        formato.setLenient(false);
        try {
            formato.parse(texto);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    @Override
    public void editar(Tarea tarea) {
        enEdicion = tarea;
        etTitulo.setText(tarea.getTitulo());
        etFecha.setText(tarea.getFechaEntrega());
        btnGuardarTarea.setText("Actualizar tarea");
    }

    @Override
    public void eliminar(Tarea tarea) {
        tareaDAO.eliminar(tarea.getId());
        if (enEdicion != null && enEdicion.getId() == tarea.getId()) {
            limpiarFormulario();
        }
        refrescar();
    }

    @Override
    public void cambiarCompletada(Tarea tarea, boolean completada) {
        tarea.setCompletada(completada);
        tareaDAO.actualizar(tarea);
        refrescar();
    }
}
