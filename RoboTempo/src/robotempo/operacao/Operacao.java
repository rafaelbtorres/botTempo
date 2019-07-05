package robotempo.operacao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Operacao implements Runnable {

    String saida = null;
    InputStream in = null;
    BufferedReader bin = null;
    Socket client = null;

    public Operacao(Socket client) {
        this.client = client;
    }

    public void run() {

        try {
            //Cria um novo socket
            in = client.getInputStream();
            bin = new BufferedReader(new InputStreamReader(in));

            //recupera o que esta em Stream
            PrintWriter pout = new PrintWriter(client.getOutputStream(), true);

            //Transforma o que esta em Stream para String
            String line;
            line = bin.readLine();
            if (line.contains(", ")) {
                try {
                    System.out.println("Recebendo dados da cidade");
                    Thread.sleep(6000);
                    //Separa a string da cidade e do estado
                    String[] textoSeparado = line.split(", ");
                    String cidade = textoSeparado[0];
                    String estado = textoSeparado[1];

                    //Inicia as funções normais do servidor do tempo
                    ManipuladorJson run = new ManipuladorJson();
                    String retornoBusca = run.buscaCidade(cidade, estado);
                    pout.println(toString());

                    //Retorna para o cliente o resultado da busca
                    System.out.println("Enviando resposta para o cliente...");
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Operacao.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    bin = new BufferedReader(new InputStreamReader(in));

                    //Armazena em buffer o resultado
                    InputStreamReader isr = new InputStreamReader(System.in);
                    BufferedReader br = new BufferedReader(isr);
                    OutputStream outputStream = null;
                    try {
                        outputStream = client.getOutputStream();
                    } catch (IOException ex) {
                        Logger.getLogger(Operacao.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //Transforma a string em Bytes
                    outputStream.write(retornoBusca.getBytes());

                    //Envia a String para o servidor
                    PrintWriter envia = new PrintWriter(client.getOutputStream(), true);
                    try {
                        pout.println();
                    } catch (Exception e) {
                        System.out.println("ERRO de parâmetros");
                        //client.close();
                        return;
                    }
                    while ((line = bin.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {

                } catch (InterruptedException ex) {
                    Logger.getLogger(Operacao.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                try {
                    System.out.println("Buscando noticias na Web");
                    Thread.sleep(6000);

                    //Inicia as funções normais do servidor de noticias
                    ServidorNoticias executa = ServidorNoticias.getInstancia();
                    ManipuladorJsonNoticia json = new ManipuladorJsonNoticia(0);
                    ManipuladorJsonNoticia json2 = new ManipuladorJsonNoticia(2);
                    ManipuladorJsonNoticia json3 = new ManipuladorJsonNoticia(4);
                    
                    Noticia selecionada = json.preenchendoDadosCidadeEscolhida();
                    Noticia selecionada2 = json2.preenchendoDadosCidadeEscolhida();
                    Noticia selecionada3 = json3.preenchendoDadosCidadeEscolhida();


                    pout.println(toString());
                    
                     //Armazena em buffer o resultado
                    InputStreamReader isr = new InputStreamReader(System.in);
                    BufferedReader br = new BufferedReader(isr);
                    OutputStream outputStream = null;
                    try {
                        outputStream = client.getOutputStream();
                    } catch (IOException ex) {
                        Logger.getLogger(Operacao.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //Transforma a string em Bytes
                    System.out.println("Tratando dos dados recebidos");
                    String autor = selecionada.getAutor();
                    String titulo = selecionada.getTitulo();
                    String resumo = selecionada.getResumo().toString();
                    System.out.println(resumo);
                    outputStream.write(autor.getBytes());
                    PrintWriter envia = new PrintWriter(client.getOutputStream(), true);
                    outputStream.write(titulo.getBytes());
                    envia = new PrintWriter(client.getOutputStream(), true);
                    outputStream.write(resumo.getBytes());
                    envia = new PrintWriter(client.getOutputStream(), true);

                    //Envia a String para o servidor
                    System.out.println("Enviando dados para o cliente");
                    //PrintWriter envia = new PrintWriter(client.getOutputStream(), true);
                    try {
                        pout.println();
                    } catch (Exception e) {
                        System.out.println("ERRO de parâmetros");
                        //client.close();
                        return;
                    }
                    while ((line = bin.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {

                } catch (InterruptedException ex) {
                    Logger.getLogger(Operacao.class.getName()).log(Level.SEVERE, null, ex);
                } 
            }
        } catch (IOException x) {

        }
    }
}
