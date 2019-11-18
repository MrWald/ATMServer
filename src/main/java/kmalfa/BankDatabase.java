package kmalfa;

import java.io.Serializable;
import java.sql.*;
import java.util.Objects;

public class BankDatabase implements Serializable {
    private transient Connection connection;
    private static String password;

    BankDatabase() throws SQLException {
        final String CONNECTION_STRING = "jdbc:sqlserver://kmalfa.database.windows.net:1433;database=bank;user=boublik@kmalfa;password=" + password + ";encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30";
        connection = DriverManager.getConnection(CONNECTION_STRING);
    }

    public static void setPassword(String value) {
        password = value;
    }

    String[] getClientData(String cardNum) {
        String[] data = new String[4];
        try (Statement statement = connection.createStatement()) {
            try (ResultSet accountData = statement.executeQuery("SELECT pin, username, balance, money_limit FROM accounts WHERE card_number='" + cardNum + "';")) {
                if (!accountData.next())
                    return new String[0];
                data[0] = accountData.getString("pin");
                data[1] = accountData.getString("username");
                data[2] = Float.toString(accountData.getFloat("balance"));
                data[3] = Float.toString(accountData.getFloat("money_limit"));
                try (ResultSet userData = statement.executeQuery("SELECT first_name FROM users WHERE username='" + data[1] + "';")) {
                    if (!userData.next())
                        return new String[0];
                    data[1] = userData.getString("first_name");
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return new String[0];
        }
        return data;
    }

    boolean setClientData(String cardNum, String[] newData) {
        try (Statement statement = connection.createStatement()) {
            try (ResultSet rs = statement.executeQuery("SELECT username FROM accounts WHERE card_number='" + cardNum + "';")) {
                if (!rs.next())
                    return false;
                String username = rs.getString("username");
                statement.executeUpdate("UPDATE accounts SET pin='" + newData[0] + "', balance=" + newData[2] + ", money_limit=" + newData[3] + " WHERE card_number='" + cardNum + "';");
                statement.executeUpdate("UPDATE users SET first_name='" + newData[1] + "' WHERE username='" + username + "';");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankDatabase that = (BankDatabase) o;
        return Objects.equals(connection, that.connection);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connection);
    }
}
