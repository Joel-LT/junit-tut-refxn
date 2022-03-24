package calculator;

public class Add {
    private int x,y;
    Add(int x, int y){
        this.x = x;
        this.y = y;
    }
    public int add(Add values){
        return values.x + values.y;
    }
}
