import java.net.*;
import java.util.*;

public class UDP03 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2208;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode25"; // Thay qCode thực tế khi thi
        byte[] sendData = sendStr.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, port);
        socket.send(sendPacket);

        // 2. Nhận
        byte[] receiveData = new byte[4096];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        String receiveStr = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");

        // 3. Xử lý
        String[] parts = receiveStr.trim().split(";", 2);
        String requestId = parts[0];
        String data = parts[1].trim();

        // Dùng LinkedHashMap để lưu tần suất theo đúng thứ tự xuất hiện đầu tiên
        Map<Character, Integer> freqMap = new LinkedHashMap<>();
        for (int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        char maxChar = data.charAt(0);
        int maxCount = -1;
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                maxChar = entry.getKey();
            }
        }

        // Tìm các vị trí xuất hiện (1-based index), mỗi vị trí cách nhau bởi dấu phẩy và có dấu phẩy ở cuối
        StringBuilder positions = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == maxChar) {
                positions.append(i + 1).append(",");
            }
        }

        // 4. Gửi kết quả
        String resultStr = requestId + ";" + maxChar + ":" + positions.toString();
        byte[] resultData = resultStr.getBytes("UTF-8");
        DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
        socket.send(resultPacket);

        socket.close();
    }
}