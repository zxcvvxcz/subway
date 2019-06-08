import java.io.*;
import java.util.HashMap;
import java.util.regex.*;
import java.util.ArrayList;
public class Subway {
    public static void main(String[] args)
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        HashMap<String, NodeLinkedList> nodeHashMap = new HashMap<>();    //이름으로 구볗하는 해시테이블
        ArrayList<StationNode> stationNodes = new ArrayList<>();    //heap sort에 활용할 arraylist
        File file = new File(args[0]);  //지하철 노선 데이터
        if(file.exists())
            stationNodes = readFile(args[0], nodeHashMap);  //지하철의 각 역에 대한 정보를 stationNodes에 저장
        try {
            String input = br.readLine();
            while(!input.equals("QUIT")) {
                command(input, stationNodes, nodeHashMap);  //출발역과 도착역 정보를 바탕으로 경로 탐색 및 출력
                input = br.readLine();
            }
        }catch (IOException e)
        {
            System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
        }
    }

    private static ArrayList<StationNode> readFile(String input,
                                                   HashMap<String, NodeLinkedList> nodeHashMap){
        final String STATION_REGEX = "(?<id>[\\S]+) (?<name>[\\S]+) (?<line>[\\S]+)";   //역 정보에 대한 정규식
        final String EDGE_WEIGHT_REGEX = "(?<id1>[\\S]+) (?<id2>[\\S]+) (?<distance>[0-9]+)";   //역 사이 거리 정규식
        HashMap<String, StationNode> idHashMap = new HashMap<>();          //고유 번호로 구별하는 해시테이블
        ArrayList<StationNode> stationNodes = new ArrayList<>();    //전체 노드를 담는 arraylist
        try {
            FileReader fileReader = new FileReader(new File(input));
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while (!(line = bufferedReader.readLine()).matches("\\s*")) {   //역에 대한 정보가 입력될 때
                Pattern p = Pattern.compile(STATION_REGEX);
                Matcher m = p.matcher(line);
                String id = "";
                String name = "";
                String newSubLine = "";
                StationNode newNode;
                if(m.find()){   //입력이 적절한 형태이면
                    id = m.group("id");
                    name = m.group("name");
                    newSubLine = m.group("line");
                }
                newNode = new StationNode(id, name, newSubLine);    //고유 번호, 역명, 호선 정보를 담은 노드 생성
                if(nodeHashMap.containsKey(name)){    //환승 가능한 역이면 다른 환승 가능한 역의 리스트에 생성된 노드 추가
                    NodeLinkedList nodeList = nodeHashMap.get(name);
                    nodeList.addNode(newNode);
                }
                else    //이름이 같은 역이 아직 검색되지 않았다면 리스트 생성
                    nodeHashMap.put(name,new NodeLinkedList(newNode));
                stationNodes.add(newNode);  //arraylist에 노드 추가
                idHashMap.put(id, newNode); //고유 번호 해시테이블에 노드 추가
            }
            while((line = bufferedReader.readLine()) != null){  // 인접한 역 사이 거리가 입력될 때
                String id1;
                String id2;
                int distance;
                Pattern p = Pattern.compile(EDGE_WEIGHT_REGEX); //역 사이 거리에 대한 정규식 EDGE_WEIGHT_REGEX
                Matcher m = p.matcher(line);
                if(m.find()){
                    id1 = m.group("id1");
                    id2 = m.group("id2");
                    distance = Integer.parseInt(m.group("distance"));
                    if(idHashMap.containsKey(id1) && idHashMap.containsKey(id2)){   //실제로 존재하는 역들이면
                        StationNode idNode1 = idHashMap.get(id1);
                        StationNode idNode2 = idHashMap.get(id2);
                        idNode1.getAdjacentNodes().add(idNode2, distance);  //두 역 사이에 weight가 distance인 edge 추가
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
            long TotalDistance = Long.MAX_VALUE;    //전체 경로 길이 저장할 변수
            if (m.find()) {
                String startStation = m.group("start"); //출발 역 이름
                String endStation = m.group("end");     //도착 역 이름
                if(!nodeHashMap.containsKey(startStation)){     //데이터에 없는 역을 검색할 때
                    System.out.println("해당 이름의 역이 없습니다.");
                    throw new Exception();
                }
                NodeLinkedList startNodeList = nodeHashMap.get(startStation);   //출발 역 리스트
                //출발 역에서 출발하는 호선에 따라 모든 최단 경로를 탐색하고, 가장 짧은 경로만 출력
                for(StationNode startNode = startNodeList.getHead(); startNode != null; startNode = startNode.getNext())
                {
                    VertexHeap tempVertexHeap = new VertexHeap(stationNodes);
                    //전체 역 노드 간 거리와 최단 거리로 표시된 edge를 초기화한뒤 힙으로 사용
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
                        EdgeList stationGraph = node.getAdjacentNodes();    //node와 인접한 노드들을 향한 edge의 집합
                        for (int j = 0; j < stationGraph.getEdges().size(); j++) {
                            Edge edge = stationGraph.getEdges().get(j); //node와 인접한 edge
                            long tempDistance = node.getDistance() + edge.getWeight();
                            if (edge.getEndNode().getDistance() > tempDistance) {
                                //기존에 저장되어있던 거리가 더 크면 거리를 갱신하고 거리를 줄여주는 edge를 표시
                                edge.getEndNode().setDistance(tempDistance);
                                edge.getEndNode().getAdjacentNodes().setNodeInPath(node);
                            }
                        }
                        if (b.size()>1 && b.get(0).getDistance() != b.get(1).getDistance())
                            //힙 소트에 시간이 많이 걸리고 힙 소트를 하고 나면 거리 값이 같은 노드 간 순서가 바뀔 수 있으므로
                            //기준 노드와 기준 노드의 다음 노드와 거리 값이 다를 경우에만 힙 소트 실행
                            tempVertexHeap.DoHeapSort();
                    }
                    StationNode finalNode = stationNodes.get(0);    //도착 역 노드 finalNode
                    for (int k = 0; k < stationNodes.size(); k++)
                        if (stationNodes.get(k).getName().equals(endStation)) {
                            finalNode = stationNodes.get(k);
                            break;
                        }
                    if(TotalDistance <= finalNode.getDistance())    //이미 더 짧은 경로가 탐색되었으면 현재 경로 무시
                        continue;
                    sb = new StringBuilder();   //전체 경로 출력을 위한 StringBuilder
                    TotalDistance = finalNode.getDistance();    //더 짧은 거리를 TotalDistance에 저장
                    for (StationNode node = finalNode; node != null;
                         node = node.getAdjacentNodes().getNodeInPath()) {
                            if (node.getDistance() != 0 &&
                                    node.getAdjacentNodes().getNodeInPath().getName().equals(node.getName())) {
                                // 환승일 때
                                sb.insert(0, "]");
                                sb.insert(0, node);
                                sb.insert(0, "[");
                                node = node.getAdjacentNodes().getNodeInPath(); //같은 역 이름을 다시 출력하지 않기 위함
                            } else  //환승이 아닐 때
                                sb.insert(0, node);
                        sb.insert(0, " ");
                    }
                }
                System.out.println(sb.toString().trim());   //왼쪽에 공백이 있으므로 trim 사용해 없앰
                System.out.println(Long.toUnsignedString(TotalDistance));
                //거리가 너무 길어져서 생기는 오버플로우를 막기 위해 unsigned long형으로 출력
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}

