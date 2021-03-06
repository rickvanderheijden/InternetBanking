package testutilities;

import com.ark.centralbank.ICentralBankRegister;
import com.ark.centralbank.ICentralBankTransaction;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.Thread.sleep;

/**
 * @author Rick van der Heijden
 */
public class CentralBankUtilities {
    private static final String JARFILE = "CentralBank.jar";
    private Process processCentralBank;
    private Service service;
    private QName qnamePort;

    public ICentralBankTransaction getCentralBankTransaction() {
        return service.getPort(qnamePort, ICentralBankTransaction.class);
    }

    /**
     * Stop the central bank
     */
    public void stopCentralBank() {
        if (processCentralBank != null) {
            processCentralBank.destroy();
        }
    }

    /**
     * Start the central bank
     * @param ipAddress The ip address of the central bank
     * @throws IOException Thrown when the process can not be started
     */
    public void startCentralBank(String ipAddress) throws IOException {
        String javaPath;
        String jarPath;
        String jrePath = System.getProperty("java.home");
        String OS = System.getProperty("os.name").toLowerCase();
        if (OS.contains("win")) {
            jarPath = "..\\out\\artifacts\\CentralBank_jar\\";
            javaPath = jrePath + "\\bin\\java.exe";
        } else {
            jarPath = "..//out//artifacts//CentralBank_jar//";
            javaPath = jrePath + "//bin//java";
        }

        processCentralBank = new ProcessBuilder(javaPath, "-jar", jarPath + JARFILE, ipAddress).start();

        waitForConnection();

        URL wsdlURL;
        try {
            wsdlURL = new URL("http://localhost:8080/CentralBank?wsdl");
        } catch (MalformedURLException e) {
            return;
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
