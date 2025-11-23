import java.util.*;

/**
 * Clase principal del modelo que representa el gimnasio y gestiona a los
 * empleados, socios, clases, pagos y registros.
 */
public class Gimnasio {
    // Constantes de configuración
    public static final String[] DIAS_SEMANA = { "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado",
            "Domingo" };
    public static final String[] TURNOS = { "Mañana", "Tarde", "Noche" };
    public static final String[] TIPOS_CLASE = { "Crossfit", "Funcional", "Aerobico", "Hyorx", "Musculacion", "Zumba" };

    // Datos del gimnasio
    private String nombre;
    private int CUIT;
    private String direccion;
    private String provincia;
    List<Empleado> empleados;
    List<Socio> socios;
    List<Clase> clases;
    List<Registro> registros;
    CuentaBancaria cuenta;
    // Estructuras auxiliares para búsquedas rápidas
    Map<Integer, Empleado> empleadosPorDni;
    Map<Integer, Socio> sociosPorDni;
    Map<String, Clase> clasesPorHorario;

    /**
     * Constructor de Gimnasio. Inicializa las estructuras de datos vacías.
     */
    public Gimnasio(String nombre, int CUIT, String direccion, String provincia) {
        this.nombre = nombre;
        this.CUIT = CUIT;
        this.direccion = direccion;
        this.provincia = provincia;
        this.empleados = new ArrayList<>();
        this.socios = new ArrayList<>();
        this.clases = new ArrayList<>();
        this.registros = new ArrayList<>();
        this.empleadosPorDni = new HashMap<>();
        this.sociosPorDni = new HashMap<>();
        this.clasesPorHorario = new HashMap<>();
    }

    // Métodos de gestión de entidades

    public void agregarEmpleado(Empleado e) {
        if (e != null) {
            e.agregarAlGimnasio(this);
        }
    }

    public void eliminarEmpleado(Empleado e) {
        if (e != null) {
            e.eliminarDelGimnasio(this);
        }
    }

    public void agregarSocio(Socio s) {
        if (s != null) {
            s.agregarAlGimnasio(this);
        }
    }

    public void eliminarSocio(Socio s) {
        if (s != null) {
            s.eliminarDelGimnasio(this);
        }
    }

    public void agregarClase(Clase c) {
        if (c != null) {
            c.agregarAlGimnasio(this);
        }
    }

    public void eliminarClase(Clase c) {
        if (c != null) {
            c.eliminarDelGimnasio(this);
        }
    }

    // Métodos de pagos y transacciones financieras

    public boolean registrarPagoSueldo(Empleado e) {
        if (e == null || cuenta == null) {
            return false;
        }
        return cuenta.registrarPagoSueldo(e, this);
    }

    /**
     * Registra el pago de la cuota de un socio (extrae de la cuenta del socio y
     * deposita en la del gimnasio).
     */
    public boolean registrarPagoSocio(Socio s, double monto) {
        if (s == null || cuenta == null) {
            return false;
        }
        return cuenta.registrarPagoSocio(s, monto, this);
    }

    // Métodos de búsqueda

    /** Busca y retorna un socio por DNI, o null si no existe. */
    public Socio buscarSocioPorDni(int dni) {
        return sociosPorDni.get(dni);
    }

    /** Busca y retorna un empleado por DNI, o null si no existe. */
    public Empleado buscarEmpleadoPorDni(int dni) {
        return empleadosPorDni.get(dni);
    }

    /** Obtiene la clase asignada en un determinado día y turno (horario). */
    public Clase getClaseEnHorario(String dia, String turno) {
        String clave = dia + " - " + turno;
        return clasesPorHorario.get(clave);
    }

    // Getters y Setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCUIT() {
        return CUIT;
    }

    public void setCUIT(int CUIT) {
        this.CUIT = CUIT;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public List<Empleado> getEmpleados() {
        return Collections.unmodifiableList(empleados);
    }

    public List<Socio> getSocios() {
        return Collections.unmodifiableList(socios);
    }

    public List<Clase> getClases() {
        return Collections.unmodifiableList(clases);
    }

    public List<Registro> getRegistros() {
        return Collections.unmodifiableList(registros);
    }

    public CuentaBancaria getCuenta() {
        return cuenta;
    }

    public void setCuenta(CuentaBancaria cuenta) {
        this.cuenta = cuenta;
    }

    @Override
    public String toString() {
        return "Gimnasio{" +
                "nombre='" + nombre + '\'' +
                ", CUIT=" + CUIT +
                ", direccion='" + direccion + '\'' +
                ", provincia='" + provincia + '\'' +
                ", empleados=" + empleados.size() +
                ", socios=" + socios.size() +
                ", clases=" + clases.size() +
                ", registros=" + registros.size() +
                '}';
    }

    /**
     * Carga los datos del sistema.
     * 1. Carga Empleados (estado).
     * 2. Carga Socios (estado).
     * 3. Carga Clases (estado).
     * 4. Carga Registros Financieros (historial).
     */
    public void cargarDatos() {
        // 1. Cargar Empleados
        cargarEmpleados();
        // 2. Cargar Socios
        cargarSocios();
        // 3. Cargar Clases
        cargarClases();
        // 4. Cargar Registros Financieros
        cargarRegistrosFinancieros();
    }

    private void cargarEmpleados() {
        java.io.File archivo = new java.io.File("datos/registrosEmpleados.txt");
        if (archivo.exists()) {
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    if (linea.trim().isEmpty())
                        continue;
                    Empleado e = Empleado.fromCSV(linea);
                    if (e != null) {
                        empleados.add(e);
                        empleadosPorDni.put(e.getDni(), e);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error cargando empleados: " + e.getMessage());
            }
        }
    }

    private void cargarSocios() {
        java.io.File archivo = new java.io.File("datos/registrosSocios.txt");
        if (archivo.exists()) {
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    if (linea.trim().isEmpty())
                        continue;
                    Socio s = Socio.fromCSV(linea);
                    if (s != null) {
                        socios.add(s);
                        sociosPorDni.put(s.getDni(), s);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error cargando socios: " + e.getMessage());
            }
        }
    }

    private void cargarClases() {
        java.io.File archivo = new java.io.File("datos/registrosClase.txt");
        if (archivo.exists()) {
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(archivo))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    if (linea.trim().isEmpty())
                        continue;
                    Clase c = Clase.fromCSV(linea, empleadosPorDni);
                    if (c != null) {
                        clases.add(c);
                        clasesPorHorario.put(c.getHorario(), c);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error cargando clases: " + e.getMessage());
            }
        }
    }

    private void cargarRegistrosFinancieros() {
        java.io.File archivo = new java.io.File("datos/registros.txt");
        if (!archivo.exists()) {
            this.cuenta = new CuentaBancaria("001", 0, this.nombre);
            return;
        }

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(archivo))) {
            String linea;
            boolean primeraLinea = true;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty())
                    continue;

                if (primeraLinea) {
                    if (linea.startsWith("GIMNASIO|")) {
                        String[] partes = linea.split("\\|");
                        if (partes.length >= 7) {
                            this.nombre = partes[1];
                            this.CUIT = Integer.parseInt(partes[2]);
                            this.direccion = partes[3];
                            this.provincia = partes[4];
                            String nroCuenta = partes[5];
                            double saldo = Double.parseDouble(partes[6]);
                            this.cuenta = new CuentaBancaria(nroCuenta, saldo, this.nombre);
                        }
                        primeraLinea = false;
                        continue;
                    }
                    primeraLinea = false;
                }

                // Procesar solo registros financieros para reconstruir saldo o historial
                procesarRegistroFinanciero(linea);
            }
            if (this.cuenta == null) {
                this.cuenta = new CuentaBancaria("001", 0, this.nombre);
            }
        } catch (Exception e) {
            System.err.println("Error cargando registros financieros: " + e.getMessage());
        }
    }

    private void procesarRegistroFinanciero(String linea) {
        try {
            String[] partes = linea.split("\\|");
            if (partes.length < 6)
                return;

            int id = Integer.parseInt(partes[0]);
            String tipo = partes[2];
            String desc = partes[3];
            double monto = Double.parseDouble(partes[4]);
            // String datosExtra = partes[5]; // No necesitamos datos extra para el saldo
            // global por ahora

            // Crear registro en memoria (solo para historial visual si se desea)
            java.util.Date fecha = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(partes[1]);
            Registro reg = new Registro(id, fecha, tipo, desc, monto, null, null, null);
            registros.add(reg);

            // Actualizar cuenta si es necesario (aunque el saldo ya viene del header,
            // si quisiéramos recalcular podríamos hacerlo aquí, pero confiamos en el header
            // por ahora
            // o solo agregamos el movimiento a la lista de la cuenta sin afectar saldo si
            // ya está seteado)
            if (this.cuenta != null) {
                this.cuenta.agregarMovimiento(reg);
            }

        } catch (Exception e) {
            // Ignorar errores de parseo en registros viejos
        }
    }

    public void guardarSocios() {
        new java.io.File("datos").mkdirs();
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter("datos/registrosSocios.txt"))) {
            for (Socio s : socios) {
                pw.println(s.toCSV());
            }
        } catch (Exception e) {
            System.err.println("Error guardando socios: " + e.getMessage());
        }
    }

    public void guardarEmpleados() {
        new java.io.File("datos").mkdirs();
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter("datos/registrosEmpleados.txt"))) {
            for (Empleado e : empleados) {
                pw.println(e.toCSV());
            }
        } catch (Exception e) {
            System.err.println("Error guardando empleados: " + e.getMessage());
        }
    }

    public void guardarClases() {
        new java.io.File("datos").mkdirs();
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter("datos/registrosClase.txt"))) {
            for (Clase c : clases) {
                pw.println(c.toCSV());
            }
        } catch (Exception e) {
            System.err.println("Error guardando clases: " + e.getMessage());
        }
    }

    /**
     * Guarda el estado financiero (Header + Historial de transacciones).
     * NO guarda entidades (Socios, Empleados, Clases) que tienen sus propios
     * archivos.
     */
    public void guardarEstadoCompleto() {
        new java.io.File("datos").mkdirs();
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter("datos/registros.txt"))) {
            // Header
            String nroCuenta = (cuenta != null) ? cuenta.getNroCuenta() : "000";
            double saldo = (cuenta != null) ? cuenta.getSaldo() : 0.0;
            pw.println("GIMNASIO|" + nombre + "|" + CUIT + "|" + direccion + "|" + provincia + "|" + nroCuenta + "|"
                    + saldo);

            // Registros (solo financieros o generales)
            for (Registro r : registros) {
                // Filtramos para no guardar basura si hubiera, aunque ahora 'registros' solo
                // debería tener financieros
                pw.println(r.toCSV());
            }
        } catch (Exception e) {
            System.err.println("Error guardando registros financieros: " + e.getMessage());
        }
    }

    // Métodos delegados que ahora también disparan el guardado específico

    public void registrarModificacionSocio(Socio s) {
        guardarSocios();
    }

    public void registrarModificacionEmpleado(Empleado e) {
        guardarEmpleados();
    }

    public void registrarModificacionClase(Clase c) {
        guardarClases();
    }
}
