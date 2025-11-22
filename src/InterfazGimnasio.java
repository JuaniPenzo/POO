import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Clase que maneja la interfaz de usuario del gimnasio, utilizando Swing y JOptionPane para interacción.
 */
public class InterfazGimnasio {
    private Gimnasio gimnasio;
    private JFrame mainFrame;
    private JButton[][] gridButtons;

    /**
     * Construye la interfaz de usuario para un gimnasio dado.
     */
    public InterfazGimnasio(Gimnasio gimnasio) {
        this.gimnasio = gimnasio;
    }

    /**
     * Muestra la interfaz gráfica principal (grilla semanal de clases y panel de control).
     */
    public void mostrarInterfazPrincipal() {
        // Crear la interfaz gráfica en el hilo de eventos de Swing
        SwingUtilities.invokeLater(this::crearYMostrarGUI);
    }

    /**
     * Crea y muestra la ventana principal con la grilla de clases y botones de menú.
     */
    private void crearYMostrarGUI() {
        if (mainFrame != null) {
            mainFrame.toFront();
            return;
        }
        mainFrame = new JFrame("Sistema - " + gimnasio.getNombre());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new BorderLayout(8, 8));

        // Cabecera con días de la semana
        JPanel header = new JPanel(new GridLayout(1, Gimnasio.DIAS_SEMANA.length));
        for (String dia : Gimnasio.DIAS_SEMANA) {
            JLabel lbl = new JLabel(dia, SwingConstants.CENTER);
            lbl.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
            header.add(lbl);
        }
        mainFrame.add(header, BorderLayout.NORTH);

        // Panel de grilla (turnos x días)
        JPanel gridPanel = new JPanel(new GridLayout(Gimnasio.TURNOS.length, Gimnasio.DIAS_SEMANA.length));
        gridButtons = new JButton[Gimnasio.TURNOS.length][Gimnasio.DIAS_SEMANA.length];
        for (int t = 0; t < Gimnasio.TURNOS.length; t++) {
            for (int d = 0; d < Gimnasio.DIAS_SEMANA.length; d++) {
                JButton btn = new JButton();
                btn.setVerticalTextPosition(SwingConstants.CENTER);
                btn.setHorizontalTextPosition(SwingConstants.CENTER);
                btn.setPreferredSize(new Dimension(140, 80));
                final int turnoIdx = t;
                final int diaIdx = d;
                btn.addActionListener((ActionEvent e) -> onGridCellClicked(diaIdx, turnoIdx));
                btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                // Deshabilitar columna Domingo (no se dictan clases los domingos)
                if ("Domingo".equalsIgnoreCase(Gimnasio.DIAS_SEMANA[d])) {
                    btn.setEnabled(false);
                    btn.setBackground(Color.LIGHT_GRAY);
                    btn.setToolTipText("No se permiten clases los domingos");
                }
                gridButtons[t][d] = btn;
                gridPanel.add(btn);
            }
        }
        mainFrame.add(gridPanel, BorderLayout.CENTER);

        // Panel de controles (botones de menú)
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
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
            System.exit(0);
        });
        controls.add(btnSocios);
        controls.add(btnEmpleados);
        controls.add(btnClases);
        controls.add(btnCuenta);
        controls.add(btnSalir);
        mainFrame.add(controls, BorderLayout.SOUTH);

        // Actualizar grilla inicial con las clases existentes
        actualizarGrilla();
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    /**
     * Acción al hacer clic en una celda de la grilla (día y turno específicos).
     * Muestra detalles de la clase en ese horario o indica si está libre, permitiendo gestionar la clase.
     */
    private void onGridCellClicked(int diaIdx, int turnoIdx) {
        String dia = Gimnasio.DIAS_SEMANA[diaIdx];
        String turno = Gimnasio.TURNOS[turnoIdx];
        Clase clase = gimnasio.getClaseEnHorario(dia, turno);
        if (clase == null) {
            JOptionPane.showMessageDialog(null, "No hay clases asignadas en " + dia + " - " + turno);
            return;
        }
        String[] opciones = { "Ver", "Modificar", "Eliminar", "Cancelar" };
        int sel = JOptionPane.showOptionDialog(null,
                "Clase: " + clase.getNombre() + "\nEntrenador: "
                        + (clase.getEntrenador() != null ? clase.getEntrenador().getNombre() : "N/A") + "\nCupo: "
                        + clase.getCupoMaximo(),
                dia + " - " + turno,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);
        if (sel == 0) {
            // Ver detalles de la clase
            JOptionPane.showMessageDialog(null, clase.toString(), "Detalle de clase", JOptionPane.INFORMATION_MESSAGE);
        } else if (sel == 1) {
            // Modificar la clase (tipo, cupo y entrenador)
            String nuevoTipo = (String) JOptionPane.showInputDialog(null, "Tipo de clase:", "Modificar clase",
                    JOptionPane.QUESTION_MESSAGE, null, Gimnasio.TIPOS_CLASE, clase.getNombre());
            if (nuevoTipo == null)
                return;
            String cupoTxt = JOptionPane.showInputDialog(null, "Cupo máximo:", String.valueOf(clase.getCupoMaximo()));
            int nuevoCupo = clase.getCupoMaximo();
            try {
                if (cupoTxt != null && !cupoTxt.trim().isEmpty()) {
                    nuevoCupo = Integer.parseInt(cupoTxt);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Cupo inválido. Se mantiene el valor anterior.");
            }
            // Obtener entrenadores disponibles de esa especialidad
            List<Entrenador> disponibles = new ArrayList<>();
            for (Empleado e : gimnasio.getEmpleados()) {
                if (e instanceof Entrenador) {
                    Entrenador ent = (Entrenador) e;
                    if (ent.getEspecialidad() != null && ent.getEspecialidad().equalsIgnoreCase(nuevoTipo)) {
                        disponibles.add(ent);
                    }
                }
            }
            if (disponibles.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay entrenadores disponibles para el tipo " + nuevoTipo);
                return;
            }
            String[] opcionesEnt = new String[disponibles.size()];
            for (int i = 0; i < disponibles.size(); i++) {
                Entrenador ent = disponibles.get(i);
                opcionesEnt[i] = ent.getDni() + " - " + ent.getNombre();
            }
            String elegido = (String) JOptionPane.showInputDialog(null, "Seleccione entrenador:", "Entrenador",
                    JOptionPane.QUESTION_MESSAGE, null, opcionesEnt, opcionesEnt[0]);
            if (elegido == null)
                return;
            Entrenador nuevoEntrenador = null;
            try {
                int dni = Integer.parseInt(elegido.split(" - ")[0]);
                Empleado empSel = gimnasio.buscarEmpleadoPorDni(dni);
                if (empSel instanceof Entrenador) {
                    nuevoEntrenador = (Entrenador) empSel;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Selección de entrenador inválida.");
                return;
            }
            // Reasignar clase a nuevo entrenador
            if (clase.getEntrenador() != null) {
                clase.getEntrenador().getClasesAsignadas().remove(clase);
            }
            clase.setNombre(nuevoTipo);
            clase.setCupoMaximo(nuevoCupo);
            clase.setEntrenador(nuevoEntrenador);
            if (nuevoEntrenador != null) {
                nuevoEntrenador.asignarClase(clase);
            }
            actualizarGrilla();
            gimnasio.registrarModificacionClase(clase);
            JOptionPane.showMessageDialog(null, "Clase modificada.");
        } else if (sel == 2) {
            // Eliminar la clase
            int confirm = JOptionPane.showConfirmDialog(null, "¿Confirma eliminar la clase " + clase.getNombre() + "?",
                    "Eliminar clase", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                gimnasio.eliminarClase(clase);
                actualizarGrilla();
                JOptionPane.showMessageDialog(null, "Clase eliminada.");
            }
        }
        // sel == 3 (Cancelar) no hace nada
    }

    /**
     * Actualiza los textos de la grilla de clases en la interfaz según las clases actuales del gimnasio.
     */
    private void actualizarGrilla() {
        for (int t = 0; t < Gimnasio.TURNOS.length; t++) {
            for (int d = 0; d < Gimnasio.DIAS_SEMANA.length; d++) {
                JButton btn = gridButtons[t][d];
                if (btn == null)
                    continue;
                String dia = Gimnasio.DIAS_SEMANA[d];
                String turno = Gimnasio.TURNOS[t];
                Clase clase = gimnasio.getClaseEnHorario(dia, turno);
                if (clase != null) {
                    // Mostrar nombre de la clase en el botón
                    btn.setText(clase.getNombre());
                    btn.setBackground(Color.CYAN);
                } else {
                    btn.setText("");
                    btn.setBackground(null);
                }
            }
        }
    }

    // ==== Menús de opciones (Socios, Empleados, Clases, Cuenta, etc.) ====

    private void menuSocios() {
        boolean volver = false;
        while (!volver) {
            String opcion = JOptionPane.showInputDialog(null,
                    "Socios - seleccione:\n1 - Listar socios\n2 - Agregar socio\n3 - Eliminar socio (por DNI)\n4 - Modificar socio (por DNI)\n0 - Volver",
                    "Menú Socios",
                    JOptionPane.QUESTION_MESSAGE);
            if (opcion == null)
                return;
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
            if (opcion == null)
                return;
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
            if (opcion == null)
                return;
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

    private void menuCuentaBancaria() {
        boolean volver = false;
        while (!volver) {
            String opcion = JOptionPane.showInputDialog(null,
                    "Cuenta bancaria - seleccione:\n1 - Registrar pago de socio\n2 - Pagar sueldo a empleado\n3 - Consultar saldo del gimnasio\n4 - Ver movimientos de cuenta\n0 - Volver",
                    "Menú Cuenta Bancaria",
                    JOptionPane.QUESTION_MESSAGE);
            if (opcion == null)
                return;
            switch (opcion) {
                case "1":
                    gestionarPagoSocio();
                    break;
                case "2":
                    pagarSueldoEmpleadoDesdeMenu();
                    break;
                case "3":
                    if (gimnasio.getCuenta() != null) {
                        JOptionPane.showMessageDialog(null, "Saldo actual: $" + gimnasio.getCuenta().getSaldo());
                    } else {
                        JOptionPane.showMessageDialog(null, "No hay cuenta bancaria asignada al gimnasio.");
                    }
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

    // === Funciones de lista y gestión para cada menú ===

    private void listarSocios() {
        if (gimnasio.getSocios().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay socios cargados en el sistema.");
            return;
        }
        StringBuilder detalle = new StringBuilder("Socios inscriptos:\n");
        for (Socio s : gimnasio.getSocios()) {
            boolean activo = s.isActivo(); // actualiza el estado activo
            detalle.append("DNI: ").append(s.getDni()).append(" - ")
                    .append(s.getNombre()).append(" ").append(s.getApellido())
                    .append(" - Activo: ").append(activo ? "Sí" : "No")
                    .append(" - Plan: ")
                    .append(s.getPlan() != null && !s.getPlan().isEmpty() ? s.getPlan() : "N/A")
                    .append(" - Vence: ").append(s.getFechaVencimientoFormateada())
                    .append("\n");
        }
        JOptionPane.showMessageDialog(null, detalle.toString());
    }

    private void listarEmpleados() {
        if (gimnasio.getEmpleados().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay empleados cargados en el sistema.");
            return;
        }
        StringBuilder detalle = new StringBuilder("Empleados registrados:\n");
        for (Empleado e : gimnasio.getEmpleados()) {
            detalle.append(e instanceof Entrenador ? "[Entrenador] " : "[Personal] ")
                    .append(e.getNombre()).append(" ").append(e.getApellido())
                    .append(" - DNI: ").append(e.getDni())
                    .append(" - Sueldo: $").append(e.getSueldo())
                    .append(e instanceof Entrenador ? " - Esp: " + ((Entrenador) e).getEspecialidad() : "")
                    .append(e instanceof Limpieza ? " - Sector: " + ((Limpieza) e).getSector() : "")
                    .append("\n");
        }
        JOptionPane.showMessageDialog(null, detalle.toString());
    }

    private void listarClases() {
        if (gimnasio.getClases().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay clases cargadas.");
            return;
        }
        StringBuilder detalle = new StringBuilder("Clases:\n");
        for (Clase c : gimnasio.getClases()) {
            detalle.append("- ").append(c.toString()).append("\n");
        }
        JOptionPane.showMessageDialog(null, detalle.toString());
    }

    private void agregarClaseDesdeMenu() {
        try {
            // Selección del tipo de clase
            String tipo = (String) JOptionPane.showInputDialog(
                    null,
                    "Seleccione el tipo de clase:",
                    "Nueva clase",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    Gimnasio.TIPOS_CLASE,
                    Gimnasio.TIPOS_CLASE[0]);
            if (tipo == null)
                return;

            // Selección del día
            String[] diasSinDomingo = java.util.Arrays.stream(Gimnasio.DIAS_SEMANA)
                    .filter(d -> !d.equalsIgnoreCase("Domingo"))
                    .toArray(String[]::new);

            String dia = (String) JOptionPane.showInputDialog(
                    null,
                    "Seleccione el día:",
                    "Nuevo horario",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    diasSinDomingo,
                    diasSinDomingo[0]);
            if (dia == null)
                return;

            // Selección del turno
            String turno = (String) JOptionPane.showInputDialog(
                    null,
                    "Seleccione el turno:",
                    "Nuevo horario",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    Gimnasio.TURNOS,
                    Gimnasio.TURNOS[0]);
            if (turno == null)
                return;

            // Validar si ya existe clase en ese horario
            if (gimnasio.getClaseEnHorario(dia, turno) != null) {
                JOptionPane.showMessageDialog(null, "Ya existe una clase asignada en ese horario.");
                return;
            }

            // Selección cupo
            String cupoTxt = JOptionPane.showInputDialog(null, "Cupo máximo de alumnos:");
            if (cupoTxt == null)
                return;
            int cupo = Integer.parseInt(cupoTxt);
            if (cupo <= 0) {
                JOptionPane.showMessageDialog(null, "El cupo debe ser mayor a 0.");
                return;
            }

            // Buscar entrenadores de esa especialidad
            List<Entrenador> disponibles = new ArrayList<>();
            for (Empleado e : gimnasio.getEmpleados()) {
                if (e instanceof Entrenador) {
                    Entrenador ent = (Entrenador) e;
                    if (ent.getEspecialidad() != null && ent.getEspecialidad().equalsIgnoreCase(tipo)) {
                        disponibles.add(ent);
                    }
                }
            }

            if (disponibles.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No hay entrenadores disponibles para la especialidad: " + tipo);
                return;
            }

            // Elegir entrenador
            String[] opcionesEnt = new String[disponibles.size()];
            for (int i = 0; i < disponibles.size(); i++) {
                Entrenador ent = disponibles.get(i);
                opcionesEnt[i] = ent.getDni() + " - " + ent.getNombre();
            }

            String seleccionado = (String) JOptionPane.showInputDialog(
                    null,
                    "Seleccione el entrenador:",
                    "Entrenador",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcionesEnt,
                    opcionesEnt[0]);
            if (seleccionado == null)
                return;

            // Obtener DNI seleccionado
            int dniEntrenador = Integer.parseInt(seleccionado.split(" - ")[0]);
            Entrenador entrenador = (Entrenador) gimnasio.buscarEmpleadoPorDni(dniEntrenador);

            // Crear clase nueva
            Clase nueva = new Clase(tipo, dia, turno, cupo, entrenador);

            // Registrar clase
            gimnasio.agregarClase(nueva);
            entrenador.asignarClase(nueva);

            // Actualizar grilla
            actualizarGrilla();

            JOptionPane.showMessageDialog(null, "Clase agregada correctamente.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Valor numérico inválido.");
        }
    }

    private void agregarSocioDesdeMenu() {
        try {
            String nombre = JOptionPane.showInputDialog(null, "Nombre del socio:");
            if (nombre == null)
                return;
            if (nombre.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "El nombre no puede estar vacío.");
                return;
            }
            String apellido = JOptionPane.showInputDialog(null, "Apellido del socio:");
            if (apellido == null)
                return;
            if (apellido.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "El apellido no puede estar vacío.");
                return;
            }
            String dniTxt = JOptionPane.showInputDialog(null, "DNI:");
            if (dniTxt == null)
                return;
            int dni = Integer.parseInt(dniTxt);
            if (gimnasio.buscarSocioPorDni(dni) != null) {
                JOptionPane.showMessageDialog(null, "Ya existe un socio con ese DNI.");
                return;
            }
            String membresia = JOptionPane.showInputDialog(null, "Tipo de membresía (ej: Premium):");
            if (membresia == null || membresia.trim().isEmpty()) {
                membresia = "Estándar";
            }
            String nroCuenta = JOptionPane.showInputDialog(null, "Nro de cuenta bancaria del socio (opcional):");
            if (nroCuenta == null || nroCuenta.trim().isEmpty()) {
                nroCuenta = "000" + dni;
            }
            // Seleccionar duración de plan
            String[] opcionesPlan = { "1", "3", "6", "12" };
            String elegidoPlan = (String) JOptionPane.showInputDialog(null, "Seleccione duración del plan (meses):",
                    "Plan", JOptionPane.QUESTION_MESSAGE, null, opcionesPlan, opcionesPlan[0]);
            int mesesPlan = 1;
            if (elegidoPlan != null) {
                try {
                    mesesPlan = Integer.parseInt(elegidoPlan);
                } catch (NumberFormatException ex) {
                    mesesPlan = 1;
                }
            }
            CuentaBancaria cuentaSocio = new CuentaBancaria(nroCuenta, 0, nombre + " " + apellido);
            Socio socio = new Socio(nombre, apellido, dni, membresia, null, cuentaSocio, new java.util.Date(),
                    new java.util.Date(), true, mesesPlan + " meses", mesesPlan);
            gimnasio.agregarSocio(socio);
            JOptionPane.showMessageDialog(null, "Socio agregado: " + socio.getNombre() + " " + socio.getApellido());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Datos numéricos inválidos.");
        }
    }

    private void eliminarSocioPorDni() {
        try {
            String dniTxt = JOptionPane.showInputDialog(null, "Ingrese DNI del socio a eliminar:");
            if (dniTxt == null)
                return;
            int dni = Integer.parseInt(dniTxt);
            Socio s = gimnasio.buscarSocioPorDni(dni);
            if (s == null) {
                JOptionPane.showMessageDialog(null, "No se encontró socio con DNI " + dni);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(null,
                    "¿Confirma eliminar a " + s.getNombre() + " " + s.getApellido() + "?", "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                gimnasio.eliminarSocio(s);
                JOptionPane.showMessageDialog(null, "Socio eliminado.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "DNI inválido.");
        }
    }

    private void modificarSocioPorDni() {
        try {
            String dniTxt = JOptionPane.showInputDialog(null, "Ingrese DNI del socio a modificar:");
            if (dniTxt == null)
                return;
            int dni = Integer.parseInt(dniTxt);
            Socio s = gimnasio.buscarSocioPorDni(dni);
            if (s == null) {
                JOptionPane.showMessageDialog(null, "No se encontró socio con DNI " + dni);
                return;
            }
            String nombre = JOptionPane.showInputDialog(null, "Nombre:", s.getNombre());
            if (nombre != null && !nombre.trim().isEmpty()) {
                s.setNombre(nombre);
            }
            String apellido = JOptionPane.showInputDialog(null, "Apellido:", s.getApellido());
            if (apellido != null && !apellido.trim().isEmpty()) {
                s.setApellido(apellido);
            }
            String nroCuenta = JOptionPane.showInputDialog(null, "Nro de cuenta bancaria:",
                    (s.getCuenta() != null ? s.getCuenta().getNroCuenta() : ""));
            if (nroCuenta != null && !nroCuenta.trim().isEmpty()) {
                if (s.getCuenta() == null) {
                    s.setCuenta(new CuentaBancaria(nroCuenta, 0, s.getNombre() + " " + s.getApellido()));
                } else {
                    s.getCuenta().setNroCuenta(nroCuenta);
                }
            }
            String[] opcionesPlan = { "1", "3", "6", "12" };
            String elegidoPlan = (String) JOptionPane.showInputDialog(null, "Seleccione duración del plan (meses):",
                    "Plan", JOptionPane.QUESTION_MESSAGE, null, opcionesPlan,
                    String.valueOf(s.getPlanMeses() > 0 ? s.getPlanMeses() : "1"));
            if (elegidoPlan != null) {
                try {
                    int meses = Integer.parseInt(elegidoPlan);
                    s.setPlanMeses(meses);
                    s.setPlan(meses + " meses");
                } catch (NumberFormatException ex) {
                    // Si no se puede parsear, no modificar planMeses
                }
            }
            JOptionPane.showMessageDialog(null, "Socio actualizado.");
            gimnasio.registrarModificacionSocio(s);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "DNI inválido.");
        }
    }

    private void agregarEmpleadoDesdeMenu() {
        try {
            String[] tiposEmpleado = { "Entrenador", "Limpieza" };
            String tipo = (String) JOptionPane.showInputDialog(null, "Tipo de empleado:", "Tipo",
                    JOptionPane.QUESTION_MESSAGE, null, tiposEmpleado, tiposEmpleado[0]);
            if (tipo == null)
                return;
            String nombre = JOptionPane.showInputDialog(null, "Nombre:");
            if (nombre == null)
                return;
            if (nombre.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "El nombre no puede estar vacío.");
                return;
            }
            String apellido = JOptionPane.showInputDialog(null, "Apellido:");
            if (apellido == null)
                return;
            if (apellido.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "El apellido no puede estar vacío.");
                return;
            }
            String dniTxt = JOptionPane.showInputDialog(null, "DNI:");
            if (dniTxt == null)
                return;
            int dni = Integer.parseInt(dniTxt);
            if (gimnasio.buscarEmpleadoPorDni(dni) != null) {
                JOptionPane.showMessageDialog(null, "Ya existe un empleado con ese DNI.");
                return;
            }
            String sexo = JOptionPane.showInputDialog(null, "Sexo (M/F):");
            if (sexo == null || sexo.trim().isEmpty()) {
                sexo = "N"; // N = No especificado
            }
            String sueldoTxt = JOptionPane.showInputDialog(null, "Sueldo:");
            double sueldo = 0;
            if (sueldoTxt != null && !sueldoTxt.trim().isEmpty()) {
                sueldo = Double.parseDouble(sueldoTxt);
            }
            if (tipo.equalsIgnoreCase("Entrenador")) {
                String especialidad = (String) JOptionPane.showInputDialog(null, "Especialidad:", "Especialidad",
                        JOptionPane.QUESTION_MESSAGE, null, Gimnasio.TIPOS_CLASE, Gimnasio.TIPOS_CLASE[0]);
                String fechaTxt = JOptionPane.showInputDialog(null, "Fecha de nacimiento (dd/MM/yyyy) (opcional):");
                java.util.Date fechaNac = new java.util.Date();
                if (fechaTxt != null && !fechaTxt.trim().isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        fechaNac = sdf.parse(fechaTxt);
                    } catch (Exception ex) {
                        fechaNac = new java.util.Date();
                    }
                }
                Entrenador entrenador = new Entrenador(nombre, apellido, dni, sexo, fechaNac, sueldo, especialidad,
                        null);
                gimnasio.agregarEmpleado(entrenador);
                JOptionPane.showMessageDialog(null, "Entrenador agregado: " + entrenador.getNombre());
            } else {
                String fechaTxt = JOptionPane.showInputDialog(null, "Fecha de nacimiento (dd/MM/yyyy) (opcional):");
                java.util.Date fechaNac = new java.util.Date();
                if (fechaTxt != null && !fechaTxt.trim().isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        fechaNac = sdf.parse(fechaTxt);
                    } catch (Exception ex) {
                        fechaNac = new java.util.Date();
                    }
                }
                String horario = JOptionPane.showInputDialog(null, "Horario de trabajo (ej. 08:00-12:00):");
                if (horario == null)
                    return;
                String sector = JOptionPane.showInputDialog(null, "Sector:");
                if (sector == null)
                    return;
                Limpieza limp = new Limpieza(nombre, apellido, dni, sexo, fechaNac, sueldo, horario, sector);
                gimnasio.agregarEmpleado(limp);
                JOptionPane.showMessageDialog(null, "Personal de limpieza agregado: " + limp.getNombre());
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Datos numéricos inválidos.");
        }
    }

    private void eliminarEmpleadoPorDni() {
        try {
            String dniTxt = JOptionPane.showInputDialog(null, "Ingrese DNI del empleado a eliminar:");
            if (dniTxt == null)
                return;
            int dni = Integer.parseInt(dniTxt);
            Empleado e = gimnasio.buscarEmpleadoPorDni(dni);
            if (e == null) {
                JOptionPane.showMessageDialog(null, "No se encontró empleado con DNI " + dni);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(null,
                    "¿Confirma eliminar a " + e.getNombre() + " " + e.getApellido() + "?", "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                gimnasio.eliminarEmpleado(e);
                JOptionPane.showMessageDialog(null, "Empleado eliminado.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "DNI inválido.");
        }
    }

    private void modificarEmpleadoPorDni() {
        try {
            String dniTxt = JOptionPane.showInputDialog(null, "Ingrese DNI del empleado a modificar:");
            if (dniTxt == null)
                return;
            int dni = Integer.parseInt(dniTxt);
            Empleado e = gimnasio.buscarEmpleadoPorDni(dni);
            if (e == null) {
                JOptionPane.showMessageDialog(null, "No se encontró empleado con DNI " + dni);
                return;
            }
            String nombre = JOptionPane.showInputDialog(null, "Nombre:", e.getNombre());
            if (nombre != null && !nombre.trim().isEmpty()) {
                e.setNombre(nombre);
            }
            String apellido = JOptionPane.showInputDialog(null, "Apellido:", e.getApellido());
            if (apellido != null && !apellido.trim().isEmpty()) {
                e.setApellido(apellido);
            }
            String sexo = JOptionPane.showInputDialog(null, "Sexo (M/F):", e.getSexo());
            if (sexo != null && !sexo.trim().isEmpty()) {
                e.setSexo(sexo);
            }
            String sueldoTxt = JOptionPane.showInputDialog(null, "Sueldo:", String.valueOf(e.getSueldo()));
            if (sueldoTxt != null && !sueldoTxt.trim().isEmpty()) {
                try {
                    e.setSueldo(Double.parseDouble(sueldoTxt));
                } catch (NumberFormatException ex) {
                    // si ingreso inválido, no cambiar sueldo
                }
            }
            if (e instanceof Entrenador) {
                Entrenador ent = (Entrenador) e;
                String especialidad = JOptionPane.showInputDialog(null, "Especialidad:", ent.getEspecialidad());
                if (especialidad != null && !especialidad.trim().isEmpty()) {
                    ent.setEspecialidad(especialidad);
                }
            } else if (e instanceof Limpieza) {
                Limpieza limp = (Limpieza) e;
                String horario = JOptionPane.showInputDialog(null, "Horario de trabajo:", limp.getHorarioTrabajo());
                if (horario != null && !horario.trim().isEmpty()) {
                    limp.setHorarioTrabajo(horario);
                }
                String sector = JOptionPane.showInputDialog(null, "Sector:", limp.getSector());
                if (sector != null && !sector.trim().isEmpty()) {
                    limp.setSector(sector);
                }
            }
            JOptionPane.showMessageDialog(null, "Empleado actualizado.");
            gimnasio.registrarModificacionEmpleado(e);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "DNI inválido.");
        }
    }

    private void eliminarClaseSeleccion() {
        if (gimnasio.getClases().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay clases para eliminar.");
            return;
        }
        List<Clase> listaClases = new ArrayList<>(gimnasio.getClases());
        String[] opciones = new String[listaClases.size()];
        for (int i = 0; i < listaClases.size(); i++) {
            Clase c = listaClases.get(i);
            opciones[i] = c.getNombre() + " - " + c.getHorario();
        }
        String seleccionado = (String) JOptionPane.showInputDialog(null, "Seleccione la clase a eliminar:",
                "Eliminar clase",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (seleccionado == null)
            return;
        // Buscar la clase seleccionada por coincidencia exacta de cadena
        Clase claseAEliminar = null;
        for (int i = 0; i < opciones.length; i++) {
            if (opciones[i].equals(seleccionado)) {
                claseAEliminar = listaClases.get(i);
                break;
            }
        }
        if (claseAEliminar == null)
            return;
        int confirm = JOptionPane.showConfirmDialog(null,
                "¿Confirma eliminar la clase " + claseAEliminar.getNombre() + " (" + claseAEliminar.getHorario() + ")?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            gimnasio.eliminarClase(claseAEliminar);
            actualizarGrilla();
            JOptionPane.showMessageDialog(null, "Clase eliminada.");
        }
    }

    private void gestionarPagoSocio() {
        if (gimnasio.getSocios().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay socios cargados en el sistema.");
            return;
        }
        try {
            String dniTexto = JOptionPane.showInputDialog(null, "Ingrese el DNI del socio:");
            if (dniTexto == null)
                return;
            int dni = Integer.parseInt(dniTexto);
            Socio socio = gimnasio.buscarSocioPorDni(dni);
            if (socio == null) {
                JOptionPane.showMessageDialog(null, "No se encontró un socio con ese DNI.");
                return;
            }
            String[] opcionesPago = {
                    "1 mes - $35000",
                    "3 meses - $100000",
                    "6 meses - $550000",
                    "12 meses - $1100000"
            };
            String elegidoPago = (String) JOptionPane.showInputDialog(null, "Seleccione el plan a comprar:",
                    "Pago de cuota", JOptionPane.QUESTION_MESSAGE, null, opcionesPago, opcionesPago[0]);
            if (elegidoPago == null)
                return;
            int meses = 1;
            double monto = 35000;
            if (elegidoPago.startsWith("3")) {
                meses = 3;
                monto = 100000;
            } else if (elegidoPago.startsWith("6")) {
                meses = 6;
                monto = 550000;
            } else if (elegidoPago.startsWith("12")) {
                meses = 12;
                monto = 1100000;
            }
            socio.setPlanMeses(meses);
            socio.setPlan(meses + " meses");
            boolean pagoOk = gimnasio.registrarPagoSocio(socio, monto);
            if (pagoOk) {
                JOptionPane.showMessageDialog(null, "El pago se registró correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo registrar el pago. Verifique los datos o saldo.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Los datos ingresados no son válidos.");
        }
    }

    private void pagarSueldoEmpleadoDesdeMenu() {
        try {
            String dniTxt = JOptionPane.showInputDialog(null, "Ingrese DNI del empleado a pagar sueldo:");
            if (dniTxt == null)
                return;
            int dni = Integer.parseInt(dniTxt);
            Empleado e = gimnasio.buscarEmpleadoPorDni(dni);
            if (e == null) {
                JOptionPane.showMessageDialog(null, "No se encontró empleado con DNI " + dni);
                return;
            }
            if (gimnasio.getCuenta() == null) {
                JOptionPane.showMessageDialog(null, "No hay cuenta bancaria del gimnasio asignada.");
                return;
            }
            // Usar registrarPagoSueldo para que se guarde en registros.txt
            boolean pagado = gimnasio.registrarPagoSueldo(e);
            if (pagado) {
                JOptionPane.showMessageDialog(null, "Sueldo pagado a " + e.getNombre() + " " + e.getApellido());
            } else {
                JOptionPane.showMessageDialog(null, "No se pudo pagar el sueldo. Fondos insuficientes.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "DNI inválido.");
        }
    }

    private void mostrarMovimientosCuentaDialog() {
        CuentaBancaria cuenta = gimnasio.getCuenta();
        if (cuenta == null) {
            JOptionPane.showMessageDialog(null, "No hay cuenta bancaria asignada.");
            return;
        }
        JDialog dialog = new JDialog(mainFrame,
                "Movimientos de la cuenta - " + (cuenta.getNroCuenta() != null ? cuenta.getNroCuenta() : ""), true);
        dialog.setLayout(new BorderLayout(8, 8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] meses = { "Todos", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto",
                "Septiembre", "Octubre", "Noviembre", "Diciembre" };
        JComboBox<String> cbMes = new JComboBox<>(meses);
        int añoActual = Calendar.getInstance().get(Calendar.YEAR);
        String[] años = { "Todos", String.valueOf(añoActual - 1), String.valueOf(añoActual),
                String.valueOf(añoActual + 1) };
        JComboBox<String> cbAño = new JComboBox<>(años);
        JButton btnFiltrar = new JButton("Filtrar");
        top.add(new JLabel("Mes:"));
        top.add(cbMes);
        top.add(new JLabel("Año:"));
        top.add(cbAño);
        top.add(btnFiltrar);
        dialog.add(top, BorderLayout.NORTH);
        DefaultTableModel modeloTabla = new DefaultTableModel(new Object[] { "Fecha", "Tipo", "Descripción", "Monto" },
                0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        dialog.add(scroll, BorderLayout.CENTER);
        JLabel lblResumen = new JLabel(" ");
        dialog.add(lblResumen, BorderLayout.SOUTH);
        Runnable poblarTabla = () -> {
            modeloTabla.setRowCount(0);
            int mesSel = cbMes.getSelectedIndex(); // 0 = Todos
            String añoStr = (String) cbAño.getSelectedItem();
            int añoSel = 0;
            if (añoStr != null && !"Todos".equalsIgnoreCase(añoStr)) {
                try {
                    añoSel = Integer.parseInt(añoStr);
                } catch (NumberFormatException ex) {
                    añoSel = 0;
                }
            }
            List<Registro> lista = new ArrayList<>();
            if (mesSel == 0 && añoSel == 0) {
                lista.addAll(cuenta.getMovimientos());
            } else if (mesSel == 0) {
                lista.addAll(cuenta.getMovimientosPorMes(0, añoSel));
            } else if (añoSel == 0) {
                for (Registro r : cuenta.getMovimientos()) {
                    Calendar cal = Calendar.getInstance();
                    if (r.getFecha() == null)
                        continue;
                    cal.setTime(r.getFecha());
                    int m = cal.get(Calendar.MONTH) + 1;
                    if (m == mesSel)
                        lista.add(r);
                }
            } else {
                lista.addAll(cuenta.getMovimientosPorMes(mesSel, añoSel));
            }
            double totalHaber = 0.0, totalDebe = 0.0;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            for (Registro r : lista) {
                String fecha = r.getFecha() != null ? sdf.format(r.getFecha()) : "";
                modeloTabla.addRow(new Object[] { fecha, r.getTipo(), r.getDescripcion(), r.getMonto() });
                if ("HABER".equalsIgnoreCase(r.getTipo())) {
                    totalHaber += r.getMonto();
                } else if ("DEBE".equalsIgnoreCase(r.getTipo())) {
                    totalDebe += r.getMonto();
                }
            }
            double neto = totalHaber - totalDebe;
            lblResumen.setText(
                    String.format("Total HABER: $%.2f   Total DEBE: $%.2f   Neto: $%.2f", totalHaber, totalDebe, neto));
        };
        btnFiltrar.addActionListener(e -> poblarTabla.run());
        poblarTabla.run();
        dialog.setSize(800, 400);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }
}
