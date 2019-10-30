package Threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import ServerConfig.ConfigArq;
import UserConfig.Storage;

public class ThreadListenServer implements Runnable {

	private DatagramSocket serverSocket;
	private DatagramPacket receivePacket;
	private ConfigArq arquivoDeConfiguracao;
	private Storage storage;
	private String standarHeadforToken = "1234";
	// private String standardHeadforMessage = "2345";

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
	}

	@Override
	public void run() {
		// sequencia de acoes baseado no sistema de turnos do yugioh competitivo (mais
		// facil de organizar)
		while (true) {
			// draw phase - preparacao para o inicio do "turno"
			System.out.println("draw phase");
			boolean myTurn = isItMyTurn();

			// standby phase - espera de consequencias e preparacao para acoes
			// depuracao - controle de erros - o tempo eh setado na declaracao do arquivo de
			// configuracao, na Main
			System.out.println("standby phase");
			awaitSeconds();

			// main phase 1 - fase de acoes e escolhas 1
			System.out.println("main phase 1");
			String sentence = "";
			if (myTurn == true) {
				sentence = getReadyForAttack();
			} else {
				sentence = waitToBeAttack();
			}

			// battle phase - fase de aguardo das consequencia das acoes
			System.out.println("battle phase");
			String consequence = null;
			if (myTurn == true) {
				attack(sentence);
			} else {
				consequence = beAttacked(sentence);
			}

			// main phase 2 - fase de acoes e escolhas 2
			System.out.println("main phase 2");
			analyzeConsequences(myTurn, consequence, sentence);

			// end phase - passa o "turno" para o oponente
			System.out.println("end phase");
			passTurn(myTurn);
		}

	}

	private void analyzeConsequences(boolean myTurn, String consequence, String sentence) {
		if (myTurn == true) {
			if(sentence!=null && sentence!="") {
				awaitMyAnswer();
			}
		} else if (consequence.contentEquals("token")) {
			arquivoDeConfiguracao.setToken(true);
			System.out.println("-------------agora eh minha vez");
		} else {
			seeMessage(sentence);
		}

	}

	private void seeMessage(String sentence) {
		String answer = sentence;
		
		String[] array1 = answer.split(";");
		String head = array1[0];
		
		String[] array2 = array1[1].split(":");
		String controleDeErro = array2[0];
		String apelidoOrigem = array2[1];
		String apelidoDestino = array2[2];
		String CRC = array2[3];
		String mensagem = array2[4];
		
		if(apelidoDestino.contentEquals(arquivoDeConfiguracao.getApelidoDaMaquinaAtual())) {
			System.out.println("mensagem era para mim");
			String response = head+";"+"ACK"+":"+apelidoOrigem+":"+apelidoDestino+":"+CRC+":"+mensagem;
			attack(response);
			
		} else if(apelidoDestino.contentEquals("TODOS")){
			System.out.println("mensagem para todos");
		} else {
			attack(answer);
			System.out.println("mensagem nao era para mim");
		}
		
	}

	private void awaitMyAnswer() {
		String answer = waitToBeAttack();
		
		String[] array1 = answer.split(";");
		String head = array1[0];
		
		String[] array2 = array1[1].split(":");
		String controleDeErro = array2[0];
		String apelidoOrigem = array2[1];
		String apelidoDestino = array2[2];
		String CRC = array2[3];
		String mensagem = array2[3];
		
		if(controleDeErro.contentEquals("nãocopiado")) {
			System.out.println("ERRO: Não foi possivel encontrar essa maquina na rede");
			storage.removeFirstMessage();
		} else if(controleDeErro.contentEquals("erro")) {
			System.out.println("ERRO: Mensagem corrompida antes da entrega. Aguardando para reenviar");
		} else if(controleDeErro.contentEquals("ACK")) {
			System.out.println("Mensagem recebida com sucesso pelo destinatario");
			storage.removeFirstMessage();
		}
		
	}

	private void passTurn(boolean myTurn) {
		if (myTurn == true) {
			System.out.println("---- passei o turno");
			attack(standarHeadforToken);
			arquivoDeConfiguracao.setToken(false);
		}

	}

	private String beAttacked(String sentence) {
		String[] array = sentence.split(";");
		if (array.length == 1) {
			return "token";
		} else {
			return array[1];
		}
	}

	private String getReadyForAttack() {
		return storage.getFirtMessage();
	}

	private String waitToBeAttack() {
		// declara o pacote a ser recebido
		byte[] receiveData = new byte[1024];
		receivePacket = new DatagramPacket(receiveData, receiveData.length);

		System.out.println("vai pegar a mensagem");
		try {
			serverSocket.receive(receivePacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String message = new String(receivePacket.getData());
		System.out.println("------pegou a mensagem: " + message);
		return message;

	}

	private void attack(String message) {
		Thread answerServerThread = new Thread(new ThreadAnswerServer(message, arquivoDeConfiguracao));
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
