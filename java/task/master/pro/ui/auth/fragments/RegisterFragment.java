package task.master.pro.ui.auth.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import task.master.pro.R;
import task.master.pro.home.HomeActivity;

public class RegisterFragment extends Fragment {

    private EditText edtName, edtEmail, edtPass, edtConfirm;
    private Button btnSignup, btnSelectPhoto;
    private ImageView imgProfile;
    private Uri selectedImageUri;

    private ActivityResultLauncher<String> imagePickerLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);

        edtName = v.findViewById(R.id.edtName);
        edtEmail = v.findViewById(R.id.edtEmail);
        edtPass = v.findViewById(R.id.edtPass);
        edtConfirm = v.findViewById(R.id.edtConfirm);
        btnSignup = v.findViewById(R.id.btnSignup);
        btnSelectPhoto = v.findViewById(R.id.btnSelectPhoto);
        imgProfile = v.findViewById(R.id.imgProfile);

        // Lanzador de selección de imagen
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedImageUri = uri;
                        imgProfile.setImageURI(selectedImageUri);
                    } else {
                        Toast.makeText(requireContext(), "No se pudo seleccionar la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        btnSelectPhoto.setOnClickListener(view -> imagePickerLauncher.launch("image/*"));
        btnSignup.setOnClickListener(view -> doSignup());
    }

    private void doSignup() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
        String conf = edtConfirm.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(requireContext(), "Ingrese su nombre", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(requireContext(), "Correo inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            Toast.makeText(requireContext(), "Ingrese contraseña", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!pass.equals(conf)) {
            Toast.makeText(requireContext(), "Contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSignup.setEnabled(false);

        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        btnSignup.setEnabled(true);
                        String msg = task.getException() != null ? task.getException().getMessage() : "Error";
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
                        return;
                    }

                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    if (selectedImageUri != null) {
                        subirFoto(uid, selectedImageUri, name, email);
                    } else {
                        guardarPerfil(uid, name, email, null);
                    }
                });
    }

    private void subirFoto(String uid, Uri imageUri, String name, String email) {
        StorageReference ref = FirebaseStorage.getInstance()
                .getReference("users/" + uid + "/profile.jpg");


        // Mostrar progreso opcional
        Toast.makeText(requireContext(), "Subiendo foto...", Toast.LENGTH_SHORT).show();

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl()
                        .addOnSuccessListener(url -> {
                            Toast.makeText(requireContext(), "Foto subida exitosamente", Toast.LENGTH_SHORT).show();
                            guardarPerfil(uid, name, email, url.toString());
                        })
                        .addOnFailureListener(e -> {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error obteniendo URL de foto", Toast.LENGTH_SHORT).show();
                            guardarPerfil(uid, name, email, null);
                        })
                )
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Error subiendo foto", Toast.LENGTH_SHORT).show();
                    guardarPerfil(uid, name, email, null);
                });
    }


    private void guardarPerfil(String uid, String name, String email, @Nullable String photoUrl) {
        Map<String, Object> data = new HashMap<>();
        data.put("displayName", name);
        data.put("email", email);
        data.put("createdAt", System.currentTimeMillis());
        if (photoUrl != null) data.put("photoUrl", photoUrl);

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .set(data)
                .addOnSuccessListener(x -> {
                    btnSignup.setEnabled(true);
                    Toast.makeText(requireContext(), "Cuenta creada con éxito", Toast.LENGTH_SHORT).show();
                    startActivity(new android.content.Intent(requireContext(), HomeActivity.class));
                    requireActivity().finish();
                })
                .addOnFailureListener(e -> {
                    btnSignup.setEnabled(true);
                    Toast.makeText(requireContext(), "Perfil guardado parcialmente", Toast.LENGTH_SHORT).show();
                });
    }
}
