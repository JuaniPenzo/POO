import java.util.Date;

/**
 * Clase que representa un registro (movimiento o acción) en el sistema.
 * Puede corresponder a un ingreso o egreso de dinero (HABER/DEBE) u otras
 * acciones del sistema (agregar/eliminar entidades).
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

    // guardarEnArchivo eliminado: La persistencia ahora es manejada
    // centralizadamente por Gimnasio.guardarEstadoCompleto()

    public String toCSV() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(idRegistro).append("|");
        sb.append(sdf.format(fecha)).append("|");
        sb.append(tipo).append("|");
        sb.append(descripcion).append("|");
        sb.append(monto).append("|");

        // Serializar entidades si existen
        if (socio != null) {
            sb.append("SOCIO;").append(socio.getDni()).append(";").append(socio.getNombre()).append(";")
                    .append(socio.getApellido())
                    .append(";").append(socio.getMembresia()).append(";").append(socio.getPlanMeses());
            // Agregar datos de cuenta del socio si es necesario para reconstruirla
            if (socio.getCuenta() != null) {
                sb.append(";").append(socio.getCuenta().getNroCuenta());
            } else {
                sb.append(";null");
            }
        } else if (empleado != null) {
            sb.append("EMPLEADO;").append(empleado.getClass().getSimpleName()).append(";")
                    .append(empleado.getDni()).append(";").append(empleado.getNombre()).append(";")
                    .append(empleado.getApellido())
                    .append(";").append(empleado.getSexo()).append(";").append(empleado.getSueldo());
            if (empleado instanceof Entrenador) {
                sb.append(";").append(((Entrenador) empleado).getEspecialidad());
            } else if (empleado instanceof Limpieza) {
                sb.append(";").append(((Limpieza) empleado).getHorarioTrabajo()).append(";")
                        .append(((Limpieza) empleado).getSector());
            }
        } else if (clase != null) {
            sb.append("CLASE;").append(clase.getNombre()).append(";").append(clase.getHorario()).append(";")
                    .append(clase.getCupoMaximo());
            if (clase.getEntrenador() != null) {
                sb.append(";").append(clase.getEntrenador().getDni());
            } else {
                sb.append(";null");
            }
        } else {
            sb.append("null");
        }

        return sb.toString();
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
