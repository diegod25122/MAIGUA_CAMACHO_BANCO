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

    // CARGA EL SALDO DESDE LA BASE DE DATOS AL INICIAR
    private void cargarSaldo() {
        try {
            // Obtener conexión a la base de datos
            Connection cn = db.Conexion.getConexion();

            if (cn == null) {
                JOptionPane.showMessageDialog(this, "No se pudo conectar a la BD");
                return;
            }

            // Consulta SQL para obtener el saldo del usuario
            String sql = "SELECT saldo FROM cuentas WHERE username=?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, cliente); // Reemplazar el ? con el nombre del cliente

            ResultSet rs = ps.executeQuery(); // Ejecutar la consulta

            // Si encuentra el usuario, obtener su saldo
            if (rs.next()) {
                saldo = rs.getDouble("saldo");
                actualizarSaldo(); // Actualizar la etiqueta visual
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ACTUALIZA LA ETIQUETA DEL SALDO Y CAMBIA EL COLOR SEGÚN EL MONTO
    private void actualizarSaldo() {
        // Mostrar saldo con 2 decimales
        lblSaldo.setText("Saldo actual: $" + String.format("%.2f", saldo));

        // Cambiar color según el saldo
        if (saldo < 500) {
            lblSaldo.setForeground(new Color(231, 76, 60)); // Rojo - saldo bajo
        } else if (saldo < 2000) {
            lblSaldo.setForeground(new Color(230, 126, 34)); // Naranja - saldo medio
        } else {
            lblSaldo.setForeground(new Color(46, 204, 113)); // Verde - saldo alto
        }
    }

    // AGREGA UN TEXTO AL ÁREA DE HISTORIAL
    private void agregarHistorial(String texto) {
        historial.append(texto + "\n");
    }

    // =============== MÉTODO DEPOSITAR ===============
    private void depositar() {
        // Pedir al usuario el monto a depositar
        String input = JOptionPane.showInputDialog(this, "Monto a depositar:");

        // Si canceló o dejó vacío, salir
        if (input == null || input.trim().isEmpty()) return;

        try {
            double monto = Double.parseDouble(input); // Convertir texto a número

            // Validación: el monto debe ser positivo
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

            // Sumar el monto al saldo actual
            saldo += monto;

            // Guardar el nuevo saldo en la base de datos
            actualizarSaldoDB();

            // Agregar al historial
            agregarHistorial("✓ Depósito: +$" + String.format("%.2f", monto));

            // Actualizar etiqueta visual
            actualizarSaldo();

            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(this,
                    "Depósito exitoso.\nNuevo saldo: $" + String.format("%.2f", saldo),
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            // Si ingresó algo que no es un número
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese un número válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // =============== MÉTODO RETIRAR ===============
    private void retirar() {
        // Pedir al usuario el monto a retirar
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

            // Validación: debe mantener un saldo mínimo de $100
            if (saldo - monto < 100) {
                JOptionPane.showMessageDialog(this,
                        "Debe mantener un saldo mínimo de $100.\n" +
                                "Saldo actual: $" + String.format("%.2f", saldo) + "\n" +
                                "Saldo disponible para retirar: $" + String.format("%.2f", saldo - 100),
                        "Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validación: debe tener saldo suficiente
            if (monto > saldo) {
                JOptionPane.showMessageDialog(this,
                        "Saldo insuficiente.\n" +
                                "Saldo actual: $" + String.format("%.2f", saldo) + "\n" +
                                "Monto solicitado: $" + String.format("%.2f", monto),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Restar el monto del saldo
            saldo -= monto;

            // Guardar en BD
            actualizarSaldoDB();

            // Agregar al historial
            agregarHistorial("✓ Retiro: -$" + String.format("%.2f", monto));

            // Actualizar visual
            actualizarSaldo();

            // Mensaje de éxito
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

    // =============== MÉTODO TRANSFERIR (CON SUMA REAL AL DESTINATARIO) ===============
    private void transferir() {
        // 1. PEDIR EL NOMBRE DEL DESTINATARIO
        String dest = JOptionPane.showInputDialog(this, "Nombre del destinatario:");

        // Validar que ingresó un nombre
        if (dest == null || dest.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe ingresar un destinatario válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        dest = dest.trim(); // Eliminar espacios al inicio y final

        // Validación: no puede transferir a sí mismo
        if (dest.equalsIgnoreCase(cliente)) {
            JOptionPane.showMessageDialog(this,
                    "No puede transferir a su propia cuenta.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. PEDIR EL MONTO A TRANSFERIR
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

            // 3. CALCULAR COMISIÓN DEL 2%
            double comision = monto * 0.02;
            double totalCobrar = monto + comision; // Total que se restará de tu cuenta

            // Validación: tener saldo suficiente (incluye comisión)
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

            // 4. VERIFICAR QUE EL DESTINATARIO EXISTA EN LA BASE DE DATOS
            Connection cn = db.Conexion.getConexion();

            if (cn == null) {
                JOptionPane.showMessageDialog(this,
                        "Error al conectar con la base de datos.");
                return;
            }

            // Buscar al destinatario en la BD
            String sqlVerificar = "SELECT username, saldo FROM cuentas WHERE username=?";
            PreparedStatement psVerificar = cn.prepareStatement(sqlVerificar);
            psVerificar.setString(1, dest);
            ResultSet rs = psVerificar.executeQuery();

            // Si no existe o está inactivo, mostrar error
            if (!rs.next()) {
                JOptionPane.showMessageDialog(this,
                        "El usuario '" + dest + "' no existe o está inactivo.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                rs.close();
                psVerificar.close();
                return;
            }

            // Obtener saldo del destinatario (aunque no lo usamos, es buena práctica verificarlo)
            double saldoDestinatario = rs.getDouble("saldo");
            rs.close();
            psVerificar.close();

            // 5. CONFIRMAR LA TRANSFERENCIA CON EL USUARIO
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Confirmar transferencia?\n\n" +
                            "Destinatario: " + dest + "\n" +
                            "Monto: $" + String.format("%.2f", monto) + "\n" +
                            "Comisión: $" + String.format("%.2f", comision) + "\n" +
                            "Total a debitar: $" + String.format("%.2f", totalCobrar),
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            // Si el usuario confirmó
            if (confirm == JOptionPane.YES_OPTION) {

                // 6. USAR TRANSACCIONES PARA ASEGURAR QUE TODO SE HAGA O NADA
                // (Si algo falla, se revierte todo automáticamente)
                cn.setAutoCommit(false); // Desactivar auto-guardado

                try {
                    // PASO A: RESTAR el dinero de tu cuenta (incluyendo comisión)
                    String sqlRestar = "UPDATE cuentas SET saldo=saldo-? WHERE username=?";
                    PreparedStatement psRestar = cn.prepareStatement(sqlRestar);
                    psRestar.setDouble(1, totalCobrar); // Total con comisión
                    psRestar.setString(2, cliente);     // Tu usuario
                    psRestar.executeUpdate();
                    psRestar.close();

                    // PASO B: SUMAR el dinero a la cuenta del destinatario (sin comisión)
                    String sqlSumar = "UPDATE cuentas SET saldo=saldo+? WHERE username=?";
                    PreparedStatement psSumar = cn.prepareStatement(sqlSumar);
                    psSumar.setDouble(1, monto);  // Solo el monto sin comisión
                    psSumar.setString(2, dest);   // Usuario destinatario
                    psSumar.executeUpdate();
                    psSumar.close();

                    // TODO SALIÓ BIEN: CONFIRMAR LOS CAMBIOS
                    cn.commit();
                    cn.setAutoCommit(true); // Reactivar auto-guardado

                    // Actualizar el saldo local (de la ventana)
                    saldo -= totalCobrar;

                    // Agregar al historial
                    agregarHistorial("✓ Transferencia a " + dest + ": -$" + String.format("%.2f", monto));
                    agregarHistorial("  Comisión: -$" + String.format("%.2f", comision));

                    // Actualizar visual
                    actualizarSaldo();

                    // Mensaje de éxito
                    JOptionPane.showMessageDialog(this,
                            "Transferencia exitosa.\n" +
                                    "Se transfirió $" + String.format("%.2f", monto) + " a " + dest + "\n" +
                                    "Nuevo saldo: $" + String.format("%.2f", saldo),
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);

                } catch (SQLException e) {
                    // SI ALGO FALLÓ: REVERTIR TODO (ROLLBACK)
                    cn.rollback();
                    cn.setAutoCommit(true);

                    JOptionPane.showMessageDialog(this,
                            "Error al procesar la transferencia.\nLa operación ha sido cancelada.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }

        } catch (NumberFormatException e) {
            // Si ingresó algo que no es número
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese un número válido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {
            // Cualquier otro error
            JOptionPane.showMessageDialog(this,
                    "Error inesperado: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // =============== ACTUALIZAR SALDO EN LA BASE DE DATOS ===============
    private void actualizarSaldoDB() {
        try {
            // Obtener conexión
            Connection cn = db.Conexion.getConexion();

            if (cn == null) {
                JOptionPane.showMessageDialog(this,
                        "Error al conectar con la base de datos.");
                return;
            }

            // Actualizar el saldo en la BD
            String sql = "UPDATE cuentas SET saldo=? WHERE username=?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setDouble(1, saldo);      // Nuevo saldo
            ps.setString(2, cliente);    // Usuario actual
            ps.executeUpdate();          // Ejecutar actualización

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error al actualizar saldo en la base de datos.");
        }
    }
}