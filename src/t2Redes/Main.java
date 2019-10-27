package t2Redes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import ServerConfig.ConfigArq;
import Threads.ThreadListenServer;
import Threads.ThreadListenUser;

public class Main {

	public static void main(String[] args) throws IOException {
		// Mensagem para abrir o terminal
		System.out.println("Bem Vindo!");

		ConfigArq arquivoDeConfiguracao = configArq();

		// cria Thread para escutar o usuario
		Thread listenUserThread = new Thread(new ThreadListenUser());

		// cria Thread para escutar anel
		Thread listenServerThread = new Thread(new ThreadListenServer(arquivoDeConfiguracao));

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
		InetAddress ip = null;
		int porta = 9876;// 6000;
		String apelido = "Bob";
		int tempo = 1;
		boolean token;// = true;

		try {
			ip = InetAddress.getLocalHost();
			// ip = InetAddress.getByName("localhost");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

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

		ConfigArq arquivoDeConfiguracao = new ConfigArq(ip, porta, apelido, tempo, token);
		arquivoDeConfiguracao.print();

		return arquivoDeConfiguracao;
	}
}
