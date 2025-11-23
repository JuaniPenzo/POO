import java.util.Date;

/**
 * Subclase de Empleado que representa al personal de limpieza, con horario de
 * trabajo y sector asignado.
 */
public class Limpieza extends Empleado {

    private String horarioTrabajo;
    private String sector;

    public Limpieza(String nombre, String apellido, int dni, String sexo,
            Date fechaNacimiento, double sueldo,
            String horarioTrabajo, String sector) {
        super(nombre, apellido, dni, sexo, fechaNacimiento, sueldo);
        this.horarioTrabajo = horarioTrabajo;
        this.sector = sector;
    }

    @Override
    public String toCSV() {
        // tipo;dni;nombre;apellido;sexo;sueldo;horario;sector
        return "Limpieza;" + getDni() + ";" + getNombre() + ";" + getApellido() + ";" + getSexo() + ";" + getSueldo()
                + ";" + horarioTrabajo + ";" + sector;
    }

    @Override
    public String toString() {
        return "Limpieza{" +
                "nombre='" + getNombre() + '\'' +
                ", apellido='" + getApellido() + '\'' +
                ", horarioTrabajo='" + horarioTrabajo + '\'' +
                ", sector='" + sector + '\'' +
                '}';
    }

    // Getters y setters

    public String getHorarioTrabajo() {
        return horarioTrabajo;
    }

    public void setHorarioTrabajo(String horarioTrabajo) {
        this.horarioTrabajo = horarioTrabajo;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }
}
