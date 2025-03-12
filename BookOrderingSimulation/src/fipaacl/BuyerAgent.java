package fipaacl;

import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.lang.acl.ACLMessage;

public class BuyerAgent extends Agent {
    protected void setup() {
        System.out.println("Buyer Agent " + getAID().getName() + " is ready.");

        SequentialBehaviour buyerBehaviour = new SequentialBehaviour();

        // 1. REQUEST info buku
        buyerBehaviour.addSubBehaviour(new jade.core.behaviours.OneShotBehaviour() {
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(getAID("Seller"));
                msg.setContent("Request informasi buku.");
                send(msg);
                System.out.println("BuyerAgent: Request informasi buku dikirim.");
            }
        });

        // 2. Menerima INFORM detail buku
        buyerBehaviour.addSubBehaviour(new jade.core.behaviours.CyclicBehaviour() {
            public void action() {
                ACLMessage reply = receive();
                if (reply != null && reply.getPerformative() == ACLMessage.INFORM) {
                    System.out.println("BuyerAgent: Detail buku diterima - " + reply.getContent());

                    // 3. Kirim REQUEST pembelian buku
                    ACLMessage purchaseRequest = new ACLMessage(ACLMessage.REQUEST);
                    purchaseRequest.addReceiver(getAID("Seller"));
                    purchaseRequest.setContent("Saya ingin membeli buku.");
                    send(purchaseRequest);
                    System.out.println("BuyerAgent: Permintaan pembelian dikirim.");
                } else {
                    block();
                }
            }
        });

        // 4. Menerima CONFIRM dari Seller
        buyerBehaviour.addSubBehaviour(new jade.core.behaviours.CyclicBehaviour() {
            public void action() {
                ACLMessage confirmation = receive();
                if (confirmation != null && confirmation.getPerformative() == ACLMessage.CONFIRM) {
                    System.out.println("BuyerAgent: Pesanan dikonfirmasi - " + confirmation.getContent());
                } else {
                    block();
                }
            }
        });

        addBehaviour(buyerBehaviour);
    }
}

