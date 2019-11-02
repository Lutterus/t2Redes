package UserConfig;

import java.util.ArrayList;
import java.util.Random;

//esta classe ser para guardar a pila de mensagens
// e implementa as operacoes sobre elas

import ServerConfig.Dados;
import sun.misc.CRC16;

public class Storage {

	// array com as mensagens
	private ArrayList<Dados> messages;
	//porcentagem de erro
	private int errorChance = 20;//%

	public Storage() {
		// inicializacao do array
		messages = new ArrayList<Dados>();
	}

	public void addMessage(String newMessage, String apelidoMaquinaAtual, String apelidoMaquinaDestino) {
		// metodo para adicao de mensagens
		if (messages.size() == 10) {
			// faz a verificacao se as mensagem armazenadas estao abaixo do limite
			// estipulado
			// printando, em formato de erro, caso exceda o limite
			System.err.println(
					"Nao foi possivel salvar esta mensagem, pois o limite de 10 mensagens foi atingido. Aguarde o envio de uma mensagem e tente novamente");
		} else {
			//calcula se havera erro, usando a probabilidade
			boolean error = calculateError();
			// se ainda estiver abaixo do limite
			// a mensagem eh armazena usando o tipo DADO
			int CRC = calculateCRC(newMessage, error);
			// novas mensagens sao sempre adicionas com o controle de erro "nãocopiado"
			Dados dado = new Dados("2345", "nãocopiado", apelidoMaquinaAtual, apelidoMaquinaDestino, CRC, newMessage);
			this.messages.add(dado);
		}
	}

	private boolean calculateError() {
		Random random = new Random();
		int answer = random.nextInt(100 - 0 + 1);
		if(answer<=errorChance) {
			return true;
		} else {
			return false;
		}
	}

	public int calculateCRC(String currentMesage, boolean error) {
		byte[] bytes = new byte[] { (byte) 0x08, (byte) 0x68, (byte) 0x14, (byte) 0x93, (byte) 0x01, (byte) 0x00,
	            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x01,
	            (byte) 0x00, (byte) 0x13, (byte) 0x50, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x22, (byte) 0x09,
	            (byte) 0x11 };
	    byte[] byteStr = new byte[4];
	    Integer crcRes = new Storage().calculate_crc(bytes);
	    
	    //System.out.println("o int: " + crcRes);
	    
	    //System.out.println("o int em hex: " + Integer.toHexString(crcRes));

	    byteStr[0] = (byte) ((crcRes & 0x000000ff));
	    byteStr[1] = (byte) ((crcRes & 0x0000ff00) >>> 8);

	    //System.out.printf("os bytes "+"%02X\n%02X", byteStr[0],byteStr[1]);
	    
	    if(error==true) {
	    	return crcRes+1;
	    } else {
	    	return crcRes;
	    }
	}
	
	private int calculate_crc(byte[] bytes) {
	    int i;
	    int crc_value = 0;
	    for (int len = 0; len < bytes.length; len++) {
	        for (i = 0x80; i != 0; i >>= 1) {
	            if ((crc_value & 0x8000) != 0) {
	                crc_value = (crc_value << 1) ^ 0x8005;
	            } else {
	                crc_value = crc_value << 1;
	            }
	            if ((bytes[len] & i) != 0) {
	                crc_value ^= 0x8005;
	            }
	        }
	    }
	    return crc_value;
	}

	public String getFirtMessage() {
		// retorna a primeira mensagem da pilha
		if (messages.size() == 0) {
			return null;
		} else {
			String returnMessage = messages.get(0).print();
			return returnMessage;
		}

	}

	public boolean getFirtMessageState() {
		// retorna o "estado" da primeira mensagem da pila
		// este metodo serve para verificar, em caso de erro, se o erro ja ocorreu outra
		// vez
		return messages.get(0).getErro();
	}

	public void setFirtMessageState() {
		// altera o "estado" da primeira mensagem da pila
		messages.get(0).setErro(true);
	}

	public void removeFirstMessage() {
		// remove a primeira mensagem da pilha
		messages.remove(0);
	}

	public void print() {
		// printa todas mensagens armazenas
		for (Dados dado : messages) {
			System.out.println(dado.print());
		}
	}
}
