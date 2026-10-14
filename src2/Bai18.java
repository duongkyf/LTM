/**
 * Đề 18: Tính checksum (cổng 2207, hai pha)
 * - Gửi: ;studentCode;qCode
 * - Nhận: requestId;data
 * - Xử lý: Tính checksum theo công thức sum((i + 1) * value[i]) mod 100000, với i bắt đầu từ 0.
 * - Gửi lại: requestId;answer
 */

import java.net.*;

public class Bai18 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2207;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode18"; 
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
        String[] numStrs = parts[1].trim().split(",");
        
        long checksum = 0;
        for (int i = 0; i < numStrs.length; i++) {
            long value = Long.parseLong(numStrs[i].trim());
            checksum += (i + 1) * value;
        }
        checksum = checksum % 100000;

        // 4. Gửi kết quả
        String resultStr = requestId + ";" + checksum;
        byte[] resultData = resultStr.getBytes();
        DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
        socket.send(resultPacket);
        
        socket.close();
    }
}