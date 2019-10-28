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

	public String getFirtMessage() {
		if (message.size() == 0) {
			return null;
		} else {
			String returnMessage = message.get(0);
			message.remove(0);
			return returnMessage;
		}

	}

	public void print() {
		for (String string : message) {
			System.out.println(string);
		}
	}
}
