package it.unicam.cs.asdl2122.mp1;

import java.util.*;

// TODO inserire import della Java SE che si ritengono necessari

/**
 * // TODO spiegare come viene implementato il multiset.
 *
 * @param <E> il tipo degli elementi del multiset
 * @author Luca Tesei (template) **INSERIRE NOME, COGNOME ED EMAIL
 * xxxx@studenti.unicam.it DELLO STUDENTE** (implementazione)
 */
public class MyMultiset<E> implements Multiset<E> {

    //TODO inserire le variabili istanza private che si ritengono necessarie
    private int size;
    private HashSet<Nodo<E>> insieme;
    private int numeroModifiche;

    // TODO inserire le classi interne che si ritengono necessarie
    private static class Nodo<E> {
        private int occorrenze;
        private E oggetto;

        Nodo(E oggetto, int occorrenze) {
            this.occorrenze = occorrenze;
            this.oggetto = oggetto;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Nodo)) return false;
            Nodo<?> nodo = (Nodo<?>) o;
            return ((this.occorrenze == nodo.occorrenze) && (this.oggetto.equals(nodo.oggetto)));
        }

        @Override
        public int hashCode() {
            int hash = 31 * 17 + this.occorrenze;
            hash = 31 * hash + this.oggetto.hashCode();
            return hash;
        }
    }

    private class Itr implements Iterator<E> {
        private Nodo<E> nodoRestituito;
        private int numeroModificheAtteso;
        private int indiceOccorrenze;
        private Iterator<Nodo<E>> iteratore;

        private Itr() {
            nodoRestituito = null;
            numeroModificheAtteso = numeroModifiche;
            iteratore = insieme.iterator();
        }

        @Override
        public boolean hasNext() {
            //Alla prima chiamata dell'iteratore uso hasNext() del Hashset
            if (nodoRestituito == null) return iteratore.hasNext();

            //Se ho ancora delle occorrenze allora hasNext() deve restituire true
            if (indiceOccorrenze > 0) {
                return true;
            }
            //Quando finiscono le occorrenze dello stesso oggetto allora uso l'hasNext() del Hashset
            return iteratore.hasNext();
        }

        @Override
        public E next() {
            if (numeroModificheAtteso != numeroModifiche) throw new ConcurrentModificationException("C'è stata una " +
                    "modifica");

            //Qualora il nodo restituito dal next avesse altre occorrenze, restituisco lo stesso oggetto e scalo un'
            //occorrenza
            if (nodoRestituito != null && indiceOccorrenze > 0) {
                indiceOccorrenze--;
                return nodoRestituito.oggetto;
            }
            //A questo punto mi trovo alla prima chiamata del next o quando finiscono le occorrenze di un oggetto
            nodoRestituito = iteratore.next();
            //Restituisco un'occorrenza e quindi tolgo uno al mio indice delle occorrenze
            indiceOccorrenze = nodoRestituito.occorrenze - 1;
            return nodoRestituito.oggetto;
        }
    }

    /**
     * Crea un multiset vuoto.
     */
    public MyMultiset() {
        insieme = new HashSet<>();
        size = 0;
        numeroModifiche = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int count(Object element) {
        if (element == null) throw new NullPointerException("L'elemento passato al count è null");
        Iterator<Nodo<E>> iterator = insieme.iterator();
        while (iterator.hasNext()) {
            Nodo<E> nodo = iterator.next();
            //Se trovo l'elemento restituisco le sue occorrenze
            if (element.equals(nodo.oggetto)) return nodo.occorrenze;
        }
        //Se non trovo l'elemento allora le sue occorrenze sono 0
        return 0;
    }

    @Override
    public int add(E element, int occurrences) {
        if (element == null) throw new NullPointerException("L'elemento da aggiungere è null");
        if (occurrences < 0) throw new IllegalArgumentException("Non si possono aggiungere valori negativi");

        Iterator<Nodo<E>> iterator = insieme.iterator();

        while (iterator.hasNext()) {
            Nodo<E> nodo = iterator.next();

            if (element.equals(nodo.oggetto)) {
                //Se le occorrenze da aggiungere sono 0, restituisco le occorrenze dell'oggetto e non apporto
                //modifiche
                if (occurrences == 0) return nodo.occorrenze;
                //Controllo se le occorrenze del nodo sommate alle nuove occorrenze superano Integer.MAX_VALUE
                //per fare questo casto momentaneamente le occorrenze del nodo in un long
                if ((((long) nodo.occorrenze) + occurrences) > Integer.MAX_VALUE)
                    throw new IllegalArgumentException("Questa " +
                            "operazione aggiungerebbe un numero maggiore di Integer.MAX_VALUE");

                //Aggiungo le modifiche, aumento la size e modifico le occorrenze
                numeroModifiche++;
                size += occurrences;
                nodo.occorrenze += occurrences;
                //Al fine di usare il contains devo rimuovere e riaggiungere un nodo con le occorrenze aggiornate poiché
                //se modificassi solo le occorrenze cambierebbe l'hashcode del nodo ed invaliderei l'hash usato dal hashset
                iterator.remove();
                insieme.add(nodo);
                //restituisco le occorrenze prima della modifica
                return nodo.occorrenze - occurrences;
            }
        }
        //Non avendo trovato il nodo allora ne creo uno nuovo, aumento size e numero modifiche poi restituisco 0 perchè
        //non era presente l'elemento
        insieme.add(new Nodo<E>(element, occurrences));
        numeroModifiche++;
        size += occurrences;
        return 0;
    }

    @Override
    public void add(E element) {
        if (element == null) throw new NullPointerException("L'elemento da aggiungere è null");

        Iterator<Nodo<E>> iterator = insieme.iterator();

        while (iterator.hasNext()) {
            Nodo<E> nodo = iterator.next();
            if (element.equals(nodo.oggetto)) {

                //Controllo se le occorrenze + 1 superano Integer.MAX_VALUE, per farlo casto le occorrenze in un long
                if (((long) nodo.occorrenze + 1) > Integer.MAX_VALUE) throw new IllegalArgumentException("Questa " +
                        "operazione aggiungerebbe un numero maggiore di Integer.MAX_VALUE");
                //Modifico le occorrenze dell'oggetto
                nodo.occorrenze++;
                //Al fine di usare il contains devo rimuovere e riaggiungere un nodo con le occorrenze aggiornate poiché
                //se modificassi solo le occorrenze cambierebbe l'hashcode del nodo ed invaliderei l'hash usato dal hashset
                iterator.remove();
                insieme.add(nodo);
                numeroModifiche++;
                size++;
                return;
            }
        }
        numeroModifiche++;
        size++;
        insieme.add(new Nodo<E>(element, 1));
    }

    @Override
    public int remove(Object element, int occurrences) {
        if (element == null) throw new NullPointerException("Elemento da rimuovere null");
        if (occurrences < 0) throw new IllegalArgumentException("Occorrenze da rimuovere negative");

        Iterator<Nodo<E>> iterator = insieme.iterator();

        while (iterator.hasNext()) {
            Nodo<E> nodo = iterator.next();
            if (element.equals(nodo.oggetto)) {
                //Se le occorrenze da rimuovere sono 0 allora fermo il metodo e restituisco le occorrenze correnti
                if (occurrences == 0) return nodo.occorrenze;
                //Se le occorrenze dell'oggetto sono maggiori di quelle da rimuovere, riduco le occorrenze.
                if (nodo.occorrenze > occurrences) {
                    nodo.occorrenze -= occurrences;
                    iterator.remove();
                    insieme.add(nodo);
                    numeroModifiche++;
                    size -= occurrences;
                    return nodo.occorrenze + occurrences;
                }
                //Altrimenti salvo le occorrenze e rimuovo il nodo
                int tempOccorrenze = nodo.occorrenze;
                insieme.remove(nodo);
                numeroModifiche++;
                size -= occurrences;
                return tempOccorrenze;
            }
        }
        return 0;
    }

    @Override
    public boolean remove(Object element) {
        if (element == null) throw new NullPointerException("Elemento da rimuovere null");

        Iterator<Nodo<E>> iterator = insieme.iterator();
        Nodo<E> temp;

        while (iterator.hasNext()) {
            temp = iterator.next();
            if (element.equals(temp.oggetto)) {
                //Se ha una sola occorrenza rimuovo il nodo
                if (temp.occorrenze == 1) {
                    numeroModifiche++;
                    size--;
                    return insieme.remove(temp);
                }
                temp.occorrenze--;
                numeroModifiche++;
                size--;
                return true;
            }
        }
        return false;

    }

    @Override
    public int setCount(E element, int count) {
        if (element == null) throw new NullPointerException("Elemento da modificare null");
        if (count < 0) throw new IllegalArgumentException("Numero di occorreze da rimuovere negativo");

        Iterator<Nodo<E>> iterator = insieme.iterator();
        Nodo<E> temp;
        int tempOccorrenze;
        while (iterator.hasNext()) {
            temp = iterator.next();

            if (element.equals(temp.oggetto)) {
                tempOccorrenze = temp.occorrenze;
                //Se count corrisponde alle occorrenze attuali allora non faccio nessuna modifica
                if (count == tempOccorrenze) return tempOccorrenze;

                //Sono sicuro di dover fare delle modifiche
                numeroModifiche++;
                //Se count è 0 rimuovo l'oggetto
                if (count == 0) {
                    //Tolgo le occorrenze di element dalla dimensione e le restituisco
                    iterator.remove();
                    size -= tempOccorrenze;
                    return tempOccorrenze;
                }
                //Riscrivo le occorrenze
                temp.occorrenze = count;
                //Al fine di usare il contains devo rimuovere e riaggiungere un nodo con le occorrenze aggiornate poiché
                //se modificassi solo le occorrenze cambierebbe l'hashcode del nodo ed invaliderei l'hash usato dal hashset
                iterator.remove();
                insieme.add(temp);
                //Se count è maggiore delle occorrenze correnti allora ne aggiungo altre
                if (count > tempOccorrenze) {
                    //Aggiungo alla dimensione la differenza tra le occorrenze attuali e count
                    size += count - tempOccorrenze;
                }
                //Se count è minore delle occorrenze correnti allora ne tolgo la differenza
                if (count < tempOccorrenze) {
                    //Tolgo count a size
                    size -= tempOccorrenze - count;
                }
                return tempOccorrenze;
            }
        }
        insieme.add(new Nodo<>(element, count));
        numeroModifiche++;
        size += count;
        return 0;
    }

    @Override
    public Set<E> elementSet() {
        Iterator<Nodo<E>> iterator = insieme.iterator();

        HashSet<E> h = new HashSet<>();
        while (iterator.hasNext()) {
            h.add(iterator.next().oggetto);
        }
        return h;
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    @Override
    public boolean contains(Object element) {
        if (element == null) throw new NullPointerException("Elemento è null");
        Iterator<Nodo<E>> iterator = insieme.iterator();
        Nodo<E> temp;

        while (iterator.hasNext()) {
            temp = iterator.next();
            if (element.equals(temp.oggetto)) return true;
        }
        return false;
    }

    @Override
    public void clear() {
        insieme.clear();
        numeroModifiche++;
        size = 0;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /*
     * Due multinsiemi sono uguali se e solo se contengono esattamente gli
     * stessi elementi (utilizzando l'equals della classe E) con le stesse
     * molteplicità.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MyMultiset)) return false;
        MyMultiset<?> obj = (MyMultiset<?>) o;
        if (obj.size != size) return false;
        return obj.insieme.containsAll(insieme);
    }

    /*
     * Da ridefinire in accordo con la ridefinizione di equals.
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 0;
        for (Nodo<E> nodo : insieme) {
            hash += nodo.hashCode();
        }
        return hash;
    }

}
