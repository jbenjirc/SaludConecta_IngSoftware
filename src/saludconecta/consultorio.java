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
import static saludconecta.farmacia.contenidoComboBox;
/**
 *
 * @author LENOVO
 */
public class consultorio extends javax.swing.JPanel {
    private int idhorarios;
       Map<String, String> idcedula = new HashMap<>();  

    /**
     * Creates new form citamedica
     */
  

    public consultorio() {
        initComponents();
        cargarDatosEnTabla();
        cedulaComboBox(combocedula,idcedula);        

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
        
   private void cargarDatosEnTabla() {
    String[] columnNames = {"ID Consultorio", "Número", "Tipo",  "Cedula","Turno","idConsultorioHorario" };
    DefaultTableModel model = new DefaultTableModel(columnNames, 0);

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        // Obtener conexión a la base de datos
        con = CONEXION.obtenerConexion();
        
        if (con != null) {
            // Consulta SQL para obtener los datos de las tablas relacionadas
            String sql = "SELECT ch.id_consultorioHorario, ch.consultorio_turno, ch.consultorio_enUso, " +
                         "c.id_consultorio, c.consultorio_numero, c.tipo_consultorio, " +
                         "CAST(m.cedula AS VARCHAR) + ' ' + e.empleado_nombre + ' ' + e.empleado_paterno + ' ' + e.empleado_materno AS nombre_medico " +
                         "FROM [CMZ_BETA].[dbo].[Consultorio_Horario] ch " +
                         "JOIN [CMZ_BETA].[dbo].[Consultorio] c ON ch.id_consultorioHorario = c.id_consultorioHorario " +
                         "JOIN [CMZ_BETA].[dbo].[Medico] m ON c.cedula = m.cedula " +
                         "JOIN [CMZ_BETA].[dbo].[Empleado] e ON m.id_empleado = e.id_empleado";

            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            // Recorrer el ResultSet y agregar los datos al modelo de la tabla
            while (rs.next()) {
                int idConsultorio = rs.getInt("id_consultorio");
                int consultorioNumero = rs.getInt("consultorio_numero");
                String tipoConsultorio = rs.getString("tipo_consultorio");
                String nombreMedico = rs.getString("nombre_medico");
                int idConsultorioHorario = rs.getInt("id_consultorioHorario");
                String consultorioTurno = rs.getString("consultorio_turno");
                String consultorioEnUso = rs.getString("consultorio_enUso");

                Object[] row = {idConsultorio, consultorioNumero, tipoConsultorio,nombreMedico, consultorioTurno, idConsultorioHorario};
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

   
 private boolean verificarCampos() {
    String consultorioTurno = txtturno.getText();
    
    String consultorioNumero = txtnumero.getText();
    String tipoConsultorio = txttipo.getText();
    boolean camposValidos = true;

   
    StringBuilder mensaje = new StringBuilder("Los siguientes campos están vacíos o incompletos:\n");

    
    if (consultorioTurno.isEmpty()) {
        mensaje.append("- Consultorio Turno\n");
        camposValidos = false;
    }
   
    if (consultorioNumero.isEmpty()) {
        mensaje.append("- Consultorio Número\n");
        camposValidos = false;
    }
    if (tipoConsultorio.isEmpty()) {
        mensaje.append("- Tipo de Consultorio\n");
        camposValidos = false;
    }
   

    if (!camposValidos) {
        JOptionPane.showMessageDialog(null, mensaje.toString(), "Campos incompletos", JOptionPane.WARNING_MESSAGE);
    }

    return camposValidos;
}
   
private void insertarConsultorio(int idConsultorioHorario) {
    String consultorioNumero = txtnumero.getText();
    String tipoConsultorio = txttipo.getText();

   String selectedcedula= (String) combocedula.getSelectedItem();
    String idcedulas = idcedula.get(selectedcedula); 
 

    Connection con = null;
    PreparedStatement ps = null;

    try {
        // Obtener conexión a la base de datos
        con = CONEXION.obtenerConexion();
        
        if (con != null) {
            // Consulta SQL para insertar datos en Consultorio
            String sqlConsultorio = "INSERT INTO [CMZ_BETA].[dbo].[Consultorio] (consultorio_numero, tipo_consultorio, cedula, id_consultorioHorario) VALUES (?, ?, ?, ?)";
            
            ps = con.prepareStatement(sqlConsultorio);
            ps.setInt(1, Integer.parseInt(consultorioNumero));
            ps.setString(2, tipoConsultorio);
            ps.setString(3, idcedulas);
            ps.setInt(4, idConsultorioHorario);
            
            // Ejecutar la inserción
            int rowsInserted = ps.executeUpdate();
            
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(null, "Registro en Consultorio insertado exitosamente.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al insertar los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
private void insertarDatos() {
    String consultorioTurno = txtturno.getText();

    Connection con = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        // Obtener conexión a la base de datos
        con = CONEXION.obtenerConexion();
        
        if (con != null) {
            // Consulta SQL para insertar datos en Consultorio_Horario
            String sqlHorario = "INSERT INTO [CMZ_BETA].[dbo].[Consultorio_Horario] (consultorio_turno, consultorio_enUso) VALUES (?, ?)";
            
            ps = con.prepareStatement(sqlHorario, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, consultorioTurno);
            ps.setInt(2, 0);
            
            // Ejecutar la inserción
            int rowsInserted = ps.executeUpdate();
            
            if (rowsInserted > 0) {
                // Obtener el id_consultorioHorario generado
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int idConsultorioHorario = rs.getInt(1);
                    JOptionPane.showMessageDialog(null, "Registro en Consultorio_Horario insertado exitosamente con ID: " + idConsultorioHorario);
                    
                    // Ahora inserta el registro en la tabla Consultorio
                    insertarConsultorio(idConsultorioHorario);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al insertar los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
private void eliminarDatos() {
    String idconsultorio = txtconsultorio.getText();
    String idhorario = String.valueOf(idhorarios); // Asumimos que este valor se ha almacenado previamente
    System.out.println(idhorario);
    if (idconsultorio.isEmpty() || idhorario.equals("-1")) {
        JOptionPane.showMessageDialog(null, "No hay datos para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    Connection con = null;
    PreparedStatement ps = null;

    try {
        con = CONEXION.obtenerConexion();

        if (con != null) {
            con.setAutoCommit(false); // Iniciar transacción

            // Eliminar de la tabla Consultorio
            String sqlDeleteConsultorio = "DELETE FROM [CMZ_BETA].[dbo].[Consultorio] WHERE id_consultorio = ?";
            ps = con.prepareStatement(sqlDeleteConsultorio);
            ps.setInt(1, Integer.parseInt(idconsultorio));

            int rowsAffectedConsultorio = ps.executeUpdate();

            // Eliminar de la tabla Consultorio_Horario
            String sqlDeleteHorario = "DELETE FROM [CMZ_BETA].[dbo].[Consultorio_Horario] WHERE id_consultorioHorario = ?";
            ps = con.prepareStatement(sqlDeleteHorario);
            ps.setInt(1, Integer.parseInt(idhorario));

            int rowsAffectedHorario = ps.executeUpdate();

            if (rowsAffectedConsultorio > 0 && rowsAffectedHorario > 0) {
                con.commit(); // Confirmar transacción
                JOptionPane.showMessageDialog(null, "Datos eliminados exitosamente.");
                cargarDatosEnTabla(); // Recargar los datos en la tabla
            } else {
                con.rollback(); // Revertir transacción
                JOptionPane.showMessageDialog(null, "No se encontraron datos para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        try {
            if (con != null) {
                con.rollback(); // Revertir transacción en caso de error
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al revertir la transacción: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        JOptionPane.showMessageDialog(null, "Error al eliminar los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            if (ps != null) ps.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cerrar la conexión: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
private void actualizarDatos() {
    String idconsultorio = txtconsultorio.getText();
    String idhorario = String.valueOf(idhorarios);
    String numeroStr = txtnumero.getText();
    String tipo = txttipo.getText();
    String turno = txtturno.getText();
    String selectedcedula= (String) combocedula.getSelectedItem();
    String idcedulas = idcedula.get(selectedcedula); 
    
    // Verificar que los campos no estén vacíos
    if (idconsultorio.isEmpty() || idhorario.equals("-1") || numeroStr.isEmpty() || tipo.isEmpty() || turno.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Todos los campos deben estar llenos.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }
    
    // Convertir valores necesarios
    int numero;
    try {
        numero = Integer.parseInt(numeroStr);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Número y cédula deben ser enteros.", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    Connection con = null;
    PreparedStatement ps = null;

    try {
        con = CONEXION.obtenerConexion();

        if (con != null) {
            con.setAutoCommit(false); // Iniciar transacción

            // Actualizar la tabla Consultorio
            String sqlUpdateConsultorio = "UPDATE [CMZ_BETA].[dbo].[Consultorio] SET consultorio_numero = ?, tipo_consultorio = ?, cedula = ? WHERE id_consultorio = ?";
            ps = con.prepareStatement(sqlUpdateConsultorio);
            ps.setInt(1, numero);
            ps.setString(2, tipo);            
            ps.setString(3, idcedulas);
            ps.setInt(4, Integer.parseInt(idconsultorio));
            int rowsAffectedConsultorio = ps.executeUpdate();

            // Actualizar la tabla Consultorio_Horario
            String sqlUpdateHorario = "UPDATE [CMZ_BETA].[dbo].[Consultorio_Horario] SET consultorio_turno = ? WHERE id_consultorioHorario = ?";
            ps = con.prepareStatement(sqlUpdateHorario);
            ps.setString(1, turno);
            ps.setInt(2, Integer.parseInt(idhorario));
            int rowsAffectedHorario = ps.executeUpdate();

            if (rowsAffectedConsultorio > 0 && rowsAffectedHorario > 0) {
                con.commit(); // Confirmar transacción
                JOptionPane.showMessageDialog(null, "Datos actualizados exitosamente.");
                cargarDatosEnTabla(); // Recargar los datos en la tabla
            } else {
                con.rollback(); // Revertir transacción
                JOptionPane.showMessageDialog(null, "No se encontraron datos para actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Error al conectar con la base de datos.", "Error de conexión", JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException e) {
        try {
            if (con != null) {
                con.rollback(); // Revertir transacción en caso de error
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al revertir la transacción: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        JOptionPane.showMessageDialog(null, "Error al actualizar los datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
txtconsultorio.setText("");
            txtnumero.setText("");
            txttipo.setText("");
            txtturno.setText("");
   

}

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtconsultorio = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        txtnumero = new javax.swing.JTextField();
        txttipo = new javax.swing.JTextField();
        txtturno = new javax.swing.JTextField();
        combocedula = new javax.swing.JComboBox<>();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setPreferredSize(new java.awt.Dimension(800, 450));

        txtconsultorio.setBorder(javax.swing.BorderFactory.createTitledBorder("Id consultorio"));
        txtconsultorio.setEnabled(false);
        txtconsultorio.setName(""); // NOI18N

        jPanel2.setBackground(new java.awt.Color(65, 70, 76));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Registrar Consultorio");

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

        txtnumero.setBorder(javax.swing.BorderFactory.createTitledBorder("Numero Consultorio"));

        txttipo.setBorder(javax.swing.BorderFactory.createTitledBorder("Tipo de consultorio"));

        txtturno.setBorder(javax.swing.BorderFactory.createTitledBorder("Turno"));

        combocedula.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "cedula" }));

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
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtconsultorio, javax.swing.GroupLayout.DEFAULT_SIZE, 297, Short.MAX_VALUE)
                                    .addComponent(txtnumero))
                                .addGap(43, 43, 43)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txttipo, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                                    .addComponent(combocedula, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(41, 41, 41)
                                .addComponent(txtturno, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1074, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(325, 325, 325)
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtconsultorio, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txttipo, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtnumero, javax.swing.GroupLayout.DEFAULT_SIZE, 58, Short.MAX_VALUE)
                            .addComponent(combocedula))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtturno, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(80, 80, 80)))
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
  if (verificarCampos()) {
        insertarDatos();
        cargarDatosEnTabla();
        limpiar();
    }    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
   int selectedRow = jTable1.getSelectedRow();
        if (selectedRow != -1) {
            // Obtener los valores de la fila seleccionada
            String id = jTable1.getValueAt(selectedRow, 0).toString();
            String numero = jTable1.getValueAt(selectedRow, 1).toString();
            String tipo = jTable1.getValueAt(selectedRow, 2).toString();
            String turno = jTable1.getValueAt(selectedRow, 4).toString();            
            String idhorario = jTable1.getValueAt(selectedRow, 5).toString();

        combocedula.setSelectedItem(jTable1.getValueAt(selectedRow, 3).toString());

            // Llenar los campos de texto
            txtconsultorio.setText(id);
            txtnumero.setText(numero);
            txttipo.setText(tipo);
            txtturno.setText(turno);


            // Guardar el idDireccion en una variable
            // Variable global en la clase
            this.idhorarios = Integer.parseInt(idhorario);
            System.out.println(idhorarios);
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    eliminarDatos();    
    limpiar();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    actualizarDatos();         
    limpiar();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
limpiar();
     
    }//GEN-LAST:event_jButton4ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> combocedula;
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
    private javax.swing.JTextField txtconsultorio;
    private javax.swing.JTextField txtnumero;
    private javax.swing.JTextField txttipo;
    private javax.swing.JTextField txtturno;
    // End of variables declaration//GEN-END:variables
}
