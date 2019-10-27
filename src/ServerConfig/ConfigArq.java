package ServerConfig;

import java.net.InetAddress;

public class ConfigArq {
	
	private InetAddress ipDestinoToken;
	private int porta;
	private String ApelidoDaMaquinaAtual;
	private int tempoToken;
	private boolean token;
	
	public ConfigArq(InetAddress ip, int porta, String apelido, int tempo, boolean token) {
		setIpDestinoToken(ip);
		setPorta(porta);
		setApelidoDaMaquinaAtual(apelido);
		setTempoToken(tempo);
		setToken(token);
	}
	
	public void print() {
		System.out.println("ip: " + ipDestinoToken.toString());
		System.out.println("porta " + porta);
		System.out.println("apelido: " + tempoToken);
		System.out.println("token: " + token);
	}

	public InetAddress getIpDestinoToken() {
		return ipDestinoToken;
	}

	public void setIpDestinoToken(InetAddress ipDestinoToken2) {
		this.ipDestinoToken = ipDestinoToken2;
	}

	public String getApelidoDaMaquinaAtual() {
		return ApelidoDaMaquinaAtual;
	}

	public void setApelidoDaMaquinaAtual(String apelidoDaMaquinaAtual) {
		ApelidoDaMaquinaAtual = apelidoDaMaquinaAtual;
	}

	public int getTempoToken() {
		return tempoToken;
	}

	public void setTempoToken(int tempoToken) {
		this.tempoToken = tempoToken;
	}

	public boolean getToken() {
		return token;
	}

	public void setToken(boolean token) {
		this.token = token;
	}

	public int getPorta() {
		return porta;
	}

	public void setPorta(int porta) {
		this.porta = porta;
	}
}
