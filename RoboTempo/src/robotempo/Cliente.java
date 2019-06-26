package robotempo;

import java.net.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {

    public static void main(String[] args) throws IOException, InterruptedException {
        InputStream in = null;
        BufferedReader bin = null;
        Socket sock = null;

        try {
            //cria novo socket
            System.out.println("Conectando ao servidor...");
            Thread.sleep(6000);
            System.out.println("-------------------------------------------------------------");
            System.out.println("Conexão realizada com sucesso na porta 9001 e IP 127.0.0.0.1!");
            System.out.println("-------------------------------------------------------------"
                    + "    ");
            sock = new Socket("127.0.0.1", 9001);
            in = sock.getInputStream();
            bin = new BufferedReader(new InputStreamReader(in));
            
            //Lê o que o cliente digitar armazena em buffer
            System.out.println("Olá, digite o nome da cidade seguido pelo estado separado por virgula");
            System.out.println("Exemplo: Alegrete, RS");
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(isr);
            String digitado = br.readLine(); 
            OutputStream outputStream = sock.getOutputStream();
            
            //Transforma a string em Bytes
            outputStream.write(digitado.getBytes());
            
            //Envia a String para o servidor
            PrintWriter pout = new PrintWriter(sock.getOutputStream(), true);
            try {
                pout.println();
            } catch (Exception e) {
                System.out.println("ERRO de parâmetros");
                sock.close();
                return;
            }
            
            
            
            
            //Recebe a resposta do servidor
            System.out.println("--------------------------------------");
            System.out.println("Aguardando resposta do servidor!");
            System.out.println("--------------------------------------");
            System.out.println(" ");
            Thread.sleep(6000);
            PrintWriter recebe = new PrintWriter(sock.getOutputStream(), true);

            //Transforma o que esta em Stream para String
            String line;
            line = bin.readLine();            
            while ((line = bin.readLine()) != null) {
                System.out.println("Dados recebidos com sucesso!");
                System.out.println("");
                String[] dadosSeparados = line.split(",");
                System.out.println("Cidade: "+ dadosSeparados[1]);
                System.out.println("Estado: "+ dadosSeparados[2]);
                System.out.println("Temperatura atual: "+ dadosSeparados[4]+"°C");
                System.out.println("Velocidade do Vento: "+ dadosSeparados[6] + "Km/h");
                System.out.println("Umidade relativa do Ar: "+ dadosSeparados[7]+ "%");
                System.out.println("Céu: "+ dadosSeparados[8]);
                System.out.println("Sensação Termica: "+ dadosSeparados[11]);
                System.out.println(" ");
                System.out.println("Data da consulta: "+ dadosSeparados[12]);
                //System.out.println(line);
            }
        } catch (IOException ioe) {
            System.err.println(ioe);
        } finally {
            sock.close();
        }
    }
}
