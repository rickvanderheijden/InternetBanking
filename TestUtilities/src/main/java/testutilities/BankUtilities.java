package testutilities;

import com.ark.bank.IBankForCentralBank;
import com.ark.bank.IBankForClientSession;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Thread.sleep;

/**
 * @author Rick van der Heijden
 */
public class BankUtilities {

    private static final String JARFILE = "Bank.jar";
    private final Map<String, Process> bankProcesses = new HashMap<>();

    /**
     * Gets the IBankForCentralBank interface
     * @param bankId The bank id to connect to
     * @param URLBase The URL that is used to connect
     * @return The connection to the interface. Null if not connected.
     */
    public IBankForCentralBank getIBankForCentralBank(String bankId, String URLBase) {
        URL wsdlURL;
        try {
            wsdlURL = new URL(URLBase + bankId + "Service?wsdl");
        } catch (MalformedURLException e) {
            return null;
        }

        waitForConnection();

        QName qname = new QName("http://bank.ark.com/", "BankService");
        Service service = Service.create(wsdlURL, qname);
        QName qnamePort = new QName("http://bank.ark.com/", "BankPort");

        return service.getPort(qnamePort, IBankForCentralBank.class);
    }

    /**
     * Gets the IBankForClientSession interface
     * @param bankId The bank id to connect to
     * @return The connection to the interface. Null if not connected.
     * @throws IOException
     * @throws NotBoundException
     */
    public IBankForClientSession getIBankForClient(String bankId) throws IOException, NotBoundException {
        waitForConnection();

        Registry registry = LocateRegistry.getRegistry("localhost",1099);
        return (IBankForClientSession) registry.lookup("bank" + bankId);
    }

    /**
     * Stop the bank
     * @param bankId The id of the bank to stop
     */
    public void stopBank(String bankId) {
        Process process = bankProcesses.get(bankId);

        if (process != null) {
            process.destroy();
        }
    }

    /**
     * Start the bank
     * @param bankId The id of the bank to start
     * @param URLBase The URL where the bank can be found
     * @param ipAddressOfCentralBank The ip address of the central bank
     * @throws IOException Thrown when the process can not be started
     */
    public void startBank(String bankId, String URLBase, String ipAddressOfCentralBank) throws IOException {

        String javaPath;
        String jarPath;
        String jrePath = System.getProperty("java.home");
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.contains("win")) {
            jarPath = "..\\out\\artifacts\\Bank_jar\\";
            javaPath = jrePath + "\\bin\\java.exe";
        } else {
            jarPath = "..//out//artifacts//Bank_jar//";
            javaPath = jrePath + "//bin//java";
        }

        Process process = new ProcessBuilder(javaPath, "-jar", jarPath + JARFILE, bankId, URLBase, ipAddressOfCentralBank).start();
        bankProcesses.put(bankId, process);
    }

    private void waitForConnection() {

        //TODO: Replace sleep with valid check.
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
