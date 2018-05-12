package utilities;

import com.ark.centralbank.ICentralBankRegister;
import com.ark.centralbank.ICentralBankTransaction;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import static java.lang.Thread.sleep;

/**
 * @author Rick van der Heijden
 */
public class CentralBankUtilities {

    private static final String JARPATH = "..\\out\\artifacts\\CentralBank_jar\\";
    private static final String JARFILE = "CentralBank.jar";
    private Process processCentralBank;
    private Service service;
    private QName qnamePort;

    public ICentralBankTransaction startCentralBankTransaction() throws IOException {
        startCentralBank();
        return service.getPort(qnamePort, ICentralBankTransaction.class);
    }

    public ICentralBankRegister startCentralBankRegister() throws IOException {
        startCentralBank();
        return service.getPort(qnamePort, ICentralBankRegister.class);
    }

    public void stopCentralBank() {
        if (processCentralBank != null) {
            processCentralBank.destroy();
        }
    }

    private void startCentralBank() throws IOException {

        String jrePath = System.getProperty("java.home");
        String javaPath = jrePath + "\\bin\\java.exe";
        processCentralBank = new ProcessBuilder(javaPath, "-jar", JARPATH + JARFILE).start();

        waitForConnection();

        URL wsdlURL = null;
        try {
            wsdlURL = new URL("http://localhost:8080/CentralBank?wsdl");
        } catch (MalformedURLException e) {
        }

        QName qname = new QName("http://centralbank.ark.com/", "CentralBankService");
        service = Service.create(wsdlURL, qname);
        qnamePort = new QName("http://centralbank.ark.com/", "CentralBankPort");
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
