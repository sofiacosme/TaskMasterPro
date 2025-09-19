package task.master.pro;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory;

public class MyApplication extends Application {

    private static final String TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializa Firebase
        FirebaseApp.initializeApp(this);

        // Obtiene instancia de App Check
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();

        // Detecta si la app está en modo debug
        boolean isDebug = (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        if (isDebug) {
            // 🔹 Modo debug: usar DebugAppCheckProviderFactory
            firebaseAppCheck.installAppCheckProviderFactory(
                    DebugAppCheckProviderFactory.getInstance()
            );
            Log.d(TAG, "App Check configurado en modo DEBUG.");
        } else {
            // 🔹 Modo producción: usar Play Integrity
            firebaseAppCheck.installAppCheckProviderFactory(
                    PlayIntegrityAppCheckProviderFactory.getInstance()
            );
            Log.d(TAG, "App Check configurado en modo Play Integrity.");
        }

        // Listener opcional para obtener token de App Check
        firebaseAppCheck.addAppCheckTokenListener(tokenResult -> {
            if (tokenResult != null) {
                Log.d(TAG, "App Check Token válido: " + tokenResult.getToken());
            } else {
                Log.w(TAG, "App Check Token NO válido");
            }
        });
    }
}
