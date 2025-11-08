import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Gimnasio {

    private String nombre;
    private int CUIT;
    private String direccion;
    private String provincia;
    private List<Empleado> empleados;
    private List<Socio> socios;
    private List<Clase> clases;
    private List<Registro> registros;
    private CuentaBancaria cuenta;

    public Gimnasio(String nombre, int CUIT, String direccion, String provincia) {
        this.nombre = nombre;
        this.CUIT = CUIT;
        this.direccion = direccion;
        this.provincia = provincia;
        this.empleados = new ArrayList<>();
        this.socios = new ArrayList<>();
        this.clases = new ArrayList<>();
        this.registros = new ArrayList<>();
    }

    public static Gimnasio iniciarSistemaDemo() {
        CuentaBancaria cuentaGimnasio = new CuentaBancaria("001", 100000, "Gimnasio Olavarría");

        Gimnasio gimnasio = new Gimnasio("Gimnasio Olavarría", 123456789, "San Martín 123", "Buenos Aires");
        gimnasio.setCuenta(cuentaGimnasio);

        Date fechaBase = new Date();

        Entrenador entrenador1 = new Entrenador(
                "Carlos", "Pérez", 12345, "M", fechaBase, 50000,
                "CrossFit", null
        );
        Limpieza limpieza1 = new Limpieza(
                "Lucía", "Gómez", 67890, "F", fechaBase, 30000,
                "08:00-12:00", "Salón principal"
        );

        gimnasio.agregarEmpleado(entrenador1);
        gimnasio.agregarEmpleado(limpieza1);

        Clase clase1 = new Clase("Funcional", "Lunes 10:00", 20, entrenador1, null);
        entrenador1.asignarClase(clase1);
        gimnasio.agregarClase(clase1);

        CuentaBancaria cuentaSocio = new CuentaBancaria("002", 50000, "Juan López");
        Socio socio1 = new Socio("Juan", "López", 11111, "Premium", null, cuentaSocio);
        gimnasio.agregarSocio(socio1);

        gimnasio.registrarPagoSocio(socio1, 10000);
        gimnasio.pagarSueldos();

        return gimnasio;
    }

    public void agregarEmpleado(Empleado e) {
        if (e != null && !empleados.contains(e)) {
            empleados.add(e);
            // registrar acción
            Registro registro = new Registro(
                    registros.size() + 1,
                    new java.util.Date(),
                    "AGREGAR_EMPLEADO",
                    "Se agregó empleado: " + e.getNombre() + " " + e.getApellido(),
                    0,
                    null,
                    e,
                    null
            );
            registros.add(registro);
        }
    }

    public void eliminarEmpleado(Empleado e) {
        if (e != null && empleados.remove(e)) {
            Registro registro = new Registro(
                    registros.size() + 1,
                    new java.util.Date(),
                    "ELIMINAR_EMPLEADO",
                    "Se eliminó empleado: " + e.getNombre() + " " + e.getApellido(),
                    0,
                    null,
                    e,
                    null
            );
            registros.add(registro);
        }
    }

    public void agregarSocio(Socio s) {
        if (s != null && !socios.contains(s)) {
            socios.add(s);
            Registro registro = new Registro(
                    registros.size() + 1,
                    new java.util.Date(),
                    "AGREGAR_SOCIO",
                    "Se agregó socio: " + s.getNombre() + " " + s.getApellido(),
                    0,
                    s,
                    null,
                    null
            );
            registros.add(registro);
        }
    }

    public void eliminarSocio(Socio s) {
        if (s != null && socios.remove(s)) {
            Registro registro = new Registro(
                    registros.size() + 1,
                    new java.util.Date(),
                    "ELIMINAR_SOCIO",
                    "Se eliminó socio: " + s.getNombre() + " " + s.getApellido(),
                    0,
                    s,
                    null,
                    null
            );
            registros.add(registro);
        }
    }

    public void agregarClase(Clase c) {
        if (c != null && !clases.contains(c)) {
            clases.add(c);
            Registro registro = new Registro(
                    registros.size() + 1,
                    new java.util.Date(),
                    "AGREGAR_CLASE",
                    "Se agregó clase: " + c.getNombre(),
                    0,
                    null,
                    null,
                    c
            );
            registros.add(registro);
        }
    }

    public void eliminarClase(Clase c) {
        if (c != null && clases.remove(c)) {
            Registro registro = new Registro(
                    registros.size() + 1,
                    new java.util.Date(),
                    "ELIMINAR_CLASE",
                    "Se eliminó clase: " + c.getNombre(),
                    0,
                    null,
                    null,
                    c
            );
            registros.add(registro);
        }
    }

    /**
     * Recorre todos los empleados y les "paga" el sueldo
     * descontando desde la cuenta del gimnasio.
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
     * Registra el pago de la cuota de un socio.
     * Acá simplemente creamos un registro y actualizamos la cuenta del gimnasio.
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
                new java.util.Date(),
                "PAGO_CUOTA",
                "Pago de cuota del socio " + s.getNombre() + " " + s.getApellido(),
                monto,
                s,
                null,
                null
        );
        registros.add(registro);
        return true;
    }

    /**
     * Anula un pago de socio (ejemplo simple: se genera un registro negativo).
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
                new java.util.Date(),
                "ANULACION_PAGO",
                "Anulación de pago de cuota del socio " + s.getNombre() + " " + s.getApellido(),
                -monto,
                s,
                null,
                null
        );
        registros.add(registro);
        return true;
    }


    public void mostrarMenuPrincipal() {
        boolean salir = false;

        while (!salir) {
            String opcion = JOptionPane.showInputDialog(
                    null,
                    construirMenuTexto(),
                    "Menú principal - " + nombre,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (opcion == null) {
                JOptionPane.showMessageDialog(null, "Cerrando el sistema. ¡Hasta luego!");
                break;
            }

            switch (opcion) {
                case "1":
                    menuSocios();
                    break;
                case "2":
                    menuEmpleados();
                    break;
                case "3":
                    menuClases();
                    break;
                case "4":
                    menuCuentaBancaria();
                    break;
                case "5":
                    listarRegistrosAcciones();
                    break;
                case "6":
                    mostrarResumenFinanciero();
                    break;
                case "0":
                    salir = true;
                    JOptionPane.showMessageDialog(null, "Gracias por usar el sistema de " + nombre + ".");
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción no válida. Intente nuevamente.");
            }
        }
    }

    private String construirMenuTexto() {
    return "Seleccione una opción:\n" +
        "1 - Socios (Listar / Agregar / Eliminar)\n" +
        "2 - Empleados (Listar / Agregar / Eliminar)\n" +
        "3 - Clases (Listar / Agregar / Eliminar)\n" +
        "4 - Cuenta bancaria\n" +
        "5 - Registros\n" +
        "6 - Mostrar resumen del gimnasio\n" +
        "0 - Salir";
    }

    private void listarSocios() {
        if (socios.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay socios cargados en el sistema.");
            return;
        }
        StringBuilder detalleSocios = new StringBuilder("Socios inscriptos:\n");
        for (Socio socio : socios) {
            boolean activo = socio.isActivo(); // actualizar estado
            detalleSocios.append("DNI: ").append(socio.getDni()).append(" - ")
                    .append(socio.getNombre()).append(" ").append(socio.getApellido())
                    .append(" - Activo: ").append(activo ? "Sí" : "No")
                    .append(" - Plan: ")
                    .append(socio.getPlan() != null && !socio.getPlan().isEmpty() ? socio.getPlan() : "N/A")
                    .append(" - Vence: ")
                    .append(socio.getFechaVencimientoFormateada())
                    .append("\n");
        }

        JOptionPane.showMessageDialog(null, detalleSocios.toString());
    }

    private void pagarSueldosDesdeMenu() {
        int pagados = pagarSueldos();

        if (pagados == 0) {
            JOptionPane.showMessageDialog(null, "No se pudo pagar sueldos. Verifique la cuenta del gimnasio.");
        } else {
            JOptionPane.showMessageDialog(null, "Se pagaron los sueldos de " + pagados + " empleados.");
        }
    }

    private void gestionarPagoSocio() {
        if (socios.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay socios cargados en el sistema.");
            return;
        }

        try {
            String dniTexto = JOptionPane.showInputDialog(null, "Ingrese el DNI del socio:");
            if (dniTexto == null) {
                return;
            }

            int dni = Integer.parseInt(dniTexto);
            Socio socio = buscarSocioPorDni(dni);
            if (socio == null) {
                JOptionPane.showMessageDialog(null, "No se encontró un socio con ese DNI.");
                return;
            }
            // Opciones fijas de plan y precio
            String[] opcionesPago = new String[]{
                    "1 mes - $35000",
                    "3 meses - $100000",
                    "6 meses - $550000",
                    "12 meses - $1100000"
            };
            String elegidoPago = (String) JOptionPane.showInputDialog(null, "Seleccione el plan a comprar:",
                    "Pago de cuota", JOptionPane.QUESTION_MESSAGE, null, opcionesPago, opcionesPago[0]);
            if (elegidoPago == null) return;

            int meses = 1;
            double monto = 35000;
            if (elegidoPago.startsWith("1 ")) { meses = 1; monto = 35000; }
            else if (elegidoPago.startsWith("3 ")) { meses = 3; monto = 100000; }
            else if (elegidoPago.startsWith("6 ")) { meses = 6; monto = 550000; }
            else if (elegidoPago.startsWith("12")) { meses = 12; monto = 1100000; }

            socio.setPlanMeses(meses);
            socio.setPlan(meses + " meses");

            boolean pagoOk = registrarPagoSocio(socio, monto);

            if (pagoOk) {
                JOptionPane.showMessageDialog(null, "El pago se registró correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo registrar el pago. Verifique los datos.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Los datos ingresados no son válidos.");
        }
    }

    // --- Menú específico para acciones sobre la cuenta bancaria ---
    private void menuCuentaBancaria() {
        boolean volver = false;
        while (!volver) {
            String opcion = JOptionPane.showInputDialog(null,
                    "Cuenta bancaria - seleccione:\n1 - Registrar pago de socio\n2 - Pagar sueldo a empleado (por DNI)\n3 - Ver monto de la cuenta corriente\n0 - Volver",
                    "Menú Cuenta Bancaria",
                    JOptionPane.QUESTION_MESSAGE);
            if (opcion == null) return;
            switch (opcion) {
                case "1":
                    gestionarPagoSocio();
                    break;
                case "2":
                    pagarSueldoEmpleadoDesdeMenu();
                    break;
                case "3":
                    if (cuenta != null) JOptionPane.showMessageDialog(null, "Saldo actual de la cuenta: $" + cuenta.getSaldo());
                    else JOptionPane.showMessageDialog(null, "No hay cuenta bancaria asignada.");
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción inválida.");
            }
        }
    }

    private void pagarSueldoEmpleadoDesdeMenu() {
        try {
            String dniTxt = JOptionPane.showInputDialog(null, "Ingrese DNI del empleado a pagar sueldo:");
            if (dniTxt == null) return;
            int dni = Integer.parseInt(dniTxt);
            Empleado e = buscarEmpleadoPorDni(dni);
            if (e == null) {
                JOptionPane.showMessageDialog(null, "No se encontró empleado con DNI " + dni);
                return;
            }
            if (cuenta == null) {
                JOptionPane.showMessageDialog(null, "No hay cuenta bancaria del gimnasio asignada.");
                return;
            }
            boolean pagado = e.cobrarSueldo(cuenta);
            if (pagado) JOptionPane.showMessageDialog(null, "Sueldo pagado a " + e.getNombre() + " " + e.getApellido());
            else JOptionPane.showMessageDialog(null, "No se pudo pagar el sueldo. Fondos insuficientes.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "DNI inválido.");
        }
    }

    private void mostrarResumenFinanciero() {
        StringBuilder resumen = new StringBuilder();
        resumen.append(toString()).append('\n');

        if (cuenta != null) {
            resumen.append("Saldo del gimnasio: $").append(cuenta.getSaldo()).append('\n');
        } else {
            resumen.append("No hay cuenta bancaria asignada.\n");
        }

        JOptionPane.showMessageDialog(null, resumen.toString());
    }

    private void listarRegistrosAcciones() {
        if (registros.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay registros disponibles.");
            return;
        }
        StringBuilder sb = new StringBuilder("Registros (Agregar / Eliminar) de Socios, Empleados y Clases:\n");
        for (Registro r : registros) {
            String tipo = r.getTipo();
            if (tipo != null && (tipo.startsWith("AGREGAR_") || tipo.startsWith("ELIMINAR_"))) {
                sb.append(r.toString()).append("\n");
            }
        }
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    private Socio buscarSocioPorDni(int dni) {
        for (Socio socio : socios) {
            if (socio.getDni() == dni) {
                return socio;
            }
        }
        return null;
    }

    private Empleado buscarEmpleadoPorDni(int dni) {
        for (Empleado e : empleados) {
            if (e.getDni() == dni) return e;
        }
        return null;
    }

    private Clase buscarClasePorNombre(String nombreClase) {
        for (Clase c : clases) {
            if (c.getNombre().equalsIgnoreCase(nombreClase)) return c;
        }
        return null;
    }

    // Métodos invocados desde el menú para CRUD simples (uso de JOptionPane para demo)
    private void agregarSocioDesdeMenu() {
        try {
            String nombre = JOptionPane.showInputDialog(null, "Nombre del socio:");
            if (nombre == null) return;
            String apellido = JOptionPane.showInputDialog(null, "Apellido del socio:");
            if (apellido == null) return;
            String dniTxt = JOptionPane.showInputDialog(null, "DNI:");
            if (dniTxt == null) return;
            int dni = Integer.parseInt(dniTxt);
            String membresia = JOptionPane.showInputDialog(null, "Tipo de membresía (e.g. Premium):");
            if (membresia == null) membresia = "Estándar";
            String nroCuenta = JOptionPane.showInputDialog(null, "Nro de cuenta bancaria del socio (opcional):");
            if (nroCuenta == null || nroCuenta.trim().isEmpty()) nroCuenta = "000" + dni;

            // seleccionar duración del plan por defecto al crear socio
            String[] opcionesPlan = new String[]{"1","3","6","12"};
            String elegidoPlan = (String) JOptionPane.showInputDialog(null, "Seleccione duración del plan (meses):",
                    "Plan", JOptionPane.QUESTION_MESSAGE, null, opcionesPlan, opcionesPlan[0]);
            int mesesPlan = 1;
            if (elegidoPlan != null) {
                try { mesesPlan = Integer.parseInt(elegidoPlan); } catch (NumberFormatException ex) { mesesPlan = 1; }
            }

            CuentaBancaria cuentaSocio = new CuentaBancaria(nroCuenta, 0 /* saldo inicial eliminado */, nombre + " " + apellido);
            // Crear socio con fecha de inscripción y último pago actuales y activo por defecto
            Socio s = new Socio(nombre, apellido, dni, membresia, null, cuentaSocio, new java.util.Date(), new java.util.Date(), true, mesesPlan + " meses", mesesPlan);
            agregarSocio(s);
            JOptionPane.showMessageDialog(null, "Socio agregado: " + s.getNombre() + " " + s.getApellido());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Datos numéricos inválidos.");
        }
    }

    private void eliminarSocioDesdeMenu() {
        try {
            String dniTxt = JOptionPane.showInputDialog(null, "Ingrese DNI del socio a eliminar:");
            if (dniTxt == null) return;
            int dni = Integer.parseInt(dniTxt);
            Socio s = buscarSocioPorDni(dni);
            if (s == null) {
                JOptionPane.showMessageDialog(null, "No se encontró socio con DNI " + dni);
                return;
            }
            eliminarSocio(s);
            JOptionPane.showMessageDialog(null, "Socio eliminado: " + s.getNombre() + " " + s.getApellido());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "DNI inválido.");
        }
    }

    private void listarEmpleados() {
        if (empleados.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay empleados cargados.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Entrenadores:\n");
        boolean anyEntr = false;
        for (Empleado e : empleados) {
            if (e instanceof Entrenador) {
                Entrenador ent = (Entrenador) e;
                sb.append("DNI: ").append(ent.getDni()).append(" - ")
                        .append(ent.getNombre()).append(" ").append(ent.getApellido())
                        .append(" - Esp: ").append(ent.getEspecialidad() != null ? ent.getEspecialidad() : "N/A")
                        .append("\n");
                anyEntr = true;
            }
        }
        if (!anyEntr) sb.append("(ninguno)\n");
        sb.append("\nPersonal de limpieza:\n");
        boolean anyLimp = false;
        for (Empleado e : empleados) {
            if (e instanceof Limpieza) {
                Limpieza limp = (Limpieza) e;
                sb.append("DNI: ").append(limp.getDni()).append(" - ")
                        .append(limp.getNombre()).append(" ").append(limp.getApellido())
                        .append(" - Horario: ").append(limp.getHorarioTrabajo() != null ? limp.getHorarioTrabajo() : "N/A")
                        .append(" - Sector: ").append(limp.getSector() != null ? limp.getSector() : "N/A")
                        .append("\n");
                anyLimp = true;
            }
        }
        if (!anyLimp) sb.append("(ninguno)\n");
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    private void agregarEmpleadoDesdeMenu() {
        try {
            String[] tipos = new String[]{"Entrenador", "Limpieza"};
            String tipo = (String) JOptionPane.showInputDialog(null, "Tipo de empleado:", "Tipo", JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);
            if (tipo == null) return;
            String nombre = JOptionPane.showInputDialog(null, "Nombre:");
            if (nombre == null) return;
            String apellido = JOptionPane.showInputDialog(null, "Apellido:");
            if (apellido == null) return;
            String dniTxt = JOptionPane.showInputDialog(null, "DNI:");
            if (dniTxt == null) return;
            int dni = Integer.parseInt(dniTxt);
            String sexo = JOptionPane.showInputDialog(null, "Sexo (M/F):");
            if (sexo == null) sexo = "N";
            String sueldoTxt = JOptionPane.showInputDialog(null, "Sueldo:");
            double sueldo = 0;
            if (sueldoTxt != null && !sueldoTxt.isEmpty()) sueldo = Double.parseDouble(sueldoTxt);
            if (tipo.equalsIgnoreCase("Entrenador")) {
                String especialidad = JOptionPane.showInputDialog(null, "Especialidad:");
                String fechaTxt = JOptionPane.showInputDialog(null, "Fecha de nacimiento (dd/MM/yyyy) (opcional):");
                java.util.Date fechaNac = new java.util.Date();
                if (fechaTxt != null && !fechaTxt.trim().isEmpty()) {
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                        fechaNac = sdf.parse(fechaTxt);
                    } catch (Exception ex) {
                        // usar fecha actual si parse falla
                        fechaNac = new java.util.Date();
                    }
                }
                Entrenador ent = new Entrenador(nombre, apellido, dni, sexo, fechaNac, sueldo, especialidad, null);
                agregarEmpleado(ent);
                JOptionPane.showMessageDialog(null, "Entrenador agregado: " + ent.getNombre());
            } else {
                String fechaTxt = JOptionPane.showInputDialog(null, "Fecha de nacimiento (dd/MM/yyyy) (opcional):");
                java.util.Date fechaNac = new java.util.Date();
                if (fechaTxt != null && !fechaTxt.trim().isEmpty()) {
                    try {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                        fechaNac = sdf.parse(fechaTxt);
                    } catch (Exception ex) {
                        fechaNac = new java.util.Date();
                    }
                }
                String horario = JOptionPane.showInputDialog(null, "Horario de trabajo (ej. 08:00-12:00):");
                String sector = JOptionPane.showInputDialog(null, "Sector:");
                Limpieza limp = new Limpieza(nombre, apellido, dni, sexo, fechaNac, sueldo, horario, sector);
                agregarEmpleado(limp);
                JOptionPane.showMessageDialog(null, "Personal de limpieza agregado: " + limp.getNombre());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Datos numéricos inválidos.");
        }
    }

    private void eliminarEmpleadoDesdeMenu() {
        try {
            String dniTxt = JOptionPane.showInputDialog(null, "Ingrese DNI del empleado a eliminar:");
            if (dniTxt == null) return;
            int dni = Integer.parseInt(dniTxt);
            Empleado e = buscarEmpleadoPorDni(dni);
            if (e == null) {
                JOptionPane.showMessageDialog(null, "No se encontró empleado con DNI " + dni);
                return;
            }
            eliminarEmpleado(e);
            JOptionPane.showMessageDialog(null, "Empleado eliminado: " + e.getNombre() + " " + e.getApellido());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "DNI inválido.");
        }
    }

    private void listarClases() {
        if (clases.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay clases cargadas.");
            return;
        }
        StringBuilder sb = new StringBuilder("Clases:\n");
        for (Clase c : clases) sb.append("- ").append(c.toString()).append("\n");
        JOptionPane.showMessageDialog(null, sb.toString());
    }

    private void agregarClaseDesdeMenu() {
        try {
            String nombreClase = JOptionPane.showInputDialog(null, "Nombre de la clase:");
            if (nombreClase == null) return;
            String horario = JOptionPane.showInputDialog(null, "Horario (ej. Lunes 10:00):");
            if (horario == null) horario = "Sin horario";
            String cupoTxt = JOptionPane.showInputDialog(null, "Cupo máximo:");
            int cupo = 10;
            if (cupoTxt != null && !cupoTxt.isEmpty()) cupo = Integer.parseInt(cupoTxt);

            String dniEntrTxt = JOptionPane.showInputDialog(null, "DNI del entrenador (opcional):");
            Entrenador ent = null;
            if (dniEntrTxt != null && !dniEntrTxt.isEmpty()) {
                try {
                    int dniEntr = Integer.parseInt(dniEntrTxt);
                    Empleado e = buscarEmpleadoPorDni(dniEntr);
                    if (e instanceof Entrenador) ent = (Entrenador) e;
                } catch (NumberFormatException ex) {
                    // ignorar
                }
            }
            // El DNI del entrenador NO es opcional: pedir hasta obtener un Entrenador válido o cancelar
            while (true) {
                String dniReq = JOptionPane.showInputDialog(null, "DNI del entrenador (obligatorio, cancelar para abortar):");
                if (dniReq == null) {
                    // el usuario canceló la operación de creación de clase
                    JOptionPane.showMessageDialog(null, "Creación de clase cancelada.");
                    return;
                }
                try {
                    int dniEntr = Integer.parseInt(dniReq.trim());
                    Empleado e = buscarEmpleadoPorDni(dniEntr);
                    if (e == null) {
                        JOptionPane.showMessageDialog(null, "No se encontró empleado con DNI " + dniEntr + ". Ingrese un DNI válido.");
                        continue;
                    }
                    if (!(e instanceof Entrenador)) {
                        JOptionPane.showMessageDialog(null, "El DNI indicado no corresponde a un Entrenador. Ingrese el DNI de un entrenador.");
                        continue;
                    }
                    ent = (Entrenador) e;
                    break;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "DNI inválido. Intente nuevamente.");
                }
            }

            Clase c = new Clase(nombreClase, horario, cupo, ent, null);
            agregarClase(c);
            // actualizar lista de clases asignadas del entrenador
            ent.asignarClase(c);
            JOptionPane.showMessageDialog(null, "Clase agregada: " + nombreClase);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Cupo inválido.");
        }
    }

    private void eliminarClaseDesdeMenu() {
        String nombreClase = JOptionPane.showInputDialog(null, "Ingrese el nombre de la clase a eliminar:");
        if (nombreClase == null) return;
        Clase c = buscarClasePorNombre(nombreClase);
        if (c == null) {
            JOptionPane.showMessageDialog(null, "No se encontró la clase: " + nombreClase);
            return;
        }
        eliminarClase(c);
        JOptionPane.showMessageDialog(null, "Clase eliminada: " + nombreClase);
    }

    

    // --- Menús por entidad ---
    private void menuSocios() {
        boolean volver = false;
        while (!volver) {
            String opcion = JOptionPane.showInputDialog(null,
                    "Socios - seleccione:\n1 - Listar socios\n2 - Agregar socio\n3 - Eliminar socio (por DNI)\n4 - Modificar socio (por DNI)\n0 - Volver",
                    "Menú Socios",
                    JOptionPane.QUESTION_MESSAGE);
            if (opcion == null) return;
            switch (opcion) {
                case "1":
                    listarSocios();
                    break;
                case "2":
                    agregarSocioDesdeMenu();
                    break;
                case "3":
                    eliminarSocioPorDni();
                    break;
                case "4":
                    modificarSocioPorDni();
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción inválida.");
            }
        }
    }

    private void menuEmpleados() {
        boolean volver = false;
        while (!volver) {
            String opcion = JOptionPane.showInputDialog(null,
                    "Empleados - seleccione:\n1 - Listar empleados\n2 - Agregar empleado\n3 - Eliminar empleado (por DNI)\n4 - Modificar empleado (por DNI)\n0 - Volver",
                    "Menú Empleados",
                    JOptionPane.QUESTION_MESSAGE);
            if (opcion == null) return;
            switch (opcion) {
                case "1":
                    listarEmpleados();
                    break;
                case "2":
                    agregarEmpleadoDesdeMenu();
                    break;
                case "3":
                    eliminarEmpleadoPorDni();
                    break;
                case "4":
                    modificarEmpleadoPorDni();
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción inválida.");
            }
        }
    }

    private void menuClases() {
        boolean volver = false;
        while (!volver) {
            String opcion = JOptionPane.showInputDialog(null,
                    "Clases - seleccione:\n1 - Listar clases\n2 - Agregar clase\n3 - Eliminar clase\n0 - Volver",
                    "Menú Clases",
                    JOptionPane.QUESTION_MESSAGE);
            if (opcion == null) return;
            switch (opcion) {
                case "1":
                    listarClases();
                    break;
                case "2":
                    agregarClaseDesdeMenu();
                    break;
                case "3":
                    eliminarClaseSeleccion();
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción inválida.");
            }
        }
    }

    // --- Selecciones con confirmación ---
    private void eliminarSocioSeleccion() {
        if (socios.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay socios para eliminar.");
            return;
        }
        String[] opciones = new String[socios.size()];
        for (int i = 0; i < socios.size(); i++) {
            Socio s = socios.get(i);
            opciones[i] = s.getDni() + " - " + s.getNombre() + " " + s.getApellido();
        }
        String seleccionado = (String) JOptionPane.showInputDialog(null, "Seleccione socio a eliminar:", "Eliminar socio",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (seleccionado == null) return;
        // extraer dni
        int dni = Integer.parseInt(seleccionado.split(" - ")[0]);
        Socio s = buscarSocioPorDni(dni);
        if (s == null) return;
        int confirm = JOptionPane.showConfirmDialog(null, "¿Confirma eliminar a " + s.getNombre() + " " + s.getApellido() + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            eliminarSocio(s);
            JOptionPane.showMessageDialog(null, "Socio eliminado.");
        }
    }

    private void eliminarEmpleadoSeleccion() {
        if (empleados.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay empleados para eliminar.");
            return;
        }
        String[] opciones = new String[empleados.size()];
        for (int i = 0; i < empleados.size(); i++) {
            Empleado e = empleados.get(i);
            opciones[i] = e.getDni() + " - " + e.getNombre() + " " + e.getApellido();
        }
        String seleccionado = (String) JOptionPane.showInputDialog(null, "Seleccione empleado a eliminar:", "Eliminar empleado",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (seleccionado == null) return;
        int dni = Integer.parseInt(seleccionado.split(" - ")[0]);
        Empleado e = buscarEmpleadoPorDni(dni);
        if (e == null) return;
        int confirm = JOptionPane.showConfirmDialog(null, "¿Confirma eliminar a " + e.getNombre() + " " + e.getApellido() + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            eliminarEmpleado(e);
            JOptionPane.showMessageDialog(null, "Empleado eliminado.");
        }
    }

    // --- Eliminación/Modificación por DNI ---
    private void eliminarSocioPorDni() {
        try {
            String dniTxt = JOptionPane.showInputDialog(null, "Ingrese DNI del socio a eliminar:");
            if (dniTxt == null) return;
            int dni = Integer.parseInt(dniTxt);
            Socio s = buscarSocioPorDni(dni);
            if (s == null) {
                JOptionPane.showMessageDialog(null, "No se encontró socio con DNI " + dni);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(null, "¿Confirma eliminar a " + s.getNombre() + " " + s.getApellido() + "?",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                eliminarSocio(s);
                JOptionPane.showMessageDialog(null, "Socio eliminado.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "DNI inválido.");
        }
    }

    private void modificarSocioPorDni() {
        try {
            String dniTxt = JOptionPane.showInputDialog(null, "Ingrese DNI del socio a modificar:");
            if (dniTxt == null) return;
            int dni = Integer.parseInt(dniTxt);
            Socio s = buscarSocioPorDni(dni);
            if (s == null) {
                JOptionPane.showMessageDialog(null, "No se encontró socio con DNI " + dni);
                return;
            }
            String nombre = JOptionPane.showInputDialog(null, "Nombre:", s.getNombre());
            if (nombre != null && !nombre.trim().isEmpty()) s.setNombre(nombre);
            String apellido = JOptionPane.showInputDialog(null, "Apellido:", s.getApellido());
            if (apellido != null && !apellido.trim().isEmpty()) s.setApellido(apellido);
            String nroCuenta = JOptionPane.showInputDialog(null, "Nro de cuenta bancaria:", s.getCuenta() != null ? s.getCuenta().getNroCuenta() : "");
            if (nroCuenta != null && !nroCuenta.trim().isEmpty()) {
                if (s.getCuenta() == null) s.setCuenta(new CuentaBancaria(nroCuenta, 0, s.getNombre() + " " + s.getApellido()));
                else s.getCuenta().setNroCuenta(nroCuenta);
            }
            String[] opcionesPlan = new String[]{"1","3","6","12"};
            String elegidoPlan = (String) JOptionPane.showInputDialog(null, "Seleccione duración del plan (meses):",
                    "Plan", JOptionPane.QUESTION_MESSAGE, null, opcionesPlan, String.valueOf(s.getPlanMeses() > 0 ? s.getPlanMeses() : "1"));
            if (elegidoPlan != null) {
                try { int meses = Integer.parseInt(elegidoPlan); s.setPlanMeses(meses); s.setPlan(meses + " meses"); } catch (NumberFormatException ex) {}
            }
            JOptionPane.showMessageDialog(null, "Socio actualizado.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "DNI inválido.");
        }
    }

    private void eliminarEmpleadoPorDni() {
        try {
            String dniTxt = JOptionPane.showInputDialog(null, "Ingrese DNI del empleado a eliminar:");
            if (dniTxt == null) return;
            int dni = Integer.parseInt(dniTxt);
            Empleado e = buscarEmpleadoPorDni(dni);
            if (e == null) {
                JOptionPane.showMessageDialog(null, "No se encontró empleado con DNI " + dni);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(null, "¿Confirma eliminar a " + e.getNombre() + " " + e.getApellido() + "?",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                eliminarEmpleado(e);
                JOptionPane.showMessageDialog(null, "Empleado eliminado.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "DNI inválido.");
        }
    }

    private void modificarEmpleadoPorDni() {
        try {
            String dniTxt = JOptionPane.showInputDialog(null, "Ingrese DNI del empleado a modificar:");
            if (dniTxt == null) return;
            int dni = Integer.parseInt(dniTxt);
            Empleado e = buscarEmpleadoPorDni(dni);
            if (e == null) {
                JOptionPane.showMessageDialog(null, "No se encontró empleado con DNI " + dni);
                return;
            }
            String nombre = JOptionPane.showInputDialog(null, "Nombre:", e.getNombre());
            if (nombre != null && !nombre.trim().isEmpty()) e.setNombre(nombre);
            String apellido = JOptionPane.showInputDialog(null, "Apellido:", e.getApellido());
            if (apellido != null && !apellido.trim().isEmpty()) e.setApellido(apellido);
            String sexo = JOptionPane.showInputDialog(null, "Sexo (M/F):", e.getSexo());
            if (sexo != null && !sexo.trim().isEmpty()) e.setSexo(sexo);
            String sueldoTxt = JOptionPane.showInputDialog(null, "Sueldo:", String.valueOf(e.getSueldo()));
            if (sueldoTxt != null && !sueldoTxt.isEmpty()) {
                try { e.setSueldo(Double.parseDouble(sueldoTxt)); } catch (NumberFormatException ex) {}
            }
            if (e instanceof Entrenador) {
                Entrenador ent = (Entrenador) e;
                String especialidad = JOptionPane.showInputDialog(null, "Especialidad:", ent.getEspecialidad());
                if (especialidad != null && !especialidad.trim().isEmpty()) ent.setEspecialidad(especialidad);
            } else if (e instanceof Limpieza) {
                Limpieza limp = (Limpieza) e;
                String horario = JOptionPane.showInputDialog(null, "Horario de trabajo:", limp.getHorarioTrabajo());
                if (horario != null && !horario.trim().isEmpty()) limp.setHorarioTrabajo(horario);
                String sector = JOptionPane.showInputDialog(null, "Sector:", limp.getSector());
                if (sector != null && !sector.trim().isEmpty()) limp.setSector(sector);
            }
            JOptionPane.showMessageDialog(null, "Empleado actualizado.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "DNI inválido.");
        }
    }

    private void eliminarClaseSeleccion() {
        if (clases.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay clases para eliminar.");
            return;
        }
        String[] opciones = new String[clases.size()];
        for (int i = 0; i < clases.size(); i++) {
            Clase c = clases.get(i);
            opciones[i] = c.getNombre() + " (" + c.getHorario() + ")";
        }
        String seleccionado = (String) JOptionPane.showInputDialog(null, "Seleccione clase a eliminar:", "Eliminar clase",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (seleccionado == null) return;
    // buscar por nombre (primer segmento antes de parentesis)
    int idx = seleccionado.indexOf(" (");
    String nombreClase = idx == -1 ? seleccionado : seleccionado.substring(0, idx);
        Clase c = buscarClasePorNombre(nombreClase);
        if (c == null) return;
        int confirm = JOptionPane.showConfirmDialog(null, "¿Confirma eliminar la clase " + c.getNombre() + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            eliminarClase(c);
            JOptionPane.showMessageDialog(null, "Clase eliminada.");
        }
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

    // Getters y setters

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
}
