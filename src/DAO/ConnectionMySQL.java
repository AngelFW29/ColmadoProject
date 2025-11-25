package DAO;

import java.sql.*;

public class ConnectionMySQL {
    private static final String HOST = "localhost:3306";
    private static final String USER = "root";
    private static final String PASSWORD = "Brijo-0505";
    private static final String DATABASE = "Colmado";

    private Connection connection;

    private static ConnectionMySQL instance;

    private ConnectionMySQL() {
        connect();
    }

    public static synchronized ConnectionMySQL getInstance() {
        if (instance == null) {
            instance = new ConnectionMySQL();
        } else {
            try {
                if (instance.getConnection().isClosed()) {
                    instance.connect();
                }
            } catch (SQLException e) {
                System.out.println("Error al verificar el estado de la conexión: " + e.getMessage());
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + HOST + "/" + DATABASE + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

            connection = DriverManager.getConnection(url, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos: " + DATABASE);

        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver MySQL no encontrado.");
        } catch (SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Para preparar sentencias
    public PreparedStatement prepare(String sql) throws SQLException {
        if (connection == null || connection.isClosed()) {
            connect();
        }
        return connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
    }

    // Para SELECT (Devuelve ResultSet)
    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = prepare(sql);
        setParams(stmt, params);
        return stmt.executeQuery();
    }

    // Para INSERT, UPDATE, DELETE (Devuelve int)
    public int executeUpdate(String sql, Object... params) throws SQLException {
        try (PreparedStatement stmt = prepare(sql)) {
            setParams(stmt, params);
            return stmt.executeUpdate();
        }
    }

    private void setParams(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }
}