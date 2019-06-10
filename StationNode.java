//각 역의 정보를 나타내는 node
public class StationNode {
    private final String id;    //고유 번호
    private final String name;  //역 이름
    private StationNode next;   //이름이 같고 호선 번호가 더 큰 노드
    private long distance;           //시작점으로부터 최단 거리
    // 인접한 역 사이 거리가 최대 1억이고 수십만개의 역이 존재할 수 있으므로 값을 long으로 받는다
    private EdgeList adjacentNodes; //인접한 노드들

    public StationNode(String id, String name){
        this.id = id;
        this.name = name;
        this.next = null;
        distance = Long.MAX_VALUE;  //distance는 가능한 최대값으로 초기화
        adjacentNodes = new EdgeList();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
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
    }   //마지막 경로 출력시 이름만 출력하기 위해 override
    @Override
    public StationNode clone(){     //같은 데이터로 여러번 검색하기 위해 검색 시 사본 이용
        StationNode cloneNode = new StationNode(id,name);
        cloneNode.setAdjacentNodes(adjacentNodes);
        return cloneNode;
    }

    @Override
    public boolean equals(Object obj) {     //Arraylist의 기능을 활용하기 위해 equals 새로 정의
        //고유 번호로 노드를 구별
        if(obj == null)
            return false;
        else if(obj.getClass() != getClass())
            return false;
        else{
            return ((StationNode)obj).getId().equals(getId());
        }
    }
}