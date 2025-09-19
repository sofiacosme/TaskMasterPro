package task.master.pro.home;

import java.io.Serializable;

public class Tarea implements Serializable {
    private String id;
    private String titulo;
    private String descripcion;
    private String prioridad;
    private String userId;
    private boolean completada;
    private String fotoUrl; // nuevo


    private static final long serialVersionUID = 1L;

    // 🔹 Constructor vacío requerido por Firestore
    public Tarea() {}

    // 🔹 Constructor completo sin foto (para compatibilidad)
    public Tarea(String id, String titulo, String descripcion, String prioridad, boolean completada, String userId) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.completada = completada;
        this.userId = userId;
    }

    // 🔹 Constructor completo con foto
    public Tarea(String id, String titulo, String descripcion, String prioridad, boolean completada, String userId, String fotoUrl) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.completada = completada;
        this.userId = userId;
        this.fotoUrl = fotoUrl;
    }

    // 🔹 Getters y Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getPrioridad() { return prioridad; }
    public void setPrioridad(String prioridad) { this.prioridad = prioridad; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
}
