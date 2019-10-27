package Threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import ServerConfig.ConfigArq;
import ServerConfig.Token;

public class ThreadListenServer implements Runnable {

	private DatagramSocket serverSocket;
	private byte[] receiveData = new byte[1024];
	DatagramPacket receivePacket;
	private Token token;
	private ConfigArq arquivoDeConfiguracao;

	public ThreadListenServer(ConfigArq arquivoDeConfiguracao) {
		this.arquivoDeConfiguracao = arquivoDeConfiguracao;
		setBeforeRun();
	}

	private void setBeforeRun() {
		try {
			serverSocket = new DatagramSocket(arquivoDeConfiguracao.getPorta());
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// declara o pacote a ser recebido
		receivePacket = new DatagramPacket(receiveData, receiveData.length);
		token = new Token(arquivoDeConfiguracao);
	}

	@Override
	public void run() {
		while (true) {			
			if(token.getStatus()==false) {
				receiveMessege();
			}else {
				sendMessage();			
			}
			
		}

	}

	private void sendMessage() {
		// TODO Auto-generated method stub
		
	}

	private void receiveMessege() {
		// recebe o pacote do cliente
		try {
			serverSocket.receive(receivePacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// pega os dados, o endere�o IP e a porta do cliente
		// para poder mandar a msg de volta
		String sentence = new String(receivePacket.getData());
		InetAddress IPAddress = receivePacket.getAddress();
		int port = receivePacket.getPort();
		System.out.println("Mensagem recebida: " + sentence);
		
		// cria Thread para responder anel
		Thread answerServerThread = new Thread(new ThreadAnswerServer(sentence));
		
		answerServerThread.start();
		try {
			answerServerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
