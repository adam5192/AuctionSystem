import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles connection to the SQL database.
 * Ensures the database path is correct relative to project path.
 */
public class DatabaseConnection {
	private static String dbPath;

	public static void initialize(String absolutePath) {
        dbPath = absolutePath;
        System.out.println("Database path initialized to: " + dbPath);
    }

	public static Connection getConnection() throws SQLException {
	    if (dbPath == null || dbPath.isEmpty()) {
	        throw new RuntimeException("Database path is not set. Ensure initialize() is called before getConnection().");
	    }

	    try {
	        // Explicitly load SQLite JDBC driver
	        Class.forName("org.sqlite.JDBC");
	    } catch (ClassNotFoundException e) {
	        throw new RuntimeException("Failed to load SQLite JDBC driver", e);
	    }

	    // Return a connection to the SQLite database
	    return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
	}

}
