package Threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import UserConfig.Storage;

public class ThreadListenUser implements Runnable {

	private BufferedReader inFromUser;
	private Storage storage;
	private String sentence = "";

	public ThreadListenUser() {
		setBeforeRun();
	}

	public void setBeforeRun() {
		storage = new Storage();
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

			storage.addMessage(sentence);
		}
	}
}
