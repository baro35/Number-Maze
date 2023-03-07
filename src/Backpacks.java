public class Backpacks {
    private Stack leftStack;
    private Stack rightStack;
    private enigma.console.Console cn;
    private int totalPoints;

    public Backpacks(enigma.console.Console cn) {
        this.leftStack = new Stack(8);
        this.rightStack = new Stack(8);
        this.cn = cn;
        this.totalPoints = 0;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public void addNumber(int number){
        // Adding the number to the left stack:
        if(!leftStack.isFull()){
            leftStack.push(number);
        }
        else{
            leftStack.pop();
            leftStack.push(number);
        }
    }

    public void display(){
        int positionX = 60;
        int positionY = 14;

        Stack tempStack = new Stack(8);

        // Clearing the backpacks:
        for (int i = 7; i <= 14; i++) {
            cn.getTextWindow().setCursorPosition(60, i);
            System.out.println(" ");

            cn.getTextWindow().setCursorPosition(66, i);
            System.out.println(" ");
        }

        // Reversing and displaying the backpacks:
        while(!leftStack.isEmpty()){
            tempStack.push(leftStack.pop());
        }
        while(!tempStack.isEmpty()){
            cn.getTextWindow().setCursorPosition(positionX, positionY);
            System.out.print(tempStack.peek());
            leftStack.push(tempStack.pop());
            positionY--;
        }

        positionX = 66;
        positionY = 14;

        while(!rightStack.isEmpty()){
            tempStack.push(rightStack.pop());
        }
        while(!tempStack.isEmpty()){
            cn.getTextWindow().setCursorPosition(positionX, positionY);
            System.out.print(tempStack.peek());
            rightStack.push(tempStack.pop());
            positionY--;
        }
    }

    public void transferNumber(int side){
        // Transferring the number from the choosen side to the other side:
        if(side == 0 && !rightStack.isFull() && !leftStack.isEmpty()){
            rightStack.push(leftStack.pop());
        }
        else if(side == 1 && !leftStack.isFull() && !rightStack.isEmpty()){
            leftStack.push(rightStack.pop());
        }
    }

    public boolean checkNumbers(){
        Stack tempStack = new Stack(8);
        boolean isMatched = false;
        
        // If both of the stacks are not empty:
        if(!leftStack.isEmpty() && !rightStack.isEmpty()){

            // Equalizing the stack levels depending on the bigger one:
            if(leftStack.size() > rightStack.size()){
                while(leftStack.size() != rightStack.size()){
                    tempStack.push(leftStack.pop());
                }
                // If the numbers at the same level are equal, removing them:
                if(leftStack.peek() == rightStack.peek()){
                    removeNumber(0);
                    isMatched = true;
                }
                while(!tempStack.isEmpty()){
                    leftStack.push(tempStack.pop());
                }
            }
            else if(leftStack.size() < rightStack.size()){
                while(leftStack.size() != rightStack.size()){
                    tempStack.push(rightStack.pop());
                }
                // If the numbers at the same level are equal, removing them:
                if(leftStack.peek() == rightStack.peek()){
                    removeNumber(0);
                    isMatched = true;
                }
                while(!tempStack.isEmpty()){
                    rightStack.push(tempStack.pop());
                }
            }
            else{
                // If the numbers at the same level are equal, removing them:
                if(leftStack.peek() == rightStack.peek()){
                    removeNumber(0);
                    isMatched = true;
                }
            }
        }
        return isMatched;
    }

    public void removeNumber(int level){
        // Pushing the elements to the temp stack until reaching to the matched level:
        Stack tempStack = new Stack(8);
        for (int i = 0; i < level; i++) {
            tempStack.push(leftStack.pop());
        }
        // Keeping matched value, calculating the score and addin the score to the total points: 
        int matchedValue = (Integer) leftStack.pop();
        int scoreFactor = 1;
        
        if(matchedValue >= 4 && matchedValue <= 6){
            scoreFactor = 5;
        }
        else if(matchedValue >= 7 && matchedValue <= 9){
            scoreFactor = 25;
        }

        totalPoints += matchedValue * scoreFactor;

        // Pushing the elements back to the left stack:
        while(!tempStack.isEmpty()){
            leftStack.push(tempStack.pop());
        }

        // Pushing the elements to the temp stack until reaching to the matched level:
        for (int i = 0; i < level; i++) {
            tempStack.push(rightStack.pop());
        }
        // Removing the matched number:
        rightStack.pop();

        // Pushing the elements back to the right stack:
        while(!tempStack.isEmpty()){
            rightStack.push(tempStack.pop());
        }
    }
}

