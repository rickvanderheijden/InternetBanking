package utilities;

import com.ark.centralbank.IBankForCentralBank;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class BankUtilities {

    private static final String JARPATH = "..\\out\\artifacts\\Bank_jar\\";
    private static final String JARFILE = "Bank.jar";
    private Process processBank;
    private Service service;
    private QName qnamePort;

    public IBankForCentralBank startBankForCentralBank() throws IOException {
        startBank();
        return service.getPort(qnamePort, IBankForCentralBank.class);
    }

    public void stopBank() {
        if (processBank != null) {
            processBank.destroy();
        }
    }

    private void startBank() throws IOException {

        String cmd = "C:\\Program Files\\Java\\jdk1.8.0_162\\bin\\java.exe -jar " + JARPATH + JARFILE;

        processBank = Runtime.getRuntime().exec(cmd);

        URL wsdlURL = null;
        try {
            wsdlURL = new URL("http://localhost:8080/Bank?wsdl");
        } catch (MalformedURLException e) {
        }

        QName qname = new QName("http://bank.ark.com/", "BankService");
        service = Service.create(wsdlURL, qname);
        qnamePort = new QName("http://bank.ark.com/", "BankPort");
    }
}
