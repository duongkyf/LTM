/**
 * Đề 2: UDP.Product, sửa tên và số lượng bị đảo (cổng 2209)
 * - Gửi: ;studentCode;qCode
 * - Nhận: 8 byte requestId và đối tượng Product
 * - Xử lý: Tên sản phẩm bị đổi ngược từ đầu tiên và từ cuối cùng. Số lượng sản phẩm bị đảo ngược giá trị.
 * - Gửi lại: 8 byte requestId và đối tượng Product đã được sửa đổi
 */

import java.io.*;
import java.net.*;

public class Bai2 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2209;

        // 1. Gửi mã sinh viên
        String sendStr = ";B23DCCN643;qCode2"; // Thay qCode thực tế khi thi
        byte[] sendData = sendStr.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, port);
        socket.send(sendPacket);

        // 2. Nhận đối tượng
        byte[] receiveData = new byte[4096];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);

        // Trích xuất requestId từ 8 byte đầu
        String requestId = new String(receivePacket.getData(), 0, 8);

        // Deserialize Object Product
        ByteArrayInputStream bais = new ByteArrayInputStream(receivePacket.getData(), 8, receivePacket.getLength() - 8);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Product product = (Product) ois.readObject();

        // 3. Xử lý dữ liệu
        String name = product.getName().trim();
        String[] words = name.split("\\s+");
        if (words.length > 1) {
            String temp = words[0];
            words[0] = words[words.length - 1];
            words[words.length - 1] = temp;
            product.setName(String.join(" ", words));
        }

        String qStr = String.valueOf(product.getQuantity());
        String revQStr = new StringBuilder(qStr).reverse().toString();
        product.setQuantity(Integer.parseInt(revQStr));

        // 4. Gửi kết quả
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(product);
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

class Product implements Serializable {
    private static final long serialVersionUID = 20161107L;
    private String id;
    private String code;
    private String name;
    private int quantity;

    public Product(String id, String code, String name, int quantity) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}