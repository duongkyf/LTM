/**
 * Bài 22: Tìm N số nguyên tố đầu tiên (cổng 2207)
 * - Gửi: ;studentCode;qCode
 * - Nhận: requestId;n
 * - Xử lý: Tìm n số nguyên tố đầu tiên.
 * - Gửi lại: requestId;p1,p2,...,pn
 */

import java.net.*;

public class Bai22 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2207;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode22"; 
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
        int n = Integer.parseInt(parts[1].trim());
        
        StringBuilder ds = new StringBuilder();
        int count = 0;
        int num = 2;
        
        while (count < n) {
            boolean isPrime = true;
            for (int i = 2; i * i <= num; i++) {
                if (num % i == 0) {
                    isPrime = false;
                    break;
                }
            }
            if (isPrime) {
                ds.append(",").append(num);
                count++;
            }
            num++;
        }
        
        // 4. Gửi kết quả
        String resultStr = requestId + ";" + ds.toString().replaceFirst(",", "");
        byte[] resultData = resultStr.getBytes();
        DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
        socket.send(resultPacket);
        
        socket.close();
    }
}