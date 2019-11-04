import java.rmi.*;
import java.util.Date;
import java.util.Map;

public interface ReceiveMessageInterface extends Remote
{
    String verifyPIN(String cardNumber, int pinVal);
    boolean withdrawMoney(String cardNumber, int val);
    Integer getBalance(String cardNumber);
    boolean changePIN(String cardNumber, int newPIN);
    boolean replenishAccount(String cardNumber, int val);
    boolean transfer(String from, String to, int val);
    boolean setAutoTransfer(String from, String to, int val, Date date);
    Map<String, Integer> messageToPerform(String src, String out, int amount, int type, Date date);
}