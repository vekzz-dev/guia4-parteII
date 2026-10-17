package sv.edu.ufg.agenda.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import sv.edu.ufg.agenda.AgendaApp;
import sv.edu.ufg.agenda.R;
import sv.edu.ufg.agenda.dao.DAOFactory;
import sv.edu.ufg.agenda.dao.MateriaDAO;
import sv.edu.ufg.agenda.dao.TareaDAO;
import sv.edu.ufg.agenda.modelo.Materia;

/**
 * Pantalla de materias. Solo conoce las interfaces del DAO y la fábrica:
 * si mañana la implementación deja de ser JSON, esta clase no cambia.
 */
public class MateriasActivity extends AppCompatActivity implements MateriaAdapter.Acciones {

    private MateriaDAO materiaDAO;
    private TareaDAO tareaDAO;
    private MateriaAdapter adapter;
    private EditText etNombre;
    private EditText etDocente;
    private Button btnGuardar;
    private Materia enEdicion; // null = se está agregando una materia nueva

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materias);

        DAOFactory daos = ((AgendaApp) getApplication()).daos();
        materiaDAO = daos.materias();
        tareaDAO = daos.tareas();

        etNombre = findViewById(R.id.etNombre);
        etDocente = findViewById(R.id.etDocente);
        btnGuardar = findViewById(R.id.btnGuardar);

        adapter = new MateriaAdapter(this);
        RecyclerView lista = findViewById(R.id.rvMaterias);
        lista.setLayoutManager(new LinearLayoutManager(this));
        lista.setAdapter(adapter);

        btnGuardar.setOnClickListener(v -> guardar());
        refrescar();
    }

    private void guardar() {
        String nombre = etNombre.getText().toString().trim();
        String docente = etDocente.getText().toString().trim();
        if (nombre.isEmpty()) {
            etNombre.setError("El nombre es obligatorio");
            return;
        }
        if (enEdicion == null) {
            materiaDAO.insertar(new Materia(0, nombre, docente));
        } else {
            enEdicion.setNombre(nombre);
            enEdicion.setDocente(docente);
            materiaDAO.actualizar(enEdicion);
        }
        limpiarFormulario();
        refrescar();
    }

    private void limpiarFormulario() {
        enEdicion = null;
        etNombre.setText("");
        etDocente.setText("");
        btnGuardar.setText("Agregar materia");
    }

    private void refrescar() {
        adapter.mostrar(materiaDAO.listar());
    }

    @Override
    public void editar(Materia materia) {
        enEdicion = materia;
        etNombre.setText(materia.getNombre());
        etDocente.setText(materia.getDocente());
        btnGuardar.setText("Actualizar materia");
    }

    @Override
    public void verTareas(Materia materia) {
        Intent intento = new Intent(this, TareasActivity.class);
        intento.putExtra("materiaId", materia.getId());
        startActivity(intento);
    }

    @Override
    public void eliminar(Materia materia) {
        if (!tareaDAO.listarPorMateria(materia.getId()).isEmpty()) {
            Toast.makeText(this, "No se puede eliminar: la materia tiene tareas",
                    Toast.LENGTH_LONG).show();
            return;
        }
        materiaDAO.eliminar(materia.getId());
        if (enEdicion != null && enEdicion.getId() == materia.getId()) {
            limpiarFormulario();
        }
        refrescar();
    }
}
