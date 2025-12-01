package threads;

import functions.Function;
import functions.basic.Log;

import java.util.Random;
import java.util.concurrent.Semaphore;

// Генератор задач с синхронизацией через семафоры
public class Generator extends Thread {
    private final Task task;              // Общая задача для передачи данных
    private final Semaphore dataReady;    // Семафор "данные готовы" (отпускается генератором)
    private final Semaphore dataProcessed; // Семафор "данные обработаны" (отпускается интегратором)
    private final Random random = new Random();

    public Generator(Task task, Semaphore dataReady, Semaphore dataProcessed) {
        super("Generator");
        if (task == null)
            throw new IllegalArgumentException("Task не должен быть null");
        if (dataReady == null || dataProcessed == null)
            throw new IllegalArgumentException("Семафоры не должны быть null");
        this.task = task;
        this.dataReady = dataReady;
        this.dataProcessed = dataProcessed;
    }

    @Override
    public void run() {
        int tasksCount = task.getTasksCount();
        try {
            for (int i = 0; i < tasksCount; ++i) {
                // Проверка прерывания потока
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Generator: обнаружен флаг прерывания, выходим из цикла");
                    break;
                }
                int taskNumber = i + 1;
                // Генерация параметров логарифмической функции
                double base = 1.0 + 9.0 * random.nextDouble();
                if (Math.abs(base - 1.0) < 1e-6)
                    base += 1e-3; // Избегаем основания 1
                Function logFunction = new Log(base);
                // Генерация левой границы
                double left = 100.0 * random.nextDouble();
                if (left <= 0.0)
                    left = Double.MIN_VALUE;
                // Генерация правой границы
                double right = 100.0 + 100.0 * random.nextDouble();
                if (right <= left)
                    right = left + 1.0;
                // Генерация шага интегрирования
                double step = random.nextDouble();
                if (step <= 0.0)
                    step = 1.0;
                dataProcessed.acquire();  // Ожидание разрешения от интегратора
                try { // Запись сгенерированных параметров в общую задачу
                    task.setFunction(logFunction);
                    task.setLeft(left);
                    task.setRight(right);
                    task.setStep(step);
                    System.out.printf( // Вывод информации
                            "[GEN] task=%3d base=%.6f left=%.6f right=%.6f step=%.6f%n",
                            taskNumber, left, right, step, base);
                } finally {
                    dataReady.release();
                } // Уведомление интегратора о готовности данных
            }
        } catch (InterruptedException e) {
            System.out.println("Generator: прерван при ожидании семафора");
            Thread.currentThread().interrupt();
        }
        System.out.println("Generator: завершение работы потока");
    }
}