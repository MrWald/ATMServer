package kmalfa;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

public interface ReceiveMessageInterface extends Remote {

    String verifyPIN(String cardNumber, int pinVal, int operation) throws RemoteException;

    boolean withdrawMoney(String cardNumber, int pin, int operation, float val) throws RemoteException;

    Float getBalance(String cardNumber, int pin, int operation) throws RemoteException;

    boolean changePIN(String cardNumber, int oldPin, int newPIN, int operation) throws RemoteException;

    boolean replenishAccount(String cardNumber, int pin, int operation, float val) throws RemoteException;

    boolean transfer(String from, String to, int pin, int operation, float val) throws RemoteException;

    boolean setAutoTransfer(String from, String to, int pin, int operation, float val, Date date) throws RemoteException;

    String getAutoTransfers(String from, int pin, int operation) throws RemoteException;

    boolean removeAutoTransfer(String from, int pinVal, int operation, int index) throws RemoteException;
}