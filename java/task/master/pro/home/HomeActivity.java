package task.master.pro.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import task.master.pro.R;

public class HomeActivity extends AppCompatActivity {

    private Button btnPerfil, btnCapturas, btnTutorial, btnRecursos, btnAcciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);  // Tu layout con menú lateral fijo

        btnPerfil = findViewById(R.id.btnPerfil);
        btnCapturas = findViewById(R.id.btnCapturas);
        btnTutorial = findViewById(R.id.btnTutorial);
        btnRecursos = findViewById(R.id.btnRecursos);
        btnAcciones = findViewById(R.id.btnAcciones);

        // Por defecto, carga PerfilFragment al inicio
        cargarFragment(new PerfilFragment());

        btnPerfil.setOnClickListener(v -> cargarFragment(new PerfilFragment()));
        btnCapturas.setOnClickListener(v -> cargarFragment(new CapturasFragment()));
        btnTutorial.setOnClickListener(v -> cargarFragment(new TutorialFragment()));
        btnRecursos.setOnClickListener(v -> cargarFragment(new RecursosFragment()));
        btnAcciones.setOnClickListener(v -> cargarFragment(new AccionesFragment()));
    }

    private void cargarFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.right_panel, fragment)
                .commit();
    }
}
