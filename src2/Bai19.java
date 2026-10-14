/**
 * Đề 19: Chuẩn hóa chuỗi thành dạng gạch ngang (cổng 2208, hai pha)
 * - Gửi: ;studentCode;qCode
 * - Nhận: requestId;data
 * - Xử lý: Chuyển chuỗi về chữ thường, loại bỏ dấu câu, gom nhiều khoảng trắng 
 *          thành một khoảng trắng và thay khoảng trắng bằng dấu gạch ngang.
 * - Gửi lại: requestId;answer
 */

import java.net.*;

public class Bai19 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2208;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode19"; 
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

        // Chuyển chữ thường
        data = data.toLowerCase();
        // Loại bỏ dấu câu (Regex: \p{Punct} đại diện cho các dấu câu trong Java)
        data = data.replaceAll("\\p{Punct}", "");
        // Gom khoảng trắng và thay bằng gạch ngang
        data = data.replaceAll("\\s+", "-");

        // 4. Gửi kết quả
        String resultStr = requestId + ";" + data;
        byte[] resultData = resultStr.getBytes("UTF-8");
        DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
        socket.send(resultPacket);
        
        socket.close();
    }
}