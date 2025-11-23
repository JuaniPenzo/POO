import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Clase que maneja la interfaz de usuario del gimnasio, utilizando Swing y
 * JOptionPane para interacción.
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
     * Muestra la interfaz gráfica principal (grilla semanal de clases y panel de
     * control).
     */
    public void mostrarInterfazPrincipal() {
        // Crear la interfaz gráfica en el hilo de eventos de Swing
        SwingUtilities.invokeLater(this::crearYMostrarGUI);
    }

    /**
     * Crea y muestra la ventana principal con la grilla de clases y botones de
     * menú.
     */
    private void crearYMostrarGUI() {
        if (mainFrame != null) {
            mainFrame.toFront();
            return;
        }

        // Aumentar tamaño de fuente global
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, new javax.swing.plaf.FontUIResource("Arial", Font.PLAIN, 16));
            }
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
                btn.setVerticalTextPosition(SwingConstants.CENTER);
                btn.setHorizontalTextPosition(SwingConstants.CENTER);
                btn.setPreferredSize(new Dimension(200, 100)); // Aumentado de 140x80
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
     * Muestra detalles de la clase en ese horario o indica si está libre,
     * permitiendo gestionar la clase.
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
        } else if (sel == 1) {
            // Modificar la clase (tipo, cupo y entrenador)
            JComboBox<String> cmbTipo = new JComboBox<>(Gimnasio.TIPOS_CLASE);
            cmbTipo.setSelectedItem(clase.getNombre());
            JTextField txtCupo = new JTextField(String.valueOf(clase.getCupoMaximo()));
            JComboBox<String> cmbEntrenador = new JComboBox<>();

            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            panel.add(new JLabel("Tipo de Clase:"));
            panel.add(cmbTipo);
            panel.add(new JLabel("Cupo Máximo:"));
            panel.add(txtCupo);
            panel.add(new JLabel("Entrenador:"));
            panel.add(cmbEntrenador);

            // Actualizar entrenadores
            java.awt.event.ActionListener updateTrainers = e -> {
                cmbEntrenador.removeAllItems();
                String tipo = (String) cmbTipo.getSelectedItem();
                for (Empleado emp : gimnasio.getEmpleados()) {
                    if (emp instanceof Entrenador) {
                        Entrenador ent = (Entrenador) emp;
                        if (ent.getEspecialidad() != null && ent.getEspecialidad().equalsIgnoreCase(tipo)) {
                            cmbEntrenador.addItem(ent.getDni() + " - " + ent.getNombre());
                        }
                    }
                }
                // Intentar seleccionar el actual si coincide
                if (clase.getEntrenador() != null) {
                    String actual = clase.getEntrenador().getDni() + " - " + clase.getEntrenador().getNombre();
                    for (int i = 0; i < cmbEntrenador.getItemCount(); i++) {
                        if (cmbEntrenador.getItemAt(i).equals(actual)) {
                            cmbEntrenador.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            };
            cmbTipo.addActionListener(updateTrainers);
            updateTrainers.actionPerformed(null);

            while (true) {
                int result = JOptionPane.showConfirmDialog(null, panel, "Modificar Clase",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    try {
                        String nuevoTipo = (String) cmbTipo.getSelectedItem();
                        String cupoTxt = txtCupo.getText().trim();
                        String entSel = (String) cmbEntrenador.getSelectedItem();

                        if (cupoTxt.isEmpty())
                            throw new Exception("El cupo es obligatorio.");
                        int nuevoCupo = Integer.parseInt(cupoTxt);
                        if (nuevoCupo <= 0)
                            throw new Exception("El cupo debe ser mayor a 0.");

                        if (entSel == null)
                            throw new Exception("Debe seleccionar un entrenador.");

                        int dniEnt = Integer.parseInt(entSel.split(" - ")[0]);
                        Entrenador nuevoEntrenador = (Entrenador) gimnasio.buscarEmpleadoPorDni(dniEnt);

                        // Reasignar
                        if (clase.getEntrenador() != null) {
                            clase.getEntrenador().getClasesAsignadas().remove(clase);
                        }
                        clase.setNombre(nuevoTipo);
                        clase.setCupoMaximo(nuevoCupo);
                        clase.setEntrenador(nuevoEntrenador);
                        nuevoEntrenador.asignarClase(clase);

                        actualizarGrilla();
                        gimnasio.registrarModificacionClase(clase);
                        JOptionPane.showMessageDialog(null, "Clase modificada exitosamente.");
                        break;
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "El cupo debe ser numérico.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    }
                } else {
                    break;
                }
            }
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
     * Actualiza los textos de la grilla de clases en la interfaz según las clases
     * actuales del gimnasio.
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
        String[] opciones = { "Listar socios", "Agregar socio", "Eliminar socio", "Modificar socio", "Volver" };
        while (true) {
            int seleccion = JOptionPane.showOptionDialog(
                    null,
                    "Seleccione una opción:",
                    "Menú Socios",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]);

            if (seleccion == JOptionPane.CLOSED_OPTION || seleccion == 4) {
                break;
            }

            switch (seleccion) {
                case 0: // Listar
                    listarSocios();
                    break;
                case 1: // Agregar
                    agregarSocioDesdeMenu();
                    break;
                case 2: // Eliminar
                    eliminarSocioPorDni();
                    break;
                case 3: // Modificar
                    modificarSocioPorDni();
                    break;
            }
        }
    }

    private void menuEmpleados() {
        String[] opciones = { "Listar empleados", "Agregar empleado", "Eliminar empleado", "Modificar empleado",
                "Volver" };
        while (true) {
            int seleccion = JOptionPane.showOptionDialog(
                    null,
                    "Seleccione una opción:",
                    "Menú Empleados",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]);

            if (seleccion == JOptionPane.CLOSED_OPTION || seleccion == 4) {
                break;
            }

            switch (seleccion) {
                case 0: // Listar
                    listarEmpleados();
                    break;
                case 1: // Agregar
                    agregarEmpleadoDesdeMenu();
                    break;
                case 2: // Eliminar
                    eliminarEmpleadoPorDni();
                    break;
                case 3: // Modificar
                    modificarEmpleadoPorDni();
                    break;
            }
        }
    }

    private void menuClases() {
        String[] opciones = { "Listar clases", "Agregar clase", "Eliminar clase", "Volver" };
        while (true) {
            int seleccion = JOptionPane.showOptionDialog(
                    null,
                    "Seleccione una opción:",
                    "Menú Clases",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]);

            if (seleccion == JOptionPane.CLOSED_OPTION || seleccion == 3) {
                break;
            }

            switch (seleccion) {
                case 0: // Listar
                    listarClases();
                    break;
                case 1: // Agregar
                    agregarClaseDesdeMenu();
                    break;
                case 2: // Eliminar
                    eliminarClaseSeleccion();
                    break;
            }
        }
    }

    private void menuCuentaBancaria() {
        String[] opciones = { "Registrar pago socio", "Pagar sueldo", "Consultar saldo", "Ver movimientos", "Volver" };
        while (true) {
            int seleccion = JOptionPane.showOptionDialog(
                    null,
                    "Seleccione una opción:",
                    "Menú Cuenta Bancaria",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opciones,
                    opciones[0]);

            if (seleccion == JOptionPane.CLOSED_OPTION || seleccion == 4) {
                break;
            }

            switch (seleccion) {
                case 0: // Registrar pago socio
                    gestionarPagoSocio();
                    break;
                case 1: // Pagar sueldo
                    pagarSueldoEmpleadoDesdeMenu();
                    break;
                case 2: // Consultar saldo
                    if (gimnasio.getCuenta() != null) {
                        JOptionPane.showMessageDialog(null, "Saldo actual: $" + gimnasio.getCuenta().getSaldo());
                    } else {
                        JOptionPane.showMessageDialog(null, "No hay cuenta bancaria asignada al gimnasio.");
                    }
                    break;
                case 3: // Ver movimientos
                    mostrarMovimientosCuentaDialog();
                    break;
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

    private void agregarSocioDesdeMenu() {
        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtDni = new JTextField();
        String[] membresias = { "Estándar", "Premium", "Platinum", "Básico" };
        JComboBox<String> cmbMembresia = new JComboBox<>(membresias);
        JTextField txtCuenta = new JTextField();
        String[] planes = { "1 mes", "3 meses", "6 meses", "12 meses" };
        JComboBox<String> cmbPlan = new JComboBox<>(planes);

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Apellido:"));
        panel.add(txtApellido);
        panel.add(new JLabel("DNI:"));
        panel.add(txtDni);
        panel.add(new JLabel("Membresía:"));
        panel.add(cmbMembresia);
        panel.add(new JLabel("Nro Cuenta (Opcional):"));
        panel.add(txtCuenta);
        panel.add(new JLabel("Duración Plan:"));
        panel.add(cmbPlan);

        while (true) {
            int result = JOptionPane.showConfirmDialog(null, panel, "Agregar Socio",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String nombre = txtNombre.getText().trim();
                    String apellido = txtApellido.getText().trim();
                    String dniStr = txtDni.getText().trim();

                    if (nombre.isEmpty() || apellido.isEmpty() || dniStr.isEmpty()) {
                        throw new Exception("Todos los campos obligatorios deben completarse.");
                    }

                    int dni = Integer.parseInt(dniStr);
                    if (gimnasio.buscarSocioPorDni(dni) != null) {
                        throw new Exception("Ya existe un socio con ese DNI.");
                    }

                    String membresia = (String) cmbMembresia.getSelectedItem();
                    String planStr = (String) cmbPlan.getSelectedItem();
                    int meses = Integer.parseInt(planStr.split(" ")[0]);

                    String nroCuenta = txtCuenta.getText().trim();
                    if (nroCuenta.isEmpty())
                        nroCuenta = "000" + dni;

                    CuentaBancaria cuentaSocio = new CuentaBancaria(nroCuenta, 0, nombre + " " + apellido);
                    Socio socio = new Socio(nombre, apellido, dni, membresia, null, cuentaSocio, new java.util.Date(),
                            new java.util.Date(), true, planStr, meses);

                    gimnasio.agregarSocio(socio);
                    JOptionPane.showMessageDialog(null, "Socio agregado exitosamente.");
                    break;

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Error: El DNI debe ser numérico.");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                }
            } else {
                break;
            }
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

            JTextField txtNombre = new JTextField(s.getNombre());
            JTextField txtApellido = new JTextField(s.getApellido());
            String[] membresias = { "Estándar", "Premium", "Platinum", "Básico" };
            JComboBox<String> cmbMembresia = new JComboBox<>(membresias);
            cmbMembresia.setSelectedItem(s.getMembresia());

            JTextField txtCuenta = new JTextField(s.getCuenta() != null ? s.getCuenta().getNroCuenta() : "");

            String[] planes = { "1 mes", "3 meses", "6 meses", "12 meses" };
            JComboBox<String> cmbPlan = new JComboBox<>(planes);
            // Intentar seleccionar el plan actual
            for (String p : planes) {
                if (p.startsWith(String.valueOf(s.getPlanMeses()))) {
                    cmbPlan.setSelectedItem(p);
                    break;
                }
            }

            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            panel.add(new JLabel("Nombre:"));
            panel.add(txtNombre);
            panel.add(new JLabel("Apellido:"));
            panel.add(txtApellido);
            panel.add(new JLabel("Membresía:"));
            panel.add(cmbMembresia);
            panel.add(new JLabel("Nro Cuenta:"));
            panel.add(txtCuenta);
            panel.add(new JLabel("Duración Plan:"));
            panel.add(cmbPlan);

            while (true) {
                int result = JOptionPane.showConfirmDialog(null, panel, "Modificar Socio",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        String nombre = txtNombre.getText().trim();
                        String apellido = txtApellido.getText().trim();
                        if (nombre.isEmpty() || apellido.isEmpty()) {
                            throw new Exception("Nombre y Apellido son obligatorios.");
                        }

                        s.setNombre(nombre);
                        s.setApellido(apellido);
                        s.setMembresia((String) cmbMembresia.getSelectedItem());

                        String planStr = (String) cmbPlan.getSelectedItem();
                        int meses = Integer.parseInt(planStr.split(" ")[0]);
                        s.setPlanMeses(meses);
                        s.setPlan(planStr);

                        String nroCuenta = txtCuenta.getText().trim();
                        if (!nroCuenta.isEmpty()) {
                            if (s.getCuenta() == null) {
                                s.setCuenta(new CuentaBancaria(nroCuenta, 0, nombre + " " + apellido));
                            } else {
                                s.getCuenta().setNroCuenta(nroCuenta);
                            }
                        }

                        gimnasio.registrarModificacionSocio(s);
                        JOptionPane.showMessageDialog(null, "Socio actualizado exitosamente.");
                        break;
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                    }
                } else {
                    break;
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "DNI inválido.");
        }
    }

    private void agregarEmpleadoDesdeMenu() {
        String[] tipos = { "Entrenador", "Limpieza" };
        JComboBox<String> cmbTipo = new JComboBox<>(tipos);
        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtDni = new JTextField();
        String[] sexos = { "M", "F", "X" };
        JComboBox<String> cmbSexo = new JComboBox<>(sexos);
        JTextField txtSueldo = new JTextField();

        // Campos específicos
        JComboBox<String> cmbEspecialidad = new JComboBox<>(Gimnasio.TIPOS_CLASE);
        JTextField txtHorario = new JTextField();
        JTextField txtSector = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Tipo de Empleado:"));
        panel.add(cmbTipo);
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Apellido:"));
        panel.add(txtApellido);
        panel.add(new JLabel("DNI:"));
        panel.add(txtDni);
        panel.add(new JLabel("Sexo:"));
        panel.add(cmbSexo);
        panel.add(new JLabel("Sueldo:"));
        panel.add(txtSueldo);

        panel.add(new JLabel("--- Entrenador ---"));
        panel.add(new JLabel(""));
        panel.add(new JLabel("Especialidad:"));
        panel.add(cmbEspecialidad);

        panel.add(new JLabel("--- Limpieza ---"));
        panel.add(new JLabel(""));
        panel.add(new JLabel("Horario:"));
        panel.add(txtHorario);
        panel.add(new JLabel("Sector:"));
        panel.add(txtSector);

        // Listener para habilitar/deshabilitar según tipo
        java.awt.event.ActionListener updateFields = e -> {
            boolean isEntrenador = "Entrenador".equals(cmbTipo.getSelectedItem());
            cmbEspecialidad.setEnabled(isEntrenador);
            txtHorario.setEnabled(!isEntrenador);
            txtSector.setEnabled(!isEntrenador);
        };
        cmbTipo.addActionListener(updateFields);
        updateFields.actionPerformed(null); // Estado inicial

        while (true) {
            int result = JOptionPane.showConfirmDialog(null, panel, "Agregar Empleado",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String nombre = txtNombre.getText().trim();
                    String apellido = txtApellido.getText().trim();
                    String dniStr = txtDni.getText().trim();
                    String sueldoStr = txtSueldo.getText().trim();

                    if (nombre.isEmpty() || apellido.isEmpty() || dniStr.isEmpty() || sueldoStr.isEmpty()) {
                        throw new Exception("Complete los campos obligatorios.");
                    }

                    int dni = Integer.parseInt(dniStr);
                    if (gimnasio.buscarEmpleadoPorDni(dni) != null) {
                        throw new Exception("Ya existe un empleado con ese DNI.");
                    }

                    double sueldo = Double.parseDouble(sueldoStr);
                    String sexo = (String) cmbSexo.getSelectedItem();
                    String tipo = (String) cmbTipo.getSelectedItem();

                    if ("Entrenador".equals(tipo)) {
                        String especialidad = (String) cmbEspecialidad.getSelectedItem();
                        Entrenador ent = new Entrenador(nombre, apellido, dni, sexo, new java.util.Date(), sueldo,
                                especialidad, null);
                        gimnasio.agregarEmpleado(ent);
                    } else {
                        String horario = txtHorario.getText().trim();
                        String sector = txtSector.getText().trim();
                        if (horario.isEmpty() || sector.isEmpty()) {
                            throw new Exception("Para Limpieza, Horario y Sector son obligatorios.");
                        }
                        Limpieza limp = new Limpieza(nombre, apellido, dni, sexo, new java.util.Date(), sueldo, horario,
                                sector);
                        gimnasio.agregarEmpleado(limp);
                    }

                    JOptionPane.showMessageDialog(null, "Empleado agregado exitosamente.");
                    break;

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Error: DNI y Sueldo deben ser numéricos.");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                }
            } else {
                break;
            }
        }
    }

    private void eliminarEmpleadoPorDni() {
        if (gimnasio.getEmpleados().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay empleados para eliminar.");
            return;
        }

        java.util.List<Empleado> empleados = gimnasio.getEmpleados();
        String[] opciones = new String[empleados.size()];
        for (int i = 0; i < empleados.size(); i++) {
            Empleado e = empleados.get(i);
            opciones[i] = e.getDni() + " - " + e.getNombre() + " " + e.getApellido();
        }

        String seleccionado = (String) JOptionPane.showInputDialog(null,
                "Seleccione el empleado a eliminar:", "Eliminar Empleado",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        if (seleccionado == null)
            return;

        try {
            int dni = Integer.parseInt(seleccionado.split(" - ")[0]);
            Empleado e = gimnasio.buscarEmpleadoPorDni(dni);

            if (e != null) {
                int confirm = JOptionPane.showConfirmDialog(null,
                        "¿Confirma eliminar a " + e.getNombre() + " " + e.getApellido() + "?", "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    gimnasio.eliminarEmpleado(e);
                    JOptionPane.showMessageDialog(null, "Empleado eliminado.");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al procesar la selección.");
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

            JTextField txtNombre = new JTextField(e.getNombre());
            JTextField txtApellido = new JTextField(e.getApellido());
            String[] sexos = { "M", "F", "X" };
            JComboBox<String> cmbSexo = new JComboBox<>(sexos);
            cmbSexo.setSelectedItem(e.getSexo());
            JTextField txtSueldo = new JTextField(String.valueOf(e.getSueldo()));

            // Campos específicos
            JComboBox<String> cmbEspecialidad = new JComboBox<>(Gimnasio.TIPOS_CLASE);
            JTextField txtHorario = new JTextField();
            JTextField txtSector = new JTextField();

            JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
            panel.add(new JLabel("Nombre:"));
            panel.add(txtNombre);
            panel.add(new JLabel("Apellido:"));
            panel.add(txtApellido);
            panel.add(new JLabel("Sexo:"));
            panel.add(cmbSexo);
            panel.add(new JLabel("Sueldo:"));
            panel.add(txtSueldo);

            if (e instanceof Entrenador) {
                Entrenador ent = (Entrenador) e;
                cmbEspecialidad.setSelectedItem(ent.getEspecialidad());
                panel.add(new JLabel("Especialidad:"));
                panel.add(cmbEspecialidad);
            } else if (e instanceof Limpieza) {
                Limpieza limp = (Limpieza) e;
                txtHorario.setText(limp.getHorarioTrabajo());
                txtSector.setText(limp.getSector());
                panel.add(new JLabel("Horario:"));
                panel.add(txtHorario);
                panel.add(new JLabel("Sector:"));
                panel.add(txtSector);
            }

            while (true) {
                int result = JOptionPane.showConfirmDialog(null, panel, "Modificar Empleado",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        String nombre = txtNombre.getText().trim();
                        String apellido = txtApellido.getText().trim();
                        String sueldoStr = txtSueldo.getText().trim();

                        if (nombre.isEmpty() || apellido.isEmpty() || sueldoStr.isEmpty()) {
                            throw new Exception("Complete los campos obligatorios.");
                        }

                        e.setNombre(nombre);
                        e.setApellido(apellido);
                        e.setSexo((String) cmbSexo.getSelectedItem());
                        e.setSueldo(Double.parseDouble(sueldoStr));

                        if (e instanceof Entrenador) {
                            ((Entrenador) e).setEspecialidad((String) cmbEspecialidad.getSelectedItem());
                        } else if (e instanceof Limpieza) {
                            String horario = txtHorario.getText().trim();
                            String sector = txtSector.getText().trim();
                            if (horario.isEmpty() || sector.isEmpty())
                                throw new Exception("Horario y Sector son obligatorios.");
                            ((Limpieza) e).setHorarioTrabajo(horario);
                            ((Limpieza) e).setSector(sector);
                        }

                        gimnasio.registrarModificacionEmpleado(e);
                        JOptionPane.showMessageDialog(null, "Empleado actualizado exitosamente.");
                        break;
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "El sueldo debe ser numérico.");
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
                    }
                } else {
                    break;
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "DNI inválido.");
        }
    }

    private void agregarClaseDesdeMenu() {
        JComboBox<String> cmbTipo = new JComboBox<>(Gimnasio.TIPOS_CLASE);

        String[] diasSinDomingo = java.util.Arrays.stream(Gimnasio.DIAS_SEMANA)
                .filter(d -> !d.equalsIgnoreCase("Domingo"))
                .toArray(String[]::new);
        JComboBox<String> cmbDia = new JComboBox<>(diasSinDomingo);
        JComboBox<String> cmbTurno = new JComboBox<>(Gimnasio.TURNOS);
        JTextField txtCupo = new JTextField("20");
        JComboBox<String> cmbEntrenador = new JComboBox<>();

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Tipo de Clase:"));
        panel.add(cmbTipo);
        panel.add(new JLabel("Día:"));
        panel.add(cmbDia);
        panel.add(new JLabel("Turno:"));
        panel.add(cmbTurno);
        panel.add(new JLabel("Cupo Máximo:"));
        panel.add(txtCupo);
        panel.add(new JLabel("Entrenador:"));
        panel.add(cmbEntrenador);

        // Actualizar entrenadores al cambiar tipo
        java.awt.event.ActionListener updateTrainers = e -> {
            cmbEntrenador.removeAllItems();
            String tipo = (String) cmbTipo.getSelectedItem();
            for (Empleado emp : gimnasio.getEmpleados()) {
                if (emp instanceof Entrenador) {
                    Entrenador ent = (Entrenador) emp;
                    if (ent.getEspecialidad() != null && ent.getEspecialidad().equalsIgnoreCase(tipo)) {
                        cmbEntrenador.addItem(ent.getDni() + " - " + ent.getNombre());
                    }
                }
            }
        };
        cmbTipo.addActionListener(updateTrainers);
        updateTrainers.actionPerformed(null); // Carga inicial

        while (true) {
            int result = JOptionPane.showConfirmDialog(null, panel, "Agregar Clase",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String tipo = (String) cmbTipo.getSelectedItem();
                    String dia = (String) cmbDia.getSelectedItem();
                    String turno = (String) cmbTurno.getSelectedItem();
                    String cupoStr = txtCupo.getText().trim();
                    String entSel = (String) cmbEntrenador.getSelectedItem();

                    if (cupoStr.isEmpty())
                        throw new Exception("El cupo es obligatorio.");
                    int cupo = Integer.parseInt(cupoStr);
                    if (cupo <= 0)
                        throw new Exception("El cupo debe ser mayor a 0.");

                    if (gimnasio.getClaseEnHorario(dia, turno) != null) {
                        throw new Exception("Ya existe una clase en ese horario.");
                    }

                    if (entSel == null) {
                        throw new Exception(
                                "Debe seleccionar un entrenador. Si no hay, agregue uno con esa especialidad primero.");
                    }

                    int dniEnt = Integer.parseInt(entSel.split(" - ")[0]);
                    Entrenador entrenador = (Entrenador) gimnasio.buscarEmpleadoPorDni(dniEnt);

                    Clase nueva = new Clase(tipo, dia, turno, cupo, entrenador);
                    gimnasio.agregarClase(nueva);
                    entrenador.asignarClase(nueva);
                    actualizarGrilla();

                    JOptionPane.showMessageDialog(null, "Clase agregada exitosamente.");
                    break;

                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Error: El cupo debe ser numérico.");
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                }
            } else {
                break;
            }
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
        if (gimnasio.getEmpleados().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay empleados cargados.");
            return;
        }

        if (gimnasio.getCuenta() == null) {
            JOptionPane.showMessageDialog(null, "No hay cuenta bancaria del gimnasio asignada.");
            return;
        }

        java.util.List<Empleado> empleados = gimnasio.getEmpleados();
        String[] opciones = new String[empleados.size()];
        for (int i = 0; i < empleados.size(); i++) {
            Empleado e = empleados.get(i);
            opciones[i] = e.getDni() + " - " + e.getNombre() + " " + e.getApellido();
        }

        String seleccionado = (String) JOptionPane.showInputDialog(null,
                "Seleccione el empleado a pagar sueldo:", "Pagar Sueldo",
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

        if (seleccionado == null)
            return;

        try {
            int dni = Integer.parseInt(seleccionado.split(" - ")[0]);
            Empleado e = gimnasio.buscarEmpleadoPorDni(dni);

            if (e != null) {
                // Usar registrarPagoSueldo para que se guarde en registros.txt
                boolean pagado = gimnasio.registrarPagoSueldo(e);
                if (pagado) {
                    JOptionPane.showMessageDialog(null, "Sueldo pagado a " + e.getNombre() + " " + e.getApellido());
                } else {
                    JOptionPane.showMessageDialog(null, "No se pudo pagar el sueldo. Fondos insuficientes.");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error al procesar la selección.");
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
