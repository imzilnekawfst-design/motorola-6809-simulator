package motorola;
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
 Méthode pour convertir une chaîne hexadécimale en entier décimal
	public static int hexToDecimal(String hexString) {
        int decimalValue = Integer.parseInt(hexString, 16);
        return decimalValue;
    }
	
