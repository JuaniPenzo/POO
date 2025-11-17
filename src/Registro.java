import java.util.Date;

/**
 * Clase que representa un registro (movimiento o acción) en el sistema.
 * Puede corresponder a un ingreso o egreso de dinero (HABER/DEBE) u otras acciones del sistema (agregar/eliminar entidades).
 */
public class Registro {

    private int idRegistro;
    private Date fecha;
    private String tipo;
    private String descripcion;
    private double monto;
    private Socio socio;
    private Empleado empleado;
    private Clase clase;

    public Registro(int idRegistro, Date fecha, String tipo, String descripcion,
                    double monto, Socio socio, Empleado empleado, Clase clase) {
        this.idRegistro = idRegistro;
        this.fecha = fecha;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.monto = monto;
        this.socio = socio;
        this.empleado = empleado;
        this.clase = clase;
    }

    @Override
    public String toString() {
        return "Registro{" +
                "idRegistro=" + idRegistro +
                ", fecha=" + fecha +
                ", tipo='" + tipo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", monto=" + monto +
                ", socio=" + (socio != null ? socio.getNombre() : "N/A") +
                ", empleado=" + (empleado != null ? empleado.getNombre() : "N/A") +
                ", clase=" + (clase != null ? clase.getNombre() : "N/A") +
                '}';
    }

    public void guardarEnArchivo() {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(
                new java.io.BufferedWriter(new java.io.FileWriter("registros.txt", true)))) {
            pw.println(this.toString());
        } catch (java.io.IOException e) {
            System.err.println("Error al guardar registro en archivo: " + e.getMessage());
        }
    }

    public Date getFecha() {
        return fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getMonto() {
        return monto;
    }

    public Socio getSocio() {
        return socio;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public Clase getClase() {
        return clase;
    }
}
