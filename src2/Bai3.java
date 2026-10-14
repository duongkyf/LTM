/**
 * Đề 3: Giải mã URL query string, sắp key (cổng 2208, hai pha)
 * - Gửi: ;studentCode;qCode
 * - Nhận: requestId;data (ví dụ: Qr12St34;role=risk%20team&user=alice)
 * - Xử lý: Giải mã URL encoding cho key và value, sau đó sắp xếp key tăng dần theo thứ tự từ điển.
 * - Gửi lại: requestId;key=value;key=value (ví dụ: Qr12St34;role=risk team;user=alice)
 */

import java.net.*;
import java.util.*;

public class Bai3 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2208;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode3"; // Thay qCode thực tế khi thi
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
        String data = parts[1];

        String[] pairs = data.trim().split("&");
        // Dùng TreeMap để tự động sắp xếp theo key
        TreeMap<String, String> map = new TreeMap<>();
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                map.put(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8"));
            }
        }

        StringBuilder resultStr = new StringBuilder(requestId);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            resultStr.append(";").append(entry.getKey()).append("=").append(entry.getValue());
        }

        // 4. Gửi kết quả
        byte[] resultData = resultStr.toString().getBytes("UTF-8");
        DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
        socket.send(resultPacket);
        
        socket.close();
    }
}