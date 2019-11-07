import java.sql.*;
import org.flywaydb.core.Flyway;

class BankDatabase
{
    private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/bank";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private Connection connection;

    BankDatabase() throws SQLException
    {
        connection = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
        Flyway flyway = Flyway.configure().dataSource(CONNECTION_STRING, USERNAME, PASSWORD).load();
        flyway.migrate();
    }

    String[] getClientData(String cardNum) throws SQLException
    {
        Statement statement = connection.createStatement();
        ResultSet accountData = statement.executeQuery("SELECT PIN, username, balance, money_limit FROM accounts WHERE card_number='" + cardNum + "'");
        if(!accountData.next())
            return null;
        ResultSet userData = statement.executeQuery("SELECT first_name FROM users WHERE username='" + accountData.getString(1) + "'");
        if(!userData.next())
            return null;
        String[] data = new String[4];
        data[0] = accountData.getString(0);
        data[1] = userData.getString(0);
        data[2] = Double.toString(accountData.getDouble(2));
        data[3] = Double.toString(accountData.getDouble(3));
        return data;
    }

    boolean setClientData(String cardNum, String[] newData) throws SQLException
    {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT username FROM accounts WHERE card_number='" + cardNum + "'");
        if(!rs.next())
            return false;
        String username = rs.getString(0);
        return statement.execute("UPDATE accounts SET PIN='" + newData[0] + "', balance=" + newData[2] + ", money_limit=" + newData[3] + " WHERE card_number='" + cardNum + "'") && statement.execute("UPDATE users SET first_name='" + newData[1] + "' WHERE username='" + username + "'");
    }
}
