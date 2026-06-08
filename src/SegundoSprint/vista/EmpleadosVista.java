package SegundoSprint.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Vista para la gestión de la plantilla de empleados del Taller de Edna Moda.
 * <p>
 * Se usa como <code>JPanel</code> embebido dentro de la
 * {@link VentanaPrincipal} (no es una ventana independiente).
 * Solo los Maestros pueden añadir, modificar, borrar y restablecer la contraseña
 * de los empleados; los demás roles no acceden a este panel.
 * </p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.0
 * @since Sprint #4
 */
public class EmpleadosVista extends JPanel {

    private JTable tablaEmpleados;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevoEmpleado;
    private JButton btnEditarEmpleado;
    private JButton btnBorrarEmpleado;
    private JButton btnResetPassword;

    // Sin columna de ID visible: solo datos legibles.
    private static final String[] COLUMNAS = {
        "Apodo", "Nombre y apellidos", "Categoría"
    };

    /**
     * @param categoria Categoría del empleado en sesión (Aprendiz, Oficial o Maestro).
     *                  Solo Maestro habilita los botones.
     */
    public EmpleadosVista(String categoria) {
        construirUI();
        boolean esMaestro = "Maestro".equals(categoria);
        btnNuevoEmpleado.setEnabled(esMaestro);
        btnEditarEmpleado.setEnabled(esMaestro);
        btnBorrarEmpleado.setEnabled(esMaestro);
        btnResetPassword.setEnabled(esMaestro);
    }

    private void construirUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitulo = new JLabel("Empleados", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaEmpleados = new JTable(modeloTabla);
        tablaEmpleados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaEmpleados.setRowHeight(22);
        tablaEmpleados.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        JScrollPane scroll = new JScrollPane(tablaEmpleados);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        panelBotones.setBackground(Color.WHITE);
        btnNuevoEmpleado  = new JButton("Nuevo empleado");
        btnEditarEmpleado = new JButton("Editar");
        btnBorrarEmpleado = new JButton("Borrar");
        btnResetPassword  = new JButton("Restablecer contraseña");

        estilizarBoton(btnNuevoEmpleado,  new Color(40, 167, 69),   Color.WHITE);
        estilizarBoton(btnEditarEmpleado, new Color(0, 123, 255),   Color.WHITE);
        estilizarBoton(btnBorrarEmpleado, new Color(220, 53, 69),   Color.WHITE);
        estilizarBoton(btnResetPassword,  new Color(255, 153, 0),   Color.WHITE);

        panelBotones.add(btnNuevoEmpleado);
        panelBotones.add(btnEditarEmpleado);
        panelBotones.add(btnBorrarEmpleado);
        panelBotones.add(btnResetPassword);

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
     * Carga filas en la tabla. Cada Object[] debe tener 3 valores
     * (apodo, nombre y apellidos, categoría).
     */
    public void cargarEmpleados(Object[][] datos) {
        modeloTabla.setRowCount(0);
        for (Object[] fila : datos) {
            modeloTabla.addRow(fila);
        }
    }

    public int getFilaSeleccionada()        { return tablaEmpleados.getSelectedRow(); }
    public JTable getTabla()                { return tablaEmpleados; }
    public JButton getBtnNuevoEmpleado()    { return btnNuevoEmpleado; }
    public JButton getBtnEditarEmpleado()   { return btnEditarEmpleado; }
    public JButton getBtnBorrarEmpleado()   { return btnBorrarEmpleado; }
    public JButton getBtnResetPassword()    { return btnResetPassword; }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Demo EmpleadosVista");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(750, 450);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new EmpleadosVista("Maestro"));
        frame.setVisible(true);
    }
}
