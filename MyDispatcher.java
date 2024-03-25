import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class MyDispatcher extends Dispatcher {
    private AtomicInteger currentHostIndex = new AtomicInteger(0);

    // constructorul clasei MyDispatcher si apelarea clasei de baza
    public MyDispatcher(SchedulingAlgorithm algorithm, List<Host> hosts) {
        super(algorithm, hosts);
    }

    // metoda addTask care adauga taskurile in functie de algoritmul ales
    // pentru roundrobin adaug taskurile pe rand la fiecare host si
    // dupa care actualizam index-ul hostului curent. in cazul celorlalte
    // planificari, se vor apela pe rand metodele corespunzatoare
    // fiecarui algoritm
    @Override
    public void addTask(Task task) {
        if (this.algorithm == SchedulingAlgorithm.ROUND_ROBIN) {
            Host currentHost = hosts.get(currentHostIndex.getAndUpdate(i -> (i + 1) % hosts.size()));
            currentHost.addTask(task);
        } else if (this.algorithm == SchedulingAlgorithm.SHORTEST_QUEUE) {
            scheduleToShortestQueue(task);
        } else if (this.algorithm == SchedulingAlgorithm.SIZE_INTERVAL_TASK_ASSIGNMENT) {
            scheduleToSizeIntervalHost(task);
        } else if (this.algorithm == SchedulingAlgorithm.LEAST_WORK_LEFT) {
            scheduleToLeastWorkLeft(task);
        }
    }

    // metoda care adauga taskurile la hostul cu coada cea mai mica
    // initial se ia primul host si se compara cu restul hosturilor
    // daca gasim un host cu coada mai mica, atunci il retinem
    // si actualizam durata minima a cozii cu durata cozii noului host
    // daca gasim un host cu coada egala, atunci il retinem doar daca
    // indexul lui este mai mic decat cel al hostului cu coada minima
    // in cazul in care nu gasim un host cu coada mai mica, atunci
    // adaugam taskul la hostul cu coada minima.
    private synchronized void scheduleToShortestQueue(Task task) {
        Host shortestQueueHost = hosts.get(0);
        for (Host host : hosts) {
            if (host.getQueueSize() < shortestQueueHost.getQueueSize()) {
                shortestQueueHost = host;
            }
        }
        shortestQueueHost.addTask(task);
    }

    // metoda atribuie un task unui host in functie de tipul sau
    // taskurile sunt impartite in 3 categorii: SHORT, MEDIUM si LONG.
    // verificam care dintre acestea este pentru a determina tipul taskului
    // si a alege hostul corespunzator in functie de tipul respectiv.
    private void scheduleToSizeIntervalHost(Task task) {
        Host assignedHost;

        if (task.getType() == TaskType.SHORT) {
            assignedHost = hosts.get(0);
        } else if (task.getType() == TaskType.MEDIUM) {
            assignedHost = hosts.get(1);
        } else if (task.getType() == TaskType.LONG) {
            assignedHost = hosts.get(2);
        } else {
            assignedHost = hosts.get(0);
        }

        assignedHost.addTask(task);
    }

    // metoda atribuie un task hostului cu cea mai mica cantitate
    // de lucru ramas. prin for parcurgem lista de hosturi dupa care
    // comparam cantitatea de lucru ramas a fiecarui host cu cantitatea
    // minima de lucru ramas. daca gasim unul, atunci il retinem si actualizam
    // cantitatea minima de lucru
    private void scheduleToLeastWorkLeft(Task task) {
        int leastWorkLeftIndex = 0;
        long leastWorkLeftDuration = hosts.get(0).getWorkLeft();

        for (int i = 1; i < hosts.size(); i++) {
            long hostWorkLeftDuration = hosts.get(i).getWorkLeft();
            if (hostWorkLeftDuration < leastWorkLeftDuration ||
                    (hostWorkLeftDuration == leastWorkLeftDuration && i < leastWorkLeftIndex)) {
                leastWorkLeftIndex = i;
                leastWorkLeftDuration = hostWorkLeftDuration;
            }
        }

        hosts.get(leastWorkLeftIndex).addTask(task);
    }


}