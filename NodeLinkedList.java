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

    public void addNode(StationNode stationNode){
        for(StationNode ptr = head; ptr!=stationNode; ptr=ptr.getNext()) {
            ptr.getAdjacentNodes().add(stationNode, (long)5);
            stationNode.getAdjacentNodes().add(ptr, (long)5);
            if(ptr.getNext() == null)
                ptr.setNext(stationNode);
        }
    }
}
