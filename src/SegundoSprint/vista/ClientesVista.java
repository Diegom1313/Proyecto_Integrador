package SegundoSprint.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Vista para la gestión de clientes del Taller de Edna Moda.
 * <p>
 * Se usa como <code>JPanel</code> embebido dentro de la
 * {@link VentanaPrincipal} (no es una ventana independiente).
 * Solo los Maestros pueden añadir, modificar y borrar clientes;
 * los demás roles ven la información en modo lectura.
 * </p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.1
 * @since Sprint #2
 */
public class ClientesVista extends JPanel {

    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevoCliente;
    private JButton btnEditarCliente;
    private JButton btnBorrarCliente;
    private JButton btnVerInfo;

    // Sin columna de ID visible: solo nombres y datos legibles.
    private static final String[] COLUMNAS = {
        "Nombre superheroe/villano", "Superpoder", "Colores", "Tipo"
    };

    /**
     * @param categoria Categoria del empleado: Aprendiz, Oficial o Maestro
     */
    public ClientesVista(String categoria) {
        construirUI();

        boolean esMaestro = "Maestro".equals(categoria);
        btnNuevoCliente.setEnabled(esMaestro);
        btnEditarCliente.setEnabled(esMaestro);
        btnBorrarCliente.setEnabled(esMaestro);
    }

    private void construirUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Titulo
        JLabel lblTitulo = new JLabel("Clientes", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        // Tabla (sin columna ID)
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaClientes.setRowHeight(22);
        tablaClientes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        JScrollPane scroll = new JScrollPane(tablaClientes);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        panelBotones.setBackground(Color.WHITE);
        btnNuevoCliente  = new JButton("Nuevo cliente");
        btnEditarCliente = new JButton("Editar");
        btnBorrarCliente = new JButton("Borrar");
        btnVerInfo       = new JButton("Ver info cliente");

        estilizarBoton(btnNuevoCliente,  new Color(40, 167, 69),   Color.WHITE);
        estilizarBoton(btnEditarCliente, new Color(0, 123, 255),   Color.WHITE);
        estilizarBoton(btnBorrarCliente, new Color(220, 53, 69),   Color.WHITE);
        estilizarBoton(btnVerInfo,       new Color(255, 153, 0),   Color.WHITE);

        panelBotones.add(btnNuevoCliente);
        panelBotones.add(btnEditarCliente);
        panelBotones.add(btnBorrarCliente);
        panelBotones.add(btnVerInfo);

        add(lblTitulo,    BorderLayout.NORTH);
        add(scroll,       BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void estilizarBoton(JButton btn, Color fondo, Color texto) {
        btn.setBackground(fondo);
        btn.setForeground(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
    }

    /**
     * Carga filas en la tabla. Cada Object[] debe tener 4 valores (nombre,
     * superpoder, colores, tipo).
     */
    public void cargarClientes(Object[][] datos) {
        modeloTabla.setRowCount(0);
        for (Object[] fila : datos) {
            modeloTabla.addRow(fila);
        }
    }

    /** Devuelve el indice de la fila seleccionada, -1 si no hay. */
    public int getFilaSeleccionada() {
        return tablaClientes.getSelectedRow();
    }

    public JTable getTabla()             { return tablaClientes; }
    public JButton getBtnNuevoCliente()  { return btnNuevoCliente; }
    public JButton getBtnEditarCliente() { return btnEditarCliente; }
    public JButton getBtnBorrarCliente() { return btnBorrarCliente; }
    public JButton getBtnVerInfo()       { return btnVerInfo; }

    public static void main(String[] args) {
        // Demostracion rapida dentro de un JFrame de prueba.
        JFrame frame = new JFrame("Demo ClientesVista");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(750, 450);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new ClientesVista("Maestro"));
        frame.setVisible(true);
    }
}
