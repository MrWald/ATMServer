package kmalfa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class RmiServer extends java.rmi.server.UnicastRemoteObject implements ReceiveMessageInterface {
    private static final int PORT = 1099;
    private static final String REGISTRY_NAME = "bankServer";
    private BankDatabase bankDB;
    private Map<AutoTransfer, Thread> autoTransfers;

    private RmiServer(String address, int port) throws RemoteException, SQLException {
        System.out.println("This address = " + address + ", Port = " + port);
        Registry registry;
        try {
            registry = LocateRegistry.createRegistry(port);
        } catch (RemoteException e) {
            registry = LocateRegistry.getRegistry(port);
        }
        try {
            registry.bind(REGISTRY_NAME, this);
        } catch (AlreadyBoundException e) {
            registry.rebind(REGISTRY_NAME, this);
        }
        bankDB = new BankDatabase();
        autoTransfers = new HashMap<>();
    }

    static public void main(String[] args) {
        try {
            String externalIp = new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream())).readLine();
            externalIp = externalIp.length() == 0 ? InetAddress.getLocalHost().getHostAddress() : externalIp;
            System.setProperty("java.rmi.server.hostname", externalIp/*"localhost"*/);
            new RmiServer(externalIp, PORT);
        } catch (RemoteException e) {
            System.err.println("Cannot get the Server registered. Exiting..." + e.getMessage());
            System.exit(1);
        } catch (UnknownHostException e) {
            System.err.println("Cannot get internet address: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Cannot get external ip address: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Cannot connect to the database. " + e.getMessage());
            System.exit(1);
        }
    }

    private static float round(float value) {
        BigDecimal bd = new BigDecimal(Float.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    @Override
    public String verifyPIN(String cardNumber, int pinVal) {
        String[] data;
        try {
            data = bankDB.getClientData(cardNumber);
        } catch (SQLException e) {
            return null;
        }
        if (data == null)
            return null;
        System.out.println(data[1] + "  " + pinVal);
        if (data[0].equals(String.valueOf(pinVal))) {
            return data[1];
        } else {
            return null;
        }
    }

    @Override
    public boolean withdrawMoney(String cardNumber, float withdrawMoney) {
        String[] data;
        try {
            data = bankDB.getClientData(cardNumber);
        } catch (SQLException e) {
            return false;
        }
        float bal = Float.parseFloat(data[2]);
        float lim = Float.parseFloat(data[3]);
        if (bal - withdrawMoney < lim)
            return false;
        bal -= withdrawMoney;
        data[2] = String.valueOf(round(bal));
        try {
            return bankDB.setClientData(cardNumber, data);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Float getBalance(String cardNumber) {
        String[] data;
        try {
            data = bankDB.getClientData(cardNumber);
        } catch (SQLException e) {
            return null;
        }
        System.out.println("Balance Info: " + data[2]);
        return Float.parseFloat(data[2]);
    }

    @Override
    public boolean changePIN(String cardNumber, int newPIN) {
        System.out.println("Card: " + cardNumber);
        String[] data;
        try {
            data = bankDB.getClientData(cardNumber);
        } catch (SQLException e) {
            return false;
        }
        data[0] = String.valueOf(newPIN);
        try {
            return bankDB.setClientData(cardNumber, data);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean replenishAccount(String cardNumber, float val) {
        String[] data;
        try {
            data = bankDB.getClientData(cardNumber);
        } catch (SQLException e) {
            return false;
        }
        float bal = Float.parseFloat(data[2]);
        bal += val;
        data[2] = String.valueOf(round(bal));
        try {
            return bankDB.setClientData(cardNumber, data);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean transfer(String from, String to, float val) {
        return withdrawMoney(from, val) && replenishAccount(to, val);
    }

    @Override
    public boolean setAutoTransfer(String from, String to, float val, Date date) {
        AtomicBoolean res = new AtomicBoolean(false);
        Thread autoTransfer = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(date.getTime());
                } catch (InterruptedException e) {
                    res.set(false);
                    e.printStackTrace();
                }
                res.set(transfer(from, to, val));
            }
        });
        autoTransfers.put(new AutoTransfer(from, to, val, date), autoTransfer);
        autoTransfer.start();
        return res.get();
    }

    @Override
    public String getAutoTransfers(String from) {
        StringBuilder res = new StringBuilder();
        res.append('[');
        for (AutoTransfer at : autoTransfers.keySet()) {
            if (res.length() > 1)
                res.append(',');
            if (at.getFrom().equals(from)) {
                res.append("{to:").append(at.getTo()).append(",value:").append(at.getValue()).append(",period:").append(at.getPeriod().toString()).append('}');
            }
        }
        return res.append(']').toString();
    }

    @Override
    public boolean removeAutoTransfer(String from, String to, float val, Date date) {
        try {
            AutoTransfer at = new AutoTransfer(from, to, val, date);
            autoTransfers.get(at).join();
            autoTransfers.remove(at);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}