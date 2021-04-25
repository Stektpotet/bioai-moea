package ga.selection;
import ga.data.Chromosome;
import ga.data.Population;

import java.util.List;

public interface ParentSelector<TProblem, TPop extends Population<TProblem, TChromo>, TChromo extends Chromosome<TProblem>> {
    public List<TChromo> select(TPop population) throws Exception;
}
