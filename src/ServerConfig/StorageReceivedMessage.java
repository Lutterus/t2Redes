package ServerConfig;

import java.util.ArrayList;

public class StorageReceivedMessage {
	// array com as mensagens
	private ArrayList<MessagesReceived> messages;
	private ConfigArq arquivoDeConfiguracao;

	public StorageReceivedMessage(ConfigArq arquivoDeConfiguracao) {
		// inicializacao do array
		this.messages = new ArrayList<MessagesReceived>();
		this.arquivoDeConfiguracao = arquivoDeConfiguracao;
	}

	public void addMessage(String newMessage, long currentTime) {
		// metodo para adicao de mensagens;
		MessagesReceived m = new MessagesReceived(newMessage, currentTime, arquivoDeConfiguracao.getTokenAntes());
		this.messages.add(m);
	}

	public String getMessage() {
		// metodo para obtencao de mensagens
		if (messages.size() == 0) {
			// validacao se existem mensagem a serem obtidas
			if (arquivoDeConfiguracao.getMaquinaGeradora() == true) {
			}
			return null;
		} else {
			String answer = messages.get(0).getMessage();
			messages.remove(0);
			return answer;
		}
	}
}
