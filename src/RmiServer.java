import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RmiServer extends java.rmi.server.UnicastRemoteObject implements ReceiveMessageInterface
{
    private Map<String, Integer> mp = new HashMap<>();
    private static final int PORT = 3232; // registry port
    private BankDatabase bankDB;

    private RmiServer(String address, int port) throws RemoteException, SQLException
    {
        System.out.println("This address = " + address + ", Port = " + port);
        // create the registry and bind the name and object.
        // rmi registry for lookup the remote objects.
        Registry registry = LocateRegistry.createRegistry(port);
        registry.rebind("bankServer", this);
        bankDB = new BankDatabase();
    }

    static public void main(String[] args)
    {
        try
        {
            // get the address of this host.
            new RmiServer(InetAddress.getLocalHost().getHostAddress(), PORT);
        }
        catch (RemoteException e)
        {
            System.err.println("Cannot get the Server registered. Exiting" + e.getMessage());
            System.exit(1);
        }
        catch(UnknownHostException e)
        {
            System.err.println("Cannot get internet address.");
        }
        catch (SQLException e)
        {
            System.err.println("Cannot connect to the database. " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public String verifyPIN(String cardNumber, int pinVal)
    {
        String[] data;
        try
        {
            data = bankDB.getClientData(cardNumber);
        }
        catch (SQLException e)
        {
            return null;
        }
        System.out.println(data[1] + "  " + pinVal);
        if (data[0].equals(String.valueOf(pinVal)))
        {
            return data[1];
        }
        else
        {
            return null;
        }
    }

    @Override
    public boolean withdrawMoney(String cardNumber, int withdrawMoney)
    {
        String[] data;
        try
        {
            data = bankDB.getClientData(cardNumber);
        }
        catch (SQLException e)
        {
            return false;
        }
        int bal = Integer.parseInt(data[2]);
        int lim = Integer.parseInt(data[3]);
        if (bal - withdrawMoney < lim)
            return false;
        bal -= withdrawMoney;
        data[2] = String.valueOf(bal);
        try
        {
            return bankDB.setClientData(cardNumber, data);
        }
        catch (Exception e)
        {
            return false;
        }
    }

    @Override
    public Integer getBalance(String cardNumber)
    {
        String[] data;
        try
        {
            data = bankDB.getClientData(cardNumber);
        }
        catch (SQLException e)
        {
            return null;
        }
        System.out.println("Balance Info: " + data[2]);
        return Integer.parseInt(data[2]);
    }

    @Override
    public boolean changePIN(String cardNumber, int newPIN)
    {
        System.out.println("Card: " + cardNumber);
        String[] data;
        try
        {
            data = bankDB.getClientData(cardNumber);
        }
        catch (SQLException e)
        {
            return false;
        }
        data[0] = String.valueOf(newPIN);
        try
        {
            return bankDB.setClientData(cardNumber, data);
        }
        catch (Exception e)
        {
            return false;
        }
    }

    @Override
    public boolean replenishAccount(String cardNumber, int val)
    {
        String[] data;
        try
        {
            data = bankDB.getClientData(cardNumber);
        }
        catch (SQLException e)
        {
            return false;
        }
        int bal = Integer.parseInt(data[2]);
        bal += val;
        data[2] = String.valueOf(bal);
        try
        {
            return bankDB.setClientData(cardNumber, data);
        }
        catch (Exception e)
        {
            return false;
        }
    }

    @Override
    public boolean transfer(String from, String to, int val)
    {
        return withdrawMoney(from, val) && replenishAccount(to, val);
    }

    @Override
    public boolean setAutoTransfer(String from, String to, int val, Date date)
    {
        return false;
    }

    @Override
    public Map<String, Integer> messageToPerform(String src, String out, int val, int type, Date date)
    {
        mp.clear();
        switch (type)
        {
            case 1:
                if (withdrawMoney(src, val))
                {
                    mp.put("isSuccess", 1);
                }
                else
                {
                    mp.put("isSuccess", 0);
                }
                return mp;
            case 2:
                Integer bal = getBalance(src);
                if (bal != -1.0)
                {
                    mp.put("isSuccess", 1);
                    mp.put("balance", bal);
                }
                else
                {
                    mp.put("isSuccess", 0);
                    mp.put("balance", -1);
                }
                return mp;
            case 3:
                if (changePIN(src, val))
                {
                    mp.put("isSuccess", 1);
                }
                else
                {
                    mp.put("isSuccess", 0);
                }
                return mp;
            case 4:
                if (replenishAccount(src, val))
                {
                    mp.put("isSuccess", 1);
                }
                else
                {
                    mp.put("isSuccess", 0);
                }
                return mp;
            case 5:
                if (transfer(src, out, val))
                {
                    mp.put("isSuccess", 1);
                }
                else
                {
                    mp.put("isSuccess", 0);
                }
                return mp;
            case 6:
                if (setAutoTransfer(src, out, val, date))
                {
                    mp.put("isSuccess", 1);
                }
                else
                {
                    mp.put("isSuccess", 0);
                }
                return mp;
            default:
                return mp;
        }
    }
}