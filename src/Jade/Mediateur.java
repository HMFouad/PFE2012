package Jade;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ProfileImpl;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.*;
public class Mediateur extends Agent {
    
//public Object[] prd;
  Vector prd;  
		/** Cette méthode est appelé directement apèes la création de l'agent pour permettre
		  * l'initialisation et l'affectation des différents comportements à cet agent 
		  * */
	protected void setup() {
		System.out.println(getLocalName()+" STARTED");
		
		try {
			// Création de desciprion de l'agent [Vendeur]
			DFAgentDescription dfd = new DFAgentDescription();
			dfd.setName(getAID());
			// Enregistrement de la description de l'agent dans DF (Directory Facilitator)
			DFService.register(this, dfd);
			System.out.println(getLocalName()+" REGISTERED WITH THE DF");
			} catch (FIPAException e) {
			e.printStackTrace();
			}
			addBehaviour(new CyclicBehaviour(this) {
				
			public void action() {
			/* L'agent Vendeur a reçu un message de l'agent Acheteur 
			   contient une demande    de la liste des produits.*/
			// Attente de message (de l'agent Acheteur)
			ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
                        ACLMessage_spec msg_commande=(ACLMessage_spec)msg;
                        
                        

			if (msg_commande != null) {				
String recherche =(String)msg_commande.contenu.elementAt(0);

//Transmet du nom de produit vers l'agent Interface
ACLMessage msg_commandeI = new ACLMessage(ACLMessage.INFORM);
msg_commandeI.addReceiver(new AID("Interface", AID.ISLOCALNAME));
msg_commandeI.setContent(recherche);
send(msg_commandeI);
                                
                                try{

 Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
    cnx = DriverManager.getConnection("jdbc:odbc:Produits_link");
    St = cnx.createStatement();
    
    Rs = St.executeQuery("Select * From Liste_produits WHERE Nom_produit='"+recherche+"'") ;
                        prd = new Vector();
                      while (Rs.next()){
        Id = Rs.getInt("Id");


        
        prd.add(Id);

        
//		System.out.println(prd);
}
}catch (Exception e){
JOptionPane.showMessageDialog(null, "Erreur de connection\n"+e.getMessage());
}
                                
			   
			/*   L'agent Vendeur répond à l'agent Acheteur en lui envoyant 
			   un objet contenant la liste des produits.
			*/													
			ACLMessage reply = msg.createReply();
			ACLMessage_spec msg_liste_Id=(ACLMessage_spec)reply;
                        msg_liste_Id.reponce=prd;
			myAgent.send(msg_liste_Id);

									

			}
			else {
			
			//Pendant que le message n'est pas encore arrivé le comportement est bloqué
			block();
			}
			}
		});
	}
	

	protected void takeDown() {
		
		// Suppression de l'agent [Acheteur] depuis le DF
		try {
			DFService.deregister(this);
			System.out.println(getLocalName()+" DEREGISTERED WITH THE DF");
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
        			
    // Déclaration des variables
    Connection cnx;
    private Statement St;
    private ResultSet Rs;
    private DefaultTableModel dt;
    private int i,Id,Quantite;
    private String Nom_produit;
    private double Prix_min,Prix_max;

    public void main() {
        try {
					// Récupération du conteneur (Main Container) en cours d'execution de Jade					
					jade.core.Runtime rt = jade.core.Runtime.instance();
				
					// Création du profil par défault
					ProfileImpl p = new ProfileImpl(false);
					AgentContainer container =rt.createAgentContainer(p); // get a container controller for creating new agents
				
					// Agent controleur pour permettre la création des agents 
					AgentController Agent=null;		
				
					/* Création de l'agent Portail
					   cette commande est équivalente à la suivante: 
					   java jade.Boot Portail:JADE_exemple_personnel.Portail
					*/
					Agent = container.createNewAgent("Mediateur", "Jade.Mediateur", null);
				
					// Démarrage de l'agent Portail
					Agent.start();	
                                        
					} catch (Exception any) {
					any.printStackTrace();}				
    }
}
