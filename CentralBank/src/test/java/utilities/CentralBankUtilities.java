package utilities;

import com.ark.centralbank.ICentralBankRegister;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class CentralBankUtilities {

    private static final String JARPATH = "out\\artifacts\\CentralBank_jar\\";
    private static final String JARFILE = "CentralBank.jar";
    private Process processCentralBank;

    public ICentralBankRegister startCentralBank() throws IOException {

        String cmd = "C:\\Program Files\\Java\\jdk1.8.0_162\\bin\\java.exe -jar " + JARPATH + JARFILE;

        processCentralBank = Runtime.getRuntime().exec(cmd);

        URL wsdlURL;
        try {
            wsdlURL = new URL("http://localhost:8080/CentralBank?wsdl");
        } catch (MalformedURLException e) {
            return null;
        }

        QName qname = new QName("http://centralbank.ark.com/", "CentralBankService");
        Service service = Service.create(wsdlURL, qname);
        QName qnamePort = new QName("http://centralbank.ark.com/", "CentralBankPort");

        return service.getPort(qnamePort, ICentralBankRegister.class);
    }

    public void stopCentralBank() {
        if (processCentralBank != null) {
            processCentralBank.destroy();
        }
    }
}
