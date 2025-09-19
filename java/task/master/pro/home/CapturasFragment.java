package task.master.pro.home;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import task.master.pro.R;

public class CapturasFragment extends Fragment {

    private RecyclerView recycler;
    private TareasAdapter adapter;
    private List<Tarea> lista = new ArrayList<>();
    private List<Tarea> listaOriginal = new ArrayList<>();
    private FirebaseFirestore db;
    private TextView txtMetricas;
    private EditText edtBuscar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_capturas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);
        db = FirebaseFirestore.getInstance();

        recycler = v.findViewById(R.id.recyclerTareas);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        txtMetricas = v.findViewById(R.id.txtMetricas);
        edtBuscar = v.findViewById(R.id.edtBuscar);
        View btnAgregar = v.findViewById(R.id.btnAgregar);

        adapter = new TareasAdapter(requireContext(), lista, new TareasAdapter.OnTareaActionListener() {
            @Override
            public void onBorrar(Tarea tarea) {
                borrarTarea(tarea);
            }

            @Override
            public void onCompletar(Tarea tarea) {
                tarea.setCompletada(!tarea.isCompletada());
                db.collection("tareas")
                        .document(tarea.getId())
                        .set(tarea)
                        .addOnSuccessListener(unused -> {
                            adapter.notifyDataSetChanged();
                            actualizarMetricas();
                        });
            }
        });
        recycler.setAdapter(adapter);

        // Busqueda en tiempo real
        edtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarLista(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        // BOTON AGREGAR
        btnAgregar.setOnClickListener(x -> {
            // Reemplazar el contenedor actual con el fragmento de NuevaTarea
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.right_panel, new NuevaTareaFragment()) // Asegúrate de que right_panel exista en tu layout
                        .addToBackStack(null)
                        .commit();
            }
        });

        cargarTareas();
    }

    private void cargarTareas() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid == null) return;

        db.collection("tareas")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(snaps -> {
                    if (!isAdded()) return;
                    listaOriginal.clear();
                    for (var d : snaps.getDocuments()) {
                        Tarea t = d.toObject(Tarea.class);
                        listaOriginal.add(t);
                    }
                    filtrarLista(edtBuscar.getText().toString());
                })
                .addOnFailureListener(e -> {
                    if (isAdded()) {
                        Toast.makeText(requireContext(), "Error al cargar tareas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filtrarLista(String query) {
        lista.clear();
        if (TextUtils.isEmpty(query)) {
            lista.addAll(listaOriginal);
        } else {
            String q = query.toLowerCase();
            for (Tarea t : listaOriginal) {
                if (t.getTitulo() != null && t.getTitulo().toLowerCase().contains(q)) {
                    lista.add(t);
                }
            }
        }
        adapter.notifyDataSetChanged();
        actualizarMetricas();
    }

    private void borrarTarea(Tarea t) {
        db.collection("tareas").document(t.getId()).delete()
                .addOnSuccessListener(x -> cargarTareas());
    }

    private void actualizarMetricas() {
        if (lista.isEmpty()) {
            txtMetricas.setText("Sin tareas");
            return;
        }
        int total = lista.size();
        int completadas = 0;
        for (Tarea t : lista) if (t.isCompletada()) completadas++;
        int porcentaje = (int) ((completadas * 100.0) / total);
        txtMetricas.setText("Progreso: " + porcentaje + "% completado");
    }
}
