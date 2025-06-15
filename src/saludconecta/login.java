/*
PONGAN SUS NOMBRES AQUI


*/
package saludconecta;

import conexion.CONEXION;
import conexion.PacienteDAO;
import java.awt.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

/**
 *
 * @author usuario
 */
public class login extends javax.swing.JFrame {

    /**
     * Creates new form MENUSECRETARIA
     */
     static  int idusuarios=0;    
     static  int idempleado=0;

    Map<String, Integer> idMap = new HashMap<>();
    
    public login() {
        initComponents();
        setIconImage(new ImageIcon(getClass().getResource("/IMAGENES/CABEZA.PNG")).getImage());
        this.setLocationRelativeTo(null);  
        poblarComboBox(tipocombox,idMap);
    }

 public static void poblarComboBox(JComboBox<String> tipocombox, Map<String, Integer> idMap) {
    String query = "SELECT id_tipoUsuario, tipo_usuarioNombre FROM Tipo_Usuario WHERE tipo_usuarioNombre != 'Paciente'";
    Connection con = CONEXION.obtenerConexion();
    
    if (con != null) {
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_tipoUsuario");
                String nombre = rs.getString("tipo_usuarioNombre");
                tipocombox.addItem(nombre);
                idMap.put(nombre, id);
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

        jMenuItem1 = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        TXTUSUSECRE1 = new javax.swing.JTextField();
        TXTCONTRASECRE1 = new javax.swing.JPasswordField();
        jButton2 = new javax.swing.JButton();
        tipocombox = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        txtusuario2 = new javax.swing.JTextField();
        txtpassword2 = new javax.swing.JPasswordField();
        jButton3 = new javax.swing.JButton();

        jMenuItem1.setText("jMenuItem1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("MENU DE LOGGIN");
        setAutoRequestFocus(false);
        setBackground(new java.awt.Color(255, 255, 255));
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(43, 43, 43));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane1.setBackground(new java.awt.Color(43, 43, 43));
        jTabbedPane1.setAlignmentX(10.0F);

        jPanel2.setBackground(new java.awt.Color(43, 43, 43));

        jLabel5.setFont(new java.awt.Font("Gill Sans Ultra Bold", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("INICIO  SESION ");

        TXTUSUSECRE1.setText("Usuario");
        TXTUSUSECRE1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TXTUSUSECRE1ActionPerformed(evt);
            }
        });

        TXTCONTRASECRE1.setText("Contraseña");

        jButton2.setBackground(new java.awt.Color(31, 106, 165));
        jButton2.setFont(new java.awt.Font("Humnst777 BT", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("INICIAR SESIÓN");
        jButton2.setBorder(null);
        jButton2.setBorderPainted(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(TXTCONTRASECRE1, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                    .addComponent(tipocombox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TXTUSUSECRE1))
                .addGap(20, 20, 20))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addComponent(jLabel5)
                .addContainerGap(86, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(TXTUSUSECRE1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(TXTCONTRASECRE1, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                .addGap(13, 13, 13)
                .addComponent(tipocombox, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        jTabbedPane1.addTab("EMPLEADO", jPanel2);

        jPanel3.setBackground(new java.awt.Color(43, 43, 43));

        jLabel6.setFont(new java.awt.Font("Gill Sans Ultra Bold", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("INICIO  SESION ");

        txtusuario2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtusuario2.setText("Usuario");
        txtusuario2.setBorder(null);
        txtusuario2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtusuario2ActionPerformed(evt);
            }
        });

        txtpassword2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txtpassword2.setText("Password");
        txtpassword2.setBorder(null);

        jButton3.setBackground(new java.awt.Color(31, 106, 165));
        jButton3.setFont(new java.awt.Font("Humnst777 BT", 1, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("INICIAR SESIÓN");
        jButton3.setBorder(null);
        jButton3.setBorderPainted(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtpassword2, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                    .addComponent(txtusuario2))
                .addGap(20, 20, 20))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addComponent(jLabel6)
                .addContainerGap(86, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(txtusuario2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtpassword2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );

        jTabbedPane1.addTab("PACIENTE", jPanel3);

        jPanel1.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 360, 360));
        jTabbedPane1.getAccessibleContext().setAccessibleName("Paciente");
        jTabbedPane1.getAccessibleContext().setAccessibleDescription("");

        getContentPane().add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 410, 390));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
   String usuario = TXTUSUSECRE1.getText();
String contra = TXTCONTRASECRE1.getText();
String selectedNombre = (String) tipocombox.getSelectedItem();
Integer id = idMap.get(selectedNombre);

if (usuario.isEmpty() || contra.isEmpty()) {
    JOptionPane.showMessageDialog(null, "Por favor, ingrese su usuario y contraseña.");
    return;
}

Connection con = null;
PreparedStatement pst = null;
ResultSet rs = null;

try {
    con = CONEXION.obtenerConexion();
    if (con != null) {
        // Consulta SQL para verificar el usuario y contraseña, y el estado activo del empleado
      String sql = "SELECT u.id_usuario, u.usuario_nombre, u.id_tipoUsuario, u.password, e.id_empleadoEstatus, e.id_empleado " +
                         "FROM Usuario u " +
                         "INNER JOIN Empleado e ON u.id_usuario = e.id_usuario " +
                         "WHERE u.usuario_nombre = ? AND u.password = ? AND u.id_tipoUsuario = ?";
                 
        pst = con.prepareStatement(sql);
        pst.setString(1, usuario);
        pst.setString(2, contra);
        pst.setInt(3, id);

        rs = pst.executeQuery();

        if (rs.next()) {
            int idUsuario = rs.getInt("id_usuario");
            int tipoUsuario = rs.getInt("id_tipoUsuario");
            int idEmpleadoEstatus = rs.getInt("id_empleadoEstatus");
            System.out.println(rs.getInt("id_empleado"));

            if (idEmpleadoEstatus == 1) {
                JOptionPane.showMessageDialog(null, "Usuario activo, inicio de sesión exitoso.");

                // Abrir formularios según el tipo de usuario
                if (tipoUsuario == 1) {
                    dispose();
                    DOCTOR doctorForm = new DOCTOR();
                    doctorForm.setVisible(true);
                     login.idempleado=rs.getInt("id_empleado"); 
                       System.out.println("data: "+login.idempleado);
                } else if (tipoUsuario == 2) {
                    dispose();
                    RECEPCIONISTA recepcionistaForm = new RECEPCIONISTA();
                    recepcionistaForm.setVisible(true);
                    
                } else if (tipoUsuario == 3) {
                    dispose();
                    PACIENTE pacienteForm = new PACIENTE();
                    pacienteForm.setVisible(true);
                    login.idusuarios=rs.getInt("id_usuario"); 
                    System.out.println("data: "+login.idusuarios);
                } else {
                    JOptionPane.showMessageDialog(null, "Bienvenido, pero no eres un doctor.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Usuario inactivo. Por favor, contacta al administrador.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos.");
        }
    } else {
        JOptionPane.showMessageDialog(null, "Error al conectar a la base de datos.");
    }
} catch (SQLException e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(null, "Error al realizar la consulta.");
} finally {
    try {
        if (rs != null) rs.close();
        if (pst != null) pst.close();
        if (con != null) con.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}



    }//GEN-LAST:event_jButton2ActionPerformed

    private void TXTUSUSECRE1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TXTUSUSECRE1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TXTUSUSECRE1ActionPerformed

    private void txtusuario2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtusuario2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtusuario2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
     // 1) Obtengo usuario y contraseña de las cajas de texto
    String usuario = txtusuario2.getText().trim();
    String contra  = txtpassword2.getText().trim();

    // 2) Validación básica: no permitir campos vacíos
    if (usuario.isEmpty() || contra.isEmpty()) {
        JOptionPane.showMessageDialog(null,
            "Por favor, ingrese su usuario y contraseña.");
        return;
    }

    Connection con = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    try {
        // 3) Abro la conexión
        con = CONEXION.obtenerConexion();
        if (con == null) {
            JOptionPane.showMessageDialog(null,
                "Error al conectar a la base de datos.");
            return;
        }

        // 4) Defino la consulta con LEFT JOIN para pacientes (id_tipoUsuario = 3)
        String sql =
          "SELECT u.id_usuario,            " +
          "       u.usuario_nombre,        " +
          "       u.id_tipoUsuario,        " +
          "       u.password,              " +
          "       e.id_empleadoEstatus,    " +
          "       e.id_empleado            " +
          "FROM [CMZ_BETA].[dbo].[Usuario] AS u   \n" +
          "LEFT JOIN [CMZ_BETA].[dbo].[Empleado] AS e \n" +
          "    ON u.id_usuario = e.id_usuario       \n" +
          "WHERE u.usuario_nombre  = ?             \n" +
          "  AND u.password        = ?             \n" +
          "  AND u.id_tipoUsuario  = 3;";

        pst = con.prepareStatement(sql);
        pst.setString(1, usuario);
        pst.setString(2, contra);
        rs = pst.executeQuery();

        if (rs.next()) {
            // 5) Obtengo id_empleadoEstatus con getObject para detectar NULL
            Integer idEmpleadoEstatus = rs.getObject("id_empleadoEstatus", Integer.class);

            if (idEmpleadoEstatus == null || idEmpleadoEstatus == 1) {
                // 6) Login válido: guardamos ID de usuario y abrimos ventana de paciente
                int idUsuario = rs.getInt("id_usuario");
                login.idusuarios = idUsuario;
//borrar esde qui
String curp = null;
String sqlCurp = "SELECT curp FROM Paciente WHERE id_usuario = ?";
try (PreparedStatement psCurp = con.prepareStatement(sqlCurp)) {
    psCurp.setInt(1, idUsuario);
    try (ResultSet rsCurp = psCurp.executeQuery()) {
        if (rsCurp.next()) {
            curp = rsCurp.getString("curp");
        }
    }
}
if (curp == null) {
    JOptionPane.showMessageDialog(null,
        "No se encontró CURP para este usuario.");
    return;
}
//hasta aca
                // ───> LÍNEA QUE DEBES AGREGAR:
                System.out.println("DEBUG en Login: idUsuario = " + login.idusuarios);
                PacienteDAO.sincronizarPacientesExistentes();

                dispose();
              PACIENTE pacienteForm = new PACIENTE(curp);
                pacienteForm.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null,
                    "Su cuenta está inactiva. Contacte al administrador.");
            }
        } else {
            JOptionPane.showMessageDialog(null,
                "Usuario o contraseña incorrectos.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null,
            "Error al realizar la consulta: " + e.getMessage());
    } finally {
        try {
            if (rs  != null) rs.close();
            if (pst != null) pst.close();
            if (con != null) con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
  //si no jala borrrar esto y quitar el de arriba solo borrar comentarios
  
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new login().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPasswordField TXTCONTRASECRE1;
    private javax.swing.JTextField TXTUSUSECRE1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JComboBox<String> tipocombox;
    private javax.swing.JPasswordField txtpassword2;
    private javax.swing.JTextField txtusuario2;
    // End of variables declaration//GEN-END:variables
}
