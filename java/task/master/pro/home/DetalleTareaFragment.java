package task.master.pro.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import task.master.pro.R;

public class DetalleTareaFragment extends Fragment {

    private static final String ARG_TAREA = "tarea";
    private Tarea tarea;

    private TextView txtTitulo, txtDescripcion, txtPrioridad;
    private ImageView imgTarea;
    private Button btnCompletar, btnVolver;

    // Constructor vacío (obligatorio)
    public DetalleTareaFragment() { }

    // Método para crear una nueva instancia con argumentos
    public static DetalleTareaFragment newInstance(Tarea tarea) {
        DetalleTareaFragment fragment = new DetalleTareaFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TAREA, tarea); // Tarea debe implementar Serializable
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tarea = (Tarea) getArguments().getSerializable(ARG_TAREA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.detalle_tarea_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);

        txtTitulo = v.findViewById(R.id.txtTitulo);
        txtDescripcion = v.findViewById(R.id.txtDescripcion);
        txtPrioridad = v.findViewById(R.id.txtPrioridad);
        imgTarea = v.findViewById(R.id.imgTarea);
        btnCompletar = v.findViewById(R.id.btnCompletar);
        btnVolver = v.findViewById(R.id.btnVolver);

        if (tarea == null) return; // seguridad

        // Mostrar datos
        txtTitulo.setText(tarea.getTitulo());
        txtDescripcion.setText(tarea.getDescripcion());
        txtPrioridad.setText(tarea.getPrioridad());
        if (tarea.getFotoUrl() != null) {
            Glide.with(this).load(tarea.getFotoUrl()).into(imgTarea);
        }

        btnCompletar.setText(tarea.isCompletada() ? "Marcar incompleta" : "Completar");
        btnCompletar.setOnClickListener(view -> {
            tarea.setCompletada(!tarea.isCompletada());
            FirebaseFirestore.getInstance()
                    .collection("tareas")
                    .document(tarea.getId())
                    .set(tarea)
                    .addOnSuccessListener(u -> {
                        Toast.makeText(requireContext(), "Tarea actualizada", Toast.LENGTH_SHORT).show();
                        btnCompletar.setText(tarea.isCompletada() ? "Marcar incompleta" : "Completar");
                    });
        });

        btnVolver.setOnClickListener(view -> {
            if (getActivity() != null) getActivity().getSupportFragmentManager().popBackStack();
        });
    }
}
