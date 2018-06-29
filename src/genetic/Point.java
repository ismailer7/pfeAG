package genetic;

import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Function;

import java.math.BigDecimal;

public class Point {

    private double x;
    private double y;
    private final double z;

    public Point(String dFunction, double x, double y) {
        this.x = x;
        this.y = y;
        Function function = new Function("At(x,y) = " + dFunction);
        Expression e1 = new Expression("At("+x+","+y+")",function);
        this.z = BigDecimal.valueOf(e1.calculate()).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
