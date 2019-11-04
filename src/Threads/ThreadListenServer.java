package Threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import ServerConfig.ConfigArq;
import ServerConfig.StorageReceivedMessage;
import UserConfig.StorageMessage;

public class ThreadListenServer implements Runnable {
	// socket
	private DatagramSocket serverSocket;
	// metodo para receber o pacote na rede
	private DatagramPacket receivePacket;
	// classe com as configuracoes uteis
	private ConfigArq arquivoDeConfiguracao;
	// classe com a lista de mensagens a serem enviadas
	private StorageReceivedMessage storageReceivedMessage;
	// codigo padrao para tokens

	public ThreadListenServer(ConfigArq arquivoDeConfiguracao, StorageReceivedMessage storageReceivedMessage) {
		this.arquivoDeConfiguracao = arquivoDeConfiguracao;
		this.storageReceivedMessage = storageReceivedMessage;
		try {
			serverSocket = new DatagramSocket(arquivoDeConfiguracao.getPorta(),
					arquivoDeConfiguracao.getIpOrigemToken());
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			// nao estando com o token, espera para receber uma mensagem
			// declara o pacote a ser recebido
			byte[] receiveData = new byte[1024];
			// e aciona o metodo para recebe-lo
			receivePacket = new DatagramPacket(receiveData, receiveData.length);

			try {
				serverSocket.receive(receivePacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String message = new String(receivePacket.getData());
			long currentTime = System.currentTimeMillis();
			storageReceivedMessage.addMessage(message, currentTime);
		}
	}
}
