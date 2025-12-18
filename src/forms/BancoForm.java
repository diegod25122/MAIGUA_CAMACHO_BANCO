package forms;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import java.awt.*;

public class BancoForm extends JFrame {

    private String cliente;
    private double saldo;

    private JLabel lblSaldo;
    private JTextArea historial;

    public BancoForm(String cliente) {
        this.cliente = cliente;

        setTitle("Operaciones Bancarias");
        setSize(600, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponents();
        cargarSaldo();
    }

    private void initComponents() {

        // FUENTES PROFESIONALES
        Font tituloFont = new Font("Segoe UI", Font.BOLD, 20);
        Font textoFont = new Font("Segoe UI", Font.PLAIN, 16);
        Font botonFont = new Font("Segoe UI", Font.BOLD, 14);

        JLabel lblCliente = new JLabel("Cliente: " + cliente);
        lblCliente.setFont(tituloFont);

        lblSaldo = new JLabel("Saldo actual: $0.00");
        lblSaldo.setFont(textoFont);

        // HISTORIAL ESTILIZADO
        historial = new JTextArea();
        historial.setEditable(false);
        historial.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(historial);

        // BOTONES CON COLORES
        JButton btnDep = new JButton("Depósito");
        btnDep.setBackground(new Color(46, 204, 113));  // Verde
        btnDep.setForeground(Color.WHITE);
        btnDep.setFont(botonFont);

        JButton btnRet = new JButton("Retiro");
        btnRet.setBackground(new Color(230, 126, 34));  // Naranja
        btnRet.setForeground(Color.WHITE);
        btnRet.setFont(botonFont);

        JButton btnTrans = new JButton("Transferencia");
        btnTrans.setBackground(new Color(52, 152, 219)); // Azul
        btnTrans.setForeground(Color.WHITE);
        btnTrans.setFont(botonFont);

        JButton btnSalir = new JButton("Salir");
        btnSalir.setBackground(new Color(231, 76, 60)); // Rojo
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFont(botonFont);

        // Acciones de los botones
        btnDep.addActionListener(e -> depositar());
        btnRet.addActionListener(e -> retirar());
        btnTrans.addActionListener(e -> transferir());
        btnSalir.addActionListener(e -> System.exit(0));

        // DISEÑO CON GROUPLAYOUT
        JPanel panel = new JPanel();
        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(lblCliente)
                        .addComponent(lblSaldo)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(btnDep, 150, 150, 150)
                                        .addComponent(btnRet, 150, 150, 150)
                        )
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(btnTrans, 150, 150, 150)
                                        .addComponent(btnSalir, 150, 150, 150)
                        )
                        .addComponent(scroll, 500, 500, 500)
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(lblCliente)
                        .addComponent(lblSaldo)
                        .addGap(20)
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnDep, 40, 40, 40)
                                        .addComponent(btnRet, 40, 40, 40)
                        )
                        .addGroup(
                                layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnTrans, 40, 40, 40)
                                        .addComponent(btnSalir, 40, 40, 40)
                        )
                        .addGap(25)
                        .addComponent(scroll, 250, 250, 250)
        );

        add(panel);
    }

    private void cargarSaldo() {
        try {
            Connection cn = db.Conexion.getConexion();

            if (cn == null) {
                JOptionPane.showMessageDialog(this, "No se pudo conectar a la BD");
                return;
            }

            String sql = "SELECT saldo FROM cuentas WHERE username=?";
            PreparedStatement ps = cn.prepareStatement(sql); //Se creó un preparedStament para  tener una conexion mas segura a la base de datos
            ps.setString(1, cliente);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                saldo = rs.getDouble("saldo");
                actualizarSaldo();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actualizarSaldo() {
        lblSaldo.setText("Saldo actual: $" + String.format("%.2f", saldo));

        // Cambiar color según el saldo
        if (saldo < 500) {
            lblSaldo.setForeground(new Color(231, 76, 60)); // Rojo
        } else if (saldo < 2000) {
            lblSaldo.setForeground(new Color(230, 126, 34)); // Naranja
        } else {
            lblSaldo.setForeground(new Color(46, 204, 113)); // Verde
        }
    }

    private void agregarHistorial(String texto) {
        historial.append(texto + "\n");
    }

    // MÉTODO DEPOSITAR MEJORADO
    private void depositar() {
        String input = JOptionPane.showInputDialog(this, "Monto a depositar:");

        // Validar que no canceló
        if (input == null || input.trim().isEmpty()) return;

        try {
            double monto = Double.parseDouble(input);

            // Validación: monto debe ser positivo
            if (monto <= 0) {
                JOptionPane.showMessageDialog(this,
                        "El monto debe ser mayor a cero.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validación: máximo $10,000 por depósito
            if (monto > 10000) {
                JOptionPane.showMessageDialog(this,
                        "El monto máximo por depósito es $10,000",
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Realizar depósito
            saldo += monto;
            actualizarSaldoDB();
            agregarHistorial("✓ Depósito: +$" + String.format("%.2f", monto));
            actualizarSaldo();

            JOptionPane.showMessageDialog(this,
                    "Depósito exitoso.\nNuevo saldo: $" + String.format("%.2f", saldo),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese un número válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // MÉTODO RETIRAR MEJORADO
    private void retirar() {
        String input = JOptionPane.showInputDialog(this, "Monto a retirar:");

        if (input == null || input.trim().isEmpty()) return;

        try {
            double monto = Double.parseDouble(input);

            // Validación: monto positivo
            if (monto <= 0) {
                JOptionPane.showMessageDialog(this,
                        "El monto debe ser mayor a cero.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validación: saldo mínimo de $100
            if (saldo - monto < 100) {
                JOptionPane.showMessageDialog(this,
                        "Debe mantener un saldo mínimo de $100.\n" +
                                "Saldo actual: $" + String.format("%.2f", saldo) + "\n" +
                                "Saldo disponible para retirar: $" + String.format("%.2f", saldo - 100),
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validación: saldo suficiente
            if (monto > saldo) {
                JOptionPane.showMessageDialog(this,
                        "Saldo insuficiente.\n" +
                                "Saldo actual: $" + String.format("%.2f", saldo) + "\n" +
                                "Monto solicitado: $" + String.format("%.2f", monto),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Realizar retiro
            saldo -= monto;
            actualizarSaldoDB();
            agregarHistorial("✓ Retiro: -$" + String.format("%.2f", monto));
            actualizarSaldo();

            JOptionPane.showMessageDialog(this,
                    "Retiro exitoso.\nNuevo saldo: $" + String.format("%.2f", saldo),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese un número válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // MÉTODO TRANSFERIR MEJORADO
    private void transferir() {
        String dest = JOptionPane.showInputDialog(this, "Nombre del destinatario:");

        if (dest == null || dest.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe ingresar un destinatario válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validación: no transferir a sí mismo
        if (dest.trim().equalsIgnoreCase(cliente)) {
            JOptionPane.showMessageDialog(this,
                    "No puede transferir a su propia cuenta.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Monto a transferir:");

        if (input == null || input.trim().isEmpty()) return;

        try {
            double monto = Double.parseDouble(input);

            // Validación: monto positivo
            if (monto <= 0) {
                JOptionPane.showMessageDialog(this,
                        "El monto debe ser mayor a cero.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Calcular comisión del 2%
            double comision = monto * 0.02;
            double totalCobrar = monto + comision;

            // Validación: saldo suficiente incluyendo comisión
            if (totalCobrar > saldo) {
                JOptionPane.showMessageDialog(this,
                        "Saldo insuficiente.\n" +
                                "Monto a transferir: $" + String.format("%.2f", monto) + "\n" +
                                "Comisión (2%): $" + String.format("%.2f", comision) + "\n" +
                                "Total requerido: $" + String.format("%.2f", totalCobrar) + "\n" +
                                "Saldo actual: $" + String.format("%.2f", saldo),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Confirmar transferencia
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Confirmar transferencia?\n\n" +
                            "Destinatario: " + dest + "\n" +
                            "Monto: $" + String.format("%.2f", monto) + "\n" +
                            "Comisión: $" + String.format("%.2f", comision) + "\n" +
                            "Total a debitar: $" + String.format("%.2f", totalCobrar),
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                // Realizar transferencia
                saldo -= totalCobrar;
                actualizarSaldoDB();
                agregarHistorial("✓ Transferencia a " + dest + ": -$" + String.format("%.2f", monto));
                agregarHistorial("  Comisión: -$" + String.format("%.2f", comision));
                actualizarSaldo();

                JOptionPane.showMessageDialog(this,
                        "Transferencia exitosa.\nNuevo saldo: $" + String.format("%.2f", saldo),
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese un número válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // MÉTODO PARA ACTUALIZAR SALDO EN BASE DE DATOS
    private void actualizarSaldoDB() {
        try {
            Connection cn = db.Conexion.getConexion();

            if (cn == null) {
                JOptionPane.showMessageDialog(this,
                        "Error al conectar con la base de datos.");
                return;
            }

            String sql = "UPDATE cuentas SET saldo=? WHERE username=?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setDouble(1, saldo);
            ps.setString(2, cliente);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar saldo en la base de datos.");
        }
    }
}