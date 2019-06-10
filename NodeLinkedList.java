//이름이 같은 StationNode들을 연결해 경로 검색시 같은 이름의 모든 역을 빠르게 검색하기 위한 linked list
public class NodeLinkedList {
    private StationNode head;   //첫번째 노드
    private int size;   //노드 갯수

    public NodeLinkedList(StationNode head){
        this.head = head;
        size = 0;
        for(;head!=null;head=head.getNext())
            size++;
    }
    public StationNode getHead() {
        return head;
    }

    public int getSize() {
        return size;
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
    @Override
    public NodeLinkedList clone(){  //같은 데이터로 여러번 검색하기 위해 검색 시 사본 이용
        NodeLinkedList cloneList = new NodeLinkedList(head.clone());
        StationNode clonePtr = cloneList.head;
        for(StationNode ptr = head.getNext(); ptr != null; ptr=ptr.getNext()){
            clonePtr.setNext(ptr.clone());
            clonePtr = clonePtr.getNext();
        }
        return cloneList;
    }
}
