package deber;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class Login extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txt_Pass;
    private JComboBox Cb_rol;
    private JPanel JPanel1;
    private JButton insertButton;
    private JTextField txtcodigo;
    private JTextField txtcedula;
    private JButton verificarConexionButton;
    private JTable datos;
    private JButton dButton;

    public Login() {
        super("LOGUEO");
        setContentPane(JPanel1);
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Ingreso_formulario();
            }
        });
        verificarConexionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                conexionBase();
            }
        });
        dButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDatos();
            }
        });
    }

    public void Ingreso_formulario() {
        // Obtener los datos del formulario
        String usuario = txtUsuario.getText();
        String contraseña = new String(txt_Pass.getPassword());
        String rol = (String) Cb_rol.getSelectedItem(); // Obtener el rol seleccionado
        String codigo = txtcodigo.getText(); // Obtener el código
        String cedula = txtcedula.getText(); // Obtener la cédula

        // Llamar a insertarDatos() con los datos obtenidos del formulario
        insertarDatos(usuario, contraseña, rol, codigo, cedula);
    }

    // Método para insertar datos en la base de datos
    public void insertarDatos(String nombre, String contraseña, String rol, String codigo, String cedula) {
        // Obtener la conexión a la base de datos
        Connection conexion = conexionBase();
        if (conexion != null) {
            // La conexión se ha establecido correctamente
            try {
                // Preparar la consulta SQL para insertar los datos en la tabla
                String sql = "INSERT INTO usuarios (nombre, contraseña, rol, codigo, cedula) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                    // Establecer los valores de los parámetros en la consulta
                    pstmt.setString(1, nombre);
                    pstmt.setString(2, contraseña);
                    pstmt.setString(3, rol);
                    pstmt.setString(4, codigo); // Agregar el código como parámetro
                    pstmt.setString(5, cedula); // Agregar la cédula como parámetro
                    // Ejecutar la consulta para insertar los datos
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Datos insertados correctamente.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al insertar datos en la base de datos: " + e.getMessage());
            } finally {
                try {
                    // Cerrar la conexión
                    conexion.close();
                } catch (SQLException e) {
                    // Manejar errores al cerrar la conexión
                    e.printStackTrace();
                }
            }
        }
    }

    // Método para mostrar todos los datos en la tabla "datos"
    public void mostrarDatos() {
        try {
            // Obtener la conexión a la base de datos
            Connection conexion = conexionBase();
            if (conexion != null) {
                // La conexión se ha establecido correctamente
                // Consulta SQL para obtener todos los datos
                String consulta = "SELECT * FROM datos";
                Statement statement = conexion.createStatement();
                ResultSet resultSet = statement.executeQuery(consulta);

                // Crear modelo de tabla y llenarlo con los resultados de la consulta
                DefaultTableModel modeloTabla = new DefaultTableModel();
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    modeloTabla.addColumn(metaData.getColumnLabel(columnIndex));
                }
                while (resultSet.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 0; i < columnCount; i++) {
                        row[i] = resultSet.getObject(i + 1);
                    }
                    modeloTabla.addRow(row);
                }
                datos.setModel(modeloTabla);

                // Cerrar la conexión y los recursos relacionados
                resultSet.close();
                statement.close();
                conexion.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al consultar la base de datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para establecer la conexión a la base de datos
    public Connection conexionBase() {
        // Configuración de la conexión a la base de datos
        String url = "jdbc:mysql://localhost:3305/capacitacion";
        String usuarioDB = "root";
        String contrasenaDB = "123456"; // La contraseña es "123456"
        Connection conexion = null;
        try {
            // Establecer la conexión
            conexion = DriverManager.getConnection(url, usuarioDB, contrasenaDB);
            System.out.println("Conexión establecida correctamente.");
        } catch (SQLException e) {
            // Manejo de excepciones
            System.out.println("Error al conectar con la base de datos: " + e.getMessage());
        }
        return conexion;
    }

    public static void main(String[] args) {
        JFrame frame = new Login();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static class Main {
        public static void main(String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Login frame = new Login();
                    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
                    frame.setSize(400, 300);
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);
                }
            });
        }
    }
}
