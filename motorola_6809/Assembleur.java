package TP6;


import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.JTextArea;


import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.Color;
import java.awt.event.ActionEvent;

public class Assembleur extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private int adresse_ROM = 0;
	private String pc;
	private String instru_pc;
	private int i = 0, nombreLignes=0 ,  position;
	private Boolean etq_pretes = false;

	JButton bouton_Execute = new JButton("executer");
	JButton bouton_PasAPas = new JButton("Pas_à_Pas");
	JButton bouton_Reset = new JButton("Reset");
	ArrayList<String> etiquette = new ArrayList<String>();
	
	final JTextArea textArea = new JTextArea();
	private Highlighter highlighter;
    private Highlighter.HighlightPainter painter;

	public Assembleur() {
		setTitle("Assembleur ");
		setAlwaysOnTop(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(1000, 120, 250, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
           contentPane.setBackground(Color.PINK );
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		textArea.setBounds(10, 60, 220, 200);
		contentPane.add(textArea);
		
		highlighter = textArea.getHighlighter();
        painter = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
		
		JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(10, 60, 220, 200); 
        contentPane.add(scrollPane);
        
        bouton_Reset.addActionListener(new ActionListener() { // Button pour effacer tout 
			public void actionPerformed(ActionEvent click) {
				ved_all();
				bouton_Execute.setEnabled(true);
				bouton_PasAPas.setEnabled(true);	
            }
		});
		bouton_Reset.setBounds(10, 5, 220, 23);
		contentPane.add(bouton_Reset);
		
		bouton_PasAPas.addActionListener(new ActionListener() { // Button  pas a pas
			public void actionPerformed(ActionEvent click) {
				String Asmbl = textArea.getText();
				if( !etq_pretes ) {
					 trouver_Toutes_Les_Etiquettes(Asmbl);
					etq_pretes = !etq_pretes;
				}
				
				executerProgramme_PasAPas(Asmbl);
            }
		});
		bouton_PasAPas.setBounds(10, 30, 110, 23);
		contentPane.add(bouton_PasAPas);
		
		bouton_Execute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String Asmbl = textArea.getText();
				if( !etq_pretes ) {
					 trouver_Toutes_Les_Etiquettes(Asmbl);
					etq_pretes = !etq_pretes;
				}
				executerProgramme(Asmbl);
			}
		});
		bouton_Execute.setBounds(120, 30, 110, 23);
		contentPane.add(bouton_Execute);
	}
	
	// Afficher un message d’erreur
	private void afficherErreur(String message) {
    JOptionPane.showMessageDialog(
        null,
        message,
        "Erreur",
        JOptionPane.ERROR_MESSAGE
    );
}

 //méthode sert à exécuter un programme assembleur ligne par ligne
	protected void executerProgramme_PasAPas(String asmbl) {
		try {
			String[] ligne = asmbl.split("\\n");
		    instru_pc = ligne[i];
		    String[] mots = ligne[i].split("\\s+");
		    executer_Instr(mots);
	        //System.out.println("ligne i: "+i++);
		    i++;
		} catch (Exception e) {
		    e.printStackTrace();
		    afficherErreur(" programme comporte des  erreurs.");
		}
	} 

  // Afficher un message d’erreur
	protected void executerProgramme(String asmbl) {
		try {
			String[] ligne = asmbl.split("\\n");
		    while (!ligne[i].equals("END")) {
		    	instru_pc = ligne[i];
			    String[] mots = ligne[i].split("\\s+");
			    executer_Instr(mots);
			    
			    i++;
	        }
		    terminerProgramme();
		    
		    Programme.afficher_Instructions(adresse_ROM, "END");
		    } catch (Exception e) {
		    e.printStackTrace();
		    afficherErreur(" erreurs Dans le programme");
		}
	}
	
	
	  // Réinitialiser tout (RAM, ROM, CPU, affichage)
	protected void ved_all() {
		Programme.h = 0;
		i = 0;
		etq_pretes= false;
		highlighter.removeAllHighlights();
		for (int j = 0; j < adresse_ROM+1; j++) {
			ROM.modifierValeurColonne(j, "FF");
		}
		for (int j = 0; j < 1024; j++) {
			RAM.modifierValeur("00", j);
		}
		CPU.reinitialiser();
		Programme. supprimer_Lignes(nombreLignes);
		adresse_ROM = 0;
		position = 0;
}
// l'instruction END 
	private void terminerProgramme() {
		pc = ROM.obtenirAdresse (adresse_ROM);
		CPU.setPC(pc);
		CPU.setinstru_PC("END");
		ROM.modifierValeurColonne(adresse_ROM, "3F");
		bouton_Execute.setEnabled(false);
		bouton_PasAPas.setEnabled(false);		
	}
     
	private void executer_Instr(String[] mots) {
		int taille = mots[0].length();
		char commantaire = mots[0].charAt(0);
		char etiquette = mots[0].charAt(taille - 1);
		
    // Si ce n’est ni un commentaire ni une étiquette
		if (commantaire != ';' && etiquette != ':') {
			pc = ROM.obtenirAdresse (adresse_ROM);
			CPU.setPC(pc);
			CPU.setinstru_PC(instru_pc);
			Programme.afficher_Instructions(adresse_ROM, instru_pc);
			nombreLignes++;
			String instr = mots[0].substring(0, taille-1); //-1 si reg est un char
			char registre = mots[0].charAt(taille-1);
			if (instr.equals("LD")) {
		        instr_ld(registre, mots);
	        } else if (instr.equals("ST")) {
	        	instr_st(registre, mots);
	        } else if (instr.equals("ADD")) {
	        	instr_add(registre, mots);
	        } else if (instr.equals("SUB")) {
	    		instr_sub(registre, mots); 
	    	} else if (instr.equals("CLR")) {
	    		instr_clr(registre);
	    	} else if (instr.equals("INC")) {
	    		instr_inc(registre);
	    	} else if (instr.equals("DEC")) {
	    		instr_dec(registre);
	    	} else if (instr.equals("ASL") || instr.equals("LSL")) {
	    		instr_asl(registre);
	    	} else if (instr.equals("ASR") || instr.equals("LSR")) {
	    		instr_asr(registre);
	    	} else if (instr.equals("AND")) {
				instr_and(registre, mots); 
			} else if (instr.equals("OR")) {
				instr_or(registre, mots); 
			} else if (instr.equals("CMP ")) {
				instr_cmp(registre, mots); 
			} else {
	    		instr = mots[0];
	    		if (instr.equals("END")) {
	    			terminerProgramme();
	        	} else if (instr.equals("ORG")) {
	    			instr_org(mots[1]); 
	    		} else if (instr.equals("SWI")) {
	    			instr_swi(); 
	    		} else if (instr.equals("NOP")) {
	    			instr_nop(); 
	    		} else if (instr.equals("TFR")) {
	    			instr_tfr(mots); 
	    		} 
	    		else if (instr.equals("MUL")) {
	    			instr_mul(); 
	    		} else if (instr.equals("ABX")) {
	    			instr_abx(); 
	    		}if(instr.equals("JMP")) {
					instr_jmp(mots[1]);
				}
				
			}
		} else {
			Programme.afficher_Instructions(instru_pc);
			nombreLignes++;
		}
	}
//__________________________instruction org ______________________________________
	private void instr_org(String adresse) {
		int id = hexToDecimal(adresse) - 0xFC00;
		// Vérifie si l'adresse est dans la plage ROM
		if (id >= 0 && id <= 1024 ) {   // taille ROM = 1024 octets
			adresse_ROM = (int) (id); // on stocke l'indice dans la ROM
		} else {
			afficherErreur("Adresse hors de la ROM !");
			
		}
	}
// Méthode pour convertir une chaîne hexadécimale en entier décimal
	public static int hexToDecimal(String hexString) {
        int decimalValue = Integer.parseInt(hexString, 16);
        return decimalValue;
    }
//__________________________instruction de comparaison _______________________________________
	private void instr_cmp(char registre, String[] mots) {//traitement des adresses a 1 octect puis reg 2o!
		int taille= mots[1].length();// longueur de l'opérande
		int valeur = -127, val_reg;   // valeur à comparer
		// Lire la valeur dans le registre
		val_reg=  convertirHexEnDecimal( getR(registre) );
		//mode d'adressage immediat
		if(mots[1].charAt(0) == '#') {
			valeur=  convertirHexEnDecimal( mots[1].substring(2) );
			ROM.modifierValeurColonne(adresse_ROM, "81");
            adresse_ROM++;
            ROM.modifierValeurColonne(adresse_ROM, mots[1].substring(2));
            adresse_ROM++;
		}
		// Mode direct ou étendu ($xx ou $xxxx)
		else if(mots[1].charAt(0) == '$' && mots[1].indexOf(',') < 0){
			String adresse = mots[1].substring(1);
			if(taille < 4 ) {							//adressage directe
				adresse = CPU.getDP() + adresse;//concatenation depui DP
			}
			valeur=  convertirHexEnDecimal((String)RAM.obtenirValeur(adresse));
			ROM.modifierValeurColonne(adresse_ROM, "B1");
            adresse_ROM++;
            ROM.modifierValeurColonne(adresse_ROM, mots[1].substring(1));
            adresse_ROM++;
		}
		// Mise à jour des flags
System.out.println(mots[1].charAt(1));
		System.out.println("CMP : valeurRegistre =  "+val_reg+", valeurMémoire = " +valeur);
		// Mise à jour des flags N et Z 
		if(val_reg < valeur) {
    		CPU.setN("1");
		}else if(val_reg == valeur) {
			CPU.setZ("1");
		}else {
			CPU.setN("0");
			CPU.setZ("0");
		}
	}
//_________________________________instruction OR ______________________________________________________
	private void instr_or(char registre, String[] mots) {
		 // Mode immédiat (#$xx)
		if(mots[1].charAt(0) == '#' && mots[1].charAt(1) == '$') {		//Mode d'adressage immediat
    		String valeurExtrait = mots[1].substring(2); // Extraire la valeur après #$

        	switch(registre) {
        		case 'A':
        			String valA = CPU.getA();
        			int newa =  convertirHexEnDecimal(valA) |  convertirHexEnDecimal(valeurExtrait);
        			int taillea = decimalEnHex(newa).length();
        			CPU.setA(decimalEnHex(newa).substring(taillea-2));
        			ROM.modifierValeurColonne(adresse_ROM, "8A");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, valeurExtrait);
                    adresse_ROM++;
        			break;
        		case 'B':
        			String valB = CPU.getA();
        			int newb =  convertirHexEnDecimal(valB) |  convertirHexEnDecimal(valeurExtrait);
        			int tailleb = decimalEnHex(newb).length();
        			CPU.setA(decimalEnHex(newb).substring(tailleb-2));
        			ROM.modifierValeurColonne(adresse_ROM, "CA");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, valeurExtrait);
                    adresse_ROM++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
			// Mode direct ou étendu ($xx ou $xxxx)
    	} else if (mots[1].charAt(0) == '$') {
		int taille = mots[1].length();
		if (taille == 5)		//Mode d'adressage etendu
		{
			String adresse = mots[1].substring(1, 5);
			String val = null;
			if (registre != 'G') {
				val = (String) RAM.obtenirValeur(adresse);
			}
        	switch(registre) {
        		case 'A':
        			String valA = CPU.getA();
        			int newa =  convertirHexEnDecimal(valA) |  convertirHexEnDecimal(val);
        			int taillea = decimalEnHex(newa).length();
        			CPU.setA(decimalEnHex(newa).substring(taillea-2));
        			ROM.modifierValeurColonne(adresse_ROM, "BA");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresse);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		case 'B':
        			String valB = CPU.getA();
        			int newb =  convertirHexEnDecimal(valB) |  convertirHexEnDecimal(val);
        			int tailleb = decimalEnHex(newb).length();
        			CPU.setA(decimalEnHex(newb).substring(tailleb-2));
        			ROM.modifierValeurColonne(adresse_ROM, "FA");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresse);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
			
		} else if (taille == 3) {		//Mode d'adressage direct
			String adresse = (String) CPU.getDP() + mots[1].substring(1, 3);
			String add1 = adresse.substring(2);
			String val = (String) RAM.obtenirValeur(adresse);
        	switch(registre) {
        		case 'A':
        			String valA = CPU.getA();
        			int newa =  convertirHexEnDecimal(valA) |  convertirHexEnDecimal(val);
        			int taillea = decimalEnHex(newa).length();
        			CPU.setA(decimalEnHex(newa).substring(taillea-2));
        			ROM.modifierValeurColonne(adresse_ROM, "9A");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add1);
                    adresse_ROM++;
        			break;
        		case 'B':
        			String valB = CPU.getB();
        			int newb =  convertirHexEnDecimal(valB) |  convertirHexEnDecimal(val);
        			int tailleb = decimalEnHex(newb).length();
        			CPU.setB(decimalEnHex(newb).substring(tailleb-2));
        			ROM.modifierValeurColonne(adresse_ROM, "DA");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add1);
                    adresse_ROM++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
			}
		} 
		}

	}
//____________________________instruction AND ______________________________________________________
	private void instr_and(char registre, String[] mots) {
		if(mots[1].charAt(0) == '#') {		//Mode d'adressage immediat
    		String val = mots[1].substring(2);
        	switch(registre) {
        		case 'A':
        			String valA = CPU.getA();
        			int newa =  convertirHexEnDecimal(valA) &  convertirHexEnDecimal(val);
        			int taillea = decimalEnHex(newa).length();
        			CPU.setA(decimalEnHex(newa).substring(taillea-2));
        			ROM.modifierValeurColonne(adresse_ROM, "84");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, val);
                    adresse_ROM++;
        			break;
        		case 'B':
        			String valB = CPU.getA();
        			int newb =  convertirHexEnDecimal(valB) &  convertirHexEnDecimal(val);
        			int tailleb = decimalEnHex(newb).length();
        			CPU.setA(decimalEnHex(newb).substring(tailleb-2));
        			ROM.modifierValeurColonne(adresse_ROM, "C4");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, val);
                    adresse_ROM++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
    	} else if (mots[1].charAt(0) == '$') {
		int taille = mots[1].length();
		if (taille == 5)		//Mode d'adressage etendu
		{
			String adresse = mots[1].substring(1, 5);
			String val = (String) RAM.obtenirValeur(adresse);
        	switch(registre) {
        		case 'A':
        			String valA = CPU.getA();
        			int newa =  convertirHexEnDecimal(valA) &  convertirHexEnDecimal(val);
        			int taillea = decimalEnHex(newa).length();
        			CPU.setA(decimalEnHex(newa).substring(taillea-2));
        			ROM.modifierValeurColonne(adresse_ROM, "B4");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresse);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		case 'B':
        			String valB = CPU.getA();
        			int newb =  convertirHexEnDecimal(valB) &  convertirHexEnDecimal(val);
        			int tailleb = decimalEnHex(newb).length();
        			CPU.setA(decimalEnHex(newb).substring(tailleb-2));
        			ROM.modifierValeurColonne(adresse_ROM, "F4");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresse);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
			
		} else if (taille == 3) {		//Mode d'adressage direct
			String adresse = (String) CPU.getDP() + mots[1].substring(1, 3);
			String add1 = adresse.substring(2);
			String val = (String) RAM.obtenirValeur(adresse);
        	switch(registre) {
        		case 'A':
        			String valA = CPU.getA();
        			int newa =  convertirHexEnDecimal(valA) &  convertirHexEnDecimal(val);
        			int taille2 = decimalEnHex(newa).length();
        			CPU.setA(decimalEnHex(newa).substring(taille2-2));
        			ROM.modifierValeurColonne(adresse_ROM, "94");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add1);
                    adresse_ROM++;
        			break;
        		case 'B':
        			String valB = CPU.getA();
        			int newb =  convertirHexEnDecimal(valB) &  convertirHexEnDecimal(val);
        			int tailleb = decimalEnHex(newb).length();
        			CPU.setA(decimalEnHex(newb).substring(tailleb-2));
        			ROM.modifierValeurColonne(adresse_ROM, "D4");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add1);
                    adresse_ROM++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
			}
		}
		}
	}
//_______________________________instruction ASR_______________________________________________________________
	private void instr_asr(char registre) {
		switch(registre) {
		case 'A':
			String valA = CPU.getA();
			int valintA =  convertirHexEnDecimal(valA) >> 1;
			CPU.setA(decimalEnHex(valintA).substring(2));
			String binaryStringA = Integer.toBinaryString( convertirHexEnDecimal(valA));
			int taillea = binaryStringA.length();
			String a = (String) binaryStringA.substring(taillea-1,taillea);
			CPU.setC(a);
			ROM.modifierValeurColonne(adresse_ROM, "47");
            adresse_ROM++;
			break;
		case 'B':
			String valB = CPU.getB();
			int valintB =  convertirHexEnDecimal(valB) >> 1;
			CPU.setB(decimalEnHex(valintB).substring(2));
			String binaryStringB = Integer.toBinaryString( convertirHexEnDecimal(valB));
			int tailleb = binaryStringB.length();
			String b = (String) binaryStringB.substring(tailleb-1,tailleb);
			CPU.setC(b);
			ROM.modifierValeurColonne(adresse_ROM, "57");
            adresse_ROM++;
			break;
		default:
            throw new IllegalArgumentException("Registre non pris en charge : " + registre);
	}
	}
// _______________________________instruction ASL __________________________________________________________
	private void instr_asl(char registre) {
		switch(registre) {
		case 'A':
			String valA = CPU.getA();
			int valintA =  convertirHexEnDecimal(valA) << 1;
			CPU.setA(decimalEnHex(valintA).substring(2));
			String binaryStringA = Integer.toBinaryString(valintA);
			int taillea = binaryStringA.length();
			String a = (String) binaryStringA.substring(taillea-3,taillea-2);
			CPU.setC(a);
			ROM.modifierValeurColonne(adresse_ROM, "48");
            adresse_ROM++;
			break;
		case 'B':
			String valB = CPU.getB();
			int valintB =  convertirHexEnDecimal(valB) << 1;
			CPU.setB(decimalEnHex(valintB).substring(2));
			String binaryStringB = Integer.toBinaryString(valintB);
			int tailleb = binaryStringB.length();
			String b = (String) binaryStringB.substring(tailleb-3,tailleb-2);
			CPU.setC(b);
			ROM.modifierValeurColonne(adresse_ROM, "58");
            adresse_ROM++;
			break;
		default:
            throw new IllegalArgumentException("Registre non pris en charge : " + registre);
	}
	}
 //___________________________instruction abx______________________________________________________
	private void instr_abx() {
		int B =  convertirHexEnDecimal(CPU.getB());
		int X =  convertirHexEnDecimal(CPU.get_X());
		String val = decimalEnHex(X + B);
		CPU.setX(val);
		flagNeg(val,16);
		flagZero(val);
		ROM.modifierValeurColonne(adresse_ROM, "3A");
        adresse_ROM++;
	}

	

	
//___________________________instruction MUL_____________________________________________________
	private void instr_mul() {
		int A =  convertirHexEnDecimal(CPU.getA());
		int B =  convertirHexEnDecimal(CPU.getB());
		int M = A * B;
		String mul = decimalEnHex(M);
		CPU.setD(mul);
		flagNeg(mul,16);
		flagZero(mul);
		ROM.modifierValeurColonne(adresse_ROM, "3D");
        adresse_ROM++;		
	}
//__________________________instrection DEC________________________________________________________
	private void instr_dec(char registre) {
    	switch(registre) {
    		case 'A':
    			String valA = CPU.getA();
    			int valintA =  convertirHexEnDecimal(valA) - 1;
    			String valAA = decimalEnHex(valintA);
    			@SuppressWarnings("unused") 
    			String val = valAA.substring(2);
    			CPU.setA(valAA.substring(2));
    			flagNeg(valAA.substring(2),8);
    			flagZero(valAA.substring(2));
    			pc = ROM.obtenirAdresse (adresse_ROM);
    			CPU.setPC(pc);
    			CPU.setinstru_PC(instru_pc);
    			ROM.modifierValeurColonne(adresse_ROM, "4A");
                adresse_ROM++;
    			break;
    		case 'B':
    			pc = ROM.obtenirAdresse (adresse_ROM);
    			CPU.setPC(pc);
    			CPU.setinstru_PC(instru_pc);
    			String valB = CPU.getB();
    			int valintB =  convertirHexEnDecimal(valB) - 1;
    			String valBB = decimalEnHex(valintB);
    			CPU.setB(valBB.substring(2));
    			flagNeg(valBB.substring(2),8);
    			flagZero(valBB.substring(2));
    			pc = ROM.obtenirAdresse (adresse_ROM);
    			CPU.setPC(pc);
    			CPU.setinstru_PC(instru_pc);
    			ROM.modifierValeurColonne(adresse_ROM, "5A");
                adresse_ROM++;
    			break;
    		default:
                throw new IllegalArgumentException("Registre non pris en charge : " + registre);
    	}
		
	}

	


	
//______________________________instruction TFR__________________________________________
	private void instr_tfr(String[] mots) {
		char R1 = mots[1].charAt(0);
		char S = mots[1].charAt(1);
		if ((R1 == 'D' && S == ',') || R1 != 'D') {
			String Val = getR(R1);
			char R2 = mots[1].charAt(2);
			char F = mots[1].charAt(mots[1].length()-1);
			if (R2 != 'D' || (R2 == 'D' && F != 'P')) {
				setR(R2,Val);
			} else if (F == 'P') {
				CPU.setDP(Val);
			}
		}
		if (R1 == 'D' && S == 'P') {
			String Val = CPU.getDP();
			char R2 = mots[1].charAt(3);
			setR(R2,Val);
		}
		ROM.modifierValeurColonne(adresse_ROM, "1F");
		adresse_ROM++;
		ROM.modifierValeurColonne(adresse_ROM, "80");
		adresse_ROM++;
	}
//________________________________________instruction SWI______________________________________
	private void instr_swi() {
		ROM.modifierValeurColonne(adresse_ROM, "3F");
		adresse_ROM++;
		ROM.modifierValeurColonne(adresse_ROM, "3F");
		bouton_Execute.setEnabled(false);
	}
// ________________________________________instruction SUB_______________________________________________ 
	private void instr_sub(char registre, String[] mots) {
		if(mots[1].charAt(0) == '#' && mots[1].charAt(1) == '$') {		//Mode d'adressage immediat
    		String val = mots[1].substring(2);
    		String val4 = mots[1].substring(2);;
    		if (mots[1].length() == 6) {
    			val4 = mots[1].substring(2, 6);
    		}
        	switch(registre) {
        		case 'A':
        			String valA = CPU.getA();
        			flagSUB(valA,val,8);
        			int valintA =  convertirHexEnDecimal(valA) -  convertirHexEnDecimal(val);
        			val = decimalEnHex(valintA);
        			val = val.substring(2);
        			CPU.setA(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.modifierValeurColonne(adresse_ROM, "80");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, val);
                    adresse_ROM++;
        			break;
        		case 'B':
        			String valB = CPU.getB();
        			flagSUB(valB,val,8);
        			int valintB =  convertirHexEnDecimal(valB) -  convertirHexEnDecimal(val);
        			val = decimalEnHex(valintB);
        			val = val.substring(2);
        			CPU.setB(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.modifierValeurColonne(adresse_ROM, "C0");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, val);
                    adresse_ROM++;
        			break;
        		case 'D':
        			String valD = CPU.getD();
        			flagSUB(valD,val,16);
        			int valintD =  convertirHexEnDecimal(valD) -  convertirHexEnDecimal(val);
        			val = decimalEnHex(valintD);
        			CPU.setD(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "83");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, val);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
    	}
		if (mots[1].charAt(0) == '$') {
		int taille = mots[1].length();
		if (taille == 5)		//Mode d'adressage etendu
		{
			String adresse = mots[1].substring(1, 5);
			String val = (String) RAM.obtenirValeur(adresse);
			String val_next = (String) RAM.obtenirValeurSuivante(adresse);
			String val4 = (String) val + val_next;
        	switch(registre) {
        		case 'A':
        			String valA = CPU.getA();
        			flagSUB(valA,val,8);
        			int valintA =  convertirHexEnDecimal(val) -  convertirHexEnDecimal(valA);
        			val = decimalEnHex(valintA);
        			val = val.substring(2);
        			CPU.setA(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.modifierValeurColonne(adresse_ROM, "B0");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresse);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		case 'B':
        			String valB = CPU.getB();
        			flagSUB(valB,val,8);
        			int valintB =  convertirHexEnDecimal(val) -  convertirHexEnDecimal(valB);
        			val = decimalEnHex(valintB);
        			val = val.substring(2);
        			CPU.setB(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.modifierValeurColonne(adresse_ROM, "F0");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresse);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		case 'D':
        			String valD = CPU.getD();
        			flagSUB(valD,val,16);
        			int valintD =  convertirHexEnDecimal(val) +  convertirHexEnDecimal(valD);
        			val = decimalEnHex(valintD);
        			CPU.setD(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "B3");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresse);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
			
		} else if (taille == 3) {		//Mode d'adressage direct
			String adresse = (String) CPU.getDP() + mots[1].substring(1, 3);
			String add1 = adresse.substring(2);
			String val = (String) RAM.obtenirValeur(adresse);
			String val_next = (String) RAM.obtenirValeurSuivante(adresse);
			String val4 = (String) val + val_next;
        	switch(registre) {
        		case 'A':
        			String valA = CPU.getA();
        			flagSUB(valA,val,8);
        			int valintA =  convertirHexEnDecimal(val) -  convertirHexEnDecimal(valA);
        			val = decimalEnHex(valintA);
        			val = val.substring(2);
        			CPU.setA(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.modifierValeurColonne(adresse_ROM, "90");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add1);
                    adresse_ROM++;
        			break;
        		case 'B':
        			String valB = CPU.getB();
        			flagSUB(valB,val,8);
        			int valintB =  convertirHexEnDecimal(val) -  convertirHexEnDecimal(valB);
        			val = decimalEnHex(valintB);
        			val = val.substring(2);
        			CPU.setB(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.modifierValeurColonne(adresse_ROM, "D0");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add1);
                    adresse_ROM++;
        			break;
        		case 'D':
        			String valD = CPU.getD();
        			flagSUB(valD,val,16);
        			int valintD =  convertirHexEnDecimal(val) -  convertirHexEnDecimal(valD);
        			val = decimalEnHex(valintD);
        			CPU.setD(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "93");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add1);
                    adresse_ROM++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
		}
		}
	}
	//____________________________________________instruction NOP ______________________________________________________________________
	private void instr_nop() {
		ROM.modifierValeurColonne(adresse_ROM, "12");
        adresse_ROM++;		
	}
	
	private void instr_inc(char registre) {
    	switch(registre) {
    		case 'A':
    			String valA = CPU.getA();
    			int valintA =  convertirHexEnDecimal(valA) + 1;
    			String valAA = decimalEnHex(valintA);
    			CPU.setA(valAA.substring(2));
    			ROM.modifierValeurColonne(adresse_ROM, "4F");
                adresse_ROM++;
    			break;
    		case 'B':
    			String valB = CPU.getB();
    			int valintB =  convertirHexEnDecimal(valB) + 1;
    			String valBB = decimalEnHex(valintB);
    			CPU.setB(valBB.substring(2));
    			ROM.modifierValeurColonne(adresse_ROM, "5F");
                adresse_ROM++;
    			break;
    		default:
                throw new IllegalArgumentException("Registre non pris en charge : " + registre);
    	}
		
	}
	//_____________________________________________instructin CLRA ,CLRB , CLR _____________________________________________________________________
	private void instr_clr(char registre) {
		//mode inhérent 
	    switch(registre) {
	    	case 'A':
	    		CPU.setA("00");
	    		ROM.modifierValeurColonne(adresse_ROM, "4F");
	        	adresse_ROM++;
	        	break;
	        case 'B':
	       		CPU.setB("00");
	     		ROM.modifierValeurColonne(adresse_ROM, "5F");
	            adresse_ROM++;
	        	break;
	        default:
	            throw new IllegalArgumentException("Registre non pris en charge : " + registre);
	        }
				
	}
	

    // ____________________________________________instructin ADDA ,ADDB ,ADDD ____________________________________________________________________________
	private void instr_add(char registre, String[] mots) {
		if(mots[1].charAt(0) == '#' && mots[1].charAt(1) == '$') {		//Mode d'adressage immediat
    		String val = mots[1].substring(2);
    		String val4 = mots[1].substring(2);;
    		if (mots[1].length() == 4) {
    			val4 = mots[1].substring(2, 4);
    		}
        	switch(registre) {
        		case 'A':
        			String valA = CPU.getA();
        			flagADD(val,valA,8);
        			int valintA =  convertirHexEnDecimal(val) +  convertirHexEnDecimal(valA);
        			val = decimalEnHex(valintA);
        			val = val.substring(2);
        			CPU.setA(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.modifierValeurColonne(adresse_ROM, "8B");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, val);
                    adresse_ROM++;
        			break;
        		case 'B':
        			String valB = CPU.getB();
        			flagADD(val,valB,8);
        			int valintB =  convertirHexEnDecimal(val) +  convertirHexEnDecimal(valB);
        			val = decimalEnHex(valintB);
        			val = val.substring(2);
        			CPU.setB(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.modifierValeurColonne(adresse_ROM, "CB");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, val);
                    adresse_ROM++;
        			break;
        		case 'D':
        			String valD = CPU.getD();
        			flagADD(val,valD,16);
        			int valintD =  convertirHexEnDecimal(val) +  convertirHexEnDecimal(valD);
        			val = decimalEnHex(valintD);
        			CPU.setD(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "C3");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, val);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
    	}
		//Mode d'adressage etendu
		if (mots[1].charAt(0) == '$') {
		int taille = mots[1].length();
		if (taille == 3)		
		{
			String adresse = mots[1].substring(1, 5);
			String val = (String) RAM.obtenirValeur(adresse);
			String val_next = (String) RAM.obtenirValeurSuivante(adresse);
			String val4 = (String) val + val_next;
        	switch(registre) {
        		case 'A':
        			String valA = CPU.getA();
        			flagADD(val,valA,8);
        			int valintA =  convertirHexEnDecimal(val) +  convertirHexEnDecimal(valA);
        			val = decimalEnHex(valintA);
        			val = val.substring(2);
        			CPU.setA(val);
        			flagNeg(val, 8);
    				flagZero(val);
        			ROM.modifierValeurColonne(adresse_ROM, "8B");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresse);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		case 'B':
        			String valB = CPU.getB();
        			flagADD(val,valB,8);
        			int valintB =  convertirHexEnDecimal(val) +  convertirHexEnDecimal(valB);
        			val = decimalEnHex(valintB);
        			val = val.substring(2);
        			CPU.setB(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.modifierValeurColonne(adresse_ROM, "FB");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresse);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		case 'D':
        			String valD = CPU.getD();
        			flagADD(val,valD,16);
        			int valintD =  convertirHexEnDecimal(val) +  convertirHexEnDecimal(valD);
        			val = decimalEnHex(valintD);
        			CPU.setD(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "F3");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresse);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
			//Mode d'adressage direct
		} else if (taille == 3) {		
			String adresse = (String) CPU.getDP() + mots[1].substring(1, 3);
			String add1 = adresse.substring(2);
			String val = (String) RAM.obtenirValeur(adresse);
			String val_next = (String) RAM.obtenirValeurSuivante(adresse);
			String val4 = (String) val + val_next;
        	switch(registre) {
        		case 'A':
        			String valA = CPU.getA();
        			flagADD(val,valA,8);
        			int valintA =  convertirHexEnDecimal(val) +  convertirHexEnDecimal(valA);
        			val = decimalEnHex(valintA);
        			val = val.substring(2);
        			CPU.setA(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.modifierValeurColonne(adresse_ROM, "9B");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add1);
                    adresse_ROM++;
        			break;
        		case 'B':
        			String valB = CPU.getB();
        			flagADD(val,valB,8);
        			int valintB =  convertirHexEnDecimal(val) +  convertirHexEnDecimal(valB);
        			val = decimalEnHex(valintB);
        			val = val.substring(2);
        			CPU.setB(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.modifierValeurColonne(adresse_ROM, "DB");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add1);
                    adresse_ROM++;
        			break;
        		case 'D':
        			String valD = CPU.getD();
        			flagADD(val,valD,16);
        			int valintD =  convertirHexEnDecimal(val) +  convertirHexEnDecimal(valD);
        			val = decimalEnHex(valintD);
        			CPU.setD(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "D3");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add1);
                    adresse_ROM++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
		}
		}
	}
// ___________________________________________instruction STA ,STB ,STD ,STX ,STY ,STS ,STU_____________________________________________________________________________________
	private void instr_st(char registre, String[] mots) {
		if (mots[1].charAt(0) == '$') {
			int taille = mots[1].length();
			//Mode d'adressage etendu
			if (taille == 5)		
			{
				String adresse = mots[1].substring(1, 5);
	        	switch(registre) {
	        		case 'A':
	        			String valA = CPU.getA();
	        			int ligneA =  convertirHexEnDecimal(adresse);
	        			RAM.modifierValeur(valA, ligneA);
	        			ROM.modifierValeurColonne(adresse_ROM, "B7");
	                    adresse_ROM++;
	                    ROM.modifierValeurColonne(adresse_ROM, adresse);
	                    adresse_ROM++;
	                    adresse_ROM++;
	        			break;
	        		case 'B':
	        			String valB = CPU.getB();
	        			int ligneB =  convertirHexEnDecimal(adresse);
	        			RAM.modifierValeur(valB, ligneB);
	        			ROM.modifierValeurColonne(adresse_ROM, "F7");
	                    adresse_ROM++;
	                    ROM.modifierValeurColonne(adresse_ROM, adresse);
	                    adresse_ROM++;
	                    adresse_ROM++;
	        			break;
	        		case 'D':
	        			String valD = CPU.getD();
	        			int ligneD =  convertirHexEnDecimal(adresse);
	        			RAM.modifierValeur(valD.substring(0, 2), ligneD);
	        			RAM.modifierValeur(valD.substring(2), ligneD+1);
	        			ROM.modifierValeurColonne(adresse_ROM, "FD");
	                    adresse_ROM++;
	                    ROM.modifierValeurColonne(adresse_ROM, adresse);
	                    adresse_ROM++;
	                    adresse_ROM++;
	        			break;
	        		case 'X':
	        			String valX = CPU.get_X();
	        			int ligneX =  convertirHexEnDecimal(adresse);
	        			RAM.modifierValeur(valX.substring(0, 2), ligneX);
	        			RAM.modifierValeur(valX.substring(2), ligneX+1);
	        			ROM.modifierValeurColonne(adresse_ROM, "BF");
	                    adresse_ROM++;
	                    ROM.modifierValeurColonne(adresse_ROM, adresse);
	                    adresse_ROM++;
	                    adresse_ROM++;
	        			break;
	        		case 'Y':
	        			String valY = CPU.get_Y();
	        			int ligneY =  convertirHexEnDecimal(adresse);
	        			RAM.modifierValeur(valY.substring(0, 2), ligneY);
	        			RAM.modifierValeur(valY.substring(2), ligneY+1);
	        			ROM.modifierValeurColonne(adresse_ROM, "10");
	                    adresse_ROM++;
	        			ROM.modifierValeurColonne(adresse_ROM, "BF");
	                    adresse_ROM++;
	                    ROM.modifierValeurColonne(adresse_ROM, adresse);
	                    adresse_ROM++;
	                    adresse_ROM++;
	        			break;
	        		case 'S':
	        			String valS = CPU.getS();
	        			int ligneS =  convertirHexEnDecimal(adresse);
	        			RAM.modifierValeur(valS.substring(0, 2), ligneS);
	        			RAM.modifierValeur(valS.substring(2), ligneS+1);
	        			ROM.modifierValeurColonne(adresse_ROM, "10");
	                    adresse_ROM++;
	                    ROM.modifierValeurColonne(adresse_ROM, "FF");
	                    adresse_ROM++;
	                    ROM.modifierValeurColonne(adresse_ROM, adresse);
	                    adresse_ROM++;
	                    adresse_ROM++;
	        			break;
	        		case 'U':
	        			String valU = CPU.getU();
	        			int ligneU =  convertirHexEnDecimal(adresse);
	        			RAM.modifierValeur(valU.substring(0, 2), ligneU);
	        			RAM.modifierValeur(valU.substring(2), ligneU+1);
	        			ROM.modifierValeurColonne(adresse_ROM, "FF");
	                    adresse_ROM++;
	                    ROM.modifierValeurColonne(adresse_ROM, adresse);
	                    adresse_ROM++;
	                    adresse_ROM++;
	        			break;
	        		default:
	                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
	        	}
				
			} else if (taille == 3 && mots[1].charAt(0) == '$') {		//Mode d'adressage direct
				String adresse = (String) CPU.getDP() + mots[1].substring(1, 3);
				String add = adresse.substring(2);
				switch(registre) {
        		case 'A':
        			String valA = CPU.getA();
        			int ligneA =  convertirHexEnDecimal(adresse);
        			RAM.modifierValeur(valA, ligneA);
        			ROM.modifierValeurColonne(adresse_ROM, "97");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add);
                    adresse_ROM++;
        			break;
        		case 'B':
        			String valB = CPU.getB();
        			int ligneB =  convertirHexEnDecimal(adresse);
        			RAM.modifierValeur(valB, ligneB);
        			ROM.modifierValeurColonne(adresse_ROM, "D7");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add);
                    adresse_ROM++;
        			break;
        		case 'D':
        			String valD = CPU.getD();
        			int ligneD =  convertirHexEnDecimal(adresse);
        			RAM.modifierValeur(valD.substring(0, 2), ligneD);
        			RAM.modifierValeur(valD.substring(2), ligneD+1);
        			ROM.modifierValeurColonne(adresse_ROM, "DD");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add);
                    adresse_ROM++;
        			break;
        		case 'X':
        			String valX = CPU.get_X();
        			int ligneX =  convertirHexEnDecimal(adresse);
        			RAM.modifierValeur(valX.substring(0, 2), ligneX);
        			RAM.modifierValeur(valX.substring(2), ligneX+1);
        			ROM.modifierValeurColonne(adresse_ROM, "9F");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add);
                    adresse_ROM++;
        			break;
        		case 'Y':
        			String valY = CPU.get_Y();
        			int ligneY =  convertirHexEnDecimal(adresse);
        			RAM.modifierValeur(valY.substring(0, 2), ligneY);
        			RAM.modifierValeur(valY.substring(2), ligneY+1);
        			ROM.modifierValeurColonne(adresse_ROM, "10");
                    adresse_ROM++;
        			ROM.modifierValeurColonne(adresse_ROM, "9F");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add);
                    adresse_ROM++;
        			break;
        		case 'S':
        			String valS = CPU.getS();
        			int ligneS =  convertirHexEnDecimal(adresse);
        			RAM.modifierValeur(valS.substring(0, 2), ligneS);
        			RAM.modifierValeur(valS.substring(2), ligneS+1);
        			ROM.modifierValeurColonne(adresse_ROM, "10");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, "DF");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add);
                    adresse_ROM++;
        			break;
        		case 'U':
        			String valU = CPU.getU();
        			int ligneU =  convertirHexEnDecimal(adresse);
        			RAM.modifierValeur(valU.substring(0, 2), ligneU);
        			RAM.modifierValeur(valU.substring(2), ligneU+1);
        			ROM.modifierValeurColonne(adresse_ROM, "DF");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add);
                    adresse_ROM++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
			}
		}
		
	}
	//___________________________________instruction LDA ,LDB ,LDD ,LDX ,LDY ,LDS ,LDU ___________________________________________________
	public int instr_ld(char registre, String[] mots) {
		if(mots[1].charAt(0) == '#') {		//Mode d'adressage immediat
    		String val = mots[1].substring(2);
    		String val4 = mots[1].substring(2);
    		if (mots[1].length() == 6) {
    			val4 = mots[1].substring(2, 6);
    		}
        	switch(registre) {
        		case 'A':
        			CPU.setA(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.modifierValeurColonne(adresse_ROM, "86");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, val);
                    adresse_ROM++;
        			break;
        		case 'B':
        			CPU.setB(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.modifierValeurColonne(adresse_ROM, "C6");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, val);
                    adresse_ROM++;
        			break;
        		case 'D':
        			CPU.setD(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "CC");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, val);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		case 'X':
        			CPU.setX(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "8E");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, val);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		case 'Y':
        			CPU.setY(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "10");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, "8E");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, val);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		case 'S':
        			CPU.setS(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "10");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, "CE");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, val);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		case 'U':
        			CPU.setU(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "CE");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, val);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
    	}
		if (mots[1].charAt(0) == '$') {
		int taille = mots[1].length();
		if (taille == 5)		//Mode d'adressage etendu
		{
			String adresse = mots[1].substring(1, 5);
			String val = (String) RAM.obtenirValeur(adresse);
			String val_next = (String) RAM.obtenirValeurSuivante(adresse);
			String val4 = (String) val + val_next;
        	switch(registre) {
        		case 'A':
        			CPU.setA(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.modifierValeurColonne(adresse_ROM, "B6");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresse);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		case 'B':
        			CPU.setB(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.modifierValeurColonne(adresse_ROM, "F6");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresse);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		case 'D':
        			CPU.setD(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "FC");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresse);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		case 'X':
        			CPU.setX(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "BE");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresse);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		case 'Y':
        			CPU.setY(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "10");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, "BE");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresse);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		case 'S':
        			CPU.setS(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "10");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, "FE");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresse);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		case 'U':
        			CPU.setU(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "FE");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresse);
                    adresse_ROM++;
                    adresse_ROM++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
			
		} else if (taille == 3) {		//Mode d'adressage direct
			String adresse = (String) CPU.getDP() + mots[1].substring(1, 3);
			String add1 = adresse.substring(2);
			String val = (String) RAM.obtenirValeur(adresse);
			String val_next = (String) RAM.obtenirValeurSuivante(adresse);
			String val4 = (String) val + val_next;
        	switch(registre) {
        		case 'A':
        			CPU.setA(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.modifierValeurColonne(adresse_ROM, "96");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add1);
                    adresse_ROM++;
        			break;
        		case 'B':
        			CPU.setB(val);
        			flagNeg(val,8);
    				flagZero(val);
        			ROM.modifierValeurColonne(adresse_ROM, "D6");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add1);
                    adresse_ROM++;
        			break;
        		case 'D':
        			CPU.setD(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "DC");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add1);
                    adresse_ROM++;
        			break;
        		case 'X':
        			CPU.setX(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "9E");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add1);
                    adresse_ROM++;
        			break;
        		case 'Y':
        			CPU.setY(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "10");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, "9E");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add1);
                    adresse_ROM++;
        			break;
        		case 'S':
        			CPU.setS(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "10");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, "DE");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add1);
                    adresse_ROM++;
        			break;
        		case 'U':
        			CPU.setU(val4);
        			flagNeg(val4,16);
    				flagZero(val4);
        			ROM.modifierValeurColonne(adresse_ROM, "DE");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, add1);
                    adresse_ROM++;
        			break;
        		default:
                    throw new IllegalArgumentException("Registre non pris en charge : " + registre);
        	}
		}
		}
		return 1;
	}

	private static String decimalEnHex(int value) {
        // Convertir en hexadicimal 
        String hexValue = Integer.toHexString(value).toUpperCase();

        // Pad with leading zeros to ensure a fixed width of 4 characters
        while (hexValue.length() < 4) {
            hexValue = "0" + hexValue;
        }

        return hexValue;
    }

	private static String hexToBinary(String hex, int nbrBits) {
        String binary = Integer.toBinaryString(Integer.parseInt(hex, 16));
        // Ajouter des zéros à gauche pour atteindre le nombre de bits spécifié
        while (binary.length() < nbrBits) {
            binary = "0" + binary;
        }
        return binary;
    }

	private static int  convertirHexEnDecimal(String hexValue) {
		while (hexValue.length() < 5) {
	        hexValue = "0" + hexValue;
	    }

	    // Convert hexadecimal to integer
	    int intValue = Integer.parseInt(hexValue, 16);

	    return intValue;
	}

	private static String getR(char R) {
		switch(R) {
		case 'A':
			return CPU.getA();
		case 'B':
			return CPU.getB();
		case 'D':
			return CPU.getD();
		case 'X':
			return CPU.get_X();
		case 'Y':
			return CPU.get_Y();
		case 'S':
			return CPU.getS();
		case 'U':
			return CPU.getU();
		default:
            throw new IllegalArgumentException("Registre non pris en charge : " + R);
		}
	}
	
	private static void setR(char R, String val) {
		int z = val.length();
		String val2 = "00";
		String val4 = "0000";
		if (z == 4) {
			val4 = val;
			val2 = val.substring(2);
		}
		else if (z == 2) {
			val2 = val;
			val4 = (String) CPU.getDP() + val;
		}
		
		switch(R) {
		case 'A':
			CPU.setA(val2);
			flagNeg(val2,8);
			flagZero(val2);
			break;
		case 'B':
			CPU.setB(val2);
			flagNeg(val2,8);
			flagZero(val2);
			break;
		case 'D':
			CPU.setD(val4);
			flagNeg(val4,16);
			flagZero(val4);
			break;
		case 'X':
			CPU.setX(val4);
			flagNeg(val4,16);
			flagZero(val4);
			break;
		case 'Y':
			CPU.setY(val4);
			flagNeg(val4,16);
			flagZero(val4);
			break;
		case 'S':
			CPU.setS(val4);
			flagNeg(val4,16);
			flagZero(val4);
			break;
		case 'U':
			CPU.setU(val4);
			flagNeg(val4,16);
			flagZero(val4);
			break;
		default:
            throw new IllegalArgumentException("Registre non pris en charge : " + R);
		}
	}
	
	private static String addBinary(String binary1, String binary2, int nbrBits) {
        int num1 = Integer.parseInt(binary1, 2);
        int num2 = Integer.parseInt(binary2, 2);
        int sum = num1 + num2;
        String binarySum = Integer.toBinaryString(sum);
        while (binarySum.length() < nbrBits) {
            binarySum = "0" + binarySum;
        }
        return binarySum;
    }
	
	private static String subtractBinary(String binary1, String binary2) {
	    // Convertir les nombres binaires en décimaux
	    int num1 = Integer.parseInt(binary1, 2);
	    int num2 = Integer.parseInt(binary2, 2);

	    // Soustraire les décimaux
	    int difference = num1 - num2;

	    // Convertir le résultat en binaire
	    return Integer.toBinaryString(difference);
	}
	
	private static boolean debordementDetectADD(String op1, String op2, int nbrBits) {
        // Convertir les opérandes hexadécimales en binaire
        String binaryOp1 = hexToBinary(op1,nbrBits);
        String binaryOp2 = hexToBinary(op2,nbrBits);

        // Effectuer l'addition binaire
        String binaryResult = addBinary(binaryOp1, binaryOp2, nbrBits);

        // Vérifier le débordement par comparaison des signes
        char signOp1 = binaryOp1.charAt(0);
        char signOp2 = binaryOp2.charAt(0);
        char signResult = binaryResult.charAt(0);

        if ((signOp1 == signOp2) && (signOp1 != signResult)) {
            return true; // Débordement détecté
        }

        // Vérifier le dépassement de la capacité
        if (binaryResult.length() > nbrBits) {
            return true; // Débordement détecté
        }

        return false; // Pas de débordement
    }
	
	private static boolean debordementDetectSUB(String op1, String op2, int nbrBits) {
	    // Convertir les opérandes hexadécimales en binaire
	    String binaryOp1 = hexToBinary(op1, nbrBits);
	    String binaryOp2 = hexToBinary(op2, nbrBits);

	    // Effectuer la soustraction binaire
	    String binaryResult = subtractBinary(binaryOp1, binaryOp2);

	    // Vérifier le débordement par comparaison des signes
	    char signOp1 = binaryOp1.charAt(0);
	    char signOp2 = binaryOp2.charAt(0);
	    char signResult = binaryResult.charAt(0);

	    if ((signOp1 != signOp2) && (signOp1 != signResult)) {
	        return true; // Débordement détecté
	    }

	    // Vérifier le dépassement de la capacité
	    if (binaryResult.length() > nbrBits) {
	        return true; // Débordement détecté
	    }

	    return false; // Pas de débordement
	}
	
	private static boolean debordementDetectMUL(String op1, String op2, int nbrBits) {
        String binaryOp1 = hexToBinary(op1, nbrBits);
        String binaryOp2 = hexToBinary(op2, nbrBits);

        long num1 = Long.parseLong(binaryOp1, 2);
        long num2 = Long.parseLong(binaryOp2, 2);

        if (num1 * num2 >= Math.pow(2, nbrBits)) {
            return true; // Débordement détecté
        }

        return false; // Pas de débordement
    }

    private static boolean detectCarryMultiplication(String op1, String op2, int nbrBits) {
        String binaryOp1 = hexToBinary(op1, nbrBits);
        String binaryOp2 = hexToBinary(op2, nbrBits);

        long num1 = Long.parseLong(binaryOp1, 2);
        long num2 = Long.parseLong(binaryOp2, 2);

        String product = Long.toBinaryString(num1 * num2);
        return product.length() > nbrBits; // Retenue détectée si la longueur du produit dépasse nbrBits
    }
	
    public static boolean testBitPoidsFort(String hex, int nbrBits) {
        // Convertir les valeurs hexadécimales en binaire
        String binary1 = hexToBinary(hex,nbrBits);
        // Ajouter les 0
        char msb = binary1.charAt(0);
        // Vérifier si le bit de poids fort est positionitif (0) ou négatif (1)
        return msb == '1';
    }
    
    private static boolean detectCarryAddition(String op1, String op2, int nbrBits) {
        // Convertir les opérandes hexadécimales en binaire
        String binaryOp1 = hexToBinary(op1, nbrBits);
        String binaryOp2 = hexToBinary(op2, nbrBits);

        // Effectuer l'addition binaire
        String binaryResult = addBinary(binaryOp1, binaryOp2,nbrBits);

        // Vérifier la retenue en comparant la longueur des résultats
        return binaryResult.length() > nbrBits;
    }
    
    private static boolean detectCarrySubtraction(String op1, String op2, int nbrBits) {
        // Convertir les opérandes hexadécimales en binaire
        String binaryOp1 = hexToBinary(op1, nbrBits);
        String binaryOp2 = hexToBinary(op2, nbrBits);

        // Effectuer la soustraction binaire
        String binaryResult = subtractBinary(binaryOp1, binaryOp2);

        // Vérifier la retenue en comparant la longueur des résultats
        return binaryResult.length() > nbrBits;
    }

    public static void flagADD(String op1,String op2, int nbrBits) {
    	if(debordementDetectADD(op1,op2,nbrBits))
    		CPU.setV("1");
    	else
    		CPU.setV("0");
    	if(detectCarryAddition(op1,op2,nbrBits))
    		CPU.setC("1");
    	else
    		CPU.setC("0");
    		
    }
    
    public static void flagSUB(String op1,String op2,int nbrBits) {
    	if(debordementDetectSUB(op1,op2,nbrBits))
    		CPU.setV("1");
    	else
        		CPU.setV("0");
    	if(detectCarrySubtraction(op1,op2,nbrBits))
    		CPU.setC("1");
    	else
    		CPU.setC("0");
    }
    
    public static void flagMUL(String op1,String op2,int nbrBits) {
    	if(debordementDetectMUL(op1,op2,nbrBits))
    		CPU.setV("1");
    	else
        		CPU.setV("0");
    	if(detectCarryMultiplication(op1,op2,nbrBits))
    		CPU.setC("1");
    	else
    		CPU.setC("0");
    }
 
    public static void flagNeg(String op, int nbrBits) {
    	if(testBitPoidsFort(op,nbrBits)) {
    		CPU.setN("1");
    	}
    	else 
    		CPU.setN("0");
    }
    
    private static void flagZero(String hexValue) {
        // Convert hexadecimal to integer
        int value = Integer.parseInt(hexValue, 16);
        // Update the Z flag in CPU based on the value
        if (value == 0) {
            CPU.setZ("1");
        } else {
            CPU.setZ("0");
        }
    }
    
	private void  trouver_Toutes_Les_Etiquettes(String asmbl) {
		int nombreLignes=0;
		String[] ligne = asmbl.split("\\n");
		while (!ligne[nombreLignes].equals("END")) {
			if (ligne[nombreLignes].charAt(ligne[nombreLignes].length()-1) == ':') {
    			position = nombreLignes;
    			System.out.println("eitiquette ligne: "+nombreLignes);
    			etiquette.add(ligne[nombreLignes]+position);
    		}
			nombreLignes++;
		}
	}
//_________________inSTRUCTION JMP __________________________________
	private void instr_jmp (String mots) {
		for(String this_etiquette : etiquette) {
			if(this_etiquette.substring(0, this_etiquette.length()-2).equals(mots)) {
				i = this_etiquette.charAt(this_etiquette.length()-1) - '0';
				break;
			}
		}
	}

}