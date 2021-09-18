package cancela_estacionamento;

import cancela_estacionamento.Model.Registro;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class FXMLDocumentController implements Initializable {

    @FXML
    private ComboBox cbPortas;

    @FXML
    private Button btnConectar;

    @FXML
    private ListView<Registro> lstRegistros;

    private SerialPort porta;

    Thread thread;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        preencherLista();
        carregarPortas();
        try {
            getConexao();
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
        
        preencherLista();
        
        porta = SerialPort.getCommPort(cbPortas.getSelectionModel().getSelectedItem().toString());
        porta.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 10000, 10000);
        porta.setBaudRate(2000000);
        InputStream in = porta.getInputStream();

        thread = new Thread() {
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
                            if (!response.equals(aux)) {
                                if (response.startsWith("A")) {
                                    gravar(response);
                                }
                            }
                            aux = response;
                        }
                        Thread.sleep(1000);
                        in.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } while (availableBytes > -1);
            }
        };

        porta.openPort();
        cbPortas.setDisable(true);
        thread.start();

    }

    @FXML
    public void btnDesconectarAction() {
        
        preencherLista();
        
        thread.interrupt();
        porta.closePort();
        cbPortas.setDisable(false);
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

    public List<Registro> consultarDados() throws Exception {
        String sql = "SELECT * FROM registro order by data_hora desc";
        PreparedStatement ps = getPreparedStatement(false, sql);

        ResultSet rs = ps.executeQuery();

        List<Registro> registros = new ArrayList<Registro>();
        while (rs.next()) {
            Registro registro = new Registro();
            registro.setStatus(rs.getString("status"));
            registro.setDataHora(rs.getString("data_hora"));
            registros.add(registro);
        }

        return registros;
    }

    private void preencherLista() {
        List<Registro> registros;
        try {
            registros = consultarDados();
            ObservableList<Registro> data = FXCollections.observableArrayList(registros);
            lstRegistros.setItems(data);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
