package Connect4_Server;

import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

/**
@author: Tom Margosian

@description: GameLogic is called as an element of BoardColumn, and is used
to run the game.  When called with the topBtn element of BoardColumn, 
it checks which player's turn it is and "drops" a piece of that player's color
into that column.

GameLogic cannot store variables across the game, as it is a part of each BoardColumn.
Therefore, it backs up and retrives all global variables to gameIObject.

*/

public class GameLogic {
   
   private int buttonPushed = 0;
   private String currentColor = "";
   private int slotBeingSet;
   private int didSomeoneWin;
   private GameInstance gameIObject = null;

   /**
      Default constructor
   */
   public GameLogic(GameInstance object1) {
   
      gameIObject = object1;
      gameIObject.createLogic(this);
     
   
      }
   
   /**
      A check that determines further action based on whose turn it was
      @param inButtonPushed value that determines the buttonPushed value based
      on which player pushed the button.
   */
   public void playerTurn(int inButtonPushed) {
      buttonPushed = inButtonPushed;
      
      if(gameIObject.getPlayer() == 0) {
         playerLogic(1);                  
    } else if (gameIObject.getPlayer() == 1) {
         playerLogic(0);
    }
   }//End playerTurn
   
   
   /**
      This method uses FOR loops to check the column of the button pushed to determine
      where to place a piece, and what color it should be.  It also calls WinConditions for it's check,
      and ends by setting the Player, turn, and associated values in gameIObject. 
      @param inNextPlayer value which takes the value to set playerTurn to after the logic run is complete.
     
   */
   public void playerLogic(int inNextPlayer) {
            //System.out.println("Is Player" + gameIObject.getPlayer() + "'s turn");
            gameIObject.printDataServer("GameLogic: Is Player" + gameIObject.getPlayer() + "'s turn");
            int stateToSet =  gameIObject.getPlayer() + 1;

         for (int i = 0; i <  gameIObject.getNumSlots(buttonPushed) + 1; i++) {
            if (gameIObject.getSlot(buttonPushed, i) == 0) {
            
               gameIObject.printDataServer("GameLogic: Checking Column " + buttonPushed + ", slot " + i + ", got state " + gameIObject.getSlot(buttonPushed, i));
               slotBeingSet = i;
               gameIObject.setSlot(buttonPushed, i, stateToSet);
               gameIObject.printDataServer("GameLogic: Set column " + buttonPushed + ", slot " + i + " to state " + stateToSet);
               
               break;
               
            } else {
                gameIObject.printDataServer("GameLogic: Checking Column " + buttonPushed + ", slot " + i + ", got state " + gameIObject.getSlot(buttonPushed, i));
            }
         }
         
         checkWinConditions();
         
         //Cleanup (Set turnCount and CurrentPlayer), and matching GUI Elements
         gameIObject.setPlayer(inNextPlayer);
         
         gameIObject.setTurn(gameIObject.getTurn() + 1);
         checkStalemate();
         
         gameIObject.formatNetworkResponse(buttonPushed, slotBeingSet, stateToSet, inNextPlayer, didSomeoneWin);
         
         if (didSomeoneWin > 0) {
            gameIObject.resetBoard();
         }

   }
   
   //Resets win condition variable
   
   public void resetWinVariable() {
      didSomeoneWin = 0;
   }
   
   /**
      Keeps track of turn count and checks the turn count against a 
      pre-designated value to see if there is a stalemate that occurred.
   */
   public void checkStalemate() {
      if (gameIObject.getTurn() > 41) {
      
         gameIObject.resetBoard();
         didSomeoneWin = 3;
      }
   }
        
   /**
      This method uses FOR loops and IF statements, which are nested in such a way that the entire board
      is checked for a possible win condition.
      For each check, a certain subset of the board is checked.  Without a peice in this subset of the board,
      it would be impossible to win in that manner.  This is especially important in the diagonal wins.
   */
   public void checkWinConditions() {
   
      //Check Vertical Wins
      int x = 0;
      int y = 0;
      int p = 0;
       
      
      //Begin Vertical Check
      for(x = 0; x < 6; x++) {
         //Checks slots 0,1,2 only (if none of these work you can't win + arrayIndexOutOfBounds)
         for(y = 0; y < 3; y++) {
            for(p = 1; p < 3; p++) {
               if (gameIObject.getSlot(x,y) == p) {
                  if (gameIObject.getSlot(x,y+1) == p) {
                     if(gameIObject.getSlot(x,y+2) == p) {
                        if(gameIObject.getSlot(x,y+3) == p) {
                           System.out.printf("Vertical Win which starts at %d, %d",x,y);
                           didSomeoneWin = p;
                        }
                     }
                  }
               }     
            //END IFS
            }
         }
      }
      //END FORS
      
      
      //Begin Horizontal Check
      //Checks Columns 0,1,2,3 only (if none of these work you can't win + arrayIndexOutOfBounds)
      for(x = 0; x < 4; x++) {
         for(y = 0; y < 5; y++) {
            for(p = 1; p < 3; p++) {
               if (gameIObject.getSlot(x,y) == p) {
                  if (gameIObject.getSlot(x+1,y) == p) {
                     if(gameIObject.getSlot(x+2,y) == p) {
                        if(gameIObject.getSlot(x+3,y) == p) {
                           System.out.printf("Horizontal Win which starts at %d, %d",x,y);
                           didSomeoneWin = p;
                         
                        }
                     }
                  }
               }      
            //END IFS
            }
         }
      }
      //END FORS

      // //Begin Diagonal Right Check
      for(x = 0; x < 4; x++) {
         for(y = 0; y < 3; y++) {
            for(p = 1; p < 3; p++) {
               if (gameIObject.getSlot(x,y) == p) {
                  if (gameIObject.getSlot(x+1,y+1) == p) {
                     if(gameIObject.getSlot(x+2,y+2) == p) {
                        if(gameIObject.getSlot(x+3,y+3) == p) {
                           System.out.println("Right Diagonal Win At");
                           didSomeoneWin = p;
                        }
                     }
                  }
               }     
            //END IFS
            }
         }
      }
      //END FORS 
      
      //Begin Diagonal Left Check
      for(x = 3; x < 7; x++) {
         for(y = 0; y < 3; y++) {
            for(p = 1; p < 3; p++) {
               if (gameIObject.getSlot(x,y) == p) {
                  if (gameIObject.getSlot(x-1,y+1) == p) {
                     if(gameIObject.getSlot(x-2,y+2) == p) {
                        if(gameIObject.getSlot(x-3,y+3) == p) {
                           System.out.println("Left Diagonal Win At");
                           didSomeoneWin = p;
                        }
                     }
                  }
               }    
            //END IFS
            }
         }
      }
       //End Diagonal Left Check
      
        
   }
}