import java.net.*;
import java.io.*;

public class DateServer
{
	public static void main(String[] args) throws IOException {
		ServerSocket sock = null;

		try {
			sock = new ServerSocket(6013);
			while (true) {
				Thread worker = new Thread(new Operacao(sock.accept()));
				worker.start();
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