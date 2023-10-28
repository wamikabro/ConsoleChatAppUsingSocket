package ConsoleChatAppUsingSocket;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username){
        try{
            this.socket = socket;
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        }catch (IOException e){
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    public void sendMessage(){
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()){
                String message = scanner.nextLine();
                bufferedWriter.write(username + ": " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch (IOException e){
            closeEverything(socket, bufferedWriter, bufferedReader);
        }
    }

    public void listenForMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromGroupChat;
                while(socket.isConnected()){
                    try{
                        messageFromGroupChat = bufferedReader.readLine();
                        System.out.println(messageFromGroupChat);
                    }catch (IOException e){
                        closeEverything(socket, bufferedWriter, bufferedReader);
                    }

                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader){
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
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your Username: ");
        String username = scanner.nextLine();

        Socket socket = null;
        try {
            socket = new Socket("localhost",5555);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}
