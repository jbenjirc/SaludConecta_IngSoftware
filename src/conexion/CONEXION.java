package conexion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class CONEXION {
 
    public static Connection obtenerConexion() {
        String url = "jdbc:sqlserver://localhost:1433;" +
             "databaseName=CMZ_BETA;" +
             "user=sa;" +
             "password=12345678;" +
             "loginTimeout=30;" +
             "encrypt=true;" +
             "trustServerCertificate=true;";

        try {
            // Asegúrate de que el controlador JDBC de SQL Server esté cargado
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            Connection con = DriverManager.getConnection(url);
            System.out.println("¡Conexión establecida exitosamente!");
            return con;
        } catch (ClassNotFoundException e) {
            System.out.println("Controlador JDBC de SQL Server no encontrado: " + e.toString());
            return null;
        } catch (SQLException ex) {
            System.out.println("Fallo en la conexión: " + ex.toString());
            return null;
        }
    }

    public static void main(String[] args) {
        Connection con = obtenerConexion();
        if (con != null) {
            // Opcional: Hacer algo con la conexión
        } else {
            System.out.println("Fallo al establecer la conexión.");
        }
    }

    public Connection getConnection() {
        throw new UnsupportedOperationException("Not supported yet."); // Para cambiar el cuerpo de los métodos generados, elige Herramientas | Plantillas.
    }
}
