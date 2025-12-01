package threads;
import functions.Function;
import functions.Functions;

// Вычислитель интеграла
public class SimpleIntegrator implements Runnable {
    private final Task task;

    public SimpleIntegrator(Task task) {
        if (task == null)
            throw new IllegalArgumentException("Task не должен быть null");
        this.task = task;
    }
    @Override
    public void run() {
        int tasksCount = task.getTasksCount(); // копия количества задач
        for (int i = 0; i < tasksCount; ++i) { // Выполняем вычисления для указанного количества задач
            int taskNumber = i + 1;

            Function f;
            double left;
            double right;
            double step;

            synchronized (task) {
                f = task.getFunction();
                left = task.getLeft();
                right = task.getRight();
                step = task.getStep();
            }
            // Простая защита от NullPointerException
            if (f == null) {
                System.out.printf("[S-INT] task=%3d function=null, интегрирование пропущено%n", taskNumber);
                continue;
            }
            // Вычисляем интеграл с полученными параметрами
            double value = Functions.integral(f, left, right, step);

            // Вывод результата в одну строку
            System.out.printf(
                    "[S-INT] task=%3d left=%.5f right=%.5f step=%.5f value=%.10f%n",
                    taskNumber, left, right, step, value
            );
        }
    }
}