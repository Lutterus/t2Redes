package ServerConfig;

//esta classe serve para estruturar o que eh uma mensagem de dados

public class Dados {

	// codigo, sendo 2345 para mensagem
	private String head;
	// controle de erro, sendo "nãocopiado" por padrao
	private String controleDeErro;
	// apelido que criou a mensagem
	private String apelidoDeOrigem;
	// apelido da maquina que ira receber a mensagem
	private String apelidoDoDestino;
	// codigo de controle de erro
	private int CRC;
	// a mensagem a ser enviada
	private String mensagem;
	// variavel para o controle de erro recorrentes de uma mesma mensagem
	private boolean erro;

	public Dados(String head, String controleDeErro, String apelidoDeOrigem, String apelidoDoDestino, int CRC,
			String mensagem) {
		setHead(head);
		setControleDeErro(controleDeErro);
		setApelidoDeOrigem(apelidoDeOrigem);
		setApelidoDoDestino(apelidoDoDestino);
		setCRC(CRC);
		setMensagem(mensagem);
		setErro(false);
	}

	public String print() {
		// metodo para retornar uma mensagem usando a estrutura e ordem especificadas no
		// enunciado
		// 2345;naocopiado:Bob:Alice:19385749:Oi Mundo!
		String returnMessage = head + ";" + controleDeErro + ":" + apelidoDeOrigem + ":" + apelidoDoDestino + ":" + CRC
				+ ":" + mensagem;
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

	public boolean getErro() {
		return erro;
	}

	public void setErro(boolean erro) {
		this.erro = erro;
	}
}
