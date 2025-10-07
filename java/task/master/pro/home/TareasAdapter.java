package task.master.pro.home;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import task.master.pro.R;

public class TareasAdapter extends RecyclerView.Adapter<TareasAdapter.ViewHolder> {

    public interface OnTareaActionListener {
        void onBorrar(Tarea tarea);
        void onCompletar(Tarea tarea);
    }

   
    private final Context context;
    private List<Tarea> lista;
    private final OnTareaActionListener listener;

    public TareasAdapter(Context context, List<Tarea> lista, OnTareaActionListener listener) {
        this.context = context;
        this.lista = lista != null ? lista : new ArrayList<>();
        this.listener = listener;
    }

   
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_tarea, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Tarea t = lista.get(pos);

        h.txtTitulo.setText(t.getTitulo());
        h.txtDescripcion.setText(t.getDescripcion());

        // Imagen de la tarea
        if (t.getFotoUrl() != null && !t.getFotoUrl().isEmpty()) {
            Glide.with(context)
                    .load(t.getFotoUrl())
                    .placeholder(R.drawable.ic_user)
                    .into(h.imgIcon);
        } else {
            h.imgIcon.setImageResource(R.drawable.ic_user);
        }

        // Color de prioridad
        switch (t.getPrioridad()) {
            case "urgente":
                h.viewPrioridad.setBackgroundColor(Color.RED);
                break;
            case "importante":
                h.viewPrioridad.setBackgroundColor(Color.YELLOW);
                break;
            case "normal":
                h.viewPrioridad.setBackgroundColor(Color.GREEN);
                break;
            case "baja":
                h.viewPrioridad.setBackgroundColor(Color.BLUE);
                break;
            default:
                h.viewPrioridad.setBackgroundColor(Color.GRAY);
        }

        // Mostrar si está completada
        if (t.isCompletada()) {
            h.txtTitulo.setPaintFlags(h.txtTitulo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            h.txtDescripcion.setPaintFlags(h.txtDescripcion.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            h.txtTitulo.setPaintFlags(h.txtTitulo.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            h.txtDescripcion.setPaintFlags(h.txtDescripcion.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Click en el botón borrar
        h.btnBorrar.setOnClickListener(v -> listener.onBorrar(t));

        // Click en toda la tarjeta para abrir detalle
        h.itemView.setOnClickListener(v -> {
            if (context instanceof FragmentActivity) {
                ((FragmentActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.right_panel, DetalleTareaFragment.newInstance(t)) // ✅ FIX
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void setTareas(List<Tarea> nuevas) {
        this.lista = nuevas != null ? nuevas : new ArrayList<>();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitulo, txtDescripcion;
        View viewPrioridad;
        Button btnBorrar;
        ImageView imgIcon;

        ViewHolder(View v) {
            super(v);
            txtTitulo = v.findViewById(R.id.txtTitulo);
            txtDescripcion = v.findViewById(R.id.txtDescripcion);
            viewPrioridad = v.findViewById(R.id.viewPrioridad);
            btnBorrar = v.findViewById(R.id.btnBorrar);
            imgIcon = v.findViewById(R.id.imgIcon);
        }
    }
}
