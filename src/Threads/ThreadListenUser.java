package Threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ServerConfig.ConfigArq;
import UserConfig.Storage;

//classe/thread para ouvir constantemente o usuario

public class ThreadListenUser implements Runnable {
	// instanciamento do teclado
	private BufferedReader inFromUser;
	// classe storage que armazena as mensagens
	private Storage storage;
	// mensagem atual
	private String sentence = "";
	// classe com as informacoes uteis de configuracao
	private ConfigArq arquivoDeConfiguracao;

	public ThreadListenUser(ConfigArq arquivoDeConfiguracao, Storage storage) {
		this.storage = storage;
		this.arquivoDeConfiguracao = arquivoDeConfiguracao;
		setBeforeRun();
	}

	public void setBeforeRun() {
		// cria o stream do teclado
		inFromUser = new BufferedReader(new InputStreamReader(System.in));
	}

	@Override
	public void run() {
		while (true) {
			// le o que foi escrito no teclado
			try {
				sentence = inFromUser.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// mensagens devem vir seguindo o padrao
			// <apelidoDestino>;<mensagem> ou TODOS;<mensagem>
			String[] array = sentence.split(";");
			// separa o destino
			String apelidoDestino = array[0];
			// separa a mensagem
			try {
				String mensagem = array[1];

				// adiciona na classe que armazena a lista de mensagens a nova mensagem
				storage.addMessage(mensagem, arquivoDeConfiguracao.getApelidoDaMaquinaAtual(), apelidoDestino);
			} catch (Exception e) {
				System.err.println("mensagem inserida no formato incorreto, utilize o formato informado");
			}
			
		}
	}
}
