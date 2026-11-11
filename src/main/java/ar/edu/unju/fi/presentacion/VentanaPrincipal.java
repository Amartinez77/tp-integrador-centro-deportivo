package ar.edu.unju.fi.presentacion;

import ar.edu.unju.fi.negocio.ControlAcceso;
import ar.edu.unju.fi.negocio.Logica;
import ar.edu.unju.fi.negocio.excepciones.AccesoDenegadoException;
import ar.edu.unju.fi.negocio.modelo.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interfaz Gráfica Principal (GUI Swing) - Capa de Presentación.
 * Diseño corporativo desacoplado mediante Fachada (Logica).
 */
public class VentanaPrincipal extends JFrame {

    private final Logica logica;
    private final ControlAcceso controlAcceso;

    // Modelos de Tablas
    private DefaultTableModel modelSocios;
    private DefaultTableModel modelEquipamiento;
    private DefaultTableModel modelFacturas;

    // Componentes del Molinete
    private JTextField txtDniMolinete;
    private JPanel panelSemaforo;
    private JLabel lblEstadoMolinete;
    private JLabel lblDetalleMolinete;

    public VentanaPrincipal(Logica logica, ControlAcceso controlAcceso) {
        if (logica == null || controlAcceso == null) {
            throw new IllegalArgumentException("Logica y ControlAcceso son obligatorios.");
        }
        this.logica = logica;
        this.controlAcceso = controlAcceso;

        configurarTemaVisual();
        configurarVentana();
        iniciarComponentes();
    }

    private void configurarTemaVisual() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // Fallback al Look and Feel del sistema
        }
    }

    private void configurarVentana() {
        setTitle("Centro Deportivo - Sistema de Gestión Integral");
        setSize(1000, 680);
        setMinimumSize(new Dimension(850, 550));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void iniciarComponentes() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tabbedPane.addTab("Socios", crearPanelSocios());
        tabbedPane.addTab("Equipamiento", crearPanelEquipamiento());
        tabbedPane.addTab("Reservas", crearPanelReservas());
        tabbedPane.addTab("Facturación", crearPanelFacturacion());
        tabbedPane.addTab("Control de Acceso", crearPanelMolinete());
        tabbedPane.addTab("Reportes", crearPanelReportes());

        add(tabbedPane);
    }

    // ==========================================
    // 1. PANEL SOCIOS
    // ==========================================
    private JPanel crearPanelSocios() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Formulario de Registro
        JPanel pnlForm = new JPanel(new GridLayout(7, 2, 8, 8));
        pnlForm.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Registrar Nuevo Socio", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));

        JTextField txtDni = new JTextField();
        JTextField txtNombre = new JTextField();
        JTextField txtApellido = new JTextField();
        JTextField txtCuota = new JTextField();
        JTextField txtExtra = new JTextField();
        JComboBox<String> cbTipo = new JComboBox<>(new String[]{"Estándar", "VIP", "Vitalicio"});

        pnlForm.add(new JLabel("DNI:"));
        pnlForm.add(txtDni);
        pnlForm.add(new JLabel("Nombre:"));
        pnlForm.add(txtNombre);
        pnlForm.add(new JLabel("Apellido:"));
        pnlForm.add(txtApellido);
        pnlForm.add(new JLabel("Cuota Base ($):"));
        pnlForm.add(txtCuota);
        pnlForm.add(new JLabel("Categoría:"));
        pnlForm.add(cbTipo);
        pnlForm.add(new JLabel("Recargo VIP / % Desc:"));
        pnlForm.add(txtExtra);

        JButton btnRegistrar = new JButton("Registrar Socio");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegistrar.setBackground(new Color(40, 116, 166));
        btnRegistrar.setForeground(Color.WHITE);

        btnRegistrar.addActionListener(e -> {
            try {
                String dni = txtDni.getText().trim();
                String nom = txtNombre.getText().trim();
                String ape = txtApellido.getText().trim();
                double cuota = Double.parseDouble(txtCuota.getText().trim());
                String tipo = (String) cbTipo.getSelectedItem();

                if ("VIP".equals(tipo)) {
                    double recargo = Double.parseDouble(txtExtra.getText().trim());
                    logica.registrarSocio(new SocioVIP(dni, nom, ape, cuota, recargo));
                } else if ("Vitalicio".equals(tipo)) {
                    double desc = Double.parseDouble(txtExtra.getText().trim());
                    logica.registrarSocio(new SocioVitalicio(dni, nom, ape, cuota, desc));
                } else {
                    logica.registrarSocio(new SocioEstandar(dni, nom, ape, cuota));
                }

                JOptionPane.showMessageDialog(this, "Socio registrado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarTablaSocios();
                txtDni.setText("");
                txtNombre.setText("");
                txtApellido.setText("");
                txtCuota.setText("");
                txtExtra.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en el registro: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel pnlIzquierdo = new JPanel(new BorderLayout(8, 8));
        pnlIzquierdo.add(pnlForm, BorderLayout.NORTH);
        pnlIzquierdo.add(btnRegistrar, BorderLayout.SOUTH);

        // Tabla
        modelSocios = crearModeloNoEditable(new String[]{"DNI", "Nombre", "Apellido", "Categoría", "Cuota Total ($)", "Estado Deuda"});
        JTable tblSocios = new JTable(modelSocios);
        estilarTabla(tblSocios);

        panel.add(pnlIzquierdo, BorderLayout.WEST);
        panel.add(new JScrollPane(tblSocios), BorderLayout.CENTER);

        actualizarTablaSocios();
        return panel;
    }

    private void actualizarTablaSocios() {
        modelSocios.setRowCount(0);
        for (Socio s : logica.obtenerTodosLosSocios()) {
            boolean alDia = logica.tieneCuotasAlDia(s.getDni());
            modelSocios.addRow(new Object[]{
                    s.getDni(),
                    s.getNombre(),
                    s.getApellido(),
                    s.getClass().getSimpleName().replace("Socio", "Socio "),
                    String.format("$ %.2f", s.calcularCuotaMensual()),
                    alDia ? "Al día" : "Pendiente"
            });
        }
    }

    // Modelo para la nueva tabla de Alquileres
    private DefaultTableModel modelAlquileres;

    // ==========================================
    // 2. PANEL EQUIPAMIENTO Y ALQUILERES
    // ==========================================
    private JPanel crearPanelEquipamiento() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10)); // Dividido en 2 secciones
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // --- SECCIÓN SUPERIOR: INVENTARIO DE EQUIPAMIENTO ---
        JPanel pnlEquipamiento = new JPanel(new BorderLayout(8, 8));
        pnlEquipamiento.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Inventario de Equipamiento", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));

        modelEquipamiento = crearModeloNoEditable(new String[]{"Código", "Descripción", "Costo Alquiler ($)", "Estado"});
        JTable tblEq = new JTable(modelEquipamiento);
        estilarTabla(tblEq);

        JPanel pnlAccionesEq = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAlta = new JButton("Alta Equipamiento");
        JButton btnAlquilar = new JButton("Registrar Alquiler");

        btnAlta.addActionListener(e -> {
            JTextField txtCod = new JTextField();
            JTextField txtNom = new JTextField();
            JTextField txtCosto = new JTextField();
            Object[] message = {"Código:", txtCod, "Nombre / Descripción:", txtNom, "Costo Alquiler ($):", txtCosto};

            int option = JOptionPane.showConfirmDialog(this, message, "Alta de Equipamiento", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    logica.registrarEquipamiento(new Equipamiento(txtCod.getText().trim(), txtNom.getText().trim(), Double.parseDouble(txtCosto.getText().trim())));
                    actualizarTablasEquipamientoYAlquileres();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnAlquilar.addActionListener(e -> {
            JTextField txtId = new JTextField("ALQ-01");
            JTextField txtDni = new JTextField();
            JTextField txtCod = new JTextField();
            JTextField txtDias = new JTextField("1");
            Object[] message = {"ID Alquiler (ej. ALQ-01):", txtId, "DNI Socio:", txtDni, "Código Equipo:", txtCod, "Días Préstamo:", txtDias};

            int option = JOptionPane.showConfirmDialog(this, message, "Registrar Nuevo Alquiler", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    logica.alquilarEquipamiento(txtId.getText().trim(), txtDni.getText().trim(), txtCod.getText().trim(), Integer.parseInt(txtDias.getText().trim()));
                    actualizarTablasEquipamientoYAlquileres();
                    JOptionPane.showMessageDialog(this, "Alquiler registrado con éxito.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pnlAccionesEq.add(btnAlta);
        pnlAccionesEq.add(btnAlquilar);
        pnlEquipamiento.add(new JScrollPane(tblEq), BorderLayout.CENTER);
        pnlEquipamiento.add(pnlAccionesEq, BorderLayout.SOUTH);

        // --- SECCIÓN INFERIOR: REGISTRO DE ALQUILERES ACTIVOS ---
        JPanel pnlAlquileres = new JPanel(new BorderLayout(8, 8));
        pnlAlquileres.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Registro de Alquileres Emitidos", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));

        modelAlquileres = crearModeloNoEditable(new String[]{"ID Alquiler", "Socio (DNI)", "Equipo (Código)", "Fecha Préstamo", "Días", "Estado"});
        JTable tblAlq = new JTable(modelAlquileres);
        estilarTabla(tblAlq);

        JPanel pnlAccionesAlq = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDevolver = new JButton("Devolver Equipamiento");

        btnDevolver.addActionListener(e -> {
            // Si el usuario seleccionó una fila de la tabla de alquileres, toma el ID automáticamente
            int filaSeleccionada = tblAlq.getSelectedRow();
            String idDefecto = (filaSeleccionada != -1) ? tblAlq.getValueAt(filaSeleccionada, 0).toString() : "ALQ-01";

            JTextField txtId = new JTextField(idDefecto);
            JTextField txtMora = new JTextField("500.0");
            Object[] message = {"ID Alquiler a devolver:", txtId, "Tarifa mora diaria ($):", txtMora};

            int option = JOptionPane.showConfirmDialog(this, message, "Registrar Devolución", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    double mora = logica.devolverEquipamiento(txtId.getText().trim(), LocalDate.now(), Double.parseDouble(txtMora.getText().trim()));
                    actualizarTablasEquipamientoYAlquileres();
                    JOptionPane.showMessageDialog(this, String.format("Devolución procesada correctamente. Recargo por mora: $%.2f", mora));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pnlAccionesAlq.add(btnDevolver);
        pnlAlquileres.add(new JScrollPane(tblAlq), BorderLayout.CENTER);
        pnlAlquileres.add(pnlAccionesAlq, BorderLayout.SOUTH);

        panel.add(pnlEquipamiento);
        panel.add(pnlAlquileres);

        actualizarTablasEquipamientoYAlquileres();
        return panel;
    }

    private void actualizarTablasEquipamientoYAlquileres() {
        // Actualizar Equipamiento
        modelEquipamiento.setRowCount(0);
        for (Equipamiento eq : logica.obtenerTodosLosEquipamientos()) {
            modelEquipamiento.addRow(new Object[]{
                    eq.getCodigo(), eq.getNombre(), String.format("$ %.2f", eq.getCostoAlquiler()),
                    eq.isDisponible() ? "Disponible" : "En Uso"
            });
        }

        // Actualizar Alquileres
        modelAlquileres.setRowCount(0);
        for (Alquiler alq : logica.obtenerTodosLosAlquileres()) {
            long dias = java.time.temporal.ChronoUnit.DAYS.between(alq.getFechaAlquiler(), alq.getFechaLimiteDevolucion());
            modelAlquileres.addRow(new Object[]{
                    alq.getIdAlquiler(),
                    alq.getSocio().getDni() + " (" + alq.getSocio().getApellido() + ")",
                    alq.getEquipamiento().getCodigo(),
                    alq.getFechaAlquiler(),
                    dias,
                    alq.isDevuelto() ? "Devuelto" : "Activo / En Préstamo"
            });
        }
    }

    private DefaultTableModel modelReservas;
    private JComboBox<String> cbActividades;

    // ==========================================
    // 3. PANEL RESERVAS
    // ==========================================
    private JPanel crearPanelReservas() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Panel Superior de Filtro y Acciones
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        pnlTop.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Filtro de Actividades y Acciones", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12)));

        cbActividades = new JComboBox<>();
        cbActividades.setPreferredSize(new Dimension(180, 25));

        JButton btnFiltrar = new JButton("Filtrar Reservas");
        JButton btnVerTodas = new JButton("Ver Todas las Reservas");
        JButton btnNuevaReserva = new JButton("Nueva Reserva");
        btnNuevaReserva.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNuevaReserva.setBackground(new Color(40, 116, 166));
        btnNuevaReserva.setForeground(Color.WHITE);

        pnlTop.add(new JLabel("Actividades Registradas:"));
        pnlTop.add(cbActividades);
        pnlTop.add(btnFiltrar);
        pnlTop.add(btnVerTodas);
        pnlTop.add(btnNuevaReserva);

        // Tabla Principal de Reservas
        modelReservas = crearModeloNoEditable(new String[]{"ID Reserva", "Actividad", "Socio (DNI / Nombre)", "Turno (Fecha y Hora)"});
        JTable tblReservas = new JTable(modelReservas);
        estilarTabla(tblReservas);

        // Acciones de Botones
        btnFiltrar.addActionListener(e -> {
            String actSel = (String) cbActividades.getSelectedItem();
            if (actSel != null && !actSel.startsWith("--")) {
                actualizarTablaReservas(actSel);
            } else {
                actualizarTablaReservas(null);
            }
        });

        btnVerTodas.addActionListener(e -> {
            if (cbActividades.getItemCount() > 0) cbActividades.setSelectedIndex(0);
            actualizarTablaReservas(null);
        });

        btnNuevaReserva.addActionListener(e -> {
            JTextField txtId = new JTextField("RES-");
            JTextField txtAct = new JTextField();
            JTextField txtDni = new JTextField();
            JTextField txtFecha = new JTextField(LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            Object[] message = {"ID Reserva:", txtId, "Código Actividad (ej. NATACION):", txtAct, "DNI Socio:", txtDni, "Fecha/Hora (dd/MM/yyyy HH:mm):", txtFecha};

            int option = JOptionPane.showConfirmDialog(this, message, "Registrar Nueva Reserva", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    LocalDateTime fh = LocalDateTime.parse(txtFecha.getText().trim(), dtf);
                    Reserva r = logica.registrarReserva(txtId.getText().trim(), txtAct.getText().trim(), txtDni.getText().trim(), fh);
                    JOptionPane.showMessageDialog(this, "Reserva realizada con éxito: " + r.getIdReserva());
                    actualizarTablaReservas(null);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(pnlTop, BorderLayout.NORTH);
        panel.add(new JScrollPane(tblReservas), BorderLayout.CENTER);

        actualizarTablaReservas(null);
        return panel;
    }

    private void actualizarTablaReservas(String actividadFiltrada) {
        // 1. Actualizar el combo desplegable de actividades creadas
        cbActividades.removeAllItems();
        Set<String> actividadesExistentes = logica.obtenerTodasLasReservasPorActividad().keySet();

        cbActividades.addItem("-- Todas las Actividades --");
        for (String act : actividadesExistentes) {
            cbActividades.addItem(act);
        }

        if (actividadFiltrada != null) {
            cbActividades.setSelectedItem(actividadFiltrada);
        }

        // 2. Poblar la tabla de reservas
        modelReservas.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        Map<String, Set<Reserva>> mapaReservas = logica.obtenerTodasLasReservasPorActividad();
        for (Map.Entry<String, Set<Reserva>> entry : mapaReservas.entrySet()) {
            String act = entry.getKey();
            if (actividadFiltrada == null || actividadFiltrada.equalsIgnoreCase(act)) {
                for (Reserva r : entry.getValue()) {
                    String socioInfo = (r.getSocio() != null) ? r.getSocio().getApellido() + ", " + r.getSocio().getNombre() + " (" + r.getSocio().getDni() + ")" : "N/D";
                    String fechaStr = (r.getFechaHora() != null) ? r.getFechaHora().format(dtf) : "N/D";
                    modelReservas.addRow(new Object[]{
                            r.getIdReserva(),
                            r.getCodigoActividad(),
                            socioInfo,
                            fechaStr
                    });
                }
            }
        }
    }

    // ==========================================
    // 4. PANEL FACTURACIÓN
    // ==========================================
    private JPanel crearPanelFacturacion() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        modelFacturas = crearModeloNoEditable(new String[]{"N° Factura", "DNI Socio", "Período Fiscal", "Fecha Emisión", "Concepto", "Monto Total ($)"});
        JTable tblFacturas = new JTable(modelFacturas);
        estilarTabla(tblFacturas);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnEmitir = new JButton("Emitir Factura Cuota");
        JButton btnFiltrarPeriodo = new JButton("Filtrar por Período / Socio");

        btnEmitir.addActionListener(e -> {
            JTextField txtNum = new JTextField("FAC-2026-");
            JTextField txtDni = new JTextField();
            JTextField txtPeriodo = new JTextField("2026-11");
            Object[] message = {"N° Factura:", txtNum, "DNI Socio:", txtDni, "Período Fiscal (YYYY-MM):", txtPeriodo};

            int option = JOptionPane.showConfirmDialog(this, message, "Emitir Factura de Cuota Mensual", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    Factura f = logica.emitirFacturaCuota(txtNum.getText().trim(), txtDni.getText().trim(), txtPeriodo.getText().trim());
                    actualizarTablaFacturas();
                    actualizarTablaSocios();
                    JOptionPane.showMessageDialog(this, "Factura emitida correctamente por un valor de $" + f.getMontoTotal());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnFiltrarPeriodo.addActionListener(e -> {
            String dni = JOptionPane.showInputDialog(this, "Ingrese DNI del Socio:");
            String periodo = JOptionPane.showInputDialog(this, "Ingrese Período Fiscal (ej. 2026-11):");
            if (dni != null && periodo != null) {
                List<Factura> filtradas = logica.obtenerFacturasDeSocioPorPeriodo(dni, periodo);
                modelFacturas.setRowCount(0);
                for (Factura f : filtradas) {
                    modelFacturas.addRow(new Object[]{
                            f.getNumeroFactura(), f.getDniSocio(), f.getPeriodoFiscal(), f.getFechaEmision(), f.getConcepto(), String.format("$ %.2f", f.getMontoTotal())
                    });
                }
            }
        });

        pnlBotones.add(btnEmitir);
        pnlBotones.add(btnFiltrarPeriodo);

        panel.add(new JScrollPane(tblFacturas), BorderLayout.CENTER);
        panel.add(pnlBotones, BorderLayout.SOUTH);

        actualizarTablaFacturas();
        return panel;
    }

    private void actualizarTablaFacturas() {
        modelFacturas.setRowCount(0);
        for (Factura f : logica.obtenerTodasLasFacturas()) {
            modelFacturas.addRow(new Object[]{
                    f.getNumeroFactura(), f.getDniSocio(), f.getPeriodoFiscal(), f.getFechaEmision(), f.getConcepto(), String.format("$ %.2f", f.getMontoTotal())
            });
        }
    }

    // ==========================================
    // 5. PANEL MOLINETE (CONTROL ACCESO)
    // ==========================================
    private JPanel crearPanelMolinete() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Control de Acceso Automático", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        txtDniMolinete = new JTextField(15);
        txtDniMolinete.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JButton btnValidar = new JButton("Validar Ingreso");
        btnValidar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnValidar.setBackground(new Color(40, 116, 166));
        btnValidar.setForeground(Color.WHITE);

        panelSemaforo = new JPanel(new BorderLayout());
        panelSemaforo.setPreferredSize(new Dimension(450, 150));
        panelSemaforo.setBackground(new Color(52, 73, 94));
        panelSemaforo.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        lblEstadoMolinete = new JLabel("ESPERANDO LECTURA", SwingConstants.CENTER);
        lblEstadoMolinete.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblEstadoMolinete.setForeground(Color.WHITE);

        lblDetalleMolinete = new JLabel("Ingrese DNI para verificar autorización", SwingConstants.CENTER);
        lblDetalleMolinete.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDetalleMolinete.setForeground(new Color(208, 211, 212));

        panelSemaforo.add(lblEstadoMolinete, BorderLayout.CENTER);
        panelSemaforo.add(lblDetalleMolinete, BorderLayout.SOUTH);

        btnValidar.addActionListener(e -> validarMolinete());
        txtDniMolinete.addActionListener(e -> validarMolinete());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(new JLabel("DNI Socio:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(txtDniMolinete, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(btnValidar, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(panelSemaforo, gbc);

        return panel;
    }

    private void validarMolinete() {
        String dni = txtDniMolinete.getText().trim();
        if (dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un DNI válido.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Socio s = logica.buscarSocio(dni);

        try {
            if (controlAcceso.validarAcceso(dni)) {
                panelSemaforo.setBackground(new Color(39, 174, 96)); // Verde limpio
                lblEstadoMolinete.setText("ACCESO AUTORIZADO");
                lblEstadoMolinete.setForeground(Color.WHITE);
                lblDetalleMolinete.setText(s != null ? "Bienvenido/a " + s.getNombre() + " " + s.getApellido() : "Socio Habilitado");
            }
        } catch (AccesoDenegadoException ex) {
            panelSemaforo.setBackground(new Color(192, 57, 43)); // Rojo limpio
            lblEstadoMolinete.setText("ACCESO DENEGADO");
            lblEstadoMolinete.setForeground(Color.WHITE);
            lblDetalleMolinete.setText("Motivo: " + ex.getMessage());
        }
    }

    // ==========================================
    // 6. PANEL REPORTES
    // ==========================================
    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        DefaultListModel<String> modelReportes = new DefaultListModel<>();
        JList<String> lstReportes = new JList<>(modelReportes);
        lstReportes.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JPanel pnlBotones = new JPanel(new GridLayout(2, 2, 10, 10));
        JButton btnOrdApellido = new JButton("Socios por Apellido (Comparable)");
        JButton btnOrdCuota = new JButton("Socios por Cuota (Comparator)");
        JButton btnRanking = new JButton("Ranking de Actividades");
        JButton btnBinary = new JButton("Búsqueda Binaria por DNI");

        btnOrdApellido.addActionListener(e -> {
            modelReportes.clear();
            modelReportes.addElement("--- SOCIOS ORDENADOS POR APELLIDO (Comparable) ---");
            logica.obtenerSociosOrdenadosPorApellido().forEach(s ->
                    modelReportes.addElement(String.format(" - %-25s | DNI: %-10s | %s", s.getApellido() + ", " + s.getNombre(), s.getDni(), s.getClass().getSimpleName())));
        });

        btnOrdCuota.addActionListener(e -> {
            modelReportes.clear();
            modelReportes.addElement("--- SOCIOS ORDENADOS POR CUOTA MENSUAL (Comparator) ---");
            logica.obtenerSociosOrdenadosPorCuota().forEach(s ->
                    modelReportes.addElement(String.format(" - %-25s | DNI: %-10s | Cuota: $%.2f", s.getApellido() + ", " + s.getNombre(), s.getDni(), s.calcularCuotaMensual())));
        });

        btnRanking.addActionListener(e -> {
            modelReportes.clear();
            modelReportes.addElement("--- RANKING DE ACTIVIDADES POR RESERVAS ---");
            logica.obtenerActividadesOrdenadasPorReservas().forEach(a ->
                    modelReportes.addElement(String.format(" - Actividad: %-15s | Inscriptos: %d", a.getCodigoActividad(), a.getCantidadReservas())));
        });

        btnBinary.addActionListener(e -> {
            String dni = JOptionPane.showInputDialog(this, "Ingrese DNI a buscar (Collections.binarySearch):");
            if (dni != null && !dni.isBlank()) {
                modelReportes.clear();
                Socio s = logica.buscarSocioPorDniBinario(dni.trim());
                if (s != null) {
                    modelReportes.addElement("SOCIO ENCONTRADO (BINARY SEARCH):");
                    modelReportes.addElement("   " + s.toString() + " | Cuota: $" + s.calcularCuotaMensual());
                } else {
                    modelReportes.addElement("No se encontró ningún socio registrado con el DNI: " + dni);
                }
            }
        });

        pnlBotones.add(btnOrdApellido);
        pnlBotones.add(btnOrdCuota);
        pnlBotones.add(btnRanking);
        pnlBotones.add(btnBinary);

        panel.add(pnlBotones, BorderLayout.NORTH);
        panel.add(new JScrollPane(lstReportes), BorderLayout.CENTER);

        return panel;
    }

    // ==========================================
    // MÉTODOS AUXILIARES DE DISEÑO
    // ==========================================
    private DefaultTableModel crearModeloNoEditable(String[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void estilarTabla(JTable tabla) {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(24);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
}