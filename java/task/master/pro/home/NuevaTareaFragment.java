package task.master.pro.home;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import task.master.pro.R;

public class NuevaTareaFragment extends Fragment {

    private EditText edtTitulo, edtDescripcion;
    private Spinner spinnerPrioridad;
    private Button btnGuardar;
    private ImageView imgSeleccionarFoto;
    private Uri selectedImageUri;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nueva_tarea, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);

        // Inicializar vistas
        edtTitulo = v.findViewById(R.id.edtTitulo);
        edtDescripcion = v.findViewById(R.id.edtDescripcion);
        spinnerPrioridad = v.findViewById(R.id.spinnerPrioridad);
        btnGuardar = v.findViewById(R.id.btnGuardar);
        imgSeleccionarFoto = v.findViewById(R.id.imgSeleccionarFoto);

        // Spinner prioridades
        String[] prioridades = {"baja", "normal", "importante", "urgente"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, prioridades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrioridad.setAdapter(adapter);

        // Lanzador para elegir foto
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        imgSeleccionarFoto.setImageURI(selectedImageUri);
                    }
                }
        );

        imgSeleccionarFoto.setOnClickListener(view -> imagePickerLauncher.launch("image/*"));
        btnGuardar.setOnClickListener(view -> guardarTarea());
    }

    private void guardarTarea() {
        String titulo = edtTitulo.getText().toString().trim();
        String descripcion = edtDescripcion.getText().toString().trim();
        String prioridad = spinnerPrioridad.getSelectedItem().toString();

        if (TextUtils.isEmpty(titulo)) {
            Toast.makeText(requireContext(), "Ingresa un título", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String id = db.collection("tareas").document().getId();

        Tarea tarea = new Tarea(id, titulo, descripcion, prioridad, false, uid);

        if (selectedImageUri != null) {
            StorageReference ref = FirebaseStorage.getInstance()
                    .getReference("tareas/" + uid + "/" + id + ".jpg");
            ref.putFile(selectedImageUri)
                    .addOnSuccessListener(task -> ref.getDownloadUrl()
                            .addOnSuccessListener(url -> {
                                tarea.setFotoUrl(url.toString());
                                db.collection("tareas").document(id)
                                        .set(tarea)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(requireContext(), "Tarea guardada", Toast.LENGTH_SHORT).show();
                                            // Volver a CapturasFragment
                                            if (getActivity() != null) {
                                                getActivity().getSupportFragmentManager()
                                                        .beginTransaction()
                                                        .replace(R.id.right_panel, new CapturasFragment())
                                                        .addToBackStack(null)
                                                        .commit();
                                            }
                                        });
                            }))
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Error subiendo foto", Toast.LENGTH_SHORT).show()
                    );
        } else {
            db.collection("tareas").document(id)
                    .set(tarea)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(requireContext(), "Tarea guardada", Toast.LENGTH_SHORT).show();
                        // Volver a CapturasFragment
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.right_panel, new CapturasFragment())
                                    .addToBackStack(null)
                                    .commit();
                        }
                    });
        }
    }

}
