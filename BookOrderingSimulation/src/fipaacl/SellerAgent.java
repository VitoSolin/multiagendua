package fipaacl;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SellerAgent extends Agent {
    protected void setup() {
        System.out.println("Seller Agent " + getAID().getName() + " is ready.");

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();

                    // 1. Menerima REQUEST informasi buku
                    if (content.equalsIgnoreCase("Request informasi buku.")) {
                        System.out.println("SellerAgent: Request info buku diterima dari " + msg.getSender().getName());

                        // 2. Mengirim INFORM detail buku
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent("Buku tersedia: 'Artificial Intelligence' harga 100.000.");
                        send(reply);
                        System.out.println("SellerAgent: Inform detail buku dikirim.");
                    }

                    // 3. Menerima REQUEST pembelian
                    else if (content.equalsIgnoreCase("Saya ingin membeli buku.")) {
                        System.out.println("SellerAgent: Permintaan pembelian diterima.");

                        // 4. Mengirim CONFIRM pesanan berhasil
                        ACLMessage confirm = msg.createReply();
                        confirm.setPerformative(ACLMessage.CONFIRM);
                        confirm.setContent("Pesanan Anda berhasil.");
                        send(confirm);
                        System.out.println("SellerAgent: Konfirmasi pemesanan dikirim.");
                    }
                } else {
                    block();
                }
            }
        });
    }
}


