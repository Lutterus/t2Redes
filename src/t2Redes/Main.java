package t2Redes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;

import ServerConfig.ConfigArq;
import Threads.ThreadListenServer;
import Threads.ThreadListenUser;
import UserConfig.Storage;

public class Main {

	public static void main(String[] args) throws IOException {
		// Mensagem para abrir o terminal
		System.out.println("Bem Vindo!");

		ConfigArq arquivoDeConfiguracao = configArq();
		Storage storage = new Storage();

		// cria Thread para escutar o usuario
		Thread listenUserThread = new Thread(new ThreadListenUser(arquivoDeConfiguracao, storage));

		// cria Thread para escutar anel
		Thread listenServerThread = new Thread(new ThreadListenServer(arquivoDeConfiguracao, storage));

		// inicia a thread
		listenUserThread.start();
		listenServerThread.start();

		// forca para esperar a finalizacao da thread
		try {
			listenUserThread.join();
			listenServerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static ConfigArq configArq() {
		System.out.println("Maquina principal?");
		// cria o stream do teclado
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		String sentence = null;
		// String ip = "192.168.1.12";
		InetAddress myIP = null;
		InetAddress nextIP = null;
		int porta = 9876;// 6000;
		String apelidoOrigem = "bob";
		String apelidoDestino = "alice";
		int tempo = 3;
		boolean token;// = true;

		try {
			sentence = inFromUser.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (sentence.contentEquals("s")) {
			token = true;
		} else {
			token = false;
		}

		System.out.println("Config 1 ou 2?");

		try {
			sentence = inFromUser.readLine();
			if (sentence.contentEquals("1")) {
				myIP = InetAddress.getByName("127.0.0.1");
				nextIP = InetAddress.getByName("127.0.0.2");
				// ip = InetAddress.getLocalHost();
				// ip = InetAddress.getByName("localhost");
			} else {
				myIP = InetAddress.getByName("127.0.0.2");
				nextIP = InetAddress.getByName("127.0.0.1");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ConfigArq arquivoDeConfiguracao = new ConfigArq(myIP, nextIP, porta, apelidoOrigem, apelidoDestino, tempo, token);
		arquivoDeConfiguracao.print();

		return arquivoDeConfiguracao;
	}
}
