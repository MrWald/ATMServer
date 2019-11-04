import java.rmi.*;
import java.util.Date;
import java.util.Map;

public interface ReceiveMessageInterface extends Remote
{
    String verifyPIN(String cardNumber, int pinVal) throws RemoteException;
    boolean withdrawMoney(String cardNumber, int val) throws RemoteException;
    Integer getBalance(String cardNumber) throws RemoteException;
    boolean changePIN(String cardNumber, int newPIN) throws RemoteException;
    boolean replenishAccount(String cardNumber, int val) throws RemoteException;
    boolean transfer(String from, String to, int val) throws RemoteException;
    boolean setAutoTransfer(String from, String to, int val, Date date) throws RemoteException;
    String getAutoTransfers(String from) throws RemoteException;
    boolean removeAutoTransfer(String from, String to, int val, Date date) throws RemoteException;
    Map<String, String> messageToPerform(String src, String out, int amount, int type, Date date) throws RemoteException;
}