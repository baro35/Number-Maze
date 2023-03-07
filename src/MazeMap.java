import java.io.File;
import java.util.Scanner;
import enigma.console.TextAttributes;
import java.awt.Color;

public class MazeMap {
    private File mapFile;
    private Scanner scanFile;
    private String line;
    private char[] charArray;
    private Object[][] mazeArray;
    private enigma.console.Console cn;
    private TextAttributes whiteColor;
    private TextAttributes blueColor;
    private TextAttributes blackColor;
    private TextAttributes greenColor;
    private TextAttributes yellowColor;
    private TextAttributes redColor;
    
    // Constructor for the original map:
    public MazeMap(String fileName, int rows, int columns,enigma.console.Console cn) throws Exception {
        this.mapFile = new File(fileName);;
        this.scanFile = new Scanner(mapFile);
        this.mazeArray = new Object[rows][columns];
        this.whiteColor = new TextAttributes(Color.black, Color.white);
        this.blueColor = new TextAttributes(Color.white, Color.blue);
        this.blackColor = new TextAttributes(Color.white, Color.black);

        this.cn = cn;
         greenColor = new TextAttributes(Color.green, Color.black);
         yellowColor = new TextAttributes(Color.yellow, Color.black);
         redColor = new TextAttributes(Color.red, Color.black);
        readFile();
    }

    // Constructor for the copy map for pathfinding:
    public MazeMap(MazeMap originalMaze, Number playerNumber){
      this.mazeArray = new Object[originalMaze.getMazeArray().length][originalMaze.getMazeArray()[0].length];

      for( int i = 0; i < originalMaze.getMazeArray().length; i++) {
         for (int j = 0; j < originalMaze.getMazeArray()[0].length; j++) {
            if(originalMaze.getMazeArray()[i][j] != (Object)' ' && originalMaze.getMazeArray()[i][j] != playerNumber){
               this.mazeArray[i][j] = '#';
            }
            else{
               this.mazeArray[i][j] = originalMaze.getMazeArray()[i][j];
            }
         }
      }
    }
    
    public File getMapFile() {
      return mapFile;
   }

   public void setMapFile(File mapFile) {
      this.mapFile = mapFile;
   }

   public Scanner getScanFile() {
      return scanFile;
   }

   public void setScanFile(Scanner scanFile) {
      this.scanFile = scanFile;
   }

   public String getLine() {
      return line;
   }

   public void setLine(String line) {
      this.line = line;
   }

   public char[] getCharArray() {
      return charArray;
   }

   public void setCharArray(char[] charArray) {
      this.charArray = charArray;
   }

   public Object[][] getMazeArray() {
      return mazeArray;
   }

   public void setMazeArray(Object[][] mazeArray) {
      this.mazeArray = mazeArray;
   }

   public enigma.console.Console getCn() {
      return cn;
   }

   public void setCn(enigma.console.Console cn) {
      this.cn = cn;
   }

   private void readFile(){
      // Reading each line from the text file and adding each character into the maze array:
        for( int i = 0; i < mazeArray.length; i++) {
            line = scanFile.nextLine();
            charArray = line.toCharArray();
            for (int j = 0; j < mazeArray[0].length; j++) {
               if(charArray[j] == '#'){
                  mazeArray[i][j] = '#';
               }
               else{
                  mazeArray[i][j] = ' ';
               }
            }
         }
    }
   
   public void display(){
      // Displaying each element of the maze depending on their color:
      cn.setTextAttributes(blackColor);
      cn.getTextWindow().setCursorPosition(0, 0);
      for (int i = 0; i < mazeArray.length; i++) {
         for (int j = 0; j < mazeArray[0].length; j++) {
            if(mazeArray[i][j].getClass().getName().equals("Number")){
               if(((Number) mazeArray[i][j]).getNumberType().equals("green")){
                  cn.setTextAttributes(greenColor);
               }
               else if(((Number) mazeArray[i][j]).getNumberType().equals("yellow")){
                  cn.setTextAttributes(yellowColor);
               }
               else if(((Number) mazeArray[i][j]).getNumberType().equals("red")) {
                  cn.setTextAttributes(redColor);
               }
               else{
                  cn.setTextAttributes(blueColor);
               }
               System.out.print(((Number) mazeArray[i][j]).getNumberValue());
               cn.setTextAttributes(blackColor);
            }
            else if((Character) mazeArray[i][j] == '#') {
               cn.setTextAttributes(blackColor);
               System.out.print(mazeArray[i][j]);
            }
            else{
               System.out.print(mazeArray[i][j]);
            }
         }
         System.out.println();
      }
      cn.setTextAttributes(blackColor);
   }

   public void addNumber(Number number){
      // Adding number to the maze array:
      mazeArray[number.getPositionY()][number.getPositionX()] = number;
   }
    
    
}
