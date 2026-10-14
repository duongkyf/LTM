/**
 * Đề 17: Che log một dòng: EMAIL, PHONE, TOKEN (cổng 2208, hai pha)
 * - Gửi: ;studentCode;qCode
 * - Nhận: requestId;data
 * - Xử lý: Che email, số điện thoại và token theo quy tắc [EMAIL], [PHONE], token=[TOKEN].
 * - Gửi lại: requestId;answer
 */

import java.net.*;

public class Bai17 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2208;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode17"; 
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

        // Sử dụng Regex để che giấu giống với code gốc
        data = data.replaceAll("token=[^\\s|]+", "token=[TOKEN]");
        data = data.replaceAll("[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+", "[EMAIL]");
        data = data.replaceAll("\\b\\d{9,11}\\b", "[PHONE]");

        // Hoặc có thể sử dụng Regex đơn giản (nếu dữ liệu chuẩn mực như De13):
        // data = data.replaceAll("email=\\S+", "email=[EMAIL]")
        //            .replaceAll("phone=\\S+", "phone=[PHONE]")
        //            .replaceAll("token=\\S+", "token=[TOKEN]");

        // 4. Gửi kết quả
        String resultStr = requestId + ";" + data;
        byte[] resultData = resultStr.getBytes("UTF-8");
        DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
        socket.send(resultPacket);
        
        socket.close();
    }
}