package Connect4_Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
@author: Tom Margosian
@description: This class handles all the functionality of each menu item
in the game.
*/  
      
class MenuActions implements ActionListener {
   public void actionPerformed(ActionEvent ae) {
      if(ae.getActionCommand().equals("Exit")) {
         System.out.print("Exit Called");
         MainGUI.exit();
      } else if(ae.getActionCommand().equals("Instructions")) {
         MainGUI.showInstructions();
      }
   }  
}
