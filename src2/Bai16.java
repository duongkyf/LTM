/**
 * Đề 16: Tìm trung vị (cổng 2207, hai pha)
 * - Gửi: ;studentCode;qCode
 * - Nhận: requestId;data (ví dụ: SjkZFpBk;268,125,584,33,685,530,661...)
 * - Xử lý: Sắp xếp dãy số tăng dần và tìm giá trị trung vị. Nếu số lượng phần tử chẵn, 
 *          lấy trung bình hai phần tử giữa và làm tròn 02 chữ số thập phân.
 * - Gửi lại: requestId;answer
 */

import java.net.*;
import java.util.*;

public class Bai16 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2207;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode16"; 
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
        
        List<Integer> list = new ArrayList<>();
        for (String s : numStrs) {
            list.add(Integer.parseInt(s.trim()));
        }
        Collections.sort(list);
        
        int n = list.size();
        String answer = "";
        if (n % 2 != 0) {
            answer = String.valueOf(list.get(n / 2));
        } else {
            double median = (list.get(n / 2 - 1) + list.get(n / 2)) / 2.0;
            // Làm tròn 2 chữ số thập phân
            median = Math.round(median * 100.0) / 100.0; 
            // Nếu là số nguyên thì in ra dạng số nguyên (bỏ .0)
            if (median == (long) median) {
                answer = String.valueOf((long) median);
            } else {
                answer = String.valueOf(median);
            }
        }

        // 4. Gửi kết quả
        String resultStr = requestId + ";" + answer;
        byte[] resultData = resultStr.getBytes();
        DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
        socket.send(resultPacket);
        
        socket.close();
    }
}