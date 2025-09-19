package task.master.pro.ui.auth.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import task.master.pro.R;
import task.master.pro.home.HomeActivity;

public class LoginFragment extends Fragment {

  private EditText edtEmail, edtPass;
  private Button btnLogin;

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
                           @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_login, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
    super.onViewCreated(v, s);

    edtEmail = v.findViewById(R.id.edtEmail);
    edtPass  = v.findViewById(R.id.edtPass);
    btnLogin = v.findViewById(R.id.btnLogin);

    btnLogin.setOnClickListener(view -> doLogin());
  }

  private void doLogin() {
    String email = edtEmail.getText().toString().trim();
    String pass  = edtPass.getText().toString().trim();

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      Toast.makeText(requireContext(), R.string.auth_invalid_email, Toast.LENGTH_SHORT).show();
      return;
    }
    if (TextUtils.isEmpty(pass)) {
      Toast.makeText(requireContext(), R.string.auth_password_required, Toast.LENGTH_SHORT).show();
      return;
    }

    btnLogin.setEnabled(false);

    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(t -> {
              btnLogin.setEnabled(true);

              if (t.isSuccessful()) {
                // Navegar a HomeActivity
                Intent intent = new Intent(requireContext(), HomeActivity.class);
                startActivity(intent);
                requireActivity().finish(); // Cierra la pantalla de login
              } else {
                String msg = t.getException() != null ? t.getException().getMessage() : "Error";
                Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();
              }
            });
  }
}
