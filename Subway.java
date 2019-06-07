import java.io.*;
import java.util.HashMap;
import java.util.regex.*;
import java.util.ArrayList;
public class Subway {
    //역 정보:호선, 다음역, 고유번호
    //역과 역 사이 소요시간 정보: 출발역, 도착역, 소요시간
    //호선:LinkedList<Node>, double,circular --> 역 갈라지므로 안 됨
    //호선:
    //Node: 이름 같은 역 String, 고유 번호: String, 이름 String
    //노드는 고유 번호로 구별
    //역 이름 nodehashtable에 넣으면 역 찾고, 검색 결과마다 모두 최단 경로 검색
    //출발역 도착역 입력:
    //최단 경로: dijkstra algorithm
    //이름이 같거나 같은 호선이면 연결 됨
    //이름 받는 해시테이블은 리스트를 받도록 하면 같은 이름의 다른 노드 받을 수 있다
    //timeout 2 java Subway $(<testset/argument/$i.txt) < testset/input/$i.txt > my_output/$i.txt
    public static void main(String args[]) throws IOException
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        HashMap<String, NodeLinkedList> nodeHashMap = new HashMap<>();    //이름으로 구볗하는 해시테이블
        ArrayList<StationNode> stationNodes = new ArrayList<>();
        File file = new File(args[0]);
        if(file.exists())
            stationNodes = readFile(args[0], nodeHashMap);
        try {
            String input = br.readLine();
            while(!input.equals("QUIT")) {
                command(input, stationNodes, nodeHashMap);
                input = br.readLine();
            }
        }catch (IOException e)
        {
            System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
        }
    }

    private static ArrayList<StationNode> readFile(String input,
                                                   HashMap<String, NodeLinkedList> nodeHashMap){
        final String STATION_REGEX = "(?<id>[\\S]+) (?<name>[\\S]+) (?<line>[\\S]+)";
        final String EDGE_WEIGHT_REGEX = "(?<id1>[\\S]+) (?<id2>[\\S]+) (?<distance>[0-9]+)";
        HashMap<String, StationNode> idHashMap = new HashMap<>();          //고유 번호로 구별하는 해시테이블
        ArrayList<StationNode> stationNodes = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(new File(input));
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";
            while (!(line = bufferedReader.readLine()).matches("\\s*")) {
                //한 줄씩 읽은 결과 line에 저장된 상태
                Pattern p = Pattern.compile(STATION_REGEX);
                Matcher m = p.matcher(line);
                String id = "";
                String name = "";
                String newSubLine = "";
                StationNode newNode;
                if(m.find()){
                    id = m.group("id");
                    name = m.group("name");
                    newSubLine = m.group("line");
                }
                newNode = new StationNode(id, name, newSubLine);
                if(nodeHashMap.containsKey(name)){    //환승역
                    NodeLinkedList nodeList = nodeHashMap.get(name);
                    nodeList.addNode(newNode);
                }
                else
                    nodeHashMap.put(name,new NodeLinkedList(newNode));
                stationNodes.add(newNode);
                idHashMap.put(id, newNode);
            }
            while((line = bufferedReader.readLine()) != null){
                String id1;
                String id2;
                long distance;
                Pattern p =Pattern.compile(EDGE_WEIGHT_REGEX);
                Matcher m = p.matcher(line);
                if(m.find()){
                    id1 = m.group("id1");
                    id2 = m.group("id2");
                    distance = Long.parseLong(m.group("distance"));
                    if(idHashMap.containsKey(id1) && idHashMap.containsKey(id2)){
                        StationNode idNode1 = idHashMap.get(id1);
                        StationNode idNode2 = idHashMap.get(id2);
                        idNode1.getAdjacentNodes().add(idNode2, distance);
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return stationNodes;
    }
    private static void command(String input, ArrayList<StationNode> stationNodes,
                                HashMap<String, NodeLinkedList> nodeHashMap) throws NullPointerException {
        final String PATH_REGEX = "(?<start>[\\S]+) (?<end>[\\S]+)";
        try {
            Pattern p = Pattern.compile(PATH_REGEX);
            Matcher m = p.matcher(input);
            StringBuilder sb = new StringBuilder();
            long TotalDistance = Long.MAX_VALUE;
            if (m.find()) {
                String startStation = m.group("start"); //출발 역
                String endStation = m.group("end");     //도착 역
                if(!nodeHashMap.containsKey(startStation)){
                    System.out.println("해당 이름의 역이 없습니다.");
                    throw new Exception();
                }
                NodeLinkedList startNodeList = nodeHashMap.get(startStation);

                for(StationNode startNode = startNodeList.getHead(); startNode != null; startNode = startNode.getNext()) {
                    VertexHeap tempVertexHeap = new VertexHeap(stationNodes);
                    startNode.setDistance(0);   //시작 점은 거리 0
                    int rootLocation = stationNodes.indexOf(startNode);    //트리의 처음 노드를 시작 역으로
                    StationNode temp = stationNodes.get(rootLocation);
                    stationNodes.set(rootLocation, stationNodes.get(0));
                    stationNodes.set(0, temp);
                    int size = stationNodes.size();
                    for (int i = 0; i < size; i++) {
                        ArrayList<StationNode> b = new ArrayList<>(size - i);
                        b.addAll(stationNodes.subList(i, size));    //트리에서 지금까지 정리된 부분을 제외한 부분
                        StationNode node = b.get(0);
                        //System.out.println(i + "번째 노드 실행" + ", 역명: " + node.getName());
                        EdgeList stationGraph = node.getAdjacentNodes();    //node와 인접한 노드들을 향한 edge의 집합
                        for (int j = 0; j < stationGraph.getEdges().size(); j++) {
                            Edge edge = stationGraph.getEdges().get(j); //node와 인접한 edge
                            //System.out.println("node.getDistance(): " + node.getDistance() + ", edge.getWeight(): " + edge.getWeight());
                            long tempDistance = node.getDistance() + edge.getWeight();
                            //System.out.println("tempDistance:" + tempDistance);
                            //System.out.println("Name of endNode:" + edge.getEndNode().getName() + ", distance of edge:" + edge.getEndNode().getDistance());
                            if (edge.getEndNode().getDistance() > tempDistance) {
                                edge.getEndNode().setDistance(tempDistance);
                                edge.getEndNode().getAdjacentNodes().setNodeInPath(node);
                                //System.out.println(node.getId()+"->"+edge.getEndNode().getId());
                                //System.out.println("Name of endNode:" + edge.getEndNode().getName() + ", distance of node:" + edge.getEndNode().getDistance());
                                //System.out.println("b:" + b.get(0));
                            }
                        }
                        if (b.size()>1 && b.get(0).getDistance() != b.get(1).getDistance())
                            tempVertexHeap.DoHeapSort();
                    }
                    int k;
                    for (k = 0; k < stationNodes.size(); k++) {
                        if (stationNodes.get(k).getName().equals(endStation))
                            break;
                    }
                    if(TotalDistance <= stationNodes.get(k).getDistance())
                        continue;
                    sb = new StringBuilder();
                    TotalDistance = stationNodes.get(k).getDistance();
                    StationNode prevNode =stationNodes.get(k);
                    for (StationNode node = stationNodes.get(k);
                         node != null; node = node.getAdjacentNodes().getNodeInPath()) {
                        if (node.getLine().equals(prevNode.getLine()))
                            if (node.getDistance() != 0 &&
                                    node.getAdjacentNodes().getNodeInPath().getName().equals(node.getName())) {
                                sb.insert(0, "]");
                                sb.insert(0, node);
                                sb.insert(0, "[");
                                node = node.getAdjacentNodes().getNodeInPath();
                            } else
                                sb.insert(0, node);
                        sb.insert(0, " ");
                        prevNode = node;
                    }
                    if(sb.toString().trim().charAt(0)=='[')
                        continue;
                }
                System.out.println(sb.toString().trim());
                System.out.println(Long.toUnsignedString(TotalDistance));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}

