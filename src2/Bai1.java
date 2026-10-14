/**
 * Đề 1: Loại ký tự của str1 xuất hiện trong str2 (cổng 2208)
 * - Gửi: ;studentCode;qCode
 * - Nhận: requestId;str1;str2
 * - Xử lý: Loại bỏ các ký tự trong chuỗi thứ nhất mà xuất hiện trong chuỗi thứ hai, giữ nguyên thứ tự xuất hiện.
 * - Gửi lại: requestId;strOutput
 */

import java.net.*;

public class Bai1 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2208;

        // 1. Gửi thông điệp
        String studentCode = "B23DCCN643";
        String qCode = "qCode1"; // Thay qCode thực tế khi thi
        String sendStr = ";" + studentCode + ";" + qCode;
        byte[] sendData = sendStr.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, port);
        socket.send(sendPacket);

        // 2. Nhận thông điệp
        byte[] receiveData = new byte[4096];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);
        String receiveStr = new String(receivePacket.getData(), 0, receivePacket.getLength(), "UTF-8");

        // 3. Xử lý dữ liệu
        String[] parts = receiveStr.trim().split(";");
        if (parts.length >= 3) {
            String requestId = parts[0];
            String str1 = parts[1];
            String str2 = parts[2];

            StringBuilder strOutput = new StringBuilder();
            for (int i = 0; i < str1.length(); i++) {
                char c = str1.charAt(i);
                // Kiểm tra xem ký tự c có xuất hiện trong str2 hay không
                if (str2.indexOf(c) == -1) { 
                    strOutput.append(c);
                }
            }

            // 4. Gửi kết quả
            String resultStr = requestId + ";" + strOutput.toString();
            byte[] resultData = resultStr.getBytes("UTF-8");
            DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
            socket.send(resultPacket);
        }
        
        socket.close();
    }
}