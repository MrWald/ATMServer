package executor;

import kmalfa.BankDatabase;
import kmalfa.RmiServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String externalIp = "";
        try {
            externalIp = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("Cannot get internet address: " + e.getMessage());
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream()))) {
            externalIp = in.readLine();
            System.setProperty("java.rmi.server.hostname", externalIp);
        } catch (IOException e) {
            System.err.println("Cannot get external ip address: " + e.getMessage());
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Enter password to the 'bank' DB:");
            BankDatabase.setPassword(in.readLine());
            new RmiServer(externalIp);
            do {
                System.out.println("Enter 'stop' to stop the server");
            } while (!in.readLine().equalsIgnoreCase("stop"));
        } catch (IOException e) {
            System.err.println("Couldn't get input from user: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Cannot connect to the database. " + e.getMessage());
            System.exit(1);
        }
    }
}
