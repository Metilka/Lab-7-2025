package functions;

public class ArrayTabulatedFunction implements TabulatedFunction, java.io.Serializable, Cloneable {
    private static final long serialVersionUID = 1L;

    private int size;                 // текущее количество точек
    private FunctionPoint[] points;   // упорядоченный по x массив точек

    private static final double EPSILON = 1e-9;

    private static boolean eq(double a, double b) {
        return Math.abs(a - b) <= EPSILON;
    }

    private static boolean lt(double a, double b) {
        return a < b - EPSILON;
    }

    private static boolean gt(double a, double b) {
        return a > b + EPSILON;
    }

    private static boolean le(double a, double b) {
        return a <= b + EPSILON;
    }

    private static boolean ge(double a, double b) {
        return a >= b - EPSILON;
    }

    // Проверка индекса на валидность
    // Исключение, если индекс вне [0, size - 1]
    private void requireIndex(int index) {
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException("index=" + index);
        }
    }

    // Конструктор получающий все точки сразу, в виде массива
    public ArrayTabulatedFunction(FunctionPoint[] arr) {
        if (arr == null || arr.length < 2) // если точек < 2
            throw new IllegalArgumentException("At least 2 points required");
        // Проверка строгого порядка по x и отсутствия дублирования x
        for (int i = 1; i < arr.length; ++i) {
            double prevX = arr[i - 1].getX();
            double curX = arr[i].getX();
            if (!lt(prevX, curX)) {
                // Если curX == prevX или curX < prevX бросаем исключение
                throw new IllegalArgumentException("Points must be strictly increasing by x");
            }
        }
        // Копируем точки во внутренний массив
        this.size = arr.length;
        this.points = new FunctionPoint[Math.max(size, 2)];
        for (int i = 0; i < size; ++i) {
            this.points[i] = new FunctionPoint(arr[i]); // копия
        }
    }

    // Конструктор [leftX; rightX], pointsCount точек, все y=0
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (rightX <= leftX) {
            throw new IllegalArgumentException("leftX must be < rightX");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("pointsCount must be >= 2");
        }
        this.size = pointsCount;
        this.points = new FunctionPoint[Math.max(pointsCount, 2)];

        // Равномерный шаг по x
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; ++i) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, 0.0);
        }
    }

    // Конструктор: [leftX; rightX], значения y — из массива
    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (rightX <= leftX) {
            throw new IllegalArgumentException("leftX must be < rightX");
        }
        if (values == null || values.length < 2) {
            throw new IllegalArgumentException("values length must be >= 2");
        }
        this.size = values.length;
        this.points = new FunctionPoint[Math.max(size, 2)];

        double step = (rightX - leftX) / (size - 1);
        for (int i = 0; i < size; ++i) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, values[i]);
        }
    }

    // Методы интерфейса TabulatedFunction

    // Количество точек в таблице
    @Override
    public int getPointsCount() {
        return size;
    }

    // Левая граница области определения. Если таблица пуста, вернется NaN
    @Override
    public double getLeftDomainBorder() {
        return (size == 0) ? Double.NaN : points[0].getX();
    }

    // Правая граница области определения. Если таблица пуста, вернется NaN
    @Override
    public double getRightDomainBorder() {
        return (size == 0) ? Double.NaN : points[size - 1].getX();
    }

    // Значение функции в точке x (с эпсилоном только для алгоритма)
    @Override
    public double getFunctionValue(double x) {
        if (size == 0) return Double.NaN;
        double left = points[0].getX();
        double right = points[size - 1].getX();
        if (lt(x, left) || gt(x, right)) return Double.NaN;

        // совпадение по x
        for (int i = 0; i < size; ++i) {
            if (eq(x, points[i].getX())) return points[i].getY();
        }
        // интервал [xi, xi+1]
        for (int i = 0; i < size - 1; ++i) {
            double x0 = points[i].getX(), y0 = points[i].getY();
            double x1 = points[i + 1].getX(), y1 = points[i + 1].getY();
            if (ge(x, x0) && le(x, x1)) {
                return y0 + (y1 - y0) * (x - x0) / (x1 - x0);
            }
        }
        return Double.NaN;
    }

    // Возвращает копию точки по индексу
    @Override
    public FunctionPoint getPoint(int index) {
        requireIndex(index);
        return new FunctionPoint(points[index]);
    }

    // Заменяет точку по индексу на переданную (сохранение строгого порядка по x)
    @Override
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        if (point == null) return;
        requireIndex(index);
        double newX = point.getX();
        if (index > 0) {
            double leftX = points[index - 1].getX();
            if (!gt(newX, leftX)) throw new InappropriateFunctionPointException("x must be > left neighbor");
        }
        if (index < size - 1) {
            double rightX = points[index + 1].getX();
            if (!lt(newX, rightX)) throw new InappropriateFunctionPointException("x must be < right neighbor");
        }
        points[index] = new FunctionPoint(point); // копия (инкапсуляция)
    }

    // Абсцисса x точки по индексу
    @Override
    public double getPointX(int index) {
        requireIndex(index);
        return points[index].getX();
    }

    // Меняет x точки по индексу. Порядок по x должен сохраниться
    @Override
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        requireIndex(index);
        if (size == 1) {
            points[0].setX(x);
            return;
        }
        if (index == 0) {
            double nextX = points[1].getX();
            if (!lt(x, nextX)) throw new InappropriateFunctionPointException("x must be < right neighbor");
            points[0].setX(x);
            return;
        }
        if (index == size - 1) {
            double prevX = points[size - 2].getX();
            if (!gt(x, prevX)) throw new InappropriateFunctionPointException("x must be > left neighbor");
            points[size - 1].setX(x);
            return;
        }
        double leftX = points[index - 1].getX();
        double rightX = points[index + 1].getX();
        if (!(gt(x, leftX) && lt(x, rightX))) {
            throw new InappropriateFunctionPointException("x must be strictly between neighbors");
        }
        points[index].setX(x);
    }

    // Ордината y точки по индексу
    @Override
    public double getPointY(int index) {
        requireIndex(index);
        return points[index].getY();
    }

    // Меняет y точки по индексу. Порядок по x не затрагивается
    @Override
    public void setPointY(int index, double y) {
        requireIndex(index);
        points[index].setY(y);
    }

    // Удаляет точку по индексу со сдвигом хвоста влево. Минимум 3 точки.
    @Override
    public void deletePoint(int index) {
        requireIndex(index);
        if (size < 3) throw new IllegalStateException("cannot delete when points count < 3");
        System.arraycopy(points, index + 1, points, index, size - index - 1);
        points[--size] = null;
    }

    // Добавляет новую точку и сохраняет порядок по x (эпсилон — только для поиска позиции)
    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        if (point == null) return;
        double x = point.getX();
        int insertIndex = 0;
        while (insertIndex < size && lt(points[insertIndex].getX(), x)) {
            insertIndex++;
        }
        if (insertIndex < size && eq(points[insertIndex].getX(), x)) {
            throw new InappropriateFunctionPointException("duplicate x");
        }

        if (size >= points.length) {
            int newCapacity = (points.length == 0) ? 2 : (points.length * 3 / 2 + 1);
            FunctionPoint[] newArray = new FunctionPoint[newCapacity];
            if (size > 0) System.arraycopy(points, 0, newArray, 0, size);
            points = newArray;
        }
        // Сдвигаем хвост вправо, чтобы освободить позицию insertIndex
        if (insertIndex < size) {
            System.arraycopy(points, insertIndex, points, insertIndex + 1, size - insertIndex);
        }
        // Вставляем копию точки
        points[insertIndex] = new FunctionPoint(point); // копия
        size++;
    }

    //  ЛР5:

    @Override
    public String toString() { // человекочитаемый вывод в формате из задания
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        for (int i = 0; i < size; ++i) {
            if (i > 0) sb.append(", ");
            FunctionPoint p = points[i];
            sb.append('(').append(p.getX()).append("; ").append(p.getY()).append(')');
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) { // Равенство, если длина и все (x,y) совпадают
        if (this == o) return true;
        // Быстрый путь
        if (o instanceof ArrayTabulatedFunction other) {
            // Если разное количество точек - функции не равны
            if (this.size != other.size) return false;
            for (int i = 0; i < this.size; ++i) {
                // Используем встроенный метод equals точек, который учитывает погрешность EPSILON
                if (!this.points[i].equals(other.points[i])) return false;
            }
            return true;
        }
        // Универсальный путь: сравнение с любой реализацией TabulatedFunction.
        if (o instanceof TabulatedFunction tf) {
            if (this.size != tf.getPointsCount()) return false; // Проверяем совпадение количества точек
            // Сравниваем точки через интерфейсные методы
            for (int i = 0; i < this.size; ++i) {
                FunctionPoint otherPoint = new FunctionPoint(tf.getPointX(i), tf.getPointY(i));
                if (!this.points[i].equals(otherPoint)) return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int h = size; // учитываем размер, чтобы {(-1,1),(0,0),(1,1)} != {(-1,1),(1,1)}
        for (int i = 0; i < size; ++i) {
            int ph = points[i].hashCode();                 // берём хеш точки
            h ^= Integer.rotateLeft(ph, (i & 15));
        }
        return h;
    }

    @Override
    public ArrayTabulatedFunction clone() { // клонирование - новый массив и новые точки
        FunctionPoint[] copy = new FunctionPoint[this.size];
        for (int i = 0; i < this.size; ++i) {
            FunctionPoint p = this.points[i];
            copy[i] = new FunctionPoint(p.getX(), p.getY()); // независимый объект точки
        }
        return new ArrayTabulatedFunction(copy); // используем конструктор из массива точек
    }
        //итератор
        @Override
    public java.util.Iterator<FunctionPoint> iterator() {
        return new java.util.Iterator<FunctionPoint>() {
            private int index = 0;
            @Override
            public boolean hasNext() {
                return index < size;
            }
            @Override
            public FunctionPoint next() {
                if (!hasNext())
                    throw new java.util.NoSuchElementException("Все элементы массива уже обработаны");

                FunctionPoint internal = points[index++];
                return new FunctionPoint(internal);
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException("Метод remove() не реализован для данного итератора");
            }
        };
    }
    // фабрика
    public static class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory {

    @Override
    public TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount) {
        return new ArrayTabulatedFunction(leftX, rightX, pointsCount);
    }

    @Override
    public TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values) {
        return new ArrayTabulatedFunction(leftX, rightX, values);
    }

    @Override
    public TabulatedFunction createTabulatedFunction(FunctionPoint[] points) {
        return new ArrayTabulatedFunction(points);
    }
}


}
