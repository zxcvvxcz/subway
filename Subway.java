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
        HashMap<String, StationNode> idHashMap = new HashMap<>();          //고유 번호로 구별하는 해시테이블
        ArrayList<StationNode> stationNodes = new ArrayList<>();    //전체 노드를 담는 arraylist
        try {
            FileReader fileReader = new FileReader(new File(input));
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while (!(line = bufferedReader.readLine()).matches("\\s*")) {//공백 입력 전까지 역에 대한 정보가 입력
                StationInfo(line,nodeHashMap,stationNodes,idHashMap);
            }
            while((line = bufferedReader.readLine()) != null){  // 인접한 역 사이 거리가 입력될 때
                SetEdges(line,idHashMap);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return stationNodes;
    }

    //경로 탐색 및 출력의 전체 과정
    private static void command(String input, ArrayList<StationNode> stationNodes,
                                HashMap<String, NodeLinkedList> nodeHashMap){
        final String PATH_REGEX = "(?<start>[\\S]+) (?<end>[\\S]+)";
        try {
            Pattern p = Pattern.compile(PATH_REGEX);
            Matcher m = p.matcher(input);
            if (m.find()) {
                String startStation = m.group("start"); //출발 역 이름
                String endStation = m.group("end");     //도착 역 이름
                if(!nodeHashMap.containsKey(startStation)){     //데이터에 없는 역을 검색하면 오류
                    System.out.println("해당 이름의 역이 없습니다.");
                    throw new Exception();
                }
                //출발 역에서 출발하는 호선에 따른 최단 경로를 모두 탐색하고, 가장 짧은 경로만 출력
                ShortestPathSearch(nodeHashMap, stationNodes, startStation, endStation);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    //역 정보 입력 메소드
    private static void StationInfo(String line,HashMap<String, NodeLinkedList> nodeHashMap,
                                    ArrayList<StationNode> stationNodes, HashMap<String, StationNode> idHashMap){
        final String STATION_REGEX = "(?<id>[\\S]+) (?<name>[\\S]+) (?<line>[\\S]+)";   //역 정보에 대한 정규식
        Pattern p = Pattern.compile(STATION_REGEX);
        Matcher m = p.matcher(line);
        if(m.find()) {   //입력이 적절한 형태이면
            String id = m.group("id");
            String name = m.group("name");
            String newSubLine = m.group("line");
            StationNode newNode = new StationNode(id, name, newSubLine);
            //고유 번호, 역명, 호선 정보를 담은 노드 생성

            if (nodeHashMap.containsKey(name)) {
                //환승 가능한 역이면 해당 역과 이름이 같은 역 리스트에 생성된 노드 추가
                NodeLinkedList nodeList = nodeHashMap.get(name);
                nodeList.addNode(newNode);
            } else    //이름이 같은 역이 아직 입력되지 않았다면 리스트 생성
                nodeHashMap.put(name, new NodeLinkedList(newNode));
            stationNodes.add(newNode);  //arraylist에 노드 추가
            idHashMap.put(id, newNode); //고유 번호 해시테이블에 노드 추가
        }
    }
    private static void SetEdges(String line, HashMap<String, StationNode> idHashMap){
        final String EDGE_WEIGHT_REGEX = "(?<id1>[\\S]+) (?<id2>[\\S]+) (?<distance>[0-9]+)";   //역 사이 거리 정규식
        Pattern p = Pattern.compile(EDGE_WEIGHT_REGEX);
        Matcher m = p.matcher(line);
        if(m.find()){
            String id1 = m.group("id1");
            String id2 = m.group("id2");
            int distance = Integer.parseInt(m.group("distance"));
            if(idHashMap.containsKey(id1) && idHashMap.containsKey(id2)){   //실제로 존재하는 역들이면
                StationNode idNode1 = idHashMap.get(id1);
                StationNode idNode2 = idHashMap.get(id2);
                idNode1.getAdjacentNodes().add(idNode2, distance);  //두 역 사이에 weight가 distance인 edge 추가
            }
        }
    }
    //최단 경로를 탐색하고 출력하는 메소드
    private static void ShortestPathSearch(HashMap<String, NodeLinkedList> nodeHashMap,
                                           ArrayList<StationNode> stationNodes,
                                           String startStation, String endStation) {
        String path = "";
        int size = stationNodes.size();
        NodeLinkedList startNodeList = nodeHashMap.get(startStation);   //출발 역 리스트
        NodeLinkedList finalNodeList = nodeHashMap.get(endStation); //종착 역 리스트
        NodeLinkedList cloneList = startNodeList.clone();   //원본 유지를 위해 사본 사용
        long TotalDistance = Long.MAX_VALUE;    //전체 경로 길이 저장할 변수
        for (StationNode startNode = cloneList.getHead(); startNode != null; startNode = startNode.getNext()) {
            ArrayList<StationNode> cloneNodes = (ArrayList<StationNode>) stationNodes.clone();
            //원본 유지를 위해 사본 사용
            VertexHeap tempVertexHeap = new VertexHeap(cloneNodes);
            int checkedFinalNodes = 0;  //종착 역에 대한 경로 탐색이 끝났는지 확인하기 위한 변수
            //전체 역 노드 간 거리와 최단 거리로 표시된 edge를 초기화한뒤 힙으로 사용
            int rootLocation = cloneNodes.indexOf(startNode);    //트리의 처음 노드를 시작 역으로
            StationNode temp = cloneNodes.get(rootLocation);
            temp.setDistance(0);//시작 점은 거리 0
            cloneNodes.set(rootLocation, cloneNodes.get(0));
            cloneNodes.set(0, temp);
            for (int i = 0; i < size; i++) {
                StationNode node = tempVertexHeap.heapDelete();     //인접 노드를 탐색하기 전 힙에서 해당 노드 제거
                EdgeList stationGraph = node.getAdjacentNodes();    //node와 인접한 노드들을 향한 edge의 집합
                for (int j = 0; j < stationGraph.getEdges().size(); j++) {
                    Edge edge = stationGraph.getEdges().get(j);     //node와 인접한 edge
                    long tempDistance = node.getDistance() + edge.getWeight();
                    if (edge.getEndNode().getDistance() > tempDistance) {
                        //기존에 저장되어있던 거리가 더 크면 거리를 갱신하고 거리를 줄여주는 edge를 표시
                        edge.getEndNode().setDistance(tempDistance);
                        edge.getEndNode().getAdjacentNodes().setNodeInPath(node);
                        //거리 갱신 후 거리 값에 맞게 정렬
                        tempVertexHeap.PercolateUp(cloneNodes.indexOf(edge.getEndNode()));
                    }
                }
                //종착역의 모든 호선에 대해 최단 경로를 찾았으면 경로 탐색 종료
                if (node.getName().equals(endStation)) {
                    checkedFinalNodes++;
                    if (checkedFinalNodes == finalNodeList.getSize())
                        break;
                }
            }
            //종착 역 리스트에서 가장 일찍 도착할 수 있는 경우 찾기
            StationNode finalNode = finalNodeList.getHead();
            long finalDistance = Long.MAX_VALUE;
            for (StationNode stationNode = finalNode; stationNode != null;
                 stationNode = stationNode.getNext()) {
                if (finalDistance > stationNode.getDistance()) {
                    finalNode = stationNode;
                    finalDistance = stationNode.getDistance();
                }
            }
            if (TotalDistance <= finalDistance)    //이미 더 짧은 경로가 탐색되었으면 현재 경로 무시
                continue;
            TotalDistance = finalDistance;    //더 짧은 거리를 TotalDistance에 저장
            path = PrintPath(finalNode);    //지금까지 얻은 최단 경로 중 짧은 값을 저장
        }
        System.out.println(path);   //경로 안의 역 이름 출력
        System.out.println(Long.toUnsignedString(TotalDistance));
        //거리가 너무 길어져서 생기는 오버플로우를 막기 위해 총 거리는 unsigned long형으로 출력
    }

    //전체 경로의 문자열을 반환
    private static String PrintPath(StationNode finalNode){
        StringBuilder sb = new StringBuilder();   //전체 경로 출력을 위한 StringBuilder
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
        return sb.toString().trim();    //왼쪽 끝의 공백 제거
    }
}
