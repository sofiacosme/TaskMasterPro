package task.master.pro.home;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import task.master.pro.R;

public class RecursosFragment extends Fragment {

    private EditText edtUrl;
    private Button btnNavegar;
    private WebView webView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recursos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtUrl = view.findViewById(R.id.edtUrl);
        btnNavegar = view.findViewById(R.id.btnNavegar);
        webView = view.findViewById(R.id.webView);

        // Configurar WebView
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        btnNavegar.setOnClickListener(v -> {
            String url = edtUrl.getText().toString().trim();

            if (TextUtils.isEmpty(url)) {
                Toast.makeText(requireContext(), "Ingresa una URL válida", Toast.LENGTH_SHORT).show();
                return;
            }

            // Si el usuario no puso "http", lo agregamos
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }

            webView.loadUrl(url);
            guardarEnFirestore(url);
        });
    }

    private void guardarEnFirestore(String url) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("url", url);
        data.put("createdAt", System.currentTimeMillis());

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("recursos")
                .add(data)
                .addOnSuccessListener(doc -> {
                    // Guardado con éxito
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "No se pudo guardar el recurso", Toast.LENGTH_SHORT).show());
    }
}
