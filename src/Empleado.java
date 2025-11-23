import java.util.Calendar;
import java.util.Date;

/**
 * Clase abstracta que representa un empleado genérico del gimnasio (modelo base
 * para distintos tipos de empleados).
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
        if (fechaNacimiento == null)
            return 0;

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
     */
    public boolean cobrarSueldo(CuentaBancaria cuentaGimnasio) {
        if (cuentaGimnasio == null) {
            return false;
        }

        // extraer y registrar el movimiento vinculando el empleado
        return cuentaGimnasio.extraer(sueldo, "Pago de sueldo a " + this.getNombre() + " (DNI:" + this.getDni() + ")",
                null, this, null);
    }

    public void agregarAlGimnasio(Gimnasio g) {
        if (g != null && !g.empleadosPorDni.containsKey(this.getDni())) {
            g.empleados.add(this);
            g.empleadosPorDni.put(this.getDni(), this);
            g.guardarEmpleados();
        }
    }

    public void eliminarDelGimnasio(Gimnasio g) {
        if (g != null && g.empleados.remove(this)) {
            g.empleadosPorDni.remove(this.getDni());
            g.guardarEmpleados();
        }
    }

    public abstract String toCSV();

    public static Empleado fromCSV(String linea) {
        String[] datos = linea.split(";");
        if (datos.length >= 7) {
            String tipo = datos[0];
            int dni = Integer.parseInt(datos[1]);
            String nombre = datos[2];
            String apellido = datos[3];
            String sexo = datos[4];
            double sueldo = Double.parseDouble(datos[5]);

            if (tipo.equals("Entrenador")) {
                String especialidad = datos[6];
                return new Entrenador(nombre, apellido, dni, sexo, new Date(), sueldo, especialidad, null);
            } else if (tipo.equals("Limpieza")) {
                String horario = datos[6];
                String sector = (datos.length > 7) ? datos[7] : "";
                return new Limpieza(nombre, apellido, dni, sexo, new Date(), sueldo, horario, sector);
            }
        }
        return null;
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
