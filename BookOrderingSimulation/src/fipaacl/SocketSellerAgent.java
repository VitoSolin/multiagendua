package fipaacl;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketSellerAgent extends Agent {
    private ServerSocket serverSocket;
    private final int PORT = 5555;
    private ExecutorService executor;
    
    @Override
    protected void setup() {
        System.out.println("Socket Seller Agent " + getAID().getName() + " is ready.");
        
        // Inisialisasi socket server
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Socket server started on port " + PORT);
            
            // Thread pool untuk menangani koneksi client
            executor = Executors.newCachedThreadPool();
            
            // Start thread untuk accept koneksi
            executor.submit(this::acceptConnections);
        } catch (IOException e) {
            System.err.println("Error starting socket server: " + e.getMessage());
            doDelete(); // Terminate agent jika server gagal start
        }
        
        // Behaviour untuk menangani pesan dari agen lain
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                // Terima pesan dengan performative REQUEST
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = receive(mt);
                
                if (msg != null) {
                    String content = msg.getContent();
                    System.out.println("SocketSellerAgent: Menerima request - " + content);
                    
                    // Kirim informasi buku
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("Judul: Java Programming, Harga: Rp150.000, Stock: Tersedia");
                    send(reply);
                    System.out.println("SocketSellerAgent: Mengirim informasi buku");
                } else {
                    block();
                }
            }
        });
        
        // Behaviour untuk menangani pesan pembelian
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                    MessageTemplate.MatchContent("Saya ingin membeli buku.")
                );
                ACLMessage order = receive(mt);
                
                if (order != null) {
                    System.out.println("SocketSellerAgent: Menerima pemesanan dari " + order.getSender().getName());
                    
                    // Kirim konfirmasi pesanan
                    ACLMessage confirmation = order.createReply();
                    confirmation.setPerformative(ACLMessage.CONFIRM);
                    confirmation.setContent("Pesanan diterima. Terima kasih telah membeli!");
                    send(confirmation);
                    System.out.println("SocketSellerAgent: Konfirmasi pesanan terkirim");
                } else {
                    block();
                }
            }
        });
    }
    
    @Override
    protected void takeDown() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            if (executor != null) {
                executor.shutdown();
            }
            System.out.println("Socket Seller Agent " + getAID().getName() + " terminating.");
        } catch (IOException e) {
            System.err.println("Error closing socket: " + e.getMessage());
        }
    }
    
    private void acceptConnections() {
        try {
            while (!serverSocket.isClosed()) {
                System.out.println("Waiting for Python client to connect...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected from: " + clientSocket.getInetAddress());
                
                // Handle koneksi di thread terpisah
                executor.submit(() -> handleClientConnection(clientSocket));
            }
        } catch (IOException e) {
            if (!serverSocket.isClosed()) {
                System.err.println("Error accepting connections: " + e.getMessage());
            }
        }
    }
    
    private void handleClientConnection(Socket clientSocket) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from Python: " + inputLine);
                
                // Proses pesan dari Python
                if (inputLine.contains("REQUEST_INFO")) {
                    // Kirim informasi buku ke client Python
                    out.println("INFO: Judul: Java Programming, Harga: Rp150.000, Stock: Tersedia");
                } else if (inputLine.contains("ORDER_BOOK")) {
                    // Kirim konfirmasi pemesanan ke client Python
                    out.println("CONFIRM: Pesanan diterima. Terima kasih telah membeli!");
                } else {
                    out.println("ERROR: Perintah tidak dikenali");
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client connection: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client disconnected");
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }
} 