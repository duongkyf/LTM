import java.io.*;
import java.net.*;

public class UDP05 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2209;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode27"; // Thay qCode thực tế khi thi
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
        Student student = (Student) ois.readObject();

        // 3. Xử lý dữ liệu
        String[] words = student.getName().trim().toLowerCase().split("\\s+");
        StringBuilder normalizedName = new StringBuilder();
        for (String w : words) {
            if (w.length() > 0) {
                normalizedName.append(Character.toUpperCase(w.charAt(0)))
                        .append(w.substring(1))
                        .append(" ");
            }
        }
        student.setName(normalizedName.toString().trim());

        // Tạo email: tên + chữ cái đầu của họ và tên đệm + @ptit.edu.vn
        StringBuilder email = new StringBuilder(words[words.length - 1]);
        for (int i = 0; i < words.length - 1; i++) {
            if (words[i].length() > 0) {
                email.append(words[i].charAt(0));
            }
        }
        email.append("@ptit.edu.vn");
        student.setEmail(email.toString());

        // 4. Gửi kết quả
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(student);
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

//class Student implements Serializable {
//    private static final long serialVersionUID = 20171107L;
//    private String id;
//    private String code;
//    private String name;
//    private String email;
//
//    public Student(String id, String code, String name, String email) {
//        this.id = id;
//        this.code = code;
//        this.name = name;
//        this.email = email;
//    }
//
//    public Student(String code) {
//        this.code = code;
//    }
//
//    public String getId() { return id; }
//    public void setId(String id) { this.id = id; }
//    public String getCode() { return code; }
//    public void setCode(String code) { this.code = code; }
//    public String getName() { return name; }
//    public void setName(String name) { this.name = name; }
//    public String getEmail() { return email; }
//    public void setEmail(String email) { this.email = email; }
//}