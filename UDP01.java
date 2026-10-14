import java.net.*;
import java.util.*;

public class UDP01 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("36.50.135.242");
        int port = 2207;

        // 1. Gửi
        String sendStr = ";B23DCCN475;mQ1srY7R"; // Thay qCode thực tế khi thi
        byte[] sendData = sendStr.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, port);
        socket.send(sendPacket);

        // 2. Nhận
        byte[] receiveData = new byte[4096];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        String receiveStr = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");

        // 3. Xử lý
        String[] parts = receiveStr.trim().split(";");
        String requestId = parts[0];
        int n = Integer.parseInt(parts[1].trim());
        String[] arrStrs = parts[2].trim().split(",");

        Set<Integer> presentSet = new HashSet<>();
        for (String s : arrStrs) {
            presentSet.add(Integer.parseInt(s.trim()));
        }

        List<String> missing = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            if (!presentSet.contains(i)) {
                missing.add(String.valueOf(i));
            }
        }

        // 4. Gửi kết quả
        String resultStr = requestId + ";" + String.join(",", missing);
        byte[] resultData = resultStr.getBytes("UTF-8");
        DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
        socket.send(resultPacket);

        socket.close();
    }
}