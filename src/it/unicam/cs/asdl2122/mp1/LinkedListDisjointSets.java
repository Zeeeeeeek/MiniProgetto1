package it.unicam.cs.asdl2122.mp1;

import java.util.HashSet;
import java.util.Set;


/**
 * La classe fa uso di un hashset per contenere tutti i suoi rappresentanti.<br><br>
 *
 * I metodi {@link LinkedListDisjointSets#makeSet} e {@link LinkedListDisjointSets#findSet} hanno complessità O(1)
 * perché non dipendono dalla dimensione di una linkedlist<br><br>
 *
 * Il metodo {@link LinkedListDisjointSets#union} ha complessità O(<code>n</code>) dove <code>n</code> è la cardinalità
 * dell'insieme con cardinalità più piccola.<br><br>
 *
 * @author Luca Tesei (template) <br>
 * Enrico Ulissi enrico.ulissi@studenti.unicam.it (implementazione)
 */
public class LinkedListDisjointSets implements DisjointSets {


    //Collezione dei rappresentanti
    private HashSet<DisjointSetElement> collezione;

    /**
     * Crea una collezione vuota di insiemi disgiunti.
     */
    public LinkedListDisjointSets() {
        collezione = new HashSet<>();
    }

    /*
     * Nella rappresentazione con liste concatenate un elemento è presente in
     * qualche insieme disgiunto se il puntatore al suo elemento rappresentante
     * (ref1) non è null.
     */

    /**
     * Verifica se un elemento è presente in un insieme disgiunto
     * @param e
     *              l'elemento da cercare
     * @return true se il rappresentante non è null, false altrimenti
     */
    @Override
    public boolean isPresent(DisjointSetElement e) {
        //Se e è null non lancio nessuna eccezione ma restituisco false
        if(e == null) return false;
        return e.getRef1() != null;
    }

    /*
     * Nella rappresentazione con liste concatenate un nuovo insieme disgiunto è
     * rappresentato da una lista concatenata che contiene l'unico elemento. Il
     * rappresentante deve essere l'elemento stesso e la cardinalità deve essere
     * 1.
     */

    /**
     * Crea un insieme singoletto con un solo elemento, imposta il rappresentante come <code>e</code> e la cardinalità
     * ad 1
     * @param e
     *              l'elemento da inserire nell'insieme creato
     * @throws NullPointerException se <code>e</code> è null
     * @throws IllegalArgumentException se l'elemento fa già parte di un insieme disgiunto
     */
    @Override
    public void makeSet(DisjointSetElement e) {
        if (e == null) throw new NullPointerException("Elemento passato null");
        if (isPresent(e)) throw new IllegalArgumentException("L'elemento passato fa già parte di un insieme disgiunto");
        //Visto che la complessità per l'add di un hashset è di O(1) allora anche il makeset avrà complessità costante
        collezione.add(e);
        //Imposto e come il suo stesso rappresentante e imposto la cardinalità della sua lista ad 1
        e.setRef1(e);
        e.setNumber(1);
    }

    /*
     * Nella rappresentazione con liste concatenate per trovare il
     * rappresentante di un elemento basta far riferimento al suo puntatore
     * ref1.
     */

    /**
     * Restituisce il rappresentante dell'elemento passato. Complessità costante
     * @param e
     *              l'elemento di cui cercare l'insieme disgiunto
     * @return rappresentante dell'elemento
     *
     * @throws NullPointerException se <code>e</code> è null
     *
     * @throws IllegalArgumentException se l'elemento passato non è presente in nessun insieme disgiunto
     */
    @Override
    public DisjointSetElement findSet(DisjointSetElement e) {
        if (e == null) throw new NullPointerException("e è null");
        if (!isPresent(e.getRef1())) throw new IllegalArgumentException("l'elemento passato non è presente" +
                "in nessuno degli insiemi disgiunti correnti");
        //Complessità di findset O(1)
        return e.getRef1();
    }

    /*
     * Dopo l'unione di due insiemi effettivamente disgiunti il rappresentante
     * dell'insieme unito è il rappresentate dell'insieme che aveva il numero
     * maggiore di elementi tra l'insieme di cui faceva parte {@code e1} e
     * l'insieme di cui faceva parte {@code e2}. Nel caso in cui entrambi gli
     * insiemi avevano lo stesso numero di elementi il rappresentante
     * dell'insieme unito è il rappresentante del vecchio insieme di cui faceva
     * parte {@code e1}.
     *
     * Questo comportamento è la risultante naturale di una strategia che
     * minimizza il numero di operazioni da fare per realizzare l'unione nel
     * caso di rappresentazione con liste concatenate.
     *
     */

    /**
     * Il metodo union ha come obbiettivo quello di inserire la lista più piccola tra il primo ed il secondo elemento
     * della lista più grande.
     * Nello specifico inserisce come successivo del rappresentante della lista più grande il rappresentante di quella
     * più piccola, poi il successivo dell'ultimo elemento della lista minore viene impostato come il secondo
     * membro della lista più grande(il successivo del rappresentante prima dell'operazione). Così da avere un numero
     * di operazioni pari alla cardinalità della lista minore, garantendo la complessità di O(n), n = cardinalità
     * lista più piccola. Aggiorna poi i vari rappresentanti e la dimensione della lista.
     * In fine rimuove il rappresentante della lista piccola dalla collezione.<br><br>
     *
     * @param e1 elemento del primo insieme da unire
     *
     * @param e2 elemento del secondo insieme da unire
     *
     * @throws NullPointerException se almeno uno tra <code>e1</code> o <code>e2</code> è null
     *
     * @throws IllegalArgumentException almeno uno tra <code>e1</code> o <code>e2</code> non sono presenti in nessun
     * insieme disgiunto
     */
    @Override
    public void union(DisjointSetElement e1, DisjointSetElement e2) {
        if (e1 == null || e2 == null) throw new NullPointerException("e1 o e2 è null");
        if (!isPresent(e1) || !isPresent(e2)) throw new IllegalArgumentException("e1 o e2 non è presente in nessuno" +
                "degli insiemi disgiunti correnti");

        //Se e1 e e2 fanno già parte dello stesso insieme disgiunto allora l’operazione non fa niente
        if (e1.getRef1() == e2.getRef1()) return;

        //La cardinalità di e1 è maggiore o uguale di quella di e2, quest'ultimo viene inserito in e1
        if (e1.getRef1().getNumber() >= e2.getRef1().getNumber()) {
            //Salvo il rappresentante di e2
            DisjointSetElement elementE2 = e2.getRef1();
            //Rimuovo il rappresentante di e2 dalla collezione
            collezione.remove(elementE2);
            //Prendo il rappresentante di e1 e il suo successivo
            DisjointSetElement rappElementoE1 = e1.getRef1();
            DisjointSetElement successivoElementoE1 = e1.getRef1().getRef2();
            //Imposto come successivo del rappresentante di e1 il rappresentante di e2
            rappElementoE1.setRef2(elementE2);
            //Cambio cardinalità
            rappElementoE1.setNumber(rappElementoE1.getNumber() + e2.getRef1().getNumber());
            while(elementE2.getRef2() != null) {
                //Modifica rappresentante
                elementE2.setRef1(rappElementoE1);
                //Prendo il successivo
                elementE2 = elementE2.getRef2();
            }
            //Al termine del ciclo elementE2 è l'ultimo elemento di E2 allora cambio il suo rappresentante e imposto
            //come suo successivo il secondo elemento della lista di E1
            elementE2.setRef1(rappElementoE1);
            elementE2.setRef2(successivoElementoE1);
            return;
        }
        //Allora la cardinalità di e2 è più grande

        //Salvo il rappresentante di e1 e lo rimuovo dalla collezione
        DisjointSetElement elementE1 = e1.getRef1();
        collezione.remove(elementE1);

        //Salvo il secondo elemento di e2 e il suo rappresentante
        DisjointSetElement rappE2 = e2.getRef1();
        DisjointSetElement successivoElementoE2 = rappE2.getRef2();
        //Imposto come successivo del rappresentante di e2 il rappresentante di e1
        rappE2.setRef2(elementE1);
        //Cambio cardinalità
        rappE2.setNumber(elementE1.getNumber() + rappE2.getNumber());
        while(elementE1.getRef2() != null) {
            //Modifica rappresentante
            elementE1.setRef1(rappE2);
            //Prendo il successivo
            elementE1 = elementE1.getRef2();
        }
        //Terminato il ciclo elementE1 corrisponde all'ultimo elemento della lista più piccola, allora imposto come suo
        //successivo il secondo elemento della lista più grande(prima dell'operazione) e aggiorno il suo rappresentante
        elementE1.setRef1(e2.getRef1());
        elementE1.setRef2(successivoElementoE2);
    }

    /**
     * Restituisce insieme dei rappresentanti degli insiemi disgiunti
     * @return collezione dei rappresentanti
     */
    @Override
    public Set<DisjointSetElement> getCurrentRepresentatives() {
        //Restituisco direttamente l'intera collezione di rappresentanti
        return collezione;
    }

    /**
     * Restituisce gli elementi della lista di cui fa parte l'elemento passato
     * @param e
     *              l'elemento di cui si vuole ottenere l'insieme disgiunto di
     *              cui fa parte
     * @return  set contenente la lista di elementi
     *
     * @throws NullPointerException se <code>e</code> è null
     *
     * @throws IllegalArgumentException se l'elemento non è presente negli insiemi disgiunti
     */
    @Override
    public Set<DisjointSetElement> getCurrentElementsOfSetContaining(
            DisjointSetElement e) {
        if(e == null) throw new NullPointerException("Elemento passato null");
        if(!isPresent(e)) throw new IllegalArgumentException("Elemento non presente negli insiemi");
        Set<DisjointSetElement> set = new HashSet<>();
        //Prendo il primo elemento di e ed inizio ad inserirlo nel set
        DisjointSetElement elemento = e.getRef1();

        while(elemento!= null) {
            set.add(elemento);
            elemento = elemento.getRef2();
        }

        return set;
    }

    /**
     * cardinalità dell'insieme di e
     * @param e
     *              l'elemento di cui si vuole ottenere la cardinalità
     * @return cardinalità
     *
     * @throws NullPointerException se <code>e</code> è null
     *
     * @throws IllegalArgumentException se <code>e</code> non è presente in nessun insieme disgiunto
     */
    @Override
    public int getCardinalityOfSetContaining(DisjointSetElement e) {
        if(e == null) throw new NullPointerException("Elemento null");
        if(!isPresent(e)) throw new IllegalArgumentException("Elemento non presente");
        return e.getRef1().getNumber();
    }

}
