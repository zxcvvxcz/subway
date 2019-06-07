//각 역의 정보를 나타내는 node
public class StationNode {
    private final String id;
    private final String name;
    private final String line;
    private StationNode next;
    private long distance;           //dijkstra algorithm에서 최단 거리 저장
    private EdgeList adjacentNodes; //인접한 노드들

    public StationNode(String id, String name, String line){
        this.id = id;
        this.name = name;
        this.line = line;
        this.next = null;
        distance = Long.MAX_VALUE;
        adjacentNodes = new EdgeList();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLine() {
        return line;
    }

    public StationNode getNext() {
        return next;
    }

    public long getDistance() {
        return distance;
    }

    public EdgeList getAdjacentNodes() {
        return adjacentNodes;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public void setNext(StationNode next) {
        this.next = next;
    }

    public void setAdjacentNodes(EdgeList adjacentNodes) {
        this.adjacentNodes = adjacentNodes;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public StationNode clone(){
        StationNode clone = new StationNode(id,name,line);
        clone.setDistance(distance);
        for(Edge e: getAdjacentNodes().getEdges())
            clone.adjacentNodes.add(e.getEndNode(), e.getWeight());
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null)
            return false;
        else if(obj.getClass() != getClass())
                return false;
        else{
            String name = ((StationNode)obj).getName();
            if(name != this.name)
                return false;
        }
        return true;
    }


}