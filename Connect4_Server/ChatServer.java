package Connect4_Server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Developed by Todd Bednarczyk, Josh Schader, Brandon Hettler, and Tom Margosian
 *
 * ChatServer is the server that corresponds to Chat Client. It is built as a test in order to later be implemented
 * in a java game. The chat server handles an infinite amount of clients. The protocol for this program is the following.
 *
 *
 *
 * Client Connects: Sends over username as a string.
 * After this initial connection the server will handle all data as messages to be sent to other clients. If a client
 * sends a message, the server returns that message to all clients, including the one that was sent by the initial
 * client. The reason it also sends the message back to the sender is so that the sender is informed that the message
 * was received by the server
 *
 *
 * The exception is if the message is /shutdown or /exit. These messages are seen as commands by the server.
 *
 * If the command is /exit it disconnects the user and it tells all other clients that the particular client has
 * disconnected.
 *
 * If the command is /shutdown then the server than requests a password from the client. If the client sends the correct
 * password than the server displays a message to all clients, disconnects all clients and stops the server.
 */
public class ChatServer {

    ArrayList<ClientChat> clients = new ArrayList<>();
    private ServerSocket chatSocket = null;
    public static final int CHAT_PORT = 23002;

    /**
     * The Constructor for the Chat Server is what accepts all the new clients. It also creates the server.
     */
    public ChatServer() {

        int clientID = 0;

        try {
            chatSocket = new ServerSocket(CHAT_PORT);
        } catch (IOException e){
            e.printStackTrace();
        }

        while(true){
            Socket cSocket = null;
            try {
                cSocket = chatSocket.accept();
                System.out.println("Client Connected");
            } catch (IOException e){
                e.printStackTrace();
            }

            clients.add(new ClientChat(cSocket));
            clients.get(clientID++).start();

        }



    }

    /**
     * Shuts the server down
     */
    public void endClients(){
        try {
            chatSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }


    /**
     * This Thread names ClientChat handles all of the different clients
     */
    class ClientChat extends Thread {
        private Socket chatSocket;
        private String userName = null;
        boolean on;

        /**
         * Gets the passed over client Socket and stores it in the thread.
         * @param cSocket the client socket to be stored
         */
        public ClientChat( Socket cSocket) {
            chatSocket = cSocket;
            on = true;
        }

        /**
         * The thread method. Handles all messages sent to the server.
         */
        @Override
        public void run(){
            PrintWriter pw = null;
            Scanner sc = null;

            try {
                pw = new PrintWriter(new OutputStreamWriter(chatSocket.getOutputStream()));
                sc = new Scanner(new InputStreamReader(chatSocket.getInputStream()));
            }
            catch (IOException e){
                e.printStackTrace();
                return;
            }

            userName = sc.nextLine();

            sendGroupMsg(String.format("%s connected!",userName));

            while (sc.hasNextLine()){
                String message = sc.nextLine();

                if (message.equals("/exit")) {
                    message = String.format("%s disconnected",userName);
                    sendGroupMsg(message);
                    sc.close();
                    pw.close();
                    on = false;
                    break;
                }
                else if (message.equals("/shutdown")) { //Command shutdown
                    sendMsg(">>Server: Enter Password");
                    while (sc.hasNextLine()){


                        String pswd = sc.nextLine();//Checks for password in order to shutdown
                        if (pswd.equals("14450")){
                            sendGroupMsg(">>Server: Shutting down ");
                            sendGroupMsg("\\\\kill//");
                            endClients();
                        }
                        else {
                            pw.println(">>Server: Invalid Password");
                            pw.flush();
                            break;
                        }
                    }

                }
                else if (message.equals("\\\\kill//")){
                    message = "";
                }
                else {

                    message = String.format("%-10s: %s", userName, message);

                    sendGroupMsg(message);
                }
            }
        }

        /**
         * This method sends messages to the client on this particular thread
         * @param message the message to be sent
         */
        public void sendMsg(String message){
            PrintWriter pw = null;

            try {
                pw = new PrintWriter(new OutputStreamWriter(chatSocket.getOutputStream()));
            }
            catch (IOException e){
                e.printStackTrace();
            }



            pw.write(message + "\n");
            pw.flush();
        }

        /**
         * This method sends messages to multiple clients on all threads. It will not send messages to clients that
         * have disconnected.
         * @param message the message to be sent
         */
        public void sendGroupMsg(String message){
            for (int i = 0; i< clients.size(); i++) {
                if (clients.get(i).getOn()) {
                    clients.get(i).sendMsg(message);
                }
            }
        }

        /**
         * Getter for the value of on
         * @return true if the client is still connected and false if the client is disconnected
         */
        public boolean getOn() {
            return on;
        }


    }

}

