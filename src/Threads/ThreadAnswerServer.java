package Threads;

public class ThreadAnswerServer implements Runnable {
	
	private String sentence;

	public ThreadAnswerServer(String sentence) {
		this.sentence = sentence;
	}

	@Override
	public void run() {
		System.out.println(sentence);		
	}

}
