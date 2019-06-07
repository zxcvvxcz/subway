//인접한 역 사이 거리를 저장할 tree
import java.util.ArrayList;

public class EdgeList {
    private ArrayList<Edge> edges;
    private StationNode nodeInPath;

    public EdgeList(){
        edges = new ArrayList<>();
        nodeInPath = null;
    }
    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<Edge> edges) {
        this.edges = edges;
    }
    public StationNode getNodeInPath() {
        return nodeInPath;
    }

    public void setNodeInPath(StationNode nodeInPath) {
        this.nodeInPath = nodeInPath;
    }
    public void add(StationNode endNode, long weight){
        edges.add(new Edge(endNode, weight));
    }
}

class Edge{
    private StationNode endNode;
    private long weight;
    public Edge(StationNode endNode, long weight){
        this.endNode = endNode;
        this.weight = weight;
    }

    public StationNode getEndNode() {
        return endNode;
    }

    public long getWeight() {
        return weight;
    }
    public void setEndNode(StationNode endNode) {
        this.endNode = endNode;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

}
