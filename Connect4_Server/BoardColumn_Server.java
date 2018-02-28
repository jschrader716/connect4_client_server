package Connect4_Server;

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

public class BoardColumn_Server {

      Actions actions = new Actions();
            
      private BoardSlot_Server[] slots = new BoardSlot_Server[6];
            
      public int columnNum = 0;
      
      public BoardColumn_Server(int inColumnNum) {
         columnNum = inColumnNum;
                  
         // adds a new BoardSlot starting from the last position in the slots array
         // This fixed the issue of having the first index in the array (slot[0]) from starting at the top of the board
         for(int i = slots.length - 1 ; i >= 0; i--){
            slots[i] = new BoardSlot_Server();
         }
      }//End BoardColumn Constructor
      
      /**
         @return the number of board slots in the column
      */
      public int getNumSlots(){
         return slots.length;
      }
      
      /**
         Obtains a specific slot in the BoardSlot array called slots.
         @return the location of a specific slot in the array
      */
      public BoardSlot_Server getSlot(int index){
         return slots[index];
      }
      
      /**
         @return the reference number of a board column
      */
      public int getColumnNum() {
         return columnNum;
      }  
      
      
      //Don't think this is doing anything anymore...
      class Actions implements ActionListener {
         public void actionPerformed(ActionEvent ae) {
            if(ae.getActionCommand().equals("Press Me!")) {
               System.out.println("\n");
               if(slots[5].getState() == 1 || slots[5].getState() == 2) {
                  //This was what set the button to disabled; figure out if there's a way to do over network (if necessary)
               }
               
            }
         }  
      }

}//End BoardColumn
   
