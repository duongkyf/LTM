/**
 * Bài 21: Max trong mỗi cửa sổ trượt kích thước k (cổng 2207)
 * - Gửi: ;studentCode;qCode
 * - Nhận: requestId;...;k;num1,num2,... (Tách bằng dấu ; và lấy k, mảng số)
 * - Xử lý: Tìm phần tử lớn nhất trong từng đoạn con liên tiếp kích thước k.
 * - Gửi lại: requestId;max1,max2,...
 */

import java.net.*;
import java.util.*;

public class Bai21 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2207;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode21"; 
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
        
        // Theo format trong UDP3DeFinal, k ở phần tử thứ 2, dãy số ở phần tử thứ 3
        int k = Integer.parseInt(parts[2].trim());
        String[] words = parts[3].trim().split(",");
        
        ArrayList<Integer> ds = new ArrayList<>();
        for (String x : words) {
            ds.add(Integer.parseInt(x.trim()));
        }
        
        StringBuilder res = new StringBuilder();
        for (int i = 0; i <= ds.size() - k; i++) {
            res.append(",").append(Collections.max(ds.subList(i, i + k)));
        }
        
        // 4. Gửi kết quả
        String resultStr = requestId + ";" + res.toString().replaceFirst(",", "");
        byte[] resultData = resultStr.getBytes();
        DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
        socket.send(resultPacket);
        
        socket.close();
    }
}