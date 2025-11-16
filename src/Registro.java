import java.util.Date;

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

    // Getters y setters

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public Socio getSocio() {
        return socio;
    }

    public void setSocio(Socio socio) {
        this.socio = socio;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Clase getClase() {
        return clase;
    }

    public void setClase(Clase clase) {
        this.clase = clase;
    }
}
