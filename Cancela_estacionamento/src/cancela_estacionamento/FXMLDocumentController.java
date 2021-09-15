package cancela_estacionamento;

import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class FXMLDocumentController implements Initializable {

    @FXML
    private ComboBox cbPortas;

    @FXML
    private Button btnConectar;
    
    @FXML 
    private TableView tabela = new TableView();
    
    @FXML
    private TableColumn colStatus= new TableColumn();
    
    @FXML
    private TableColumn colData= new TableColumn();
    
    private SerialPort porta;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        carregarPortas();
        try {
            getConexao();
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            System.out.println(getConexao());
        } catch (SQLException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void carregarPortas() {
        SerialPort[] portNames = SerialPort.getCommPorts();

        for (SerialPort portName : portNames) {
            cbPortas.getItems().add(portName.getSystemPortName());
        }
    }

    @FXML
    private void btnConectarAction() throws SQLException {
        porta = SerialPort.getCommPort(cbPortas.getSelectionModel().getSelectedItem().toString());
        porta.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 10000, 10000);
        porta.setBaudRate(2000000);
        InputStream in = porta.getInputStream();

        Thread thread = new Thread() {
            String aux = "";

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
                            //condicao de controle para entrar na funcao de slavar no banco
                            // salvar no banco
                            if (!response.equals(aux)) {
//                                System.out.println("Dentro do if = "+response);
//                                System.out.println(response.equalsIgnoreCase("aberto"));
//                                System.out.println(response.startsWith("A"));
                                if(response.startsWith("A")){
                                    System.out.println("ENTROUUUUUUUUUUUU");
                                   gravar(response); 
                                }
                                
                            }
                            aux = response;
                            //atualizar tabela
                        }

                        Thread.sleep(1000);

                        in.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (availableBytes > -1);
            }
        };

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

    protected Connection getConexao() throws SQLException {
        String url = "jdbc:postgresql://" + "localhost" + ":" + "5432" + "/" + "estacionamento";
        Connection conn = DriverManager.getConnection(url, "postgres", "postgres");
        return conn;
    }

    protected PreparedStatement getPreparedStatement(boolean chavePrimaria, String sql) throws Exception {
        PreparedStatement ps = null;
        if (chavePrimaria) {
            ps = getConexao().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        } else {
            ps = getConexao().prepareStatement(sql);
        }

        return ps;
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void gravar(String response) throws Exception {

        String sql = "insert into registro (status, data_hora) values (?,?)";
        PreparedStatement ps = getPreparedStatement(false, sql);

        ps.setString(1, response);
        ps.setString(2, getDateTime());
        ps.executeUpdate();
    }
}
