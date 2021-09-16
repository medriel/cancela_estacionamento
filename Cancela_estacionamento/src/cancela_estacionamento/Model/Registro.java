package cancela_estacionamento.Model;

public class Registro {

    private String status;
    private String data_hora;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDataHora() {
        return data_hora;
    }

    public void setDataHora(String data_hora) {
        this.data_hora = data_hora;
    }
}

//        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//        Date date = new Date();
//        this.data_hora = dateFormat.format(date);
