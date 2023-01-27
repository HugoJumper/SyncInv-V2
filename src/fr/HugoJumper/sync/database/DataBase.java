package fr.HugoJumper.sync.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.configuration.file.FileConfiguration;

public class DataBase {
    private Connection connection;

    private FileConfiguration fileConfiguration;

    public DataBase(FileConfiguration fileConfiguration) {
        this.fileConfiguration = fileConfiguration;
    }

    public void connect() throws SQLException {
        String url = "jdbc:" + getEngine() + "://" + getHost() + ":" + getPort() + "/" + getDatabaseName() + "?autoReconnect=true";
        this.connection = DriverManager.getConnection(url, getUsername(), getPassword());
        String createInvTable = "CREATE TABLE IF NOT EXISTS " + getSyncTableName() + " (id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT, player_name VARCHAR(255) NOT NULL, inventory BLOB NOT NULL, armor BLOB NOT NULL, enderchest BLOB NOT NULL, exp INT(11) DEFAULT 0, life INT(11) DEFAULT 0, feed INT(11) DEFAULT 0)";
        Statement statement = this.connection.createStatement();
        statement.execute(createInvTable);
        statement.close();
    }

    public void disconnect() {
        if (isConnected())
            try {
                this.connection.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public PreparedStatement prepareWrappedStatement(String query) {
        try {
            return this.connection.prepareStatement(query);
        } catch (SQLException e) {
            if (e.getMessage().contains("?autoReconnect")) {
                reloadConnexion();
                return prepareWrappedStatement(query);
            }
            return null;
        }
    }

    public void reloadConnexion() {
        disconnect();
        try {
            connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return (this.connection != null);
    }

    public Connection getConnection() {
        return this.connection;
    }

    private int getPort() {
        return this.fileConfiguration.getInt("database.port");
    }

    private String getHost() {
        return this.fileConfiguration.getString("database.host");
    }

    public String getSyncTableName() {
        return this.fileConfiguration.getString("database.syncTableName");
    }

    private String getPassword() {
        return this.fileConfiguration.getString("database.password");
    }

    private String getUsername() {
        return this.fileConfiguration.getString("database.username");
    }

    private String getEngine() {
        return this.fileConfiguration.getString("database.engine");
    }

    private String getDatabaseName() {
        return this.fileConfiguration.getString("database.database");
    }
}

