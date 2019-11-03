package ServerConfig;

import java.net.InetAddress;

//esta classe representa o arquivo de configuracoes e infomacoes adicionais que sao vitais para o funcionamento do programa

public class ConfigArq {

	// ip desta maquina
	private InetAddress ipOrigemToken;
	// ip da maquina direita
	private InetAddress ipDestinoToken;
	// ip da porta padrao a ser utilizada
	private int porta;
	// apelido da maquina atual
	private String ApelidoDaMaquinaAtual;
	// tempo que o pacote deve permancer nesta maquina
	private int tempoToken;
	// se esta maquina, atualmente (em tempo de execucao), esta com o token
	private boolean token;
	// se esta foi a maquina agerar o token e eh quem fara o controle do mesmo
	private boolean maquinaGeradora;
	//tempo estipulado em que o token deve levar para dar a volta
	private long tempoMG;
	//tempo de tolerancia em torno do tempo estipulado
	//ex: tempoMG = 5 segundos, tempoMGtolerancia = 1 segundo.
	//se o jogo chegar em de 4 a 6 segundos, esta ok. Se chegar em menos de 4 ou mais de 6, nao esta ok
	private long tempoMGtolerancia;
	//ultima vez que estava com o token
	private long tokenAntes;
	//se devera ignorar o proximo token que vier
	private boolean ignoreToken;

	public ConfigArq(InetAddress ip, InetAddress nextIP, int porta, String apelidoOrigem, int tempo, boolean token, long tempoMG, long tempoMGtolerancia) {
		setIpOrigemToken(ip);
		setIpDestinoToken(nextIP);
		setPorta(porta);
		setApelidoDaMaquinaAtual(apelidoOrigem);
		setTempoToken(tempo);
		setToken(token);
		setMaquinaGeradora(token);
		setTempoMG(tempoMG);
		setTempoMGtolerancia(tempoMGtolerancia);
		setTokenAntes(-1);
		setIgnoreToken(false);

	}

	public void print() {
		System.out.println("ip: " + ipDestinoToken.toString());
		System.out.println("porta " + porta);
		System.out.println("tempo token: " + tempoToken);
		System.out.println("apelido: " + ApelidoDaMaquinaAtual);
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

	public InetAddress getIpOrigemToken() {
		return ipOrigemToken;
	}

	public void setIpOrigemToken(InetAddress ipOrigemToken) {
		this.ipOrigemToken = ipOrigemToken;
	}

	public boolean getMaquinaGeradora() {
		return maquinaGeradora;
	}

	public void setMaquinaGeradora(boolean maquinaGeradora) {
		this.maquinaGeradora = maquinaGeradora;
	}

	public long getTempoMG() {
		return tempoMG;
	}

	public void setTempoMG(long tempoMG2) {
		this.tempoMG = tempoMG2;
	}

	public long getTempoMGtolerancia() {
		return tempoMGtolerancia;
	}

	public void setTempoMGtolerancia(long tempoMGtolerancia2) {
		this.tempoMGtolerancia = tempoMGtolerancia2;
	}

	public long getTokenAntes() {
		return tokenAntes;
	}

	public void setTokenAntes(long l) {
		this.tokenAntes = l;
	}

	public boolean getIgnoreToken() {
		return ignoreToken;
	}

	public void setIgnoreToken(boolean ignoreToken) {
		this.ignoreToken = ignoreToken;
	}
}
