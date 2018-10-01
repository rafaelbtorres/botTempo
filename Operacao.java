import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Operacao implements Runnable {
	double resposta = 0;
	String operador = null;
	double operando1 = 0;
	double operando2 = 0;
	String saida = null;
	InputStream in = null;
	BufferedReader bin = null;
	Socket client = null;

	public  Operacao(Socket client){
		this.client = client;
	}

	public void parser(String in) {
		String[] dado;
		try{
			dado = in.split(":");
			operador = dado[0];
			operando1 = Double.parseDouble(dado[1]);
			operando2 = Double.parseDouble(dado[2]);
		}catch(Exception e){
			saida = e.toString();
		}
	}

	public void calculador() {
		switch (operador) {
		case "+":
			resposta = operando1 + operando2;
			break;
		case "-":
			resposta = operando1 - operando2;
			break;
		case "*":
			resposta = operando1 * operando2;
			break;
		case "/":
			if(operando2 != 0)
				resposta = operando1 / operando2;
			break;
		default:
			saida = "Erro de parâmetros";
			resposta = 0;
		}
	}

	public String toString() {
		if (saida== null) {
			return String.format("%.2f "+ operador + " %.2f = %.2f", operando1, operando2, resposta);
		}else {
			return saida;
		}
	}
	@Override
	public void run() {

		try {
			in = client.getInputStream();
			bin = new BufferedReader(new InputStreamReader(in));
			PrintWriter pout = new PrintWriter(client.getOutputStream(), true);
			String line;
			line = bin.readLine();
			System.out.println("Mensagem recebida: " + line);
			parser(line);
			calculador();
			try {
				Thread.sleep(10000); //Sleep adicionado para poder observar o uso das threads
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			pout.println(toString());
			pout.close();
			client.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
