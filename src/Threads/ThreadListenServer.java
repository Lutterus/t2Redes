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
		//sequencia de acoes baseado no sistema de turnos do yugioh competitivo (mais facil de organizar) 
		while (true) {
			//draw phase - preparacao para o inicio do "turno"
			boolean myTurn = isItMyTurn();
			
			//standby phase - espera de consequencias e preparacao para acoes
			//depuracao - controle de erros - o tempo eh setado na declaracao do arquivo de configuracao, na Main
			awaitSeconds(); 
			
			//main phase1 - fase de acoes e escolhas 1
			String sentence="";
			if(myTurn==true) {
				attack();
			}else {
				sentence = waitForAttack();
			}
			//battle phase - fase de aguardo das consequencia das acoes
			System.out.println(sentence);
			
			//main phase 2 - fase de acoes e escolhas 2
			
			//end phase - passa o "turno" para o oponente 
		}

	}

	private String waitForAttack() {
		try {
			serverSocket.receive(receivePacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// pega os dados, o endere�o IP e a porta do cliente
		// para poder mandar a msg de volta
		String sentence = new String(receivePacket.getData());
		return sentence;
		
	}

	private void attack() {
		Thread answerServerThread = new Thread(new ThreadAnswerServer(storage, arquivoDeConfiguracao));
		answerServerThread.start();
		try {
			answerServerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void awaitSeconds() {
		try {
			TimeUnit.SECONDS.sleep(arquivoDeConfiguracao.getTempoToken());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	private boolean isItMyTurn() {
		if (arquivoDeConfiguracao.getToken() == true) {
			return true;
		} else {
			return false;
		}
		
	}
}
