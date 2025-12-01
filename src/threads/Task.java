package threads;
import functions.Function;
// Класс вычисления интеграла функции
public class Task {
    private Function function;  // Функция для интегрирования
    private double left;        // Левая граница интервала
    private double right;       // Правая граница интервала
    private double step;        // Шаг интегрирования
    private int tasksCount;     // Количество подзадач

    public Task() {}// Конструктор по умолчанию
    // Конструктор со всеми параметрами
    public Task(Function function, double left, double right, double step, int tasksCount) {
        this.function = function;
        this.left = left;
        this.right = right;
        this.step = step;
        this.tasksCount = tasksCount;
    }
    // Геттеры и сеттеры
    public Function getFunction() { return function; }
    public void setFunction(Function function) { this.function = function; }

    public double getLeft() { return left; }
    public void setLeft(double left) { this.left = left; }
    public double getRight() { return right; }
    public void setRight(double right) { this.right = right; }

    public double getStep() { return step; }
    public void setStep(double step) { this.step = step; }

    public int getTasksCount() { return tasksCount; }

    // Сеттер с проверкой валидности
    public void setTasksCount(int tasksCount) {
        if (tasksCount <= 0)
            throw new IllegalArgumentException("Количество заданий должно быть > 0: " + tasksCount);
        this.tasksCount = tasksCount;
    }

    // Вывод для отладки
    @Override
    public String toString() {
        return "Task{function=" + function + ", left=" + left +
               ", right=" + right + ", step=" + step +
               ", tasksCount=" + tasksCount + '}';
    }
}