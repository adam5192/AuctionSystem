import java.sql.Connection;
import java.sql.Statement;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
/**
 * Initializes database.
 * Executes "setup.sql" to create the tables needed for the db.
 * @author adamm
 *
 */
public class DatabaseInitializer {

    public static void executeSqlScript(String filePath) {
        try (Connection conn = DatabaseConnection.getConnection();
             BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            StringBuilder sqlBuilder = new StringBuilder();
            String line;

            // Read the file line by line
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip comments and empty lines
                if (line.startsWith("--") || line.isEmpty()) {
                    continue;
                }
                
                sqlBuilder.append(line).append(" ");

                // Execute each statement individually when we find a semicolon
                if (line.endsWith(";")) {
                    String sql = sqlBuilder.toString().trim();

                    // Execute the SQL statement and reset the builder
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(sql); // Execute the current SQL statement
                    } catch (SQLException e) {
                        System.err.println("Error executing statement: " + sql);
                        e.printStackTrace();
                    }

                    sqlBuilder.setLength(0); // Clear the builder for the next statement
                }
            }
            System.out.println("SQL script executed successfully.");

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
