package sv.edu.ufg.agenda.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import sv.edu.ufg.agenda.AgendaApp;
import sv.edu.ufg.agenda.R;
import sv.edu.ufg.agenda.dao.DAOFactory;
import sv.edu.ufg.agenda.dao.TareaDAO;
import sv.edu.ufg.agenda.modelo.Materia;
import sv.edu.ufg.agenda.modelo.Prioridad;
import sv.edu.ufg.agenda.modelo.Tarea;

/**
 * Pantalla de tareas de una materia: alta, edición, eliminación, búsqueda por
 * título y marcado como completada. Recibe el id de la materia por Intent y
 * aplica las validaciones de las reglas de negocio.
 */
public class TareasActivity extends AppCompatActivity implements TareaAdapter.Acciones {

    private TareaDAO tareaDAO;
    private Materia materia;
    private TareaAdapter adapter;
    private TextView tvEncabezado;
    private EditText etBuscarTarea;
    private EditText etTitulo;
    private EditText etFecha;
    private Spinner spPrioridad;
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
        etBuscarTarea = findViewById(R.id.etBuscarTarea);
        etTitulo = findViewById(R.id.etTitulo);
        etFecha = findViewById(R.id.etFecha);
        spPrioridad = findViewById(R.id.spPrioridad);
        btnGuardarTarea = findViewById(R.id.btnGuardarTarea);

        prepararPrioridades();

        adapter = new TareaAdapter(this);
        RecyclerView lista = findViewById(R.id.rvTareas);
        lista.setLayoutManager(new LinearLayoutManager(this));
        lista.setAdapter(adapter);

        btnGuardarTarea.setOnClickListener(v -> guardar());
        etBuscarTarea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int inicio, int antes, int despues) { }

            @Override
            public void onTextChanged(CharSequence s, int inicio, int antes, int despues) {
                refrescar();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        refrescar();
    }

    /** Llena el desplegable con Alta, Media y Baja, en ese orden. */
    private void prepararPrioridades() {
        Prioridad[] valores = Prioridad.values();
        String[] etiquetas = new String[valores.length];
        for (int i = 0; i < valores.length; i++) {
            etiquetas[i] = valores[i].etiqueta();
        }
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, etiquetas);
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrioridad.setAdapter(adaptador);
        spPrioridad.setSelection(Prioridad.MEDIA.ordinal());
    }

    private Prioridad prioridadSeleccionada() {
        int posicion = spPrioridad.getSelectedItemPosition();
        Prioridad[] valores = Prioridad.values();
        if (posicion < 0 || posicion >= valores.length) {
            return Prioridad.MEDIA;
        }
        return valores[posicion];
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
            tareaDAO.insertar(new Tarea(0, materia.getId(), titulo, fecha, prioridadSeleccionada()));
        } else {
            enEdicion.setTitulo(titulo);
            enEdicion.setFechaEntrega(fecha);
            enEdicion.setPrioridad(prioridadSeleccionada());
            tareaDAO.actualizar(enEdicion);
        }
        limpiarFormulario();
        refrescar();
    }

    private void limpiarFormulario() {
        enEdicion = null;
        etTitulo.setText("");
        etFecha.setText("");
        spPrioridad.setSelection(Prioridad.MEDIA.ordinal());
        btnGuardarTarea.setText("Agregar tarea");
    }

    private void refrescar() {
        List<Tarea> todas = tareaDAO.listarPorMateria(materia.getId());

        int completadas = 0;
        for (Tarea t : todas) {
            if (t.isCompletada()) {
                completadas++;
            }
        }
        tvEncabezado.setText(materia.getNombre() + " · "
                + completadas + " de " + todas.size() + " completadas");

        List<Tarea> visibles = filtrar(todas);

        // El formato aaaa-mm-dd se ordena como texto igual que como fecha.
        Collections.sort(visibles, (a, b) -> a.getFechaEntrega().compareTo(b.getFechaEntrega()));

        adapter.mostrar(visibles);
    }

    /**
     * buscarPorTitulo() trabaja sobre todas las tareas, porque el contrato de
     * TareaDAO no conoce la pantalla; aquí se acota a la materia abierta.
     */
    private List<Tarea> filtrar(List<Tarea> todas) {
        String filtro = etBuscarTarea.getText().toString().trim();
        if (filtro.isEmpty()) {
            return new ArrayList<>(todas);
        }
        List<Tarea> visibles = new ArrayList<>();
        for (Tarea t : tareaDAO.buscarPorTitulo(filtro)) {
            if (t.getMateriaId() == materia.getId()) {
                visibles.add(t);
            }
        }
        return visibles;
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
        spPrioridad.setSelection(tarea.getPrioridad().ordinal());
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
