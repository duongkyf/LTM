/**
 * Đề 4: Phân vị 90 và số phần tử lớn hơn trung bình (cổng 2207, hai pha)
 * - Gửi: ;studentCode;qCode
 * - Nhận: requestId;data (data là dãy số phân cách bằng dấu phẩy)
 * - Xử lý: Sắp xếp tăng dần và lấy phân vị 90 tại ceil(n * 0.9) - 1. Đếm phần tử lớn hơn TBC của dãy ban đầu.
 * - Gửi lại: requestId;p90=<giá_trị>;aboveAvg=<n>
 */

import java.net.*;
import java.util.*;

public class Bai4 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2207;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode4"; // Thay qCode thực tế khi thi
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
        String[] numberStrs = parts[1].trim().split(",");

        ArrayList<Integer> list = new ArrayList<>();
        long sum = 0;
        for (String s : numberStrs) {
            int val = Integer.parseInt(s.trim());
            list.add(val);
            sum += val;
        }
        
        // Tính Average
        int n = list.size();
        double avg = (double) sum / n;
        
        // Sắp xếp để lấy p90
        Collections.sort(list);
        int p90Index = (int) Math.ceil(n * 0.9) - 1;
        int p90 = list.get(p90Index);

        // Đếm
        int aboveAvg = 0;
        for (int val : list) {
            if (val > avg) aboveAvg++;
        }

        // 4. Gửi kết quả
        String resultStr = requestId + ";p90=" + p90 + ";aboveAvg=" + aboveAvg;
        byte[] resultData = resultStr.getBytes();
        DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
        socket.send(resultPacket);
        
        socket.close();
    }
}