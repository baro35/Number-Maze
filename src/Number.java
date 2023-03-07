import java.util.Random;

public class Number {
    private int numberValue;
    private Coordinate crd;
    private String numberType;

    private int direction;
    private int Xdirection;
    private int Ydirection;

    private int directionRange;

    private int resetTime;

    // Constructor for the player number:
    public Number(int numberValue, int positionX, int positionY, String numberType) {
        this.numberValue = numberValue;
        crd = new Coordinate(positionX,positionY);
        this.numberType = numberType;

        this.direction = 0;
        this.Xdirection = 0;
        this.Ydirection = 0;

        this.directionRange = 0;

        this.resetTime = 0;
    }

    // Constructor for computer numbers:
    public Number(int numberValue, int positionX, int positionY) {
        this.numberValue = numberValue;
        
        crd = new Coordinate(positionX,positionY);
        if(numberValue >= 1 && numberValue <=3){
            this.numberType = "green";
        }
        else if(numberValue >= 4 && numberValue <=6){
            this.numberType = "yellow";
        }
        else{
            this.numberType = "red";
        }
    }

    public int getResetTime() {
        return resetTime;
    }

    public void setResetTime(int resetTime) {
        this.resetTime = resetTime;
    }

    public int getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(int numberValue) {
        this.numberValue = numberValue;
    }

    public int getPositionX() {
        return crd.getX();
    }

    public void setPositionX(int positionX) {
        this.crd.setX(positionX); 
    }

    public int getPositionY() {
        return crd.getY();
    }

    public void setPositionY(int positionY) {
        this.crd.setY(positionY);
    }

    public String getNumberType() {
        return numberType;
    }

    public void setNumberType(String numberType) {
        this.numberType = numberType;
    }

    public Coordinate getCrd() {
        return crd;
    }

    public void setCrd(Coordinate crd) {
        this.crd = crd;
    }

    public boolean availableSpace(Object[][] mazeArray, Number playerNumber){
        // Checking if any of the 4 directions is empty space or player number:
        if(mazeArray[crd.getY() + 0][crd.getX() + 1] == (Object) ' '|| mazeArray[crd.getY() + 0][crd.getX() - 1] == (Object) ' '|| mazeArray[crd.getY() + 1][crd.getX() + 0] == (Object) ' '|| mazeArray[crd.getY() -1][crd.getX() + 0] == (Object) ' ') return true;
 
        if(mazeArray[crd.getY() + 0][crd.getX() + 1] == playerNumber|| mazeArray[crd.getY() + 0][crd.getX() - 1] == playerNumber|| mazeArray[crd.getY() + 1][crd.getX() + 0] == playerNumber|| mazeArray[crd.getY() - 1][crd.getX() + 0] == playerNumber) return true;

        return false;
    }

    public boolean randomMovement(MazeMap mazeMap, Number playerNumber, Number[] numbers, int numberCounter,Backpacks backpacks){
        Object[][] mazeArray = mazeMap.getMazeArray();

        // If there is an available space to go:
        if(availableSpace(mazeArray,playerNumber)){ 
            
            // Changing the direction until finding a way to go:
            while(mazeArray[crd.getY() + Ydirection][crd.getX() + Xdirection] == (Object) '#' || mazeArray[crd.getY() + Ydirection][crd.getX() + Xdirection].getClass().getName().equals("Number") && mazeArray[crd.getY() + Ydirection][crd.getX() + Xdirection] != playerNumber || directionRange == 0){
                randomDirection();
            }

            // If there is empty space on the way, moving towards it:
            if(mazeArray[crd.getY() + Ydirection][crd.getX() + Xdirection] == (Object) ' ') {
                mazeArray[crd.getY() + Ydirection][crd.getX() + Xdirection] = mazeArray[crd.getY()][crd.getX()];
                mazeArray[crd.getY()][crd.getX()] = ' ';
                crd.setX(crd.getX() + Xdirection);
                crd.setY(crd.getY() + Ydirection);
                directionRange--;
            }
            // If there is player number on the way, trying to eat it:
            else if(mazeArray[crd.getY() + Ydirection][crd.getX() + Xdirection] == playerNumber){
                return ComputerEatNumber(playerNumber, numberCounter, mazeArray, numbers, backpacks);
            }
        }
        return false;
    }

    public boolean pathFindingDFS(MazeMap mazeMap,Number playerNumber, Coordinate start, Object[][] dotsArray, Number[] numbers,int numberCounter,Backpacks backpacks){
        // Generating a copy of the map for path finding:
        MazeMap copyMap = new MazeMap(mazeMap,playerNumber);

        Stack crdStack = new Stack(23*55);
        Object[][] mazeArray = mazeMap.getMazeArray();

        // Pushing the starting coordinate into the stack:
        crdStack.push(start);

        Coordinate nextElement;
        Coordinate newElement;
        Coordinate previous;
        boolean isMoved = false;

        while(!crdStack.isEmpty()){
            // Keeping the next element by popping from the stack:
            nextElement = (Coordinate) crdStack.pop();

            // Marking the coordinate of the element:
            copyMap.getMazeArray()[nextElement.getY()][nextElement.getX()] = 'X';

            // For each direction:
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if((i == 0 && j != 0) || (j == 0 && i != 0)){

                        // If there is a player number on the way:
                        if(copyMap.getMazeArray()[nextElement.getY()+i][nextElement.getX()+j] == playerNumber){

                            // If the red number is next to the player number, trying to eat the player number:
                            if(start == nextElement){
                                return ComputerEatNumber(playerNumber, numberCounter, mazeArray, numbers, backpacks);
                            }

                            // Otherwise, going back to the starting number one by one from the current element and adding each dot to the path: 
                            previous = nextElement;
                            while(true){
                                if(start == previous.getPreviousPosition()){
                                    break;
                                }
                                else{
                                    dotsArray[previous.getY()][previous.getX()] = '.';
                                    previous = previous.getPreviousPosition();
                                }
                            }

                            // Moving the number one step along the path:
                            mazeArray[previous.getY()][previous.getX()] = mazeArray[crd.getY()][crd.getX()];

                            if(previous.getY() != crd.getY() || previous.getX() != crd.getX()){
                                mazeArray[crd.getY()][crd.getX()] = ' ';
                            }

                            crd.setX(previous.getX());
                            crd.setY(previous.getY());

                            isMoved = true;
                            break;
                        }
                        // If there is an empty space on the way:
                        if(copyMap.getMazeArray()[nextElement.getY()+i][nextElement.getX()+j] == (Object) ' '){
                            // Adding the coordinates of the empty space as a new element to the stack:
                            newElement = new Coordinate(nextElement.getX()+j,nextElement.getY()+i,nextElement);

                            // Marking the coordinate of the element:
                            copyMap.getMazeArray()[newElement.getY()][newElement.getX()] = 'X';

                            crdStack.push(newElement);
                        }
                    }
                }
                if(isMoved) break;
            }
            if(isMoved) break;
        }
        return false;
    }

    // Same algorithm with using queue:
    public boolean pathFindingBFS(MazeMap mazeMap,Number playerNumber, Coordinate start, Object[][] dotsArray, Number[] numbers,int numberCounter,Backpacks backpacks){
        MazeMap copyMap = new MazeMap(mazeMap,playerNumber);
        Queue crdQueue = new Queue(23*55);
        Object[][] mazeArray = mazeMap.getMazeArray();

        crdQueue.enqueue(start);

        Coordinate nextElement;
        Coordinate newElement;
        Coordinate previous;
        boolean isMoved = false;

        while(!crdQueue.isEmpty()){
            nextElement = (Coordinate) crdQueue.dequeue();
            copyMap.getMazeArray()[nextElement.getY()][nextElement.getX()] = 'X';

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if((i == 0 && j != 0) || (j == 0 && i != 0)){
                        if(copyMap.getMazeArray()[nextElement.getY()+i][nextElement.getX()+j] == playerNumber){
                            if(start == nextElement){
                                return ComputerEatNumber(playerNumber, numberCounter, mazeArray, numbers, backpacks);
                            }
                            previous = nextElement;
                            while(true){
                                if(start == previous.getPreviousPosition()){
                                    break;
                                }
                                else{
                                    dotsArray[previous.getY()][previous.getX()] = '.';
                                    previous = previous.getPreviousPosition();
                                }
                            }
                            mazeArray[previous.getY()][previous.getX()] = mazeArray[crd.getY()][crd.getX()];

                            if(previous.getY() != crd.getY() || previous.getX() != crd.getX()){
                                mazeArray[crd.getY()][crd.getX()] = ' ';
                            }
                            
                            crd.setX(previous.getX());
                            crd.setY(previous.getY());
                            isMoved = true;
                            break;
                        }

                        if(copyMap.getMazeArray()[nextElement.getY()+i][nextElement.getX()+j] == (Object) ' '){
                            newElement = new Coordinate(nextElement.getX()+j,nextElement.getY()+i,nextElement);
                            copyMap.getMazeArray()[newElement.getY()][newElement.getX()] = 'X';
            
                            crdQueue.enqueue(newElement);
                        }
                    }
                }
                if(isMoved) break;
            }
            if(isMoved) break;
        }
        return false;
    }

    private boolean ComputerEatNumber(Number playerNumber,int numberCounter,Object[][] mazeArray, Number[] numbers, Backpacks backpacks){
        // If the player number is bigger:
        if(numberValue <= playerNumber.getNumberValue()){
            // Removing the computer number and adding it to the backpacks:
            for (int i = 0; i < numberCounter; i++) {
                if(mazeArray[crd.getY()][crd.getX()] == numbers[i]){
                    backpacks.addNumber(numbers[i].getNumberValue());
                    numbers[i] = null;
                    mazeArray[crd.getY()][crd.getX()] = ' ';
                    return false;
                }
            }
        }
        // Otherwise, ending the game:
        return true;
    }

    public boolean playerEatNumber(MazeMap mazeMap, Number[] numbers, int numberCounter, Backpacks backpacks){
        // If the player number is bigger:
        if(((Number) mazeMap.getMazeArray()[crd.getY()][crd.getX()]).getNumberValue() <= numberValue){
            // Removing the computer number and adding it to the backpacks:
            for (int i = 0; i < numberCounter; i++) {
                if(mazeMap.getMazeArray()[crd.getY()][crd.getX()] == numbers[i]){
                    backpacks.addNumber(numbers[i].getNumberValue());
                    numbers[i] = null;
                    return false;
                }
            }
        }
        // Otherwise, ending the game:
        return true;
    }

    public void randomDirection(){
        // Generating random direction and random range for that direction:
        Random rand = new Random();
        direction = rand.nextInt(4)+1;
        directionRange = rand.nextInt(5)+3;
        if(direction == 1){
            //Left direction
            Xdirection = -1;
            Ydirection = 0;
        }
        else if(direction == 2){
            //Right direction
            Xdirection = +1;
            Ydirection = 0;
        }
        else if(direction == 3){
            //Down direction
            Xdirection = 0;
            Ydirection = 1;
        }
        else if(direction == 4){
            //Up direction
            Xdirection = 0;
            Ydirection = -1;
        }
    }
}
