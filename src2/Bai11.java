/**
 * Đề 11: UDP.Book, chuẩn hóa title, author, ISBN, publishDate (cổng 2209)
 * - Gửi: ;studentCode;qCode
 * - Nhận: 8 byte requestId, đối tượng Book
 * - Xử lý: 
 *     + Chuẩn hóa title: viết hoa chữ cái đầu của mỗi từ.
 *     + Chuẩn hóa author: định dạng "HỌ, Tên" (Họ viết hoa, Tên viết hoa chữ cái đầu).
 *     + Chuẩn hóa mã ISBN: định dạng "978-3-16-148410-0" (XXX-X-XX-XXXXXX-X).
 *     + Chuyển publishDate: yyyy-mm-dd sang mm/yyyy.
 * - Gửi lại: 8 byte requestId, đối tượng Book đã cập nhật.
 */

import java.io.*;
import java.net.*;

public class Bai11 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2209;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode11"; // Thay qCode thực tế khi thi
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
        Book book = (Book) ois.readObject();

        // 3. Xử lý dữ liệu
        // Chuẩn hóa title
        String[] wTitle = book.getTitle().trim().toLowerCase().split("\\s+");
        StringBuilder newTitle = new StringBuilder();
        for (String w : wTitle) {
            if (w.length() > 0) newTitle.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(" ");
        }
        book.setTitle(newTitle.toString().trim());

        // Chuẩn hóa author (HỌ, Tên)
        String[] wAuthor = book.getAuthor().trim().toLowerCase().split("\\s+");
        if (wAuthor.length > 0) {
            String ho = wAuthor[0].toUpperCase();
            StringBuilder ten = new StringBuilder();
            for (int i = 1; i < wAuthor.length; i++) {
                if (wAuthor[i].length() > 0) {
                    ten.append(Character.toUpperCase(wAuthor[i].charAt(0))).append(wAuthor[i].substring(1)).append(" ");
                }
            }
            book.setAuthor(ho + ", " + ten.toString().trim());
        }

        // Chuẩn hóa ISBN 
        String isbn = book.getIsbn().replaceAll("[^0-9]", "");
        if (isbn.length() == 13) {
            isbn = isbn.substring(0, 3) + "-" + isbn.substring(3, 4) + "-" + isbn.substring(4, 6) + "-" + isbn.substring(6, 12) + "-" + isbn.substring(12);
        }
        book.setIsbn(isbn);

        // Chuyển ngày yyyy-mm-dd -> mm/yyyy
        String[] dateParts = book.getPublishDate().split("-");
        if (dateParts.length == 3) {
            book.setPublishDate(dateParts[1] + "/" + dateParts[0]);
        }

        // 4. Gửi kết quả
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(book);
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

class Book implements Serializable {
    private static final long serialVersionUID = 20251107L;
    private String id;
    private String title;
    private String author;
    private String isbn;
    private String publishDate;

    public Book(String id, String title, String author, String isbn, String publishDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publishDate = publishDate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getPublishDate() { return publishDate; }
    public void setPublishDate(String publishDate) { this.publishDate = publishDate; }
}