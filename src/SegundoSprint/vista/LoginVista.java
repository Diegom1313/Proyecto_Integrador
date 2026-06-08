package SegundoSprint.vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Ventana de login para los empleados del taller de Edna Moda.
 * <p>
 * Permite el acceso a la aplicación introduciendo el apodo (nombre en clave)
 * y la contraseña. La validación real se delega en
 * {@link TercerSprint.control.LoginControlador}.
 * </p>
 *
 * <p>Sprint #2 — Vista; integrada con el controlador en el Sprint #3.</p>
 *
 * @author Equipo Proyecto Integrador 1ºDAW — Hijos Del Compilador
 * @version 1.1
 * @since Sprint #2
 */
public class LoginVista extends JFrame {

    private JTextField txtApodo;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblError;

    /** Construye y muestra la ventana de login con los campos vacíos. */
    public LoginVista() {
        setTitle("Taller de Edna Moda - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setIconImages(IconoApp.getIconImages());

        JPanel panelPrincipal = new JPanel(new BorderLayout());

        // Panel título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setBackground(new Color(30, 30, 30));
        JLabel lblTitulo = new JLabel("TALLER EDNA MODA", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        panelTitulo.add(lblTitulo);

        // Panel formulario
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JLabel lblApodo = new JLabel("Nombre en clave:");
        txtApodo = new JTextField(15);

        JLabel lblPass = new JLabel("Contraseña:");
        txtPassword = new JPasswordField(15);

        // Boton de login. En el LAF de Windows los JButton normales ignoran
        // setBackground/setForeground porque el LAF pinta su propio fondo.
        // Sobreescribimos paintComponent para pintar nosotros mismos el
        // rectangulo oscuro y asegurar que el texto blanco se ve.
        final Color btnNormal = new Color(30, 30, 30);
        final Color btnHover  = new Color(60, 60, 60);
        final Color btnPress  = new Color(10, 10, 10);
        btnLogin = new JButton("Entrar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = btnNormal;
                if (!isEnabled())                       bg = new Color(120, 120, 120);
                else if (getModel().isPressed())        bg = btnPress;
                else if (getModel().isRollover())       bg = btnHover;
                g2.setColor(bg);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 13));
        btnLogin.setFocusPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setOpaque(false);
        btnLogin.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(10, 10, 10), 1),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));

        lblError = new JLabel(" ", SwingConstants.CENTER);
        lblError.setForeground(Color.RED);

        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(lblApodo, gbc);
        gbc.gridx = 1;
        panelForm.add(txtApodo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(lblPass, gbc);
        gbc.gridx = 1;
        panelForm.add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panelForm.add(btnLogin, gbc);

        gbc.gridy = 3;
        panelForm.add(lblError, gbc);

        // Acción login (se conectará al controlador)
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String apodo = txtApodo.getText().trim();
                String pass = new String(txtPassword.getPassword());
                if (apodo.isEmpty() || pass.isEmpty()) {
                    lblError.setText("Por favor, rellena todos los campos.");
                } else {
                    // TODO: conectar con Controlador
                    lblError.setText("Credenciales no válidas.");
                }
            }
        });

        // Enter en password también hace login
        txtPassword.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    btnLogin.doClick();
                }
            }
        });

        panelPrincipal.add(panelTitulo, BorderLayout.NORTH);
        panelPrincipal.add(panelForm, BorderLayout.CENTER);
        add(panelPrincipal);

        setVisible(true);
    }

    /** @return el apodo introducido por el usuario, ya recortado de espacios. */
    public String getApodo() {
        return txtApodo.getText().trim();
    }

    /** @return la contraseña introducida por el usuario en claro. */
    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    /**
     * Muestra un mensaje de error en la zona inferior del formulario.
     *
     * @param mensaje texto a mostrar.
     */
    public void mostrarError(String mensaje) {
        lblError.setText(mensaje);
    }

    /** Limpia cualquier mensaje de error mostrado anteriormente. */
    public void limpiarError() {
        lblError.setText(" ");
    }

    /** @return el botón "Entrar", para que el controlador le añada listeners. */
    public JButton getBtnLogin() {
        return btnLogin;
    }

    /**
     * Punto de entrada solo para pruebas visuales aisladas de la vista.
     *
     * @param args argumentos de línea de comandos (no usados).
     */
    public static void main(String[] args) {
        new LoginVista();
    }
}
