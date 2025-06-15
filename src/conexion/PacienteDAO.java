/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author usuario
 */
public class PacienteDAO {
    
 public static void sincronizarPacientesExistentes() {
        String sql =
          "UPDATE p\n" +
          "   SET p.id_usuario = u.id_usuario\n" +
          "FROM Paciente p\n" +
          "JOIN Usuario u\n" +
          "  ON LOWER(u.usuario_nombre) = LOWER(\n" +
          "         p.pac_nombre + '_' + p.pac_paterno + '_' + p.pac_materno\n" +
          "       )\n" +
          "WHERE u.id_tipoUsuario = 3;";

        try (
            Connection con = CONEXION.obtenerConexion();
            PreparedStatement pst = con.prepareStatement(sql);
        ) {
            int count = pst.executeUpdate();
            System.out.println("DEBUG: Pacientes sincronizados = " + count);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error al sincronizar Paciente↔Usuario: " + ex.getMessage());
        }
    }
}