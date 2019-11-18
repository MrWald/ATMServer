package kmalfa;

import kmalfa.utils.PinCodeAnalyzer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class RmiServer extends java.rmi.server.UnicastRemoteObject implements ReceiveMessageInterface {
    private static final int PORT = 1099;
    private static final String REGISTRY_NAME = "bankServer";
    private BankDatabase bankDB;
    private List<AutoTransfer> autoTransfers;

    public RmiServer(String address) throws RemoteException, SQLException {
        System.out.println("This address = " + address + ", Port = " + PORT);
        Registry registry;
        try {
            registry = LocateRegistry.createRegistry(PORT);
        } catch (RemoteException e) {
            registry = LocateRegistry.getRegistry(PORT);
        }
        try {
            registry.bind(REGISTRY_NAME, this);
        } catch (AlreadyBoundException e) {
            registry.rebind(REGISTRY_NAME, this);
        }
        bankDB = new BankDatabase();
        autoTransfers = new ArrayList<>();
    }

    private static float round(float value) {
        BigDecimal bd = new BigDecimal(Float.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    @Override
    public String verifyPIN(String cardNumber, int pinVal, int operation) {
        String[] data;
        data = bankDB.getClientData(cardNumber);
        if (data.length == 0)
            return "";
        pinVal = PinCodeAnalyzer.getPin(pinVal, operation);
        System.out.println(data[1] + "  " + pinVal);
        if (data[0].equals(String.valueOf(pinVal))) {
            return data[1];
        } else {
            return "";
        }
    }

    @Override
    public boolean withdrawMoney(String cardNumber, int pinVal, int operation, float withdrawMoney) {
        return !verifyPIN(cardNumber, pinVal, operation).isEmpty() && withdrawMoney(cardNumber, withdrawMoney);
    }

    boolean withdrawMoney(String cardNum, float val) {
        String[] data;
        data = bankDB.getClientData(cardNum);
        float bal = Float.parseFloat(data[2]);
        float lim = Float.parseFloat(data[3]);
        if (bal - val < lim)
            return false;
        bal -= val;
        data[2] = String.valueOf(round(bal));
        try {
            return bankDB.setClientData(cardNum, data);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Float getBalance(String cardNumber, int pinVal, int operation) {
        if (verifyPIN(cardNumber, pinVal, operation).isEmpty())
            return (float) -1;
        String[] data;
        data = bankDB.getClientData(cardNumber);
        System.out.println("Balance Info: " + data[2]);
        return Float.parseFloat(data[2]);
    }

    @Override
    public boolean changePIN(String cardNumber, int oldPin, int newPIN, int operation) {
        if (verifyPIN(cardNumber, oldPin, operation).isEmpty())
            return false;
        System.out.println("Card: " + cardNumber);
        String[] data = bankDB.getClientData(cardNumber);
        data[0] = String.valueOf(PinCodeAnalyzer.getPin(newPIN, operation));
        try {
            return bankDB.setClientData(cardNumber, data);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean replenishAccount(String cardNumber, int pinVal, int operation, float val) {
        return verifyPIN(cardNumber, pinVal, operation) != null && replenishAccount(cardNumber, val);
    }

    boolean replenishAccount(String cardNum, float val) {
        String[] data;
        data = bankDB.getClientData(cardNum);
        float bal = Float.parseFloat(data[2]);
        bal += val;
        data[2] = String.valueOf(round(bal));
        try {
            return bankDB.setClientData(cardNum, data);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean transfer(String from, String to, int pinVal, int operation, float val) {
        return bankDB.getClientData(to) != null && withdrawMoney(from, pinVal, operation, val) && replenishAccount(to, val);
    }

    @Override
    public boolean setAutoTransfer(String from, String to, int pinVal, int operation, float val, Date date) {
        if (verifyPIN(from, pinVal, operation).isEmpty() || bankDB.getClientData(to).length == 0)
            return false;
        autoTransfers.add(new AutoTransfer(from, to, val, date, this));
        return true;
    }

    @Override
    public String getAutoTransfers(String from, int pinVal, int operation) {
        if (verifyPIN(from, pinVal, operation).isEmpty())
            return "";
        StringBuilder res = new StringBuilder();
        res.append('[');
        for (AutoTransfer at : autoTransfers) {
            if (res.length() > 1)
                res.append(',');
            if (at.getFrom().equals(from)) {
                res.append("{to:'").append(at.getTo()).append("',value:'").append(at.getValue()).append("',period:'").append(at.getPeriod().getTime()).append("'}");
            }
        }
        return res.append(']').toString();
    }

    @Override
    public boolean removeAutoTransfer(String from, int pinVal, int operation, int index) {
        if (verifyPIN(from, pinVal, operation).isEmpty())
            return false;
        try {
            autoTransfers.get(index).stop();
            autoTransfers.remove(index);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RmiServer rmiServer = (RmiServer) o;
        return Objects.equals(bankDB, rmiServer.bankDB) &&
                Objects.equals(autoTransfers, rmiServer.autoTransfers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), bankDB, autoTransfers);
    }
}