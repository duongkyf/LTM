/**
 * Đề 5: PricedProduct, tính finalPrice có phí fee (cổng 2209)
 * - Gửi: ;studentCode;qCode
 * - Nhận: 8 byte requestId, theo sau là đối tượng PricedProduct
 * - Xử lý: finalPrice = round2(basePrice * (1 + taxRate/100) * (1 - discountRate/100) + fee).
 *          fee = 25 nếu basePrice >= 1000, ngược lại fee = 0.
 * - Gửi lại: 8 byte requestId, theo sau là đối tượng PricedProduct đã cập nhật finalPrice.
 */

import java.io.*;
import java.net.*;

public class Bai5 {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(5000);
        InetAddress serverAddress = InetAddress.getByName("api.ptit.store");
        int port = 2209;

        // 1. Gửi
        String sendStr = ";B23DCCN643;qCode5"; 
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
        PricedProduct product = (PricedProduct) ois.readObject();

        // 3. Xử lý
        double basePrice = product.getBasePrice();
        double taxRate = product.getTaxRate();
        double discountRate = product.getDiscountRate();
        
        double fee = (basePrice >= 1000) ? 25 : 0;
        double finalPrice = basePrice * (1 + taxRate / 100) * (1 - discountRate / 100) + fee;
        
        finalPrice = Math.round(finalPrice * 100.0) / 100.0;
        product.setFinalPrice(finalPrice);

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

class PricedProduct implements Serializable {
    private static final long serialVersionUID = 20260517L;
    private String id;
    private String name;
    private double basePrice;
    private double taxRate;
    private double discountRate;
    private double finalPrice;

    public PricedProduct() { }

    public PricedProduct(String id, String name, double basePrice, double taxRate, double discountRate) {
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.taxRate = taxRate;
        this.discountRate = discountRate;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public double getTaxRate() { return taxRate; }
    public void setTaxRate(double taxRate) { this.taxRate = taxRate; }
    public double getDiscountRate() { return discountRate; }
    public void setDiscountRate(double discountRate) { this.discountRate = discountRate; }
    public double getFinalPrice() { return finalPrice; }
    public void setFinalPrice(double finalPrice) { this.finalPrice = finalPrice; }
}