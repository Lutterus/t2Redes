package Threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ServerConfig.ConfigArq;
import UserConfig.Storage;

public class ThreadListenUser implements Runnable {

	private BufferedReader inFromUser;
	private Storage storage;
	private String sentence = "";
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
			try {
				sentence = inFromUser.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			storage.addMessage(sentence, arquivoDeConfiguracao.getApelidoDaMaquinaAtual(), arquivoDeConfiguracao.getApelidoDaMaquinaDestino());
		}
	}
}
