package motorola_6809;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class ROM extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel panneauPrincipal;
    static DefaultTableModel modeleTableau = new DefaultTableModel();

    public ROM() {
        // Configuration de la fenêtre ROM
        setTitle("ROM");
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fermer la ROM ne ferme pas l'application
        setBounds(750, 120, 212, 276);

        // Création du panneau principal
        panneauPrincipal = new JPanel();
        panneauPrincipal.setBorder(new EmptyBorder(5, 5, 5, 5));
        panneauPrincipal.setBackground(Color.PINK);
        setContentPane(panneauPrincipal);
        panneauPrincipal.setLayout(new BorderLayout(0, 0));

        // Création du tableau ROM
        modeleTableau.addColumn("Adresse");
        modeleTableau.addColumn("Valeur");

        JTable tableROM = new JTable(modeleTableau);
        JScrollPane scrollPane = new JScrollPane(tableROM);
        panneauPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Remplir la ROM avec les adresses par défaut et valeur FF
        for (int i = 0xFC00; i <= 0xFFFF; i++) {
            String adresse = decimalEnHex(i);
            modeleTableau.addRow(new Object[]{adresse, "FF"});
        }
    }

    /**
     * Modifier la valeur dans la colonne "Valeur" du tableau
     */
    public static void modifierValeurColonne(int indexLigne, String valeur) {
        if (valeur.length() == 2) {
            // Si valeur = 2 caractères
            if (indexLigne >= 0 && indexLigne < modeleTableau.getRowCount()) {
                modeleTableau.setValueAt(valeur, indexLigne, 1);
            } else {
                System.out.println("Index de ligne invalide");
            }
        } else if (valeur.length() > 2) {
            // Si valeur = 4 caractères (ex : 1234)
            if (indexLigne >= 0 && indexLigne < modeleTableau.getRowCount()) {
                modeleTableau.setValueAt(valeur.substring(0, 2), indexLigne, 1);
                indexLigne++;
                modeleTableau.setValueAt(valeur.substring(2), indexLigne, 1);
            } else {
                System.out.println("Index de ligne invalide");
            }
        }
    }

    /**
     * Convertir un entier en hexadécimal sur 4 caractères
     */
    private static String decimalEnHex(int valeur) {
        String hex = Integer.toHexString(valeur).toUpperCase();
        while (hex.length() < 4) {
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * Obtenir l'adresse ROM à partir de l'indice
     */
    public static String obtenirAdresse(int indexROM) {
        if (indexROM >= 0 && indexROM < modeleTableau.getRowCount()) {
            return (String) modeleTableau.getValueAt(indexROM, 0);
        } else {
            System.out.println("Index ROM invalide");
            return null;
        }
    }

    /**
     * Ajouter des lignes ROM supplémentaires (exemple pour 5120 à 6144)
     */
    public static void remplirROMSupplementaire() {
        for (int i = 5120; i < 6144; i++) {
            String adresse = decimalEnHex(i);
            modeleTableau.addRow(new Object[]{adresse, "FF"});
        }
    }
}
