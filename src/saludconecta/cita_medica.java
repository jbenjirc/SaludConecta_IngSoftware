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
import static saludconecta.historialcitas.statusComboBox;
/**
 *
 * @author LENOVO
 */
public class cita_medica extends javax.swing.JPanel {

    /**
     * Creates new form citamedica
     */
     Map<String, String> idPaciente = new HashMap<>();   
     
      Map<String, Integer> idconsultorio = new HashMap<>();
      Map<String, String> turnoConsultorio = new HashMap<>();
      Map<String, Boolean> enUsoConsultorio = new HashMap<>();
     
     Map<String, Integer> idespecialidad = new HashMap<>();
     Map<String, String> descripcionEspecialidad = new HashMap<>();
     Map<String, Double> costoEspecialidad = new HashMap<>();
        Map<String, String> idstatus = new HashMap<>();


    public cita_medica() {
        initComponents();
        pacienteComboBox(combopaciente,idPaciente);        
        especialidadComboBox(especialidadcombo, idespecialidad, descripcionEspecialidad, costoEspecialidad);
        consultorioComboBox(consultoriocombo, idconsultorio, turnoConsultorio, enUsoConsultorio);
        listarCitas();
        statusComboBox(combostatus, idstatus);

    }
    
    private void actualizarStatusCita() {
    // Obtener el folio de la cita desde el campo de texto txtfolio
    int folioCita = Integer.parseInt(txtfolio1.getText()); // Asumiendo que txtfolio es tu campo de texto para el folio

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

private void listarCitas() {
    String query = "SELECT c.folio_cita, c.cita_hora, c.cita_fecha, c.cita_usuarioAgenda, " +
                   "m.cedula, ce.estatusCita, ce.motivoCancelacion, " +
                   "p.pac_nombre + ' ' + p.pac_paterno + ' ' + p.pac_materno AS nombre_paciente, " +
                   "e.id_empleado, e.empleado_CURP, e.empleado_nombre, e.empleado_paterno, e.empleado_materno, " +
                   "es.id_especialidad, es.nombre_especialidad, es.descripcion, es.costo_especiliadad, " +
                   "cns.id_consultorio, cns.consultorio_numero, ch.consultorio_turno, ch.consultorio_enUso " +
                   "FROM Cita c " +
                   "JOIN Medico m ON c.cedula = m.cedula " +
                   "JOIN Cita_Estatus ce ON c.id_citaEstatus = ce.id_citaEstatus " +
                   "JOIN Paciente p ON c.id_paciente = p.CURP " +
                   "JOIN Empleado e ON m.id_empleado = e.id_empleado " +
                   "JOIN Especialidad es ON m.id_especialidad = es.id_especialidad " +
                   "JOIN Consultorio cns ON m.cedula = cns.cedula " +
                   "JOIN Consultorio_Horario ch ON cns.id_consultorioHorario = ch.id_consultorioHorario " +
                   "WHERE e.id_empleado = ?";

    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        con = CONEXION.obtenerConexion();
        if (con != null) {
            pst = con.prepareStatement(query);
            pst.setInt(1, login.idempleado);  // Usa el id_empleado del login
            rs = pst.executeQuery();

            // Crear el modelo de la tabla
            DefaultTableModel model = new DefaultTableModel(new String[]{
                "Folio Cita", "Hora", "Fecha",  "Medico", 
                "Estatus Cita",  "Nombre Paciente", 
      
                "Nombre Especialidad", "Consultorio numero", 
               
            }, 0);

            // Llenar el modelo con los datos del ResultSet
            while (rs.next()) {
                int folioCita = rs.getInt("folio_cita");
                String citaHora = rs.getString("cita_hora");
                Date citaFecha = rs.getDate("cita_fecha");
                String nombreEmpleado = rs.getString("empleado_nombre") + " " + rs.getString("empleado_paterno") + " " + rs.getString("empleado_materno");
                String estatusCita = rs.getString("estatusCita");
                String nombrePaciente = rs.getString("nombre_paciente");
                String nombreEspecialidad = rs.getString("nombre_especialidad");
                String numeroConsultorio = rs.getString("consultorio_numero");

                // Agregar fila al modelo de la tabla
                model.addRow(new Object[]{
                    folioCita, citaHora, citaFecha, nombreEmpleado, 
                    estatusCita, nombrePaciente, 
                      nombreEspecialidad,
                    numeroConsultorio
                });
            }

            // Asignar el modelo al JTable
            jTable1.setModel(model);
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
}

    private void eliminarRegistro(int folioCita) {
    String query = "DELETE FROM [CMZ_BETA].[dbo].[Cita] WHERE folio_cita = ?";
    Connection con = CONEXION.obtenerConexion();

    if (con != null) {
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, folioCita);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar el registro: " + e.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.toString());
            }
        }
    }}
    
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

public static void consultorioComboBox(JComboBox<String> consultoriocombo, Map<String, Integer> idconsultorio, Map<String, String> turnoConsultorio, Map<String, Boolean> enUsoConsultorio) {
    String query = "SELECT c.id_consultorio, c.consultorio_numero, ch.consultorio_turno, ch.consultorio_enUso " +
                   "FROM [CMZ_BETA].[dbo].[Empleado] e " +
                   "JOIN [CMZ_BETA].[dbo].[Medico] m ON e.id_empleado = m.id_empleado " +
                   "JOIN [CMZ_BETA].[dbo].[Consultorio] c ON m.cedula = c.cedula " +
                   "JOIN [CMZ_BETA].[dbo].[Consultorio_Horario] ch ON c.id_consultorioHorario = ch.id_consultorioHorario " +
                   "WHERE e.id_empleado = ?";
    Connection con = CONEXION.obtenerConexion();
    
    if (con != null) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(query);
            ps.setInt(1, login.idempleado);  // Usa el id_empleado del login
            rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id_consultorio");
                String numero = rs.getString("consultorio_numero");
                String turno = rs.getString("consultorio_turno");
                boolean enUso = rs.getBoolean("consultorio_enUso");

                consultoriocombo.addItem(numero);
                idconsultorio.put(numero, id);
                turnoConsultorio.put(numero, turno);
                enUsoConsultorio.put(numero, enUso);
            }
        } catch (SQLException e) {
            System.out.println("Error al realizar la consulta: " + e.toString());
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.toString());
            }
        }
    }
}

public void mostrarDoctorInfo() {
    String query = "SELECT e.id_empleado, e.empleado_nombre, e.empleado_paterno, e.empleado_materno " +
                   "FROM [CMZ_BETA].[dbo].[Empleado] e " +
                   "WHERE e.id_empleado = ?";

    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        con = CONEXION.obtenerConexion();
        if (con != null) {
            pst = con.prepareStatement(query);
            pst.setInt(1, login.idempleado);  // Usa el id_empleado del login
            rs = pst.executeQuery();

            if (rs.next()) {
                String nombre = rs.getString("empleado_nombre");
                String paterno = rs.getString("empleado_paterno");
                String materno = rs.getString("empleado_materno");

                String doctorInfo = nombre + " " + paterno + " " + materno;
                txtdoctor.setText(doctorInfo);
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró información del doctor.");
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
}

public static void especialidadComboBox(JComboBox<String> especialidadcombo, Map<String, Integer> idespecialidad, Map<String, String> descripcionEspecialidad, Map<String, Double> costoEspecialidad) {
    String query = "SELECT e.id_empleado, e.empleado_CURP, m.cedula, " +
                   "es.id_especialidad, es.nombre_especialidad, es.descripcion, es.costo_especiliadad " +
                   "FROM [CMZ_BETA].[dbo].[Empleado] e " +
                   "JOIN [CMZ_BETA].[dbo].[Medico] m ON e.id_empleado = m.id_empleado " +
                   "JOIN [CMZ_BETA].[dbo].[Especialidad] es ON m.id_especialidad = es.id_especialidad " +
                   "WHERE e.id_empleado = ?";

    Connection con = CONEXION.obtenerConexion();

    if (con != null) {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            pst = con.prepareStatement(query);
            pst.setInt(1, login.idempleado);  // Usa el id_empleado del login
            rs = pst.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id_especialidad");
                String nombre = rs.getString("nombre_especialidad");
                String descripcion = rs.getString("descripcion");
                double costo = rs.getDouble("costo_especiliadad");
                especialidadcombo.addItem(nombre);
                idespecialidad.put(nombre, id);
                descripcionEspecialidad.put(nombre, descripcion);
                costoEspecialidad.put(nombre, costo);
            }
        } catch (SQLException e) {
            System.out.println("Error al realizar la consulta: " + e.toString());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.toString());
            }
        }
    }
}   
    
    public static void pacienteComboBox(JComboBox<String> tipocombox, Map<String, String> idPaciente) {
    String query = "SELECT CURP,p.pac_nombre + ' ' + p.pac_paterno + ' ' + p.pac_materno AS nombre_paciente FROM Paciente p";
    Connection con = CONEXION.obtenerConexion();
    
    if (con != null) {
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("CURP");
                String nombre = rs.getString("nombre_paciente");
                tipocombox.addItem(nombre);
                idPaciente.put(nombre, id);
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
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtfolio1 = new javax.swing.JTextField();
        consultoriocombo = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtdoctor = new javax.swing.JLabel();
        combopaciente = new javax.swing.JComboBox<>();
        especialidadcombo = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        txtprecio = new javax.swing.JTextField();
        txtturno = new javax.swing.JTextField();
        txthorario = new javax.swing.JTextField();
        fechadate = new com.toedter.calendar.JDateChooser();
        txtdescripcion = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton4 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        combostatus = new javax.swing.JComboBox<>();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 450));

        txtfolio1.setBorder(javax.swing.BorderFactory.createTitledBorder("Folio"));
        txtfolio1.setEnabled(false);
        txtfolio1.setName(""); // NOI18N

        consultoriocombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Consultorio" }));
        consultoriocombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                consultoriocomboActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(65, 70, 76));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Registrar Cita");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("DOCTOR:");

        txtdoctor.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
        txtdoctor.setForeground(new java.awt.Color(255, 255, 255));
        txtdoctor.setText("dfdf");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel2)
                .addGap(70, 70, 70)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtdoctor, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(517, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(17, Short.MAX_VALUE))
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txtdoctor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        combopaciente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Paciente" }));

        especialidadcombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Especialidades" }));
        especialidadcombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                especialidadcomboActionPerformed(evt);
            }
        });

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
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jTable1MouseEntered(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton1.setBackground(new java.awt.Color(91, 209, 179));
        jButton1.setText("DAR DE BAJA");
        jButton1.setBorder(null);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        txtprecio.setBorder(javax.swing.BorderFactory.createTitledBorder("Precio"));
        txtprecio.setEnabled(false);

        txtturno.setBorder(javax.swing.BorderFactory.createTitledBorder("Turno"));
        txtturno.setEnabled(false);
        txtturno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtturnoActionPerformed(evt);
            }
        });

        txthorario.setBorder(javax.swing.BorderFactory.createTitledBorder("Hora HH:MM:SS"));
        txthorario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txthorarioActionPerformed(evt);
            }
        });

        fechadate.setBorder(javax.swing.BorderFactory.createTitledBorder("Fecha"));

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setBorder(javax.swing.BorderFactory.createTitledBorder("Descripcion"));
        jTextArea1.setEnabled(false);
        txtdescripcion.setViewportView(jTextArea1);

        jButton4.setBackground(new java.awt.Color(91, 209, 179));
        jButton4.setText("Limpiar");
        jButton4.setBorder(null);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);

        combostatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Status" }));
        combostatus.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        combostatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combostatusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtfolio1, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(fechadate, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                                        .addComponent(txthorario, javax.swing.GroupLayout.Alignment.LEADING)))
                                .addGap(34, 34, 34)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtturno)
                                    .addComponent(consultoriocombo, 0, 350, Short.MAX_VALUE)
                                    .addComponent(combopaciente, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(combostatus, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(14, 14, 14)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtdescripcion, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                            .addComponent(txtprecio)
                            .addComponent(especialidadcombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(297, 297, 297)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1074, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jSeparator2))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtfolio1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(combopaciente, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(especialidadcombo, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtprecio, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(consultoriocombo, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txthorario, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(fechadate, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                            .addComponent(txtdescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(txtturno))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(combostatus, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                        .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)))
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
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

    private void especialidadcomboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_especialidadcomboActionPerformed
        String selectedNombre = (String) especialidadcombo.getSelectedItem();
                if (selectedNombre != null) {
                    jTextArea1.setText(descripcionEspecialidad.get(selectedNombre));
                    txtprecio.setText(String.valueOf(costoEspecialidad.get(selectedNombre)));
                }
    }//GEN-LAST:event_especialidadcomboActionPerformed

    private void consultoriocomboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_consultoriocomboActionPerformed
                String selectedNombre = (String) consultoriocombo.getSelectedItem();
                if (selectedNombre != null) {
                    txtturno.setText(turnoConsultorio.get(selectedNombre));
                }       
            
    }//GEN-LAST:event_consultoriocomboActionPerformed

    private void txtturnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtturnoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtturnoActionPerformed

    private void txthorarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txthorarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txthorarioActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
  
        int selectedRow = jTable1.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        if (selectedRow != -1) {
            txtfolio1.setText(model.getValueAt(selectedRow, 0).toString());  // Suponiendo que la columna 0 contiene el valor para txtusuarios
            txthorario.setText(model.getValueAt(selectedRow, 1).toString());
             try {
                    String fechaStr = model.getValueAt(selectedRow, 2).toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date fechaNacimiento = sdf.parse(fechaStr);
                    fechadate.setDate(fechaNacimiento);
                } catch (ParseException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al convertir la fecha: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

            combopaciente.setSelectedItem(model.getValueAt(selectedRow, 5).toString());
            especialidadcombo.setSelectedItem(model.getValueAt(selectedRow, 6).toString());
             consultoriocombo.setSelectedItem(model.getValueAt(selectedRow, 7).toString());

        } else {
            JOptionPane.showMessageDialog(null, "Por favor, seleccione una fila válida.", "Error", JOptionPane.ERROR_MESSAGE);
        
        }
    
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    actualizarStatusCita();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTable1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseEntered
        // TODO add your handling code here:
    }//GEN-LAST:event_jTable1MouseEntered

    private void combostatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combostatusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combostatusActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> combopaciente;
    private javax.swing.JComboBox<String> combostatus;
    private javax.swing.JComboBox<String> consultoriocombo;
    private javax.swing.JComboBox<String> especialidadcombo;
    private com.toedter.calendar.JDateChooser fechadate;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JScrollPane txtdescripcion;
    private javax.swing.JLabel txtdoctor;
    private javax.swing.JTextField txtfolio1;
    private javax.swing.JTextField txthorario;
    private javax.swing.JTextField txtprecio;
    private javax.swing.JTextField txtturno;
    // End of variables declaration//GEN-END:variables
}
