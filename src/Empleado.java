import java.util.Calendar;
import java.util.Date;

/**
 * Clase abstracta que representa un empleado genérico del gimnasio (modelo base para distintos tipos de empleados).
 */
public abstract class Empleado {

    private String nombre;
    private String apellido;
    private int dni;
    private String sexo;
    private Date fechaNacimiento;
    private double sueldo;

    public Empleado(String nombre, String apellido, int dni, String sexo,
                    Date fechaNacimiento, double sueldo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.sueldo = sueldo;
    }

    public int calcularEdad() {
        if (fechaNacimiento == null) return 0;

        Calendar hoy = Calendar.getInstance();
        Calendar fnac = Calendar.getInstance();
        fnac.setTime(fechaNacimiento);

        int edad = hoy.get(Calendar.YEAR) - fnac.get(Calendar.YEAR);

        if (hoy.get(Calendar.DAY_OF_YEAR) < fnac.get(Calendar.DAY_OF_YEAR)) {
            edad--;
        }
        return edad;
    }

    /**
     * Descuenta el sueldo desde la cuenta del gimnasio.
     * Podrías luego extender esto para depositar en una cuenta de empleado.
     */
    public boolean cobrarSueldo(CuentaBancaria cuentaGimnasio) {
        if (cuentaGimnasio == null) {
            return false;
        }

        // extraer y registrar el movimiento vinculando el empleado
        return cuentaGimnasio.extraer(sueldo, "Pago de sueldo a " + this.getNombre() + " (DNI:" + this.getDni() + ")", null, this, null);
    }

    @Override
    public String toString() {
        return "Empleado{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", dni=" + dni +
                ", sexo='" + sexo + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", sueldo=" + sueldo +
                '}';
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public double getSueldo() {
        return sueldo;
    }

    public void setSueldo(double sueldo) {
        this.sueldo = sueldo;
    }
}
