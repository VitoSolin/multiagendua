package KMQL;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketSellerAgentKQML extends Agent {
    private ServerSocket serverSocket;
    private final int PORT = 5556; // Port berbeda dari agen FIPA-ACL
    private ExecutorService executor;
    
    @Override
    protected void setup() {
        System.out.println("Socket Seller KQML Agent " + getAID().getName() + " is ready.");
        
        // Inisialisasi socket server
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("KQML Socket server started on port " + PORT);
            
            // Thread pool untuk menangani koneksi client
            executor = Executors.newCachedThreadPool();
            
            // Start thread untuk accept koneksi
            executor.submit(this::acceptConnections);
        } catch (IOException e) {
            System.err.println("Error starting KQML socket server: " + e.getMessage());
            doDelete(); // Terminate agent jika server gagal start
        }
        
        // Behaviour untuk menangani pesan ketersediaan buku
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                // Filter pesan dengan performative QUERY_IF dan konten yang sesuai
                MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF),
                    MessageTemplate.MatchContent("ask-if: Apakah buku tersedia?")
                );
                ACLMessage msg = receive(mt);
                
                if (msg != null) {
                    System.out.println("SocketSellerAgentKQML: Menerima ask-if dari " + msg.getSender().getName());
                    
                    // Kirim tell tentang ketersediaan
                    ACLMessage reply = msg.createReply();
                    reply.setPerformative(ACLMessage.INFORM);
                    reply.setContent("tell: Buku tersedia. Ada 5 buku yang tersedia.");
                    send(reply);
                    System.out.println("SocketSellerAgentKQML: Mengirim tell ketersediaan");
                } else {
                    block();
                }
            }
        });
        
        // Behaviour untuk menangani pesan pembelian
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                // Filter pesan dengan performative REQUEST dan konten yang sesuai
                MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                    MessageTemplate.MatchContent("achieve: Saya ingin membeli buku.")
                );
                ACLMessage order = receive(mt);
                
                if (order != null) {
                    System.out.println("SocketSellerAgentKQML: Menerima achieve dari " + order.getSender().getName());
                    
                    // Kirim konfirmasi pesanan
                    ACLMessage confirmation = order.createReply();
                    confirmation.setPerformative(ACLMessage.CONFIRM);
                    confirmation.setContent("reply: Pesanan sukses. Terima kasih telah membeli!");
                    send(confirmation);
                    System.out.println("SocketSellerAgentKQML: Mengirim reply konfirmasi");
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
            System.out.println("Socket Seller KQML Agent " + getAID().getName() + " terminating.");
        } catch (IOException e) {
            System.err.println("Error closing KQML socket: " + e.getMessage());
        }
    }
    
    private void acceptConnections() {
        try {
            while (!serverSocket.isClosed()) {
                System.out.println("Waiting for Python client to connect to KQML agent...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected to KQML agent from: " + clientSocket.getInetAddress());
                
                // Handle koneksi di thread terpisah
                executor.submit(() -> handleClientConnection(clientSocket));
            }
        } catch (IOException e) {
            if (!serverSocket.isClosed()) {
                System.err.println("Error accepting connections in KQML agent: " + e.getMessage());
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
                System.out.println("Received from Python (KQML): " + inputLine);
                
                // Proses pesan dari Python dengan format KQML
                if (inputLine.contains("ASK_IF")) {
                    // Kirim informasi ketersediaan buku ke client Python
                    out.println("TELL: Buku tersedia. Ada 5 buku yang tersedia.");
                } else if (inputLine.contains("ACHIEVE")) {
                    // Kirim konfirmasi pemesanan ke client Python
                    out.println("REPLY: Pesanan sukses. Terima kasih telah membeli!");
                } else {
                    out.println("ERROR: Perintah KQML tidak dikenali");
                }
            }
        } catch (IOException e) {
            System.err.println("Error handling client connection in KQML agent: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Client disconnected from KQML agent");
            } catch (IOException e) {
                System.err.println("Error closing client socket in KQML agent: " + e.getMessage());
            }
        }
    }
} 