package org.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable { //implements runnable so that instances can be executed

    public static ArrayList<ClientHandler>clientHandlers = new ArrayList<>(); // the purpose of this array list is to keep track of all the clients
    private Socket socket;
    private BufferedReader bufferedReader; //to read messages from clients
    private BufferedWriter bufferedWriter; //to write messages to other clients
    private String clientUsername;

    public ClientHandler(Socket socket){
        try{
            this.socket = socket;
            this.bufferedWriter =new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader =new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername =bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " +clientUsername + " has entered the chat" );

        }catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()){
            try{
              messageFromClient = bufferedReader.readLine();
              broadcastMessage(messageFromClient);
            }catch (IOException e){
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }
    public void broadcastMessage (String messageToSend){
        for (ClientHandler clientHandler: clientHandlers){
          try{
              if (!clientHandler.clientUsername.equals(clientUsername)){
                 clientHandler.bufferedWriter.write(messageToSend);
                 clientHandler.bufferedWriter.newLine();
                 clientHandler.bufferedWriter.flush();
              }
          }catch (IOException e){
             closeEverything(socket, bufferedReader, bufferedWriter);
          }
        }
    }
    public  void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " +clientUsername + " has left the chat!");
    }
    public  void closeEverything(Socket socket, BufferedReader bufferedReader,BufferedWriter bufferedWriter ){
        removeClientHandler();
        try{
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
