package Threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import ServerConfig.ConfigArq;
import UserConfig.Storage;

public class ThreadListenServer implements Runnable {

	private DatagramSocket serverSocket;
	private byte[] receiveData = new byte[1024];
	private DatagramPacket receivePacket;
	private ConfigArq arquivoDeConfiguracao;
	private Storage storage;

	public ThreadListenServer(ConfigArq arquivoDeConfiguracao, Storage storage) {
		this.arquivoDeConfiguracao = arquivoDeConfiguracao;
		this.storage = storage;
		setBeforeRun();
	}

	private void setBeforeRun() {
		try {
			serverSocket = new DatagramSocket(arquivoDeConfiguracao.getPorta(),
					arquivoDeConfiguracao.getIpOrigemToken());
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// declara o pacote a ser recebido
		receivePacket = new DatagramPacket(receiveData, receiveData.length);
	}

	@Override
	public void run() {
		while (true) {
			if (arquivoDeConfiguracao.getToken() == true) {
				myTurn();
			} else {
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

				if (sentence.contentEquals(arquivoDeConfiguracao.getApelidoDaMaquinaAtual())) {
					myMessage(sentence);
				} else {
					notMyMessage(sentence);
				}
			}

		}

	}

	private void notMyMessage(String sentence) {
		System.out.println("a mensagem NAAAO era para mim");
		System.out.println("sentence: " + sentence);

	}

	private void myMessage(String sentence) {
		System.out.println("a mensagem EEEERA para mim");
		System.out.println("sentence: " + sentence);

	}

	private void myTurn() {
		System.out.println("essa é minha vez");
		Thread answerServerThread = new Thread(new ThreadAnswerServer(storage, arquivoDeConfiguracao));
		answerServerThread.start();
		try {
			answerServerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
