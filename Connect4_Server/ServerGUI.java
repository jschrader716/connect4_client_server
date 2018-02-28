package Connect4_Server;

import javax.swing.*;
import javax.swing.border.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

/**
@author: Brandon Hettler

@description: ServerGUI acts as the main 'hub' for all client connections as it accepts it using
server socket. After the client connects, a new thread instance of ClientThread is started which remains
in the loop until another player is connected. After doing so, there socket connections are passed to 
GameInstance/GameLogic on the server to begin the game.

Although the client socket connections are passed to other classes on the server end, ServerGUI must remain
open or the clients will be forced out.

*/


public class ServerGUI extends JFrame {

   private ServerSocket sSocket = null;
   // public InetAddress localhost = null;
   public static final int SERVER_PORT = 23001;
   public int clientsConnected = 0;
   public int gamesConnected = 0;
   
   private Socket cSocket = null;
   
   public JLabel jLogLabel = new JLabel("Log:");
   public JTextArea jLogArea = new JTextArea(10, 35);
   public JLabel clientsConnected_label = new JLabel("Clients Connected: 0");
   public JLabel serverIP_label = new JLabel("                         Server IP: XXX.XXX.XXX.XXX");

   public JLabel ipLabel = new JLabel("");
   
   
   public JButton start = new JButton("Start Server");
   public JButton stop = new JButton("Stop Server");
   
   ArrayList<ClientThread> threads = new ArrayList<ClientThread>();
   ArrayList<GameLogic> gameLogic_threads = new ArrayList<GameLogic>();
   ArrayList<GameInstance> gameInstance_threads = new ArrayList<GameInstance>();



    /**
      ServerGUI Constructor
   */
   
   public ServerGUI() {
      try{
      String serverIP = (Inet4Address.getLocalHost().getHostAddress());
      } catch(UnknownHostException uhe){}

      this.setTitle("Server GUI");
      this.setSize(450, 250);
      this.setLocation(600, 50);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      JPanel central = new JPanel();
      JPanel northern = new JPanel();
      JPanel southern = new JPanel();
      central.add(jLogLabel);
      central.add(new JScrollPane(jLogArea));
      southern.add(clientsConnected_label);
      
      try{
         String serverIP = (Inet4Address.getLocalHost().getHostAddress());
         serverIP_label.setText("                         Server IP: " + serverIP);

      } catch(UnknownHostException uhe){}

      southern.add(serverIP_label);
      
      this.add(northern, BorderLayout.NORTH);
      this.add(central, BorderLayout.CENTER);
      this.add(southern, BorderLayout.SOUTH);
      

      this.setVisible(true); //last note

      /**
      ChatThread Constructor - creates new chat
      */

       Thread chatThread = new Thread() {
           @Override
           public void run(){
               ChatServer cServer = new ChatServer();
           }
       };

       chatThread.start();
      
         
      /** Server Information */
      try {
         sSocket = new ServerSocket(SERVER_PORT);
         jLogArea.append("Server Started!\n");
      }
      catch(IOException e1) {
         System.out.println("Uh oh! An exception");
      }
      
   while(true) {
      for (int b = 0; b < threads.size(); b++) {
         if (threads.get(b).getSocket().isConnected() == false) {
            clientsConnected--;
            clientsConnected_label.setText("Clients Connected: " + clientsConnected);
            
         }
      }
         
         
         Socket cSocket = null; /* Client Socket */

         try {
            /* Wait for a connection */
            cSocket = sSocket.accept();
            clientsConnected++;
            
            clientsConnected_label.setText("Clients Connected: " + clientsConnected);

         }
         catch(IOException e1) {
            System.out.println("Uh oh! An exception");
         }
         
         System.out.println("Client connected!");
         String ipConnected = cSocket.getRemoteSocketAddress().toString();
         jLogArea.append("Client connected!  IP: "+ ipConnected.substring(1) + "\n");
         
         System.out.println(cSocket);
         
         synchronized(threads) {
         
            threads.add(new ClientThread(cSocket));
            
            Thread t = new Thread(threads.get(clientsConnected - 1));
            t.start();
            
         }
         
      } 
    } 
    
    /**
      ClientThread - creates new thread upon socket connection
      of a client.
    */
      
   class ClientThread extends Thread {
      Socket cSocket = null;
      
      public ClientThread(Socket clientsSocket) {
         cSocket = clientsSocket;
         
      }
         
      public Socket getSocket() {
         return cSocket;
      }
         
      
      public void run() {
          PrintWriter pwt = null;
         Scanner scn = null;
         boolean message_sent = false;
         boolean gameStarted = false;
         
         try {
               pwt = new PrintWriter(new OutputStreamWriter(cSocket.getOutputStream()));
               scn = new Scanner(new InputStreamReader(cSocket.getInputStream()));
            }
            catch (IOException e1) {
               System.out.println("Uh oh! An exception");
            }
        while (getSocket().isConnected()) {
           while (gameStarted == false && clientsConnected > 0) {

               if (clientsConnected % 2 != 0) {
                     if (message_sent == false) {
                        message_sent = true;
                        
                     }
               }
               else {
                  System.out.println("Starting Game...");
                  jLogArea.append("Starting Game " + (gamesConnected + 1) + "\n");
                  
                  gameStarted = true;
                  gamesConnected++;
                  
                  /** MULTI-THREADING CODE */
                  gameInstance_threads.add(new GameInstance(threads.get(clientsConnected - 2).getSocket(), threads.get(clientsConnected - 1).getSocket(), gamesConnected));
                  gameLogic_threads.add(new GameLogic(gameInstance_threads.get(gamesConnected - 1)));
                  
               }
                  
            }
            
                  try{
                     sleep(1000);
                  }
                  catch (InterruptedException e1) {
                     //
                  }
         }
         
            System.out.println("Client disconnected!");
            jLogArea.append("Client disconnected!\n");
            
            clientsConnected--;
            clientsConnected_label.setText("Clients Connected: " + clientsConnected);
      }
      
   }

   /**
      Main Method - creates new object of server GUI
   */
   
   public static void main (String [] args) {
      new ServerGUI();
   }
   
}
