package cancelaestacionamento;

import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class FXMLDocumentController implements Initializable {

    @FXML
    private Label label;

    @FXML
    private void abrePortaAction(ActionEvent event) {
        comPort.openPort();
    }

    @FXML
    private void fechaPortaAction(ActionEvent event) {
        comPort.closePort();
    }

    private static SerialPort comPort = SerialPort.getCommPorts()[0];

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        InputStream in = comPort.getInputStream();
        StringBuilder output = new StringBuilder();
        try {
            for (int j = 0; j < 1000; ++j) {
//                System.out.print((char) in.read());
                char ch = (char) in.read();
                output.append(ch);
            }
            System.out.println(output.toString());
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        comPort.closePort();
    }

}
