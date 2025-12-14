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
    private double saldo = 1000.00;

    private JLabel lblSaldo;
    private JTextArea historial;

    public BancoForm(String cliente) {
        this.cliente = cliente;

        setTitle("Operaciones Bancarias");
        setSize(600, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {

        // FUENTES PROFESIONALES
        Font tituloFont = new Font("Segoe UI", Font.BOLD, 20);
        Font textoFont = new Font("Segoe UI", Font.PLAIN, 16);
        Font botonFont = new Font("Segoe UI", Font.BOLD, 14);

        JLabel lblCliente = new JLabel("Cliente: " + cliente);
        lblCliente.setFont(tituloFont);

        lblSaldo = new JLabel("Saldo actual: $" + saldo);
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

    private void actualizarSaldo() {
        lblSaldo.setText("Saldo actual: $" + saldo);
    }

    private void agregarHistorial(String texto) {
        historial.append(texto + "\n");
    }

    private void depositar() {
        String input = JOptionPane.showInputDialog(this, "Monto a depositar:");
        if (input == null) return;
        try {
            double monto = Double.parseDouble(input);
            saldo += monto;
            agregarHistorial("Depósito: +$" + monto);
            actualizarSaldo();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Número inválido.");
        }
    }

    private void retirar() {
        String input = JOptionPane.showInputDialog(this, "Monto a retirar:");
        if (input == null) return;
        try {
            double monto = Double.parseDouble(input);
            if (monto <= saldo) {
                saldo -= monto;
                agregarHistorial("Retiro: -$" + monto);
                actualizarSaldo();
            } else {
                JOptionPane.showMessageDialog(this, "Saldo insuficiente.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Número inválido.");
        }
    }

    private void transferir() {
        String dest = JOptionPane.showInputDialog(this, "Destinatario:");
        if (dest == null) return;

        String input = JOptionPane.showInputDialog(this, "Monto a transferir:");
        if (input == null) return;

        try {
            double monto = Double.parseDouble(input);
            if (monto <= saldo) {
                saldo -= monto;
                agregarHistorial("Transferencia a " + dest + ": -$" + monto);
                JOptionPane.showMessageDialog(this, "Transferencia exitosa.");
                actualizarSaldo();
            } else {
                JOptionPane.showMessageDialog(this, "Saldo insuficiente.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Monto inválido.");
        }
    }
}
