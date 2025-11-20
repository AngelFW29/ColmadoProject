package DAO;

import java.sql.*;


public class ConnectionMySQL {
    private final String host;
    private final String user;
    private final String password;
    private final String database;
    private Connection connection;

    public ConnectionMySQL(String host, String user, String password, String database) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.database = database;

    }

    public void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + host + "/" + database + "?useSSL=false&serverTimezone=UTC";

            connection = DriverManager.getConnection(url, user, password);

        } catch (ClassNotFoundException e) {
            System.out.println("Error: el driver de MySQL no se encuentra.");
        } catch (SQLException e) {
            System.out.println("Error al conectar con MySQL: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public PreparedStatement prepare(String sql) throws SQLException {
        if (connection == null || connection.isClosed())
            connect();

        return connection.prepareStatement(sql);
    }       

    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = prepare(sql);
        setParams(stmt, params);

        return stmt.executeQuery();
    }

    public int executeUpdate(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = prepare(sql);
        setParams(stmt, params);

        return stmt.executeUpdate();
    }

    private void setParams(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

}
