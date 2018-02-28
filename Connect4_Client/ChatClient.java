package Connect4_Client;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient extends JPanel {

    JButton sendMsg = new JButton("send");
    JTextArea chatArea = new JTextArea();
    JScrollPane jsp = new JScrollPane(chatArea);
    JTextField message = new JTextField();
    

    public static final int CHAT_PORT = 23002;
    private Socket socket = null;
    private Scanner chatScanner = null;
    private PrintWriter chatWriter = null;

    private ReceiveChat chat = null;
    


    public ChatClient(String username, String ipaddress){
        super(new BorderLayout());
        
        Panel msgSendPnl = new Panel(new GridLayout(1,2));
        
        msgSendPnl.add(message);
        msgSendPnl.add(sendMsg);
        
        DefaultCaret caret = (DefaultCaret)chatArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        message.setColumns(15);
        chatArea.setEditable(false);
        
        add(jsp, BorderLayout.CENTER);
        add(msgSendPnl, BorderLayout.SOUTH);
        

        try {
            socket = new Socket(ipaddress, CHAT_PORT);
            chatScanner = new Scanner(new InputStreamReader(socket.getInputStream()));
            chatWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            sendMsg.setEnabled(true);
            message.setEnabled(true);

            chatWriter.println(username);
            chatWriter.flush();


        } catch (IOException e) {
            chatArea.append("Cannot connect to server");
            return;

        }
        chat = new ReceiveChat();
        chat.start();


        sendMsg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMsg();
            }
        });
        
        message.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent ke) {
                if(ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMsg();
                }
            }
        });

        //fff
    }


    /**
     * SendMsg first checks to see if the user is trying to execute the command "exit".
     * If the user is trying to exit, it sends a message to the server and then shuts down.
     * If the user is not trying to exit then it sends their message to the server
     */
    private void sendMsg(){
        if (message.getText().equals("/exit")){
            String disconnect = "/exit";
            sendMsg(disconnect);

            System.exit(0);
        }
        sendMsg(message.getText());
    }

    /**
     * This send message sends a particular message to the server and then erases what is in the message field.
     * @param msg the message to be sent to the server
     */
    private void sendMsg(String msg){
        if (!msg.equals("\\\\kill//")) {
            chatWriter.println(msg);
            chatWriter.flush();
            message.setText("");
        }
        else {
            chatArea.append("--INVALID MESSAGE--");
            chatArea.append("\n");
        }
    }
    
    class ReceiveChat extends Thread {

        String msg;

        /**
         * The main loop and thread of the class. This listens for messages from the server. For normal messages
         * it appends them to the text area. If the message from the server is \\kill// then
         */
        public void run(){
            while (chatScanner.hasNextLine()){
                msg = chatScanner.nextLine();
                if (msg.equals("\\\\kill//")){
                    sendMsg.setEnabled(false);
                    chatArea.setEnabled(false);
                    sendMsg.setEnabled(true);
                    chatArea.setEnabled(true);
                    break;
                }
                else {
                    chatArea.append(msg);
                    chatArea.append("\n");
                }
            }
        }


    }

}
