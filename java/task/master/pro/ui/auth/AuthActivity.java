package task.master.pro.ui.auth;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.button.MaterialButtonToggleGroup;

import task.master.pro.R;
import task.master.pro.ui.auth.fragments.LoginFragment;
import task.master.pro.ui.auth.fragments.RegisterFragment;

public class AuthActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_auth);

    MaterialButtonToggleGroup toggle = findViewById(R.id.toggleAuth);

    // Carga inicial: Login
    getSupportFragmentManager().beginTransaction()
            .setReorderingAllowed(true)
            .replace(R.id.auth_container, new LoginFragment())
            .commit();
    toggle.check(R.id.btnLoginTab);

    // Cambiar entre Login / Registro
    toggle.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
      if (!isChecked) return;
      Fragment f = (checkedId == R.id.btnRegisterTab) ? new RegisterFragment() : new LoginFragment();
      getSupportFragmentManager().beginTransaction()
              .setReorderingAllowed(true)
              .replace(R.id.auth_container, f)
              .commit();
    });
  }
}
