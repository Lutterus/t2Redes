package UserConfig;

import java.util.ArrayList;

public class Storage {

	private ArrayList<String> message;

	public Storage() {
		message = new ArrayList<String>();
	}

	public void addMessage(String newMessage) {
		this.message.add(newMessage);
	}

	public void print() {
		for (String string : message) {
			System.out.println(string);
		}
	}
}
