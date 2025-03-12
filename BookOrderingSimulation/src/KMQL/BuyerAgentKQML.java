package KMQL;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class BuyerAgentKQML extends Agent {
    protected void setup() {
        System.out.println("Buyer KQML Agent " + getAID().getName() + " is ready.");

        // 1. Kirim ask-if tentang ketersediaan buku
        addBehaviour(new OneShotBehaviour() {
            public void action() {
                ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
                msg.addReceiver(new AID("seller", AID.ISLOCALNAME));
                msg.setContent("ask-if: Apakah buku tersedia?");
                send(msg);
                System.out.println("BuyerAgentKQML: Mengirim ask-if ke seller.");
            }
        });

        // 2. Terima tell status ketersediaan
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage reply = receive();
                if (reply != null && reply.getPerformative() == ACLMessage.INFORM) {
                    System.out.println("BuyerAgentKQML: Status ketersediaan diterima - " + reply.getContent());
                    
                    // 3. Kirim achieve untuk membeli buku
                    ACLMessage purchase = new ACLMessage(ACLMessage.REQUEST);
                    purchase.addReceiver(new AID("seller", AID.ISLOCALNAME));
                    purchase.setContent("achieve: Saya ingin membeli buku.");
                    send(purchase);
                    System.out.println("BuyerAgentKQML: Mengirim achieve ke seller.");
                } else {
                    block();
                }
            }
        });

        // 4. Terima reply konfirmasi
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage confirmation = receive();
                if (confirmation != null && confirmation.getPerformative() == ACLMessage.CONFIRM) {
                    System.out.println("BuyerAgentKQML: Pesanan dikonfirmasi - " + confirmation.getContent());
                } else {
                    block();
                }
            }
        });
    }
}