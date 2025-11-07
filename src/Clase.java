import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Clase {

    private String nombre;
    private String horario;
    private int cupoMaximo;
    private Entrenador entrenador;
    private List<Socio> sociosInscriptos;

    public Clase(String nombre, String horario, int cupoMaximo,
                 Entrenador entrenador, List<Socio> sociosInscriptos) {
        this.nombre = nombre;
        this.horario = horario;
        this.cupoMaximo = cupoMaximo;
        this.entrenador = entrenador;
        this.sociosInscriptos = crearListaInicial(sociosInscriptos);
    }

    public boolean agregarSocio(Socio s) {
        if (s == null) {
            return false;
        }

        if (sociosInscriptos.size() < cupoMaximo && !sociosInscriptos.contains(s)) {
            sociosInscriptos.add(s);
            return true;
        }
        return false;
    }

    public boolean eliminarSocio(Socio s) {
        return sociosInscriptos.remove(s);
    }

    @Override
    public String toString() {
        return "Clase{" +
                "nombre='" + nombre + '\'' +
                ", horario='" + horario + '\'' +
                ", cupoMaximo=" + cupoMaximo +
                ", entrenador=" + (entrenador != null ? entrenador.getNombre() : "N/A") +
                ", sociosInscriptos=" + sociosInscriptos.size() +
                '}';
    }

    // Getters y setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(int cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public Entrenador getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(Entrenador entrenador) {
        this.entrenador = entrenador;
    }

    public List<Socio> getSociosInscriptos() {
        return Collections.unmodifiableList(sociosInscriptos);
    }

    private List<Socio> crearListaInicial(List<Socio> socios) {
        return socios != null ? socios : new ArrayList<>();
    }
}
