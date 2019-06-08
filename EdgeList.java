//인접한 역 사이 거리를 저장할 tree
import java.util.ArrayList;

public class EdgeList {
    private ArrayList<Edge> edges;  //역과 역 사이 edge들의 집합
    private StationNode nodeInPath; //이 edgeList를 가진 노드로 향하는 edge를 가진 노드(들어오는 edge의 시작 노드)

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
    public void add(StationNode endNode, int weight){  //새로운 edge 추가
        edges.add(new Edge(endNode, weight));
    }
}

class Edge{
    private StationNode endNode;    //edge의 끝점
    private int weight;            //인접한 역 사이 거리는 최대 1억이므로 int 사용
    public Edge(StationNode endNode, int weight){
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

    public void setWeight(int weight) {
        this.weight = weight;
    }

}
