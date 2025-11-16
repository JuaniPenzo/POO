import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    // GUI components for the timetable
    private JFrame mainFrame;
    private JButton[][] gridButtons; // [turno][dia]
    private final String[] diasSemana = new String[]{"Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo"};
    private final String[] turnos = new String[]{"Mañana","Tarde","Noche"};

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

    // Clase demo en Lunes - Mañana para encajar con la grilla de turnos
    Clase clase1 = new Clase("Funcional", "Lunes - Mañana", 20, entrenador1, null);
        entrenador1.asignarClase(clase1);
        gimnasio.agregarClase(clase1);

        CuentaBancaria cuentaSocio = new CuentaBancaria("002", 50000, "Juan López");
        Socio socio1 = new Socio("Juan", "López", 11111, "Premium", null, cuentaSocio);
        gimnasio.agregarSocio(socio1);

        gimnasio.registrarPagoSocio(socio1, 10000);
        gimnasio.pagarSueldos();

    // Añadir datos adicionales para pruebas
    gimnasio.seedDatosCompletos();

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

    /**
     * Popula el gimnasio con múltiples datos de prueba: entrenadores, personal de limpieza,
     * socios, clases y algunos pagos/sueldos para generar movimientos en la cuenta.
     */
    private void seedDatosCompletos() {
        // Nombres reales y creíbles para pruebas
        String[] especialidades = new String[]{"Crossfit","Funcional","Aerobico","Hyorx","Musculacion","Zumba"};

        // Entrenadores reales (nombre, apellido, dni, sexo, fechaNac, sueldo, especialidad)
        Object[][] entrenadores = new Object[][]{
                {"Federico","Álvarez",20123456,"M",50000, "Crossfit"},
                {"Sofía","Morales",20234567,"F",48000, "Funcional"},
                {"Valentina","López",20345678,"F",46000, "Zumba"},
                {"Diego","Fernández",20456789,"M",52000, "Musculacion"},
                {"Camila","Díaz",20567890,"F",45000, "Aerobico"},
                {"Matías","Romero",20678901,"M",51000, "Hyorx"}
        };
        for (Object[] row : entrenadores) {
            String nom = (String) row[0];
            String ape = (String) row[1];
            int dni = (int) row[2];
            String sexo = (String) row[3];
            double sueldo = ((Number)row[4]).doubleValue();
            String esp = (String) row[5];
            Entrenador ent = new Entrenador(nom, ape, dni, sexo, new Date(), sueldo, esp, null);
            agregarEmpleado(ent);
        }

        // Personal de limpieza con nombres reales
        String[][] limpiezas = new String[][]{
                {"Ana","Ruiz","F"},
                {"Laura","Medina","F"},
                {"Paula","Castro","F"}
        };
        for (int i = 0; i < limpiezas.length; i++) {
            String nom = limpiezas[i][0];
            String ape = limpiezas[i][1];
            String sexo = limpiezas[i][2];
            Limpieza limp = new Limpieza(nom, ape, 30010 + i, sexo, new Date(), 26000, "08:00-12:00", "Sector " + (i+1));
            agregarEmpleado(limp);
        }

        // Socios con nombres reales y cuentas
        String[][] sociosDatos = new String[][]{
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
        int[] opcionesMeses = new int[]{1,3,6,12};
        for (String[] sdata : sociosDatos) {
            String nom = sdata[0];
            String ape = sdata[1];
            int dni = Integer.parseInt(sdata[2]);
            String nroCuenta = "AR" + dni;
            CuentaBancaria cb = new CuentaBancaria(nroCuenta, 180000 + (idx * 5000), nom + " " + ape);
            Socio s = new Socio(nom, ape, dni, "Estándar", null, cb);
            int meses = opcionesMeses[idx % opcionesMeses.length];
            s.setPlanMeses(meses);
            s.setPlan(meses + " meses");
            agregarSocio(s);
            // registrar pago para la mayoría
            if (idx < 9) registrarPagoSocio(s, 35000 * meses);
            idx++;
        }

        // Crear algunas clases y asignar entrenadores según su especialidad
        String[] dias = new String[]{"Lunes","Martes","Miércoles","Jueves","Viernes","Sábado"};
        int claseCount = 0;
        for (String dia : dias) {
            for (String turno : turnos) {
                if (claseCount >= 8) break;
                String tipo = especialidades[claseCount % especialidades.length];
                Entrenador elegido = null;
                for (Empleado e : empleados) if (e instanceof Entrenador) {
                    Entrenador en = (Entrenador) e;
                    if (en.getEspecialidad() != null && en.getEspecialidad().equalsIgnoreCase(tipo)) { elegido = en; break; }
                }
                if (elegido == null) continue;
                Clase c = new Clase(tipo, dia + " - " + turno, 20, elegido, null);
                agregarClase(c);
                elegido.asignarClase(c);
                claseCount++;
            }
            if (claseCount >= 8) break;
        }

        // Pagar sueldos para generar movimientos DEBE
        pagarSueldos();
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
            // si la interfaz gráfica está visible, actualizar la grilla
            SwingUtilities.invokeLater(this::actualizarGrilla);
        }
    }

    public void eliminarClase(Clase c) {
        if (c != null && clases.remove(c)) {
            // remover la clase de la lista del entrenador si aplica
            if (c.getEntrenador() != null) {
                c.getEntrenador().getClasesAsignadas().remove(c);
            }
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

    /**
     * Nueva interfaz principal basada en Swing: muestra una grilla semanal (3 turnos x 7 días)
     * y controles para abrir los menús existentes. La grilla se actualiza cuando se agregan clases.
     */
    public void mostrarInterfazPrincipal() {
        // Crear UI en EDT
        SwingUtilities.invokeLater(this::createAndShowGUI);
    }

    private void createAndShowGUI() {
        if (mainFrame != null) {
            mainFrame.toFront();
            return;
        }
        mainFrame = new JFrame("Sistema - " + nombre);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout(8,8));

        // Header: días de la semana
        JPanel header = new JPanel(new GridLayout(1, diasSemana.length));
        for (String dia : diasSemana) {
            JLabel lbl = new JLabel(dia, SwingConstants.CENTER);
            lbl.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            header.add(lbl);
        }
        mainFrame.add(header, BorderLayout.NORTH);

        // Grid de turnos x dias
        JPanel gridPanel = new JPanel(new GridLayout(turnos.length, diasSemana.length));
        gridButtons = new JButton[turnos.length][diasSemana.length];
        for (int t = 0; t < turnos.length; t++) {
            for (int d = 0; d < diasSemana.length; d++) {
                JButton btn = new JButton();
                btn.setVerticalTextPosition(SwingConstants.CENTER);
                btn.setHorizontalTextPosition(SwingConstants.CENTER);
                btn.setPreferredSize(new Dimension(140, 80));
                final int turnoIdx = t;
                final int diaIdx = d;
                btn.addActionListener(e -> onGridCellClicked(diaIdx, turnoIdx));
                btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                // deshabilitar la columna Domingo para indicar que no se crean clases ese día
                if ("Domingo".equalsIgnoreCase(diasSemana[d])) {
                    btn.setEnabled(false);
                    btn.setBackground(Color.LIGHT_GRAY);
                    btn.setToolTipText("No se permiten clases los domingos");
                }
                gridButtons[t][d] = btn;
                gridPanel.add(btn);
            }
        }
        mainFrame.add(gridPanel, BorderLayout.CENTER);

        // Panel de botones de control
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 10,10));
        JButton btnSocios = new JButton("Socios");
        btnSocios.addActionListener(e -> menuSocios());
        JButton btnEmpleados = new JButton("Empleados");
        btnEmpleados.addActionListener(e -> menuEmpleados());
        JButton btnClases = new JButton("Clases");
        btnClases.addActionListener(e -> menuClases());
        JButton btnCuenta = new JButton("Cuenta bancaria");
        btnCuenta.addActionListener(e -> menuCuentaBancaria());
        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> {
            mainFrame.dispose();
            // también cerrar la aplicación
            System.exit(0);
        });
        controls.add(btnSocios);
        controls.add(btnEmpleados);
        controls.add(btnClases);
        controls.add(btnCuenta);
        controls.add(btnSalir);
        mainFrame.add(controls, BorderLayout.SOUTH);

        actualizarGrilla();

        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    private void onGridCellClicked(int diaIdx, int turnoIdx) {
        String dia = diasSemana[diaIdx];
        String turno = turnos[turnoIdx];
        // buscar clase en ese slot (debiera ser como máximo una porque evitamos solapamientos)
        Clase encontrada = null;
        for (Clase c : clases) {
            String horario = c.getHorario() != null ? c.getHorario() : "";
            if (horario.toLowerCase().contains(dia.toLowerCase()) && horario.toLowerCase().contains(turno.toLowerCase())) {
                encontrada = c;
                break;
            }
        }
        if (encontrada == null) {
            JOptionPane.showMessageDialog(null, "No hay clases asignadas en " + dia + " - " + turno);
            return;
        }

        String[] opciones = new String[]{"Ver","Modificar","Eliminar","Cancelar"};
        int sel = JOptionPane.showOptionDialog(null,
                "Clase: " + encontrada.getNombre() + "\nEntrenador: " + (encontrada.getEntrenador() != null ? encontrada.getEntrenador().getNombre() : "N/A") + "\nCupo: " + encontrada.getCupoMaximo(),
                dia + " - " + turno,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);

        if (sel == 0) { // Ver
            JOptionPane.showMessageDialog(null, encontrada.toString(), "Detalle clase", JOptionPane.INFORMATION_MESSAGE);
        } else if (sel == 1) { // Modificar
            // permitir modificar tipo (nombre), cupo y entrenador (entrenador debe tener especialidad compatible)
            String[] tiposClase = new String[]{"Crossfit","Funcional","Aerobico","Hyorx","Musculacion","Zumba"};
            String nuevoTipo = (String) JOptionPane.showInputDialog(null, "Tipo de clase:", "Modificar clase",
                    JOptionPane.QUESTION_MESSAGE, null, tiposClase, encontrada.getNombre());
            if (nuevoTipo == null) return;
            String cupoTxt = JOptionPane.showInputDialog(null, "Cupo máximo:", String.valueOf(encontrada.getCupoMaximo()));
            int nuevoCupo = encontrada.getCupoMaximo();
            try { if (cupoTxt != null && !cupoTxt.isEmpty()) nuevoCupo = Integer.parseInt(cupoTxt); } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(null, "Cupo inválido. Se mantiene el valor previo."); }
            // buscar entrenadores disponibles para nuevoTipo
            java.util.List<Entrenador> disp = new java.util.ArrayList<>();
            for (Empleado e : empleados) if (e instanceof Entrenador) {
                Entrenador en = (Entrenador) e;
                if (en.getEspecialidad() != null && en.getEspecialidad().equalsIgnoreCase(nuevoTipo)) disp.add(en);
            }
            if (disp.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay entrenadores disponibles para el tipo " + nuevoTipo + ". No se puede modificar.");
                return;
            }
            String[] opts = new String[disp.size()];
            for (int i=0;i<disp.size();i++) opts[i] = disp.get(i).getDni() + " - " + disp.get(i).getNombre();
            String elegido = (String) JOptionPane.showInputDialog(null, "Seleccione entrenador:", "Entrenador",
                    JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
            if (elegido == null) return;
            Entrenador nuevoEnt = null;
            try { int d = Integer.parseInt(elegido.split(" - ")[0]); Empleado eSel = buscarEmpleadoPorDni(d); if (eSel instanceof Entrenador) nuevoEnt = (Entrenador)eSel; } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(null, "Selección inválida."); return; }
            // actualizar entrenador asignado: remover clase del anterior y asignar al nuevo
            if (encontrada.getEntrenador() != null) {
                encontrada.getEntrenador().getClasesAsignadas().remove(encontrada);
            }
            encontrada.setNombre(nuevoTipo);
            encontrada.setCupoMaximo(nuevoCupo);
            encontrada.setEntrenador(nuevoEnt);
            nuevoEnt.asignarClase(encontrada);
            actualizarGrilla();
            JOptionPane.showMessageDialog(null, "Clase modificada.");
        } else if (sel == 2) { // Eliminar
            int conf = JOptionPane.showConfirmDialog(null, "Confirma eliminar la clase " + encontrada.getNombre() + "?", "Eliminar", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                // eliminar de entrenador
                if (encontrada.getEntrenador() != null) encontrada.getEntrenador().getClasesAsignadas().remove(encontrada);
                eliminarClase(encontrada);
                actualizarGrilla();
                JOptionPane.showMessageDialog(null, "Clase eliminada.");
            }
        }
    }

    private void actualizarGrilla() {
        if (gridButtons == null) return;
        // limpiar
        for (int t = 0; t < turnos.length; t++) {
            for (int d = 0; d < diasSemana.length; d++) {
                gridButtons[t][d].setText("");
            }
        }
        // llenar según clases
        for (Clase c : clases) {
            String horario = c.getHorario() != null ? c.getHorario() : "";
            int diaIdx = -1;
            int turnoIdx = -1;
            for (int i = 0; i < diasSemana.length; i++) {
                if (horario.toLowerCase().contains(diasSemana[i].toLowerCase())) { diaIdx = i; break; }
            }
            for (int i = 0; i < turnos.length; i++) {
                if (horario.toLowerCase().contains(turnos[i].toLowerCase())) { turnoIdx = i; break; }
            }
            if (diaIdx >= 0 && turnoIdx >= 0) {
                String text = "<html><b>"+c.getNombre()+"</b><br/>" + (c.getEntrenador()!=null?c.getEntrenador().getNombre():"") + "</html>";
                gridButtons[turnoIdx][diaIdx].setText(text);
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
                    "Cuenta bancaria - seleccione:\n1 - Registrar pago de socio\n2 - Pagar sueldo a empleado (por DNI)\n3 - Ver monto de la cuenta corriente\n4 - Ver movimientos (Debe/Haber)\n0 - Volver",
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
                case "4":
                    mostrarMovimientosCuentaDialog();
                    break;
                case "0":
                    volver = true;
                    break;
                default:
                    JOptionPane.showMessageDialog(null, "Opción inválida.");
            }
        }
    }

    /**
     * Muestra un diálogo con una tabla de movimientos (DEBE/HABER) y filtros por mes/año.
     * También muestra totales: Haber, Debe y Neto (ganancia/pérdida)
     */
    private void mostrarMovimientosCuentaDialog() {
        if (cuenta == null) {
            JOptionPane.showMessageDialog(null, "No hay cuenta bancaria asignada.");
            return;
        }

        JDialog dialog = new JDialog(mainFrame, "Movimientos de la cuenta - " + (cuenta.getNroCuenta() != null ? cuenta.getNroCuenta() : ""), true);
        dialog.setLayout(new BorderLayout(8,8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] meses = new String[]{"Todos","Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
        JComboBox<String> cbMes = new JComboBox<>(meses);
        int añoActual = Calendar.getInstance().get(Calendar.YEAR);
        String[] años = new String[]{"Todos", String.valueOf(añoActual-1), String.valueOf(añoActual), String.valueOf(añoActual+1)};
        JComboBox<String> cbAño = new JComboBox<>(años);
        JButton btnFiltrar = new JButton("Filtrar");
        top.add(new JLabel("Mes:")); top.add(cbMes);
        top.add(new JLabel("Año:")); top.add(cbAño);
        top.add(btnFiltrar);
        dialog.add(top, BorderLayout.NORTH);

        // Tabla de movimientos
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Fecha","Tipo","Descripción","Monto"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        dialog.add(sp, BorderLayout.CENTER);

        JLabel lblResumen = new JLabel(" ");
        dialog.add(lblResumen, BorderLayout.SOUTH);

        // Helper para poblar la tabla según filtros
        Runnable poblar = () -> {
            model.setRowCount(0);
            int mesSel = cbMes.getSelectedIndex(); // 0 == Todos, 1..12
            String añoStr = (String) cbAño.getSelectedItem();
            int añoSel = 0;
            if (añoStr != null && !"Todos".equalsIgnoreCase(añoStr)) {
                try { añoSel = Integer.parseInt(añoStr); } catch (NumberFormatException ex) { añoSel = 0; }
            }

            java.util.List<Registro> lista = new ArrayList<>();
            if (mesSel == 0 && añoSel == 0) {
                lista.addAll(cuenta.getMovimientos());
            } else if (mesSel == 0) {
                // todos los meses, año específico
                lista.addAll(cuenta.getMovimientosPorMes(0, añoSel));
            } else if (añoSel == 0) {
                // mes específico en todos los años
                for (Registro r : cuenta.getMovimientos()) {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    if (r.getFecha() == null) continue;
                    cal.setTime(r.getFecha());
                    int m = cal.get(java.util.Calendar.MONTH) + 1;
                    if (m == mesSel) lista.add(r);
                }
            } else {
                // mes y año específicos
                lista.addAll(cuenta.getMovimientosPorMes(mesSel, añoSel));
            }

            double haber = 0.0, debe = 0.0;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            for (Registro r : lista) {
                String fecha = r.getFecha() != null ? sdf.format(r.getFecha()) : "";
                model.addRow(new Object[]{fecha, r.getTipo(), r.getDescripcion(), r.getMonto()});
                if (r.getTipo() != null && r.getTipo().equalsIgnoreCase("HABER")) haber += r.getMonto();
                else if (r.getTipo() != null && r.getTipo().equalsIgnoreCase("DEBE")) debe += r.getMonto();
            }
            double neto = haber - debe;
            lblResumen.setText(String.format("Total HABER: $%.2f   Total DEBE: $%.2f   Neto: $%.2f", haber, debe, neto));
        };

        btnFiltrar.addActionListener(e -> poblar.run());

        // Poblar inicial
        poblar.run();

        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
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
        String[] especialidades = new String[]{"Crossfit","Funcional","Aerobico","Hyorx","Musculacion","Zumba"};
        String especialidad = (String) JOptionPane.showInputDialog(null, "Seleccione especialidad:", "Especialidad",
            JOptionPane.QUESTION_MESSAGE, null, especialidades, especialidades[0]);
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
        String[] tiposClase = new String[]{"Crossfit","Funcional","Aerobico","Hyorx","Musculacion","Zumba"};
        String nombreClase = (String) JOptionPane.showInputDialog(null, "Seleccione tipo de clase:", "Tipo de clase",
            JOptionPane.QUESTION_MESSAGE, null, tiposClase, tiposClase[0]);
        if (nombreClase == null) return;
        // Seleccionar día y turno (grilla). Solo permitir Lunes-Sábado para creación.
        String[] diasParaCrear = new String[]{"Lunes","Martes","Miércoles","Jueves","Viernes","Sábado"};
        String dia = (String) JOptionPane.showInputDialog(null, "Día de la clase:", "Día",
            JOptionPane.QUESTION_MESSAGE, null, diasParaCrear, diasParaCrear[0]);
        if (dia == null) return;
        String turno = (String) JOptionPane.showInputDialog(null, "Turno:", "Turno",
            JOptionPane.QUESTION_MESSAGE, null, turnos, turnos[0]);
        if (turno == null) return;
        String horario = dia + " - " + turno;
        // Verificar que no exista ya una clase en ese día/turno
        for (Clase existente : clases) {
            String h = existente.getHorario() != null ? existente.getHorario() : "";
            if (h.toLowerCase().contains(dia.toLowerCase()) && h.toLowerCase().contains(turno.toLowerCase())) {
                JOptionPane.showMessageDialog(null, "Ya existe una clase en " + dia + " - " + turno + ". No se puede sobreponer.");
                return;
            }
        }
        String cupoTxt = JOptionPane.showInputDialog(null, "Cupo máximo:");
            int cupo = 10;
            if (cupoTxt != null && !cupoTxt.isEmpty()) cupo = Integer.parseInt(cupoTxt);

            // Seleccionar entrenadores disponibles cuya especialidad coincida con el tipo de clase
            Entrenador ent = null;
            java.util.List<Entrenador> entrenadoresDisponibles = new java.util.ArrayList<>();
            for (Empleado e : empleados) {
                if (e instanceof Entrenador) {
                    Entrenador en = (Entrenador) e;
                    if (en.getEspecialidad() != null && en.getEspecialidad().equalsIgnoreCase(nombreClase)) {
                        entrenadoresDisponibles.add(en);
                    }
                }
            }
            if (entrenadoresDisponibles.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay entrenadores disponibles para el tipo de clase '" + nombreClase + "'.");
                return;
            }
            // Mostrar lista para seleccionar
            String[] opcionesEntr = new String[entrenadoresDisponibles.size()];
            for (int i = 0; i < entrenadoresDisponibles.size(); i++) {
                Entrenador en = entrenadoresDisponibles.get(i);
                opcionesEntr[i] = en.getDni() + " - " + en.getNombre() + " " + en.getApellido();
            }
            String elegido = (String) JOptionPane.showInputDialog(null, "Seleccione entrenador:", "Entrenador",
                    JOptionPane.QUESTION_MESSAGE, null, opcionesEntr, opcionesEntr[0]);
            if (elegido == null) return; // cancel
            try {
                int dniSel = Integer.parseInt(elegido.split(" - ")[0]);
                Empleado eSel = buscarEmpleadoPorDni(dniSel);
                if (eSel instanceof Entrenador) ent = (Entrenador) eSel;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Selección inválida de entrenador.");
                return;
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
        int dni = -1;
        try {
            dni = Integer.parseInt(seleccionado.split(" - ")[0]);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "DNI inválido en la selección.");
            return;
        }
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
    int dni = -1;
    try {
        dni = Integer.parseInt(seleccionado.split(" - ")[0]);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(null, "DNI inválido en la selección.");
        return;
    }
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
