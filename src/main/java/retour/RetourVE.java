package retour;

import java.util.List;

public record RetourVE(List<List<Integer>> v, List<Etat> e) {
    @Override
    public String toString() {
        return String.format("""
                V : %s
                E : %s
                """, v, e);
    }
}
