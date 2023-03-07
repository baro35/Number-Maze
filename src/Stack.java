public class Stack {
    private int top;
    private Object[] elements;

    public Stack(int capacity) {
        elements = new Object[capacity];
        top = -1;
    }

    public void push(Object data){
        if(isFull()){
            System.out.println("Stack overflow!");
        }
        else{
            top++;
            elements[top] = data;
        }
    }

    public Object pop(){
        if(isEmpty()){
            System.out.println("Stack is empty!");
            return null;
        }
        else{
            Object returnData = elements[top];
            top--;
            return returnData;
        }
    }

    public Object peek(){
        if(isEmpty()){
            System.out.println("Stack is empty!");
            return null;
        }
        else{
            return elements[top];
        }
    }

    public boolean isFull(){
        if(top + 1 == elements.length) return true;
        else return false;
    }

    public boolean isEmpty(){
        if(top == -1) return true;
        else return false;
    }

    public int size(){
        return top+1;
    }



    
}
