package Threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import ServerConfig.ConfigArq;
import UserConfig.Storage;

public class ThreadAnswerServer implements Runnable {

	private String sentence;
	private ConfigArq arquivoDeConfiguracao;

	public ThreadAnswerServer(String sentence, ConfigArq arquivoDeConfiguracao) {
		this.sentence = sentence;
		this.arquivoDeConfiguracao = arquivoDeConfiguracao;
	}

	@Override
	public void run() {
		if (sentence == null) {
			System.out.println("Nenhuma mensagem a ser enviada");
		} else {
			System.out.println("---essa e a mensagem a ser enviada: " + sentence);
			// declara socket cliente
			DatagramSocket clientSocket = null;
			try {
				clientSocket = new DatagramSocket();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			byte[] sendData = new byte[1024];
			
			//le a mensagem
			sendData = sentence.getBytes();

			// cria pacote com o dado, o endere�o do server e porta do servidor
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
