package UserConfig;

import java.util.ArrayList;

import ServerConfig.Dados;

public class Storage {

	private ArrayList<Dados> messages;

	public Storage() {
		messages = new ArrayList<Dados>();
	}

	public void addMessage(String newMessage, String apelidoMaquinaAtual, String apelidoMaquinaDestino) {
		String controle = "nao sei";
		Dados dado = new Dados("2345", controle, apelidoMaquinaAtual, apelidoMaquinaDestino, 19385749, newMessage);
		this.messages.add(dado);
	}

	public String getFirtMessage() {
		if (messages.size() == 0) {
			return null;
		} else {
			String returnMessage = messages.get(0).print();
			messages.remove(0);
			return returnMessage;
		}

	}

	public void print() {
		for (Dados dado : messages) {
			System.out.println(dado.print());
		}
	}
}
