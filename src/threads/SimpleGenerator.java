package threads;

import functions.Function;
import functions.basic.Log;

import java.util.Random;

// Генератор задач для вычисления интеграла логарифмической функции
public class SimpleGenerator implements Runnable {
    private final Task task;
    private final Random random = new Random();  // Генератор случайных чисел

    public SimpleGenerator(Task task) {
        if (task == null)
            throw new IllegalArgumentException("Task не должен быть null");
        this.task = task;
    }

    @Override
    public void run() {
        int tasksCount = task.getTasksCount(); // Копия количества задач
        for (int i = 0; i < tasksCount; ++i) { // Используем локальную переменную
            int taskNumber = i + 1;

            double base = 1 + 9 * random.nextDouble();     // Генерируем основание логарифма в диапазоне (1, 10)
            if (Math.abs(base - 1) < 1e-6)               // Избегаем основания 1
                base += 1e-3;

            Function logFunction = new Log(base);

            // Левая граница в диапазоне (0, 100)
            double left = 100 * random.nextDouble();

            if (left <= 0)
                left = Double.MIN_VALUE; // Гарантируем положительное значение

            // Правая граница в диапазоне (100, 200), обязательно больше левой
            double right = 100 + 100 * random.nextDouble();

            if (right <= left)
                right = left + 1; // Гарантируем что right > left

            // Шаг интегрирования в диапазоне (0, 1]
            double step = random.nextDouble();

            if (step <= 0)
                step = 1; // Минимальный шаг = 1

            // Заполняем общую задачу сгенерированными параметрами
            synchronized (task) {
                task.setFunction(logFunction);
                task.setLeft(left);
                task.setRight(right);
                task.setStep(step);

                // Выводим информацию о сгенерированной задаче
                System.out.printf("[S-GEN] task=%3d base=%.5f left=%.5f right=%.5f step=%.5f%n",
                        taskNumber, base, left, right, step);
            }
        }
    }
}