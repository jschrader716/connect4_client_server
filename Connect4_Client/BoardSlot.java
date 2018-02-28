package Connect4_Client;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
@author: Josh Schrader

@description: This class is a slot object, where the slots are drawn
and the state of the slot is stored.  
*/

public class BoardSlot extends JPanel{
   
   private static final Color blankColor = new Color(255,255,255);
   private static final Color p1Color = new Color(164, 0, 32); 
   private static final Color p2Color = new Color(223, 204, 41); 
   
   /**
      The state variable determines the state in which the slot is in Ex: if the state is equal to 0,
      then the slot is considered empty and the color of the slot will be white.
      0 = empty/blank/vacant/void/nonexistent etc...
      1 = player 1
      2 = player 2
   */
   private int state = 0;
   
   public BoardSlot(){
      setOpaque(false);
      setVisible(true);
   }
   
   /**
      Sets up how each circle looks in every panel in the board column class,
      calculates the size of the circle, and sets the color of the circle based
      on its state.
   */
   @Override
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      
      
      // makes circles not look shitty
      Graphics2D g2 = (Graphics2D)g;
      RenderingHints rh = new RenderingHints(
         RenderingHints.KEY_ANTIALIASING,
         RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setRenderingHints(rh);
      
      // Changes color of circle based on state
      switch(state){
         case 0: 
            g2.setColor(blankColor);
            break;
         case 1:
            g2.setColor(p1Color);
            break;
         case 2:
            g2.setColor(p2Color);
            break;
      }
      
      // calculates size of circle
      int size = getHeight() - 20;
      int xCoord = (getWidth()/2) - (size/2);
      int yCoord = (getHeight()/2) - (size/2);
      
      // draws the actual circle
      g2.fillOval(xCoord, yCoord, size, size);
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
   
   /**
      Uses the repaint function to update the look of the boardslot based on its new state value.
   */
   public void updateState() {
      super.repaint();
   }
}