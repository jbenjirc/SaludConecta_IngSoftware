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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

/**
 *
 * @author LENOVO
 */
public class separar_citas extends javax.swing.JPanel {
       Map<String, String> idcedula = new HashMap<>();         
       Map<String, String> idstatus = new HashMap<>();  


    /**
     * Creates new form separar_citas
     */
    public separar_citas() {
        initComponents();
        cargarNombreCompleto();
                cedulaComboBox(combocedula,idcedula);                 
       

    }

       private void cargarNombreCompleto() {
    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        con = CONEXION.obtenerConexion();
        if (con != null) {
            // En lugar de Empleado, busco en Usuario el campo usuario_nombre
            String sql = "SELECT usuario_nombre FROM Usuario WHERE id_usuario = ?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, login.idusuarios);

            rs = pst.executeQuery();
            if (rs.next()) {
                String nombreUsuario = rs.getString("usuario_nombre");
                txtusuario.setText(nombreUsuario);
            } else {
                // Por si no existiera el usuario en Usuario (aunque eso no debería ocurrir)
                JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al cargar el nombre del usuario: " + e.getMessage());
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
String query = "SELECT TOP (1000)\n" +
"    CAST(m.cedula AS VARCHAR) AS cedula,\n" +
"    CAST(m.cedula AS VARCHAR) + ' ' + e.empleado_nombre + ' ' + e.empleado_paterno + ' ' + e.empleado_materno AS nombre_empleados\n" +
"FROM [CMZ_BETA].[dbo].[Medico] m\n" +
"JOIN [CMZ_BETA].[dbo].[Empleado] e ON m.id_empleado = e.id_empleado;";
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
private void registrarCita() {
  // 1) Leer campos de la interfaz
    String horaCita = txtHora.getText();
    Date fechaCita = jDatefecha.getDate();
    String usuarioAgenda = "Paciente";
    String selectedCedula = (String) combocedula.getSelectedItem();
    String idCedula = idcedula.get(selectedCedula);
    int idUsuario = login.idusuarios; // <-- Debe ser 42

    // ───> DEBUG 1: Imprime en consola qué idUsuario estás usando
    System.out.println("DEBUG en registrarCita(): idUsuario = " + idUsuario);

    // 2) Validar que los campos no estén vacíos
    if (horaCita.isEmpty() || fechaCita == null || selectedCedula == null) {
        JOptionPane.showMessageDialog(null, "Por favor, complete todos los campos.");
        return;
    }

    // 3) Validar que la fecha sea posterior a hoy
    Date today = new Date();
    if (fechaCita.before(today)) {
        JOptionPane.showMessageDialog(null, "La fecha de la cita debe ser mayor a la de hoy.");
        return;
    }

    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        // 4) Abrir conexión
        con = CONEXION.obtenerConexion();
        if (con == null) {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.");
            return;
        }

        // ───> DEBUG 2: Mostrar la consulta antes de ejecutarla
            String sqlCurp =
            "SELECT p.CURP " +
            "FROM Paciente p " +
            "JOIN Usuario u ON p.CURP = u.CURP " +
            "WHERE u.id_usuario = ?";
        pst = con.prepareStatement(sqlCurp);
        pst.setInt(1, idUsuario);
        System.out.println("DEBUG en registrarCita(): Ejecutando → " + sqlCurp + " = " + idUsuario);
        rs = pst.executeQuery();


        // 5) Obtener resultado
        String curpPaciente = null;
        if (rs.next()) {
            curpPaciente = rs.getString("CURP");
            // ───> DEBUG 3: Mostrar el CURP recuperado
            System.out.println("DEBUG en registrarCita(): curpPaciente = " + curpPaciente);
        }
        rs.close();
        pst.close();

        // 6) Si no se encontró CURP, salir con mensaje
        if (curpPaciente == null) {
            JOptionPane.showMessageDialog(null, "No se encontró paciente con este usuario.");
            return;
        }

        // 7) Verificar que esa fecha/hora no esté ocupada
        String checkSql = "SELECT COUNT(*) FROM Cita WHERE cita_fecha = ? AND cita_hora = ?";
        pst = con.prepareStatement(checkSql);
        pst.setDate(1, new java.sql.Date(fechaCita.getTime()));
        pst.setString(2, horaCita);
        rs = pst.executeQuery();
        if (rs.next() && rs.getInt(1) > 0) {
            JOptionPane.showMessageDialog(null, "Ya existe una cita registrada con esta fecha y hora.");
            rs.close();
            pst.close();
            return;
        }
        rs.close();
        pst.close();

        // 8) Insertar la nueva cita en Cita, usando el CURP como id_paciente
        String sqlInsert = 
            "INSERT INTO Cita (cita_hora, cita_fecha, cita_usuarioAgenda, cedula, id_citaEstatus, id_paciente) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
        pst = con.prepareStatement(sqlInsert);
        pst.setString(1, horaCita);
        pst.setDate(2, new java.sql.Date(fechaCita.getTime()));
        pst.setString(3, usuarioAgenda);
        pst.setString(4, idCedula);
        pst.setInt(5, 2);              // id_citaEstatus (según tu lógica)
        pst.setString(6, curpPaciente); // se usa el CURP recuperado

        int rowsAffected = pst.executeUpdate();
        if (rowsAffected > 0) {
            JOptionPane.showMessageDialog(null, "Cita registrada exitosamente.");
        } else {
            JOptionPane.showMessageDialog(null, "Error al registrar la cita.");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al realizar la consulta: " + e.getMessage());
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
        txtCostoEspecialidad = new javax.swing.JTextField();
        txtNombreEspecialidad = new javax.swing.JTextField();
        txtDescripcion = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(65, 70, 76));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Agendar una cita");

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

        jDatefecha.setBorder(javax.swing.BorderFactory.createTitledBorder("Fecha"));

        combocedula.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Medico" }));
        combocedula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combocedulaActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(0, 96, 152));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton1.setText("Limpiar");
        jButton1.setBorder(null);
        jButton1.setBorderPainted(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        txtCostoEspecialidad.setBorder(javax.swing.BorderFactory.createTitledBorder("Costo"));
        txtCostoEspecialidad.setEnabled(false);

        txtNombreEspecialidad.setBorder(javax.swing.BorderFactory.createTitledBorder("Especialidad"));
        txtNombreEspecialidad.setEnabled(false);

        txtDescripcion.setBorder(javax.swing.BorderFactory.createTitledBorder("Descripcion"));
        txtDescripcion.setEnabled(false);

        jButton2.setBackground(new java.awt.Color(2, 132, 132));
        jButton2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jButton2.setText("Registrar cita");
        jButton2.setBorder(null);
        jButton2.setBorderPainted(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtfolio, javax.swing.GroupLayout.PREFERRED_SIZE, 1040, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtusuario, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(80, 80, 80)
                                .addComponent(combocedula, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtHora, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(80, 80, 80)
                                .addComponent(txtNombreEspecialidad, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(txtCostoEspecialidad, javax.swing.GroupLayout.PREFERRED_SIZE, 310, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jDatefecha, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(80, 80, 80)
                                .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 680, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(70, 70, 70)
                .addComponent(txtfolio, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtusuario, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(combocedula, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtHora, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombreEspecialidad, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCostoEspecialidad, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDatefecha, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(57, 57, 57)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1087, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String selectedcedula= (String) combocedula.getSelectedItem();
        String idcedulas = idcedula.get(selectedcedula);   
        

    txtHora.setText("");
    jDatefecha.setDate(null);
    combocedula.setSelectedIndex(0); // Selecciona el primer elemento


    }//GEN-LAST:event_jButton1ActionPerformed

    private void combocedulaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combocedulaActionPerformed
        String selectedcedula= (String) combocedula.getSelectedItem();
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
                String sql = "SELECT m.cedula, e.descripcion, e.nombre_especialidad, e.costo_especiliadad " +
                "FROM Medico m " +
                "JOIN Especialidad e ON m.id_especialidad = e.id_especialidad " +
                "WHERE m.cedula = ?;";

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
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_combocedulaActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
registrarCita();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> combocedula;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private com.toedter.calendar.JDateChooser jDatefecha;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextField txtCostoEspecialidad;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtHora;
    private javax.swing.JTextField txtNombreEspecialidad;
    private javax.swing.JTextField txtfolio;
    private javax.swing.JTextField txtusuario;
    // End of variables declaration//GEN-END:variables
}
