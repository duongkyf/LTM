/**
 * Đề 8: Employee, chuẩn hóa name, tăng lương x%, đổi hireDate (cổng 2209)
 * - Gửi: ;studentCode;qCode
 * - Nhận: 8 byte requestId, đối tượng Employee
 * - Xử lý: Chuẩn hóa name (Viết hoa chữ đầu mỗi từ). Tăng salary thêm x% (x = tổng các chữ số của năm). 
 *          Chuyển hireDate sang dd/mm/yyyy.
 * - Gửi lại: 8 byte requestId, đối tượng Employee đã cập nhật.
 */

import java.io.*;
import java.net.*;

public class Bai8 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2209;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode8"; 
        byte[] sendData = sendStr.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, port);
        socket.send(sendPacket);

        // 2. Nhận
        byte[] receiveData = new byte[4096];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);

        String requestId = new String(receivePacket.getData(), 0, 8);

        ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData(), 8, receivePacket.getLength() - 8);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Employee employee = (Employee) ois.readObject();

        // 3. Xử lý dữ liệu
        String[] words = employee.getName().trim().toLowerCase().split("\\s+");
        StringBuilder normalizedName = new StringBuilder();
        for (String w : words) {
            if (w.length() > 0) {
                normalizedName.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
            }
        }
        employee.setName(normalizedName.toString().trim());

        String hireDate = employee.getHireDate(); 
        String[] dateParts = hireDate.split("-");
        String yearStr = dateParts[0];
        int x = 0;
        for (char c : yearStr.toCharArray()) {
            x += (c - '0');
        }
        double newSalary = employee.getSalary() * (1 + (double) x / 100);
        newSalary = Math.round(newSalary * 100.0) / 100.0;
        employee.setSalary(newSalary);

        String newDate = dateParts[2] + "/" + dateParts[1] + "/" + dateParts[0];
        employee.setHireDate(newDate);

        // 4. Gửi kết quả
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(employee);
        oos.flush();
        byte[] objData = baos.toByteArray();

        byte[] reqBytes = requestId.getBytes();
        byte[] resultData = new byte[8 + objData.length];
        System.arraycopy(reqBytes, 0, resultData, 0, 8);
        System.arraycopy(objData, 0, resultData, 8, objData.length);

        DatagramPacket resultPacket = new DatagramPacket(resultData, resultData.length, receivePacket.getAddress(), receivePacket.getPort());
        socket.send(resultPacket);
        
        socket.close();
    }
}

class Employee implements Serializable {
    private static final long serialVersionUID = 20261107L;
    private String id;
    private String name;
    private double salary;
    private String hireDate;

    public Employee(String id, String name, double salary, String hireDate) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.hireDate = hireDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }
    public String getHireDate() { return hireDate; }
    public void setHireDate(String hireDate) { this.hireDate = hireDate; }
}