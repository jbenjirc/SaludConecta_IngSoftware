/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package saludconecta;

import conexion.CONEXION;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import com.toedter.calendar.JDateChooser;
import java.util.Date;
import javax.swing.JTextField;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
/**
 *
 * @author LENOVO
 */
public class medico extends javax.swing.JPanel {
    private int idDireccion;

    /**
     * Creates new form citamedica
     */
       Map<String, String> idusuario = new HashMap<>();       
       Map<String, String> iddireccion = new HashMap<>();   
       Map<String, String> idestatus = new HashMap<>();   
       Map<String, String> idhorario = new HashMap<>();   
   


    public medico() {
        initComponents();
        usuarioComboBox(combousuario,idusuario);        
        direccionComboBox(combodireccion,iddireccion);        
        horarioComboBox(combohorario,idhorario);        
        estatusComboBox(combostatus,idestatus);        
      cargarDatosEnTabla();

    }
    
  public static void usuarioComboBox(JComboBox<String> combousuario, Map<String, String> idusuario) {
    String query = "SELECT TOP (1000) [id_usuario] ,[usuario_nombre] FROM [CMZ_BETA].[dbo].[Usuario]";
    Connection con = CONEXION.obtenerConexion();
    
    if (con != null) {
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id_usuario");
                String nombre = rs.getString("usuario_nombre");
                combousuario.addItem(nombre);
                idusuario.put(nombre, id);
            }

        } catch (SQLException e) {
            System.out.println("Error al realizar la consulta: " + e.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.toString());
            }
        }
    }
}

  
  public static void direccionComboBox(JComboBox<String> combodireccion, Map<String, String> iddireccion) {
    String query = "SELECT TOP (1000) [id_Direccion],[calle]+' '+[numero]+' '+[colonia]+' '+[codigoPostal] as direcciones FROM [CMZ_BETA].[dbo].[Direccion]";
    Connection con = CONEXION.obtenerConexion();

    if (con != null) {
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id_Direccion");
                String nombre = rs.getString("direcciones");
                combodireccion.addItem(nombre);
                iddireccion.put(nombre, id);
            }

        } catch (SQLException e) {
            System.out.println("Error al realizar la consulta: " + e.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.toString());
            }
        }
    }
}

    
  public static void estatusComboBox(JComboBox<String> combostatus, Map<String, String> idestatus) {
    String query = "SELECT TOP (1000) [id_empleadoEstatus],[empleado_Estatus] FROM [CMZ_BETA].[dbo].[Empleado_Estatus]";
    Connection con = CONEXION.obtenerConexion();
    
    if (con != null) {
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id_empleadoEstatus");
                String nombre = rs.getString("empleado_Estatus");
                combostatus.addItem(nombre);
                idestatus.put(nombre, id);
            }

        } catch (SQLException e) {
            System.out.println("Error al realizar la consulta: " + e.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.toString());
            }
        }
    }
}

  
 public static void horarioComboBox(JComboBox<String> combohorario, Map<String, String> idhorario) {
    String query = "SELECT TOP (1000) [id_horario], [horario_turno] + ' ' + CONVERT(VARCHAR, [horario_inicio], 108) + ' ' + CONVERT(VARCHAR, [horario_fin], 108) AS horarios FROM [CMZ_BETA].[dbo].[Horario]";
    Connection con = CONEXION.obtenerConexion();

    if (con != null) {
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id_horario");
                String nombre = rs.getString("horarios");
                combohorario.addItem(nombre);
                idhorario.put(nombre, id);
            }

        } catch (SQLException e) {
            System.out.println("Error al realizar la consulta: " + e.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.toString());
            }
        }
    }
}

  private void registrarEmpleado() {
    String curp = txtcurp.getText();
    String nombre = txtnombres.getText();
    String paterno = txtpaternos.getText();
    String materno = txtmaternos.getText();
    Date fechaNacimiento = datenacimiento.getDate();
    String telefono = txttelefono.getText();
    String fechaInicio = txtinicio.getText();
    String fechaFin = txtfinal.getText();
    String sueldoStr = txtsueldo.getText();
    String medico = txtmedico.getText();

    String selectedNhorario = (String) combohorario.getSelectedItem();
    String idhorarios = idhorario.get(selectedNhorario); 
    
    String selectedNusuarios = (String) combousuario.getSelectedItem();
    String idusuarios = idusuario.get(selectedNusuarios); 
    
    String selectedNstatus = (String) combostatus.getSelectedItem();
    String idstatus = idestatus.get(selectedNstatus); 
    
    String selectedNdirecion = (String) combodireccion.getSelectedItem();
    String iddireciones = iddireccion.get(selectedNdirecion);



    double sueldo;
    try {
        sueldo = Double.parseDouble(sueldoStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "El sueldo debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    Connection con = null;
    PreparedStatement ps = null;

    try {
        con = CONEXION.obtenerConexion();

        if (con != null) {
            String sqlInsert = "INSERT INTO [CMZ_BETA].[dbo].[Empleado] (empleado_CURP, empleado_nombre, empleado_paterno, empleado_materno, empleado_tel, empleado_fechaNacimiento, empleado_sueldo, fechaInicio, fechaFin, id_usuario, id_horario, id_empleadoEstatus, id_direccion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sqlInsert);
            ps.setString(1, curp);
            ps.setString(2, nombre);
            ps.setString(3, paterno);
            ps.setString(4, materno);
            ps.setString(5, telefono);
            ps.setDate(6, new java.sql.Date(fechaNacimiento.getTime()));
            ps.setDouble(7, sueldo);
            ps.setString(8, fechaInicio);
            ps.setString(9, fechaFin);
            ps.setString(10, idusuarios);
            ps.setString(11, idhorarios);
            ps.setString(12, idstatus);
            ps.setString(13, iddireciones);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Empleado registrado exitosamente.");
                cargarDatosEnTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(null, "Error al registrar el empleado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al registrar el empleado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
private void cargarDatosEnTabla() {
    String[] columnNames = {"ID Empleado", "CURP", "Nombre", "Apellido Paterno", "Apellido Materno", "Teléfono", "Fecha Nacimiento", "Sueldo", "Fecha Inicio", "Fecha Fin", "Usuario", "Horario", "Estatus", "Dirección"};
    DefaultTableModel model = new DefaultTableModel(columnNames, 0);

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        // Obtener conexión a la base de datos
        con = CONEXION.obtenerConexion();

        if (con != null) {
            // Consulta SQL para obtener los datos de las tablas relacionadas
            String sql = "SELECT e.id_empleado, e.empleado_CURP, e.empleado_nombre, e.empleado_paterno, e.empleado_materno, e.empleado_tel, " +
             "e.empleado_fechaNacimiento, e.empleado_sueldo, e.fechaInicio, e.fechaFin, " +
             "u.usuario_nombre, " +
             "h.horario_turno + ' ' + CONVERT(VARCHAR, h.horario_inicio, 108) + ' ' + CONVERT(VARCHAR, h.horario_fin, 108) AS horarios, " +
             "es.empleado_Estatus, " +
             "d.calle + ' ' + d.numero + ' ' + d.colonia + ' ' + d.codigoPostal AS direcciones " +
             "FROM [CMZ_BETA].[dbo].[Empleado] e " +
             "JOIN [CMZ_BETA].[dbo].[Usuario] u ON e.id_usuario = u.id_usuario " +
             "JOIN [CMZ_BETA].[dbo].[Horario] h ON e.id_horario = h.id_horario " +
             "JOIN [CMZ_BETA].[dbo].[Empleado_Estatus] es ON e.id_empleadoEstatus = es.id_empleadoEstatus " +
             "JOIN [CMZ_BETA].[dbo].[Direccion] d ON e.id_direccion = d.id_direccion " +
             "WHERE u.id_tipoUsuario = 1";

            
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            // Recorrer el ResultSet y agregar los datos al modelo de la tabla
            while (rs.next()) {
                int idEmpleado = rs.getInt("id_empleado");
                String curp = rs.getString("empleado_CURP");
                String nombre = rs.getString("empleado_nombre");
                String paterno = rs.getString("empleado_paterno");
                String materno = rs.getString("empleado_materno");
                String telefono = rs.getString("empleado_tel");
                Date fechaNacimiento = rs.getDate("empleado_fechaNacimiento");
                double sueldo = rs.getDouble("empleado_sueldo");
                Date fechaInicio = rs.getDate("fechaInicio");
                Date fechaFin = rs.getDate("fechaFin");
                String usuarioNombre = rs.getString("usuario_nombre");
                String horario = rs.getString("horarios");
                String estatus = rs.getString("empleado_Estatus");
                String direccion = rs.getString("direcciones");

                Object[] row = {idEmpleado, curp, nombre, paterno, materno, telefono, fechaNacimiento, sueldo, fechaInicio, fechaFin, usuarioNombre, horario, estatus, direccion};
                model.addRow(row);
            }

            // Asignar el modelo al JTable
            jTable1.setModel(model);
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al cargar los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
private void limpiarCampos() {
    txtcurp.setText("");
    txtnombres.setText("");
    txtpaternos.setText("");
    txtmaternos.setText("");
    datenacimiento.setDate(null);
    txttelefono.setText("");
    txtinicio.setText("");
    txtfinal.setText("");
    txtsueldo.setText("");
    txtmedico.setText("");
}

private void actualizarEmpleado() {
    String idEmpleado = txtmedico.getText();
    String curp = txtcurp.getText();
    String nombre = txtnombres.getText();
    String paterno = txtpaternos.getText();
    String materno = txtmaternos.getText();
    Date fechaNacimiento = datenacimiento.getDate();
    String telefono = txttelefono.getText();
    String fechaInicio = txtinicio.getText();
    String fechaFin = txtfinal.getText().trim();  // trim() para asegurarse de manejar espacios en blanco

    // Si fechaFin está vacío, asignar un espacio en blanco
    if (fechaFin.isEmpty()) {
        fechaFin = " ";
    }
    String sueldoStr = txtsueldo.getText();
    String medico = txtmedico.getText();

    String selectedNhorario = (String) combohorario.getSelectedItem();
    String idhorarios = idhorario.get(selectedNhorario); 
    
    String selectedNusuarios = (String) combousuario.getSelectedItem();
    String idusuarios = idusuario.get(selectedNusuarios); 
    
    String selectedNstatus = (String) combostatus.getSelectedItem();
    String idstatus = idestatus.get(selectedNstatus); 
    
    String selectedNdirecion = (String) combodireccion.getSelectedItem();
    String iddireciones = iddireccion.get(selectedNdirecion);

    if (idEmpleado.isEmpty() || curp.isEmpty() || nombre.isEmpty() || paterno.isEmpty() || materno.isEmpty() || fechaNacimiento == null || telefono.isEmpty() ||
        fechaInicio.isEmpty() || sueldoStr.isEmpty() || medico.isEmpty() ||
        idhorarios == null || idusuarios == null || idstatus == null || iddireciones == null) {
        
        JOptionPane.showMessageDialog(null, "Todos los campos deben estar llenos.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    double sueldo;
    try {
        sueldo = Double.parseDouble(sueldoStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "El sueldo debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    java.sql.Date fechaSQL = null;
    if (fechaNacimiento != null) {
        fechaSQL = new java.sql.Date(fechaNacimiento.getTime());
    }

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        con = CONEXION.obtenerConexion();

        if (con != null) {
            // Verificar si la fecha de la cita es menor o igual a la fecha actual
            String checkSql = "SELECT cita_fecha FROM Cita WHERE id_paciente = ?";
            ps = con.prepareStatement(checkSql);
            ps.setInt(1, Integer.parseInt(idEmpleado));
            rs = ps.executeQuery();

            boolean canUpdateStatus = true;
            Date today = new Date();
            while (rs.next()) {
                Date citaFecha = rs.getDate("cita_fecha");
                if (citaFecha != null && citaFecha.after(today)) {
                    canUpdateStatus = false;
                    break;
                }
            }
            rs.close();
            ps.close();

            if (!canUpdateStatus) {
                JOptionPane.showMessageDialog(null, "El empleado tiene citas programadas en fechas futuras, no se puede cambiar el estatus.", "Error", JOptionPane.ERROR_MESSAGE);
            }

            // Construir la consulta de actualización
            StringBuilder sqlUpdate = new StringBuilder("UPDATE [CMZ_BETA].[dbo].[Empleado] SET empleado_CURP = ?, empleado_nombre = ?, empleado_paterno = ?, empleado_materno = ?, empleado_tel = ?, empleado_fechaNacimiento = ?, empleado_sueldo = ?, fechaInicio = ?, fechaFin = ?, id_usuario = ?, id_horario = ?, id_direccion = ?");
            if (canUpdateStatus) {
                sqlUpdate.append(", id_empleadoEstatus = ?");
            }
            sqlUpdate.append(" WHERE id_empleado = ?");

            ps = con.prepareStatement(sqlUpdate.toString());
            ps.setString(1, curp);
            ps.setString(2, nombre);
            ps.setString(3, paterno);
            ps.setString(4, materno);
            ps.setString(5, telefono);
            ps.setDate(6, fechaSQL);
            ps.setDouble(7, sueldo);
            ps.setString(8, fechaInicio);
            ps.setString(9, fechaFin);
            ps.setString(10, idusuarios);
            ps.setString(11, idhorarios);
            ps.setString(12, iddireciones);
            if (canUpdateStatus) {
                ps.setString(13, idstatus);
                ps.setInt(14, Integer.parseInt(idEmpleado));
            } else {
                ps.setInt(13, Integer.parseInt(idEmpleado));
            }

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Empleado actualizado exitosamente.");
                cargarDatosEnTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(null, "Error al actualizar el empleado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al actualizar el empleado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


private void eliminarEmpleado() {
    String idEmpleado = txtmedico.getText();

    if (idEmpleado.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Debe seleccionar un empleado para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    Connection con = null;
    PreparedStatement ps = null;

    try {
        con = CONEXION.obtenerConexion();

        if (con != null) {
            String sqlDelete = "DELETE FROM [CMZ_BETA].[dbo].[Empleado] WHERE id_empleado = ?";
            ps = con.prepareStatement(sqlDelete);
            ps.setInt(1, Integer.parseInt(idEmpleado));

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Empleado eliminado exitosamente.");
                cargarDatosEnTabla();
                                limpiarCampos();

            } else {
                JOptionPane.showMessageDialog(null, "Error al eliminar el empleado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al eliminar el empleado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

  void limpiar(){
      txtcurp.setText("");
   txtnombres.setText("");
    txtpaternos.setText("");
   txtmaternos.setText("");
    datenacimiento.setDate(null);
   txttelefono.setText("");
  txtinicio.setText("");
 txtfinal.setText("");
 txtsueldo.setText("");
txtmedico.setText("");

}

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtmedico = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        datenacimiento = new com.toedter.calendar.JDateChooser();
        jButton4 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        txtcurp = new javax.swing.JTextField();
        txtpaternos = new javax.swing.JTextField();
        txtnombres = new javax.swing.JTextField();
        txttelefono = new javax.swing.JTextField();
        txtmaternos = new javax.swing.JTextField();
        txtinicio = new javax.swing.JTextField();
        txtfinal = new javax.swing.JTextField();
        txtsueldo = new javax.swing.JTextField();
        combousuario = new javax.swing.JComboBox<>();
        combodireccion = new javax.swing.JComboBox<>();
        combohorario = new javax.swing.JComboBox<>();
        combostatus = new javax.swing.JComboBox<>();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 450));

        txtmedico.setBorder(javax.swing.BorderFactory.createTitledBorder("Id medico"));
        txtmedico.setEnabled(false);
        txtmedico.setName(""); // NOI18N

        jPanel2.setBackground(new java.awt.Color(65, 70, 76));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Registrar Medico");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel2)
                .addContainerGap(947, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton1.setBackground(new java.awt.Color(91, 209, 179));
        jButton1.setText("Actualizar");
        jButton1.setBorder(null);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(232, 138, 124));
        jButton2.setText("Eliminar");
        jButton2.setBorder(null);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(96, 151, 255));
        jButton3.setText("Registrar");
        jButton3.setBorder(null);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        datenacimiento.setBorder(javax.swing.BorderFactory.createTitledBorder("Fecha Nacimiento"));

        jButton4.setBackground(new java.awt.Color(91, 209, 179));
        jButton4.setText("Limpiar");
        jButton4.setBorder(null);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        txtcurp.setBorder(javax.swing.BorderFactory.createTitledBorder("CURP"));

        txtpaternos.setBorder(javax.swing.BorderFactory.createTitledBorder("Apellido paterno"));

        txtnombres.setBorder(javax.swing.BorderFactory.createTitledBorder("Nombres"));

        txttelefono.setBorder(javax.swing.BorderFactory.createTitledBorder("Telefono"));

        txtmaternos.setBorder(javax.swing.BorderFactory.createTitledBorder("Apellido Materno"));

        txtinicio.setBorder(javax.swing.BorderFactory.createTitledBorder("Fecha inicial"));

        txtfinal.setBorder(javax.swing.BorderFactory.createTitledBorder("Fecha final"));

        txtsueldo.setBorder(javax.swing.BorderFactory.createTitledBorder("Sueldo"));

        combousuario.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Usuarios" }));

        combodireccion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Direccion" }));

        combohorario.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Horario" }));

        combostatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Status" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addGap(20, 20, 20)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(txtmedico, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                                                .addComponent(txtcurp)))
                                        .addComponent(datenacimiento, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGap(20, 20, 20)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txttelefono, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(combousuario, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(43, 43, 43)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtpaternos, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                                    .addComponent(txtnombres)
                                    .addComponent(txtmaternos)
                                    .addComponent(combohorario, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(combostatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(28, 28, 28)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtfinal)
                                    .addComponent(txtinicio)
                                    .addComponent(txtsueldo, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(combodireccion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1074, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 1092, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(329, 329, 329)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txttelefono, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGap(5, 5, 5)
                            .addComponent(txtsueldo, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtinicio)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(txtfinal, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(combodireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtmedico, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtnombres, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtcurp, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtpaternos, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(datenacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(txtmaternos, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(combohorario, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(combousuario, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(combostatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 693, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    String selectedNhorario = (String) combohorario.getSelectedItem();
    String idhorarios = idhorario.get(selectedNhorario); 
    
    String selectedNusuarios = (String) combousuario.getSelectedItem();
    String idusuarios= idusuario.get(selectedNusuarios); 
    
    String selectedNstatus = (String) combostatus.getSelectedItem();
    String idstatus= idestatus.get(selectedNstatus); 
    
    String selectedNdirecion = (String) combodireccion.getSelectedItem();
    String iddireciones = iddireccion.get(selectedNdirecion); 
    
       registrarEmpleado();


    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked

    int selectedRow = jTable1.getSelectedRow();
    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

    txtmedico.setText(model.getValueAt(selectedRow, 0).toString());
    txtcurp.setText(model.getValueAt(selectedRow, 1).toString());
    txtnombres.setText(model.getValueAt(selectedRow, 2).toString());
    txtpaternos.setText(model.getValueAt(selectedRow, 3).toString());
    txtmaternos.setText(model.getValueAt(selectedRow, 4).toString());
    txttelefono.setText(model.getValueAt(selectedRow, 5).toString());

    // Convertir la cadena de fecha a un objeto Date
    try {
        String fechaStr = model.getValueAt(selectedRow, 6).toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaNacimiento = sdf.parse(fechaStr);
        datenacimiento.setDate(fechaNacimiento);
    } catch (ParseException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al convertir la fecha: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    txtsueldo.setText(model.getValueAt(selectedRow, 7).toString());
    txtinicio.setText(model.getValueAt(selectedRow, 8).toString());
    String finalValue = model.getValueAt(selectedRow, 9) != null ? model.getValueAt(selectedRow, 9).toString() : " ";
    txtfinal.setText(finalValue);
    
    // Seleccionar los valores en los comboboxes
    combousuario.setSelectedItem(model.getValueAt(selectedRow, 10).toString());
    combohorario.setSelectedItem(model.getValueAt(selectedRow, 11).toString());
    combostatus.setSelectedItem(model.getValueAt(selectedRow, 12).toString());
    combodireccion.setSelectedItem(model.getValueAt(selectedRow, 13).toString());


    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    eliminarEmpleado();
                

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    actualizarEmpleado();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
    limpiarCampos();
  // TODO add your handling code here:
    }//GEN-LAST:event_jButton4ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> combodireccion;
    private javax.swing.JComboBox<String> combohorario;
    private javax.swing.JComboBox<String> combostatus;
    private javax.swing.JComboBox<String> combousuario;
    private com.toedter.calendar.JDateChooser datenacimiento;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtcurp;
    private javax.swing.JTextField txtfinal;
    private javax.swing.JTextField txtinicio;
    private javax.swing.JTextField txtmaternos;
    private javax.swing.JTextField txtmedico;
    private javax.swing.JTextField txtnombres;
    private javax.swing.JTextField txtpaternos;
    private javax.swing.JTextField txtsueldo;
    private javax.swing.JTextField txttelefono;
    // End of variables declaration//GEN-END:variables
}
