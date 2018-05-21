package testutilities;

import com.ark.bank.IBankForCentralBank;
import com.ark.bank.IBankForClientSession;
import fontyspublisher.IRemotePublisherForListener;

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

    public IBankForCentralBank getIBankForCentralBank(String bankId, String URLBase) throws IOException {
        URL wsdlURL = null;
        try {
            wsdlURL = new URL(URLBase + bankId + "Service?wsdl");
        } catch (MalformedURLException e) {
        }

        waitForConnection();

        QName qname = new QName("http://bank.ark.com/", "BankService");
        Service service = Service.create(wsdlURL, qname);
        QName qnamePort = new QName("http://bank.ark.com/", "BankPort");

        return service.getPort(qnamePort, IBankForCentralBank.class);
    }

    public IBankForClientSession getIBankForClient(String bankId) throws IOException, NotBoundException {
        waitForConnection();

        Registry registry = LocateRegistry.getRegistry("localhost",1099);
        IRemotePublisherForListener remotePublisher = (IRemotePublisherForListener) registry.lookup("bankPublisher" + bankId);
        //remotePublisher.subscribeRemoteListener(this, "fondsen");
        return (IBankForClientSession) registry.lookup("bank" + bankId);
    }

    public void stopBank(String bankId) {
        Process process = bankProcesses.get(bankId);

        if (process != null) {
            process.destroy();
        }
    }

    public void startBank(String bankId, String URLBase) throws IOException {

        String javaPath;
        String jarPath;
        String jrePath = System.getProperty("java.home");
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.indexOf("win") >= 0) {
            jarPath = "..\\out\\artifacts\\Bank_jar\\";
            javaPath = jrePath + "\\bin\\java.exe";
        } else {
            jarPath = "..//out//artifacts//Bank_jar//";
            javaPath = jrePath + "//bin//java";
        }

        Process process = new ProcessBuilder(javaPath, "-jar", jarPath + JARFILE, bankId, URLBase).start();
        bankProcesses.put(bankId, process);
    }

    private void waitForConnection() {

        //TODO: Replace sleep with valid check.
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
