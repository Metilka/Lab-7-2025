package functions;
import java.util.Iterator;
public interface TabulatedFunction extends Function, Cloneable, Iterable<FunctionPoint> {

    @Override
    Iterator<FunctionPoint> iterator();

    TabulatedFunction clone(); // Создает и возвращает копию табулированной функции
    int getPointsCount();
    FunctionPoint getPoint(int index);
    void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException;
    double getPointX(int index);
    void setPointX(int index, double x) throws InappropriateFunctionPointException;
    double getPointY(int index);
    void setPointY(int index, double y);
    void deletePoint(int index);
    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;

}
