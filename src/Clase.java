import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/** Clase que representa una actividad o sesión dictada en el gimnasio */
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

    public Clase(String nombre, String dia, String turno, int cupoMaximo, Entrenador entrenador) {
        this.nombre = nombre;
        this.horario = dia + " - " + turno;
        this.cupoMaximo = cupoMaximo;
        this.entrenador = entrenador;
        this.sociosInscriptos = new ArrayList<>();
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

    public void agregarAlGimnasio(Gimnasio g) {
        if (g != null && !g.clasesPorHorario.containsKey(this.getHorario())) {
            g.clases.add(this);
            g.clasesPorHorario.put(this.getHorario(), this);
            // Vincular clase con entrenador si corresponde
            if (this.getEntrenador() != null) {
                this.getEntrenador().asignarClase(this);
            }
            g.guardarClases();
        }
    }

    public void eliminarDelGimnasio(Gimnasio g) {
        if (g != null && g.clases.remove(this)) {
            g.clasesPorHorario.remove(this.getHorario());
            if (this.getEntrenador() != null) {
                this.getEntrenador().getClasesAsignadas().remove(this);
            }
            g.guardarClases();
        }
    }

    public String toCSV() {
        String dniEntrenador = (entrenador != null) ? String.valueOf(entrenador.getDni()) : "null";
        return nombre + ";" + horario + ";" + cupoMaximo + ";" + dniEntrenador;
    }

    public static Clase fromCSV(String linea, Map<Integer, Empleado> empleados) {
        String[] datos = linea.split(";");
        if (datos.length >= 4) {
            String nombre = datos[0];
            String horario = datos[1];
            int cupo = Integer.parseInt(datos[2]);
            String dniEntStr = datos[3];

            Entrenador ent = null;
            if (!dniEntStr.equals("null") && empleados != null) {
                Empleado emp = empleados.get(Integer.parseInt(dniEntStr));
                if (emp instanceof Entrenador) {
                    ent = (Entrenador) emp;
                }
            }

            Clase c = new Clase(nombre, horario, cupo, ent, null);
            if (ent != null) {
                ent.asignarClase(c);
            }
            return c;
        }
        return null;
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
