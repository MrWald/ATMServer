import java.sql.*;

class BankDatabase
{
    private static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/bank";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private Connection connection;

    BankDatabase() throws SQLException
    {
        connection = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
    }

    String[] getClientData(String cardNum) throws SQLException
    {
        Statement statement = connection.createStatement();
        ResultSet res = statement.executeQuery("SELECT * FROM accounts WHERE number='" + cardNum + "'");
        if(!res.next())
            return null;
        String[] data = new String[4];
        data[0] = res.getString(0);
        data[1] = res.getString(1);
        data[2] = res.getString(2);
        data[3] = res.getString(3);
        return data;
    }

    boolean setClientData(String cardNum, String[] newData) throws SQLException
    {
        Statement statement = connection.createStatement();
        return statement.execute("UPDATE accounts SET PIN='" + newData[0] + "', username='" + newData[1] + "', balance='" + newData[2] + "', limit='" + newData[3] + "' WHERE number='" + cardNum + "'");
    }
}
