package Connect4_Client;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


/**
@author: Josh Schrader, Tom Margosian

@description: BoardColumn represents a single column on the board.
Each column has a border layout that separates the JButton from the
rest of the slots in the panel. The slots are structured using a grid
layout and each slot is represented by the BoardSlot class. These board slots
are managed in an array called slots. Each column has its own array of slots.
The logic for interacting with those slots should be placed within this class.  

*/

public class BoardColumn extends JPanel {

      Actions actions = new Actions();
      
      public static final int largeColumnWidth = (180);
      public static final int smallColumnWidth = (120);
      public static final int largeColumnHeight = (MainGUI.LARGE_GUI_HEIGHT - MainGUI.HEADER_HEIGHT);      
      public static final int smallColumnHeight = (MainGUI.SMALL_GUI_HEIGHT - MainGUI.HEADER_HEIGHT);

       
      private BoardSlot[] slots = new BoardSlot[6];
      
      JButton topBtn = new JButton("Drop Token");
      private boolean columnFull = false;
      JPanel slotPanel = new JPanel();
      
      public int columnNum = 0;
      
      public BoardColumn(int inColumnNum) {
      
         columnNum = inColumnNum;
         
         setLayout(new BorderLayout());
         
         if (MainGUI.largeScreen == true) {
            setPreferredSize(new Dimension((largeColumnWidth - 10), largeColumnHeight - 50));
         } else if (MainGUI.largeScreen == false) {
            setPreferredSize(new Dimension((smallColumnWidth - 10), smallColumnHeight - 50));
         }
         
         slotPanel.setLayout(new GridLayout(6, 1, 0, 0));
         slotPanel.setBackground(new Color(79, 109, 206));
         
         add(topBtn, BorderLayout.NORTH);
         add(slotPanel, BorderLayout.CENTER);
         
         // adds a new BoardSlot starting from the last position in the slots array
         // This fixed the issue of having the first index in the array (slot[0]) from starting at the top of the board
         for(int i = slots.length - 1 ; i >= 0; i--){
            slots[i] = new BoardSlot();
            slotPanel.add(slots[i]);
         }
         topBtn.addActionListener(actions);

         setVisible(true);
      }//End BoardColumn Constructor
      
      /**
         @return the number of board slots in the column
      */
      public int getNumSlots(){
         return slots.length;
      }
      
      public void checkColumnFull() {
         if(slots[5].getState() == 1 || slots[5].getState() == 2) {
            topBtn.setEnabled(false);
            columnFull = true;
         } else {
            columnFull = false;
         }
      }
      
      /**
         Enables the topBtn.
      */
      public void enableBtn() { 
         if (columnFull == false) {
            topBtn.setEnabled(true);
         }
      }
      
      /**
      Disables the topBtn
      */
      
      public void disableBtn() {
         if (columnFull == false) {
            topBtn.setEnabled(false);
         }
      }
      
      /**
         Obtains a specific slot in the BoardSlot array called slots.
         @return the location of a specific slot in the array
      */
      public BoardSlot getSlot(int index){
         return slots[index];
      }
      
      /**
         @return the reference number of a board column
      */
      public int getColumnNum() {
         return columnNum;
      }  
      
      class Actions implements ActionListener {
         public void actionPerformed(ActionEvent ae) {
            if(ae.getActionCommand().equals("Drop Token")) {
               System.out.println("\n");
               //Calls method to send network data to drop the token in the column specified
               MainGUI.sendNetworkData(columnNum);
               
               
               
               if(slots[5].getState() == 1 || slots[5].getState() == 2) {
                  topBtn.setEnabled(false);
               }
               
            }
         }  
      }

}//End BoardColumn
   
