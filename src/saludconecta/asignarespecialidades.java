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
import java.text.SimpleDateFormat;
/**
 *
 * @author LENOVO
 */
public class asignarespecialidades extends javax.swing.JPanel {
           Map<String, String> idempleado = new HashMap<>();             
           Map<String, String> idespecialidad = new HashMap<>();   
 


    /**
     * Creates new form citamedica
     */
  

    public asignarespecialidades() {
        initComponents();
        empleadoComboBox(comboempleado,idempleado);
        especialidadComboBox(comboespecialidad,idespecialidad);
        listarMedicos() ;
    }
    
       public static void empleadoComboBox(JComboBox<String> comboempleado, Map<String, String> idempleado) {
String query = "SELECT TOP (1000) [id_empleado],[empleado_nombre]+' '+[empleado_paterno]+' '+[empleado_materno] as nombres FROM [CMZ_BETA].[dbo].[Empleado]";
    Connection con = CONEXION.obtenerConexion();
    
    if (con != null) {
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id_empleado");
                String nombre = rs.getString("nombres");
                comboempleado.addItem(nombre);
                idempleado.put(nombre, id);
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
       
    public static void especialidadComboBox(JComboBox<String> comboespecialidad, Map<String, String> idespecialidad) {
String query = "SELECT TOP (1000) [id_especialidad],[nombre_especialidad]FROM [CMZ_BETA].[dbo].[Especialidad]";
    Connection con = CONEXION.obtenerConexion();
    
    if (con != null) {
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id_especialidad");
                String nombre = rs.getString("nombre_especialidad");
                comboespecialidad.addItem(nombre);
                idespecialidad.put(nombre, id);
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
private void listarMedicos() {
    String[] columnNames = {"Cedula", "Nombre Empleado", "Especialidad"};
    DefaultTableModel model = new DefaultTableModel(columnNames, 0);

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        con = CONEXION.obtenerConexion();
        if (con != null) {
            String sql = "SELECT m.cedula, " +
                         "e.empleado_nombre + ' ' + e.empleado_paterno + ' ' + e.empleado_materno AS nombres, " +
                         "es.nombre_especialidad " +
                         "FROM Medico m " +
                         "JOIN Empleado e ON m.id_empleado = e.id_empleado " +
                         "JOIN Especialidad es ON m.id_especialidad = es.id_especialidad";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                String cedula = rs.getString("cedula");
                String nombreEmpleado = rs.getString("nombres");
                String especialidad = rs.getString("nombre_especialidad");

                Object[] row = {cedula, nombreEmpleado, especialidad};
                model.addRow(row);
            }

            jTable1.setModel(model);
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al listar medicos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

private void registrarMedico() {
    String cedula = txtcedula.getText();
    String selectedEmpleado = (String) comboempleado.getSelectedItem();
               String idEmpleado = idempleado.get(selectedEmpleado);
    String selectedEspecialidad = (String) comboespecialidad.getSelectedItem();
               String idEspecialidad = idespecialidad.get(selectedEspecialidad);

    Connection con = null;
    PreparedStatement ps = null;

    try {
        con = CONEXION.obtenerConexion();
        if (con != null) {
            String sql = "INSERT INTO Medico (cedula, id_empleado, id_especialidad) VALUES (?, ?, ?)";
            ps = con.prepareStatement(sql);
            ps.setString(1, cedula);
            ps.setString(2, idEmpleado);
            ps.setString(3, idEspecialidad);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Medico registrado exitosamente.");
            limpiarCampos() ;
            listarMedicos();
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al registrar medico: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
 
 
private void actualizarMedico() {
    String cedula = txtcedula.getText();
    String selectedEmpleado = (String) comboempleado.getSelectedItem();
               String idEmpleados = idempleado.get(selectedEmpleado);
    String selectedEspecialidad = (String) comboespecialidad.getSelectedItem();
               String idEspecialidades = idespecialidad.get(selectedEspecialidad);

    Connection con = null;
    PreparedStatement ps = null;

    try {
        con = CONEXION.obtenerConexion();
        if (con != null) {
            String sql = "UPDATE Medico SET id_empleado = ?, id_especialidad = ? WHERE cedula = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, idEmpleados);
            ps.setString(2, idEspecialidades);
            ps.setString(3, cedula);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Medico actualizado exitosamente.");
             listarMedicos();
            limpiarCampos() ;
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al actualizar medico: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


private void eliminarMedico() {
    String cedula = txtcedula.getText();

    Connection con = null;
    PreparedStatement ps = null;

    try {
        con = CONEXION.obtenerConexion();
        if (con != null) {
            String sql = "DELETE FROM Medico WHERE cedula = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, cedula);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Medico eliminado exitosamente.");
             listarMedicos();
            limpiarCampos() ;
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al eliminar medico: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

private void limpiarCampos() {
    txtcedula.setText("");
    comboempleado.setSelectedIndex(0);
    comboespecialidad.setSelectedIndex(0);
}


    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        txtcedula = new javax.swing.JTextField();
        comboempleado = new javax.swing.JComboBox<>();
        comboespecialidad = new javax.swing.JComboBox<>();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 450));

        jPanel2.setBackground(new java.awt.Color(65, 70, 76));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Asignar especialidad al medico");

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

        jButton4.setBackground(new java.awt.Color(91, 209, 179));
        jButton4.setText("Limpiar");
        jButton4.setBorder(null);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        txtcedula.setBorder(javax.swing.BorderFactory.createTitledBorder("Cedula"));

        comboempleado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Empleado" }));

        comboespecialidad.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Especialidad" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 1092, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1074, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(325, 325, 325)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(txtcedula, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(100, 100, 100)
                        .addComponent(comboempleado, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(88, 88, 88)
                        .addComponent(comboespecialidad, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtcedula, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(comboempleado, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(comboespecialidad, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(38, 38, 38)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE)
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
registrarMedico();
        if(txtcedula.equals("")){
       System.out.println("");
    }    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
   int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) {
            // Obtener los valores de la fila seleccionada
            String cedula = jTable1.getValueAt(selectedRow, 0).toString();
            comboempleado.setSelectedItem(jTable1.getValueAt(selectedRow, 1).toString());
            comboespecialidad.setSelectedItem(jTable1.getValueAt(selectedRow, 2).toString());


            // Llenar los campos de texto
            txtcedula.setText(cedula);
    


           
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    String selectedempleado = (String) comboempleado.getSelectedItem();
    String idhempleado = idempleado.get(selectedempleado); 
    
    String selectedespecialidad = (String) comboespecialidad.getSelectedItem();
    String idespecialidades = idespecialidad.get(selectedespecialidad); 
    eliminarMedico();
  
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
      actualizarMedico();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

 limpiarCampos() ;    
    }//GEN-LAST:event_jButton4ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> comboempleado;
    private javax.swing.JComboBox<String> comboespecialidad;
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
    private javax.swing.JTextField txtcedula;
    // End of variables declaration//GEN-END:variables
}
