/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package projetoldp;




import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import java.net.ServerSocket;

public class GameServer {
    public static void runServer(int port) {
        
        try {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                System.out.println("Server iniciado. À espera dos clientes...");
                // Aceita a conecção dos dois clientes
                Socket client1 = serverSocket.accept();
                Socket client2 = serverSocket.accept();
                // Começa um novo jogo com essas ligações
                
                // Cria um thread para "ouvir" o cliente 1
                Thread listenerThread1 = new Thread(() -> startListener(client1,client2));
                listenerThread1.start();
                // Cria um thread para "ouvir" o cliente 2
                Thread listenerThread2 = new Thread(() -> startListener(client2,client1));
                listenerThread2.start();
                // Fecha o socket do servidor
            }
        } catch (IOException e) {
        }
    }
    
        private static void startListener(Socket clientSocket, Socket client2) {
            try {
                InputStream inputStream = clientSocket.getInputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;

                // Loop para receber as mensagens continuamente
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    String receivedMessage = new String(buffer, 0, bytesRead);
                    System.out.println("Recebido: " + receivedMessage);

                    // Processa a mensagem recebida

                    // Exemplo: Enviar uma resposta de volta para o cliente
                    clientSocket.getOutputStream().write(receivedMessage.getBytes());
                    client2.getOutputStream().write(receivedMessage.getBytes());
                }

             // Fechar o socket do cliente

         } catch (IOException e) {
             // Lida com exceções de E/S (Input/Output)
        }
}

      public static void main(String[] args) {
          runServer(5000);
      }
}
