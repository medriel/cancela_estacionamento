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
    
     @Override
    public String toString() {
        return (
                "--------------------------------------------------------------------------------------\n"+
                "\t \t \t \t Controle de Acesso da cancela \n"+
                "Status da cancela: "+status + "\n"
                + "Data e hora da abertura da cancela: " + data_hora + "\n"+
                "--------------------------------------------------------------------------------------\n\n"
                );
    }
}
