package kmalfa;
import java.rmi.*;
import java.util.Date;

public interface ReceiveMessageInterface extends Remote
{
    String verifyPIN(String cardNumber, int pinVal) throws RemoteException;
    boolean withdrawMoney(String cardNumber, float val) throws RemoteException;
    Float getBalance(String cardNumber) throws RemoteException;
    boolean changePIN(String cardNumber, int newPIN) throws RemoteException;
    boolean replenishAccount(String cardNumber, float val) throws RemoteException;
    boolean transfer(String from, String to, float val) throws RemoteException;
    boolean setAutoTransfer(String from, String to, float val, Date date) throws RemoteException;
    String getAutoTransfers(String from) throws RemoteException;
    boolean removeAutoTransfer(String from, String to, float val, Date date) throws RemoteException;
}