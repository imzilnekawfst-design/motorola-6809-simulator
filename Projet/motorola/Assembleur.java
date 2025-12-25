package motorola_6809;


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
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
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
			 //  ignorer les étiquettes
        if (ligne[i].trim().endsWith(":")) {
            i++;
            return;
        }
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
				 // ignorer les étiquettes
        if (ligne[i].trim().endsWith(":")) {
            i++;
            return;
        }
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
		Programme.ligneCourante = 0;
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
	    	}  else if (instr.equals("AND")) {
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
	    		} else if (instr.equals("NOP")) {
	    			instr_nop(); 
	    		} else if (instr.equals("TFR")) {
	    			instr_tfr(mots); 
	    		} 
	    		else if (instr.equals("MUL")) {
	    			instr_mul(); 
	    		} else if (instr.equals("ABX")) {
	    			instr_abx(); 
	    		}if(instr.equals("TEST")) {
					instr_test(registre);
					

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

    /* =========================
       MODE IMMÉDIAT : #$xx
       ========================= */
    if (mots[1].charAt(0) == '#' && mots[1].charAt(1) == '$') {

        // Valeur immédiate (sans #$)
        String valeurHex = mots[1].substring(2);

        switch (registre) {

            case 'A':
                String registreA = CPU.getA();
                int resultatA = convertirHexEnDecimal(registreA)
                              | convertirHexEnDecimal(valeurHex);

                String resultatHexA = decimalEnHex(resultatA);
                CPU.setA(resultatHexA.substring(resultatHexA.length() - 2));

                // Opcode ORA immédiat
                ROM.modifierValeurColonne(adresse_ROM, "8A");
                adresse_ROM++;
                ROM.modifierValeurColonne(adresse_ROM, valeurHex);
                adresse_ROM++;
                break;

            case 'B':
                String registreB = CPU.getB();
                int resultatB = convertirHexEnDecimal(registreB)
                              | convertirHexEnDecimal(valeurHex);

                String resultatHexB = decimalEnHex(resultatB);
                CPU.setB(resultatHexB.substring(resultatHexB.length() - 2));

                // Opcode ORB immédiat
                ROM.modifierValeurColonne(adresse_ROM, "CA");
                adresse_ROM++;
                ROM.modifierValeurColonne(adresse_ROM, valeurHex);
                adresse_ROM++;
                break;

            default:
                throw new IllegalArgumentException("Registre non supporté : " + registre);
        }
    }

    /* =========================
       MODE DIRECT ou ÉTENDU : $xx / $xxxx
       ========================= */
    else if (mots[1].charAt(0) == '$') {

        int longueurMot = mots[1].length();

        /* -------- MODE ÉTENDU ($xxxx) -------- */
        if (longueurMot == 5) {

            String adresseMemoire = mots[1].substring(1, 5);
            String valeurMemoire = (String) RAM.obtenirValeur(adresseMemoire);

            switch (registre) {

                case 'A':
                    String valA = CPU.getA();
                    int resA = convertirHexEnDecimal(valA)
                             | convertirHexEnDecimal(valeurMemoire);

                    String resHexA = decimalEnHex(resA);
                    CPU.setA(resHexA.substring(resHexA.length() - 2));

                    // Opcode ORA étendu
                    ROM.modifierValeurColonne(adresse_ROM, "BA");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseMemoire);
                    adresse_ROM += 2;
                    break;

                case 'B':
                    String valB = CPU.getB();
                    int resB = convertirHexEnDecimal(valB)
                             | convertirHexEnDecimal(valeurMemoire);

                    String resHexB = decimalEnHex(resB);
                    CPU.setB(resHexB.substring(resHexB.length() - 2));

                    // Opcode ORB étendu
                    ROM.modifierValeurColonne(adresse_ROM, "FA");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseMemoire);
                    adresse_ROM += 2;
                    break;

                default:
                    throw new IllegalArgumentException("Registre non supporté : " + registre);
            }
        }

        /* -------- MODE DIRECT ($xx) -------- */
        else if (longueurMot == 3) {

            String adresseComplete = CPU.getDP() + mots[1].substring(1, 3);
            String adresseDirecte = adresseComplete.substring(2);
            String valeurMemoire = (String) RAM.obtenirValeur(adresseComplete);

            switch (registre) {

                case 'A':
                    int resA = convertirHexEnDecimal(CPU.getA())
                             | convertirHexEnDecimal(valeurMemoire);

                    CPU.setA(decimalEnHex(resA).substring(
                            decimalEnHex(resA).length() - 2));

                    // Opcode ORA direct
                    ROM.modifierValeurColonne(adresse_ROM, "9A");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseDirecte);
                    adresse_ROM++;
                    break;

                case 'B':
                    int resB = convertirHexEnDecimal(CPU.getB())
                             | convertirHexEnDecimal(valeurMemoire);

                    CPU.setB(decimalEnHex(resB).substring(
                            decimalEnHex(resB).length() - 2));

                    // Opcode ORB direct
                    ROM.modifierValeurColonne(adresse_ROM, "DA");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseDirecte);
                    adresse_ROM++;
                    break;

                default:
                    throw new IllegalArgumentException("Registre non supporté : " + registre);
            }
        }
    }

    /* =========================
       MISE À JOUR DES FLAGS
       ========================= */
    int resultatFinal = convertirHexEnDecimal(getR(registre));

    if (resultatFinal == 0) {
        CPU.setZ("1");
        CPU.setN("0");
    }
    else if ((resultatFinal & 0x80) != 0) {
        CPU.setN("1");
        CPU.setZ("0");
    }
    else {
        CPU.setN("0");
        CPU.setZ("0");
    }
}


//____________________________instruction AND ______________________________________________________
	private void instr_and(char registre, String[] mots) {
		
    
      // Mode immédiat : #$xx
      
    if (mots[1].charAt(0) == '#') {
        String valeurHex = mots[1].substring(2);

        switch (registre) {
            case 'A':
                int resA = convertirHexEnDecimal(CPU.getA()) 
                         & convertirHexEnDecimal(valeurHex);
                CPU.setA(decimalEnHex(resA).substring(decimalEnHex(resA).length() - 2));

                ROM.modifierValeurColonne(adresse_ROM, "84"); // Opcode ANDA immédiat
                adresse_ROM++;
                ROM.modifierValeurColonne(adresse_ROM, valeurHex);
                adresse_ROM++;
                break;

            case 'B':
                int resB = convertirHexEnDecimal(CPU.getB()) 
                         & convertirHexEnDecimal(valeurHex);
                CPU.setB(decimalEnHex(resB).substring(decimalEnHex(resB).length() - 2));

                ROM.modifierValeurColonne(adresse_ROM, "C4"); // Opcode ANDB immédiat
                adresse_ROM++;
                ROM.modifierValeurColonne(adresse_ROM, valeurHex);
                adresse_ROM++;
                break;

            default:
                throw new IllegalArgumentException("Registre non supporté : " + registre);
        }
    }

    
      // Mode direct / étendu : $xx / $xxxx
       
    else if (mots[1].charAt(0) == '$') {
        int longueurMot = mots[1].length();

        /* Mode étendu ($xxxx) */
        if (longueurMot == 5) {
            String adresseMemoire = mots[1].substring(1, 5);
            String valeurMemoire = (String) RAM.obtenirValeur(adresseMemoire);

            switch (registre) {
                case 'A':
                    int resA = convertirHexEnDecimal(CPU.getA()) 
                             & convertirHexEnDecimal(valeurMemoire);
                    CPU.setA(decimalEnHex(resA).substring(decimalEnHex(resA).length() - 2));

                    ROM.modifierValeurColonne(adresse_ROM, "B4"); // Opcode ANDA étendu
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseMemoire);
                    adresse_ROM += 2;
                    break;

                case 'B':
                    int resB = convertirHexEnDecimal(CPU.getB()) 
                             & convertirHexEnDecimal(valeurMemoire);
                    CPU.setB(decimalEnHex(resB).substring(decimalEnHex(resB).length() - 2));

                    ROM.modifierValeurColonne(adresse_ROM, "F4"); // Opcode ANDB étendu
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseMemoire);
                    adresse_ROM += 2;
                    break;

                default:
                    throw new IllegalArgumentException("Registre non supporté : " + registre);
            }
        }

        /* Mode direct ($xx) */
        else if (longueurMot == 3) {
            String adresseComplete = CPU.getDP() + mots[1].substring(1, 3);
            String adresseDirecte = adresseComplete.substring(2);
            String valeurMemoire = (String) RAM.obtenirValeur(adresseComplete);

            switch (registre) {
                case 'A':
                    int resA = convertirHexEnDecimal(CPU.getA()) 
                             & convertirHexEnDecimal(valeurMemoire);
                    CPU.setA(decimalEnHex(resA).substring(decimalEnHex(resA).length() - 2));

                    ROM.modifierValeurColonne(adresse_ROM, "94"); // Opcode ANDA direct
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseDirecte);
                    adresse_ROM++;
                    break;

                case 'B':
                    int resB = convertirHexEnDecimal(CPU.getB()) 
                             & convertirHexEnDecimal(valeurMemoire);
                    CPU.setB(decimalEnHex(resB).substring(decimalEnHex(resB).length() - 2));

                    ROM.modifierValeurColonne(adresse_ROM, "D4"); // Opcode ANDB direct
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseDirecte);
                    adresse_ROM++;
                    break;

                default:
                    throw new IllegalArgumentException("Registre non supporté : " + registre);
            }
        }
    }
		
int resultat = convertirHexEnDecimal(getR(registre));
if(resultat == 0) {
    CPU.setZ("1");  // Résultat = 0 → flag Z
    CPU.setN("0");  // Résultat non négatif → flag N
} else if((resultat & 0x80) != 0) {
    CPU.setN("1");  // Bit de signe = 1 → flag N
    CPU.setZ("0");  // Résultat ≠ 0 → flag Z
} else {
    CPU.setN("0");  // Bit de signe = 0 → flag N
    CPU.setZ("0");  // Résultat ≠ 0 → flag Z
}

// Flags V et C sont toujours 0 pour AND
CPU.setV("0");
CPU.setC("0");

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
	//________Méthode pour detecter la retenue  en multiplucation
    private static boolean detectCarryMultiplication(String op1, String op2, int nbrBits) {
        String binaryOp1 = hexToBinary(op1, nbrBits);
        String binaryOp2 = hexToBinary(op2, nbrBits);

        long num1 = Long.parseLong(binaryOp1, 2);
        long num2 = Long.parseLong(binaryOp2, 2);

        String product = Long.toBinaryString(num1 * num2);
        return product.length() > nbrBits; // Retenue détectée si la longueur du produit dépasse nbrBits
    }
	  //____________ Méthode pour mettre à jour les drapeaux après une opération  MUL
    public static void flagMUL(String op1,String op2,int nbrBits) {
    	if(detectCarryMultiplication(op1,op2,nbrBits))
    		CPU.setC("1");
    	else
    		CPU.setC("0");
    }
	
//__________________________instrection DEC A , B________________________________________________________
private void instr_dec(char registre) {
    // Obtenir la valeur actuelle du registre
    String valeurHex;
    switch (registre) {
        case 'A': valeurHex = CPU.getA(); break;
        case 'B': valeurHex = CPU.getB(); break;
        default: throw new IllegalArgumentException("Registre non pris en charge : " + registre);
    }

    // Décrémenter la valeur
    int valeurDecimale = convertirHexEnDecimal(valeurHex) - 1;
    String nouvelleValeur = decimalEnHex(valeurDecimale).substring(decimalEnHex(valeurDecimale).length() - 2);

    // Mettre à jour le registre
    switch (registre) {
        case 'A': CPU.setA(nouvelleValeur); break;
        case 'B': CPU.setB(nouvelleValeur); break;
    }

    // Mettre à jour les flags
    flagNeg(nouvelleValeur, 8);
    flagZero(nouvelleValeur);

    // Mettre à jour le PC
    pc = ROM.obtenirAdresse(adresse_ROM);
    CPU.setPC(pc);
    CPU.setinstru_PC(instru_pc);

    // Écrire l'opcode dans la ROM
    String opcode;
    switch (registre) {
        case 'A': opcode = "4A"; break;
        case 'B': opcode = "5A"; break;
        default: throw new IllegalArgumentException("Registre non pris en charge : " + registre);
    }
    ROM.modifierValeurColonne(adresse_ROM, opcode);
    adresse_ROM++;
}


	
//______________________________instruction TFR__________________________________________
	private void instr_tfr(String[] mots) {
     
	// Séparer les registres source et destination (Ex: "D,X" ou "DP,A")
	String operands = mots[1]; 
    String[] regs = operands.split(",");
    String regSource = regs[0].trim();
    String regDest = regs[1].trim();

    // Lire la valeur du registre source
    String valeur;
    if (regSource.equals("DP")) {
        valeur = CPU.getDP();
    } else if (regSource.length() == 1) {
        valeur = getR(regSource.charAt(0));
    } else {
        throw new IllegalArgumentException("Registre source invalide : " + regSource);
    }

    // Écrire la valeur dans le registre destination
    if (regDest.equals("DP")) {
        CPU.setDP(valeur);
    } else if (regDest.length() == 1) {
        setR(regDest.charAt(0), valeur);
    } else {
        throw new IllegalArgumentException("Registre destination invalide : " + regDest);
    }

    // Mise à jour de la ROM
    ROM.modifierValeurColonne(adresse_ROM, "1F");
    adresse_ROM++;
}

// ________________________________________instruction SUB_______________________________________________ 
	private void instr_sub(char registre, String[] mots) {
    
    System.out.println("=== Instruction SUB ===");
    
    String operande = mots[1];
    
    // Déterminer le mode d'adressage
    if (operande.startsWith("#$")) {
        // Mode immédiat : #$12 ou #$1234
        sub_immediat(registre, operande);
    } 
    else if (operande.startsWith("$")) {
        // Mode direct ou étendu : $12 ou $1234
        if (operande.length() == 3) {
            sub_direct(registre, operande);
        } else {
            sub_etendu(registre, operande);
        }
    }
}

// ========== MODE IMMÉDIAT ==========
private void sub_immediat(char registre, String operande) {
    
    // Extraire la valeur : #$12 -> "12"
    String valeur = operande.substring(2);
    
    switch(registre) {
        case 'A':
            sub_registre_8bits(CPU.getA(), valeur, "80", 'A');
            break;
        case 'B':
            sub_registre_8bits(CPU.getB(), valeur, "C0", 'B');
            break;
        case 'D':
            sub_registre_16bits(CPU.getD(), valeur, "83");
            break;
    }
}

// ========== MODE DIRECT ==========
private void sub_direct(char registre, String operande) {
    
    // Calculer l'adresse : DP + offset
    String offset = operande.substring(1);
    String adresse = CPU.getDP() + offset;
    String valeur = (String) RAM.obtenirValeur(adresse);
    
    switch(registre) {
        case 'A':
            sub_registre_8bits(CPU.getA(), valeur, "90", 'A');
            ROM.modifierValeurColonne(adresse_ROM - 1, offset);
            break;
        case 'B':
            sub_registre_8bits(CPU.getB(), valeur, "D0", 'B');
            ROM.modifierValeurColonne(adresse_ROM - 1, offset);
            break;
        case 'D':
            String valeur16 = valeur + RAM.obtenirValeurSuivante(adresse);
            sub_registre_16bits(CPU.getD(), valeur16, "93");
            ROM.modifierValeurColonne(adresse_ROM - 1, offset);
            break;
    }
}

// ========== MODE ÉTENDU ==========
private void sub_etendu(char registre, String operande) {
    
    // Extraire l'adresse : $1234
    String adresse = operande.substring(1);
    String valeur = (String) RAM.obtenirValeur(adresse);
    
    switch(registre) {
        case 'A':
            sub_registre_8bits(CPU.getA(), valeur, "B0", 'A');
            ROM.modifierValeurColonne(adresse_ROM - 1, adresse);
            break;
        case 'B':
            sub_registre_8bits(CPU.getB(), valeur, "F0", 'B');
            ROM.modifierValeurColonne(adresse_ROM - 1, adresse);
            break;
        case 'D':
            String valeur16 = valeur + RAM.obtenirValeurSuivante(adresse);
            sub_registre_16bits(CPU.getD(), valeur16, "B3");
            ROM.modifierValeurColonne(adresse_ROM - 1, adresse);
            break;
    }
}

// ========== SOUSTRACTION 8 BITS ==========
private void sub_registre_8bits(String valeurReg, String valeurSub, String opcode, char nomReg) {
    
    // Convertir en nombres
    int reg = convertirHexEnDecimal(valeurReg);
    int sub = convertirHexEnDecimal(valeurSub);
    
    // Calculer le résultat
    int resultat = reg - sub;
    
    // Convertir en hexa (2 caractères)
    String resultatHex = String.format("%02X", resultat & 0xFF);
    
    // Mettre à jour le registre
    if (nomReg == 'A') {
        CPU.setA(resultatHex);
    } else {
        CPU.setB(resultatHex);
    }
    
    // Mettre à jour les flags
    mettreAJourFlags(reg, sub, resultat, 8);
    
    // Écrire dans la ROM
    ROM.modifierValeurColonne(adresse_ROM, opcode);
    adresse_ROM++;
    ROM.modifierValeurColonne(adresse_ROM, valeurSub);
    adresse_ROM++;
    
    System.out.println("SUB " + nomReg + " : " + reg + " - " + sub + " = " + (resultat & 0xFF));
}

// ========== SOUSTRACTION 16 BITS ==========
private void sub_registre_16bits(String valeurReg, String valeurSub, String opcode) {
    
    // Convertir en nombres
    int reg = convertirHexEnDecimal(valeurReg);
    int sub = convertirHexEnDecimal(valeurSub);
    
    // Calculer le résultat
    int resultat = reg - sub;
    
    // Convertir en hexa (4 caractères)
    String resultatHex = String.format("%04X", resultat & 0xFFFF);
    
    // Mettre à jour le registre D
    CPU.setD(resultatHex);
    
    // Mettre à jour les flags
    mettreAJourFlags(reg, sub, resultat, 16);
    
    // Écrire dans la ROM
    ROM.modifierValeurColonne(adresse_ROM, opcode);
    adresse_ROM++;
    ROM.modifierValeurColonne(adresse_ROM, valeurSub.substring(0, 2));
    adresse_ROM++;
    ROM.modifierValeurColonne(adresse_ROM, valeurSub.substring(2, 4));
    adresse_ROM++;
    
    System.out.println("SUB D : " + reg + " - " + sub + " = " + (resultat & 0xFFFF));
}

// ========== MISE À JOUR DES FLAGS ==========
private void mettreAJourFlags(int valeurReg, int valeurSub, int resultat, int bits) {
    
    int masque = (bits == 8) ? 0xFF : 0xFFFF;
    int resultatMasque = resultat & masque;
    
    // Flag Z (Zero) : résultat = 0
    if (resultatMasque == 0) {
        CPU.setZ("1");
    } else {
        CPU.setZ("0");
    }
    
    // Flag N (Negative) : bit de poids fort = 1
    int bitPoidsFort = (bits == 8) ? 0x80 : 0x8000;
    if ((resultatMasque & bitPoidsFort) != 0) {
        CPU.setN("1");
    } else {
        CPU.setN("0");
    }
    
    // Flag C (Carry) : débordement non signé
    if (resultat < 0) {
        CPU.setC("1");
    } else {
        CPU.setC("0");
    }
    
    // Flag V (Overflow) : débordement signé
    int maxPos = (bits == 8) ? 127 : 32767;
    int minNeg = (bits == 8) ? -128 : -32768;
    
    if (resultat > maxPos || resultat < minNeg) {
        CPU.setV("1");
    } else {
        CPU.setV("0");
    }
}

	//____________________________________________instruction NOP ______________________________________________________________________
	private void instr_nop() {
		ROM.modifierValeurColonne(adresse_ROM, "12");
        adresse_ROM++;		
	}
	//____________________________________________instruction INC A , B _____________________________________________________________________
	private void instr_inc(char registre) {
    	switch(registre) {
    		case 'A':
    			String valA = CPU.getA();
    			int valintA =  convertirHexEnDecimal(valA) + 1;
    			String valAA = decimalEnHex(valintA);
    			CPU.setA(valAA.substring(2));
    			ROM.modifierValeurColonne(adresse_ROM, "4C");
                adresse_ROM++;
    			break;
    		case 'B':
    			String valB = CPU.getB();
    			int valintB =  convertirHexEnDecimal(valB) + 1;
    			String valBB = decimalEnHex(valintB);
    			CPU.setB(valBB.substring(2));
    			ROM.modifierValeurColonne(adresse_ROM, "5C");
                adresse_ROM++;
    			break;
    		default:
                throw new IllegalArgumentException("Registre non pris en charge : " + registre);
    	}
		
	}
	//_____________________________________________instructin CLRA ,CLRB _____________________________________________________________________
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
				 CPU.setZ("1");
				 CPU.setN("0");
				 CPU.setV("0");
				 CPU.setC("0");
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

   // _______________________ Met à jour les drapeaux après une addition
public static void flagADD(String op1, String op2, int nbBits) {
    if (debordAdd(op1, op2, nbBits)) {
        CPU.setV("1");
    } else {
        CPU.setV("0");
    }

    if (retenueAdd(op1, op2, nbBits)) {
        CPU.setC("1");
    } else {
        CPU.setC("0");
    }
}

// _______________________ Vérifie la retenue
private static boolean retenueAdd(String op1, String op2, int nbBits) {
    String bin1 = hexToBinary(op1, nbBits);
    String bin2 = hexToBinary(op2, nbBits);
    String resBin = addBin(bin1, bin2, nbBits);

    if (resBin.length() > nbBits) {
        return true;
    } else {
        return false;
    }
}

// _______________________ Vérifie le débordement
private static boolean debordAdd(String op1, String op2, int nbBits) {
    String bin1 = hexToBinary(op1, nbBits);
    String bin2 = hexToBinary(op2, nbBits);
    String resBin = addBin(bin1, bin2, nbBits);

   char signe1 = bin1.charAt(0);
    char signe2 = bin2.charAt(0);
    char signeRes = resBin.charAt(0);

      return ((signe1 == signe2) && (signe1 != signeRes)) || (resBin.length() > nbBits);
}
 // _______________________ Addition binaire
private static String addBin(String b1, String b2, int nbBits) {
    int n1 = Integer.parseInt(b1, 2);
    int n2 = Integer.parseInt(b2, 2);
    int somme = n1 + n2;

    String res = Integer.toBinaryString(somme);

    // Ajouter des zéros à gauche si nécessaire
    while (res.length() < nbBits) {
        res = "0" + res;
    }

    return res;
}



// ___________________________________________instruction STA ,STB ,STD ,STX ,STY ,STS ,STU_____________________________________________________________________________________
	private void instr_st(char registre, String[] mots) {
		 /* =========================
       MODE ÉTENDU ($xxxx)
       ========================= */
    if (mots[1].charAt(0) == '$') {

        int longueurMot = mots[1].length();

        if (longueurMot == 5) { // $xxxx

            String adresseMemoire = mots[1].substring(1, 5);

            switch (registre) {

                case 'A':
                    String valA = CPU.getA();
                    int ligneA = convertirHexEnDecimal(adresseMemoire);

                    flagNeg(valA, 8);
                    flagZero(valA);

                    RAM.modifierValeur(valA, ligneA);

                    ROM.modifierValeurColonne(adresse_ROM, "B7"); // Opcode STA étendu
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseMemoire);
                    adresse_ROM += 2;
                    break;

                case 'B':
                    String valB = CPU.getB();
                    int ligneB = convertirHexEnDecimal(adresseMemoire);

                    flagNeg(valB, 8);
                    flagZero(valB);

                    RAM.modifierValeur(valB, ligneB);

                    ROM.modifierValeurColonne(adresse_ROM, "F7");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseMemoire);
                    adresse_ROM += 2;
                    break;

                case 'D':
                    String valD = CPU.getD();
                    int ligneD = convertirHexEnDecimal(adresseMemoire);

                    flagNeg(valD, 16);
                    flagZero(valD);

                    RAM.modifierValeur(valD.substring(0, 2), ligneD);
                    RAM.modifierValeur(valD.substring(2), ligneD + 1);

                    ROM.modifierValeurColonne(adresse_ROM, "FD");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseMemoire);
                    adresse_ROM += 2;
                    break;

                case 'X':
                    String valX = CPU.get_X();
                    int ligneX = convertirHexEnDecimal(adresseMemoire);

                    flagNeg(valX, 16);
                    flagZero(valX);

                    RAM.modifierValeur(valX.substring(0, 2), ligneX);
                    RAM.modifierValeur(valX.substring(2), ligneX + 1);

                    ROM.modifierValeurColonne(adresse_ROM, "BF");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseMemoire);
                    adresse_ROM += 2;
                    break;

                case 'Y':
                    String valY = CPU.get_Y();
                    int ligneY = convertirHexEnDecimal(adresseMemoire);

                    flagNeg(valY, 16);
                    flagZero(valY);

                    RAM.modifierValeur(valY.substring(0, 2), ligneY);
                    RAM.modifierValeur(valY.substring(2), ligneY + 1);

                    ROM.modifierValeurColonne(adresse_ROM, "10");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, "BF");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseMemoire);
                    adresse_ROM += 2;
                    break;

                case 'S':
                    String valS = CPU.getS();
                    int ligneS = convertirHexEnDecimal(adresseMemoire);

                    flagNeg(valS, 16);
                    flagZero(valS);

                    RAM.modifierValeur(valS.substring(0, 2), ligneS);
                    RAM.modifierValeur(valS.substring(2), ligneS + 1);

                    ROM.modifierValeurColonne(adresse_ROM, "10");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, "FF");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseMemoire);
                    adresse_ROM += 2;
                    break;

                case 'U':
                    String valU = CPU.getU();
                    int ligneU = convertirHexEnDecimal(adresseMemoire);

                    flagNeg(valU, 16);
                    flagZero(valU);

                    RAM.modifierValeur(valU.substring(0, 2), ligneU);
                    RAM.modifierValeur(valU.substring(2), ligneU + 1);

                    ROM.modifierValeurColonne(adresse_ROM, "FF");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseMemoire);
                    adresse_ROM += 2;
                    break;

                default:
                    throw new IllegalArgumentException("Registre non supporté : " + registre);
            }
        }

        /* =========================
           MODE DIRECT ($xx)
           ========================= */
        else if (longueurMot == 3) { 

            String adresseComplete = CPU.getDP() + mots[1].substring(1, 3);
            String addDirect = adresseComplete.substring(2);

            switch (registre) {

                case 'A':
                    String valA = CPU.getA();
                    int ligneA = convertirHexEnDecimal(adresseComplete);

                    flagNeg(valA, 8);
                    flagZero(valA);

                    RAM.modifierValeur(valA, ligneA);

                    ROM.modifierValeurColonne(adresse_ROM, "97");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, addDirect);
                    adresse_ROM++;
                    break;

                case 'B':
                    String valB = CPU.getB();
                    int ligneB = convertirHexEnDecimal(adresseComplete);

                    flagNeg(valB, 8);
                    flagZero(valB);

                    RAM.modifierValeur(valB, ligneB);

                    ROM.modifierValeurColonne(adresse_ROM, "D7");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, addDirect);
                    adresse_ROM++;
                    break;

                case 'D':
                    String valD = CPU.getD();
                    int ligneD = convertirHexEnDecimal(adresseComplete);

                    flagNeg(valD, 16);
                    flagZero(valD);

                    RAM.modifierValeur(valD.substring(0, 2), ligneD);
                    RAM.modifierValeur(valD.substring(2), ligneD + 1);

                    ROM.modifierValeurColonne(adresse_ROM, "DD");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, addDirect);
                    adresse_ROM++;
                    break;

                case 'X':
                    String valX = CPU.get_X();
                    int ligneX = convertirHexEnDecimal(adresseComplete);

                    flagNeg(valX, 16);
                    flagZero(valX);

                    RAM.modifierValeur(valX.substring(0, 2), ligneX);
                    RAM.modifierValeur(valX.substring(2), ligneX + 1);

                    ROM.modifierValeurColonne(adresse_ROM, "9F");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, addDirect);
                    adresse_ROM++;
                    break;

                case 'Y':
                    String valY = CPU.get_Y();
                    int ligneY = convertirHexEnDecimal(adresseComplete);

                    flagNeg(valY, 16);
                    flagZero(valY);

                    RAM.modifierValeur(valY.substring(0, 2), ligneY);
                    RAM.modifierValeur(valY.substring(2), ligneY + 1);

                    ROM.modifierValeurColonne(adresse_ROM, "10");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, "9F");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, addDirect);
                    adresse_ROM++;
                    break;

                case 'S':
                    String valS = CPU.getS();
                    int ligneS = convertirHexEnDecimal(adresseComplete);

                    flagNeg(valS, 16);
                    flagZero(valS);

                    RAM.modifierValeur(valS.substring(0, 2), ligneS);
                    RAM.modifierValeur(valS.substring(2), ligneS + 1);

                    ROM.modifierValeurColonne(adresse_ROM, "10");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, "DF");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, addDirect);
                    adresse_ROM++;
                    break;

                case 'U':
                    String valU = CPU.getU();
                    int ligneU = convertirHexEnDecimal(adresseComplete);

                    flagNeg(valU, 16);
                    flagZero(valU);

                    RAM.modifierValeur(valU.substring(0, 2), ligneU);
                    RAM.modifierValeur(valU.substring(2), ligneU + 1);

                    ROM.modifierValeurColonne(adresse_ROM, "DF");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, addDirect);
                    adresse_ROM++;
                    break;

                default:
                    throw new IllegalArgumentException("Registre non supporté : " + registre);
            }
        }
    }

    // Toujours remettre le flag V à 0
    CPU.setV("0");
}
	//___________________________________instruction LDA ,LDB ,LDD ,LDX ,LDY ,LDS ,LDU ___________________________________________________
	public int instr_ld(char registre, String[] mots) {
    /* =========================
       MODE IMMÉDIAT : #$xx ou #$xxxx
       ========================= */
    if (mots[1].charAt(0) == '#') {

        String valeur = mots[1].substring(2);   // Valeur 8 bits
        String valeur16 = mots[1].substring(2); // Valeur 16 bits pour D/X/Y/S/U

        if (mots[1].length() == 6) {
            valeur16 = mots[1].substring(2, 6);
        }

        switch (registre) {

            case 'A':
                CPU.setA(valeur);
                flagNeg(valeur, 8);
                flagZero(valeur);
                ROM.modifierValeurColonne(adresse_ROM, "86");
                adresse_ROM++;
                ROM.modifierValeurColonne(adresse_ROM, valeur);
                adresse_ROM++;
                break;

            case 'B':
                CPU.setB(valeur);
                flagNeg(valeur, 8);
                flagZero(valeur);
                ROM.modifierValeurColonne(adresse_ROM, "C6");
                adresse_ROM++;
                ROM.modifierValeurColonne(adresse_ROM, valeur);
                adresse_ROM++;
                break;

            case 'D':
                CPU.setD(valeur16);
                flagNeg(valeur16, 16);
                flagZero(valeur16);
                ROM.modifierValeurColonne(adresse_ROM, "CC");
                adresse_ROM++;
                ROM.modifierValeurColonne(adresse_ROM, valeur);
                adresse_ROM += 2;
                break;

            case 'X':
                CPU.setX(valeur16);
                flagNeg(valeur16, 16);
                flagZero(valeur16);
                ROM.modifierValeurColonne(adresse_ROM, "8E");
                adresse_ROM++;
                ROM.modifierValeurColonne(adresse_ROM, valeur);
                adresse_ROM += 2;
                break;

            case 'Y':
                CPU.setY(valeur16);
                flagNeg(valeur16, 16);
                flagZero(valeur16);
                ROM.modifierValeurColonne(adresse_ROM, "10");
                adresse_ROM++;
                ROM.modifierValeurColonne(adresse_ROM, "8E");
                adresse_ROM++;
                ROM.modifierValeurColonne(adresse_ROM, valeur);
                adresse_ROM += 2;
                break;

            case 'S':
                CPU.setS(valeur16);
                flagNeg(valeur16, 16);
                flagZero(valeur16);
                ROM.modifierValeurColonne(adresse_ROM, "10");
                adresse_ROM++;
                ROM.modifierValeurColonne(adresse_ROM, "CE");
                adresse_ROM++;
                ROM.modifierValeurColonne(adresse_ROM, valeur);
                adresse_ROM += 2;
                break;

            case 'U':
                CPU.setU(valeur16);
                flagNeg(valeur16, 16);
                flagZero(valeur16);
                ROM.modifierValeurColonne(adresse_ROM, "CE");
                adresse_ROM++;
                ROM.modifierValeurColonne(adresse_ROM, valeur);
                adresse_ROM += 2;
                break;

            default:
                throw new IllegalArgumentException("Registre non supporté : " + registre);
        }
    }

    /* =========================
       MODE ÉTENDU ($xxxx) ou DIRECT ($xx)
       ========================= */
    else if (mots[1].charAt(0) == '$') {

        int longueurMot = mots[1].length();

        /* -------- MODE ÉTENDU ($xxxx) -------- */
        if (longueurMot == 5) {

            String adresseMemoire = mots[1].substring(1, 5);
            String val = (String) RAM.obtenirValeur(adresseMemoire);
            String valSuivante = (String) RAM.obtenirValeurSuivante(adresseMemoire);
            String valeur16 = val + valSuivante;

            switch (registre) {

                case 'A':
                    CPU.setA(val);
                    flagNeg(val, 8);
                    flagZero(val);
                    ROM.modifierValeurColonne(adresse_ROM, "B6");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseMemoire);
                    adresse_ROM += 2;
                    break;

                case 'B':
                    CPU.setB(val);
                    flagNeg(val, 8);
                    flagZero(val);
                    ROM.modifierValeurColonne(adresse_ROM, "F6");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseMemoire);
                    adresse_ROM += 2;
                    break;

                case 'D':
                    CPU.setD(valeur16);
                    flagNeg(valeur16, 16);
                    flagZero(valeur16);
                    ROM.modifierValeurColonne(adresse_ROM, "FC");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseMemoire);
                    adresse_ROM += 2;
                    break;

                case 'X':
                    CPU.setX(valeur16);
                    flagNeg(valeur16, 16);
                    flagZero(valeur16);
                    ROM.modifierValeurColonne(adresse_ROM, "BE");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseMemoire);
                    adresse_ROM += 2;
                    break;

                case 'Y':
                    CPU.setY(valeur16);
                    flagNeg(valeur16, 16);
                    flagZero(valeur16);
                    ROM.modifierValeurColonne(adresse_ROM, "10");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, "BE");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseMemoire);
                    adresse_ROM += 2;
                    break;

                case 'S':
                    CPU.setS(valeur16);
                    flagNeg(valeur16, 16);
                    flagZero(valeur16);
                    ROM.modifierValeurColonne(adresse_ROM, "10");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, "FE");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseMemoire);
                    adresse_ROM += 2;
                    break;

                case 'U':
                    CPU.setU(valeur16);
                    flagNeg(valeur16, 16);
                    flagZero(valeur16);
                    ROM.modifierValeurColonne(adresse_ROM, "FE");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, adresseMemoire);
                    adresse_ROM += 2;
                    break;

                default:
                    throw new IllegalArgumentException("Registre non supporté : " + registre);
            }
        }

        /* -------- MODE DIRECT ($xx) -------- */
        else if (longueurMot == 3) {

            String adresseComplete = CPU.getDP() + mots[1].substring(1, 3);
            String addDirect = adresseComplete.substring(2);

            String val = (String) RAM.obtenirValeur(adresseComplete);
            String valSuivante = (String) RAM.obtenirValeurSuivante(adresseComplete);
            String valeur16 = val + valSuivante;

            switch (registre) {

                case 'A':
                    CPU.setA(val);
                    flagNeg(val, 8);
                    flagZero(val);
                    ROM.modifierValeurColonne(adresse_ROM, "96");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, addDirect);
                    adresse_ROM++;
                    break;

                case 'B':
                    CPU.setB(val);
                    flagNeg(val, 8);
                    flagZero(val);
                    ROM.modifierValeurColonne(adresse_ROM, "D6");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, addDirect);
                    adresse_ROM++;
                    break;

                case 'D':
                    CPU.setD(valeur16);
                    flagNeg(valeur16, 16);
                    flagZero(valeur16);
                    ROM.modifierValeurColonne(adresse_ROM, "DC");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, addDirect);
                    adresse_ROM++;
                    break;

                case 'X':
                    CPU.setX(valeur16);
                    flagNeg(valeur16, 16);
                    flagZero(valeur16);
                    ROM.modifierValeurColonne(adresse_ROM, "9E");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, addDirect);
                    adresse_ROM++;
                    break;

                case 'Y':
                    CPU.setY(valeur16);
                    flagNeg(valeur16, 16);
                    flagZero(valeur16);
                    ROM.modifierValeurColonne(adresse_ROM, "10");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, "9E");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, addDirect);
                    adresse_ROM++;
                    break;

                case 'S':
                    CPU.setS(valeur16);
                    flagNeg(valeur16, 16);
                    flagZero(valeur16);
                    ROM.modifierValeurColonne(adresse_ROM, "10");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, "DE");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, addDirect);
                    adresse_ROM++;
                    break;

                case 'U':
                    CPU.setU(valeur16);
                    flagNeg(valeur16, 16);
                    flagZero(valeur16);
                    ROM.modifierValeurColonne(adresse_ROM, "DE");
                    adresse_ROM++;
                    ROM.modifierValeurColonne(adresse_ROM, addDirect);
                    adresse_ROM++;
                    break;

                default:
                    throw new IllegalArgumentException("Registre non supporté : " + registre);
            }
        }
    }

    return 1;
}
//_________________inSTRUCTION Test A ,B  __________________________________
	private void instr_test(char registre) {
    // TST teste un registre : met à jour les flags N et Z
    String valeur;

    switch (registre) {
        case 'A':
            valeur = CPU.getA();
            flagNeg(valeur, 8);
            flagZero(valeur);
            ROM.modifierValeurColonne(adresse_ROM, "4D"); // Opcode TSTA
            adresse_ROM++;
            break;

        case 'B':
            valeur = CPU.getB();
            flagNeg(valeur, 8);
            flagZero(valeur);
            ROM.modifierValeurColonne(adresse_ROM, "5D"); // Opcode TSTB
            adresse_ROM++;
            break;

        default:
            throw new IllegalArgumentException("Registre non supporté pour TST : " + registre);
    }
}
//______________________________________________INSTRUCTION TEST _______________
private void instr_TST_MEM(String[] mots) {
    // TST_MEM : teste la valeur en mémoire et met à jour les flags N et Z
    String adresse;
    String valeur;

    if (mots[1].charAt(0) == '$') {
        int longueur = mots[1].length();

        // -------- MODE DIRECT ($xx) --------
        if (longueur == 3) {
            adresse = CPU.getDP() + mots[1].substring(1, 3);
            valeur = (String) RAM.obtenirValeur(adresse);

            ROM.modifierValeurColonne(adresse_ROM, "B4"); // Exemple d'opcode TST direct
            adresse_ROM++;
            ROM.modifierValeurColonne(adresse_ROM, mots[1].substring(1, 3));
            adresse_ROM++;

        } 
        // -------- MODE ÉTENDU ($xxxx) --------
        else if (longueur == 5) {
            adresse = mots[1].substring(1, 5);
            valeur = (String) RAM.obtenirValeur(adresse);

            ROM.modifierValeurColonne(adresse_ROM, "BC"); // Exemple d'opcode TST étendu
            adresse_ROM++;
            ROM.modifierValeurColonne(adresse_ROM, adresse);
            adresse_ROM++;
            adresse_ROM++;
        } 
        else {
            throw new IllegalArgumentException("Adresse invalide pour TST_MEM : " + mots[1]);
        }

        // Mise à jour des flags en fonction de la valeur mémoire
        int valDec = convertirHexEnDecimal(valeur);
        if (valDec == 0) {
            CPU.setZ("1");
            CPU.setN("0");
        } else if ((valDec & 0x80) != 0) {
            CPU.setN("1");
            CPU.setZ("0");
        } else {
            CPU.setN("0");
            CPU.setZ("0");
        }

    } else {
        throw new IllegalArgumentException("Syntaxe invalide pour TST_MEM : " + mots[1]);
    }
}



// _________________Méthode pour convertir de décimal vers hexadicimal
	private static String decimalEnHex(int value) {
        // Convertir en hexadicimal 
        String hexValue = Integer.toHexString(value).toUpperCase();

        // Pad with leading zeros to ensure a fixed width of 4 characters
        while (hexValue.length() < 4) {
            hexValue = "0" + hexValue;
        }

        return hexValue;
    }
// _________________ Méthode pour convertir de hexadicimal vers binaire
	private static String hexToBinary(String hex, int nbrBits) {
        String binary = Integer.toBinaryString(Integer.parseInt(hex, 16));
        // Ajouter des zéros à gauche pour atteindre le nombre de bits spécifié
        while (binary.length() < nbrBits) {
            binary = "0" + binary;
        }
        return binary;
    }

   //_________________methode pour convertir de hexadicimal vers dicimal 
	private static int  convertirHexEnDecimal(String hexValue) {
		while (hexValue.length() < 5) {
	        hexValue = "0" + hexValue;
	    }
	    return Integer.parseInt(hexValue, 16);
	}


 // _______________________Methodes pour obtenir et modifier les registres 
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
	// _________________________________Methode por modifier les registres 
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
	
	
	//________________ Méthode pour tester le bit de poids fort d'une valeur hexadicimele 
    public static boolean testBitPoidsFort(String hex, int nbrBits) {
        // Convertir les valeurs hexadécimales en binaire
        String binary1 = hexToBinary(hex,nbrBits);
        // Ajouter les 0
        char msb = binary1.charAt(0);
        // Vérifier si le bit de poids fort est positionitif (0) ou négatif (1)
        return msb == '1';
    }
    
//_______________________________________________________________________________________________________________________________________
 // _________________________________Méthode pour mettre à jour le drapeau Négatif
    public static void flagNeg(String op, int nbrBits) {
    	if(testBitPoidsFort(op,nbrBits)) {
    		CPU.setN("1");
    	}
    	else 
    		CPU.setN("0");
    }
    // ______________________________Méthode pour mettre à jour le drapeau Zéro
    private static void flagZero(String hexValue) {
        // Convert hexadecimal to integer
        int value = Integer.parseInt(hexValue, 16);
    
        if (value == 0) {
            CPU.setZ("1");
        } else {
            CPU.setZ("0");
        }
    }

    //______________________Methode pour trouver toutes les étiquettes dans le code assembleur ___________________________
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

}
