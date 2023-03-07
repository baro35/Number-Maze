import enigma.core.Enigma;
import enigma.event.TextMouseEvent;
import enigma.event.TextMouseListener;
//import jdk.internal.org.objectweb.asm.tree.analysis.Value;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import enigma.console.TextAttributes;
import java.awt.Color;
import java.util.Random;
import java.util.Scanner;

public class NumberMaze {
   public enigma.console.Console cn = Enigma.getConsole("NumberMaze",100,30,16);
   public TextMouseListener tmlis; 
   public KeyListener klis; 

   // ------ Standard variables for mouse and keyboard ------
   public int mousepr;          // mouse pressed?
   public int mousex, mousey;   // mouse text coords.
   public int keypr;   // key pressed?
   public int rkey;    // key   (for press/release)
   // ----------------------------------------------------
   
   public static Number[] numbers;
   public static int numberCounter;
   public static Random rand;
   public static int difficulty;
   public static int choosenValue;
   
   public Object[][] dotsArray;
   public MazeMap mazeMap;
   boolean gameOver; 
   boolean restart = true; 

   public Number playerNumber; 
   public Backpacks backpacks;

   NumberMaze() throws Exception {   // --- Contructor
                 
      // ------ Standard code for mouse and keyboard ------ Do not change
      tmlis=new TextMouseListener() {
         public void mouseClicked(TextMouseEvent arg0) {}
         public void mousePressed(TextMouseEvent arg0) {
            if(mousepr==0) {
               mousepr=1;
               mousex=arg0.getX();
               mousey=arg0.getY();
            }
         }
         public void mouseReleased(TextMouseEvent arg0) {}
      };
      cn.getTextWindow().addTextMouseListener(tmlis);
    
      klis=new KeyListener() {
         public void keyTyped(KeyEvent e) {}
         public void keyPressed(KeyEvent e) {
            if(keypr==0) {
               keypr=1;
               rkey=e.getKeyCode();
            }
         }
         public void keyReleased(KeyEvent e) {}
      };
      cn.getTextWindow().addKeyListener(klis);
      // ----------------------------------------------------

      // Main game loop:
      while(restart == true){
         // Resetting the structures:
         numbers = new Number[1000];
         numberCounter = 0;
         rand = new Random();
         mazeMap = new MazeMap("maze.txt",23,55,cn);
         gameOver = false;
         
         // Player chooses the difficulty:
         chooseDifficulty();

         // Creating the player number:
         int[] randomPosition = randomAvailablePosition();
         playerNumber = new Number(choosenValue,randomPosition[0],randomPosition[1],"blue");

         Queue inputList = new Queue(10);
         
         // Adding 10 random number value to the input list:
         for (int i = 0; i < 10; i++) {
            inputList.enqueue(randomNumberValue());
         }
         
         int time = 0; 
         
         // Inserting 25 numbers to the game:
         for (int i = 0; i < 25; i++) {
            insertNumber(inputList,mazeMap); 
         }

         // Displaying the input list:
         cn.getTextWindow().setCursorPosition(58,2);
         for (int i = 0; i < inputList.size(); i++) {
            System.out.print(inputList.peek());
            inputList.enqueue(inputList.dequeue());
         }

         // Adding the player number into the map:
         mazeMap.addNumber(playerNumber);

         // Displaying map, menu and backpacks:
         mazeMap.display();
         displayMenu();
         backpacks = new Backpacks(cn);
         backpacks.display();

         // Waiting 1 second before starting the game to let the player find out the location:
         Thread.sleep(1000);

         while(true) {
            if(keypr==1) {    // if keyboard button pressed

               // Depending on the key pressed, moving the player number:
               if(rkey==KeyEvent.VK_LEFT && playerNumber.getNumberValue() != 1){
                  playerMovement(-1,0);
                  if(gameOver) break;
               }  

               if(rkey==KeyEvent.VK_RIGHT && playerNumber.getNumberValue() != 1){
                  playerMovement(+1,0);
                  if(gameOver) break;
               } 

               if(rkey==KeyEvent.VK_UP && playerNumber.getNumberValue() != 1){
                  playerMovement(0,-1);
                  if(gameOver) break;
               } 

               if(rkey==KeyEvent.VK_DOWN && playerNumber.getNumberValue() != 1){
                  playerMovement(0,+1);
                  if(gameOver) break;
               } 
               
               char rckey=(char)rkey;
               
               // Depending on the key pressed, transferring the numbers between backpacks:
               if(rckey=='q' || rckey=='Q'){
                  backpacks.transferNumber(1); 
               }
               else if(rckey=='w' || rckey=='W'){
                  backpacks.transferNumber(0); 
               }

               keypr=0;    // last action  
            }

            // If the player number value is 1 and there is no match for 4 seconds, set player number as 2:
            if(time - playerNumber.getResetTime() == 80 && playerNumber.getNumberValue() == 1){
               playerNumber.setNumberValue(2);
            }

            // Inserting new number to the game each five seconds:
            if(time % 100 == 0 && time > 0){
               insertNumber(inputList,mazeMap); 
               
               cn.getTextWindow().setCursorPosition(58,2);
               for (int i = 0; i < inputList.size(); i++) {
                  System.out.print(inputList.peek());
                  inputList.enqueue(inputList.dequeue());
               }
            } 
            
            // Number movements:
            if(time % 10 == 0){
               numberMovements();
               if(gameOver) break;
            }
            
            // Displaying map, path finding dots and backpacks:
            mazeMap.display();
            displayDots();
            backpacks.display();
            
            // Checks backpacks and changes the value of the number:
            if(backpacks.checkNumbers()){
               playerNumber.setNumberValue(playerNumber.getNumberValue()+1);
               if(playerNumber.getNumberValue() > 9){
                  playerNumber.setNumberValue(1);
                  playerNumber.setResetTime(time);
               } 
            }

            // Display time:
            cn.getTextWindow().setCursorPosition(64, 22);
            System.out.println(time/20);

            // Display total points:
            cn.getTextWindow().setCursorPosition(65, 20);
            System.out.println(backpacks.getTotalPoints());
            
            Thread.sleep(50);
            time++;
         }

         // If the loop is broken, displaying game over screen:
         cn.getTextWindow().setCursorPosition(34, 24);
         System.out.println("--Game Over--");
         Scanner s = new Scanner(System.in);
         System.out.print("Would you like to play again? (Y/N): ");
         String answer = s.nextLine();
         if(answer.equalsIgnoreCase("n")){
            restart = false;
         } 
         else{
            // Clearing the console:
            cn.getTextWindow().setCursorPosition(0, 0);
            for (int i = 0; i < 26; i++) {
               for (int j = 0; j < 90; j++) {
                  System.out.print(" ");
               }
               System.out.println();
            }
            cn.getTextWindow().setCursorPosition(0, 0);
         }
      }
   }

   public void chooseDifficulty(){
      // Changing starting value and number possibilities depending on the difficulty choose:
      System.out.println("1- Easy");
      System.out.println("2- Normal");
      System.out.println("3- Hard");
      System.out.println("4- Nightmare");
      System.out.print("Choose difficulty: ");

      Scanner s = new Scanner(System.in);
      String choose = s.nextLine();

      if(choose.equals("4")){
         choosenValue = 4;
         difficulty = 80; 
      }
      else if(choose.equals("3")){
         choosenValue = 3;
         difficulty = 50;

      }
      else if(choose.equals("2")){
         choosenValue = 4;
         difficulty = 10;
      }
      else{
         choosenValue = 5;
         difficulty = 1;
      }
   }

   public int[] randomAvailablePosition(){
      int randomX;
      int randomY;
      int[] returnArray = new int[2];

      // Changing x and y coordinates randomly until empty space was found:
      while(true){
         randomX= rand.nextInt(54)+1;
         randomY= rand.nextInt(22)+1;
         if(mazeMap.getMazeArray()[randomY][randomX].getClass().getName().equals("java.lang.Character")){
            if((Character) mazeMap.getMazeArray()[randomY][randomX] == ' '){
               returnArray[0] = randomX;
               returnArray[1] = randomY;
               break;
            }
         }
      }
      return returnArray;
   }
   
   public void playerMovement(int Dx, int Dy){
      // Checking if the given position is a wall or empty space: 
      if(mazeMap.getMazeArray()[playerNumber.getPositionY() + Dy][playerNumber.getPositionX() + Dx].getClass().getName().equals("java.lang.Character")){

         // If the given position is not a wall, then moving to that position:
         if( mazeMap.getMazeArray()[playerNumber.getPositionY() + Dy][playerNumber.getPositionX() + Dx] != (Object)'#'){

            mazeMap.getMazeArray()[playerNumber.getPositionY()][playerNumber.getPositionX()] = ' ';
            mazeMap.getMazeArray()[playerNumber.getPositionY() + Dy][playerNumber.getPositionX() + Dx] = playerNumber;

            playerNumber.setPositionX(playerNumber.getPositionX() + Dx);
            playerNumber.setPositionY(playerNumber.getPositionY() + Dy);
         }
      }
      // If there is no wall or empty space, that means there is a number: 
      else{
         mazeMap.getMazeArray()[playerNumber.getPositionY()][playerNumber.getPositionX()] = ' ';

         playerNumber.setPositionX(playerNumber.getPositionX() + Dx);
         playerNumber.setPositionY(playerNumber.getPositionY() + Dy);

         // If can't eat the number, ending the game:
         if(playerNumber.playerEatNumber(mazeMap,numbers,numberCounter,backpacks)){
            gameOver = true;
         }
         // Otherwise, moving the number to the given position:
         else{
            mazeMap.getMazeArray()[playerNumber.getPositionY()][playerNumber.getPositionX()] = playerNumber;
         }
      }
   }

   public void numberMovements(){
      // Resetting the path finding dots each tour:
      dotsArray = new Object[23][55];
      
      // Depending on the color of each number, moving the numbers randomly or with using pathfinding:
      for (int i = 0; i < numberCounter; i++) {
         if(numbers[i]!= null && numbers[i].getNumberType().equals("yellow")){
            gameOver = numbers[i].randomMovement(mazeMap,playerNumber,numbers,numberCounter,backpacks);
            if(gameOver) break;
         }
         else if(numbers[i]!= null && numbers[i].getNumberType().equals("red")){
            gameOver = numbers[i].pathFindingBFS(mazeMap,playerNumber,numbers[i].getCrd(),dotsArray,numbers,numberCounter,backpacks);
            if(gameOver) break; 
         }
      }
   }

   public void displayDots(){
      // Displaying the dots:
      for (int i = 0; i < dotsArray.length; i++) {
         for (int j = 0; j < dotsArray[0].length; j++) {
            if(dotsArray[i][j] == (Object) '.' && !mazeMap.getMazeArray()[i][j].getClass().getName().equals("Number")){
               cn.getTextWindow().setCursorPosition(j, i);
               System.out.print(dotsArray[i][j]);
            } 
         }
      }
   } 

   public static int randomNumberValue(){
      // Depending on the probability value and number selection, generating random number value:
      int probability = rand.nextInt(100)+difficulty;
      int selection = rand.nextInt(3)+1;
      
      if(probability>0&&probability<=75) {
          switch(selection) {
          case 1:
              return 1;
          case 2:
              return 2;
          case 3:
              return 3;
          }
      }
      else if(probability>75&&probability<=95) {
        switch(selection) {
        case 1:
            return 4;
        case 2:
            return 5;
        case 3:
            return 6;
        }
      }
      else
        switch(selection) {
        case 1:
            return 7;
        case 2:
            return 8;
        case 3:
            return 9;
        }
        return -1;
   }

   public void insertNumber(Queue inputList, MazeMap mazeMap){
      // Generating random number value and random position:
      int randomNum = randomNumberValue();
      int[] randomPosition = randomAvailablePosition();

      // Adding generated number into the numbers array:
      numbers[numberCounter] = new Number((Integer)inputList.dequeue(),randomPosition[0],randomPosition[1]);
      
      // Adding generated number into the map:
      mazeMap.addNumber(numbers[numberCounter]);
      numberCounter++;

      // Adding generated number into the queue:
      inputList.enqueue(randomNum);
   }

   public void displayMenu(){
      // Displaying the menu:
      cn.getTextWindow().setCursorPosition(58, 0);
      System.out.println("Input");
      cn.getTextWindow().setCursorPosition(58, 1);
      System.out.println("<<<<<<<<<<");
      cn.getTextWindow().setCursorPosition(58, 3);
      System.out.println("<<<<<<<<<<");
      
      cn.getTextWindow().setCursorPosition(58, 6);
      System.out.println("Backpacks");

      cn.getTextWindow().setCursorPosition(58, 7);
      System.out.println("|   | |   |");

      cn.getTextWindow().setCursorPosition(58, 8);
      System.out.println("|   | |   |");

      cn.getTextWindow().setCursorPosition(58, 9);
      System.out.println("|   | |   |");

      cn.getTextWindow().setCursorPosition(58, 10);
      System.out.println("|   | |   |");

      cn.getTextWindow().setCursorPosition(58, 11);
      System.out.println("|   | |   |");

      cn.getTextWindow().setCursorPosition(58, 12);
      System.out.println("|   | |   |");

      cn.getTextWindow().setCursorPosition(58, 13);
      System.out.println("|   | |   |");

      cn.getTextWindow().setCursorPosition(58, 14);
      System.out.println("|   | |   |");
      
      cn.getTextWindow().setCursorPosition(58, 15);
      System.out.println("+---+ +---+");

      cn.getTextWindow().setCursorPosition(58, 16);
      System.out.println("Left  Right");

      cn.getTextWindow().setCursorPosition(58, 17);
      System.out.println("  Q     W");

      cn.getTextWindow().setCursorPosition(58, 20);
      System.out.println("Score:");

      cn.getTextWindow().setCursorPosition(58, 22);
      System.out.println("Time:");
   }
}
