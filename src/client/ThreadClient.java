/**
 * @author mauriverti
 */

package client;

import static client.Client.ipDestino;
import static client.Client.portDestino;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class ThreadClient extends Thread {

    private final String hostname;
    private Boolean keepRunning = false;

    public ThreadClient() {
        
        String hostname = "";
        
        try {
            Process proc = Runtime.getRuntime().exec("hostname");

            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null) {
                hostname += line;
            }

            proc.waitFor();
        } catch (Exception e) {
            System.out.println("PRoblema em pegar hostname");
            e.printStackTrace();
        }
        
        this.hostname = hostname;

    }

    @Override
    public void run() {
        do {
            Socket client = null;
            keepRunning = false;
            client = clientConnect(client);
            sendToServer(client);
        } while (keepRunning);
    }

    private void sendToServer(Socket client) {
        try {
            System.out.println("Cliente " + hostname + " se conectou com sucesso!");
            Scanner scanner = new Scanner(client.getInputStream());
            if (scanner.hasNextLine()) {
                Integer port = Integer.parseInt(scanner.nextLine());
                System.out.println("Iniciando comunicacao na Porta: " + port);
                client = new Socket(ipDestino, port);
            }

            PrintStream saida = new PrintStream(client.getOutputStream());
            String getHostnameCommand = "hostname";
            Process getHostNameProcess = Runtime.getRuntime().exec(getHostnameCommand);

//            BufferedReader bufferHostname = new BufferedReader(new InputStreamReader(getHostNameProcess.getInputStream()));
//            String hostname = bufferHostname.readLine();
            String getStatusCommand = "sudo dstat -c -m -t 1";

            Process getStatusProcess = Runtime.getRuntime().exec(getStatusCommand);
            BufferedReader reader = new BufferedReader(new InputStreamReader(getStatusProcess.getInputStream()));
            String line;
            reader.readLine(); // cabecalho
            reader.readLine(); // titulos

            scanner = new Scanner(client.getInputStream());

            while ((line = reader.readLine()) != null) {
                saida.println(hostname + "->" + line);
            }

            getStatusProcess.waitFor();

            saida.close();
            client.close();
        } catch (ConnectException ce) {
            System.out.println("Falha ao se comunicar com o servidor em " + ipDestino + ":" + portDestino);
            keepRunning = true;
        } catch (Exception e) {
            System.out.println("Falha inesperada no Cliente");
            keepRunning = true;
        }

        System.out.println("client start over");

    }

    private Socket clientConnect(Socket client) {
        while (client == null) {
            try {
                client = new Socket(ipDestino, portDestino);
            } catch (IOException ex) {
            }
        }
        return client;
    }
}