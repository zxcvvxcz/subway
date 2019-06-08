//이름이 같은 StationNode들을 연결해 경로 검색시 같은 이름의 모든 역을 빠르게 검색하기 위한 linked list
public class NodeLinkedList {
    private StationNode head;

    public NodeLinkedList(StationNode head){
        this.head = head;
    }
    public StationNode getHead() {
        return head;
    }

    public void setHead(StationNode head) {
        this.head = head;
    }

    public void addNode(StationNode stationNode){   //이름이 같은 노드를 리스트에 추가
        for(StationNode ptr = head; ptr!=stationNode; ptr=ptr.getNext()) {
            //이름이 같은 노드끼리는 환승역 관계이므로 서로 5분 거리
            ptr.getAdjacentNodes().add(stationNode, 5);
            stationNode.getAdjacentNodes().add(ptr, 5);
            if(ptr.getNext() == null)
                ptr.setNext(stationNode);
        }
    }
}
