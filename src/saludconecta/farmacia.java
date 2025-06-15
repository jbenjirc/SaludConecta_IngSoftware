

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
public class farmacia extends javax.swing.JPanel {
        Map<String, String> idpresentacion = new HashMap<>();       
       Map<String, String> idcontenido = new HashMap<>();  
    /**
     * Creates new form citamedica
     */
  

    public farmacia() {
        initComponents();
          presentacionComboBox(combopresentacion,idpresentacion);             
          contenidoComboBox(combocontenido,idcontenido);        
     listarMedicamentos();

    }
private void listarMedicamentos() {
    String[] columnNames = {"ID Medicamento", "Nombre", "Stock", "Costo", "Presentación", "Contenido"};
    DefaultTableModel model = new DefaultTableModel(columnNames, 0);

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        con = CONEXION.obtenerConexion();
        
        if (con != null) {
            String sql = "SELECT m.id_medicamento, m.med_nombre, m.med_stock, m.med_costo, " +
             "p.medPresentacion + ' Patente:' + CASE WHEN p.patente = 1 THEN 'Sí' ELSE 'No' END + " +
             "' Generico:' + CASE WHEN p.generico = 1 THEN 'Sí' ELSE 'No' END AS presentaciones, " +
             "CAST(c.med_contenido AS VARCHAR) + ' ' + c.unidad_medida AS contenidos " +
             "FROM Medicamento m " +
             "JOIN Med_Presentacion p ON m.id_medPresentacion = p.id_medPresentacion " +
             "JOIN Med_Contenido c ON m.id_medContenido = c.id_medContenido";


            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                int idMedicamento = rs.getInt("id_medicamento");
                String nombre = rs.getString("med_nombre");
                int stock = rs.getInt("med_stock");
                double costo = rs.getDouble("med_costo");
                String presentacion = rs.getString("presentaciones");
                String contenido = rs.getString("contenidos");

                Object[] row = {idMedicamento, nombre, stock, costo, presentacion, contenido};
                model.addRow(row);
            }
            
            jTable1.setModel(model);
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al listar medicamentos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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

      public static void presentacionComboBox(JComboBox<String> combopresentacion, Map<String, String> idpresentacion) {
String query = "SELECT TOP (1000) [id_medPresentacion], " +
               "[medPresentacion] + ' Patente:' + CASE WHEN [patente] = 1 THEN 'Sí' ELSE 'No' END + " +
               "' Generico:' + CASE WHEN [generico] = 1 THEN 'Sí' ELSE 'No' END AS presentaciones " +
               "FROM [CMZ_BETA].[dbo].[Med_Presentacion]";
    Connection con = CONEXION.obtenerConexion();
    
    if (con != null) {
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id_medPresentacion");
                String nombre = rs.getString("presentaciones");
                combopresentacion.addItem(nombre);
                idpresentacion.put(nombre, id);
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
      
   public static void contenidoComboBox(JComboBox<String> combocontenido, Map<String, String> idcontenido) {
String query = "SELECT TOP (1000) [id_medContenido], [med_API], CAST([med_contenido] AS VARCHAR) + ' ' + [unidad_medida] AS contenidos " +
               "FROM [CMZ_BETA].[dbo].[Med_Contenido]";
    Connection con = CONEXION.obtenerConexion();
    
    if (con != null) {
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id_medContenido");
                String nombre = rs.getString("contenidos");
                combocontenido.addItem(nombre);
                idcontenido.put(nombre, id);
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

private void registrarMedicamento() {
    String nombre = txtnombres.getText();
    int stock = Integer.parseInt(txtstock.getText());
    double costo = Double.parseDouble(txtcosto.getText());
    String selectedNpresentacion = (String) combopresentacion.getSelectedItem();
    String idpresentaciones = idpresentacion.get(selectedNpresentacion); 
    String selectedcontenido = (String) combocontenido.getSelectedItem();
    String idcontenidos = idcontenido.get(selectedcontenido);
    
    Connection con = null;
    PreparedStatement ps = null;

    try {
        con = CONEXION.obtenerConexion();
        
        if (con != null) {
            String sql = "INSERT INTO Medicamento (med_nombre, med_stock, med_costo, id_medPresentacion, id_medContenido) VALUES (?, ?, ?, ?, ?)";
            ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setInt(2, stock);
            ps.setDouble(3, costo);
            ps.setInt(4, Integer.parseInt(idpresentaciones));
            ps.setInt(5, Integer.parseInt(idcontenidos));
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Medicamento registrado con éxito.");
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al registrar medicamento: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

private void eliminarMedicamento() {
    int selectedRow = jTable1.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(null, "Por favor, seleccione un medicamento para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    int idMedicamento = (int) model.getValueAt(selectedRow, 0);

    Connection con = null;
    PreparedStatement ps = null;

    try {
        con = CONEXION.obtenerConexion();
        
        if (con != null) {
            String sql = "DELETE FROM Medicamento WHERE id_medicamento = ?";
            ps = con.prepareStatement(sql);
            ps.setInt(1, idMedicamento);
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Medicamento eliminado con éxito.");
            listarMedicamentos(); // Actualizar la lista después de eliminar
            limpiar();
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al eliminar medicamento: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


private void actualizarMedicamento() {
    int selectedRow = jTable1.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(null, "Por favor, seleccione un medicamento para actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
    int idMedicamento = (int) model.getValueAt(selectedRow, 0);
    String nombre = txtnombres.getText();
    int stock = Integer.parseInt(txtstock.getText());
    double costo = Double.parseDouble(txtcosto.getText());
    String selectedNpresentacion = (String) combopresentacion.getSelectedItem();
    String idpresentaciones = idpresentacion.get(selectedNpresentacion); 
    String selectedcontenido = (String) combocontenido.getSelectedItem();
    String idcontenidos = idcontenido.get(selectedcontenido);

    Connection con = null;
    PreparedStatement ps = null;

    try {
        con = CONEXION.obtenerConexion();
        
        if (con != null) {
            String sql = "UPDATE Medicamento SET med_nombre = ?, med_stock = ?, med_costo = ?, id_medPresentacion = ?, id_medContenido = ? WHERE id_medicamento = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setInt(2, stock);
            ps.setDouble(3, costo);
            ps.setInt(4, Integer.parseInt(idpresentaciones));
            ps.setInt(5, Integer.parseInt(idcontenidos));
            ps.setInt(6, idMedicamento);
            
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Medicamento actualizado con éxito.");
            listarMedicamentos(); // Actualizar la lista después de actualizar
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al actualizar medicamento: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
    
    txtfarmacia.setText("");    
    txtcosto.setText("");
    txtnombres.setText("");
    txtstock.setText("");
    combopresentacion.setSelectedIndex(0);    
    combocontenido.setSelectedIndex(0); // Reiniciar selección en JComboBox
 // Reiniciar selección en JComboBox
}

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtfarmacia = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        txtnombres = new javax.swing.JTextField();
        txtstock = new javax.swing.JTextField();
        combopresentacion = new javax.swing.JComboBox<>();
        combocontenido = new javax.swing.JComboBox<>();
        txtcosto = new javax.swing.JTextField();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 450));

        txtfarmacia.setBorder(javax.swing.BorderFactory.createTitledBorder("Id farmacia"));
        txtfarmacia.setEnabled(false);
        txtfarmacia.setName(""); // NOI18N

        jPanel2.setBackground(new java.awt.Color(65, 70, 76));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Registrar Usuarios");

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

        txtnombres.setBorder(javax.swing.BorderFactory.createTitledBorder("nombre"));

        txtstock.setBorder(javax.swing.BorderFactory.createTitledBorder("Stock"));

        combopresentacion.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Presentacion" }));

        combocontenido.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "contenido" }));

        txtcosto.setBorder(javax.swing.BorderFactory.createTitledBorder("Costo"));

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
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtfarmacia, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                            .addComponent(txtnombres))
                        .addGap(43, 43, 43)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtstock, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                            .addComponent(combopresentacion, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(combocontenido, 0, 301, Short.MAX_VALUE)
                            .addComponent(txtcosto))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1074, javax.swing.GroupLayout.PREFERRED_SIZE)))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(325, 325, 325)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtfarmacia, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtstock, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtcosto, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtnombres, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                            .addComponent(combopresentacion))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(combocontenido, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
String selectedNpresentacion = (String) combopresentacion.getSelectedItem();
    String idpresentaciones = idpresentacion.get(selectedNpresentacion); 
    
    String selectedcontenido = (String) combocontenido.getSelectedItem();
    String idcontenidos = idcontenido.get(selectedcontenido); 
       registrarMedicamento();
       listarMedicamentos();
       limpiar();
        if(txtnombres.equals("")){
       System.out.println("");
    }    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
int selectedRow = jTable1.getSelectedRow();
DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

if (selectedRow != -1) { 
    txtfarmacia.setText(model.getValueAt(selectedRow, 0).toString());  // Suponiendo que la columna 0 contiene el valor para txtusuarios
    txtnombres.setText(model.getValueAt(selectedRow, 1).toString());   // Suponiendo que la columna 1 contiene el valor para txtnombres
    txtstock.setText(model.getValueAt(selectedRow, 2).toString());     
    txtcosto.setText(model.getValueAt(selectedRow, 3).toString()); // Suponiendo que la columna 2 contiene el valor para txtcorreo
// Suponiendo que la columna 2 contiene el valor para txtcorreo
    
    combopresentacion.setSelectedItem(model.getValueAt(selectedRow, 4).toString());    
    combocontenido.setSelectedItem(model.getValueAt(selectedRow, 5).toString());


} else {
    JOptionPane.showMessageDialog(null, "Por favor, seleccione una fila válida.", "Error", JOptionPane.ERROR_MESSAGE);
}
       
        
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
eliminarMedicamento();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        actualizarMedicamento();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
limpiar();
     
    }//GEN-LAST:event_jButton4ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> combocontenido;
    private javax.swing.JComboBox<String> combopresentacion;
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
    private javax.swing.JTextField txtcosto;
    private javax.swing.JTextField txtfarmacia;
    private javax.swing.JTextField txtnombres;
    private javax.swing.JTextField txtstock;
    // End of variables declaration//GEN-END:variables
}
