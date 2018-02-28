package Connect4_Client;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import java.net.*;

/**
@author: Josh Schrader, Tom Margosian, Todd Bednarczyk

@description: This class is the Main GUI for Connect4.
The class sets up all needed components for the game to look authentic.
It also holds all variables which are needed GameWide, as GameLogic
cannot hold them.
*/

public class MainGUI extends JFrame {
//Original 1260x900
   public static final int LARGE_GUI_WIDTH =  1600;
   public static final int LARGE_GUI_HEIGHT = 900;
   public static final int SMALL_GUI_WIDTH = 1250;
   public static final int SMALL_GUI_HEIGHT = 700;
   
   public static final int HEADER_WIDTH = 50;
   public static final int HEADER_HEIGHT = 50;
   public static final int LARGE_COLUMN_WIDTH = 1260;
   public static final int SMALL_COLUMN_WIDTH = 840;
   
   public static boolean largeScreen;

   
   public static final int SERVER_PORT = 23001; // the port that the server is going to be listening on
   
   public static int currentTurn = 0;
   public static int currentPlayer = 0;
   private static int clientIsPlayer;
   
   private String username = "";
   private String ip = "";
   
   // the components for the GUI
   JPanel header = new JPanel();
   JMenuBar menuBar = new JMenuBar();
   JMenu options = new JMenu("Options");
   JMenu help = new JMenu("Help");
   JMenuItem instructions = new JMenuItem("Instructions");
   JMenuItem exitItem = new JMenuItem("Exit");
   JLabel gameName = new JLabel("Connect4");
   public static JLabel whosTurn = new JLabel("Waiting for other player...");
   public static JLabel turnNumber = new JLabel("Current Turn: 0");
   
   JPanel board = new JPanel();
   MenuActions mActions = new MenuActions();
   
   private static Socket socket;
   private static PrintWriter clientWriter;
   private Scanner clientReader;
      
   //Set the ArrayList to Static
   public static ArrayList <BoardColumn> columns = new ArrayList<>();
   
   /**
      Takes care of the main setup of the gui.
   */
   public MainGUI(Socket inSocket, String inUsername, String inIpAddress){
   
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      double screenWidth = screenSize.getWidth();
      double screenHeight = screenSize.getHeight();

      if (screenWidth > 1600) {
         setSize(LARGE_GUI_WIDTH, LARGE_GUI_HEIGHT);
         largeScreen = true;
      } else {
         setSize(SMALL_GUI_WIDTH, SMALL_GUI_HEIGHT);
         largeScreen = false;
      }
      
      setLocationRelativeTo(null);
      setTitle("Connect Four");
      setLayout(new BorderLayout());
      setResizable(false);
      
      
      // menu components
      add(menuBar);
      setJMenuBar(menuBar);
      menuBar.add(options);
      menuBar.add(help);
      options.add(exitItem);
      help.add(instructions);
      
      instructions.addActionListener(mActions);
      exitItem.addActionListener(mActions);
      
      
      // header implementation
      add(header, BorderLayout.NORTH);
      header.setPreferredSize(new Dimension(HEADER_WIDTH, HEADER_HEIGHT));
      header.setLayout(new BorderLayout());
      header.setBackground(new Color(251, 251, 251));
      header.setBorder(new EmptyBorder(0, 40, 0, 40)); //provides padding for the elements in the header
      
      
      //adds the guts of the header panel
      gameName.setFont(new Font("SAN_SERIF", 1, 25));
      whosTurn.setFont(new Font("SAN_SERIF", 1, 25));
      whosTurn.setHorizontalAlignment(SwingConstants.CENTER);
      turnNumber.setFont(new Font("SAN_SERIF", 1, 20));

      header.add(gameName, BorderLayout.WEST);
      header.add(whosTurn, BorderLayout.CENTER);
      header.add(turnNumber, BorderLayout.EAST);
      
      
      //main board implementation
      add(board, BorderLayout.CENTER);
      board.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
      board.setBackground(new Color(251, 251, 251));
          
      
      //adds all the columns to the board
      for(int i = 0; i < 7; i++) {
         columns.add(new BoardColumn(i));
      }
      
      // adds all the column from the arraylist into the panel
      for(BoardColumn column : columns){
         board.add(column);
      }   
      
      for (int i = 0; i < columns.size(); i++) {
         columns.get(i).disableBtn();
      }
      
      username = inUsername;
      ip = inIpAddress;
      
      socket = inSocket;
      ServerListener sl = new ServerListener();
      

      add(new ChatClient(username,ip), BorderLayout.EAST);
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      setVisible(true);
   } 
   
     
   public void validateSetupData(String name, String ipAddress){
      boolean validData = false;
      
      if(username.equals("")){
         username = "Guest";
      }
      if(ipAddress.equals("")){
         while(validData == false){
            if(!ipAddress.equals("")){
              ip = ipAddress;
              validData = true;
            }
            else{
               ipAddress = JOptionPane.showInputDialog(this, "IP Address of Server: ");
            }
         }
      }
   }
   
   /**
      Updates the variable that determines the turn number.
      @param inTurn the value that determines the current turn number
   */
   public static void setTurn(int inTurn) {
      currentTurn = inTurn;
   }
   
   /**
      Obtains the variable that determines the turn count.
      @return the turn count
   */
   public static int getTurn() {
      return currentTurn;
   }
   
   /**
      Updates the variable that determines whose turn it is.
      @param inPlayer the value that determines the value of currentPlayer
   */
   public static void setPlayer(int inPlayer) {
      currentPlayer = inPlayer;
   }
   
   /**
      Obtains the variable that determines whose turn it is.
      @return the value of the variable currentPlayer
   */
   public static int getPlayer() {
      return currentPlayer;
   }
   
   /**
      obtains the number of slots in a specific board column in the arraylist
      param inColumn integer that represents the index in the arrayList columns
   */
   public static int getNumSlots(int inColumn) {
      int numSlots = columns.get(inColumn).getNumSlots();
      return numSlots;
   }
   
   /**
      Updates the state of the slot and changes the color based on the state.
      @param inX represents the index in the arraylist called columns
      @param inY the location of the boardslot in a board column 
      @param inState integer that determines the state of a slot
   */
   public static void setSlot(int inX, int inY, int inState) {
      columns.get(inX).getSlot(inY).setState(inState);
      columns.get(inX).getSlot(inY).updateState();
      columns.get(inX).checkColumnFull();
   } 
   
   /**
      Obtains the state of a specific slot.
      @param inX represents the index in the arraylist called columns
      @param the location of the boardslot in a board column
      @return the state of a specific slot
   */
   public static int getSlot(int inX, int inY) { 
      int slotValue = columns.get(inX).getSlot(inY).getState();
      return slotValue;
   }
   
   /**
      Resets all values in the entire board to their starting state
      and re-enables any buttons that may have been disabled during gameplay.
   */
   public static void resetBoard() {
      for(int x = 0; x < 7; x++) {
         for(int y = 0; y < 6; y++) {
            setSlot(x,y,0);
         }
      }
      currentTurn = 0;
      currentPlayer = 0;
      String currentColor = "";
      
      if(clientIsPlayer == 0) {
         updatePlayerTurnGUI("0");
      } else if (clientIsPlayer == 1) {
         updatePlayerTurnGUI("0");
      }
      
      turnNumber.setText("Current Turn: 0");
      
      //Re-Enable Buttons
      for (int i = 0; i < columns.size(); i++) {
         columns.get(i).enableBtn();
      }

   } 
   
   /**
      Shows a goodbye message to the players.
   */
   public static void exit() {
      JOptionPane.showMessageDialog(null, "Thanks for Playing.  Press OK to Exit.");
      clientWriter.flush();
      clientWriter.close();
      System.exit(0);
   }
   
   /**
      Allows the players read instructions on how to play the game.
   */
   public static void showInstructions() {
      String insTextNew = "Instructions:\n\nThe pieces fall straight down, occupying the next available space within the column. \n\nThe objective of the game is to be the first to form a horizontal, vertical, or diagonal \nline of four of one's own color discs. Press the buttons  of the desired column and \nit will fall to the next available slot. Each player can one make one move at a time. \nThe game will stop once the first player has won and give you an option to reset. \nYou can restart and save your game at any time by clicking reset on the drop down menu.";
      String insText = "The objective of the game is for a player to place their pieces\nSo that they are 4 in a Row.  This can be done\nHorizontally, Vertically, or Diagonally.";
      JOptionPane.showMessageDialog(null, insTextNew);
 
   }
   
   /**
      Updates the JLabel called whosTurn
      @param playerTurn Specified data that is recieved from the server by the ServerListener class
   */
   public static void updatePlayerTurnGUI(String playerTurn){
      if(Integer.parseInt(playerTurn) == 0) {
             if (clientIsPlayer == 0) {
               whosTurn.setText("Red's Turn (Your Turn)");
               for (int i = 0; i < columns.size(); i++) {
                  columns.get(i).enableBtn();
               }
               System.out.println("Client 0: Enabled Buttons");
            } else if (clientIsPlayer == 1) {
               whosTurn.setText("Red's Turn (Their Turn)");
               for (int i = 0; i < columns.size(); i++) {
                  columns.get(i).disableBtn();
               }
               System.out.println("Client 1: Disabled Buttons");

            }

         
      } else if (Integer.parseInt(playerTurn) == 1) {
         
            if (clientIsPlayer == 0) {
               whosTurn.setText("Yellow's Turn (Their Turn)");
               for (int i = 0; i < columns.size(); i++) {
                  columns.get(i).disableBtn();
               }
            } else if (clientIsPlayer == 1) {
               whosTurn.setText("Yellow's Turn (Your Turn)");
               for (int i = 0; i < columns.size(); i++) {
                  columns.get(i).enableBtn();
               }
            }

      }//end else if playerTurn == 1
      currentTurn++;
      turnNumber.setText("Current Turn: " + currentTurn);
   }//End UpdatePlayerTurnGUI
   
   
   /**
      Checks to see if anybody has won the game based on data sent back from the server.  This is also used 
      @param winInt Specified data that is recieved from the server by the ServerListener class
   */
   public void winCheck(String winInt){
      if(Integer.parseInt(winInt) == 1){
         JOptionPane.showMessageDialog(null, "Red wins!");
         resetBoard();
      }
      else if(Integer.parseInt(winInt) == 2){
         JOptionPane.showMessageDialog(null, "Yellow wins!");
         resetBoard();
      }
      else if(Integer.parseInt(winInt) == 3){
         JOptionPane.showMessageDialog(null, "Stalemate!");
         resetBoard();
      } else if(Integer.parseInt(winInt) == 4) {
         clientIsPlayer = 0;
         System.out.println("WinCheck: This is client " + clientIsPlayer);
         updatePlayerTurnGUI("0");
         currentTurn = 1;
         turnNumber.setText("Current Turn: " + currentTurn);

         //Re-enables all buttons (for if the column was full)
         for (int i = 0; i < columns.size(); i++) {
            columns.get(i).checkColumnFull();
         }
      } else if (Integer.parseInt(winInt) == 5) {
         clientIsPlayer = 1;
         System.out.println("WinCheck: This is client " + clientIsPlayer);
         updatePlayerTurnGUI("0");
         currentTurn = 1;
         turnNumber.setText("Current Turn: " + currentTurn);

         
         //Re-enables all buttons (for if the column was full)
         for (int i = 0; i < columns.size(); i++) {
            columns.get(i).checkColumnFull();
         }
         
      }
   }
   
   public static int setColumnNum(int inColumnNum) {
      return inColumnNum;
      
   }  
   
   
   public static void sendNetworkData(int inColumnNum){
      try{
         clientWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
      }
      catch(IOException ioe){System.out.println();}
      
      String columnNum = Integer.toString(inColumnNum);
      System.out.println("Sending: " + columnNum);
      
      clientWriter.println(columnNum);
      clientWriter.flush();
   }
      
   //INNER CLASS 
   class ServerListener implements Runnable {
      
      Thread thread;
      
      public ServerListener(){
         thread = new Thread(this);
         thread.start();
      }
      
      public void run(){
         try{
            clientReader = new Scanner(new InputStreamReader(socket.getInputStream()));
         }
         catch(IOException ioe){System.out.println("Run method IOException: " + ioe);}
         
         
         while(clientReader.hasNextLine()){
            String serverData = clientReader.nextLine();
            
            
            String[] parsedData = serverData.split(",");
            System.out.println("Recieved: " + serverData + "\n");
            
            
            // We updated the board based on data recieved from server through the setSlot method
            setSlot(Integer.parseInt(parsedData[0]),Integer.parseInt(parsedData[1]),Integer.parseInt(parsedData[2]));
            
            // Change the player turn in the GUI based on output from server
            updatePlayerTurnGUI(parsedData[3]);
            
            // Checks for win or stalemate values sent from the server
            winCheck(parsedData[4]);
         }
      }
   } //END OF SERVERLISTENER CLASS
   
}



