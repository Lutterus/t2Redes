package ServerConfig;

public class Token {

	private boolean status;

	public Token(ConfigArq arquivoDeConfiguracao) {
		if(arquivoDeConfiguracao.getToken()==true) {
			setStatus(true);
		}else {
			setStatus(false);
		}
		
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}
}
