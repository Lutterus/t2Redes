package Threads;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import ServerConfig.ConfigArq;
import UserConfig.Storage;

public class ThreadListenServer implements Runnable {

	// socket
	private DatagramSocket serverSocket;
	// metodo para receber o pacote na rede
	private DatagramPacket receivePacket;
	// classe com as configuracoes uteis
	private ConfigArq arquivoDeConfiguracao;
	// classe com a lista de mensagens a serem enviadas
	private Storage storage;
	// codigo padrao para tokens
	private String standarHeadforToken = "1234";
	// codigo padrao para mensagens/dados
	// private String standardHeadforMessage = "2345";

	public ThreadListenServer(ConfigArq arquivoDeConfiguracao, Storage storage) {
		this.arquivoDeConfiguracao = arquivoDeConfiguracao;
		this.storage = storage;
		setBeforeRun();
	}

	private void setBeforeRun() {
		// instancia o socket
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
			boolean myTurn = isItMyTurn();

			// standby phase - espera de consequencias e preparacao para acoes
			// depuracao - controle de erros - o tempo eh setado na declaracao do arquivo de
			// configuracao, na Main
			awaitSeconds();

			// main phase 1 - fase de acoes e escolhas 1
			String sentence = "";
			if (myTurn == true) {
				sentence = getReadyForAttack();
			} else {
				sentence = waitToBeAttack();
			}

			// battle phase - fase de consequencia das acoes escolhidas
			String consequence = null;
			if (myTurn == true) {
				attack(sentence);
			} else {
				consequence = beAttacked(sentence);
			}

			// main phase 2 - fase de acoes e escolhas 2
			analyzeConsequences(myTurn, consequence, sentence);

			// end phase - passa o "turno" para o oponente
			passTurn(myTurn);
		}

	}

	private void passTurn(boolean myTurn) {
		// finalizacao do looping
		if (myTurn == true) {
			// se o token estiver com etsa maquina, ela deve passar para frente
			// todas as operacoes possiveis ja foram contempladas
			System.out.println("TOKEN passado adiante");
			// mensagem contendo o token enviada adiante na rede
			attack(standarHeadforToken);
			// alteracao das configuracoes para marcar que nao esta mais com o token
			arquivoDeConfiguracao.setToken(false);
		}

	}

	private void analyzeConsequences(boolean myTurn, String consequence, String sentence) {
		if (myTurn == true) {
			// se estiver com o token verfica
			if (sentence != null && sentence != "") {
				// se uma mensagem foi enviada, ele espera pelo retono da resposta
				awaitMyAnswer();
			}
		} else if (consequence.contentEquals("token")) {
			// se nao estiver com o token, mas a mensagem recebida for um token
			// marca que esta com o token e se prepara para poder enviar mensagem no proximo
			// ciclo
			arquivoDeConfiguracao.setToken(true);
			System.out.println("TOKEN recebido, agora é sua vez de enviar uma mensagem!");
		} else {
			// se nao estiver com o token e for uma mensagem com dados, ela sera analisada
			seeMessage(sentence);
		}

	}

	private void awaitMyAnswer() {
		// chama o metodo de recebimento de mensagem e aguarda
		String answer = waitToBeAttack();
		// quebra a mensagem em dois primeiro pedacos
		String[] array1 = answer.split(";");
		// pedaco 1, head, pode ser 1234 ou 2345
		String head = array1[0];
		// pedaco dois e quebrados em varios, dando uma variavel para cada campo do dado
		String[] array2 = array1[1].split(":");
		String controleDeErro = array2[0];
		String apelidoOrigem = array2[1];
		String apelidoDestino = array2[2];
		String CRC = array2[3];
		String mensagem = array2[4];

		System.out.println("Mensagem enviada para " + apelidoDestino + ": " + mensagem);

		if (apelidoDestino.contentEquals("TODOS")) {
			// tratamento especial para mensagem enviadas em Broadcast
			// nao olha controle de erros, apenas espera retorno e remove ela da pila
			System.out.println("Mensagem transmitida por <Broadcast> com sucesso");
			storage.removeFirstMessage();
		} else if (controleDeErro.contentEquals("nãocopiado")) {
			// mensagem nao compiada, entende que maquina destino nao esta na rede
			System.out.println("ERRO: Não foi possivel encontrar essa maquina na rede");
			// remove mensagem da pilha e avisa
			storage.removeFirstMessage();
		} else if (controleDeErro.contentEquals("erro")) {
			// mensagem voltou com erro
			if (storage.getFirtMessageState() == true) {
				// se for a segunda vez que o erro ocorre
				System.out.println("ERRO: Novamente ocorreu um erro durante a entrega da mensagem. A mensagem sera removida por seguranca");
				// avisa que nao foi possivel enviar e remove a mensagem da pila
				storage.removeFirstMessage();
			} else {
				// se foi a primeira vez que o erro ocorre
				storage.setFirtMessageState();
				// avisa sobre o erro, mas nao remove ele da pilha, para tentar novamente
				System.out.println("ERRO: A mensagem chegou corrompida no destinatario. Aguardando proxima oportunidade para tentar novamente");
			}
		} else if (controleDeErro.contentEquals("ACK")) {
			// mensagem retornada com sucesso de envio
			System.out.println("Mensagem enviado por <Unicast> e recebida com sucesso pelo destinatario");
			// usuario avisado e mensagem retirada da pilha
			storage.removeFirstMessage();
		}

	}

	private void seeMessage(String sentence) {
		// cacuete de usar variavel interna para evitar erros
		String answer = sentence;
		// quebra a mensagem em dois primeiro pedacos
		String[] array1 = answer.split(";");
		// pedaco 1, head, pode ser 1234 ou 2345
		String head = array1[0];
		// pedaco dois e quebrados em varios, dando uma variavel para cada campo do dado
		String[] array2 = array1[1].split(":");
		String controleDeErro = array2[0];
		String apelidoOrigem = array2[1];
		String apelidoDestino = array2[2];
		String CRC = array2[3];
		int intCRC = Integer.parseInt(CRC);
		String mensagem = array2[4];

		if (apelidoDestino.contentEquals(arquivoDeConfiguracao.getApelidoDaMaquinaAtual())) {
			// mensagem recebida era para esta maquina
			System.out.println("Mensagem recebida de: <" + apelidoOrigem + "> em <Unicast>");
			int newCRC = storage.calculateCRC(CRC, false);
			String response = "";
			if(intCRC==newCRC) {
				// avisa sobre o recebimento da mensagem e o conteudo dela e quem enviou
				System.out.println("Conteudo da mensagem: " + mensagem);
				response = head + ";" + "ACK" + ":" + apelidoOrigem + ":" + apelidoDestino + ":" + CRC + ":" + mensagem;
			}else {
				// avisa sobre o recebimento da mensagem e o erro
				System.out.println("Verificado que a mensagem contém um erro. O Remetente será avisado.");
				response = head + ";" + "erro" + ":" + apelidoOrigem + ":" + apelidoDestino + ":" + CRC + ":" + mensagem;
			}
			// altera o controle de erro da mensagem e a passa para proxima maquina no anel
			attack(response);
		} else if (apelidoDestino.contentEquals("TODOS")) {
			// mensagem foi enviada em broadcast
			System.out.println("Mensagem recebida de: <" + apelidoOrigem + "> em <Broadcast>");
			// avisa sobre o recebimento da mensagem e o conteudo dela e que foi enviada
			// para todos
			System.out.println("Conteudo da mensagem: " + mensagem);
			// NAO altera o controle de erro da mensagem e a passa para proxima maquina no
			// anel
			attack(answer);
		} else {
			// mensagem destinada a outra maquina
			// a mensagem apenas e passada adiante
			attack(answer);
		}

	}

	private String beAttacked(String sentence) {
		// recebe a mensagem e indentifica o tipo dela
		String[] array = sentence.split(";");
		if (array.length == 1) {
			// mensagens apenas com head sao classificadas como tokens
			// ex:1234
			return "token";
		} else {
			// mensagem com body sao indentificadas como dados
			// ex:2345;naocopiado:Bob:Alice:19385749:Oi Mundo!
			return array[1];
		}
	}

	private void attack(String message) {
		// metodo para enviar uma mensagem para a maquina da direita
		Thread answerServerThread = new Thread(new ThreadAnswerServer(message, arquivoDeConfiguracao));
		// inicia thread independente
		answerServerThread.start();
		try {
			// forca pelo pela espera da conclusao dela
			answerServerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String waitToBeAttack() {
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
		return message;

	}

	private String getReadyForAttack() {
		// estando com o token, pega a primeira mensagem da pilha, para enviar
		return storage.getFirtMessage();
	}

	private void awaitSeconds() {
		// espera do tempo que a estação permanecerá com os pacotes (para fins de
		// depuração), em segundos.
		try {
			TimeUnit.SECONDS.sleep(arquivoDeConfiguracao.getTempoToken());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean isItMyTurn() {
		// verificar se o token esta com esta maquina no momento
		if (arquivoDeConfiguracao.getToken() == true) {
			return true;
		} else {
			return false;
		}

	}
}
