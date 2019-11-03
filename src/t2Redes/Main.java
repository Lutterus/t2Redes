package t2Redes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

import ServerConfig.ConfigArq;
import ServerConfig.StorageReceivedMessage;
import Threads.ThreadFlow;
import Threads.ThreadListenServer;
import Threads.ThreadListenUser;
import UserConfig.StorageMessage;

public class Main {

	public static void main(String[] args) throws IOException {
		// Mensagem para abrir o terminal
		System.out.println("Bem Vindo!");

		ConfigArq arquivoDeConfiguracao = configArq();
		StorageMessage storageMessage = new StorageMessage();
		StorageReceivedMessage storageReceivedMessage = new StorageReceivedMessage(arquivoDeConfiguracao);

		// cria Thread para seguir o fluxo
		Thread flow = new Thread(new ThreadFlow(arquivoDeConfiguracao, storageMessage, storageReceivedMessage));

		// cria Thread para escutar o usuario
		Thread listenUserThread = new Thread(new ThreadListenUser(arquivoDeConfiguracao, storageMessage));

		// cria Thread para escutar anel
		Thread listenServerThread = new Thread(new ThreadListenServer(arquivoDeConfiguracao, storageReceivedMessage));

		// inicia a thread
		flow.start();
		listenUserThread.start();
		listenServerThread.start();

		// forca para esperar a finalizacao da thread
		try {
			listenUserThread.join();
			flow.join();
			listenServerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static ConfigArq configArq() {
		// cria o stream do teclado
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		String sentence = null;

		System.out.println("Deseja usar configuracoes externas - digite 1");
		System.out.println("Ou deseja usar configuracoes internas (teste) - digite 2");

		try {
			sentence = inFromUser.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ConfigArq arquivoDeConfiguracao = null;

		if (sentence.contentEquals("1")) {
			arquivoDeConfiguracao = external();
		} else if (sentence.contentEquals("2")) {
			arquivoDeConfiguracao = internal();
		} else {
			System.out.println("comando nao reconhecido");
		}
		return arquivoDeConfiguracao;
	}

	private static ConfigArq external() {
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		String sentence = null;

		// String ip = "192.168.1.12";
		InetAddress myIP = null;
		InetAddress nextIP = null;
		int porta = 6000;// 6000;
		String apelidoOrigem = "";
		int tempo = 2;
		boolean token = true;
		long tempoMG = -1;
		long tempoMGtolerancia = -1;

		System.out.println("Config 1, 2, 3 ou 4?");

		try {
			sentence = inFromUser.readLine();
			if (sentence.contentEquals("1")) {
				myIP = InetAddress.getByName("127.0.0.1");
				nextIP = InetAddress.getByName("127.0.0.2");
				token = true;
				apelidoOrigem = "bob";
				// ip = InetAddress.getLocalHost();
				// ip = InetAddress.getByName("localhost");
			} else {
				myIP = InetAddress.getByName("127.0.0.2");
				nextIP = InetAddress.getByName("127.0.0.1");
				token = false;
				apelidoOrigem = "alice";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (token == true) {
			System.out.println("Verificado que esta sera a maquina que gerara o token");
			System.out.println(
					"qual e o tempo (estipulado) para o token retornar a esta maquina depois de sair (em segundos)?");

			try {
				sentence = inFromUser.readLine();
				tempoMG = Integer.parseInt(sentence);
				tempoMG = tempoMG * 1000;

				System.out.println("Qual o tempo de tolerancia? (intervalo de tempo em torno do tempo estipulado)");
				sentence = inFromUser.readLine();
				tempoMGtolerancia = Integer.parseInt(sentence);
				tempoMGtolerancia = tempoMGtolerancia * 1000;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		ConfigArq arquivoDeConfiguracao = new ConfigArq(myIP, nextIP, porta, apelidoOrigem, tempo, token, tempoMG,
				tempoMGtolerancia);

		return arquivoDeConfiguracao;
	}

	private static ConfigArq internal() {
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		String sentence = null;

		// String ip = "192.168.1.12";
		InetAddress myIP = null;
		InetAddress nextIP = null;
		int porta = 6000;// 6000;
		String apelidoOrigem = "";
		int tempo = 2;
		boolean token = true;
		long tempoMG = -1;
		long tempoMGtolerancia = -1;

		System.out.println("Config 1, 2, 3 ou 4?");

		try {
			sentence = inFromUser.readLine();
			if (sentence.contentEquals("1")) {
				myIP = InetAddress.getByName("127.0.0.1");
				nextIP = InetAddress.getByName("127.0.0.2");
				token = true;
				apelidoOrigem = "bob";
				// ip = InetAddress.getLocalHost();
				// ip = InetAddress.getByName("localhost");
			} else {
				myIP = InetAddress.getByName("127.0.0.2");
				nextIP = InetAddress.getByName("127.0.0.1");
				token = false;
				apelidoOrigem = "alice";
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (token == true) {
			System.out.println("Verificado que esta sera a maquina que gerara o token");
			System.out.println(
					"qual e o tempo (estipulado) para o token retornar a esta maquina depois de sair (em segundos)?");

			try {
				sentence = inFromUser.readLine();
				tempoMG = Integer.parseInt(sentence);
				tempoMG = tempoMG * 1000;

				System.out.println("Qual o tempo de tolerancia? (intervalo de tempo em torno do tempo estipulado)");
				sentence = inFromUser.readLine();
				tempoMGtolerancia = Integer.parseInt(sentence);
				tempoMGtolerancia = tempoMGtolerancia * 1000;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		ConfigArq arquivoDeConfiguracao = new ConfigArq(myIP, nextIP, porta, apelidoOrigem, tempo, token, tempoMG,
				tempoMGtolerancia);

		return arquivoDeConfiguracao;
	}
}
