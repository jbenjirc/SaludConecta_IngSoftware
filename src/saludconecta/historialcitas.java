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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author LENOVO
 */
public class historialcitas extends javax.swing.JPanel {

    Map<String, String> idcedula = new HashMap<>();
    Map<String, String> idstatus = new HashMap<>();

    /**
     * Creates new form separar_citas
     */
    public historialcitas() {
        initComponents();
        cargarNombreCompleto();
        cedulaComboBox(combocedula, idcedula);
        statusComboBox(combostatus, idstatus);
        listarCitas();

    }

private void listarCitas() {
    String query =
      "SELECT c.folio_cita, c.cita_hora, c.cita_fecha, c.cita_usuarioAgenda, " +
      "       CAST(m.cedula AS VARCHAR) + ' ' + e.empleado_nombre + ' ' + e.empleado_paterno + ' ' + e.empleado_materno AS nombre_empleados, " +
      "       ce.estatusCita + ' ' + ce.motivoCancelacion AS estados " +
      "FROM Cita c " +
      "  JOIN Medico m       ON c.cedula      = m.cedula " +
      "  JOIN Empleado e     ON m.id_empleado  = e.id_empleado " +
      "  JOIN Cita_Estatus ce ON c.id_citaEstatus = ce.id_citaEstatus " +
      "  JOIN Paciente p     ON c.id_paciente = p.CURP " +
      // —> ahora nos unimos a Usuario para filtrar por quien inició sesión
      "  JOIN Usuario u      ON p.CURP        = u.CURP " +
      "WHERE u.id_usuario = ?";

    try (Connection con = CONEXION.obtenerConexion();
         PreparedStatement pst = con.prepareStatement(query)) {

        pst.setInt(1, login.idusuarios);
        ResultSet rs = pst.executeQuery();
        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Folio","Hora de Cita","Fecha de Cita","Nombre Empleados","Estados"}, 0
        );

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("folio_cita"),
                rs.getString("cita_hora"),
                rs.getDate("cita_fecha"),
                rs.getString("nombre_empleados"),
                rs.getString("estados")
            });
        }
        jTable1.setModel(model);
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al realizar la consulta: " + e.getMessage());
    }
}


private void buscarPorFechas() {
    Date fechaInicio = jdateinicio.getDate();
    Date fechaFinal  = jdatefinal.getDate();
    // ... validaciones de fechas ...

    String query =
      "SELECT c.folio_cita, c.cita_hora, c.cita_fecha, c.cita_usuarioAgenda, " +
      "       CAST(m.cedula AS VARCHAR) + ' ' + e.empleado_nombre + ' ' + e.empleado_paterno + ' ' + e.empleado_materno AS nombre_empleados, " +
      "       ce.estatusCita + ' ' + ce.motivoCancelacion AS estados " +
      "FROM Cita c " +
      "  JOIN Medico m       ON c.cedula      = m.cedula " +
      "  JOIN Empleado e     ON m.id_empleado  = e.id_empleado " +
      "  JOIN Cita_Estatus ce ON c.id_citaEstatus = ce.id_citaEstatus " +
      "  JOIN Paciente p     ON c.id_paciente = p.CURP " +
      // —> de igual forma, unimos con Usuario
      "  JOIN Usuario u      ON p.CURP        = u.CURP " +
      "WHERE u.id_usuario = ? " +
      "  AND c.cita_fecha BETWEEN ? AND ?";

    try (Connection con = CONEXION.obtenerConexion();
         PreparedStatement pst = con.prepareStatement(query)) {

        pst.setInt(1, login.idusuarios);
        pst.setDate (2, new java.sql.Date(fechaInicio.getTime()));
        pst.setDate (3, new java.sql.Date(fechaFinal.getTime()));
        ResultSet rs = pst.executeQuery();

        DefaultTableModel model = new DefaultTableModel(
            new String[]{"Folio","Hora de Cita","Fecha de Cita","Nombre Empleados","Estados"}, 0
        );
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("folio_cita"),
                rs.getString("cita_hora"),
                rs.getDate("cita_fecha"),
                rs.getString("nombre_empleados"),
                rs.getString("estados")
            });
        }
        jTable1.setModel(model);
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al realizar la consulta: " + e.getMessage());
    }
}


    private void cargarNombreCompleto() {
         Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        con = CONEXION.obtenerConexion();
        if (con != null) {
            // 1) Cambiamos la consulta para traer el nombre desde Paciente, no desde Empleado
            String sql = 
                  "SELECT pac_nombre + ' ' + pac_paterno + ' ' + pac_materno AS nombre_completo "
                + "FROM Paciente "
                + "WHERE id_usuario = ?";

            pst = con.prepareStatement(sql);
            pst.setInt(1, login.idusuarios); // id_usuario del paciente

            rs = pst.executeQuery();
            if (rs.next()) {
                String nombreCompleto = rs.getString("nombre_completo");
                txtusuario.setText(nombreCompleto); 
            } else {
                // Si no lo encuentra, solo mostramos esto; pero en principio esto ya no debería pasar
                JOptionPane.showMessageDialog(null, "Paciente no encontrado.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al cargar el nombre del paciente: " + e.getMessage());
    } finally {
        try {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage());
        }
    }
    }

    public static void cedulaComboBox(JComboBox<String> combocedula, Map<String, String> idcedula) {
        String query = "SELECT TOP (1000)\n"
                + "    CAST(m.cedula AS VARCHAR) AS cedula,\n"
                + "    CAST(m.cedula AS VARCHAR) + ' ' + e.empleado_nombre + ' ' + e.empleado_paterno + ' ' + e.empleado_materno AS nombre_empleados\n"
                + "FROM [CMZ_BETA].[dbo].[Medico] m\n"
                + "JOIN [CMZ_BETA].[dbo].[Empleado] e ON m.id_empleado = e.id_empleado;";
        Connection con = CONEXION.obtenerConexion();

        if (con != null) {
            try (PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String id = rs.getString("cedula");
                    String nombre = rs.getString("nombre_empleados");
                    combocedula.addItem(nombre);
                    idcedula.put(nombre, id);
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

private void actualizarStatusCita() {
    // Obtener el folio de la cita desde el campo de texto txtfolio
    int folioCita = Integer.parseInt(txtfolio.getText()); // Asumiendo que txtfolio es tu campo de texto para el folio

    String selectedStatus = (String) combostatus.getSelectedItem();
    String idStatus = idstatus.get(selectedStatus);  // Obtener el idStatus del mapa

    if (selectedStatus == null || idStatus == null) {
        JOptionPane.showMessageDialog(null, "Por favor, seleccione un estado para la cita.");
        return;
    }

    Connection con = null;
    PreparedStatement pst = null;

    try {
        // Obtener conexión a la base de datos
        con = CONEXION.obtenerConexion();
        if (con != null) {
            // Consulta SQL para actualizar el estado de la cita
            String sql = "UPDATE Cita SET id_citaEstatus = ? WHERE folio_cita = ?";

            pst = con.prepareStatement(sql);
            pst.setString(1, idStatus);
            pst.setInt(2, folioCita);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Estado de cita actualizado correctamente.");
                listarCitas() ;
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró la cita con el folio proporcionado.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al realizar la actualización: " + e.getMessage());
    } finally {
        try {
            if (pst != null) {
                pst.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage());
        }
    }
}

    public static void statusComboBox(JComboBox<String> combostatus, Map<String, String> idstatus) {
        String query = "SELECT TOP (1000) [id_citaEstatus],[estatusCita]+' '+[motivoCancelacion] as estados FROM [CMZ_BETA].[dbo].[Cita_Estatus]";
        Connection con = CONEXION.obtenerConexion();

        if (con != null) {
            try (PreparedStatement ps = con.prepareStatement(query);
                    ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String id = rs.getString("id_citaEstatus");
                    String nombre = rs.getString("estados");
                    combostatus.addItem(nombre);
                    idstatus.put(nombre, id);
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        txtusuario = new javax.swing.JTextField();
        txtfolio = new javax.swing.JTextField();
        txtHora = new javax.swing.JTextField();
        jDatefecha = new com.toedter.calendar.JDateChooser();
        combocedula = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        combostatus = new javax.swing.JComboBox<>();
        txtCostoEspecialidad = new javax.swing.JTextField();
        txtNombreEspecialidad = new javax.swing.JTextField();
        txtDescripcion = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jdateinicio = new com.toedter.calendar.JDateChooser();
        jdatefinal = new com.toedter.calendar.JDateChooser();
        jButton3 = new javax.swing.JButton();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(65, 70, 76));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Historial de citas");

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

        txtusuario.setBorder(javax.swing.BorderFactory.createTitledBorder("usuario"));
        txtusuario.setEnabled(false);

        txtfolio.setBorder(javax.swing.BorderFactory.createTitledBorder("Folio"));
        txtfolio.setEnabled(false);

        txtHora.setBorder(javax.swing.BorderFactory.createTitledBorder("Hora"));
        txtHora.setEnabled(false);

        jDatefecha.setBorder(javax.swing.BorderFactory.createTitledBorder("Fecha"));
        jDatefecha.setEnabled(false);

        combocedula.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Medico" }));
        combocedula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combocedulaActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(2, 132, 132));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setText("Limpiar");
        jButton1.setBorder(null);
        jButton1.setBorderPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        combostatus.setEditable(true);
        combostatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Status" }));
        combostatus.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        txtCostoEspecialidad.setBorder(javax.swing.BorderFactory.createTitledBorder("Costo"));
        txtCostoEspecialidad.setEnabled(false);

        txtNombreEspecialidad.setBorder(javax.swing.BorderFactory.createTitledBorder("Especialidad"));
        txtNombreEspecialidad.setEnabled(false);

        txtDescripcion.setBorder(javax.swing.BorderFactory.createTitledBorder("Descripcion"));
        txtDescripcion.setEnabled(false);

        jButton2.setBackground(new java.awt.Color(25, 115, 184));
        jButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton2.setText("Dar de alta");
        jButton2.setBorder(null);
        jButton2.setBorderPainted(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jdateinicio.setBorder(javax.swing.BorderFactory.createTitledBorder("Fecha inicio"));

        jdatefinal.setBorder(javax.swing.BorderFactory.createTitledBorder("Fecha final"));
        jdatefinal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jdatefinalMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jdatefinalMouseEntered(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jdatefinalMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jdatefinalMouseReleased(evt);
            }
        });
        jdatefinal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jdatefinalKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jdatefinalKeyReleased(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(25, 115, 184));
        jButton3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Buscar");
        jButton3.setBorder(null);
        jButton3.setBorderPainted(false);
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtNombreEspecialidad, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtHora, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jDatefecha, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtfolio, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtusuario, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(combocedula, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(txtCostoEspecialidad, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(combostatus, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(119, 119, 119)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jdateinicio, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jdatefinal, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 757, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(txtfolio, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtusuario, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDatefecha, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtHora, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(combocedula, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtNombreEspecialidad, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtCostoEspecialidad, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(combostatus, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jdateinicio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jdatefinal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(16, 16, 16))))
        );

        add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1087, -1));
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // Selecciona el primer elemento

listarCitas(); 
    }//GEN-LAST:event_jButton1ActionPerformed

    private void combocedulaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combocedulaActionPerformed
        String selectedcedula = (String) combocedula.getSelectedItem();
        String idcedulas = idcedula.get(selectedcedula);   // Obtén el idCedula desde el mapa

        if (selectedcedula == null || idcedulas == null) {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione una cédula.");
            return;
        }

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            con = CONEXION.obtenerConexion();
            if (con != null) {
                String sql = "SELECT m.cedula, e.descripcion, e.nombre_especialidad, e.costo_especiliadad "
                        + "FROM Medico m "
                        + "JOIN Especialidad e ON m.id_especialidad = e.id_especialidad "
                        + "WHERE m.cedula = ?;";

                pst = con.prepareStatement(sql);
                pst.setString(1, idcedulas);

                rs = pst.executeQuery();

                if (rs.next()) {
                    String descripcion = rs.getString("descripcion");
                    String nombreEspecialidad = rs.getString("nombre_especialidad");
                    String costoEspecialidad = rs.getString("costo_especiliadad");

                    // Mostrar los datos en tus componentes de la interfaz gráfica
                    txtDescripcion.setText(descripcion);
                    txtNombreEspecialidad.setText(nombreEspecialidad);
                    txtCostoEspecialidad.setText(costoEspecialidad);
                } else {
                    JOptionPane.showMessageDialog(null, "No se encontraron datos para la cédula seleccionada.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al realizar la consulta: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_combocedulaActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
actualizarStatusCita();           // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int selectedRow = jTable1.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        if (selectedRow != -1) {
            txtfolio.setText(model.getValueAt(selectedRow, 0).toString());  // Suponiendo que la columna 0 contiene el valor para txtusuarios
            txtHora.setText(model.getValueAt(selectedRow, 1).toString());
             try {
                    String fechaStr = model.getValueAt(selectedRow, 2).toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date fechaNacimiento = sdf.parse(fechaStr);
                    jDatefecha.setDate(fechaNacimiento);
                } catch (ParseException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al convertir la fecha: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

            combocedula.setSelectedItem(model.getValueAt(selectedRow, 3).toString());
            combostatus.setSelectedItem(model.getValueAt(selectedRow, 4).toString());

        } else {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione una fila válida.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable1MouseClicked

    private void jdatefinalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jdatefinalMousePressed
      
    }//GEN-LAST:event_jdatefinalMousePressed

    private void jdatefinalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jdatefinalMouseClicked
       // TODO add your handling code here:
    }//GEN-LAST:event_jdatefinalMouseClicked

    private void jdatefinalMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jdatefinalMouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jdatefinalMouseEntered

    private void jdatefinalMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jdatefinalMouseReleased
         // TODO add your handling code here:
    }//GEN-LAST:event_jdatefinalMouseReleased

    private void jdatefinalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jdatefinalKeyReleased
     buscarPorFechas();    // TODO add your handling code here:
    }//GEN-LAST:event_jdatefinalKeyReleased

    private void jdatefinalKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jdatefinalKeyPressed
         // TODO add your handling code here:
    }//GEN-LAST:event_jdatefinalKeyPressed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
     buscarPorFechas();   // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> combocedula;
    private javax.swing.JComboBox<String> combostatus;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private com.toedter.calendar.JDateChooser jDatefecha;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private com.toedter.calendar.JDateChooser jdatefinal;
    private com.toedter.calendar.JDateChooser jdateinicio;
    private javax.swing.JTextField txtCostoEspecialidad;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtHora;
    private javax.swing.JTextField txtNombreEspecialidad;
    private javax.swing.JTextField txtfolio;
    private javax.swing.JTextField txtusuario;
    // End of variables declaration//GEN-END:variables
}
