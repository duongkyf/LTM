/**
 * Đề 13: Che log và đếm ERROR/INFO/WARN (cổng 2208, hai pha)
 * - Gửi: ;studentCode;qCode
 * - Nhận: requestId;data (nhiều dòng log nối bằng ||)
 * - Xử lý: Che email, số điện thoại, token. Đếm dòng bắt đầu bằng ERROR, INFO, WARN.
 * - Gửi lại: requestId;maskedLog##ERROR=n;INFO=n;WARN=n
 */

import java.net.*;

public class Bai13 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2208;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode13"; 
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
        String[] lines = parts[1].trim().split("\\|\\|");
        
        int error = 0, info = 0, warn = 0;
        StringBuilder maskedLog = new StringBuilder();

        for (String line : lines) {
            if (line.startsWith("ERROR")) error++;
            else if (line.startsWith("INFO")) info++;
            else if (line.startsWith("WARN")) warn++;

            // Dùng Regex để che giấu thông tin
            line = line.replaceAll("email=\\S+", "email=[EMAIL]")
                       .replaceAll("phone=\\S+", "phone=[PHONE]")
                       .replaceAll("token=\\S+", "token=[TOKEN]");
            
            maskedLog.append("||").append(line);
        }
        
        String finalMasked = maskedLog.toString().replaceFirst("\\|\\|", "");
        String resultStr = requestId + ";" + finalMasked + "##ERROR=" + error + ";INFO=" + info + ";WARN=" + warn;

        // 4. Gửi kết quả
        byte[] resultData = resultStr.getBytes("UTF-8");
        DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
        socket.send(resultPacket);
        
        socket.close();
    }
}