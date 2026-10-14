/**
 * Đề 20: Số lớn thứ hai và số nhỏ thứ hai trong 50 số (cổng 2207)
 * - Gửi: ;studentCode;qCode
 * - Nhận: requestId;z1,z2,...,z50
 * - Xử lý: Tìm số lớn thứ hai và số nhỏ thứ hai của thông điệp.
 * - Gửi lại: requestId;secondMax,secondMin
 */

import java.net.*;
import java.util.*;

public class Bai20 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2207;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode20"; 
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
        
        // Dùng TreeSet để lọc trùng lặp và tự động sắp xếp
        TreeSet<Integer> st = new TreeSet<>();
        for (String x : numStrs) {
            st.add(Integer.parseInt(x.trim()));
        }
        
        // lower(last()): lấy số bé hơn trực tiếp phần tử lớn nhất -> Max thứ 2
        int secondMax = st.lower(st.last());
        // higher(first()): lấy số lớn hơn trực tiếp phần tử bé nhất -> Min thứ 2
        int secondMin = st.higher(st.first());

        // 4. Gửi kết quả
        String resultStr = requestId + ";" + secondMax + "," + secondMin;
        byte[] resultData = resultStr.getBytes();
        DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
        socket.send(resultPacket);
        
        socket.close();
    }
}