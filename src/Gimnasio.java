import java.util.*;

/**
 * Clase principal del modelo que representa el gimnasio y gestiona a los empleados, socios, clases, pagos y registros.
 * Separa la lógica de negocio (gestión de datos) de la interfaz de usuario (ver {@link InterfazGimnasio}).
 */
public class Gimnasio {
    // Constantes de configuración
    public static final String[] DIAS_SEMANA = {"Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo"};
    public static final String[] TURNOS = {"Mañana","Tarde","Noche"};
    public static final String[] TIPOS_CLASE = {"Crossfit","Funcional","Aerobico","Hyorx","Musculacion","Zumba"};

    // Datos del gimnasio
    private String nombre;
    private int CUIT;
    private String direccion;
    private String provincia;
    private List<Empleado> empleados;
    private List<Socio> socios;
    private List<Clase> clases;
    private List<Registro> registros;
    private CuentaBancaria cuenta;
    // Estructuras auxiliares para búsquedas rápidas
    private Map<Integer, Empleado> empleadosPorDni;
    private Map<Integer, Socio> sociosPorDni;
    private Map<String, Clase> clasesPorHorario;

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
        if (e != null && !empleadosPorDni.containsKey(e.getDni())) {
            empleados.add(e);
            empleadosPorDni.put(e.getDni(), e);
            // Registrar acción en registros
            Registro registro = new Registro(
                    registros.size() + 1,
                    new Date(),
                    "AGREGAR_EMPLEADO",
                    "Se agregó empleado: " + e.getNombre() + " " + e.getApellido(),
                    0,
                    null,
                    e,
                    null
            );
            registros.add(registro);
            registro.guardarEnArchivo();
        }
    }

    public void eliminarEmpleado(Empleado e) {
        if (e != null && empleados.remove(e)) {
            empleadosPorDni.remove(e.getDni());
            // Registrar acción en registros
            Registro registro = new Registro(
                    registros.size() + 1,
                    new Date(),
                    "ELIMINAR_EMPLEADO",
                    "Se eliminó empleado: " + e.getNombre() + " " + e.getApellido(),
                    0,
                    null,
                    e,
                    null
            );
            registros.add(registro);
            registro.guardarEnArchivo();
        }
    }

    public void agregarSocio(Socio s) {
        if (s != null && !sociosPorDni.containsKey(s.getDni())) {
            socios.add(s);
            sociosPorDni.put(s.getDni(), s);
            Registro registro = new Registro(
                    registros.size() + 1,
                    new Date(),
                    "AGREGAR_SOCIO",
                    "Se agregó socio: " + s.getNombre() + " " + s.getApellido(),
                    0,
                    s,
                    null,
                    null
            );
            registros.add(registro);
            registro.guardarEnArchivo();
        }
    }

    public void eliminarSocio(Socio s) {
        if (s != null && socios.remove(s)) {
            sociosPorDni.remove(s.getDni());
            Registro registro = new Registro(
                    registros.size() + 1,
                    new Date(),
                    "ELIMINAR_SOCIO",
                    "Se eliminó socio: " + s.getNombre() + " " + s.getApellido(),
                    0,
                    s,
                    null,
                    null
            );
            registros.add(registro);
            registro.guardarEnArchivo();
        }
    }

    public void agregarClase(Clase c) {
        if (c != null && !clasesPorHorario.containsKey(c.getHorario())) {
            clases.add(c);
            clasesPorHorario.put(c.getHorario(), c);
            // Vincular clase con entrenador si corresponde
            if (c.getEntrenador() != null) {
                c.getEntrenador().asignarClase(c);
            }
            Registro registro = new Registro(
                    registros.size() + 1,
                    new Date(),
                    "AGREGAR_CLASE",
                    "Se agregó clase: " + c.getNombre(),
                    0,
                    null,
                    null,
                    c
            );
            registros.add(registro);
            registro.guardarEnArchivo();
        }
    }

    public void eliminarClase(Clase c) {
        if (c != null && clases.remove(c)) {
            clasesPorHorario.remove(c.getHorario());
            if (c.getEntrenador() != null) {
                c.getEntrenador().getClasesAsignadas().remove(c);
            }
            Registro registro = new Registro(
                    registros.size() + 1,
                    new Date(),
                    "ELIMINAR_CLASE",
                    "Se eliminó clase: " + c.getNombre(),
                    0,
                    null,
                    null,
                    c
            );
            registros.add(registro);
            registro.guardarEnArchivo();
        }
    }

    // Métodos de pagos y transacciones financieras

    /**
     * Recorre todos los empleados y les paga el sueldo desde la cuenta del gimnasio.
     * @return la cantidad de empleados a quienes se pagó correctamente.
     */
    public int pagarSueldos() {
        if (cuenta == null) {
            return 0;
        }
        int empleadosPagados = 0;
        for (Empleado e : empleados) {
            if (e.cobrarSueldo(cuenta)) {
                empleadosPagados++;
            }
        }
        return empleadosPagados;
    }

    /**
     * Registra el pago de la cuota de un socio (extrae de la cuenta del socio y deposita en la del gimnasio).
     * @param s Socio que realiza el pago.
     * @param monto Monto de la cuota a pagar.
     * @return true si el pago se realizó correctamente, false en caso contrario.
     */
    public boolean registrarPagoSocio(Socio s, double monto) {
        if (s == null || cuenta == null) {
            return false;
        }
        boolean pagoRealizado = s.pagarCuota(monto, cuenta);
        if (!pagoRealizado) {
            return false;
        }
        Registro registro = new Registro(
                registros.size() + 1,
                new Date(),
                "PAGO_CUOTA",
                "Pago de cuota del socio " + s.getNombre() + " " + s.getApellido(),
                monto,
                s,
                null,
                null
        );
        registros.add(registro);
        registro.guardarEnArchivo();
        return true;
    }

    /**
     * Anula un pago de cuota de un socio (genera un movimiento inverso de devolución).
     * @param s Socio al que se le anulará el pago.
     * @param monto Monto a devolver.
     * @return true si la anulación se realizó correctamente, false en caso contrario.
     */
    public boolean anularRegistroPagoSocio(Socio s, double monto) {
        if (s == null || cuenta == null) {
            return false;
        }
        if (!cuenta.extraer(monto)) {
            return false;
        }
        if (s.getCuenta() != null) {
            s.getCuenta().depositar(monto);
        }
        Registro registro = new Registro(
                registros.size() + 1,
                new Date(),
                "ANULACION_PAGO",
                "Anulación de pago de cuota del socio " + s.getNombre() + " " + s.getApellido(),
                -monto,
                s,
                null,
                null
        );
        registros.add(registro);
        registro.guardarEnArchivo();
        return true;
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
     * Inicializa un sistema de gimnasio con datos de demostración.
     * Crea un gimnasio con empleados, socios, clases y movimientos de ejemplo para pruebas.
     */
    public static Gimnasio iniciarSistemaDemo() {
        // Crear cuenta bancaria del gimnasio con saldo inicial
        CuentaBancaria cuentaGimnasio = new CuentaBancaria("001", 100000, "Gimnasio Olavarría");
        Gimnasio gimnasio = new Gimnasio("Gimnasio Olavarría", 123456789, "San Martín 123", "Buenos Aires");
        gimnasio.setCuenta(cuentaGimnasio);

        Date fechaBase = new Date();
        // Empleados iniciales de ejemplo
        Entrenador entrenador1 = new Entrenador("Carlos", "Pérez", 12345, "M", fechaBase, 50000, "Crossfit", null);
        Limpieza limpieza1 = new Limpieza("Lucía", "Gómez", 67890, "F", fechaBase, 30000, "08:00-12:00", "Salón principal");
        gimnasio.agregarEmpleado(entrenador1);
        gimnasio.agregarEmpleado(limpieza1);

        // Clase demo inicial (Lunes - Mañana)
        Clase clase1 = new Clase("Funcional", "Lunes - Mañana", 20, entrenador1, null);
        gimnasio.agregarClase(clase1);
        
        // Asignar la clase al entrenador
        entrenador1.asignarClase(clase1);

        // Socio inicial de ejemplo
        CuentaBancaria cuentaSocio = new CuentaBancaria("002", 50000, "Juan López");
        Socio socio1 = new Socio("Juan", "López", 11111, "Premium", null, cuentaSocio);
        gimnasio.agregarSocio(socio1);
        // Registrar un pago inicial de cuota
        gimnasio.registrarPagoSocio(socio1, 10000);
        // Pagar sueldos iniciales
        gimnasio.pagarSueldos();

        // Poblar gimnasio con más datos de prueba
        gimnasio.seedDatosCompletos();

        return gimnasio;
    }

    /**
     * Carga un conjunto amplio de datos de ejemplo en el gimnasio.
     * Incluye múltiples entrenadores, personal de limpieza, socios, clases y algunos movimientos financieros.
     */
    private void seedDatosCompletos() {
        String[] especialidades = TIPOS_CLASE;
        // Entrenadores adicionales (nombre, apellido, dni, sexo, sueldo, especialidad)
        Object[][] entrenadoresData = {
            {"Federico","Álvarez",20123456,"M",50000.0, "Crossfit"},
            {"Sofía","Morales",20234567,"F",48000.0, "Funcional"},
            {"Valentina","López",20345678,"F",46000.0, "Zumba"},
            {"Diego","Fernández",20456789,"M",52000.0, "Musculacion"},
            {"Camila","Díaz",20567890,"F",45000.0, "Aerobico"},
            {"Matías","Romero",20678901,"M",51000.0, "Hyorx"}
        };
        for (Object[] row : entrenadoresData) {
            String nom = (String) row[0];
            String ape = (String) row[1];
            int dni = (int) row[2];
            String sexo = (String) row[3];
            double sueldo = (double) row[4];
            String esp = (String) row[5];
            Entrenador ent = new Entrenador(nom, ape, dni, sexo, new Date(), sueldo, esp, null);
            agregarEmpleado(ent);
        }
        // Personal de limpieza adicional
        String[][] limpiezaData = {
            {"Ana","Ruiz","F"},
            {"Laura","Medina","F"},
            {"Paula","Castro","F"}
        };
        for (int i = 0; i < limpiezaData.length; i++) {
            String nom = limpiezaData[i][0];
            String ape = limpiezaData[i][1];
            String sexo = limpiezaData[i][2];
            Limpieza limp = new Limpieza(nom, ape, 30010 + i, sexo, new Date(), 26000, "08:00-12:00", "Sector " + (i+1));
            agregarEmpleado(limp);
        }
        // Socios adicionales con cuentas bancarias
        String[][] sociosData = {
            {"Martín","García","40123456"},
            {"María","Rodríguez","40134567"},
            {"Lucía","González","40145678"},
            {"José","Martínez","40156789"},
            {"Camila","Pérez","40167890"},
            {"Lucas","Hernández","40178901"},
            {"Ana","Sánchez","40189012"},
            {"Fernando","Torres","40190123"},
            {"Martina","Vargas","40201234"},
            {"Bruno","Rossi","40212345"}
        };
        int idx = 0;
        int[] opcionesMeses = {1,3,6,12};
        for (String[] sdata : sociosData) {
            String nom = sdata[0];
            String ape = sdata[1];
            int dni = Integer.parseInt(sdata[2]);
            String nroCuenta = "AR" + dni;
            CuentaBancaria cb = new CuentaBancaria(nroCuenta, 180000 + (idx * 5000), nom + " " + ape);
            Socio socio = new Socio(nom, ape, dni, "Estándar", null, cb);
            int meses = opcionesMeses[idx % opcionesMeses.length];
            socio.setPlanMeses(meses);
            socio.setPlan(meses + " meses");
            agregarSocio(socio);
            // Registrar pagos de cuota para la mayoría de los socios
            if (idx < 9) {
                registrarPagoSocio(socio, 35000 * meses);
            }
            idx++;
        }
        // Crear algunas clases automáticamente (hasta 8 clases)
        String[] dias = {"Lunes","Martes","Miércoles","Jueves","Viernes","Sábado"};
        int claseCount = 0;
        for (String dia : dias) {
            for (String turno : TURNOS) {
                if (claseCount >= 8) break;
                String tipo = especialidades[claseCount % especialidades.length];
                // Buscar entrenador con especialidad correspondiente
                Entrenador entrenador = null;
                for (Empleado e : empleados) {
                    if (e instanceof Entrenador) {
                        Entrenador en = (Entrenador) e;
                        if (en.getEspecialidad() != null && en.getEspecialidad().equalsIgnoreCase(tipo)) {
                            entrenador = en; break;
                        }
                    }
                }
                if (entrenador == null) continue;
                Clase nuevaClase = new Clase(tipo, dia + " - " + turno, 20, entrenador, null);
                agregarClase(nuevaClase);
                // Asignar clase al entrenador
                entrenador.asignarClase(nuevaClase);
                claseCount++;
            }
            if (claseCount >= 8) break;
        }
        // Pagar sueldos una vez más para generar movimientos DEBE adicionales
        pagarSueldos();
    }
}
