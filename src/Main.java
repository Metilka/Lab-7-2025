import functions.*;
import functions.basic.*;

public class Main {
    public static void main(String[] args) {

        // Проверка итераторов
        System.out.println("1. Проверка итераторов:");
        System.out.println("ArrayTabulatedFunction:");
        TabulatedFunction func1 = new ArrayTabulatedFunction(0, 5, 3);
        for (FunctionPoint p : func1) {
            System.out.println(p);
        }

        System.out.println("LinkedListTabulatedFunction:");
        TabulatedFunction func2 = new LinkedListTabulatedFunction(
            new FunctionPoint[]{new FunctionPoint(1, 1), new FunctionPoint(2, 4), new FunctionPoint(3, 9)});
        for (FunctionPoint p : func2) {
            System.out.println(p);
        }

        // Проверка фабрик
        System.out.println("\n2. Проверка фабрик:");
        TabulatedFunction tf;

        // Сначала фабрика по умолчанию (должна быть Array)
        tf = TabulatedFunctions.tabulate(new Sin(), 0, Math.PI, 4);
        System.out.println("По умолчанию: " + tf.getClass().getSimpleName());

        // Меняем на LinkedList
        TabulatedFunctions.setTabulatedFunctionFactory(
            new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(new Sin(), 0, Math.PI, 4);
        System.out.println("После смены: " + tf.getClass().getSimpleName());

        // Обратно на Array
        TabulatedFunctions.setTabulatedFunctionFactory(
            new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(new Sin(), 0, Math.PI, 4);
        System.out.println("Обратно: " + tf.getClass().getSimpleName());

        // Проверка рефлексии
        System.out.println("\n3. Проверка рефлексии:");
        TabulatedFunction rf;

        rf = TabulatedFunctions.createTabulatedFunction(
            ArrayTabulatedFunction.class, 0, 10, 4);
        System.out.println("Array через рефлексию: " + rf.getClass().getSimpleName());

        rf = TabulatedFunctions.createTabulatedFunction(
            LinkedListTabulatedFunction.class,
            new FunctionPoint[]{new FunctionPoint(0,0), new FunctionPoint(1,1)});
        System.out.println("LinkedList через рефлексию: " + rf.getClass().getSimpleName());

        rf = TabulatedFunctions.tabulate(
            LinkedListTabulatedFunction.class, new Cos(), 0, Math.PI, 5);
        System.out.println("Табулирование через рефлексию: " + rf.getClass().getSimpleName());

        // Проверка ошибок
        System.out.println("\n4. Проверка ошибок:");
        try {
            TabulatedFunctions.createTabulatedFunction(
                ArrayTabulatedFunction.class, 10, 0, 3);
        } catch (Exception e) {
            System.out.println("Поймали ошибку: " + e.getMessage());
        }

    }
}