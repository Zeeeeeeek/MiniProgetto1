package it.unicam.cs.asdl2122.mp1;

import java.util.HashSet;
import java.util.Set;

// TODO insireire import della Java SE che si ritengono necessari

/**
 * // TODO spiegare come viene implementata la classe
 *
 * @author Luca Tesei (template) **INSERIRE NOME, COGNOME ED EMAIL
 * xxxx@studenti.unicam.it DELLO STUDENTE** (implementazione)
 */
public class LinkedListDisjointSets implements DisjointSets {

    // TODO inserire le variabili istanza private che si ritengono necessarie

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
    @Override
    public boolean isPresent(DisjointSetElement e) {
        if(e == null) return false;
        return e.getRef1() != null;
    }

    /*
     * Nella rappresentazione con liste concatenate un nuovo insieme disgiunto è
     * rappresentato da una lista concatenata che contiene l'unico elemento. Il
     * rappresentante deve essere l'elemento stesso e la cardinalità deve essere
     * 1.
     */
    @Override
    public void makeSet(DisjointSetElement e) {
        if (e == null) throw new NullPointerException("Elemento passato null");
        if (isPresent(e)) throw new IllegalArgumentException("L'elemento passato fa già parte di un insieme disgiunto");

        collezione.add(e);
        e.setRef1(e);
        e.setNumber(1);
    }

    /*
     * Nella rappresentazione con liste concatenate per trovare il
     * rappresentante di un elemento basta far riferimento al suo puntatore
     * ref1.
     */
    @Override
    public DisjointSetElement findSet(DisjointSetElement e) {
        if (e == null) throw new NullPointerException("e è null");
        if (!isPresent(e.getRef1())) throw new IllegalArgumentException("l'elemento passato non è presente" +
                "in nessuno degli insiemi disgiunti correnti");
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
    @Override
    public void union(DisjointSetElement e1, DisjointSetElement e2) {
        if (e1 == null || e2 == null) throw new NullPointerException("e1 o e2 è null");
        if (!isPresent(e1) || !isPresent(e2)) throw new IllegalArgumentException("e1 o e2 non è presente in nessuno" +
                "degli insiemi disgiunti correnti");

        //Se e1 e e2 fanno già parte dello stesso insieme disgiunto allora l’operazione non fa niente
        if (e1.getRef1() == e2.getRef1()) return;

        //La cardinalità di e1 è maggiore di quella di e2
        if (e1.getRef1().getNumber() >= e2.getRef1().getNumber()) {
            DisjointSetElement elementE2 = e2.getRef1();
            DisjointSetElement rappE2 = elementE2;
            DisjointSetElement rappElementoE1 = e1.getRef1();
            DisjointSetElement successivoElementoE1 = e1.getRef1().getRef2();
            //Imposto come successivo del rappresentante di e1 il rappresentante di e2
            rappElementoE1.setRef2(elementE2);
            //Cambio cardinalità
            rappElementoE1.setNumber(e1.getRef1().getNumber() + e2.getRef1().getNumber());
            while(elementE2.getRef2() != null) {
                //Modifica rappresentante
                elementE2.setRef1(rappElementoE1);
                //Prendo il successivo
                elementE2 = elementE2.getRef2();
            }
            elementE2.setRef1(rappElementoE1);
            elementE2.setRef2(successivoElementoE1);
            collezione.remove(rappE2);

            return;
        }
        //Allora la cardinalità di e2 è più grande
        DisjointSetElement elementE1 = e1.getRef1();
        DisjointSetElement rappE1 = elementE1;
        DisjointSetElement rappElementoE2 = e2.getRef1();
        DisjointSetElement successivoElementoE2 = e2.getRef1().getRef2();
        //Imposto come successivo del rappresentante di e2 il rappresentante di e1
        rappElementoE2.setRef2(elementE1);
        //Cambio cardinalità
        rappElementoE2.setNumber(e1.getRef1().getNumber() + e2.getRef1().getNumber());
        while(elementE1.getRef2() != null) {
            //Modifica rappresentante
            elementE1.setRef1(rappElementoE2);
            //Prendo il successivo
            elementE1 = elementE1.getRef2();
        }
        elementE1.setRef1(rappElementoE2);
        elementE1.setRef2(successivoElementoE2);
        collezione.remove(rappE1);
    }

    @Override
    public Set<DisjointSetElement> getCurrentRepresentatives() {
        return collezione;
    }

    @Override
    public Set<DisjointSetElement> getCurrentElementsOfSetContaining(
            DisjointSetElement e) {
        if(e == null) throw new NullPointerException("Elemento passato null");
        if(!isPresent(e)) throw new IllegalArgumentException("Elemento non presente negli insiemi");
        Set<DisjointSetElement> set = new HashSet<>();
        //Prendo il rappresentante di e
        DisjointSetElement elemento = e.getRef1();

        while(elemento!= null) {
            set.add(elemento);
            elemento = elemento.getRef2();
        }

        return set;
    }

    @Override
    public int getCardinalityOfSetContaining(DisjointSetElement e) {
        if(e == null) throw new NullPointerException("Elemento null");
        if(!isPresent(e)) throw new IllegalArgumentException("Elemento non presente");
        return e.getRef1().getNumber();
    }

}
