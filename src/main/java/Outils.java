import retour.Etat;
import retour.RenvoieAjoutFils;
import retour.RetourVE;

import java.util.*;

public class Outils {

    /**
     * Retourne un graphe a partir d'un reseau de Petri
     * @param P    Nombre de places P
     * @param T    Nombre de transitions T
     * @param pre  Un tableau pre[p][t] qui indique combien de jetons doivent être pris dans p quand on emprunte t
     * @param post Un tableau post[p][t] qui indique combien de jetons doivent être ajoutés dans p quand on emprunte t (pas utilisé ici)
     * @param m    Un marquage est représenté par un tableau m (m[p]=nombre de jetons dans p)
     * @return Un Graphe
     */
    public static RetourVE graphe(int P, int T, List<List<Integer>> pre, List<List<Integer>> post, List<Integer> m) {
        List<List<Integer>> V = new ArrayList<>();
        List<Etat> E = new ArrayList<>();
        V.add(m);

        Stack<List<Integer>> S = new Stack<>();
        S.push(m);
        while(!S.empty()) {
            System.out.println("STACK : " + S);
            List<Integer> marquageActuel = S.peek();
            S.pop();
            List<Integer> tFranchissables = franchissable(P, T, pre, post, marquageActuel);
            tFranchissables.forEach(t -> {
                List<Integer> mPrime = new ArrayList<>(marquageActuel);

                List<Integer> preT = pre.get(t);
                List<Integer> postT = post.get(t);

                for (int i = 0; i < P; i++) {
                    mPrime.set(i, mPrime.get(i) - preT.get(i) + postT.get(i));
                }

                if(!V.contains(mPrime)) {
                    V.add(mPrime);
                    S.push(mPrime);
                }

                E.add(new Etat(Arrays.asList(marquageActuel, mPrime), t));

            });
        }

        return new RetourVE(V, E);
    }

    /**
     * @param P    Nombre de places P
     * @param T    Nombre de transitions T
     * @param pre  Un tableau pre[p][t] qui indique combien de jetons doivent être pris dans p quand on emprunte t
     * @param post Un tableau post[p][t] qui indique combien de jetons doivent être ajoutés dans p quand on emprunte t (pas utilisé ici)
     * @param m    Un marquage est représenté par un tableau m (m[p]=nombre de jetons dans p)
     */
    public static List<Integer> franchissable(int P, int T, List<List<Integer>> pre, List<List<Integer>> post, List<Integer> m) {
        List<Integer> transitionsFranchissables = new ArrayList<>();
        for (int i = 0; i < T; i++) { // T
            boolean tPossible = true;
            for (int j = 0; j < P; j++) { // P
                if (pre.get(i).get(j) > m.get(j)) {
                    tPossible = false;
                    break;
                }
            }
            if (tPossible) {
                transitionsFranchissables.add(i);
            }
        }
        return transitionsFranchissables;
    }

    /**
     * Ajoute un fils à l'arbre
     *
     * @param arbre_dict un dictionnaire qui à chaque identifiant associe son parent dans l'arbre
     * @param arbre_tab  un tableau qui à chaque identifiant associe un marquage
     * @param iden       identifiant du père
     * @param m          Liste de jetons à mettre en fils
     * @return RenvoieAjoutFils objet contenant arbre_dict et arbre_tab
     */
    public static RenvoieAjoutFils ajoutFils(List<Integer> arbre_dict, List<List<Integer>> arbre_tab, int iden, List<Integer> m) {
        arbre_dict.add(iden);
        arbre_tab.add(m);
        return new RenvoieAjoutFils(arbre_dict, arbre_tab);
    }

    /**
     * Liste tous les ancettres de l'identifiant iden
     *
     * @param arbre_dict un dictionnaire qui à chaque identifiant associe son parent dans l'arbre
     * @param arbre_tab  un tableau qui à chaque identifiant associe un marquage
     * @param iden       identifiant du marquage
     * @return arbre_tab completé
     */
    public static List<List<Integer>> ancetres(List<Integer> arbre_dict, List<List<Integer>> arbre_tab, int iden) {
        if (iden == 0) {
            List<List<Integer>> lists = new ArrayList<>();
            lists.add(arbre_tab.get(iden));
            return lists;
        } else {
            int iden_parent = arbre_dict.get(iden);
            List<List<Integer>> a = ancetres(arbre_dict, arbre_tab, iden_parent);
            a.add(arbre_tab.get(iden));
            return a;
        }
    }

    /**
     * Compare deux emplacements de jetons (marquage associés)
     * Detecte les boucles infinis
     *
     * @param m1 Emplacements de jetons 1
     * @param m2 Emplacements de jetons 1
     * @return true si un inclue dans l'autre
     */
    public static boolean temoin(List<Integer> m1, List<Integer> m2) {
        boolean test = false;
        for (int p = 0; p < m1.size(); p++) {
            if (m1.get(p) > m2.get(p)) {
                return false;
            } else if (m1.get(p) < m2.get(p)) {
                test = true;
            }
        }
        return test;
    }

    /**
     * @param arbre_dict     un dictionnaire qui à chaque identifiant associe son parent dans l'arbre
     * @param arbre_tab      un tableau qui à chaque identifiant associe un marquage
     * @param iden           identifiant du parent
     * @param franchissables id des transitions franchissables depuis l'endroit où l'on se trouve
     * @param P              Nombre de places P
     * @param T              Nombre de transitions T
     * @param pre            Un tableau pre[p][t] qui indique combien de jetons doivent être pris dans p quand on emprunte t
     *                       * @param post Un tableau post[p][t] qui indique combien de jetons doivent être ajoutés dans p quand on emprunte t
     *                       * @param m Un marquage est représenté par un tableau m (m[p]=nombre de jetons dans p)
     * @return true si borné, false sinon
     */
    public static int estBorneWorker(List<Integer> arbre_dict, List<List<Integer>> arbre_tab, int iden, List<Integer> franchissables, int P, int T, List<List<Integer>> pre, List<List<Integer>> post, List<Integer> m) {
        List<List<Integer>> listeAncetre = ancetres(arbre_dict, arbre_tab, iden);

        //Condition 2 : Si `f` a un ancêtre étiqueté par `N` tel que `∀q, M(q) <= N(q)` et `∃p, M(p) < N(p)`,
        // alors on s'arrête (le réseau est non borné)
        for (List<Integer> ancetre : listeAncetre) {
            if (temoin(ancetre, m)) {
                return 0;
            }
        }
        //Condition 1 : Si `f` a un ancêtre étiqueté par `M`, on passe à la feuille suivante.
        listeAncetre.remove(listeAncetre.size()-1); //On s'auto-enlève de la liste des ancetres
        if (listeAncetre.contains(m) && iden != 0) {
            return -1;
        }

        //Sinon, pour chaque transition `t` admissible par `M`, on calcule `M'` tel que `M --t-> M'`,
        // et on ajoute un fils à `f`, étiqueté par `M'`.
        int idenPere = iden;
        for (Integer t : franchissables) {

            List<Integer> mPrime = new ArrayList<>(m);

            for (int i = 0; i < P; i++) {
                List<Integer> preDeP = pre.get(i);
                mPrime.set(i, mPrime.get(i) - preDeP.get(t));

                List<Integer> postDeP = post.get(i);
                mPrime.set(i, mPrime.get(i) + postDeP.get(t));
            }
            RenvoieAjoutFils arbre = ajoutFils(arbre_dict, arbre_tab, idenPere, mPrime);
            iden = arbre.arbre_dict().size()-1;

            int result = estBorneWorker(arbre.arbre_dict(), arbre.arbre_tab(), iden, franchissable(P, T, pre, post, mPrime), P, T, pre, post, mPrime);
            if (result == 0)
                return 0;
        }

        return 1;
    }

    /**
     * Vérifie qu'un réseau est borné
     *
     * @param P    Nombre de places P
     * @param T    Nombre de transitions T
     * @param pre  Un tableau pre[p][t] qui indique combien de jetons doivent être pris dans p quand on emprunte t
     * @param post Un tableau post[p][t] qui indique combien de jetons doivent être ajoutés dans p quand on emprunte t
     * @param m    Un marquage est représenté par un tableau m (m[p]=nombre de jetons dans p)
     * @return true si borné, false sinon
     */
    public static boolean estBorne(int P, int T, List<List<Integer>> pre, List<List<Integer>> post, List<Integer> m) {
        RenvoieAjoutFils arbre = ajoutFils(new ArrayList<>(), new ArrayList<>(), 0, m);
        List<Integer> franchissables = franchissable(P, T, pre, post, m);
        boolean etat = estBorneWorker(arbre.arbre_dict(), arbre.arbre_tab(), 0, franchissables, P, T, pre, post, m) == 1;
        System.out.println(etat ? "Le réseau est borné" : "Le réseau n'est pas borné");
        return etat;
    }
}
