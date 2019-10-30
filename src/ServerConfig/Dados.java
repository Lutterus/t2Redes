package ServerConfig;

public class Dados {

	private String head;
	private String controleDeErro;
	private String apelidoDeOrigem;
	private String apelidoDoDestino;
	private int CRC;
	private String mensagem;

	public Dados(String head, String controleDeErro, String apelidoDeOrigem, String apelidoDoDestino, int CRC, String mensagem) {
		setHead(head);
		setControleDeErro(controleDeErro);
		setApelidoDeOrigem(apelidoDeOrigem);
		setApelidoDoDestino(apelidoDoDestino);
		setCRC(CRC);
		setMensagem(mensagem);
	}
	
	public String print() {
		//2345;naocopiado:Bob:Alice:19385749:Oi Mundo!
		String returnMessage = head+";"+controleDeErro+":"+apelidoDeOrigem+":"+apelidoDoDestino+":"+CRC+":"+mensagem;
		return returnMessage;
	}

	public String getHead() {
		return head;
	}

	public void setHead(String head) {
		this.head = head;
	}

	public String getControleDeErro() {
		return controleDeErro;
	}

	public void setControleDeErro(String controleDeErro) {
		this.controleDeErro = controleDeErro;
	}

	public String getApelidoDeOrigem() {
		return apelidoDeOrigem;
	}

	public void setApelidoDeOrigem(String apelidoDeOrigem) {
		this.apelidoDeOrigem = apelidoDeOrigem;
	}

	public String getApelidoDoDestino() {
		return apelidoDoDestino;
	}

	public void setApelidoDoDestino(String apelidoDoDestino) {
		this.apelidoDoDestino = apelidoDoDestino;
	}

	public int getCRC() {
		return CRC;
	}

	public void setCRC(int cRC) {
		CRC = cRC;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
}
