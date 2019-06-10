//priority queue 대신 heap을 사용해 더 빠르게 경로를 찾기 위한 노드들의 힙
import java.util.ArrayList;

public class VertexHeap {
    private ArrayList<StationNode> vertexHeap;

    public VertexHeap(ArrayList<StationNode> arrayList){
        vertexHeap = arrayList;
        for(StationNode stationNode : vertexHeap){
            //새로운 시작 역과 마지막 역이 주어질 때마다 새로 힙을 정렬해야 하므로 이 때마다 힙을 새로 만든다.
            //그러므로 힙을 새로 만들때마다 힙 내부 노드들의 값을 초기화해준다.
            stationNode.setDistance(Long.MAX_VALUE);
            stationNode.getAdjacentNodes().setNodeInPath(null);
        }
    }
    public void swap(int num1, int num2){   //arraylist 내의 두 노드의 위치를 바꾸는 메소드
        StationNode temp = vertexHeap.get(num1);
        vertexHeap.set(num1, vertexHeap.get(num2));
        vertexHeap.set(num2, temp);
    }

    public void PercolateDown(int i, int n){    //HW4 method 활용
        int child=2*i+1;
        int rightChild=2*(i+1);
        if(child<=n){
            if((rightChild<=n) &&  (vertexHeap.get(child).getDistance() > vertexHeap.get(rightChild).getDistance()))
                child=rightChild;
            if(vertexHeap.get(i).getDistance() > vertexHeap.get(child).getDistance()){
                swap(i,child);
                PercolateDown(child,n);
            }
        }
    }

    public void PercolateUp(int i){     //heap을 이루도록 노드 이동
        int parent = (i-1) / 2;
        if(i>0 && vertexHeap.get(parent).getDistance() > vertexHeap.get(i).getDistance()){
            swap(parent,i);
            PercolateUp(parent);
        }
    }

    public StationNode heapDelete(){    //heap에서 노드 삭제
        if(!vertexHeap.isEmpty()){
            int last = vertexHeap.size() - 1;
            StationNode root = vertexHeap.get(0);
            vertexHeap.set(0,vertexHeap.get(last));
            vertexHeap.remove(last--);
            PercolateDown(0,last);
            return root;
        }
        else
            return null;
    }
}
