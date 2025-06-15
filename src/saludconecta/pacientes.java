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
public class pacientes extends javax.swing.JPanel {
    private int idDireccion;

    
        PACIENTE uno=new PACIENTE();
        
    public pacientes() {
        initComponents();
        llenarTablaRelacionada();
    }
  
    
public void registrarDatos() {
    // Obtener valores de los campos de texto para la tabla Direccion
    String calle = txtcalle.getText();
    String numeroStr = txtnumero.getText();
    String colonia = txtcolonia.getText();
    String codigoPostalStr = txtpostal.getText();

    // Validar que los campos de Direccion no estén vacíos
    if (calle == null || calle.isEmpty() ||
        numeroStr == null || numeroStr.isEmpty() ||
        colonia == null || colonia.isEmpty() ||
        codigoPostalStr == null || codigoPostalStr.isEmpty()) {

        JOptionPane.showMessageDialog(null, "Todos los campos de dirección deben estar completos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Validar que los campos numéricos de Direccion sean enteros
    int numero;
    int codigoPostal;
    try {
        numero = Integer.parseInt(numeroStr);
        codigoPostal = Integer.parseInt(codigoPostalStr);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(null, "Los campos 'Número' y 'Código Postal' deben ser enteros.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Obtener valores de los campos de texto para la tabla Paciente
    String curp = txtcurp.getText();
    String nombres = txtnombres.getText();
    String paternos = txtpaternos.getText();
    String maternos = txtmaternos.getText();
    String fechaNacimiento = ((JTextField) fechadate.getDateEditor().getUiComponent()).getText();
    String edadStr = txtedad.getText();
    String telefono = txttelefono.getText();

    // Validar que los campos de Paciente no estén vacíos
    if (curp == null || curp.isEmpty() ||
        nombres == null || nombres.isEmpty() ||
        paternos == null || paternos.isEmpty() ||
        maternos == null || maternos.isEmpty() ||
        fechaNacimiento == null || fechaNacimiento.isEmpty() ||
        edadStr == null || edadStr.isEmpty() ||
        telefono == null || telefono.isEmpty()) {

        JOptionPane.showMessageDialog(null, "Todos los campos de paciente deben estar completos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Validar que el campo de edad sea un entero
    int edad;
    try {
        edad = Integer.parseInt(edadStr);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(null, "El campo 'Edad' debe ser un entero.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Insertar datos en la tabla Direccion y obtener el ID generado
    int idDireccion = -1;
    Connection con = CONEXION.obtenerConexion();
    String queryDireccion = "INSERT INTO [CMZ_BETA].[dbo].[Direccion] (calle, numero, colonia, codigoPostal) OUTPUT INSERTED.id_Direccion VALUES (?, ?, ?, ?)";

    if (con != null) {
        try (PreparedStatement ps = con.prepareStatement(queryDireccion)) {
            ps.setString(1, calle);
            ps.setInt(2, numero);
            ps.setString(3, colonia);
            ps.setInt(4, codigoPostal);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idDireccion = rs.getInt(1);
            }

        } catch (SQLException ex) {
            System.out.println("Error al insertar los datos: " + ex.toString());
            return;
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                System.out.println("Error al cerrar la conexión: " + ex.toString());
            }
        }
    }

    if (idDireccion == -1) {
        JOptionPane.showMessageDialog(null, "No se pudo obtener el ID de la dirección recién insertada.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Insertar datos en la tabla Paciente
    con = CONEXION.obtenerConexion();
    String queryPaciente = "INSERT INTO [CMZ_BETA].[dbo].[Paciente] (CURP, pac_nombre, pac_materno, pac_paterno, pac_fechaNacimiento, pac_edad, pac_tel, id_direccion,id_usuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?,?)";

    if (con != null) {
        try (PreparedStatement ps = con.prepareStatement(queryPaciente)) {
            ps.setString(1, curp);
            ps.setString(2, nombres);
            ps.setString(3, paternos);
            ps.setString(4, maternos);
            ps.setString(5, fechaNacimiento);
            ps.setInt(6, edad);
            ps.setString(7, telefono);
            ps.setInt(8, idDireccion);            
            ps.setInt(9, 38);


            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Paciente insertado exitosamente");
             llenarTablaRelacionada();
                                                          limpiar();


        } catch (SQLException ex) {
            System.out.println("Error al insertar los datos: " + ex.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                System.out.println("Error al cerrar la conexión: " + ex.toString());
            }
        }
    }
}

public void llenarTablaRelacionada() {
    String query = "SELECT p.CURP, p.pac_nombre, p.pac_materno, p.pac_paterno, p.pac_fechaNacimiento, " +
                   "p.pac_edad, p.pac_tel, p.id_usuario, d.id_Direccion, d.calle, d.numero, " +
                   "d.colonia, d.codigoPostal " +
                   "FROM [CMZ_BETA].[dbo].[Paciente] p " +
                   "JOIN [CMZ_BETA].[dbo].[Direccion] d ON p.id_direccion = d.id_Direccion ";

    Connection con = CONEXION.obtenerConexion();
    SimpleDateFormat sdfDB = new SimpleDateFormat("yyyy-MM-dd"); // Formato en el que está la fecha en la base de datos
SimpleDateFormat sdfDisplay = new SimpleDateFormat("dd-MMM-yyyy"); // Formato de presentación deseado

    if (con != null) {
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            // Crear el modelo de tabla
            DefaultTableModel model = new DefaultTableModel(new String[]{
                "CURP", "Nombre", "Apellido Materno", "Apellido Paterno", "Fecha Nacimiento",
                "Edad", "Teléfono", "ID Usuario", "ID Dirección", "Calle", "Número", "Colonia", "Código Postal"
            }, 0);

            // Llenar el modelo con los datos del ResultSet
            while (rs.next()) {
                String curp = rs.getString("CURP");
                String nombre = rs.getString("pac_nombre");
                String materno = rs.getString("pac_materno");
                String paterno = rs.getString("pac_paterno");
                Date fechaFormateada = rs.getDate("pac_fechaNacimiento");

        // Crear el formato que quieres para la fecha
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");

        // Formatear la fecha
        String fechaNacimiento = sdf.format(fechaFormateada);
                int edad = rs.getInt("pac_edad");
                String telefono = rs.getString("pac_tel");
                int idUsuario = rs.getInt("id_usuario");
                int idDireccion = rs.getInt("id_Direccion");
                String calle = rs.getString("calle");
                int numero = rs.getInt("numero");
                String colonia = rs.getString("colonia");
                int codigoPostal = rs.getInt("codigoPostal");

                model.addRow(new Object[]{curp, nombre, materno, paterno, fechaNacimiento,
                                          edad, telefono, idUsuario, idDireccion, calle, numero, colonia, codigoPostal});
            }

            // Asignar el modelo al JTable
            jTable1.setModel(model);

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
void limpiar(){
      txtcurp.setText("");
   txtnombres.setText("");
    txtpaternos.setText("");
   txtmaternos.setText("");
    fechadate.setDate(null);
   txttelefono.setText("");
  txtcalle.setText("");
 txtnumero.setText("");
 txtcolonia.setText("");
txtpostal.setText("");
txtedad.setText("");
txtpaciente.setText("");

}

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtpaciente = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        fechadate = new com.toedter.calendar.JDateChooser();
        jButton4 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        txtcurp = new javax.swing.JTextField();
        txtpaternos = new javax.swing.JTextField();
        txtedad = new javax.swing.JTextField();
        txtnombres = new javax.swing.JTextField();
        txttelefono = new javax.swing.JTextField();
        txtmaternos = new javax.swing.JTextField();
        txtcalle = new javax.swing.JTextField();
        txtnumero = new javax.swing.JTextField();
        txtcolonia = new javax.swing.JTextField();
        txtpostal = new javax.swing.JTextField();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 450));

        txtpaciente.setBorder(javax.swing.BorderFactory.createTitledBorder("Id Paciente"));
        txtpaciente.setEnabled(false);
        txtpaciente.setName(""); // NOI18N

        jPanel2.setBackground(new java.awt.Color(65, 70, 76));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Registrar Paciente");

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

        fechadate.setBorder(javax.swing.BorderFactory.createTitledBorder("Fecha Nacimiento"));

        jButton4.setBackground(new java.awt.Color(91, 209, 179));
        jButton4.setText("Limpiar");
        jButton4.setBorder(null);

        txtcurp.setBorder(javax.swing.BorderFactory.createTitledBorder("CURP"));

        txtpaternos.setBorder(javax.swing.BorderFactory.createTitledBorder("Apellido paterno"));

        txtedad.setBorder(javax.swing.BorderFactory.createTitledBorder("Edad"));

        txtnombres.setBorder(javax.swing.BorderFactory.createTitledBorder("Nombres"));

        txttelefono.setBorder(javax.swing.BorderFactory.createTitledBorder("Telefono"));

        txtmaternos.setBorder(javax.swing.BorderFactory.createTitledBorder("Apellido Materno"));

        txtcalle.setBorder(javax.swing.BorderFactory.createTitledBorder("Calle"));

        txtnumero.setBorder(javax.swing.BorderFactory.createTitledBorder("Numero"));

        txtcolonia.setBorder(javax.swing.BorderFactory.createTitledBorder("Colonia"));

        txtpostal.setBorder(javax.swing.BorderFactory.createTitledBorder("Codigo Postal"));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtpaciente, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                                    .addComponent(txtcurp)))
                            .addComponent(txtedad, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fechadate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(43, 43, 43)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtpaternos, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                            .addComponent(txtnombres)
                            .addComponent(txtmaternos)
                            .addComponent(txttelefono))
                        .addGap(41, 41, 41)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(txtnumero)
                                .addComponent(txtcalle)
                                .addComponent(txtcolonia, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtpostal))
                        .addGap(269, 269, 269))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1074, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(328, 328, 328)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 1092, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtpaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtnombres, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtcurp, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtpaternos, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtmaternos, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtedad, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(fechadate, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txttelefono)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtcalle, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtnumero, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtcolonia, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtpostal, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(11, 11, 11)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    registrarDatos(); 
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
   int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) {
            // Obtener los valores de la fila seleccionada
            String curp = jTable1.getValueAt(selectedRow, 0).toString();
            String nombre = jTable1.getValueAt(selectedRow, 1).toString();
            String materno = jTable1.getValueAt(selectedRow, 2).toString();
            String paterno = jTable1.getValueAt(selectedRow, 3).toString();
            String fechaNacimiento = jTable1.getValueAt(selectedRow, 4).toString();
            String edad = jTable1.getValueAt(selectedRow, 5).toString();
            String telefono = jTable1.getValueAt(selectedRow, 6).toString();
            String idUsuario = jTable1.getValueAt(selectedRow, 7).toString();
            String idDireccion = jTable1.getValueAt(selectedRow, 8).toString();
            String calle = jTable1.getValueAt(selectedRow, 9).toString();
            String numero = jTable1.getValueAt(selectedRow, 10).toString();
            String colonia = jTable1.getValueAt(selectedRow, 11).toString();
            String codigoPostal = jTable1.getValueAt(selectedRow, 12).toString();

            // Llenar los campos de texto
            txtcurp.setText(curp);
            txtnombres.setText(nombre);
            txtmaternos.setText(materno);
            txtpaternos.setText(paterno);
            ((JTextField)fechadate.getDateEditor().getUiComponent()).setText(fechaNacimiento);
            txtedad.setText(edad);
            txttelefono.setText(telefono);
            txtpaciente.setText(idUsuario);
            txtcalle.setText(calle);            
            txtnumero.setText(numero);
            txtcolonia.setText(colonia);
            txtpostal.setText(codigoPostal);


            // Guardar el idDireccion en una variable
            // Variable global en la clase
            this.idDireccion = Integer.parseInt(idDireccion);
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
Connection con = CONEXION.obtenerConexion();
String curp = txtcurp.getText();
int direccionId = idDireccion;
if (con != null) {
    try {
        // Verificar si el CURP existe en la tabla Paciente
        String queryCheck = "SELECT COUNT(*) FROM [CMZ_BETA].[dbo].[Paciente] WHERE CURP = ?";
        try (PreparedStatement psCheck = con.prepareStatement(queryCheck)) {
            psCheck.setString(1, curp);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // El CURP existe, proceder con la eliminación
                    // Eliminar de la tabla Paciente
                    String queryDeletePaciente = "DELETE FROM [CMZ_BETA].[dbo].[Paciente] WHERE CURP = ?";
                    try (PreparedStatement psDeletePaciente = con.prepareStatement(queryDeletePaciente)) {
                        psDeletePaciente.setString(1, curp);
                        psDeletePaciente.executeUpdate();
                    }

                    // Eliminar de la tabla Direccion
                    String queryDeleteDireccion = "DELETE FROM [CMZ_BETA].[dbo].[Direccion] WHERE id_Direccion = ?";
                    try (PreparedStatement psDeleteDireccion = con.prepareStatement(queryDeleteDireccion)) {
                        psDeleteDireccion.setInt(1, direccionId);
                        psDeleteDireccion.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(null, "Registros eliminados exitosamente.");
                                             llenarTablaRelacionada();
                                             limpiar();

                } else {
                    // El CURP no existe, mostrar un mensaje de error
                    JOptionPane.showMessageDialog(null, "El CURP no existe en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    } catch (SQLException e) {
        System.out.println("Error al eliminar los registros: " + e.toString());
    } finally {
        try {
            con.close();
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión: " + e.toString());
        }
    }
} else {
    JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
}
       
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
                                                 
    // Obtener valores de los campos
    String curp = txtcurp.getText();
    String nombre = txtnombres.getText();
    String paterno = txtpaternos.getText();
    String materno = txtmaternos.getText();
    Date fechaNacimiento = fechadate.getDate();
    
    int edad;
    try {
        edad = Integer.parseInt(txtedad.getText());
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "La edad debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    String telefono = txttelefono.getText();
    int direccionId = idDireccion;

    String calle = txtcalle.getText();
    String numeroStr = txtnumero.getText();
    String colonia = txtcolonia.getText();
    String codigoPostalStr = txtpostal.getText();
    int numero;
    int codigoPostal;

    try {
        numero = Integer.parseInt(numeroStr);
        codigoPostal = Integer.parseInt(codigoPostalStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "El número y el código postal deben ser números enteros.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Verificar que todos los campos estén completos
    if (curp.isEmpty() || nombre.isEmpty() || paterno.isEmpty() || materno.isEmpty() || fechaNacimiento == null || txtedad.getText().isEmpty() || telefono.isEmpty() ||
        calle.isEmpty() || numeroStr.isEmpty() || colonia.isEmpty() || codigoPostalStr.isEmpty()) {
    
    StringBuilder mensaje = new StringBuilder("Los siguientes campos están vacíos o incompletos:\n");

    if (curp.isEmpty()) {
        mensaje.append("- CURP\n");
    }
    if (nombre.isEmpty()) {
        mensaje.append("- Nombre\n");
    }
    if (paterno.isEmpty()) {
        mensaje.append("- Apellido Paterno\n");
    }
    if (materno.isEmpty()) {
        mensaje.append("- Apellido Materno\n");
    }
    if (fechaNacimiento == null) {
        mensaje.append("- Fecha de Nacimiento\n");
    }
    if (txtedad.getText().isEmpty()) {
        mensaje.append("- Edad\n");
    }
    if (telefono.isEmpty()) {
        mensaje.append("- Teléfono\n");
    }
    if (calle.isEmpty()) {
        mensaje.append("- Calle\n");
    }
    if (numeroStr.isEmpty()) {
        mensaje.append("- Número\n");
    }
    if (colonia.isEmpty()) {
        mensaje.append("- Colonia\n");
    }
    if (codigoPostalStr.isEmpty()) {
        mensaje.append("- Código Postal\n");
    }

    JOptionPane.showMessageDialog(null, mensaje.toString(), "Campos incompletos", JOptionPane.WARNING_MESSAGE);
    return;
}




    Connection con = CONEXION.obtenerConexion();
    
    if (con != null) {
        try {
            // Comprobar si el CURP existe en la tabla Paciente
            String queryCheck = "SELECT COUNT(*) FROM [CMZ_BETA].[dbo].[Paciente] WHERE CURP = ?";
            try (PreparedStatement psCheck = con.prepareStatement(queryCheck)) {
                psCheck.setString(1, curp);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        // El CURP existe, realizar la actualización en la tabla Paciente y Direccion
                        String queryUpdatePaciente = "UPDATE [CMZ_BETA].[dbo].[Paciente] SET pac_nombre = ?, pac_materno = ?, pac_paterno = ?, pac_fechaNacimiento = ?, pac_edad = ?, pac_tel = ?, id_direccion = ? WHERE CURP = ?";
                        try (PreparedStatement psUpdatePaciente = con.prepareStatement(queryUpdatePaciente)) {
                            psUpdatePaciente.setString(1, nombre);
                            psUpdatePaciente.setString(2, materno);
                            psUpdatePaciente.setString(3, paterno);
                            psUpdatePaciente.setDate(4, new java.sql.Date(fechaNacimiento.getTime()));
                            psUpdatePaciente.setInt(5, edad);
                            psUpdatePaciente.setString(6, telefono);
                            psUpdatePaciente.setInt(7, direccionId);
                            psUpdatePaciente.setString(8, curp);

                            psUpdatePaciente.executeUpdate();
                        }

                        String queryUpdateDireccion = "UPDATE [CMZ_BETA].[dbo].[Direccion] SET calle = ?, numero = ?, colonia = ?, codigoPostal = ? WHERE id_Direccion = ?";
                        try (PreparedStatement psUpdateDireccion = con.prepareStatement(queryUpdateDireccion)) {
                            psUpdateDireccion.setString(1, calle);
                            psUpdateDireccion.setInt(2, numero);
                            psUpdateDireccion.setString(3, colonia);
                            psUpdateDireccion.setInt(4, codigoPostal);
                            psUpdateDireccion.setInt(5, direccionId);

                            psUpdateDireccion.executeUpdate();
                        }

                        JOptionPane.showMessageDialog(null, "Datos actualizados exitosamente.");
                         llenarTablaRelacionada();
                                                                      limpiar();

                    } else {
                        // El CURP no existe, mostrar un mensaje
                        JOptionPane.showMessageDialog(null, "El CURP no existe en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al actualizar los datos: " + e.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.toString());
            }
        }
    }


    }//GEN-LAST:event_jButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser fechadate;
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
    private javax.swing.JTextField txtcalle;
    private javax.swing.JTextField txtcolonia;
    private javax.swing.JTextField txtcurp;
    private javax.swing.JTextField txtedad;
    private javax.swing.JTextField txtmaternos;
    private javax.swing.JTextField txtnombres;
    private javax.swing.JTextField txtnumero;
    private javax.swing.JTextField txtpaciente;
    private javax.swing.JTextField txtpaternos;
    private javax.swing.JTextField txtpostal;
    private javax.swing.JTextField txttelefono;
    // End of variables declaration//GEN-END:variables
}
