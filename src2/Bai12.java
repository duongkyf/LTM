/**
 * Đề 12: Cộng hai số nhị phân, đổi ra thập phân (cổng 2208)
 * - Gửi: ;studentCode;qCode
 * - Nhận: requestId;b1,b2 (b1, b2 là chuỗi nhị phân)
 * - Xử lý: Tính tổng 2 số nhị phân và đổi ra số thập phân (hệ cơ số 10).
 * - Gửi lại: requestId;sum
 */

import java.net.*;

public class Bai12 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2208;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode12"; 
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
        String[] binaryStrs = parts[1].split(",");
        
        // Parse nhị phân (hệ cơ số 2) sang Long và tính tổng
        long sum = Long.parseLong(binaryStrs[0].trim(), 2) + Long.parseLong(binaryStrs[1].trim(), 2);

        // 4. Gửi kết quả
        String resultStr = requestId + ";" + sum;
        byte[] resultData = resultStr.getBytes();
        DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
        socket.send(resultPacket);
        
        socket.close();
    }
}