package it.unicam.cs.asdl2122.mp1;


import java.util.*;
import java.util.Arrays;

/**
 * Un multiset sfrutta l'Hashset per contenere tutti i suoi elementi, l'hashset in questione contiene delle istanze di
 * tipo {@link Elemento}. Gli elementi hanno un oggetto e un intero che ne rappresentano le occorrenze così che, se un oggetto
 * dovesse ripetersi, non vengono creati molti puntatori allo stesso oggetto ma se ne incrementano le occorrenze dell'elemento.<br><br>
 *
 * Sono stati ridefiniti i metodi {@link Elemento#equals} e {@link Elemento#hashCode} per un corretto controllo tra elementi.<br><br>
 *
 * È stato creato un iteratore {@link Itr} per il multiset. L'iteratore è fail-fast, i metodi {@link Itr#hasNext} e
 * {@link Itr#next} tengono conto delle occorrenze di uno stesso oggetto.<br><br>
 *
 * Tutti i metodi che modificano le occorrenze di un oggetto del multiset rimuovono e ricreano l'elemento con le occorrenze
 * giuste. Questo perché l'hashset usa una hashmap con key di ogni elemento il suo hashcode. Qualora si limitasse ad una
 * semplice modifica della variabile occorrenze si invaliderebbe l'hash usato come key.
 *
 * @param <E> il tipo degli elementi del multiset
 * @author Luca Tesei (template) <br>
 * Enrico Ulissi enrico.ulissi@studenti.unicam.it (implementazione)
 */
public class MyMultiset<E> implements Multiset<E> {

    private int size;
    private HashSet<Elemento<E>> insieme;
    private int numeroModifiche;

    /*
        Classe per gli elementi dell'insieme, ogni elemento ha un oggetto di tipo E ed un intero che ne rappresenta le
        occorrenze, così da non creare tanti puntatori ad un oggetto nel caso in cui dovesse ripetersi.

        La classe è statica poiché non necessita di accedere ai campi della classe MyMultiset per funzionare
     */
    private static class Elemento<E> {
        private int occorrenze;
        private E oggetto;

        Elemento(E oggetto, int occorrenze) {
            this.occorrenze = occorrenze;
            this.oggetto = oggetto;
        }

        //Due elementi sono uguali se l'oggetto e le occorrenze sono uguali
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof MyMultiset.Elemento)) return false;
            Elemento<?> elemento = (Elemento<?>) o;
            return ((this.occorrenze == elemento.occorrenze) && (this.oggetto.equals(elemento.oggetto)));
        }

        //Riscritto hashcode in accordo ai valori confrontati nell'equals
        @Override
        public int hashCode() {
            int hash = 31 * 17 + this.occorrenze;
            hash = 31 * hash + this.oggetto.hashCode();
            return hash;
        }
    }

    /*
        Iteratore fail-fast per MyMultiset, classe non statica perché necessita di accedere agli elementi della classe
        in cui è stata creata per funzionare
     */
    private class Itr implements Iterator<E> {
        private Elemento<E> elementoRestituito;
        private int numeroModificheAtteso;
        private int indiceOccorrenze;
        private Iterator<Elemento<E>> iteratore;

        private Itr() {
            elementoRestituito = null;
            numeroModificheAtteso = numeroModifiche;
            iteratore = insieme.iterator();
        }

        @Override
        public boolean hasNext() {
            //Alla prima chiamata dell'iteratore uso hasNext() del Hashset
            if (elementoRestituito == null) return iteratore.hasNext();

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

            //Qualora l'elemento restituito dal next avesse altre occorrenze, restituisco lo stesso oggetto e tolgo un'
            //occorrenza
            if (elementoRestituito != null && indiceOccorrenze > 0) {
                indiceOccorrenze--;
                return elementoRestituito.oggetto;
            }
            //A questo punto mi trovo alla prima chiamata del next o quando finiscono le occorrenze di un oggetto
            elementoRestituito = iteratore.next();
            //Restituisco un'occorrenza e quindi tolgo uno al mio indice delle occorrenze
            indiceOccorrenze = elementoRestituito.occorrenze - 1;
            return elementoRestituito.oggetto;
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

    /**
     * Metodo per ottenere la cardinalità di un multinsieme, tiene conto delle occorrenze di ogni oggetto.
     *
     * @return la dimensione del multinsieme
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Restituisco le occorrenze di un elemento
     *
     * @param element l'elemento di cui contare le occorrenze
     * @return numero di occorrenze di {@code element} nel multinsieme. Restituisco 0 se non presente
     * @throws NullPointerException se element è null
     */
    @Override
    public int count(Object element) {
        if (element == null) throw new NullPointerException("L'elemento passato al count è null");
        Iterator<Elemento<E>> iterator = insieme.iterator();
        while (iterator.hasNext()) {
            Elemento<E> elemento = iterator.next();
            //Se trovo l'elemento restituisco le sue occorrenze
            if (element.equals(elemento.oggetto)) return elemento.occorrenze;
        }
        //Se non trovo l'elemento allora le sue occorrenze sono 0
        return 0;
    }

    /**
     * Aggiunge un elemento e le sue occorrenze all'insieme. Modifica le occorrenze se l'elemento è già
     * presente, nel caso contrario ne crea un altro con le giuste occorrenze.<br><br>
     *
     * Nota: utilizzando un hashset quando si modifica un elemento questo deve essere rimosso e riaggiunto con
     * le giuste occorrenze. Altrimenti invaliderei l'hash usato dall'hashset nell'hashmap.
     *
     * @param element     l'elemento di cui aggiungere le occorrenze
     * @param occurrences il numero di occorrenze dell'elemento da
     *                    aggiungere. Può essere zero, nel qual caso non
     *                    verrà apportata alcuna modifica.
     * @return il numero di occorrenze dell'elemento prima dell'operazione. Se l'elemento viene aggiunto
     * per la prima volta restituisce 0.
     * @throws NullPointerException     se element è null
     * @throws IllegalArgumentException se le occorrenze sono negative
     */

    @Override
    public int add(E element, int occurrences) {
        if (element == null) throw new NullPointerException("L'elemento da aggiungere è null");
        if (occurrences < 0) throw new IllegalArgumentException("Non si possono aggiungere valori negativi");

        Iterator<Elemento<E>> iterator = insieme.iterator();

        while (iterator.hasNext()) {
            Elemento<E> elemento = iterator.next();

            if (element.equals(elemento.oggetto)) {
                //Se le occorrenze da aggiungere sono 0, restituisco le occorrenze dell'oggetto e non apporto
                //modifiche
                if (occurrences == 0) return elemento.occorrenze;
                //Controllo se le occorrenze dell'elemento sommate alle nuove occorrenze superano Integer.MAX_VALUE
                //per fare questo casto momentaneamente le occorrenze in un long
                if ((((long) elemento.occorrenze) + occurrences) > Integer.MAX_VALUE)
                    throw new IllegalArgumentException("Questa " +
                            "operazione aggiungerebbe un numero maggiore di Integer.MAX_VALUE");

                //Aggiungo le modifiche, aumento la size e modifico le occorrenze
                numeroModifiche++;
                size += occurrences;
                elemento.occorrenze += occurrences;
                //Rimuovo e riaggiungo l'elemento con le giuste occorrenze per non invalidare l'hash dell'hashmap
                iterator.remove();
                insieme.add(elemento);
                //restituisco le occorrenze prima della modifica
                return elemento.occorrenze - occurrences;
            }
        }
        //Arrivati a questo punto l'elemento non fa già parte dell'insieme
        //allora ne creo uno nuovo, aumento size e numero modifiche poi restituisco 0 perchè
        //non era presente nell'insieme prima di questa operazione.
        insieme.add(new Elemento<E>(element, occurrences));
        numeroModifiche++;
        size += occurrences;
        return 0;
    }

    /**
     * Variante del metodo add che aggiunge una sola occorrenza.<br><br>
     *
     * Nota: utilizzando un hashset quando si modifica un elemento questo deve essere rimosso e riaggiunto con
     * le giuste occorrenze. Altrimenti invaliderei l'hash usato dall'hashset nell'hashmap.
     *
     * @param element l'elemento di cui aggiungere l'occorrenza
     * @throws NullPointerException se element è null
     */
    @Override
    public void add(E element) {
        if (element == null) throw new NullPointerException("L'elemento da aggiungere è null");

        Iterator<Elemento<E>> iterator = insieme.iterator();

        while (iterator.hasNext()) {
            Elemento<E> elemento = iterator.next();
            if (element.equals(elemento.oggetto)) {

                //Controllo se le occorrenze + 1 superano Integer.MAX_VALUE, per farlo casto le occorrenze in un long
                if (((long) elemento.occorrenze + 1) > Integer.MAX_VALUE) throw new IllegalArgumentException("Questa " +
                        "operazione aggiungerebbe un numero maggiore di Integer.MAX_VALUE");
                //Modifico le occorrenze dell'oggetto
                elemento.occorrenze++;
                //Rimuovo e riaggiungo l'elemento per non invalidarne l'hash
                iterator.remove();
                insieme.add(elemento);
                numeroModifiche++;
                size++;
                return;
            }
        }
        //L'elemento non è gia presente e lo aggiungo
        numeroModifiche++;
        size++;
        insieme.add(new Elemento<E>(element, 1));
    }

    /**
     * Rimuove delle occorrenze di un dato elemento se presente nell'insieme. Se le occorrenze da togliere superano
     * quelle presenti, rimuovo l'elemento.<br><br>
     * Nota: utilizzando un hashset quando si modifica un elemento questo deve essere rimosso e riaggiunto con
     * le giuste occorrenze. Altrimenti invaliderei l'hash usato dall'hashset nell'hashmap.
     *
     * @param element     l'elemento di cui rimuovere le occorrenze
     * @param occurrences il numero di occorrenze dell'elemento da
     *                    rimuovere. Può essere zero, nel qual caso non
     *                    verrà apportata alcuna modifica
     * @return numero di occorrenze prima dell'operazione
     * @throws NullPointerException     se element è null
     * @throws IllegalArgumentException se le occorrenze sono negative
     */
    @Override
    public int remove(Object element, int occurrences) {
        if (element == null) throw new NullPointerException("Elemento da rimuovere null");
        if (occurrences < 0) throw new IllegalArgumentException("Occorrenze da rimuovere negative");

        Iterator<Elemento<E>> iterator = insieme.iterator();

        while (iterator.hasNext()) {
            Elemento<E> elemento = iterator.next();
            if (element.equals(elemento.oggetto)) {
                //Se le occorrenze da rimuovere sono 0 allora fermo il metodo e restituisco le occorrenze correnti
                if (occurrences == 0) return elemento.occorrenze;
                //Se le occorrenze dell'oggetto sono maggiori di quelle da rimuovere, riduco le occorrenze.
                if (elemento.occorrenze > occurrences) {
                    elemento.occorrenze -= occurrences;
                    //Rimuovo e aggiungo per non invalidare l'hash
                    iterator.remove();
                    insieme.add(elemento);
                    numeroModifiche++;
                    size -= occurrences;
                    return elemento.occorrenze + occurrences;
                }
                //Altrimenti salvo le occorrenze e rimuovo l'elemento
                int elementoOccorrenze = elemento.occorrenze;
                insieme.remove(elemento);
                numeroModifiche++;
                size -= elementoOccorrenze;
                return elementoOccorrenze;
            }
        }
        //L'elemento da rimuovere non era presente allora restituisco 0
        return 0;
    }

    /**
     * Variante del metodo remove che rimuove una sola occorrenza<br>
     * Nota: utilizzando un hashset quando si modifica un elemento questo deve essere rimosso e riaggiunto con
     * le giuste occorrenze. Altrimenti invaliderei l'hash usato dall'hashset nell'hashmap.
     *
     * @param element l'elemento di cui rimuovere l'occorrenza
     * @return vero se è stata rimossa un'ocorrenza o falsa altrimenti
     * @throws NullPointerException se element è null
     */
    @Override
    public boolean remove(Object element) {
        if (element == null) throw new NullPointerException("Elemento da rimuovere null");

        Iterator<Elemento<E>> iterator = insieme.iterator();

        while (iterator.hasNext()) {
            Elemento<E> elemento = iterator.next();
            if (element.equals(elemento.oggetto)) {
                //Se l'elemento ha una sola occorrenza lo rimuovo
                if (elemento.occorrenze == 1) {
                    numeroModifiche++;
                    size--;
                    return insieme.remove(elemento);
                }
                //Se ha più occorrenze lo rimuovo e lo ricreo con le giuste occorrenze, per non invalidare l'hash usato
                //dall' hashset.
                elemento.occorrenze--;
                iterator.remove();
                insieme.add(elemento);
                numeroModifiche++;
                size--;
                return true;
            }
        }
        //Arrivati in questo punto non ho trovato l'elemento e restituisco false
        return false;

    }

    /**
     * Imposto un certo numero di occorrenze per un elemento<br>
     * Nota: utilizzando un hashset quando si modifica un elemento questo deve essere rimosso e riaggiunto con
     * le giuste occorrenze. Altrimenti invaliderei l'hash usato dall'hashset nell'hashmap.
     *
     * @param element l'elemento di cui aggiungere o togliere occorrenze
     * @param count   numero di occorrenze da impostare
     * @return numero di occorrenze prima dell'operazione
     * @throws NullPointerException     se element è null
     * @throws IllegalArgumentException se count è negativo
     */
    @Override
    public int setCount(E element, int count) {
        if (element == null) throw new NullPointerException("Elemento da modificare null");
        if (count < 0) throw new IllegalArgumentException("Numero di occorreze da rimuovere negativo");

        Iterator<Elemento<E>> iterator = insieme.iterator();

        int elementoOccorrenze = 0;
        while (iterator.hasNext()) {
            Elemento<E> elemento = iterator.next();

            if (element.equals(elemento.oggetto)) {
                elementoOccorrenze = elemento.occorrenze;
                //Se count corrisponde alle occorrenze attuali allora non faccio nessuna modifica
                if (count == elementoOccorrenze) return elementoOccorrenze;

                //Sono sicuro di dover fare delle modifiche
                numeroModifiche++;
                //Se count è 0 rimuovo l'oggetto
                if (count == 0) {
                    //Tolgo l'elemento e restituisco le vecchie occorrenze
                    iterator.remove();
                    size -= elementoOccorrenze;
                    return elementoOccorrenze;
                }
                //Riscrivo le occorrenze
                elemento.occorrenze = count;
                //Rimuovo e aggiungo l'elemento per l'hash
                iterator.remove();
                insieme.add(elemento);
                //Se count è maggiore delle occorrenze correnti allora ne aggiungo altre
                if (count > elementoOccorrenze) {
                    //Aggiungo alla dimensione la differenza tra le occorrenze attuali e count
                    size += count - elementoOccorrenze;
                }
                //Se count è minore delle occorrenze correnti allora ne tolgo la differenza
                if (count < elementoOccorrenze) {
                    size -= elementoOccorrenze - count;
                }
                return elementoOccorrenze;
            }
        }
        //Arrivati a questo punto l'elemento non è presente nell'insieme e quindi ne creo uno se count è maggiore
        //di 0
        if (count > 0) {
            insieme.add(new Elemento<>(element, count));
            numeroModifiche++;
            size += count;
            return elementoOccorrenze;
        }
        //In questo caso count è pari a 0 e l'elemento non è presente, allora non aggiungo nulla e restituisco 0
        return 0;
    }

    /**
     * Creo un set contenente elementi distinti del multinsieme
     *
     * @return set degli elementi, senza le eventuali occorrenze
     */
    @Override
    public Set<E> elementSet() {

        Iterator<Elemento<E>> iterator = insieme.iterator();
        //Creo un hashset nuovo e ci inserisco solo gli oggetti dell'insieme senza contare le occorrenze
        HashSet<E> h = new HashSet<>();
        while (iterator.hasNext()) {
            h.add(iterator.next().oggetto);
        }
        return h;
    }

    /**
     * Crea un iterator per il multiset
     *
     * @return iterator
     */
    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    /**
     * Verifica se un elemento è contenuto in un multinsieme.
     *
     * @param element l'elemento da cercare
     * @return true se è nell'insieme, false altrimenti
     * @throws NullPointerException se element è null
     */
    @Override
    public boolean contains(Object element) {
        if (element == null) throw new NullPointerException("Elemento è null");
        Iterator<Elemento<E>> iterator = insieme.iterator();
        //Controllo ogni elemento e se trovo l'oggetto che cerco restituisco true
        while (iterator.hasNext()) {
            Elemento<E> elemento = iterator.next();
            if (element.equals(elemento.oggetto)) return true;
        }
        //Non avendo trovato l'oggetto restituisco false
        return false;
    }

    /**
     * Svuota l'insieme, aggiunge una modifica e imposta la dimensione a 0
     */
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
        //Se le dimensioni sono differenti sono certo non abbiano gli stessi oggetti
        if (obj.size != size) return false;
        //Non avendo invalidato l'hash durante le modifiche allora sono in grado di usare il containsAll
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
        for (Elemento<E> elemento : insieme) {
            hash += 31 * elemento.hashCode();
        }
        return hash;
    }

}
