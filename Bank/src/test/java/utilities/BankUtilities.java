package utilities;

import com.ark.bank.IBankForCentralBank;
import com.ark.bank.IBankForClientSession;
import fontyspublisher.IRemotePublisherForListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import static java.lang.Thread.sleep;

public class BankUtilities {

    private static final String JARPATH = "..\\out\\artifacts\\Bank_jar\\";
    private static final String JARFILE = "Bank.jar";
    private Process processBank;
    private Service service;
    private QName qnamePort;
    private IRemotePublisherForListener remotePublisher;

    public IBankForCentralBank startBankForCentralBank() throws IOException {
        startBank();

        URL wsdlURL = null;
        try {
            wsdlURL = new URL("http://localhost:1200/RABOService?wsdl");
        } catch (MalformedURLException e) {
        }

        waitForConnection();

        QName qname = new QName("http://bank.ark.com/", "BankService");
        service = Service.create(wsdlURL, qname);
        qnamePort = new QName("http://bank.ark.com/", "BankPort");

        return service.getPort(qnamePort, IBankForCentralBank.class);
    }

    public IBankForClientSession startBankForClient() throws IOException, NotBoundException {
        startBank();

        waitForConnection();

        Registry registry = LocateRegistry.getRegistry("localhost",1099);
        remotePublisher = (IRemotePublisherForListener) registry.lookup("bankPublisher");
        //remotePublisher.subscribeRemoteListener(this, "fondsen");
        return (IBankForClientSession) registry.lookup("bank");
    }

    public void stopBank() {
        if (processBank != null) {
            processBank.destroy();
        }
    }

    private void startBank() throws IOException {
        String jrePath = System.getProperty("java.home");
        String javaPath = jrePath + "\\bin\\java.exe";
        processBank = new ProcessBuilder(javaPath, "-jar", JARPATH + JARFILE).start();
    }

    private boolean waitForConnection() {

        //TODO: Replace sleep with valid check.
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return true;
    }
}
