package SegundoSprint.vista;

import TercerSprint.control.CitaDAO;
import TercerSprint.control.CitasControlador;
import TercerSprint.control.ClienteDAO;
import TercerSprint.control.ClientesControlador;
import TercerSprint.control.EmpleadoDAO;
import TercerSprint.control.EmpleadosControlador;
import TercerSprint.control.MensajesError;
import TercerSprint.control.SesionActual;
import TercerSprint.control.TallerDAO;
import TercerSprint.control.TalleresControlador;
import TercerSprint.control.TrajeDAO;
import TercerSprint.modelo.Cita;
import TercerSprint.modelo.Cliente;
import TercerSprint.modelo.Empleado;
import TercerSprint.modelo.Empleado.Categoria;
import TercerSprint.modelo.Taller;
import TercerSprint.modelo.Traje;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ventana principal de la aplicación del Taller de Edna Moda.
 * <p>
 * Las distintas secciones (clientes, citas, talleres) se muestran como
 * <code>JPanel</code>s intercambiables dentro del panel central. Ninguna
 * tabla muestra identificadores numéricos: cliente, traje, taller y empleado
 * se identifican siempre por su nombre legible; los IDs se guardan en listas
 * paralelas internas para poder ejecutar las operaciones contra la BBDD.
 * </p>
 *
 * <p>Sprint #2 — Vista; integrada con los controladores en el Sprint #3.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.2
 * @since Sprint #2
 */
public class VentanaPrincipal extends JFrame {

    private String empleadoApodo;
    private String categoriaEmpleado;

    // Menus / botones de la barra superior
    private JMenu menuCitas;
    private JButton botonTalleres;
    private JButton botonClientes;
    private JButton botonEmpleados;

    // Items Citas
    private JMenuItem itemVerTodasCitas;
    private JMenuItem itemMisCitas;
    private JMenuItem itemNuevaCita;
    private JMenuItem itemEditarCita;
    private JMenuItem itemBorrarCita;

    // Permisos de Talleres (se aplican a los botones del panel)
    private boolean permisoVerTalleres = true;
    private boolean permisoNuevoTaller = true;
    private boolean permisoEditarTaller = true;
    private boolean permisoBorrarTaller = true;

    // Permisos de Clientes (se aplican a los botones del panel)
    private boolean permisoVerClientes = true;
    private boolean permisoNuevoCliente = true;
    private boolean permisoEditarCliente = true;
    private boolean permisoBorrarCliente = true;

    // Permisos de Empleados (solo Maestro)
    private boolean permisoNuevoEmpleado   = true;
    private boolean permisoEditarEmpleado  = true;
    private boolean permisoBorrarEmpleado  = true;
    private boolean permisoResetPassword   = true;

    // Items sesion
    private JMenuItem itemMiPassword;
    private JMenuItem itemCerrarSesion;

    private JLabel lblBienvenida;
    private JPanel panelContenido;

    // Controladores (se crean perezosamente)
    private CitasControlador citasControlador;
    private TalleresControlador talleresControlador;
    private ClientesControlador clientesControlador;
    private EmpleadosControlador empleadosControlador;

    public VentanaPrincipal(String apodo, String categoria) {
        this.empleadoApodo = apodo;
        this.categoriaEmpleado = categoria;

        setTitle("Taller Edna Moda");
        setSize(960, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setIconImages(IconoApp.getIconImages());

        construirMenu();
        construirLayout();
        aplicarPermisos();
        asignarListeners();

        setVisible(true);
    }

    // =====================================================================
    // CONSTRUCCION DE UI
    // =====================================================================

    private void construirMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(30, 30, 30));
        menuBar.setOpaque(true);

        // "Citas" se mantiene como menu desplegable (tiene dos opciones).
        menuCitas = crearMenu("Citas");
        itemVerTodasCitas = crearItem("Ver todas las citas");
        itemMisCitas      = crearItem("Mis citas");
        itemNuevaCita     = crearItem("Nueva cita");
        itemEditarCita    = crearItem("Editar cita");
        itemBorrarCita    = crearItem("Borrar cita");
        menuCitas.add(itemVerTodasCitas);
        menuCitas.add(itemMisCitas);

        // "Talleres", "Clientes" y "Empleados" son botones directos: al pulsar
        // se entra al panel correspondiente sin abrir submenu.
        botonTalleres  = crearBotonBarra("Talleres");
        botonClientes  = crearBotonBarra("Clientes");
        botonEmpleados = crearBotonBarra("Empleados");

        JMenu menuSesion = crearMenu("Sesión");
        itemMiPassword   = crearItem("Cambiar mi contraseña");
        itemCerrarSesion = crearItem("Cerrar sesión");
        menuSesion.add(itemMiPassword);
        menuSesion.addSeparator();
        menuSesion.add(itemCerrarSesion);

        menuBar.add(menuCitas);
        menuBar.add(botonTalleres);
        menuBar.add(botonClientes);
        menuBar.add(botonEmpleados);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuSesion);

        setJMenuBar(menuBar);
    }

    /**
     * Crea un boton plano para la barra superior con el mismo estilo que los
     * JMenu (fondo oscuro, texto blanco). Al pulsarlo no se abre submenu,
     * sino que ejecuta directamente la accion asociada.
     */
    private JButton crearBotonBarra(String texto) {
        final Color fondoNormal = new Color(30, 30, 30);
        final Color fondoHover  = new Color(60, 60, 60);
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                Color bg = fondoNormal;
                if (getModel().isRollover() || getModel().isPressed()) bg = fondoHover;
                g2.setColor(bg);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        return btn;
    }

    private JMenu crearMenu(String texto) {
        JMenu menu = new JMenu(texto);
        menu.setForeground(Color.WHITE);
        menu.setOpaque(true);
        menu.setBackground(new Color(30, 30, 30));
        menu.setFont(new Font("Arial", Font.BOLD, 13));
        return menu;
    }

    private JMenuItem crearItem(String texto) {
        JMenuItem item = new JMenuItem(texto);
        item.setForeground(Color.BLACK);
        item.setBackground(Color.WHITE);
        item.setOpaque(true);
        item.setFont(new Font("Arial", Font.PLAIN, 13));
        return item;
    }

    /**
     * Crea un boton de accion con un estilo consistente en toda la app.
     */
    private JButton crearBotonAccion(String texto) {
        final Color fondoNormal  = new Color(255, 153, 0);
        final Color fondoHover   = new Color(255, 173, 51);
        final Color fondoPress   = new Color(204, 122, 0);
        final Color borde        = new Color(204, 122, 0);

        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = fondoNormal;
                if (!isEnabled())                            bg = new Color(210, 210, 210);
                else if (getModel().isPressed())             bg = fondoPress;
                else if (getModel().isRollover())            bg = fondoHover;
                g2.setColor(bg);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde, 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        return btn;
    }

    private void construirLayout() {
        JPanel base = new JPanel(new BorderLayout());
        base.setBackground(Color.WHITE);

        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelSuperior.setBackground(new Color(245, 245, 245));
        lblBienvenida = new JLabel("Bienvenido/a, " + empleadoApodo + " (" + categoriaEmpleado + ")");
        lblBienvenida.setFont(new Font("Arial", Font.ITALIC, 13));
        panelSuperior.add(lblBienvenida);

        panelContenido = crearPanelInicio();

        base.add(panelSuperior, BorderLayout.NORTH);
        base.add(panelContenido, BorderLayout.CENTER);
        setContentPane(base);
    }

    /**
     * Crea el panel inicial que se muestra justo después del login.
     * <ul>
     *   <li><b>Aprendiz / Oficial</b>: panel "Mis citas" (las del usuario logueado).</li>
     *   <li><b>Maestro</b>: panel con todas las citas (tiene más sentido al ver toda la operativa).</li>
     *   <li>Si por cualquier motivo falla la creación del panel de citas (p. ej. la BBDD
     *       no responde), se muestra como respaldo la pantalla de bienvenida clásica.</li>
     * </ul>
     */
    private JPanel crearPanelInicio() {
        try {
            boolean soloMias = !"Maestro".equals(categoriaEmpleado);
            return panelCitas(soloMias);
        } catch (Exception ex) {
            // Si la BBDD no responde u ocurre algo inesperado, no bloqueamos la
            // ventana principal: mostramos el panel de bienvenida tradicional.
            return crearPanelBienvenida();
        }
    }

    /** Pantalla de bienvenida de respaldo (logo y eslogan del taller). */
    private JPanel crearPanelBienvenida() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        JLabel lbl = new JLabel("<html><div style='text-align:center;'>"
                + "<span style='font-size:36px;'>&#9986;</span><br>"
                + "<b style='font-size:22px;'>TALLER EDNA MODA</b><br>"
                + "<i style='font-size:13px;color:gray;'>No capes.</i>"
                + "</div></html>", SwingConstants.CENTER);
        p.add(lbl);
        return p;
    }

    private void mostrarPanel(JPanel nuevo) {
        JPanel base = (JPanel) getContentPane();
        base.remove(panelContenido);
        panelContenido = nuevo;
        base.add(panelContenido, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void aplicarPermisos() {
        switch (categoriaEmpleado) {
            case "Aprendiz":
                itemVerTodasCitas.setEnabled(false);
                itemNuevaCita.setEnabled(false);
                itemEditarCita.setEnabled(false);
                itemBorrarCita.setEnabled(false);
                permisoVerTalleres = false;
                permisoNuevoTaller = false;
                permisoEditarTaller = false;
                permisoBorrarTaller = false;
                permisoVerClientes = false;
                permisoNuevoCliente = false;
                permisoEditarCliente = false;
                permisoBorrarCliente = false;
                permisoNuevoEmpleado = false;
                permisoEditarEmpleado = false;
                permisoBorrarEmpleado = false;
                permisoResetPassword = false;
                botonTalleres.setEnabled(false);
                botonClientes.setEnabled(false);
                botonEmpleados.setEnabled(false);
                break;
            case "Oficial":
                // Oficial: crea y edita sus citas (no borra), ve talleres
                // (no los gestiona) y no accede a clientes ni a empleados.
                itemBorrarCita.setEnabled(false);
                permisoNuevoTaller = false;
                permisoEditarTaller = false;
                permisoBorrarTaller = false;
                permisoVerClientes = false;
                permisoNuevoCliente = false;
                permisoEditarCliente = false;
                permisoBorrarCliente = false;
                permisoNuevoEmpleado = false;
                permisoEditarEmpleado = false;
                permisoBorrarEmpleado = false;
                permisoResetPassword = false;
                botonClientes.setEnabled(false);
                botonEmpleados.setEnabled(false);
                break;
            case "Maestro":
                // Acceso total
                break;
            default:
                menuCitas.setEnabled(false);
                itemNuevaCita.setEnabled(false);
                itemEditarCita.setEnabled(false);
                itemBorrarCita.setEnabled(false);
                permisoVerTalleres = false;
                permisoNuevoTaller = false;
                permisoEditarTaller = false;
                permisoBorrarTaller = false;
                permisoVerClientes = false;
                permisoNuevoCliente = false;
                permisoEditarCliente = false;
                permisoBorrarCliente = false;
                permisoNuevoEmpleado = false;
                permisoEditarEmpleado = false;
                permisoBorrarEmpleado = false;
                permisoResetPassword = false;
                botonTalleres.setEnabled(false);
                botonClientes.setEnabled(false);
                botonEmpleados.setEnabled(false);
        }
    }

    private void asignarListeners() {
        itemMiPassword.addActionListener(e -> dialogoCambiarMiPassword());
        itemCerrarSesion.addActionListener(e -> {
            SesionActual.cerrar();
            dispose();
            LoginVista vista = new LoginVista();
            new TercerSprint.control.LoginControlador(vista);
        });

        itemVerTodasCitas.addActionListener(e -> mostrarPanel(panelCitas(false)));
        itemMisCitas.addActionListener(e -> mostrarPanel(panelCitas(true)));
        botonTalleres.addActionListener(e -> {
            if (botonTalleres.isEnabled()) mostrarPanel(panelTalleres());
        });
        botonClientes.addActionListener(e -> {
            if (botonClientes.isEnabled()) mostrarPanel(panelClientes());
        });
        botonEmpleados.addActionListener(e -> {
            if (botonEmpleados.isEnabled()) mostrarPanel(panelEmpleados());
        });
    }

    /** Atajo para mostrar un mensaje de "Selecciona un elemento". */
    private void avisoSeleccion(String accion, String elemento) {
        JOptionPane.showMessageDialog(this,
                "Selecciona un " + elemento + " a " + accion + " en la tabla.",
                "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    // =====================================================================
    // PANEL DE CITAS
    // =====================================================================

    /**
     * Panel de listado de citas. La selección de la fila en la tabla es lo
     * que determina sobre qué cita actúan los botones Editar y Borrar.
     *
     * @param soloMias si <code>true</code>, solo se muestran las citas del empleado autenticado.
     */
    private JPanel panelCitas(boolean soloMias) {
        if (citasControlador == null) citasControlador = new CitasControlador();
        String titulo = soloMias ? "Mis citas" : "Todas las citas";

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        String[] columnas = {"Cliente", "Traje", "Taller", "Empleado resp.",
                             "Día", "Hora", "Duración (h)"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JLabel estado = new JLabel(" ");
        estado.setForeground(new Color(100, 100, 100));

        // Lista paralela con las citas mostradas (una por fila).
        final List<Cita> citasEnTabla = new ArrayList<>();

        try {
            ClienteDAO cliDAO  = new ClienteDAO();
            TrajeDAO   trajeDAO = new TrajeDAO();
            TallerDAO  tallerDAO = new TallerDAO();
            EmpleadoDAO empDAO = new EmpleadoDAO();

            Map<Integer, String> nomClientes = new HashMap<>();
            for (Cliente cli : cliDAO.listarTodos())
                nomClientes.put(cli.getNumeroCliente(), cli.getNombre());

            Map<Integer, String> nomTrajes = new HashMap<>();
            for (Traje t : trajeDAO.listarTodos())
                nomTrajes.put(t.getIdentificador(), t.getNombre());

            Map<Integer, String> nomTalleres = new HashMap<>();
            for (Taller t : tallerDAO.listarTodos())
                nomTalleres.put(t.getNumeroTaller(),
                        (t.getTipoSala() != null ? t.getTipoSala().getTexto() + " - " : "")
                                + t.getNombreSala());

            Map<Integer, String> nomEmp = new HashMap<>();
            for (Empleado e : empDAO.listarTodos())
                nomEmp.put(e.getIdentificador(), e.getApodo());

            List<Cita> citas = soloMias
                    ? citasControlador.listarMisCitas()
                    : citasControlador.listarCitasVisibles();

            for (Cita c : citas) {
                citasEnTabla.add(c);
                String nomCli    = nomClientes.getOrDefault(c.getIdCliente(), "-");
                String nomTraje  = nomTrajes.getOrDefault(c.getIdTraje(), "-");
                String nomTaller = nomTalleres.getOrDefault(c.getIdTaller(), "-");
                String nomEmpR   = nomEmp.getOrDefault(c.getIdEmpleadoResponsable(), "-");

                modelo.addRow(new Object[]{nomCli, nomTraje, nomTaller, nomEmpR,
                        c.getDia(), c.getHora(), c.getDuracion()});
            }
            estado.setText("Mostrando " + citas.size() + " cita(s).");
        } catch (SQLException ex) {
            estado.setText("Error al cargar citas: " + ex.getMessage());
            estado.setForeground(new Color(180, 0, 0));
        } catch (IllegalStateException ex) {
            estado.setText(ex.getMessage());
            estado.setForeground(new Color(180, 0, 0));
        }

        // Botonera inferior: siempre se muestran los 3 botones aunque la
        // ventana sea estrecha. El texto de estado se coloca debajo para
        // que nunca tape los botones.
        JButton btnNueva  = crearBotonAccion("Nueva cita");
        JButton btnEditar = crearBotonAccion("Editar cita");
        JButton btnBorrar = crearBotonAccion("Borrar cita");
        btnNueva.addActionListener(e -> dialogoNuevaCita(soloMias));
        btnEditar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) { avisoSeleccion("editar", "cita"); return; }
            Cita seleccionada = citasEnTabla.get(fila);
            // Si es Oficial, solo puede editar las citas de las que es responsable.
            // Avisamos antes de abrir el diálogo para evitar que rellene datos
            // y se encuentre el error tras pulsar OK.
            if ("Oficial".equals(categoriaEmpleado)) {
                Empleado yo = SesionActual.getEmpleado();
                if (yo == null
                        || seleccionada.getIdEmpleadoResponsable() != yo.getIdentificador()) {
                    JOptionPane.showMessageDialog(this,
                            "Como Oficial solo puedes editar tus propias citas.",
                            "No permitido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            dialogoEditarCita(seleccionada, soloMias);
        });
        btnBorrar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) { avisoSeleccion("borrar", "cita"); return; }
            dialogoBorrarCita(citasEnTabla.get(fila), soloMias);
        });
        btnNueva.setEnabled(itemNuevaCita.isEnabled());
        btnEditar.setEnabled(itemEditarCita.isEnabled());
        btnBorrar.setEnabled(itemBorrarCita.isEnabled());

        JPanel sur = new JPanel(new BorderLayout());
        sur.setBackground(Color.WHITE);
        JPanel botonesIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
        botonesIzq.setBackground(Color.WHITE);
        botonesIzq.add(btnNueva);
        botonesIzq.add(btnEditar);
        botonesIzq.add(btnBorrar);
        JPanel filaEstado = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filaEstado.setBackground(Color.WHITE);
        filaEstado.add(estado);
        sur.add(botonesIzq, BorderLayout.NORTH);
        sur.add(filaEstado, BorderLayout.SOUTH);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(sur, BorderLayout.SOUTH);
        return panel;
    }

    // =====================================================================
    // PANEL DE TALLERES
    // =====================================================================

    private JPanel panelTalleres() {
        if (talleresControlador == null) talleresControlador = new TalleresControlador();

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitulo = new JLabel("Talleres");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        String[] columnas = {"Nombre de sala", "Tipo de sala"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JLabel estado = new JLabel(" ");
        estado.setForeground(new Color(100, 100, 100));

        final List<Taller> talleresEnTabla = new ArrayList<>();

        try {
            List<Taller> talleres = talleresControlador.listarTalleres();
            for (Taller t : talleres) {
                talleresEnTabla.add(t);
                modelo.addRow(new Object[]{
                        t.getNombreSala(),
                        t.getTipoSala() != null ? t.getTipoSala().getTexto() : ""
                });
            }
            estado.setText("Mostrando " + talleres.size() + " taller(es).");
        } catch (SQLException ex) {
            estado.setText("Error al cargar talleres: " + ex.getMessage());
            estado.setForeground(new Color(180, 0, 0));
        } catch (IllegalStateException ex) {
            estado.setText(ex.getMessage());
            estado.setForeground(new Color(180, 0, 0));
        }

        JButton btnNuevo  = crearBotonAccion("Nuevo taller");
        JButton btnEditar = crearBotonAccion("Editar taller");
        JButton btnBorrar = crearBotonAccion("Borrar taller");
        btnNuevo.addActionListener(e -> dialogoNuevoTaller());
        btnEditar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) { avisoSeleccion("editar", "taller"); return; }
            dialogoEditarTaller(talleresEnTabla.get(fila));
        });
        btnBorrar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) { avisoSeleccion("borrar", "taller"); return; }
            dialogoBorrarTaller(talleresEnTabla.get(fila));
        });
        btnNuevo.setEnabled(permisoNuevoTaller);
        btnEditar.setEnabled(permisoEditarTaller);
        btnBorrar.setEnabled(permisoBorrarTaller);

        JPanel sur = new JPanel(new BorderLayout());
        sur.setBackground(Color.WHITE);
        JPanel botonesIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
        botonesIzq.setBackground(Color.WHITE);
        botonesIzq.add(btnNuevo);
        botonesIzq.add(btnEditar);
        botonesIzq.add(btnBorrar);
        JPanel filaEstado = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filaEstado.setBackground(Color.WHITE);
        filaEstado.add(estado);
        sur.add(botonesIzq, BorderLayout.NORTH);
        sur.add(filaEstado, BorderLayout.SOUTH);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(sur, BorderLayout.SOUTH);
        return panel;
    }

    // =====================================================================
    // PANEL DE CLIENTES
    // =====================================================================

    private JPanel panelClientes() {
        if (clientesControlador == null) clientesControlador = new ClientesControlador();

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitulo = new JLabel("Clientes");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        String[] columnas = {"Nombre superhéroe/villano", "Superpoder", "Colores", "Tipo"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JLabel estado = new JLabel(" ");
        estado.setForeground(new Color(100, 100, 100));

        final List<Cliente> clientesEnTabla = new ArrayList<>();

        try {
            List<Cliente> clientes = clientesControlador.listarClientes();
            for (Cliente c : clientes) {
                clientesEnTabla.add(c);
                modelo.addRow(new Object[]{
                        c.getNombre(),
                        c.getSuperpoder(),
                        c.getColores(),
                        c.getTipo() != null ? c.getTipo().getTexto() : ""
                });
            }
            estado.setText("Mostrando " + clientes.size() + " cliente(s).");
        } catch (SQLException ex) {
            estado.setText("Error al cargar clientes: " + ex.getMessage());
            estado.setForeground(new Color(180, 0, 0));
        } catch (IllegalStateException ex) {
            estado.setText(ex.getMessage());
            estado.setForeground(new Color(180, 0, 0));
        }

        JButton btnVerInfo = crearBotonAccion("Ver info cliente");
        btnVerInfo.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) { avisoSeleccion("ver", "cliente"); return; }
            dialogoInfoCliente(clientesEnTabla.get(fila));
        });

        JButton btnNuevo  = crearBotonAccion("Nuevo cliente");
        JButton btnEditar = crearBotonAccion("Editar cliente");
        JButton btnBorrar = crearBotonAccion("Borrar cliente");
        btnNuevo.addActionListener(e -> dialogoNuevoCliente());
        btnEditar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) { avisoSeleccion("editar", "cliente"); return; }
            dialogoEditarCliente(clientesEnTabla.get(fila));
        });
        btnBorrar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) { avisoSeleccion("borrar", "cliente"); return; }
            dialogoBorrarCliente(clientesEnTabla.get(fila));
        });
        btnNuevo.setEnabled(permisoNuevoCliente);
        btnEditar.setEnabled(permisoEditarCliente);
        btnBorrar.setEnabled(permisoBorrarCliente);

        JPanel sur = new JPanel(new BorderLayout());
        sur.setBackground(Color.WHITE);
        JPanel botonesIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
        botonesIzq.setBackground(Color.WHITE);
        botonesIzq.add(btnVerInfo);
        botonesIzq.add(btnNuevo);
        botonesIzq.add(btnEditar);
        botonesIzq.add(btnBorrar);
        JPanel filaEstado = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filaEstado.setBackground(Color.WHITE);
        filaEstado.add(estado);
        sur.add(botonesIzq, BorderLayout.NORTH);
        sur.add(filaEstado, BorderLayout.SOUTH);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(sur, BorderLayout.SOUTH);
        return panel;
    }

    // =====================================================================
    // DIALOGO: VER INFO CLIENTE (con sus trajes y opcion de editar traje)
    // =====================================================================

    /**
     * Muestra una pantalla con todos los datos del cliente y una tabla con
     * los trajes que tiene asociados. Si el empleado tiene permisos
     * (Oficial o Maestro), se ofrece un botón para editar el traje
     * seleccionado en la tabla.
     */
    private void dialogoInfoCliente(Cliente c) {
        if (clientesControlador == null) clientesControlador = new ClientesControlador();
        try {
            List<Traje> trajes = clientesControlador.listarTrajesDe(c.getNumeroCliente());

            JPanel contenido = new JPanel(new BorderLayout(10, 10));
            contenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel datosCliente = new JPanel(new GridLayout(0, 2, 6, 6));
            datosCliente.setBorder(BorderFactory.createTitledBorder("Datos del cliente"));
            datosCliente.add(new JLabel("Nombre:"));
            datosCliente.add(new JLabel(c.getNombre()));
            datosCliente.add(new JLabel("Superpoder:"));
            datosCliente.add(new JLabel(c.getSuperpoder() != null ? c.getSuperpoder() : "-"));
            datosCliente.add(new JLabel("Colores:"));
            datosCliente.add(new JLabel(c.getColores() != null ? c.getColores() : "-"));
            datosCliente.add(new JLabel("Tipo:"));
            datosCliente.add(new JLabel(c.getTipo() != null ? c.getTipo().getTexto() : "-"));

            String[] colsTraje = {"Nombre del traje", "Estado"};
            DefaultTableModel mTrajes = new DefaultTableModel(colsTraje, 0) {
                public boolean isCellEditable(int r, int col) { return false; }
            };
            for (Traje t : trajes) {
                mTrajes.addRow(new Object[]{
                        t.getNombre(),
                        t.getEstado() != null ? t.getEstado().getTexto() : "-"
                });
            }
            JTable tablaTrajes = new JTable(mTrajes);
            tablaTrajes.setRowHeight(22);
            tablaTrajes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
            JScrollPane scrollTrajes = new JScrollPane(tablaTrajes);
            scrollTrajes.setBorder(BorderFactory.createTitledBorder("Trajes (" + trajes.size() + ")"));
            scrollTrajes.setPreferredSize(new Dimension(420, 150));

            // Botones del diálogo: editar traje seleccionado
            JButton btnEditarTraje = crearBotonAccion("Editar traje");
            btnEditarTraje.setEnabled("Oficial".equals(categoriaEmpleado)
                                    || "Maestro".equals(categoriaEmpleado));
            JPanel sur = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
            sur.add(btnEditarTraje);

            contenido.add(datosCliente, BorderLayout.NORTH);
            contenido.add(scrollTrajes, BorderLayout.CENTER);
            contenido.add(sur, BorderLayout.SOUTH);

            // Diálogo modal para poder reabrirlo tras editar
            final JDialog dlg = new JDialog(this, "Info cliente: " + c.getNombre(), true);
            dlg.setContentPane(contenido);
            dlg.pack();
            dlg.setLocationRelativeTo(this);

            btnEditarTraje.addActionListener(e -> {
                int fila = tablaTrajes.getSelectedRow();
                if (fila == -1) { avisoSeleccion("editar", "traje"); return; }
                Traje seleccionado = trajes.get(fila);
                dlg.dispose();
                dialogoEditarTraje(seleccionado, c);
            });

            dlg.setVisible(true);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, MensajesError.traducir(ex),
                    "No se ha podido completar la operación", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Diálogo de edición de un traje (nombre y estado). Solo accesible para
     * Oficiales y Maestros.
     */
    private void dialogoEditarTraje(Traje traje, Cliente cliente) {
        if (clientesControlador == null) clientesControlador = new ClientesControlador();

        JTextField txtNombre = new JTextField(traje.getNombre());
        JComboBox<Traje.Estado> cmbEstado = new JComboBox<>(Traje.Estado.values());
        if (traje.getEstado() != null) cmbEstado.setSelectedItem(traje.getEstado());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Nombre del traje:")); form.add(txtNombre);
        form.add(new JLabel("Estado:"));            form.add(cmbEstado);

        int res = JOptionPane.showConfirmDialog(this, form,
                "Editar traje: " + traje.getNombre(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) {
            // Reabrimos la info del cliente al cancelar
            dialogoInfoCliente(cliente);
            return;
        }

        try {
            Traje actualizado = new Traje(traje.getIdentificador(),
                    traje.getIdCliente(),
                    txtNombre.getText().trim(),
                    (Traje.Estado) cmbEstado.getSelectedItem());
            boolean ok = clientesControlador.modificarTraje(actualizado);
            JOptionPane.showMessageDialog(this,
                    ok ? "Traje modificado." : "No se ha modificado ninguna fila.",
                    "Editar traje",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, MensajesError.traducir(ex),
                    "No se ha podido completar la operación", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No permitido", JOptionPane.WARNING_MESSAGE);
        }
        // Reabrimos siempre la info del cliente al volver
        dialogoInfoCliente(cliente);
    }

    // =====================================================================
    // DIALOGOS DE CITAS
    // =====================================================================

    /**
     * Construye un formulario compacto y minimalista para los diálogos
     * de cita. Coloca cada etiqueta a la izquierda y su control a la
     * derecha, alineados, con tipografía discreta y poco espacio entre filas.
     *
     * @param filas pares (etiqueta, componente). Debe tener longitud par.
     * @return panel listo para mostrar en un JOptionPane.
     */
    private JPanel crearFormularioCompacto(Object... filas) {
        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        GroupLayout gl = new GroupLayout(form);
        form.setLayout(gl);
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(false);

        GroupLayout.ParallelGroup colLabels = gl.createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.ParallelGroup colControls = gl.createParallelGroup(GroupLayout.Alignment.LEADING);
        GroupLayout.SequentialGroup vertical = gl.createSequentialGroup();

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 12);
        Color labelColor = new Color(80, 80, 80);

        for (int i = 0; i + 1 < filas.length; i += 2) {
            JLabel lbl = new JLabel(filas[i].toString());
            lbl.setFont(labelFont);
            lbl.setForeground(labelColor);

            Component ctrl = (Component) filas[i + 1];
            // Acotar la altura preferida de los combos y campos
            if (ctrl instanceof JComboBox || ctrl instanceof JTextField) {
                Dimension d = ctrl.getPreferredSize();
                ctrl.setPreferredSize(new Dimension(220, 24));
                ctrl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
            }

            colLabels.addComponent(lbl);
            colControls.addComponent(ctrl, 220, 220, Short.MAX_VALUE);

            vertical.addGroup(gl.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(lbl)
                    .addComponent(ctrl));
            if (i + 2 < filas.length) vertical.addGap(4);
        }

        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addGroup(colLabels)
                .addGap(10)
                .addGroup(colControls));
        gl.setVerticalGroup(vertical);
        return form;
    }

    /** Construye el panel reutilizable para seleccionar 0..2 aprendices. */
    private JList<Empleado> crearListaAprendices(List<Empleado> aprendices,
                                                 List<Integer> idsPreseleccion) {
        DefaultListModel<Empleado> model = new DefaultListModel<>();
        int[] indicesSel = new int[idsPreseleccion != null ? idsPreseleccion.size() : 0];
        int seleccionadosCount = 0;
        for (int i = 0; i < aprendices.size(); i++) {
            Empleado a = aprendices.get(i);
            model.addElement(a);
            if (idsPreseleccion != null && idsPreseleccion.contains(a.getIdentificador())) {
                if (seleccionadosCount < indicesSel.length) {
                    indicesSel[seleccionadosCount++] = i;
                }
            }
        }
        JList<Empleado> lista = new JList<>(model);
        lista.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        lista.setVisibleRowCount(3);
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        // Recortar al número real de coincidencias para evitar índices vacíos
        if (seleccionadosCount > 0) {
            int[] reales = new int[seleccionadosCount];
            System.arraycopy(indicesSel, 0, reales, 0, seleccionadosCount);
            lista.setSelectedIndices(reales);
        }
        return lista;
    }

    /**
     * Diálogo de creación de una nueva cita. Permite asignar de 0 a 2
     * aprendices a la cita.
     */
    private void dialogoNuevaCita(boolean reabrirSoloMias) {
        if (citasControlador == null) citasControlador = new CitasControlador();
        if (clientesControlador == null) clientesControlador = new ClientesControlador();

        try {
            ClienteDAO cDao = new ClienteDAO();
            TrajeDAO trajeDAO = new TrajeDAO();
            TallerDAO tallerDAO = new TallerDAO();
            EmpleadoDAO empDAO = new EmpleadoDAO();

            List<Cliente> clientes = cDao.listarTodos();
            List<Taller> talleres = tallerDAO.listarTodos();
            List<Empleado> empleados = empDAO.listarTodos();

            if (clientes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay clientes registrados.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (talleres.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay talleres registrados.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JComboBox<Cliente> cmbCliente = new JComboBox<>(clientes.toArray(new Cliente[0]));
            JComboBox<Taller> cmbTaller = new JComboBox<>(talleres.toArray(new Taller[0]));
            JComboBox<Object> cmbTraje = new JComboBox<>();

            Runnable actualizarTrajes = () -> {
                cmbTraje.removeAllItems();
                Cliente sel = (Cliente) cmbCliente.getSelectedItem();
                if (sel != null) {
                    try {
                        for (Traje t : trajeDAO.listarPorCliente(sel.getNumeroCliente())) {
                            cmbTraje.addItem(t);
                        }
                    } catch (SQLException ex) { /* ignorar */ }
                }
                cmbTraje.addItem("[ + Nuevo traje ]");
            };
            actualizarTrajes.run();
            cmbCliente.addActionListener(ev -> actualizarTrajes.run());

            JComboBox<Empleado> cmbEmpResp = new JComboBox<>();
            for (Empleado emp : empleados) {
                if (emp.puedeCrearCitas()) cmbEmpResp.addItem(emp);
            }

            // Lista de aprendices (0..2)
            List<Empleado> aprendices = new ArrayList<>();
            for (Empleado emp : empleados) {
                if (emp.getCategoria() == Categoria.APRENDIZ) aprendices.add(emp);
            }
            JList<Empleado> listaAprendices = crearListaAprendices(aprendices, null);
            JScrollPane scrollAprendices = new JScrollPane(listaAprendices);
            scrollAprendices.setPreferredSize(new Dimension(220, 60));

            JTextField txtDia      = new JTextField(LocalDate.now().toString());
            JTextField txtHora     = new JTextField("10:00");
            JTextField txtDuracion = new JTextField("1");

            JPanel form = crearFormularioCompacto(
                    "Cliente",              cmbCliente,
                    "Traje",                cmbTraje,
                    "Taller",               cmbTaller,
                    "Empleado responsable", cmbEmpResp,
                    "<html>Aprendices (0-2)<br><span style='color:#888;font-size:10px;'>(Ctrl + click)</span></html>",
                    scrollAprendices,
                    "Día (YYYY-MM-DD)",     txtDia,
                    "Hora (HH:MM)",         txtHora,
                    "Duración (horas)",     txtDuracion);

            int res = JOptionPane.showConfirmDialog(this, form,
                    "Nueva cita", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res != JOptionPane.OK_OPTION) return;

            Cliente clienteSelec = (Cliente) cmbCliente.getSelectedItem();
            Taller tallerSelec = (Taller) cmbTaller.getSelectedItem();
            Empleado empSelec = (Empleado) cmbEmpResp.getSelectedItem();

            if (clienteSelec == null || tallerSelec == null || empSelec == null) {
                JOptionPane.showMessageDialog(this, "Debes seleccionar cliente, taller y empleado.",
                        "Datos no válidos", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int idTraje;
            Object trajeSelec = cmbTraje.getSelectedItem();
            if ("[ + Nuevo traje ]".equals(trajeSelec)) {
                JTextField txtNombreTraje = new JTextField("Traje nuevo");
                JComboBox<Traje.Estado> cmbEstadoTraje = new JComboBox<>(Traje.Estado.values());
                JPanel formTraje = new JPanel(new GridLayout(0, 2, 8, 8));
                formTraje.add(new JLabel("Nombre del traje:"));  formTraje.add(txtNombreTraje);
                formTraje.add(new JLabel("Estado inicial:"));    formTraje.add(cmbEstadoTraje);
                int resT = JOptionPane.showConfirmDialog(this, formTraje,
                        "Crear nuevo traje para " + clienteSelec.getNombre(),
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (resT != JOptionPane.OK_OPTION) return;
                Traje nuevoTraje = new Traje(0, clienteSelec.getNumeroCliente(),
                        txtNombreTraje.getText().trim(),
                        (Traje.Estado) cmbEstadoTraje.getSelectedItem());
                idTraje = trajeDAO.crear(nuevoTraje);
                if (idTraje == -1) {
                    JOptionPane.showMessageDialog(this,
                            "No se ha podido crear el traje. Inténtalo de nuevo.",
                            "Crear traje", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else if (trajeSelec instanceof Traje) {
                idTraje = ((Traje) trajeSelec).getIdentificador();
            } else {
                JOptionPane.showMessageDialog(this,
                        "El cliente no tiene trajes. Selecciona '[ + Nuevo traje ]'.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validar selección de aprendices (0..2)
            List<Empleado> aprendicesSel = listaAprendices.getSelectedValuesList();
            if (aprendicesSel.size() > Cita.MAX_APRENDICES) {
                JOptionPane.showMessageDialog(this,
                        "Como máximo " + Cita.MAX_APRENDICES + " aprendices por cita.",
                        "Datos no válidos", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate dia  = LocalDate.parse(txtDia.getText().trim());
            LocalTime hora = LocalTime.parse(txtHora.getText().trim());
            int duracion   = Integer.parseInt(txtDuracion.getText().trim());

            Cita cita = CitasControlador.construir(
                    clienteSelec.getNumeroCliente(), idTraje,
                    empSelec.getIdentificador(), tallerSelec.getNumeroTaller(),
                    dia, hora, duracion);
            // Aprendices (0..2)
            List<Integer> idsAprendices = new ArrayList<>();
            for (Empleado a : aprendicesSel) idsAprendices.add(a.getIdentificador());
            cita.setIdsAprendices(idsAprendices);

            citasControlador.crearCita(cita);
            JOptionPane.showMessageDialog(this,
                    "Cita creada correctamente.",
                    "Nueva cita", JOptionPane.INFORMATION_MESSAGE);
            // Refresco automático de la tabla
            mostrarPanel(panelCitas(reabrirSoloMias));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "La duración debe ser un número entero.",
                    "Datos no válidos", JOptionPane.ERROR_MESSAGE);
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha u hora incorrecto.",
                    "Datos no válidos", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar la cita: " + ex.getMessage(),
                    "Error BBDD", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No permitido", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Diálogo de edición de una cita ya existente. */
    private void dialogoEditarCita(Cita cita, boolean reabrirSoloMias) {
        if (citasControlador == null) citasControlador = new CitasControlador();

        try {
            ClienteDAO cDao = new ClienteDAO();
            TrajeDAO trajeDAO = new TrajeDAO();
            TallerDAO tallerDAO = new TallerDAO();
            EmpleadoDAO empDAO = new EmpleadoDAO();

            // Recargar la cita (para tener los aprendices actuales).
            CitaDAO citaDAO = new CitaDAO();
            Cita cargada = citaDAO.buscarPorId(cita.getIdentificador());
            if (cargada == null) {
                JOptionPane.showMessageDialog(this, "La cita ya no existe.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                mostrarPanel(panelCitas(reabrirSoloMias));
                return;
            }

            List<Cliente> clientes = cDao.listarTodos();
            List<Taller> talleres = tallerDAO.listarTodos();
            List<Empleado> empleados = empDAO.listarTodos();

            JComboBox<Cliente> cmbCliente = new JComboBox<>(clientes.toArray(new Cliente[0]));
            for (Cliente cli : clientes) {
                if (cli.getNumeroCliente() == cargada.getIdCliente()) {
                    cmbCliente.setSelectedItem(cli);
                    break;
                }
            }
            JComboBox<Taller> cmbTaller = new JComboBox<>(talleres.toArray(new Taller[0]));
            for (Taller t : talleres) {
                if (t.getNumeroTaller() == cargada.getIdTaller()) {
                    cmbTaller.setSelectedItem(t);
                    break;
                }
            }

            JComboBox<Object> cmbTraje = new JComboBox<>();
            Runnable actualizarTrajes = () -> {
                cmbTraje.removeAllItems();
                Cliente sel = (Cliente) cmbCliente.getSelectedItem();
                if (sel != null) {
                    try {
                        for (Traje t : trajeDAO.listarPorCliente(sel.getNumeroCliente())) {
                            cmbTraje.addItem(t);
                            if (t.getIdentificador() == cargada.getIdTraje()) {
                                cmbTraje.setSelectedItem(t);
                            }
                        }
                    } catch (SQLException ex) { /* ignorar */ }
                }
                cmbTraje.addItem("[ + Nuevo traje ]");
            };
            actualizarTrajes.run();
            cmbCliente.addActionListener(ev -> actualizarTrajes.run());

            JComboBox<Empleado> cmbEmpResp = new JComboBox<>();
            for (Empleado emp : empleados) {
                if (emp.puedeCrearCitas()) cmbEmpResp.addItem(emp);
            }
            for (int i = 0; i < cmbEmpResp.getItemCount(); i++) {
                if (cmbEmpResp.getItemAt(i).getIdentificador() == cargada.getIdEmpleadoResponsable()) {
                    cmbEmpResp.setSelectedIndex(i);
                    break;
                }
            }

            // Aprendices ya asignados
            List<Empleado> aprendices = new ArrayList<>();
            for (Empleado emp : empleados) {
                if (emp.getCategoria() == Categoria.APRENDIZ) aprendices.add(emp);
            }
            JList<Empleado> listaAprendices = crearListaAprendices(
                    aprendices, cargada.getIdsAprendices());
            JScrollPane scrollAprendices = new JScrollPane(listaAprendices);
            scrollAprendices.setPreferredSize(new Dimension(220, 60));

            JTextField txtDia      = new JTextField(cargada.getDia().toString());
            JTextField txtHora     = new JTextField(cargada.getHora().toString());
            JTextField txtDuracion = new JTextField(String.valueOf(cargada.getDuracion()));

            JPanel form = crearFormularioCompacto(
                    "Cliente",              cmbCliente,
                    "Traje",                cmbTraje,
                    "Taller",               cmbTaller,
                    "Empleado responsable", cmbEmpResp,
                    "<html>Aprendices (0-2)<br><span style='color:#888;font-size:10px;'>(Ctrl + click)</span></html>",
                    scrollAprendices,
                    "Día (YYYY-MM-DD)",     txtDia,
                    "Hora (HH:MM)",         txtHora,
                    "Duración (horas)",     txtDuracion);

            int res = JOptionPane.showConfirmDialog(this, form,
                    "Editar cita #" + cargada.getIdentificador(),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res != JOptionPane.OK_OPTION) return;

            Cliente clienteSelec = (Cliente) cmbCliente.getSelectedItem();
            Taller tallerSelec = (Taller) cmbTaller.getSelectedItem();
            Empleado empSelec = (Empleado) cmbEmpResp.getSelectedItem();
            if (clienteSelec == null || tallerSelec == null || empSelec == null) {
                JOptionPane.showMessageDialog(this, "Debes seleccionar cliente, taller y empleado.",
                        "Datos no válidos", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int idTraje;
            Object trajeSelec = cmbTraje.getSelectedItem();
            if ("[ + Nuevo traje ]".equals(trajeSelec)) {
                JTextField txtNombreTraje = new JTextField("Traje nuevo");
                JComboBox<Traje.Estado> cmbEstadoTraje = new JComboBox<>(Traje.Estado.values());
                JPanel formTraje = new JPanel(new GridLayout(0, 2, 8, 8));
                formTraje.add(new JLabel("Nombre del traje:"));  formTraje.add(txtNombreTraje);
                formTraje.add(new JLabel("Estado inicial:"));    formTraje.add(cmbEstadoTraje);
                int resT = JOptionPane.showConfirmDialog(this, formTraje,
                        "Crear nuevo traje para " + clienteSelec.getNombre(),
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (resT != JOptionPane.OK_OPTION) return;
                Traje nuevoTraje = new Traje(0, clienteSelec.getNumeroCliente(),
                        txtNombreTraje.getText().trim(),
                        (Traje.Estado) cmbEstadoTraje.getSelectedItem());
                idTraje = trajeDAO.crear(nuevoTraje);
                if (idTraje == -1) {
                    JOptionPane.showMessageDialog(this,
                            "No se ha podido crear el traje. Inténtalo de nuevo.",
                            "Crear traje", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else if (trajeSelec instanceof Traje) {
                idTraje = ((Traje) trajeSelec).getIdentificador();
            } else {
                JOptionPane.showMessageDialog(this,
                        "El cliente no tiene trajes.",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<Empleado> aprendicesSel = listaAprendices.getSelectedValuesList();
            if (aprendicesSel.size() > Cita.MAX_APRENDICES) {
                JOptionPane.showMessageDialog(this,
                        "Como máximo " + Cita.MAX_APRENDICES + " aprendices por cita.",
                        "Datos no válidos", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate dia  = LocalDate.parse(txtDia.getText().trim());
            LocalTime hora = LocalTime.parse(txtHora.getText().trim());
            int duracion   = Integer.parseInt(txtDuracion.getText().trim());

            Cita actualizada = new Cita(cargada.getIdentificador(), dia, hora, duracion,
                    clienteSelec.getNumeroCliente(), idTraje,
                    empSelec.getIdentificador(), tallerSelec.getNumeroTaller());
            List<Integer> idsAprendices = new ArrayList<>();
            for (Empleado a : aprendicesSel) idsAprendices.add(a.getIdentificador());
            actualizada.setIdsAprendices(idsAprendices);

            boolean ok = citasControlador.modificarCita(actualizada);
            JOptionPane.showMessageDialog(this,
                    ok ? "Cita modificada correctamente." : "No se ha modificado ninguna fila.",
                    "Editar cita",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            mostrarPanel(panelCitas(reabrirSoloMias));

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "La duración debe ser un número entero.",
                    "Datos no válidos", JOptionPane.ERROR_MESSAGE);
        } catch (java.time.format.DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Formato de fecha u hora incorrecto.",
                    "Datos no válidos", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al modificar la cita: " + ex.getMessage(),
                    "Error BBDD", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No permitido", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Diálogo de borrado de la cita seleccionada en la tabla. */
    private void dialogoBorrarCita(Cita cita, boolean reabrirSoloMias) {
        if (citasControlador == null) citasControlador = new CitasControlador();
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres borrar la cita del "
                        + cita.getDia() + " a las " + cita.getHora() + "?",
                "Confirmar borrado", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            boolean ok = citasControlador.borrarCita(cita.getIdentificador());
            JOptionPane.showMessageDialog(this,
                    ok ? "Cita borrada." : "No se ha borrado ninguna fila.",
                    "Borrar cita",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            mostrarPanel(panelCitas(reabrirSoloMias));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, MensajesError.traducir(ex),
                    "No se ha podido completar la operación", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No permitido", JOptionPane.WARNING_MESSAGE);
        }
    }

    // =====================================================================
    // DIALOGOS DE TALLERES
    // =====================================================================

    private void dialogoNuevoTaller() {
        if (talleresControlador == null) talleresControlador = new TalleresControlador();

        JTextField txtNombre = new JTextField();
        JComboBox<Taller.TipoSala> cmbTipo = new JComboBox<>(Taller.TipoSala.values());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Nombre de sala:"));  form.add(txtNombre);
        form.add(new JLabel("Tipo de sala:"));    form.add(cmbTipo);

        int res = JOptionPane.showConfirmDialog(this, form,
                "Nuevo taller", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            Taller t = new Taller(0, txtNombre.getText().trim(),
                    (Taller.TipoSala) cmbTipo.getSelectedItem());
            talleresControlador.crearTaller(t);
            JOptionPane.showMessageDialog(this, "Taller creado correctamente.",
                    "Nuevo taller", JOptionPane.INFORMATION_MESSAGE);
            mostrarPanel(panelTalleres());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el taller: " + ex.getMessage(),
                    "Error BBDD", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No permitido", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void dialogoEditarTaller(Taller selec) {
        if (talleresControlador == null) talleresControlador = new TalleresControlador();

        JTextField txtNombre = new JTextField(selec.getNombreSala());
        JComboBox<Taller.TipoSala> cmbTipo = new JComboBox<>(Taller.TipoSala.values());
        cmbTipo.setSelectedItem(selec.getTipoSala());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Nuevo nombre:"));   form.add(txtNombre);
        form.add(new JLabel("Nuevo tipo:"));     form.add(cmbTipo);

        int res = JOptionPane.showConfirmDialog(this, form,
                "Editar taller: " + selec.getNombreSala(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            Taller t = new Taller(selec.getNumeroTaller(), txtNombre.getText().trim(),
                    (Taller.TipoSala) cmbTipo.getSelectedItem());
            boolean ok = talleresControlador.modificarTaller(t);
            JOptionPane.showMessageDialog(this,
                    ok ? "Taller modificado." : "No se ha modificado ninguna fila.",
                    "Editar taller",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            if (ok) mostrarPanel(panelTalleres());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, MensajesError.traducir(ex),
                    "No se ha podido completar la operación", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No permitido", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void dialogoBorrarTaller(Taller selec) {
        if (talleresControlador == null) talleresControlador = new TalleresControlador();
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres borrar el taller '" + selec.getNombreSala() + "'?",
                "Confirmar borrado", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            boolean ok = talleresControlador.borrarTaller(selec.getNumeroTaller());
            JOptionPane.showMessageDialog(this,
                    ok ? "Taller borrado." : "No se ha borrado ninguna fila.",
                    "Borrar taller",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            if (ok) mostrarPanel(panelTalleres());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, MensajesError.traducir(ex),
                    "No se ha podido completar la operación", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No permitido", JOptionPane.WARNING_MESSAGE);
        }
    }

    // =====================================================================
    // DIALOGOS DE CLIENTES
    // =====================================================================

    private void dialogoNuevoCliente() {
        if (clientesControlador == null) clientesControlador = new ClientesControlador();

        JTextField txtNombre     = new JTextField();
        JTextField txtSuperpoder = new JTextField();
        JTextField txtColores    = new JTextField();
        JComboBox<Cliente.Tipo> cmbTipo = new JComboBox<>(Cliente.Tipo.values());
        JTextField txtNombreTraje = new JTextField("Traje principal");
        JComboBox<Traje.Estado> cmbEstadoTraje = new JComboBox<>(Traje.Estado.values());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("-- Datos del cliente --")); form.add(new JLabel(""));
        form.add(new JLabel("Nombre:"));                  form.add(txtNombre);
        form.add(new JLabel("Superpoder:"));              form.add(txtSuperpoder);
        form.add(new JLabel("Colores:"));                 form.add(txtColores);
        form.add(new JLabel("Tipo:"));                    form.add(cmbTipo);
        form.add(new JLabel("-- Traje principal --"));    form.add(new JLabel(""));
        form.add(new JLabel("Nombre del traje:"));        form.add(txtNombreTraje);
        form.add(new JLabel("Estado inicial:"));          form.add(cmbEstadoTraje);

        int res = JOptionPane.showConfirmDialog(this, form,
                "Nuevo cliente", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            Cliente c = new Cliente(0,
                    txtNombre.getText().trim(),
                    txtSuperpoder.getText().trim(),
                    txtColores.getText().trim(),
                    (Cliente.Tipo) cmbTipo.getSelectedItem());
            int idCliente = clientesControlador.crearCliente(c);

            String nomTraje = txtNombreTraje.getText().trim();
            if (nomTraje.isEmpty()) nomTraje = "Traje principal";
            Traje traje = new Traje(0, idCliente, nomTraje,
                    (Traje.Estado) cmbEstadoTraje.getSelectedItem());
            clientesControlador.crearTraje(traje);

            JOptionPane.showMessageDialog(this,
                    "Cliente '" + c.getNombre() + "' creado con su traje principal.",
                    "Nuevo cliente", JOptionPane.INFORMATION_MESSAGE);
            mostrarPanel(panelClientes());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al guardar el cliente: " + ex.getMessage(),
                    "Error BBDD", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No permitido", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void dialogoEditarCliente(Cliente selec) {
        if (clientesControlador == null) clientesControlador = new ClientesControlador();

        JTextField txtNombre     = new JTextField(selec.getNombre());
        JTextField txtSuperpoder = new JTextField(selec.getSuperpoder() != null ? selec.getSuperpoder() : "");
        JTextField txtColores    = new JTextField(selec.getColores() != null ? selec.getColores() : "");
        JComboBox<Cliente.Tipo> cmbTipoE = new JComboBox<>(Cliente.Tipo.values());
        if (selec.getTipo() != null) cmbTipoE.setSelectedItem(selec.getTipo());

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("Nombre:"));     form.add(txtNombre);
        form.add(new JLabel("Superpoder:")); form.add(txtSuperpoder);
        form.add(new JLabel("Colores:"));    form.add(txtColores);
        form.add(new JLabel("Tipo:"));       form.add(cmbTipoE);

        int res = JOptionPane.showConfirmDialog(this, form,
                "Editar cliente: " + selec.getNombre(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        try {
            Cliente c = new Cliente(selec.getNumeroCliente(),
                    txtNombre.getText().trim(),
                    txtSuperpoder.getText().trim(),
                    txtColores.getText().trim(),
                    (Cliente.Tipo) cmbTipoE.getSelectedItem());
            boolean ok = clientesControlador.modificarCliente(c);
            JOptionPane.showMessageDialog(this,
                    ok ? "Cliente modificado." : "No se ha modificado ninguna fila.",
                    "Editar cliente",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            if (ok) mostrarPanel(panelClientes());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, MensajesError.traducir(ex),
                    "No se ha podido completar la operación", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No permitido", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void dialogoBorrarCliente(Cliente selec) {
        if (clientesControlador == null) clientesControlador = new ClientesControlador();
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres borrar al cliente '" + selec.getNombre() + "'?",
                "Confirmar borrado", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            boolean ok = clientesControlador.borrarCliente(selec.getNumeroCliente());
            JOptionPane.showMessageDialog(this,
                    ok ? "Cliente borrado." : "No se ha borrado ninguna fila.",
                    "Borrar cliente",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            if (ok) mostrarPanel(panelClientes());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, MensajesError.traducir(ex),
                    "No se ha podido completar la operación", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No permitido", JOptionPane.WARNING_MESSAGE);
        }
    }

    // =====================================================================
    // PANEL DE EMPLEADOS
    // =====================================================================

    /**
     * Panel de gestión de la plantilla. Listado de empleados y botones
     * Nuevo / Editar / Borrar / Restablecer contraseña.
     * Reservado al Maestro.
     */
    private JPanel panelEmpleados() {
        if (empleadosControlador == null) empleadosControlador = new EmpleadosControlador();

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitulo = new JLabel("Empleados");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        String[] columnas = {"Apodo", "Nombre y apellidos", "Categoría"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        JLabel estado = new JLabel(" ");
        estado.setForeground(new Color(100, 100, 100));

        final List<Empleado> empleadosEnTabla = new ArrayList<>();

        try {
            List<Empleado> empleados = empleadosControlador.listarEmpleados();
            for (Empleado e : empleados) {
                empleadosEnTabla.add(e);
                modelo.addRow(new Object[]{
                        e.getApodo(),
                        e.getNombreApellidos(),
                        e.getCategoria() != null ? e.getCategoria().getTexto() : ""
                });
            }
            estado.setText("Mostrando " + empleados.size() + " empleado(s).");
        } catch (SQLException ex) {
            estado.setText(MensajesError.traducir(ex));
            estado.setForeground(new Color(180, 0, 0));
        } catch (IllegalStateException ex) {
            estado.setText(ex.getMessage());
            estado.setForeground(new Color(180, 0, 0));
        }

        JButton btnNuevo  = crearBotonAccion("Nuevo empleado");
        JButton btnEditar = crearBotonAccion("Editar empleado");
        JButton btnBorrar = crearBotonAccion("Borrar empleado");
        JButton btnReset  = crearBotonAccion("Restablecer contraseña");

        btnNuevo.addActionListener(e -> dialogoNuevoEmpleado());
        btnEditar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) { avisoSeleccion("editar", "empleado"); return; }
            dialogoEditarEmpleado(empleadosEnTabla.get(fila));
        });
        btnBorrar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) { avisoSeleccion("borrar", "empleado"); return; }
            dialogoBorrarEmpleado(empleadosEnTabla.get(fila));
        });
        btnReset.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila == -1) { avisoSeleccion("restablecer la contraseña del", "empleado"); return; }
            dialogoResetPasswordEmpleado(empleadosEnTabla.get(fila));
        });

        btnNuevo.setEnabled(permisoNuevoEmpleado);
        btnEditar.setEnabled(permisoEditarEmpleado);
        btnBorrar.setEnabled(permisoBorrarEmpleado);
        btnReset.setEnabled(permisoResetPassword);

        JPanel sur = new JPanel(new BorderLayout());
        sur.setBackground(Color.WHITE);
        JPanel botonesIzq = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
        botonesIzq.setBackground(Color.WHITE);
        botonesIzq.add(btnNuevo);
        botonesIzq.add(btnEditar);
        botonesIzq.add(btnBorrar);
        botonesIzq.add(btnReset);
        JPanel filaEstado = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        filaEstado.setBackground(Color.WHITE);
        filaEstado.add(estado);
        sur.add(botonesIzq, BorderLayout.NORTH);
        sur.add(filaEstado, BorderLayout.SOUTH);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(sur, BorderLayout.SOUTH);
        return panel;
    }

    // =====================================================================
    // DIALOGOS DE EMPLEADOS
    // =====================================================================

    /**
     * Divide "Nombre Apellido1 Apellido2" en {nombre, apellidos}.
     * Si solo hay una palabra, devuelve { palabra, "" }.
     */
    private String[] partirNombreApellidos(String texto) {
        if (texto == null) return new String[]{"", ""};
        String t = texto.trim();
        int sp = t.indexOf(' ');
        if (sp < 0) return new String[]{t, ""};
        return new String[]{t.substring(0, sp), t.substring(sp + 1).trim()};
    }

    /** Crea un combo con las tres categorías. */
    private JComboBox<Empleado.Categoria> comboCategorias(Empleado.Categoria seleccionada) {
        JComboBox<Empleado.Categoria> combo = new JComboBox<>(Empleado.Categoria.values());
        if (seleccionada != null) combo.setSelectedItem(seleccionada);
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                           boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Empleado.Categoria) {
                    setText(((Empleado.Categoria) value).getTexto());
                }
                return this;
            }
        });
        return combo;
    }

    private void dialogoNuevoEmpleado() {
        if (empleadosControlador == null) empleadosControlador = new EmpleadosControlador();

        // Los campos se crean UNA SOLA VEZ fuera del bucle. Así, si la validación
        // o el guardado fallan, al reabrir el diálogo todos los datos que el
        // usuario ya había escrito se conservan automáticamente.
        JTextField txtNombre     = new JTextField(20);
        JTextField txtApellidos  = new JTextField(20);
        JTextField txtApodo      = new JTextField(20);
        JPasswordField txtPass   = new JPasswordField(20);
        JPasswordField txtPass2  = new JPasswordField(20);
        JComboBox<Empleado.Categoria> cmbCat = comboCategorias(Empleado.Categoria.APRENDIZ);

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        form.add(new JLabel("Nombre:"));             form.add(txtNombre);
        form.add(new JLabel("Apellidos:"));          form.add(txtApellidos);
        form.add(new JLabel("Apodo (login):"));      form.add(txtApodo);
        form.add(new JLabel("Categoría:"));          form.add(cmbCat);
        form.add(new JLabel("Contraseña:"));         form.add(txtPass);
        form.add(new JLabel("Repetir contraseña:")); form.add(txtPass2);
        form.add(new JLabel(" "));
        form.add(new JLabel("<html><i>Mínimo 4 caracteres, sin espacios.</i></html>"));

        while (true) {
            int resp = JOptionPane.showConfirmDialog(this, form,
                    "Nuevo empleado", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (resp != JOptionPane.OK_OPTION) return; // Cancelado por el usuario

            // Validar que las dos contraseñas coincidan antes de invocar al controlador.
            String p1 = new String(txtPass.getPassword());
            String p2 = new String(txtPass2.getPassword());
            if (!p1.equals(p2)) {
                JOptionPane.showMessageDialog(this,
                        "Las dos contraseñas no coinciden.",
                        "No permitido", JOptionPane.WARNING_MESSAGE);
                continue; // Reabrir el diálogo con los datos rellenos
            }

            try {
                empleadosControlador.crearEmpleado(
                        txtNombre.getText(),
                        txtApellidos.getText(),
                        txtApodo.getText(),
                        (Empleado.Categoria) cmbCat.getSelectedItem(),
                        p1);
                JOptionPane.showMessageDialog(this,
                        "Empleado creado correctamente.",
                        "Nuevo empleado", JOptionPane.INFORMATION_MESSAGE);
                mostrarPanel(panelEmpleados());
                return; // Éxito: salimos del bucle
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, MensajesError.traducir(ex),
                        "No se ha podido completar la operación", JOptionPane.ERROR_MESSAGE);
                // Caemos al continue implícito del while → reabre el diálogo
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "No permitido", JOptionPane.WARNING_MESSAGE);
                // Igualmente, reabrir el diálogo con los datos rellenos
            }
        }
    }

    private void dialogoEditarEmpleado(Empleado selec) {
        if (empleadosControlador == null) empleadosControlador = new EmpleadosControlador();

        String[] partes = partirNombreApellidos(selec.getNombreApellidos());
        JTextField txtNombre    = new JTextField(partes[0], 20);
        JTextField txtApellidos = new JTextField(partes[1], 20);
        JTextField txtApodo     = new JTextField(selec.getApodo(), 20);
        JComboBox<Empleado.Categoria> cmbCat = comboCategorias(selec.getCategoria());

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        form.add(new JLabel("Nombre:"));        form.add(txtNombre);
        form.add(new JLabel("Apellidos:"));     form.add(txtApellidos);
        form.add(new JLabel("Apodo (login):")); form.add(txtApodo);
        form.add(new JLabel("Categoría:"));     form.add(cmbCat);
        form.add(new JLabel(" "));
        form.add(new JLabel("<html><i>La contraseña se cambia con<br>'Restablecer contraseña'.</i></html>"));

        // Bucle: si la edición falla por validación, mantenemos los datos
        // y reabrimos el diálogo para corregirlos sin tener que empezar de cero.
        while (true) {
            int resp = JOptionPane.showConfirmDialog(this, form,
                    "Editar empleado: " + selec.getApodo(),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (resp != JOptionPane.OK_OPTION) return;

            try {
                boolean ok = empleadosControlador.modificarEmpleado(
                        selec.getIdentificador(),
                        txtNombre.getText(),
                        txtApellidos.getText(),
                        txtApodo.getText(),
                        (Empleado.Categoria) cmbCat.getSelectedItem());
                JOptionPane.showMessageDialog(this,
                        ok ? "Empleado actualizado correctamente." : "No se ha modificado ninguna fila.",
                        "Editar empleado",
                        ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
                if (ok) mostrarPanel(panelEmpleados());
                return;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, MensajesError.traducir(ex),
                        "No se ha podido completar la operación", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "No permitido", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void dialogoBorrarEmpleado(Empleado selec) {
        if (empleadosControlador == null) empleadosControlador = new EmpleadosControlador();
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres borrar al empleado '" + selec.getApodo() + "'?\n"
              + "Esta acción no se puede deshacer.",
                "Confirmar borrado", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            boolean ok = empleadosControlador.borrarEmpleado(selec.getIdentificador());
            JOptionPane.showMessageDialog(this,
                    ok ? "Empleado borrado." : "No se ha borrado ninguna fila.",
                    "Borrar empleado",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            if (ok) mostrarPanel(panelEmpleados());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, MensajesError.traducir(ex),
                    "No se ha podido completar la operación", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No permitido", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void dialogoResetPasswordEmpleado(Empleado selec) {
        if (empleadosControlador == null) empleadosControlador = new EmpleadosControlador();

        // Pedir directamente la nueva contraseña (sin opción de valor por defecto).
        JPasswordField txtPass1 = new JPasswordField(20);
        JPasswordField txtPass2 = new JPasswordField(20);
        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        form.add(new JLabel("Nueva contraseña:"));   form.add(txtPass1);
        form.add(new JLabel("Repetir contraseña:")); form.add(txtPass2);
        form.add(new JLabel(" "));
        form.add(new JLabel("<html><i>Mínimo 4 caracteres, sin espacios.</i></html>"));

        int r = JOptionPane.showConfirmDialog(this, form,
                "Nueva contraseña para " + selec.getApodo(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (r != JOptionPane.OK_OPTION) return;

        String p1 = new String(txtPass1.getPassword());
        String p2 = new String(txtPass2.getPassword());
        if (!p1.equals(p2)) {
            JOptionPane.showMessageDialog(this,
                    "Las dos contraseñas no coinciden.",
                    "No permitido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean ok = empleadosControlador.cambiarPassword(selec.getIdentificador(), p1);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Contraseña cambiada correctamente.",
                        "Restablecer contraseña", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, MensajesError.traducir(ex),
                    "No se ha podido completar la operación", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No permitido", JOptionPane.WARNING_MESSAGE);
        }
    }

    // =====================================================================
    // DIALOGO: CAMBIAR MI PROPIA CONTRASEÑA (autoservicio para todos los roles)
    // =====================================================================

    /**
     * Diálogo accesible desde el menú "Sesión → Cambiar mi contraseña" por
     * cualquier empleado autenticado. Pide la contraseña actual (como medida
     * de seguridad) y la nueva contraseña dos veces.
     */
    private void dialogoCambiarMiPassword() {
        if (empleadosControlador == null) empleadosControlador = new EmpleadosControlador();

        // Los campos se crean fuera del bucle para conservar los datos si la
        // validación falla y el diálogo se reabre.
        JPasswordField txtActual = new JPasswordField(20);
        JPasswordField txtNueva  = new JPasswordField(20);
        JPasswordField txtNueva2 = new JPasswordField(20);

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        form.add(new JLabel("Contraseña actual:"));    form.add(txtActual);
        form.add(new JLabel("Nueva contraseña:"));     form.add(txtNueva);
        form.add(new JLabel("Repetir contraseña:"));   form.add(txtNueva2);
        form.add(new JLabel(" "));
        form.add(new JLabel("<html><i>Mínimo 4 caracteres, sin espacios.</i></html>"));

        while (true) {
            int resp = JOptionPane.showConfirmDialog(this, form,
                    "Cambiar mi contraseña — " + empleadoApodo,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (resp != JOptionPane.OK_OPTION) return;

            String actual = new String(txtActual.getPassword());
            String nueva  = new String(txtNueva.getPassword());
            String nueva2 = new String(txtNueva2.getPassword());

            if (!nueva.equals(nueva2)) {
                JOptionPane.showMessageDialog(this,
                        "Las dos contraseñas nuevas no coinciden.",
                        "No permitido", JOptionPane.WARNING_MESSAGE);
                continue;
            }

            try {
                boolean ok = empleadosControlador.cambiarMiPassword(actual, nueva);
                if (ok) {
                    JOptionPane.showMessageDialog(this,
                            "Tu contraseña se ha cambiado correctamente.",
                            "Contraseña actualizada", JOptionPane.INFORMATION_MESSAGE);
                }
                return;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, MensajesError.traducir(ex),
                        "No se ha podido completar la operación", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "No permitido", JOptionPane.WARNING_MESSAGE);
                // Limpiar solo el campo "actual" si se equivocó al introducirla,
                // dejando las nuevas tal cual para que no las tenga que reescribir.
                if (ex.getMessage() != null && ex.getMessage().contains("actual")) {
                    txtActual.setText("");
                }
            }
        }
    }

    // GETTERS
    public JMenuItem getItemVerTodasCitas()   { return itemVerTodasCitas; }
    public JMenuItem getItemMisCitas()         { return itemMisCitas; }
    public JMenuItem getItemNuevaCita()        { return itemNuevaCita; }
    public JMenuItem getItemEditarCita()       { return itemEditarCita; }
    public JMenuItem getItemBorrarCita()       { return itemBorrarCita; }
    public JButton getBotonTalleres()          { return botonTalleres; }
    public JButton getBotonClientes()          { return botonClientes; }
    public JButton getBotonEmpleados()         { return botonEmpleados; }
    public JMenuItem getItemCerrarSesion()     { return itemCerrarSesion; }
}
