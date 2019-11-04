package t2Redes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import ServerConfig.ConfigArq;
import ServerConfig.StorageReceivedMessage;
import Threads.ThreadFlow;
import Threads.ThreadListenServer;
import Threads.ThreadListenUser;
import UserConfig.StorageMessage;

public class Main {
	public static String localArquivoDeConfiguracao = "C:\\Users\\Joao\\Documents\\projetos\\trabalhos\\t2Redes\\assests\\arquivoDeConfiguracao.txt";

	public static void main(String[] args) throws IOException {
		// Mensagem para abrir o terminal
		System.out.println("Bem Vindo!");

		ConfigArq arquivoDeConfiguracao = configArq();
		arquivoDeConfiguracao.print();
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

	private static ConfigArq configArq() throws IOException {
		// cria o stream do teclado
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		String sentence = null;

		System.out.println("Aquivo de configuracao lido");
		System.out.println("Deseja usar configuracoes externas - digite 1");
		System.out.println("Ou deseja usar configuracoes internas (teste) - digite 2");

		sentence = inFromUser.readLine();

		ConfigArq arquivoDeConfiguracao = null;

		if (sentence.contentEquals("1")) {
			arquivoDeConfiguracao = readTXT(inFromUser, arquivoDeConfiguracao, 1);
		} else if (sentence.contentEquals("2")) {
			arquivoDeConfiguracao = readTXT(inFromUser, arquivoDeConfiguracao, 2);
		} else {
			System.out.println("comando nao reconhecido");
		}
		return arquivoDeConfiguracao;
	}

	private static ConfigArq readTXT(BufferedReader inFromUser, ConfigArq arquivoDeConfiguracao, int j)
			throws IOException {
		File file = new File(localArquivoDeConfiguracao);
		BufferedReader br = new BufferedReader(new FileReader(file));
		int i = 0;
		String st;
		String ipDestino = "";
		int porta = 0;
		String apelido = "";
		int tempoToken = 0;
		boolean maquinaGeradora = false;
		while ((st = br.readLine()) != null) {
			if (i == 0) {
				String[] array = st.split(":");
				ipDestino = array[0];
				porta = Integer.parseInt(array[1]);
			} else if (i == 1) {
				apelido = st;
			} else if (i == 2) {
				tempoToken = Integer.parseInt(st);
			} else if (i == 3) {
				if (st.contentEquals("true")) {
					maquinaGeradora = true;
				} else if (st.contentEquals("false")) {
					maquinaGeradora = false;
				} else {
					System.err.println("Arquivo de configuracao no formato incorreto");
				}
			} else {
				System.err.println("Arquivo de configuracao no formato incorreto");
			}
			i++;
		}
		if (j == 1) {
			arquivoDeConfiguracao = external(inFromUser, ipDestino, porta, apelido, tempoToken, maquinaGeradora);

		} else {
			arquivoDeConfiguracao = internal(inFromUser, porta, tempoToken);
		}
		return arquivoDeConfiguracao;
	}

	private static ConfigArq internal(BufferedReader inFromUser, int porta, int tempoToken) throws IOException {
		String sentence = null;
		InetAddress myIP = null;
		InetAddress nextIP = null;
		String apelidoOrigem = "";
		boolean token = true;
		long tempoMG = -1;
		long tempoMGtolerancia = -1;

		System.out.println("Config 1 ou 2?");

		sentence = inFromUser.readLine();
		if (sentence.contentEquals("1")) {
			myIP = InetAddress.getByName("127.0.0.1");
			nextIP = InetAddress.getByName("127.0.0.2");
			token = true;
			apelidoOrigem = "joao";
		} else if (sentence.contentEquals("2")) {
			myIP = InetAddress.getByName("127.0.0.2");
			nextIP = InetAddress.getByName("127.0.0.1");
			token = false;
			apelidoOrigem = "lucas";
		} else {
			System.out.println("COMANDO NAO RECONHECIDO");
		}

		if (token == true) {
			System.out.println("Verificado que esta sera a maquina que gerara o token");
			System.out.println(
					"qual e o tempo (estipulado) para o token retornar a esta maquina depois de sair (em segundos)?");

			sentence = inFromUser.readLine();
			tempoMG = Integer.parseInt(sentence);
			tempoMG = tempoMG * 1000;

			System.out.println("Qual o tempo de tolerancia? (intervalo de tempo em torno do tempo estipulado)");
			sentence = inFromUser.readLine();
			tempoMGtolerancia = Integer.parseInt(sentence);
			tempoMGtolerancia = tempoMGtolerancia * 1000;
		}

		ConfigArq arquivoDeConfiguracao = new ConfigArq(myIP, nextIP, porta, apelidoOrigem, tempoToken, token, tempoMG,
				tempoMGtolerancia);

		return arquivoDeConfiguracao;
	}

	private static ConfigArq external(BufferedReader inFromUser, String ipDestino, int porta, String apelido,
			int tempoToken, boolean maquinaGeradora) throws IOException {
		String sentence = null;
		System.out.println("Enumarando ips:");
		int i = 0;
		Enumeration Interfaces = NetworkInterface.getNetworkInterfaces();
		while (Interfaces.hasMoreElements()) {
			NetworkInterface Interface = (NetworkInterface) Interfaces.nextElement();
			Enumeration Addresses = Interface.getInetAddresses();
			while (Addresses.hasMoreElements()) {
				i++;
				InetAddress Address = (InetAddress) Addresses.nextElement();
				System.out.println("ip " + i + ": " + Address.getHostAddress());
			}
		}
		System.out.println("Digite o numero do ip que voce quer");
		sentence = inFromUser.readLine();
		int sentenceInt = Integer.parseInt(sentence);
		i=0;
		
		
		InetAddress myIP = null;
		Interfaces = NetworkInterface.getNetworkInterfaces();
		while (Interfaces.hasMoreElements()) {
			NetworkInterface Interface = (NetworkInterface) Interfaces.nextElement();
			Enumeration Addresses = Interface.getInetAddresses();
			while (Addresses.hasMoreElements()) {
				i++;
				InetAddress Address = (InetAddress) Addresses.nextElement();
				if(i==sentenceInt) {
					myIP = Address;
				}
			}
		}
		System.out.println("Escolhido IP: " + myIP.getHostAddress());
		// ip = InetAddress.getLocalHost();
		// ip = InetAddress.getByName("localhost");
		// String ip = "192.168.1.12";
		//InetAddress myIP = InetAddress.getLocalHost();
		InetAddress nextIP = InetAddress.getByName(ipDestino);
		long tempoMG = -1;
		long tempoMGtolerancia = -1;

		if (maquinaGeradora == true) {
			System.out.println("Verificado que esta sera a maquina que gerara o token");
			System.out.println(
					"qual e o tempo (estipulado) para o token retornar a esta maquina depois de sair (em segundos)?");

			sentence = inFromUser.readLine();
			tempoMG = Integer.parseInt(sentence);
			tempoMG = tempoMG * 1000;

			System.out.println("Qual o tempo de tolerancia? (intervalo de tempo em torno do tempo estipulado)");
			sentence = inFromUser.readLine();
			tempoMGtolerancia = Integer.parseInt(sentence);
			tempoMGtolerancia = tempoMGtolerancia * 1000;
		}

		ConfigArq arquivoDeConfiguracao = new ConfigArq(myIP, nextIP, porta, apelido, tempoToken, maquinaGeradora,
				tempoMG, tempoMGtolerancia);

		return arquivoDeConfiguracao;
	}
}
