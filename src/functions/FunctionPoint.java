package functions;

import java.io.Serializable;

public class FunctionPoint implements Serializable, Cloneable { // делаем точку клонируемой
    private static final long serialVersionUID = 1L;
    private static final double EPSILON = 1e-9;

    private double x;
    private double y;

    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public FunctionPoint(FunctionPoint other) {
        this(other.x, other.y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() { // человекочитаемый вывод
        // Требуемый формат: (x; y)
        return "(" + x + "; " + y + ")";
    }

    @Override
    public boolean equals(Object o) { // сравниваем точки по координатно
        if (this == o) return true;
        if (o == null || o.getClass() != FunctionPoint.class) return false;
        FunctionPoint that = (FunctionPoint) o;
        // сравнение чисел типа double
        return Math.abs(this.x - that.x) <= EPSILON
                && Math.abs(this.y - that.y) <= EPSILON;
    }

    @Override
    public int hashCode() { // хэш из битов double через XOR
        // округляем до ближайшего кратного EPSILON
        double qx = Math.rint(x / EPSILON) * EPSILON;
        double qy = Math.rint(y / EPSILON) * EPSILON;
        // Нормализуем нули, чтобы +0.0 и -0.0 давали одинаковый хеш
        if (qx == 0.0) qx = 0.0;
        if (qy == 0.0) qy = 0.0;
             // Преобразуем double в long для получения точного битового представления
        long xb = Double.doubleToLongBits(qx);
        long yb = Double.doubleToLongBits(qy);
            // Разбиваем 64-битные значения на две 32-битные части
        int xLow = (int) (xb);
        int xHigh = (int) (xb >>> 32);
        int yLow = (int) (yb);
        int yHigh = (int) (yb >>> 32);
        int h = 31;
        h ^= xLow ^ xHigh;
        h = Integer.rotateLeft(h, 5);
        h ^= yLow ^ yHigh;
        return h;
    }

    @Override   // функция клонирования точки
    public FunctionPoint clone() { return new FunctionPoint(this.x, this.y);}
}
