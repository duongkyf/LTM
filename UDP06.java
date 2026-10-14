import UDP.Customer;

import java.io.*;
import java.net.*;

public class UDP06 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("36.50.135.242");
        int port = 2209;

        // 1. Gửi
        String sendStr = ";B23DCCN475;ifhDT8xe"; // Thay qCode thực tế khi thi
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
        Customer customer = (Customer) ois.readObject();

        // 3. Xử lý dữ liệu
        String[] words = customer.getName().trim().toLowerCase().split("\\s+");
        if (words.length > 0) {
            // Tạo userName: chữ cái đầu của họ và tên đệm + tên (in thường)
            StringBuilder userName = new StringBuilder();
            for (int i = 0; i < words.length - 1; i++) {
                if (words[i].length() > 0) {
                    userName.append(words[i].charAt(0));
                }
            }
            userName.append(words[words.length - 1]);
            customer.setUserName(userName.toString());

            // Chuẩn hóa name: "TÊN, Họ Và Tên Đệm"
            String ten = words[words.length - 1].toUpperCase();
            StringBuilder hoDem = new StringBuilder();
            for (int i = 0; i < words.length - 1; i++) {
                if (words[i].length() > 0) {
                    hoDem.append(Character.toUpperCase(words[i].charAt(0)))
                            .append(words[i].substring(1))
                            .append(" ");
                }
            }
            customer.setName(ten + ", " + hoDem.toString().trim());
        }

        // Chuyển ngày sinh mm-dd-yyyy -> dd/mm/yyyy
        String[] dateParts = customer.getDayOfBirth().trim().split("-");
        if (dateParts.length == 3) {
            customer.setDayOfBirth(dateParts[1] + "/" + dateParts[0] + "/" + dateParts[2]);
        }

        // 4. Gửi kết quả
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(customer);
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

//package UDP;
//
//import java.io.Serializable;
//
//public class Customer implements Serializable {
//    private static final long serialVersionUID = 20151107L;
//    private String id;
//    private String code;
//    private String name;
//    private String dayOfBirth;
//    private String userName;
//
//    public Customer(String id, String code, String name, String dayOfBirth, String userName) {
//        this.id = id;
//        this.code = code;
//        this.name = name;
//        this.dayOfBirth = dayOfBirth;
//        this.userName = userName;
//    }
//
//    public String getId() { return id; }
//    public void setId(String id) { this.id = id; }
//    public String getCode() { return code; }
//    public void setCode(String code) { this.code = code; }
//    public String getName() { return name; }
//    public void setName(String name) { this.name = name; }
//    public String getDayOfBirth() { return dayOfBirth; }
//    public void setDayOfBirth(String dayOfBirth) { this.dayOfBirth = dayOfBirth; }
//    public String getUserName() { return userName; }
//    public void setUserName(String userName) { this.userName = userName; }
//}
