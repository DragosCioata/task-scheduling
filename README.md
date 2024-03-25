Cioata Dragos Robert 335CC

## Tema 2 - APD

In aceasta tema am implementat un sistem de planificare a 
taskurilor folosind threaduri. Asadar, in clasa MyHost am implementat 
un host in care se regaseste logica de preluare si gestionare a task urilor, 
folosind BlockingQueue pentru o sincronizare corecta. M-am folosit de o coada 
pentru a retine taskurile. Metodele specifice sunt descrise  prin comentarii 
in cod. In clasa MyDispatcher am implementat cele 4  politici de planificare
RoundRobin, ShortestQueue,  Size Interval Task Assignment si 
Least Work Left. Prima politica de planificare este cea mai simpla, 
alocandu-se task uri nodului (i+1)%n asa cum este specificat si in enunt.
Am realizat acest lucru direct in addTask, iar pentru celelalte 3 poltiici 
am creat cate o metoda specifica, fiecare fiind explicata in comentarii.
Am reusit implementarea cu prioritati doar pentru politica RR, SITA si SQ,
nu si pentru LWL. Pentru aceasta politica am implementat doar varianta simpla.
Tema este predata cu o intarziere de 2 zile (deci 20% depunctare).