package forms;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import db.Conexion;
import java.sql.*;

public class Loginform extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtClave;
    private JButton btnIngresar;

    public Loginform() {
        setTitle("Inicio de Sesión");
        setSize(320, 180);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        JLabel lblU = new JLabel("Usuario:");
        JLabel lblC = new JLabel("Contraseña:");

        txtUsuario = new JTextField(15);
        txtClave = new JPasswordField(15);
        btnIngresar = new JButton("Ingresar");

        btnIngresar.addActionListener(e -> validarLogin());

        JPanel panel = new JPanel();
        panel.add(lblU);
        panel.add(txtUsuario);
        panel.add(lblC);
        panel.add(txtClave);
        panel.add(btnIngresar);

        add(panel);
    }

    private void validarLogin() {
        String user = txtUsuario.getText();
        String pass = new String(txtClave.getPassword());
        try {
            Connection cn = Conexion.getConexion();

            String sql = "SELECT * FROM usuarios WHERE username=? AND password=? AND activo=true";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, user);
            ps.setString(2, pass);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Bienvenido " + user);
                new BancoForm(user).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Credenciales incorrectas");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error conexión BD");
        }
    }
}

