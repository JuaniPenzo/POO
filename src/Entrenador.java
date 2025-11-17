import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Subclase de Empleado que representa a un Entrenador, con especialidad y clases asignadas.
 */
public class Entrenador extends Empleado {

    private String especialidad;
    private List<Clase> clasesAsignadas;

    public Entrenador(String nombre, String apellido, int dni, String sexo,
                      Date fechaNacimiento, double sueldo,
                      String especialidad, List<Clase> clasesAsignadas) {
        super(nombre, apellido, dni, sexo, fechaNacimiento, sueldo);
        this.especialidad = especialidad;
        this.clasesAsignadas = crearListaInicial(clasesAsignadas);
    }

    public void asignarClase(Clase c) {
        if (c != null && !clasesAsignadas.contains(c)) {
            clasesAsignadas.add(c);
        }
    }

    @Override
    public String toString() {
        return "Entrenador{" +
                "nombre='" + getNombre() + '\'' +
                ", apellido='" + getApellido() + '\'' +
                ", especialidad='" + especialidad + '\'' +
                ", clasesAsignadas=" + clasesAsignadas.size() +
                '}';
    }

    private List<Clase> crearListaInicial(List<Clase> clases) {
        return clases != null ? clases : new ArrayList<>();
    }

    // Getters y setters

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public List<Clase> getClasesAsignadas() {
        return clasesAsignadas;
    }

    public void setClasesAsignadas(List<Clase> clasesAsignadas) {
        this.clasesAsignadas = clasesAsignadas;
    }
}
