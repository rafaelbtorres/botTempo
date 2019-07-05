package robotempo;

import robotempo.operacao.Operacao;
import java.net.*;
import java.io.*;

public class Servidor
{
	public static void main(String[] args) throws IOException {
		ServerSocket sock = null;

		try {
			sock = new ServerSocket(9001);
                        System.out.println("Servidor online");
			while (true) {
				Thread worker = new Thread(new Operacao(sock.accept()));
				worker.start();
                                System.out.println("cliente conectado.");
                               
			}
		}
		catch (IOException ioe) {
			System.err.println(ioe);
		}
		finally {
			if (sock != null)
				sock.close();
		}
	}
}
