import java.util.*;

/**
 * Clase principal del modelo que representa el gimnasio y gestiona a los empleados, socios, clases, pagos y registros.
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
                    null);
            registros.add(registro);
            guardarEstadoCompleto();
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
                    null);
            registros.add(registro);
            guardarEstadoCompleto();
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
                    null);
            registros.add(registro);
            guardarEstadoCompleto();
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
                    null);
            registros.add(registro);
            guardarEstadoCompleto();
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
                    c);
            registros.add(registro);
            guardarEstadoCompleto();
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
                    c);
            registros.add(registro);
            guardarEstadoCompleto();
        }
    }

    // Métodos de pagos y transacciones financieras

    public boolean registrarPagoSueldo(Empleado e) {
        if (e == null || cuenta == null) {
            return false;
        }
        boolean pagoRealizado = e.cobrarSueldo(cuenta);
        if (!pagoRealizado) {
            return false;
        }
        Registro registro = new Registro(
                registros.size() + 1,
                new Date(),
                "DEBE",
                "Pago de sueldo a " + e.getNombre() + " " + e.getApellido(),
                e.getSueldo(),
                null,
                e,
                null);
        registros.add(registro);
        guardarEstadoCompleto();
        return true;
    }

    /**
     * Registra el pago de la cuota de un socio (extrae de la cuenta del socio y deposita en la del gimnasio).
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
                "HABER",
                "Pago de cuota del socio " + s.getNombre() + " " + s.getApellido(),
                monto,
                s,
                null,
                null);
        registros.add(registro);
        guardarEstadoCompleto();
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
     * Carga los datos del sistema desde el archivo de registros.
     * Reconstruye el estado del gimnasio reproduciendo los eventos guardados.
     */
    public void cargarDatos() {
        java.io.File archivo = new java.io.File("registros.txt");
        if (!archivo.exists()) {
            // Si no existe, iniciamos con cuenta vacía o básica
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
                        // GIMNASIO|Nombre|CUIT|Direccion|Provincia|NroCuenta|Saldo
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

                procesarRegistro(linea, true);
            }
            // Si no se creó cuenta (archivo viejo o sin header), crear una por defecto
            if (this.cuenta == null) {
                this.cuenta = new CuentaBancaria("001", 0, this.nombre);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void procesarRegistro(String linea, boolean isLoading) {
        try {
            String[] partes = linea.split("\\|");
            if (partes.length < 6)
                return;

            int id = Integer.parseInt(partes[0]);
            String tipo = partes[2];
            String desc = partes[3];
            double monto = Double.parseDouble(partes[4]);
            String datosExtra = partes[5];

            Socio socioRef = null;
            Empleado empleadoRef = null;
            Clase claseRef = null;

            if (tipo.equals("AGREGAR_SOCIO")) {
                String[] datos = datosExtra.split(";");
                if (datos.length >= 7) {
                    int dni = Integer.parseInt(datos[1]);
                    String nombre = datos[2];
                    String apellido = datos[3];
                    String membresia = datos[4];
                    int planMeses = Integer.parseInt(datos[5]);
                    String nroCuenta = datos[6];

                    CuentaBancaria cb = new CuentaBancaria(nroCuenta, 0, nombre + " " + apellido);
                    Socio s = new Socio(nombre, apellido, dni, membresia, null, cb);
                    s.setPlanMeses(planMeses);
                    s.setPlan(planMeses + " meses");
                    socios.add(s);
                    sociosPorDni.put(dni, s);
                    socioRef = s;
                }
            } else if (tipo.equals("AGREGAR_EMPLEADO")) {
                String[] datos = datosExtra.split(";");
                if (datos.length >= 8) {
                    String claseTipo = datos[1];
                    int dni = Integer.parseInt(datos[2]);
                    String nombre = datos[3];
                    String apellido = datos[4];
                    String sexo = datos[5];
                    double sueldo = Double.parseDouble(datos[6]);

                    Empleado e = null;
                    if (claseTipo.equals("Entrenador")) {
                        String esp = datos[7];
                        e = new Entrenador(nombre, apellido, dni, sexo, new Date(), sueldo, esp, null);
                    } else if (claseTipo.equals("Limpieza")) {
                        String horario = datos[7];
                        String sector = (datos.length > 8) ? datos[8] : "";
                        e = new Limpieza(nombre, apellido, dni, sexo, new Date(), sueldo, horario, sector);
                    }

                    if (e != null) {
                        empleados.add(e);
                        empleadosPorDni.put(dni, e);
                        empleadoRef = e;
                    }
                }
            } else if (tipo.equals("AGREGAR_CLASE")) {
                String[] datos = datosExtra.split(";");
                if (datos.length >= 5) {
                    String nombre = datos[1];
                    String horario = datos[2];
                    int cupo = Integer.parseInt(datos[3]);
                    String dniEntStr = datos[4];

                    Entrenador ent = null;
                    if (!dniEntStr.equals("null")) {
                        Empleado emp = empleadosPorDni.get(Integer.parseInt(dniEntStr));
                        if (emp instanceof Entrenador)
                            ent = (Entrenador) emp;
                    }

                    Clase c = new Clase(nombre, horario, cupo, ent, null);
                    clases.add(c);
                    clasesPorHorario.put(horario, c);
                    if (ent != null)
                        ent.asignarClase(c);
                    claseRef = c;
                }
            } else if (tipo.equals("ELIMINAR_SOCIO")) {
                String[] datos = datosExtra.split(";");
                if (datos.length >= 2) {
                    int dni = Integer.parseInt(datos[1]);
                    Socio s = sociosPorDni.get(dni);
                    if (s != null) {
                        socios.remove(s);
                        sociosPorDni.remove(dni);
                        socioRef = s;
                    }
                }
            } else if (tipo.equals("ELIMINAR_EMPLEADO")) {
                String[] datos = datosExtra.split(";");
                if (datos.length >= 3) {
                    int dni = Integer.parseInt(datos[2]);
                    Empleado e = empleadosPorDni.get(dni);
                    if (e != null) {
                        empleados.remove(e);
                        empleadosPorDni.remove(dni);
                        empleadoRef = e;
                    }
                }
            } else if (tipo.equals("ELIMINAR_CLASE")) {
                String[] datos = datosExtra.split(";");
                if (datos.length >= 3) {
                    String horario = datos[2];
                    Clase c = clasesPorHorario.get(horario);
                    if (c != null) {
                        clases.remove(c);
                        clasesPorHorario.remove(horario);
                        if (c.getEntrenador() != null) {
                            c.getEntrenador().getClasesAsignadas().remove(c);
                        }
                        claseRef = c;
                    }
                }
            } else if (tipo.equals("HABER")) {
                // Si estamos cargando, NO actualizamos el saldo porque ya lo leímos del header
                if (!isLoading) {
                    if (this.cuenta == null)
                        this.cuenta = new CuentaBancaria("001", 0, this.nombre);
                    this.cuenta.depositar(monto, desc);
                }

                if (!datosExtra.equals("null") && datosExtra.startsWith("SOCIO")) {
                    String[] datos = datosExtra.split(";");
                    int dni = Integer.parseInt(datos[1]);
                    Socio s = sociosPorDni.get(dni);
                    if (s != null) {
                        if (s.getCuenta() != null) {
                            s.getCuenta().extraer(monto, "Pago cuota (histórico)");
                        }
                        socioRef = s;
                    }
                }
            } else if (tipo.equals("DEBE")) {
                if (!isLoading) {
                    if (this.cuenta != null)
                        this.cuenta.extraer(monto, desc);
                }
                if (!datosExtra.equals("null") && datosExtra.startsWith("EMPLEADO")) {
                    String[] datos = datosExtra.split(";");
                    // EMPLEADO;Clase;dni...
                    if (datos.length >= 3) {
                        int dni = Integer.parseInt(datos[2]);
                        Empleado e = empleadosPorDni.get(dni);
                        if (e != null) {
                            empleadoRef = e;
                        }
                    }
                }
            } else if (tipo.equals("ANULACION_PAGO")) {
                if (!isLoading) {
                    if (this.cuenta != null)
                        this.cuenta.extraer(Math.abs(monto), desc);
                }
                if (!datosExtra.equals("null") && datosExtra.startsWith("SOCIO")) {
                    String[] datos = datosExtra.split(";");
                    int dni = Integer.parseInt(datos[1]);
                    Socio s = sociosPorDni.get(dni);
                    if (s != null && s.getCuenta() != null) {
                        s.getCuenta().depositar(Math.abs(monto), "Devolución (histórico)");
                        socioRef = s;
                    }
                }
            } else if (tipo.equals("MODIFICAR_SOCIO")) {
                String[] datos = datosExtra.split(";");
                if (datos.length >= 7) {
                    int dni = Integer.parseInt(datos[1]);
                    Socio s = sociosPorDni.get(dni);
                    if (s != null) {
                        s.setNombre(datos[2]);
                        s.setApellido(datos[3]);
                        s.setMembresia(datos[4]);
                        int planMeses = Integer.parseInt(datos[5]);
                        s.setPlanMeses(planMeses);
                        s.setPlan(planMeses + " meses");
                        if (s.getCuenta() != null)
                            s.getCuenta().setNroCuenta(datos[6]);
                        socioRef = s;
                    }
                }
            } else if (tipo.equals("MODIFICAR_EMPLEADO")) {
                String[] datos = datosExtra.split(";");
                if (datos.length >= 7) {
                    int dni = Integer.parseInt(datos[2]);
                    Empleado e = empleadosPorDni.get(dni);
                    if (e != null) {
                        e.setNombre(datos[3]);
                        e.setApellido(datos[4]);
                        e.setSexo(datos[5]);
                        e.setSueldo(Double.parseDouble(datos[6]));

                        if (e instanceof Entrenador && datos.length > 7) {
                            ((Entrenador) e).setEspecialidad(datos[7]);
                        } else if (e instanceof Limpieza && datos.length > 7) {
                            ((Limpieza) e).setHorarioTrabajo(datos[7]);
                            if (datos.length > 8)
                                ((Limpieza) e).setSector(datos[8]);
                        }
                        empleadoRef = e;
                    }
                }
            } else if (tipo.equals("MODIFICAR_CLASE")) {
                String[] datos = datosExtra.split(";");
                if (datos.length >= 5) {
                    String horario = datos[2];
                    Clase c = clasesPorHorario.get(horario);
                    if (c != null) {
                        c.setNombre(datos[1]);
                        c.setCupoMaximo(Integer.parseInt(datos[3]));
                        String dniEntStr = datos[4];
                        if (!dniEntStr.equals("null")) {
                            int dniEnt = Integer.parseInt(dniEntStr);
                            Empleado emp = empleadosPorDni.get(dniEnt);
                            if (emp instanceof Entrenador) {
                                c.setEntrenador((Entrenador) emp);
                                ((Entrenador) emp).asignarClase(c);
                            }
                        } else {
                            c.setEntrenador(null);
                        }
                        claseRef = c;
                    }
                }
            }

            // Crear el objeto Registro y añadirlo a la lista en memoria
            java.util.Date fecha = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(partes[1]);
            Registro reg = new Registro(id, fecha, tipo, desc, monto, socioRef, empleadoRef, claseRef);
            registros.add(reg);
            if (isLoading && this.cuenta != null) {
                if (tipo.equals("HABER") || tipo.equals("DEBE") ||
                        tipo.equals("ANULACION_PAGO") || tipo.equals("DEPOSITO") ||
                        tipo.equals("EXTRACCION") || tipo.equals("HABER") || tipo.equals("DEBE")) {
                    this.cuenta.agregarMovimiento(reg);
                }
            }

        } catch (Exception e) {
            System.err.println("Error procesando línea registro: " + linea + " -> " + e.getMessage());
        }
    }

    /**
     * Guarda el estado completo del gimnasio (header con saldo) y todos los registros.
     * Sobrescribe el archivo registros.txt.
     */
    public void guardarEstadoCompleto() {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter("registros.txt"))) {
            // Escribir Header
            // GIMNASIO|Nombre|CUIT|Direccion|Provincia|NroCuenta|Saldo
            String nroCuenta = (cuenta != null) ? cuenta.getNroCuenta() : "000";
            double saldo = (cuenta != null) ? cuenta.getSaldo() : 0.0;
            pw.println("GIMNASIO|" + nombre + "|" + CUIT + "|" + direccion + "|" + provincia + "|" + nroCuenta + "|"
                    + saldo);

            // Escribir Registros
            for (Registro r : registros) {
                pw.println(r.toCSV());
            }
        } catch (java.io.IOException e) {
            System.err.println("Error al guardar estado completo: " + e.getMessage());
        }
    }

    public void registrarModificacionSocio(Socio s) {
        if (s == null)
            return;
        Registro registro = new Registro(registros.size() + 1, new Date(), "MODIFICAR_SOCIO",
                "Se modificó socio: " + s.getNombre() + " " + s.getApellido(), 0, s, null, null);
        registros.add(registro);
        guardarEstadoCompleto();
    }

    public void registrarModificacionEmpleado(Empleado e) {
        if (e == null)
            return;
        Registro registro = new Registro(registros.size() + 1, new Date(), "MODIFICAR_EMPLEADO",
                "Se modificó empleado: " + e.getNombre() + " " + e.getApellido(), 0, null, e, null);
        registros.add(registro);
        guardarEstadoCompleto();
    }

    public void registrarModificacionClase(Clase c) {
        if (c == null)
            return;
        Registro registro = new Registro(registros.size() + 1, new Date(), "MODIFICAR_CLASE",
                "Se modificó clase: " + c.getNombre(), 0, null, null, c);
        registros.add(registro);
        guardarEstadoCompleto();
    }
}
