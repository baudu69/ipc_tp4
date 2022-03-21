import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Graphe {
    @Test
    void testGraphe() {
        System.out.println("****Figure 1****");
        //Pre
        List<List<Integer>> pre = new ArrayList<>();
        pre.add(Stream.of(1, 0, 0, 0, 0).collect(Collectors.toCollection(ArrayList::new)));
        pre.add(Stream.of(0, 1, 0, 0, 0).collect(Collectors.toCollection(ArrayList::new)));
        pre.add(Stream.of(0, 0, 1, 0, 0).collect(Collectors.toCollection(ArrayList::new)));
        pre.add(Stream.of(0, 0, 0, 1, 1).collect(Collectors.toCollection(ArrayList::new)));
        pre.add(Stream.of(0, 0, 0, 0, 1).collect(Collectors.toCollection(ArrayList::new)));
        //Post
        List<List<Integer>> post = new ArrayList<>();
        post.add(Stream.of(0, 1, 1, 0, 0).collect(Collectors.toCollection(ArrayList::new)));
        post.add(Stream.of(0, 0, 0, 1, 0).collect(Collectors.toCollection(ArrayList::new)));
        post.add(Stream.of(0, 0, 0, 0, 1).collect(Collectors.toCollection(ArrayList::new)));
        post.add(Stream.of(1, 0, 0, 0, 0).collect(Collectors.toCollection(ArrayList::new)));
        post.add(Stream.of(0, 0, 1, 0, 0).collect(Collectors.toCollection(ArrayList::new)));
        //M
        List<Integer> M = Stream.of(1, 0, 0, 0, 0).collect(Collectors.toCollection(ArrayList::new));

        int P = pre.size();
        int T = pre.get(0).size();
        System.out.println(Outils.graphe(P, T, pre, post, M));
    }
}
