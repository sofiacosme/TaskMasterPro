package task.master.pro.home;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import task.master.pro.R;

public class PerfilFragment extends Fragment {

    private ImageView imgPerfil, imgEditar;
    private EditText edtNombre;
    private TextView txtRol, txtHabilidades, txtExperiencia;
    private Button btnGuardar;
    private Uri selectedImageUri;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);

        imgPerfil = v.findViewById(R.id.imgPerfil);
        imgEditar = v.findViewById(R.id.imgEditar);
        edtNombre = v.findViewById(R.id.edtNombre);
        txtRol = v.findViewById(R.id.txtRol);
        txtHabilidades = v.findViewById(R.id.txtHabilidades);
        txtExperiencia = v.findViewById(R.id.txtExperiencia);
        btnGuardar = v.findViewById(R.id.btnGuardar);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        edtNombre.setEnabled(false);
        btnGuardar.setVisibility(View.GONE);

        // Lanzador de selección de imagen
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        imgPerfil.setImageURI(selectedImageUri);
                        btnGuardar.setVisibility(View.VISIBLE);
                        edtNombre.setEnabled(true);
                    }
                }
        );

        imgEditar.setOnClickListener(view -> imagePickerLauncher.launch("image/*"));

        edtNombre.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnGuardar.setVisibility(View.VISIBLE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnGuardar.setOnClickListener(view -> guardarCambios());

        cargarPerfil();
    }

    private void cargarPerfil() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        db.collection("users").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) return;

                    edtNombre.setText(doc.getString("displayName"));
                    txtRol.setText(doc.getString("rol") != null ? doc.getString("rol") : "Gestora de Proyectos");
                    txtHabilidades.setText(doc.getString("habilidades") != null ? doc.getString("habilidades") : "-");
                    txtExperiencia.setText(doc.getString("experiencia") != null ? doc.getString("experiencia") : "-");

                    String fotoUrl = doc.getString("photoUrl");
                    if (!TextUtils.isEmpty(fotoUrl)) {
                        Glide.with(requireContext())
                                .load(fotoUrl)
                                .placeholder(R.mipmap.ic_launcher_round)
                                .circleCrop()
                                .into(imgPerfil);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Error cargando perfil", Toast.LENGTH_SHORT).show()
                );
    }

    private void guardarCambios() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(requireContext(), "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        String nombre = edtNombre.getText().toString().trim();
        if (TextUtils.isEmpty(nombre)) {
            Toast.makeText(requireContext(), "Nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        btnGuardar.setEnabled(false);

        Map<String, Object> data = new HashMap<>();
        data.put("displayName", nombre);

        if (selectedImageUri != null) {
            subirFoto(uid, selectedImageUri, data);
        } else {
            actualizarFirestore(uid, data);
        }
    }

    private void subirFoto(String uid, Uri imageUri, Map<String, Object> data) {
        StorageReference ref = FirebaseStorage.getInstance()
                .getReference("users/" + uid + "/profile.jpg");

        Toast.makeText(requireContext(), "Subiendo foto...", Toast.LENGTH_SHORT).show();

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl()
                        .addOnSuccessListener(url -> {
                            data.put("photoUrl", url.toString());
                            actualizarFirestore(uid, data);
                        })
                        .addOnFailureListener(e -> {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error obteniendo URL de foto", Toast.LENGTH_SHORT).show();
                            actualizarFirestore(uid, data);
                        })
                )
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Error subiendo foto", Toast.LENGTH_SHORT).show();
                    actualizarFirestore(uid, data);
                });
    }

    private void actualizarFirestore(String uid, Map<String, Object> data) {
        db.collection("users").document(uid)
                .set(data, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(x -> {
                    Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
                    btnGuardar.setEnabled(true);
                    btnGuardar.setVisibility(View.GONE);
                    edtNombre.setEnabled(false);
                    selectedImageUri = null;
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error actualizando perfil", Toast.LENGTH_SHORT).show();
                    btnGuardar.setEnabled(true);
                });
    }
}
