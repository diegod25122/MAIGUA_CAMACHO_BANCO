package forms;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import db.Conexion;
import java.sql.*;
import java.awt.*;

public class Loginform extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtClave;
    private JButton btnIngresar;
    private int intentosFallidos = 0;
    private static final int MAX_INTENTOS = 3;

    public Loginform() {
        setTitle("Inicio de Sesión");
        setSize(350, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        // Panel principal con mejor diseño
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Título
        JLabel lblTitulo = new JLabel("SISTEMA BANCARIO");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        // Usuario
        JLabel lblU = new JLabel("Usuario:");
        lblU.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(lblU, gbc);

        txtUsuario = new JTextField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(txtUsuario, gbc);

        // Contraseña
        JLabel lblC = new JLabel("Contraseña:");
        lblC.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(lblC, gbc);

        txtClave = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(txtClave, gbc);

        // Botón
        btnIngresar = new JButton("Ingresar");
        btnIngresar.setBackground(new Color(52, 152, 219));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setPreferredSize(new Dimension(120, 35));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnIngresar, gbc);

        btnIngresar.addActionListener(e -> validarLogin());

        // Permitir Enter para ingresar
        txtClave.addActionListener(e -> validarLogin());

        add(panel);
    }

    private void validarLogin() {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtClave.getPassword());

        // VALIDACIÓN 1: Campos vacíos
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor complete todos los campos.",
                    "Campos vacíos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // VALIDACIÓN 2: Longitud mínima
        if (user.length() < 3) {
            JOptionPane.showMessageDialog(this,
                    "El usuario debe tener al menos 3 caracteres.",
                    "Usuario inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (pass.length() < 4) {
            JOptionPane.showMessageDialog(this,
                    "La contraseña debe tener al menos 4 caracteres.",
                    "Contraseña inválida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // VALIDACIÓN 3: Caracteres válidos (solo letras y números)
        if (!user.matches("[a-zA-Z0-9]+")) {
            JOptionPane.showMessageDialog(this,
                    "El usuario solo puede contener letras y números.",
                    "Usuario inválido",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // VALIDACIÓN 4: Máximo de intentos
        if (intentosFallidos >= MAX_INTENTOS) {
            JOptionPane.showMessageDialog(this,
                    "Ha excedido el número máximo de intentos.\nLa aplicación se cerrará.",
                    "Acceso bloqueado",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(0);
            return;
        }

        // Intentar login
        realizarLogin(user, pass);
    }

    private void realizarLogin(String user, String pass) {
        try {
            Connection cn = Conexion.getConexion();

            // VALIDACIÓN 5: Conexión a BD
            if (cn == null) {
                JOptionPane.showMessageDialog(this,
                        "No se pudo conectar a la base de datos.\nIntente más tarde.",
                        "Error de conexión",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql = "SELECT * FROM usuarios WHERE username=? AND password=? AND activo=true";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Login exitoso
                intentosFallidos = 0; // Resetear intentos
                JOptionPane.showMessageDialog(this,
                        "¡Bienvenido " + user + "!",
                        "Acceso concedido",
                        JOptionPane.INFORMATION_MESSAGE);

                // Abrir formulario bancario
                new BancoForm(user).setVisible(true);
                this.dispose();

            } else {
                // Login fallido
                intentosFallidos++;
                int intentosRestantes = MAX_INTENTOS - intentosFallidos;

                if (intentosRestantes > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Usuario o contraseña incorrectos.\n" +
                                    "Intentos restantes: " + intentosRestantes,
                            "Error de autenticación",
                            JOptionPane.ERROR_MESSAGE);

                    // Limpiar campos
                    txtClave.setText("");
                    txtUsuario.requestFocus();

                } else {
                    JOptionPane.showMessageDialog(this,
                            "Ha excedido el número máximo de intentos.\nLa aplicación se cerrará.",
                            "Acceso bloqueado",
                            JOptionPane.ERROR_MESSAGE);
                    System.exit(0);
                }
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error al consultar la base de datos:\n" + e.getMessage(),
                    "Error SQL",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error inesperado:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Loginform().setVisible(true);
        });
    }
}