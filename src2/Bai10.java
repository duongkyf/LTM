/**
 * Đề 10: Tìm giá trị còn thiếu, dãy có thể trùng (cổng 2207)
 * - Gửi: ;studentCode;qCode
 * - Nhận: requestId;n;A1,A2,...Am (n <= 100, m <= n)
 * - Xử lý: Tìm các giá trị còn thiếu trong khoảng từ 1 đến n.
 * - Gửi lại: requestId;B1,B2,...,Bk
 */

import java.net.*;
import java.util.*;

public class Bai10 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2207;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode10"; 
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
        byte[] resultData = resultStr.getBytes();
        DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
        socket.send(resultPacket);
        
        socket.close();
    }
}