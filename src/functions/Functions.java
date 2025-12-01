package functions;

import functions.meta.*;

public final class Functions {
    private Functions() {
        // Запрещаем создание экземпляров
        throw new AssertionError("No instances");
    }

    public static Function shift(Function f, double shiftX, double shiftY) {
        return new Shift(f, shiftX, shiftY);
    }

    public static Function scale(Function f, double scaleX, double scaleY) {
        return new Scale(f, scaleX, scaleY);
    }

    public static Function power(Function f, double power) {
        return new Power(f, power);
    }

    public static Function sum(Function f1, Function f2) {
        return new Sum(f1, f2);
    }

    public static Function mult(Function f1, Function f2) {
        return new Mult(f1, f2);
    }

    public static Function composition(Function outer, Function inner) {
        return new Composition(outer, inner);
    }

    public static double integral(Function f, double left, double right, double step) {
        // Проверка входных данных
        if (f == null)
            throw new IllegalArgumentException("Функция не должна быть null");

        if (Double.isNaN(left) || Double.isNaN(right) || Double.isNaN(step))
            throw new IllegalArgumentException("Границы и шаг интегрирования не должны быть NaN");

        if (!Double.isFinite(left) || !Double.isFinite(right) || !Double.isFinite(step))
            throw new IllegalArgumentException("Границы и шаг интегрирования должны быть конечными числами");

        if (step <= 0.0)
            throw new IllegalArgumentException("Шаг интегрирования должен быть > 0. Получено: " + step);

        if (left >= right)
            throw new IllegalArgumentException("Левая граница должна быть меньше правой. left = " + left + ", right = " + right);

        // Получаем границы области определения функции
        double domainLeft = f.getLeftDomainBorder();
        double domainRight = f.getRightDomainBorder();

        // Проверяем что запрашиваемый интервал полностью находится в области определения
        if (left < domainLeft || right > domainRight) {
            throw new IllegalArgumentException(
                    "Интервал [" + left + "; " + right + "] выходит за область определения функции: [" +
                            domainLeft + "; " + domainRight + "]");
        }
        double result = 0;
        double x = left;      // Начинаем с левой границы интервала

        while (x < right) {  // Проходим по всему интервалу от left до right с шагом step
            double next = x + step;
            if (next > right)
                next = right;  // На последнем отрезке берем правую границу
            // Вычисляем значения функции в текущей и следующей точках
            double fx = f.getFunctionValue(x);
            double fNext = f.getFunctionValue(next);
            double h = next - x;  // Фактическая длина текущего отрезка
            // Добавляем площадь текущей трапеции к результату
            result += (fx + fNext) * h / 2.0;
            x = next;
        } // Переходим к следующему отрезку
        return result;  // Возвращаем вычисленное значение интеграла
    }
}