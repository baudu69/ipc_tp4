package retour;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record Etat(List<List<Integer>> marquage, int transition) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Etat etat = (Etat) o;
        return transition == etat.transition && Objects.equals(marquage, etat.marquage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(marquage, transition);
    }

    @Override
    public String toString() {
        List<List<Integer>> marquagePropre = new ArrayList<>();
        marquage.forEach(it -> {
            List<Integer> liste = new ArrayList<>();
            for (int i = 0; i < it.size(); i++) {
                if (it.get(i) == 1) {
                    liste.add(i + 1);
                }
            }
            marquagePropre.add(liste);
                }
        );
        return String.format("""
                    
                    %s : %s
                """, transition, marquagePropre);
    }

}
