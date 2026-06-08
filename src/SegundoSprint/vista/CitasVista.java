package SegundoSprint.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Vista para la gestión de citas del Taller de Edna Moda.
 * <p>
 * Se usa como <code>JPanel</code> embebido dentro de la
 * {@link VentanaPrincipal}. Muestra un listado de citas con opciones de
 * crear, editar y borrar según el rol del empleado autenticado.
 * </p>
 *
 * <p>La tabla no muestra IDs: cliente, traje, taller y empleado se listan
 * siempre por su nombre.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.1
 * @since Sprint #2
 */
public class CitasVista extends JPanel {

    private JTable tablaCitas;
    private DefaultTableModel modeloTabla;
    private JButton btnNuevaCita;
    private JButton btnEditarCita;
    private JButton btnBorrarCita;
    private JComboBox<String> cmbFiltro;
    private String categoria;

    // Sin ID, solo nombres legibles.
    private static final String[] COLUMNAS = {
        "Cliente", "Traje", "Taller", "Empleado responsable", "Dia", "Hora", "Duracion (h)"
    };

    /**
     * @param categoria Categoria del empleado: Aprendiz, Oficial o Maestro
     */
    public CitasVista(String categoria) {
        this.categoria = categoria;
        construirUI();
        aplicarPermisos();
    }

    private void construirUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Panel superior: titulo y filtro
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panelSuperior.setBackground(Color.WHITE);
        JLabel lblTitulo = new JLabel("Citas");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        cmbFiltro = new JComboBox<>(new String[]{"Todas las citas", "Mis citas"});
        panelSuperior.add(lblTitulo);
        panelSuperior.add(new JLabel("Mostrar:"));
        panelSuperior.add(cmbFiltro);

        // Tabla de citas (sin columna ID)
        modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tablaCitas = new JTable(modeloTabla);
        tablaCitas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCitas.setRowHeight(22);
        tablaCitas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        JScrollPane scroll = new JScrollPane(tablaCitas);

        // Panel inferior: botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(Color.WHITE);
        btnNuevaCita  = new JButton("Nueva cita");
        btnEditarCita = new JButton("Editar");
        btnBorrarCita = new JButton("Borrar");

        estilizarBoton(btnNuevaCita,  new Color(40, 167, 69),  Color.WHITE);
        estilizarBoton(btnEditarCita, new Color(0, 123, 255),  Color.WHITE);
        estilizarBoton(btnBorrarCita, new Color(220, 53, 69),  Color.WHITE);

        panelBotones.add(btnNuevaCita);
        panelBotones.add(btnEditarCita);
        panelBotones.add(btnBorrarCita);

        add(panelSuperior, BorderLayout.NORTH);
        add(scroll,        BorderLayout.CENTER);
        add(panelBotones,  BorderLayout.SOUTH);
    }

    private void aplicarPermisos() {
        switch (categoria) {
            case "Aprendiz":
                btnNuevaCita.setEnabled(false);
                btnEditarCita.setEnabled(false);
                btnBorrarCita.setEnabled(false);
                cmbFiltro.setSelectedItem("Mis citas");
                cmbFiltro.setEnabled(false);
                break;
            case "Oficial":
                btnBorrarCita.setEnabled(false);
                break;
            case "Maestro":
                // Acceso total
                break;
        }
    }

    private void estilizarBoton(JButton btn, Color fondo, Color texto) {
        btn.setBackground(fondo);
        btn.setForeground(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setFocusPainted(false);
    }

    /** Carga filas en la tabla. Cada Object[] debe tener 7 valores. */
    public void cargarCitas(Object[][] datos) {
        modeloTabla.setRowCount(0);
        for (Object[] fila : datos) {
            modeloTabla.addRow(fila);
        }
    }

    /** Devuelve el indice de la fila seleccionada, -1 si no hay. */
    public int getFilaSeleccionada() {
        return tablaCitas.getSelectedRow();
    }

    public JTable getTabla()                { return tablaCitas; }
    public String getFiltroSeleccionado()   { return (String) cmbFiltro.getSelectedItem(); }
    public JButton getBtnNuevaCita()        { return btnNuevaCita; }
    public JButton getBtnEditarCita()       { return btnEditarCita; }
    public JButton getBtnBorrarCita()       { return btnBorrarCita; }
    public JComboBox<String> getCmbFiltro() { return cmbFiltro; }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Demo CitasVista");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 500);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new CitasVista("Maestro"));
        frame.setVisible(true);
    }
}
