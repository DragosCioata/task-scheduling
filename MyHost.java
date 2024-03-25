import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class MyHost extends Host {
    private long totalWorkLeft = 0;
    private boolean shouldTerminate = false;
    private final BlockingQueue<Task> taskQueue = new PriorityBlockingQueue<>(11, Comparator.comparingInt(Task::getPriority).reversed());
    private boolean isrunning = false;

    // metoda run care se executa in thread. cat timp nu trebuie sa se termine
    // si thread-ul nu este intrerupt, se preia task-ul cu cea mai mare prioritate
    // se marcheaza ca se executa un task, se simuleaza executia task-ului
    // se marcheaza task-ul ca fiind finalizat, se marcheaza ca nu se mai executa
    // niciun task si se afiseaza id-ul task-ului si timpul de finalizare. daca
    // thread-ul este intrerupt, se iese din metoda.
    @Override
    public void run() {
        while (!shouldTerminate && !Thread.currentThread().isInterrupted()) {
            try {
                Task task = taskQueue.take();
                isrunning = true;
                Thread.sleep(task.getDuration());
                task.finish();
                isrunning = false;
                System.out.println(task.getId() + "," + Math.round(task.getFinish()));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    // metoda aceasta este folosita pentru adaugarea unui task in coada
    // si se adauga totodata durata task-ului la totalul de munca ramasa
    @Override
    public void addTask(Task task) {
        taskQueue.add(task);
        totalWorkLeft += task.getDuration();
    }

    // aici vom obtine dimensiunea cozii de task-uri. daca se executa un task,
    // atunci dimensiunea cozii va fi dimensiunea cozii + 1, altfel, va fi doar
    // dimensiunea cozii
    public synchronized int getQueueSize() {
        if (isrunning) {
            return taskQueue.size() + 1;
        }
        return taskQueue.size();
    }

    // se obtine totalul de munca ramasa
    @Override
    public long getWorkLeft() {
        return totalWorkLeft;
    }

    // metoda pentru oprirea executiei, unde se seteaza ca trebuie sa se termine
    // si se intrerupe thread-ul
    @Override
    public void shutdown() {
        shouldTerminate = true;
        this.interrupt();
    }
}