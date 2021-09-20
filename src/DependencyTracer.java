import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;

public class DependencyTracer {

    List<String> inputLines;
    Map<String, List<String>> dependencyMap = new HashMap<>();
    Map<String, List<String>> inverseMap = new HashMap<>();

    private void loadInput(String path) {
        Path filePath = Path.of(path);
        try {
            inputLines = Files.readAllLines(filePath);

            for(String s : inputLines) {
                String[] splits = s.split(" ");
                if(splits.length > 0) {
                    //1st alphabet of the row is root
                    String parent = splits[0];

                    List<String> childList = new ArrayList<>();
                    for(int i=1 ; i< splits.length ; i++) {
                        //collect the 1st level child nodes
                        childList.add(splits[i]);

                        //check if we have saved the parent of current child node
                        List<String> parentNodesList = inverseMap.get(splits[i]);
                        if(null == parentNodesList) {
                            parentNodesList = new ArrayList<>();
                            parentNodesList.add(parent);
                            //save the inverse child-parent relation
                            //this will help us crawl inversely later
                            inverseMap.put(splits[i], parentNodesList);
                        } else {
                            parentNodesList.add(parent);
                        }
                    }
                    //save the parent-child relation
                    dependencyMap.put(parent, childList);
                }
            }
        } catch (Exception e) {
            System.out.println("Exception occured while reading file");
        }
    }

    private void printInverseDependencies() {
        for(Entry<String, List<String>> entry : inverseMap.entrySet()) {
            System.out.print(entry.getKey()+" ");
            for (String s : entry.getValue()) {
                System.out.print(s + " ");
            }
            System.out.println();
        }
    }

    private void traceDependencies(Map<String, List<String>> dependencyMap) {
        for(Entry<String, List<String>> entry : dependencyMap.entrySet()) {
            SortedSet<String> dependencySet = new TreeSet<>();
            //get 1st level child nodes
            List<String> dependencies = entry.getValue();

            //to handle cyclic dependencies we need to
            //track nodes that we have already visited
            Set<String> visitedSet = new HashSet<>();
            visitedSet.add(entry.getKey());

            //unleash the beast - recursion
            for(String s : dependencies)
                startTracing(s, dependencyMap, dependencySet, visitedSet);

            //print the root node
            System.out.print(entry.getKey()+" ");

            //print the recursive dependencies
            for (String s : dependencySet) {
                System.out.print(s + " ");
            }
            System.out.println();
        }
    }

    private void startTracing(String s, Map<String, List<String>> dependencyMap, SortedSet<String> sortedSet, Set<String> visitedSet) {
        sortedSet.add(s);
        List<String> dependencies = dependencyMap.get(s);

        //if no more child nodes, let's head back
        if(null == dependencies)
            return;

        for(String dependency : dependencies)
            //check if we have visited this node earlier
            if(!visitedSet.contains(dependency)) {
                visitedSet.add(dependency);
                startTracing(dependency, dependencyMap, sortedSet, visitedSet);
            }

    }

    public static void main(String[] args) {
        DependencyTracer dependencyTracer = new DependencyTracer();
        if(args.length > 0) {
            dependencyTracer.loadInput(args[0]);
            System.out.println("Dependency Analysis");
            dependencyTracer.traceDependencies(dependencyTracer.dependencyMap);
            System.out.println("------------- \nInverse dependency");
            dependencyTracer.traceDependencies(dependencyTracer.inverseMap);
        } else
            System.out.println("File path expected as first param to the program");
    }

}
