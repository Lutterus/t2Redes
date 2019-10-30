package UserConfig;

import java.util.ArrayList;

import ServerConfig.Dados;

public class Storage {

	private ArrayList<Dados> messages;

	public Storage() {
		messages = new ArrayList<Dados>();
	}

	public void addMessage(String newMessage, String apelidoMaquinaAtual, String apelidoMaquinaDestino) {
		if(messages.size()==10) {
			System.out.println("Nao foi possivel salvar esta mensagem, pois o limite de 10 mensagens foi atingido. Aguarde o envio de uma mensagem e tente novamente");
		} else {
			int CRC = calculateCRC();
			Dados dado = new Dados("2345", "nãocopiado", apelidoMaquinaAtual, apelidoMaquinaDestino, CRC, newMessage);
			this.messages.add(dado);
		}
	}

	private int calculateCRC() {
		return 19385749;
	}

	public String getFirtMessage() {
		if (messages.size() == 0) {
			return null;
		} else {
			String returnMessage = messages.get(0).print();
			return returnMessage;
		}

	}
	
	public void removeFirstMessage() {
		messages.remove(0);
	}

	public void print() {
		for (Dados dado : messages) {
			System.out.println(dado.print());
		}
	}
}
