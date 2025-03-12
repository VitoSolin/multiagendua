package KMQL;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.core.AID;

public class SellerAgentKQML extends Agent {
    protected void setup() {
        System.out.println("Seller KQML Agent " + getAID().getName() + " is ready.");

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();

                    // 1. Menerima ask-if
                    if (content.startsWith("ask-if")) {
                        System.out.println("SellerAgentKQML: Menerima ask-if.");

                        // 2. Mengirim tell status ketersediaan
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent("tell: Buku tersedia.");
                        send(reply);
                        System.out.println("SellerAgentKQML: Mengirim tell.");
                    }

                    // 3. Menerima achieve pembelian
                    else if (content.startsWith("achieve")) {
                        System.out.println("SellerAgentKQML: Menerima achieve.");

                        // 4. Mengirim reply konfirmasi
                        ACLMessage confirm = msg.createReply();
                        confirm.setPerformative(ACLMessage.CONFIRM);
                        confirm.setContent("reply: Pembelian berhasil.");
                        send(confirm);
                        System.out.println("SellerAgentKQML: Mengirim reply.");
                    }
                } else {
                    block();
                }
            }
        });
    }
}