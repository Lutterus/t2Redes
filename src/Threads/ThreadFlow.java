package Threads;

import java.util.concurrent.TimeUnit;

import ServerConfig.ConfigArq;
import ServerConfig.StorageReceivedMessage;
import UserConfig.StorageMessage;

public class ThreadFlow implements Runnable {
	// classe com as configuracoes uteis
	private ConfigArq arquivoDeConfiguracao;
	// classe com a lista de mensagens a serem enviadas
	private StorageMessage storageMessage;
	// codigo padrao para tokens
	private String standarHeadforToken = "1234";
	// lista de mensagens recebidas
	private StorageReceivedMessage storageReceivedMessage;

	public ThreadFlow(ConfigArq arquivoDeConfiguracao, StorageMessage storageMessage,
			StorageReceivedMessage storageReceivedMessage) {
		this.arquivoDeConfiguracao = arquivoDeConfiguracao;
		this.storageMessage = storageMessage;
		this.storageReceivedMessage = storageReceivedMessage;
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
			myTurn = awaitSeconds(myTurn);

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

			if (myTurn == true || consequence.contains("void") == false) {
				// main phase 2 - fase de acoes e escolhas 2
				analyzeConsequences(myTurn, consequence, sentence);

				// end phase - passa o "turno" para o oponente
				passTurn(myTurn);
			} else {
				System.out.println(
						"limite de espera de uma mensagem ou de um token de 10 segundos alcancado. Reiniciando conexao");
			}
		}

	}

	private void passTurn(boolean myTurn) {
		// finalizacao do looping
		if (myTurn == true) {
			// se o token estiver com esta maquina, ela deve passar para frente
			// todas as operacoes possiveis ja foram contempladas
			System.out.println("TOKEN passado adiante");
			// mensagem contendo o token enviada adiante na rede
			attack(standarHeadforToken);

			// alteracao das configuracoes para marcar que nao esta mais com o token
			arquivoDeConfiguracao.setToken(false);
			if (arquivoDeConfiguracao.getMaquinaGeradora() == true) {
				// guarda o momento em que o token deixou a maquina
				arquivoDeConfiguracao.setTokenAntes(System.currentTimeMillis());
			}

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
			if (arquivoDeConfiguracao.getIgnoreToken() == true) {
				arquivoDeConfiguracao.setIgnoreToken(false);
				System.out.println("TOKEN recebido e ignorado, conforme solicitado");
			} else {
				if (arquivoDeConfiguracao.getMaquinaGeradora() == true) {
					if (calculateTime(2) == false) {
						arquivoDeConfiguracao.setToken(true);
						System.out.println("TOKEN recebido, agora e sua vez de enviar uma mensagem!");
					}
				} else {
					arquivoDeConfiguracao.setToken(true);
					System.out.println("TOKEN recebido, agora e sua vez de enviar uma mensagem!");
				}

			}
		} else {
			// se nao estiver com o token e for uma mensagem com dados, ela sera analisada
			seeMessage(sentence);
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
			int newCRC = storageMessage.calculateCRC(CRC, false);
			String response = "";
			if (intCRC == newCRC) {
				// avisa sobre o recebimento da mensagem e o conteudo dela e quem enviou
				System.out.println("Conteudo da mensagem: " + mensagem);
				response = head + ";" + "ACK" + ":" + apelidoOrigem + ":" + apelidoDestino + ":" + CRC + ":" + mensagem;
			} else {
				// avisa sobre o recebimento da mensagem e o erro
				System.out.println("Verificado que a mensagem contém um erro. O Remetente será avisado.");
				response = head + ";" + "erro" + ":" + apelidoOrigem + ":" + apelidoDestino + ":" + CRC + ":"
						+ mensagem;
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

	private void awaitMyAnswer() {
		// chama o metodo de recebimento de mensagem e aguarda
		String answer = waitToBeAttack();
		if (answer != null) {
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
				storageMessage.removeFirstMessage();
			} else if (controleDeErro.contentEquals("nãocopiado")) {
				// mensagem nao compiada, entende que maquina destino nao esta na rede
				System.out.println("ERRO: Não foi possivel encontrar essa maquina na rede");
				// remove mensagem da pilha e avisa
				storageMessage.removeFirstMessage();
			} else if (controleDeErro.contentEquals("erro")) {
				// mensagem voltou com erro
				if (storageMessage.getFirtMessageState() == true) {
					// se for a segunda vez que o erro ocorre
					System.out.println(
							"ERRO: Novamente ocorreu um erro durante a entrega da mensagem. A mensagem sera removida por seguranca");
					// avisa que nao foi possivel enviar e remove a mensagem da pila
					storageMessage.removeFirstMessage();
				} else {
					// se foi a primeira vez que o erro ocorre
					storageMessage.setFirtMessageState();
					// avisa sobre o erro, mas nao remove ele da pilha, para tentar novamente
					System.out.println(
							"ERRO: A mensagem chegou corrompida no destinatario. Aguardando proxima oportunidade para tentar novamente");
				}
			} else if (controleDeErro.contentEquals("ACK")) {
				// mensagem retornada com sucesso de envio
				System.out.println("Mensagem enviado por <Unicast> e recebida com sucesso pelo destinatario");
				// usuario avisado e mensagem retirada da pilha
				storageMessage.removeFirstMessage();
			}
		} else {
			// mensagem voltou com erro
			if (storageMessage.getFirtMessageState() == true) {
				// se for a segunda vez que o erro ocorre
				System.out.println(
						"ERRO: Novamente ocorreu um erro durante a entrega da mensagem. A mensagem sera removida por seguranca");
				// avisa que nao foi possivel enviar e remove a mensagem da pila
				storageMessage.removeFirstMessage();
			} else {
				// se foi a primeira vez que o erro ocorre
				storageMessage.setFirtMessageState();
				// avisa sobre o erro, mas nao remove ele da pilha, para tentar novamente
				System.out.println(
						"ERRO: A mensagem chegou corrompida no destinatario. Aguardando proxima oportunidade para tentar novamente");
			}
		}

	}

	private String beAttacked(String sentence) {
		if (sentence != null) {
			// recebe a mensagem e indentifica o tipo dela
			if (sentence.contains(";") == false) {
				return "token";
			} else {
				String[] array = sentence.split(";");
				// mensagem com body sao indentificadas como dados
				// ex:2345;naocopiado:Bob:Alice:19385749:Oi Mundo!
				return array[1];
			}
		} else {
			return "void";
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
		// tempo limite de 5 segundos para quem nao etsa com o token
		// maximo de 10 segundos para quem esta com o token
		int limit = 5;
		if (arquivoDeConfiguracao.getMaquinaGeradora() == true) {
			limit = 10;
		}
		String answer = storageReceivedMessage.getMessage();

		for (int i = 0; i < limit; i++) {
			if (answer != null) {
				break;
			} else {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				answer = storageReceivedMessage.getMessage();
			}
		}

		return answer;
	}

	private String getReadyForAttack() {
		// estando com o token, pega a primeira mensagem da pilha, para enviar
		return storageMessage.getFirtMessage();
	}

	private boolean awaitSeconds(boolean myTurn) {
		// verificacao se o token esta demorando mais do que deveria
		// apenas a primeira maquina a gerar o token pode fazer
		boolean tooLong = false;
		if (arquivoDeConfiguracao.getMaquinaGeradora() == true && arquivoDeConfiguracao.getTokenAntes() != -1) {
			tooLong = calculateTime(1);
		}
		// espera normal do tempo de depuracao
		try {
			TimeUnit.SECONDS.sleep(arquivoDeConfiguracao.getTempoToken());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (tooLong == true) {
			return true;
		} else {
			return myTurn;
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

	private boolean calculateTime(int i) {
		long currentTime = System.currentTimeMillis();
		long lastTime = arquivoDeConfiguracao.getTokenAntes();
		long difference = currentTime - lastTime;
		long limit;
		if (i == 1) {
			limit = arquivoDeConfiguracao.getTempoMG() + arquivoDeConfiguracao.getTempoMGtolerancia();
		} else {
			limit = arquivoDeConfiguracao.getTempoMG() - arquivoDeConfiguracao.getTempoMGtolerancia();
		}

		if (limit < 0) {
			limit = limit * (-1);
		}
		if (difference < 0) {
			difference = difference * (-1);
		}
		if (i == 1) {
			if (difference > limit) {
				System.out.println("o tempo limite para retono do token foi excedido. Novo token criado");
				arquivoDeConfiguracao.setTokenAntes(System.currentTimeMillis());
				return true;
			} else {
				return false;
			}
		} else if (i == 2) {
			if (difference < limit) {
				System.out.println("Token recebido antes do experado. O token sera removido por seguranca");
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

}
