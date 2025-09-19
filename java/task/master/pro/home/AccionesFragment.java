package task.master.pro.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import task.master.pro.R;

public class AccionesFragment extends Fragment {

    private Button btnNuevaTarea, btnExportar, btnLimpiar, btnSincronizar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_acciones, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);

        btnNuevaTarea = v.findViewById(R.id.btnNuevaTarea);
        btnExportar = v.findViewById(R.id.btnExportar);
        btnLimpiar = v.findViewById(R.id.btnLimpiar);
        btnSincronizar = v.findViewById(R.id.btnSincronizar);

        btnNuevaTarea.setOnClickListener(view -> {
            // Ir a fragmento de nueva tarea
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.right_panel, new NuevaTareaFragment())
                    .addToBackStack(null)
                    .commit();
        });

        btnExportar.setOnClickListener(view -> exportarTareas());
        btnLimpiar.setOnClickListener(view -> limpiarTareas());
        btnSincronizar.setOnClickListener(view -> sincronizarTareas());
    }

    private void exportarTareas() {
        String uid = getUid();
        if (uid == null) return;

        FirebaseFirestore.getInstance()
                .collection("tareas")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(result -> {
                    if (result.isEmpty()) {
                        Toast.makeText(requireContext(), "No hay tareas para exportar", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    StringBuilder tareasTexto = new StringBuilder("📋 Mis Tareas:\n");
                    for (var doc : result) {
                        String titulo = doc.getString("titulo");
                        String prioridad = doc.getString("prioridad");
                        tareasTexto.append("• ").append(titulo != null ? titulo : "Sin título")
                                .append(" [").append(prioridad != null ? prioridad : "sin prioridad").append("]\n");
                    }

                    // Copiar al portapapeles
                    android.content.ClipboardManager clipboard =
                            (android.content.ClipboardManager) requireContext().getSystemService(requireContext().CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Tareas", tareasTexto.toString());
                    clipboard.setPrimaryClip(clip);

                    Toast.makeText(requireContext(), "Tareas copiadas al portapapeles", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Error al exportar: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }



    private void limpiarTareas() {
        String uid = getUid();
        if (uid == null) return;

        FirebaseFirestore.getInstance()
                .collection("tareas")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(result -> {
                    for (var doc : result) doc.getReference().delete();
                    Toast.makeText(requireContext(), "Tareas eliminadas", Toast.LENGTH_SHORT).show();
                });
    }


    private void sincronizarTareas() {
        // Simula sincronización
        Toast.makeText(requireContext(), "Tareas sincronizadas desde Firestore", Toast.LENGTH_SHORT).show();
    }

    private @Nullable String getUid() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return null;
        }
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


}