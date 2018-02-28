package Connect4_Server;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
@author: Josh Schrader  
*/

public class BoardSlot_Server {
      
   /**
      The state variable determines the state in which the slot is in Ex: if the state is equal to 0,
      then the slot is considered empty and the color of the slot will be white.
      0 = empty/blank/vacant/void/nonexistent etc...
      1 = player 1
      2 = player 2
   */
   private int state = 0;
   
   public BoardSlot_Server(){
   
   }
   
   /**
      Determines the state of the slot in 'this' particular instance.
      @param integer value that is used to set the state of a board slot
   */
   public void setState(int inState){
      this.state = inState;
   }
   
   /**
      @return the state of the board slot
   */
   public int getState(){
      return state;
   }
}