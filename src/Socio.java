import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Clase que representa un socio del gimnasio.
 * Contiene sus datos personales, estado de plan, cuenta bancaria y clases
 * inscriptas.
 */
public class Socio {

    private String nombre;
    private String apellido;
    private int dni;
    private String membresia;
    private List<Clase> clasesInscriptas;
    private CuentaBancaria cuenta;
    private Date fechaInscripcion;
    private Date fechaVencimientoPlan;
    private boolean activo;
    private String plan;
    private int planMeses; // duración en meses: 1,3,6,12

    public Socio(String nombre, String apellido, int dni, String membresia,
            List<Clase> clasesInscriptas, CuentaBancaria cuenta) {
        this(nombre, apellido, dni, membresia, clasesInscriptas, cuenta, new Date(), null, false, "", 0);
    }

    // Constructor ampliado con los nuevos campos
    public Socio(String nombre, String apellido, int dni, String membresia,
            List<Clase> clasesInscriptas, CuentaBancaria cuenta,
            Date fechaInscripcion, Date fechaVencimientoPlan, boolean activo, String plan, int planMeses) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.membresia = membresia;
        this.cuenta = cuenta;
        this.clasesInscriptas = crearListaInicial(clasesInscriptas);
        this.fechaInscripcion = fechaInscripcion != null ? fechaInscripcion : new Date();
        this.fechaVencimientoPlan = fechaVencimientoPlan != null ? fechaVencimientoPlan : this.fechaInscripcion;
        this.activo = activo;
        this.plan = plan != null ? plan : "";
        this.planMeses = planMeses;
    }

    public boolean pagarCuota(double monto, CuentaBancaria cuentaGimnasio) {
        if (cuenta == null || cuentaGimnasio == null || monto <= 0) {
            return false;
        }
        if (cuenta.extraerForzado(monto, "Pago de cuota", this, null, null)) {
            // depositar en la cuenta del gimnasio y vincular este socio al movimiento
            cuentaGimnasio.depositar(monto,
                    "Pago de cuota del socio " + this.getNombre() + " (DNI:" + this.getDni() + ")", this, null, null);
            // actualizar fecha de vencimiento del plan: sumar los meses contratados
            Date ahora = new Date();
            Date base = (fechaVencimientoPlan != null && fechaVencimientoPlan.after(ahora)) ? fechaVencimientoPlan
                    : ahora;
            Calendar cal = Calendar.getInstance();
            cal.setTime(base);
            if (planMeses > 0)
                cal.add(Calendar.MONTH, planMeses);
            fechaVencimientoPlan = cal.getTime();
            this.activo = true;
            return true;
        }
        return false;
    }

    public void agregarAlGimnasio(Gimnasio g) {
        if (g != null && !g.sociosPorDni.containsKey(this.getDni())) {
            g.socios.add(this);
            g.sociosPorDni.put(this.getDni(), this);
            // No creamos registro de evento para persistencia, solo para historial si se
            // desea
            // Pero la persistencia ahora es por estado
            g.guardarSocios();
        }
    }

    public void eliminarDelGimnasio(Gimnasio g) {
        if (g != null && g.socios.remove(this)) {
            g.sociosPorDni.remove(this.getDni());
            g.guardarSocios();
        }
    }

    public String toCSV() {
        String nroCuenta = (cuenta != null) ? cuenta.getNroCuenta() : "null";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String fechaInsc = (fechaInscripcion != null) ? sdf.format(fechaInscripcion) : "null";
        String fechaVenc = (fechaVencimientoPlan != null) ? sdf.format(fechaVencimientoPlan) : "null";
        return dni + ";" + nombre + ";" + apellido + ";" + membresia + ";" + planMeses + ";" + nroCuenta + ";"
                + fechaInsc + ";" + fechaVenc;
    }

    public static Socio fromCSV(String linea) {
        String[] datos = linea.split(";");
        if (datos.length >= 6) {
            int dni = Integer.parseInt(datos[0]);
            String nombre = datos[1];
            String apellido = datos[2];
            String membresia = datos[3];
            int planMeses = Integer.parseInt(datos[4]);
            String nroCuenta = datos[5];

            CuentaBancaria cb = null;
            if (!nroCuenta.equals("null")) {
                cb = new CuentaBancaria(nroCuenta, 0, nombre + " " + apellido);
            }

            // Parse dates if available (backward compatibility)
            Date fechaInsc = null;
            Date fechaVenc = null;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            try {
                if (datos.length >= 7 && !datos[6].equals("null")) {
                    fechaInsc = sdf.parse(datos[6]);
                }
                if (datos.length >= 8 && !datos[7].equals("null")) {
                    fechaVenc = sdf.parse(datos[7]);
                }
            } catch (Exception e) {
                // If parsing fails, dates remain null
            }

            // If dates are null, set defaults
            if (fechaInsc == null) {
                fechaInsc = new Date();
            }
            if (fechaVenc == null) {
                // Calculate expiration based on plan
                Calendar cal = Calendar.getInstance();
                cal.setTime(fechaInsc);
                cal.add(Calendar.MONTH, planMeses);
                fechaVenc = cal.getTime();
            }

            Socio s = new Socio(nombre, apellido, dni, membresia, null, cb, fechaInsc, fechaVenc, false,
                    planMeses + " meses", planMeses);
            return s;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Socio{" +
                "nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", dni=" + dni +
                ", membresia='" + membresia + '\'' +
                ", clasesInscriptas=" + clasesInscriptas.size() +
                '}';
    }

    // Getters y setters

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

    public String getMembresia() {
        return membresia;
    }

    public void setMembresia(String membresia) {
        this.membresia = membresia;
    }

    public CuentaBancaria getCuenta() {
        return cuenta;
    }

    public void setCuenta(CuentaBancaria cuenta) {
        this.cuenta = cuenta;
    }

    public Date getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(Date fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public Date getFechaVencimientoPlan() {
        return fechaVencimientoPlan;
    }

    public void setFechaVencimientoPlan(Date fechaVencimientoPlan) {
        this.fechaVencimientoPlan = fechaVencimientoPlan;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public int getPlanMeses() {
        return planMeses;
    }

    public void setPlanMeses(int planMeses) {
        this.planMeses = planMeses;
    }

    /**
     * Verifica si el socio está activo (plan vigente a la fecha actual).
     * Actualiza el atributo 'activo' según la fecha de vencimiento del plan.
     */
    public boolean isActivo() {
        if (fechaVencimientoPlan == null) {
            this.activo = false;
            return false;
        }
        Date ahora = new Date();
        boolean ahoraActivo = !ahora.after(fechaVencimientoPlan); // ahora <= vencimiento
        this.activo = ahoraActivo;
        return ahoraActivo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getFechaVencimientoFormateada() {
        if (fechaVencimientoPlan == null)
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(fechaVencimientoPlan);
    }

    public List<Clase> getClasesInscriptas() {
        return Collections.unmodifiableList(clasesInscriptas);
    }

    private List<Clase> crearListaInicial(List<Clase> clases) {
        return clases != null ? clases : new ArrayList<>();
    }
}
