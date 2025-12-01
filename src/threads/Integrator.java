package threads;

import functions.Function;
import functions.Functions;

import java.util.concurrent.Semaphore;

// Вычислитель интеграла с синхронизацией через семафоры
public class Integrator extends Thread {
    private final Task task;              // Общая задача для получения данных
    private final Semaphore dataReady;    // Семафор "данные готовы" (отпускается генератором)
    private final Semaphore dataProcessed; // Семафор "данные обработаны" (отпускается интегратором)

    public Integrator(Task task, Semaphore dataReady, Semaphore dataProcessed) {
        super("Integrator");
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
                    System.out.println("Integrator: обнаружен флаг прерывания, выходим из цикла");
                    break;
                }
                int taskNumber = i + 1;
                // Ожидаем пока генератор подготовит данные
                dataReady.acquire();
                double left;
                double right;
                double step;
                Function function;
                try {
                    // Читаем параметры из общей задачи
                    function = task.getFunction();
                    left = task.getLeft();
                    right = task.getRight();
                    step = task.getStep();
                } finally { // Уведомляем генератор о том что данные обработаны
                    dataProcessed.release();
                }
                // Вычисляем интеграл
                double result = Functions.integral(function, left, right, step);
                // Выводим результат вычислений
                System.out.printf("[INT] task=%3d left=%.6f right=%.6f step=%.6f value=%.10f%n",
                        taskNumber, left, right, step, result);
            }
        } catch (InterruptedException e) {
            System.out.println("Integrator: прерван при ожидании семафора");
            Thread.currentThread().interrupt();
        }
        System.out.println("Integrator: завершение работы потока");
    }
}