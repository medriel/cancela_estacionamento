package cancela_estacionamento;

import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

public class FXMLDocumentController implements Initializable {
    
    @FXML
    private ComboBox cbPortas;

    @FXML
    private Button btnConectar;

    private SerialPort porta;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        carregarPortas();
    }    
    
     private void carregarPortas() {
        SerialPort[] portNames = SerialPort.getCommPorts();

        for (SerialPort portName : portNames) {
            cbPortas.getItems().add(portName.getSystemPortName());
        }
    }
     
     @FXML
    private void btnConectarAction() {
        porta = SerialPort.getCommPort(cbPortas.getSelectionModel().getSelectedItem().toString());
        porta.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 10000, 10000);
        porta.setBaudRate(2000000);
        InputStream in = porta.getInputStream();

        Thread thread = new Thread() {

            public void run() {
                int availableBytes = 0;
                System.out.println(availableBytes);
                do {
                    try {
                        System.out.println("Thread");
                        availableBytes = porta.bytesAvailable();
                        System.out.println(availableBytes);
                        if (availableBytes > 0) {
                            byte[] buffer = new byte[1024];
                            int bytesRead = porta.readBytes(buffer, Math.min(buffer.length, porta.bytesAvailable()));
                            String response = new String(buffer, 0, bytesRead);
                            System.out.println(response);
                            // salvar no banco
                        }

                        Thread.sleep(1000);

                        in.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }while(availableBytes>-1);
            }
        };
        
        //for que ler do banco e atualiza a tabela

        if (btnConectar.getText().equals("Conectar")) {
            System.out.println("Conectar");
            porta.openPort();
            btnConectar.setText("Desconectar");
            cbPortas.setDisable(true);
            thread.start();
            
        } else {
            thread.interrupt();
            porta.closePort();
            cbPortas.setDisable(false);
            btnConectar.setText("Conectar");
        }
    }
}
