import java.util.ArrayList;

public class VertexHeap {
    private ArrayList<StationNode> vertexHeap;

    public VertexHeap(ArrayList<StationNode> arrayList){
        vertexHeap = arrayList;
        for(StationNode stationNode : vertexHeap){
            stationNode.setDistance(Long.MAX_VALUE);
            stationNode.getAdjacentNodes().setNodeInPath(null);
        }
    }
    public ArrayList<StationNode> getVertexHeap() {
        return vertexHeap;
    }
    public void swap(int num1, int num2){
        StationNode temp = vertexHeap.get(num1);
        vertexHeap.set(num1, vertexHeap.get(num2));
        vertexHeap.set(num2, temp);
    }
    public void setVertexHeap(ArrayList<StationNode> vertexHeap) {
        this.vertexHeap = vertexHeap;
    }

    public ArrayList<StationNode> DoHeapSort()
    {
        int len=vertexHeap.size();
        for(int i=(len-1)/2;i>=0;i--)
            PercolateDown(i,len-1);
        for(int size=len-1;size>0;size--){
            swap(size,0);
            PercolateDown(0,size-1);
        }
        return (vertexHeap);
    }

    public void PercolateDown(int i, int n){
        int child=2*i+1;
        int rightChild=2*(i+1);
        if(child<=n){
            if((rightChild<=n) &&  (vertexHeap.get(child).getDistance() < vertexHeap.get(rightChild).getDistance()))
                child=rightChild;
            if(vertexHeap.get(i).getDistance() < vertexHeap.get(child).getDistance()){
                swap(i,child);
                PercolateDown(child,n);
            }
        }
    }
}
