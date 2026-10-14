/**
 * Đề 7: Loại ký tự đặc biệt, số, ký tự trùng (cổng 2208)
 * - Gửi: ;studentCode;qCode
 * - Nhận: requestId;strInput
 * - Xử lý: Loại bỏ ký tự đặc biệt, số, ký tự trùng và giữ nguyên thứ tự xuất hiện.
 * - Gửi lại: requestId;strOutput
 */

import java.net.*;
import java.util.*;

public class Bai7 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2208;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode7"; // Thay qCode thực tế khi thi
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
        String strInput = parts[1].trim();

        // Dùng LinkedHashSet để giữ nguyên thứ tự và loại bỏ ký tự trùng
        LinkedHashSet<Character> set = new LinkedHashSet<>();
        for (char c : strInput.toCharArray()) {
            if (Character.isLetter(c)) { // Chỉ lấy chữ cái
                set.add(c);
            }
        }

        StringBuilder strOutput = new StringBuilder();
        for (Character c : set) {
            strOutput.append(c);
        }

        // 4. Gửi kết quả
        String resultStr = requestId + ";" + strOutput.toString();
        byte[] resultData = resultStr.getBytes("UTF-8");
        DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
        socket.send(resultPacket);
        
        socket.close();
    }
}