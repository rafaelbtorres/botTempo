package robotempo;

import java.net.*;
import java.io.*;
import java.io.BufferedReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import robotempo.operacao.ManipuladorJsonNoticia;
import robotempo.operacao.Noticia;
import robotempo.operacao.ServidorNoticias;

public class Cliente {

    public static void main(String[] args) throws IOException, InterruptedException {
        InputStream in = null;
        BufferedReader bin = null;
        Socket sock = null;
        String servidor_central = "127.0.0.1";
        String servidor_backup_01= "0.0.0.0";
        String servidor_backup_02= "0.0.0.0";

        //cria novo socket
        System.out.println("Conectando os servidores...");
        Thread.sleep(6000);
        int tentativas = 0;

        //redundancia de servidores
            do {
                try {
                    System.out.println("Tentativa numero: " + tentativas);
                    sock = new Socket(servidor_central, 9001);
                    System.out.println("-------------------------------------------------------------");
                    System.out.println("Conexão realizada com sucesso na porta 9001 e IP: " + servidor_central);
                    System.out.println("-------------------------------------------------------------"
                            + "    ");
                    break;
                } catch (UnknownHostException ex) {
                    System.out.println("Servidor central offline, tentando conectar no servidor_backup_01");
                    Thread.sleep(3000);
                }
                try {
                    sock = new Socket(servidor_backup_01, 9001);
                    System.out.println("-------------------------------------------------------------");
                    System.out.println("Conexão realizada com sucesso na porta 9001 e IP: " + servidor_backup_01);
                    System.out.println("-------------------------------------------------------------"
                            + "    ");
                    break;
                } catch (UnknownHostException ex) {
                    System.out.println("Servidor Backup_01, tentando conectar no servidor_backup_02");
                    Thread.sleep(3000);
                }
                try {
                    sock = new Socket(servidor_backup_02, 9001);
                    System.out.println("-------------------------------------------------------------");
                    System.out.println("Conexão realizada com sucesso na porta 9001 e IP: " + servidor_backup_02);
                    System.out.println("-------------------------------------------------------------"
                            + "    ");
                    break;
                } catch (UnknownHostException ex) {
                    Thread.sleep(3000);
                    System.out.println("Será feito nova tentativa!");
                    System.out.println(" ");
                }
                tentativas++;
                if(tentativas == 4){
                    System.out.println("Todos os servidores estão offline!");
                    Thread.sleep(10000);
                }
            } while (tentativas != 4);
            
        //criação do buffer
        in = sock.getInputStream();
        bin = new BufferedReader(new InputStreamReader(in));
        String entrada;
        System.out.println("Olá, eu sou o Robozão");
        System.out.println("Deseja que eu mostre os comandos dispoíveis?");
        do {
            String resposta;
            Scanner ent = new Scanner(System.in);
            entrada = ent.next();
            if (entrada.equals("sim")) {
                System.out.println("Esses são meus principais comandos: ");
                System.out.println("/noticia - Permitirá que você visualize as ultimas noticias dos principais meios de comunicação");
                System.out.println("/cidade - Permitirá que você consulte dados climeticos de uma determinada cidade");
                System.out.println("/autores - Mostrará na tela quais foram meus desenvolvedores");
                System.out.println("/sair - Irei encerrar minha conexão");
                System.out.println("Por favor digite algum comando: ");
            } else {
                System.out.println("Beleza, então digite algum comando: ");
            }
            entrada = ent.next();
            if (entrada.equals("/autores")) {
                System.out.println("Meus mestres foram: ");
                System.out.println("Rafael Torres, Rafael Mardegan e Vargner Ereno");
            } else if (entrada.equals("/sair")) {
                System.out.println("Encerrando o sistema...");
                Thread.sleep(6000);
            } else if (entrada.equals("/cidade")) {
                //Lê o que o cliente digitar armazena em buffer
                System.out.println("Muito bem, digite o nome da cidade seguido pelo estado separado por virgula");
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
                //Thread.sleep(1000);
                PrintWriter recebe = new PrintWriter(sock.getOutputStream(), true);

                //Transforma o que esta em Stream para String
                String line;
                line = bin.readLine();
                while ((line = bin.readLine()) != null) {
                    System.out.println("Dados recebidos com sucesso!");
                    System.out.println("");
                    String[] dadosSeparados = line.split(",");
                    System.out.println("Cidade: " + dadosSeparados[1].substring(7));
                    System.out.println("Estado: " + dadosSeparados[2].substring(8));
                    System.out.println("Temperatura atual: \"" + dadosSeparados[4].substring(21) + "°C\"");
                    System.out.println("Velocidade do Vento: \"" + dadosSeparados[6].substring(16) + "Km/h\"");
                    System.out.println("Umidade relativa do Ar: \"" + dadosSeparados[7].substring(11) + "%\"");
                    System.out.println("Céu: " + dadosSeparados[8].substring(12));
                    System.out.println("Sensação Termica: \"" + dadosSeparados[11].substring(12) + "°C\"");
                    System.out.println(" ");
                    System.out.println("Data da consulta: {{" + dadosSeparados[12].substring(7));
                    System.out.println(" ");
                    //System.out.println(line);
                    break;
                }

            } else if (entrada.equals("/noticia") || entrada.equals("/noticias")) {
                System.out.println("");
                System.out.println("--------------------------------------");
                System.out.println("Ultimas Noticias: ");
                System.out.println("--------------------------------------");
                ServidorNoticias executa = ServidorNoticias.getInstancia();
                ManipuladorJsonNoticia json = new ManipuladorJsonNoticia(0);
                ManipuladorJsonNoticia json2 = new ManipuladorJsonNoticia(2);
                ManipuladorJsonNoticia json3 = new ManipuladorJsonNoticia(4);

                Noticia selecionada = json.preenchendoDadosCidadeEscolhida();
                Noticia selecionada2 = json2.preenchendoDadosCidadeEscolhida();
                Noticia selecionada3 = json3.preenchendoDadosCidadeEscolhida();

                System.out.println("Autor: " + selecionada.getAutor() + "\n");
                System.out.println("Título: " + selecionada.getTitulo() + "\n");

                System.out.println("Resumo: " + selecionada.getTitulo() + "\n");
                for (int i = 0; i < selecionada.getResumo().size(); i++) {
                    System.out.println(selecionada.getResumo().get(i).toString() + "\n");
                    //System.out.println(selecionada.getResumo().get(i).toString());
                }
                System.out.println("Veja a notícia completa em: \n" + selecionada.getSensacaoTermica() + "\n\n=============================\n\n");
                System.out.println("Autor: " + selecionada2.getAutor() + "\n");
                System.out.println("Título: " + selecionada2.getTitulo() + "\n");

                System.out.println("Resumo: " + selecionada2.getTitulo() + "\n");
                for (int i = 0; i < selecionada2.getResumo().size(); i++) {
                    System.out.println(selecionada2.getResumo().get(i).toString() + "\n");

                }

                System.out.println("Veja a notícia completa em: \n" + selecionada2.getSensacaoTermica() + "\n\n=============================\n\n");

                System.out.println("Autor: " + selecionada3.getAutor() + "\n");
                System.out.println("Título: " + selecionada3.getTitulo() + "\n");

                System.out.println("Resumo: " + selecionada3.getTitulo() + "\n");
                for (int i = 0; i < selecionada3.getResumo().size(); i++) {
                    System.out.println(selecionada3.getResumo().get(i).toString() + "\n");
                    //System.out.println(selecionada.getResumo().get(i).toString());
                }

                System.out.println("Veja a notícia completa em: \n" + selecionada3.getSensacaoTermica() + "\n\n=============================\n\n");
            }
            System.out.println("Deseja mais alguma coisa?");
        } while (!"/sair".equals(entrada));
        sock.close();
    }
}
