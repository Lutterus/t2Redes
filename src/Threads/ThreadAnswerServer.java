package Threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import ServerConfig.ConfigArq;

//classe para envio de mensagens atraves do anel

public class ThreadAnswerServer implements Runnable {
	// a mensagem a ser enviada
	private String sentence;
	// a classe arquivo de configuracao com as informacoes uteis
	private ConfigArq arquivoDeConfiguracao;

	public ThreadAnswerServer(String sentence, ConfigArq arquivoDeConfiguracao) {
		// instanciacao
		this.sentence = sentence;
		this.arquivoDeConfiguracao = arquivoDeConfiguracao;
	}

	@Override
	public void run() {
		// se a mensagem vier como null, significa que nao ha mensagem disponiveis na
		// pilha
		if (sentence == null) {
			// usuario eh alertado sobre isso
			System.out.println("Nenhuma mensagem a ser enviada");
		} else {
			// se nao for null, inicia o processo de envio de mensagem

			// declara socket cliente
			DatagramSocket clientSocket = null;
			try {
				clientSocket = new DatagramSocket();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// declara a estrutura de dados
			byte[] sendData = new byte[1024];

			// le a mensagem e a passa para a estrutura
			sendData = sentence.getBytes();

			// cria pacote com o dado, o endereco e porta do servidor
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
					arquivoDeConfiguracao.getIpDestinoToken(), arquivoDeConfiguracao.getPorta());

			// envia o pacote
			try {
				clientSocket.send(sendPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// fecha o cliente
			clientSocket.close();
		}

	}

}
