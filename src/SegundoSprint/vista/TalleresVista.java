package SegundoSprint.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Vista para la gestión de talleres del Estudio de Edna Moda.
 * <p>
 * Se usa como <code>JPanel</code> embebido dentro de la
 * {@link VentanaPrincipal}. Solo los Maestros pueden crear, editar y borrar
 * talleres; los Oficiales solo pueden ver el listado y los Aprendices no
 * tienen acceso a esta sección.
 * </p>
 *
 * <p>La tabla no muestra IDs: se identifica cada taller por su nombre y tipo.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.1
 * @since Sprint #2
 */
public class TalleresVista extends JPanel {

    private JTable tablaTalleres;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevoTaller;
    private JButton btnEditarTaller;
    private JButton btnBorrarTaller;

    // Sin columna ID
    private static final String[] COLUMNAS = {"Nombre de sala", "Tipo de sala"};

    /**
     * @param categoria Categoria del empleado: Aprendiz, Oficial o Maestro
     */
    public TalleresVista(String categoria) {
        construirUI();

        // Solo el Maestro puede gestionar talleres
        boolean esMaestro = "Maestro".equals(categoria);
        btnNuevoTaller.setEnabled(esMaestro);
        btnEditarTaller.setEnabled(esMaestro);
        btnBorrarTaller.setEnabled(esMaestro);
    }

    private void construirUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Titulo
        JLabel lblTitulo = new JLabel("Lista de Talleres", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        // Tabla (sin columna ID)
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaTalleres = new JTable(modeloTabla);
        tablaTalleres.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaTalleres.setRowHeight(22);
        tablaTalleres.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        JScrollPane scroll = new JScrollPane(tablaTalleres);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(Color.WHITE);
        btnNuevoTaller  = new JButton("Nuevo taller");
        btnEditarTaller = new JButton("Editar");
        btnBorrarTaller = new JButton("Borrar");

        estilizarBoton(btnNuevoTaller,  new Color(40, 167, 69),   Color.WHITE);
        estilizarBoton(btnEditarTaller, new Color(0, 123, 255),   Color.WHITE);
        estilizarBoton(btnBorrarTaller, new Color(220, 53, 69),   Color.WHITE);

        panelBotones.add(btnNuevoTaller);
        panelBotones.add(btnEditarTaller);
        panelBotones.add(btnBorrarTaller);

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
     * Carga filas en la tabla. Cada Object[] debe tener 2 valores: nombre y tipo.
     */
    public void cargarTalleres(Object[][] datos) {
        modeloTabla.setRowCount(0);
        for (Object[] fila : datos) {
            modeloTabla.addRow(fila);
        }
    }

    public int getFilaSeleccionada() {
        return tablaTalleres.getSelectedRow();
    }

    public JTable getTabla()             { return tablaTalleres; }
    public JButton getBtnNuevoTaller()   { return btnNuevoTaller; }
    public JButton getBtnEditarTaller()  { return btnEditarTaller; }
    public JButton getBtnBorrarTaller()  { return btnBorrarTaller; }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Demo TalleresVista");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 450);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new TalleresVista("Maestro"));
        frame.setVisible(true);
    }
}
