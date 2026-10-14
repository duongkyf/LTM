/**
 * Đề 6: Đoạn con tăng nghiêm ngặt dài nhất (cổng 2207, hai pha)
 * - Gửi: ;studentCode;qCode
 * - Nhận: requestId;data (ví dụ: Ef56Gh78;4,9,12,3,8)
 * - Xử lý: Tìm đoạn con liên tiếp tăng nghiêm ngặt dài nhất. Nếu có nhiều đoạn, chọn đoạn đầu tiên.
 * - Gửi lại: requestId;segment=<csv>;length=<n>;sum=<tổng>
 */

import java.net.*;
import java.util.*;

public class Bai6 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2207;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode6"; // Thay qCode thực tế khi thi
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
        
        int[] arr = new int[numStrs.length];
        for(int i = 0; i < numStrs.length; i++){
            arr[i] = Integer.parseInt(numStrs[i].trim());
        }

        int maxLength = 1, currentLength = 1;
        int maxStart = 0, currentStart = 0;

        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > arr[i - 1]) {
                currentLength++;
            } else {
                if (currentLength > maxLength) {
                    maxLength = currentLength;
                    maxStart = currentStart;
                }
                currentLength = 1;
                currentStart = i;
            }
        }
        // Kiểm tra đoạn cuối cùng
        if (currentLength > maxLength) {
            maxLength = currentLength;
            maxStart = currentStart;
        }

        long sum = 0;
        List<String> segment = new ArrayList<>();
        for (int i = maxStart; i < maxStart + maxLength; i++) {
            sum += arr[i];
            segment.add(String.valueOf(arr[i]));
        }

        // 4. Gửi kết quả
        String resultStr = requestId + ";segment=" + String.join(",", segment) + ";length=" + maxLength + ";sum=" + sum;
        byte[] resultData = resultStr.getBytes();
        DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
        socket.send(resultPacket);
        
        socket.close();
    }
}