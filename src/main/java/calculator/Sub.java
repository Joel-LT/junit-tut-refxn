package calculator;

public class Sub {
    private int x,y;
    Sub(int x, int y){
        this.x = x;
        this.y = y;
    }
    public int sub(Sub values){
        return values.x - values.y;
    }
}
