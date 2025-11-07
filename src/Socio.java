import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Socio {

    private String nombre;
    private String apellido;
    private int dni;
    private String membresia;
    private List<Clase> clasesInscriptas;
    private CuentaBancaria cuenta;

    // Nuevos campos requeridos
    private Date fechaInscripcion;
    private Date fechaVencimientoPlan; // fecha en que vence el plan
    private boolean activo;
    private String plan; // ejemplo: "Premium"
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

    public boolean inscribirse(Clase c) {
        if (c == null) {
            return false;
        }

        if (!clasesInscriptas.contains(c)) {
            clasesInscriptas.add(c);
            return true;
        }
        return false;
    }

    public boolean cancelarInscripcion(Clase c) {
        return clasesInscriptas.remove(c);
    }

    public boolean pagarCuota(double monto, CuentaBancaria cuentaGimnasio) {
        if (cuenta == null || cuentaGimnasio == null || monto <= 0) {
            return false;
        }

        if (cuenta.extraer(monto)) {
            cuentaGimnasio.depositar(monto);
            // actualizar fecha de vencimiento del plan: sumar los meses contratados
            Date ahora = new Date();
            Date base = (fechaVencimientoPlan != null && fechaVencimientoPlan.after(ahora)) ? fechaVencimientoPlan : ahora;
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(base);
            if (planMeses > 0) cal.add(java.util.Calendar.MONTH, planMeses);
            fechaVencimientoPlan = cal.getTime();
            this.activo = true;
            return true;
        }
        return false;
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

    public List<Clase> getClasesInscriptas() {
        return Collections.unmodifiableList(clasesInscriptas);
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

    public String getFechaVencimientoFormateada() {
        if (fechaVencimientoPlan == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(fechaVencimientoPlan);
    }

    private List<Clase> crearListaInicial(List<Clase> clases) {
        return clases != null ? clases : new ArrayList<>();
    }
}
