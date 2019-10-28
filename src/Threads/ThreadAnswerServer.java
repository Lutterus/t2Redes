package Threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import ServerConfig.ConfigArq;
import UserConfig.Storage;

public class ThreadAnswerServer implements Runnable {

	private Storage storage;
	private ConfigArq arquivoDeConfiguracao;

	public ThreadAnswerServer(Storage storage, ConfigArq arquivoDeConfiguracao) {
		this.storage = storage;
		this.arquivoDeConfiguracao = arquivoDeConfiguracao;
	}

	@Override
	public void run() {
		String sentence = storage.getFirtMessage();

		if (sentence == null) {
			System.out.println("null");
		} else {
			// declara socket cliente
			DatagramSocket clientSocket = null;
			try {
				clientSocket = new DatagramSocket();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			byte[] sendData = new byte[1024];

			// l� uma linha do teclado

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
