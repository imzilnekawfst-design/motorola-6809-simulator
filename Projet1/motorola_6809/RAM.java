package motorola_6809;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class RAM extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel panneauPrincipal;
    static DefaultTableModel modeleTableau = new DefaultTableModel();

    public RAM() {
        // Configuration de la fenêtre RAM
        setTitle("RAM");
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Fermer RAM ne ferme pas l'application
        setBounds(550, 120, 212, 276);

        // Panneau principal
        panneauPrincipal = new JPanel();
        panneauPrincipal.setBorder(new EmptyBorder(5, 5, 5, 5));
        panneauPrincipal.setBackground(Color.PINK);
        setContentPane(panneauPrincipal);
        panneauPrincipal.setLayout(new BorderLayout(0, 0));

        JPanel panelTableau = new JPanel();
        panneauPrincipal.add(panelTableau, BorderLayout.CENTER);
        panelTableau.setLayout(new BorderLayout(0, 0));

        // Définition des colonnes du tableau RAM
        modeleTableau.addColumn("Adresse");
        modeleTableau.addColumn("Valeur");

        JTable tableRAM = new JTable(modeleTableau);
        JScrollPane scrollPane = new JScrollPane(tableRAM);
        panelTableau.add(scrollPane, BorderLayout.CENTER);

        // Remplir la RAM avec des adresses et valeurs par défaut
        for (int i = 0; i < 1024; i++) {
            String adresse = decimalEnHex(i);
            modeleTableau.addRow(new Object[]{adresse, "00"});
        }
    }

    /**
     * Réinitialiser la RAM avec toutes les valeurs à 00
     */
    public static void initialiserRAM() {
        modeleTableau.setRowCount(0); // Vider le tableau
        for (int i = 0; i < 1024; i++) {
            String adresse = decimalEnHex(i);
            modeleTableau.addRow(new Object[]{adresse, "00"});
        }
    }

    /**
     * Modifier la valeur dans la RAM à l'indice spécifié
     */
    public static void modifierValeur(String valeur, int index) {
        if (index >= 0 && index < modeleTableau.getRowCount()) {
            modeleTableau.setValueAt(valeur, index, 1);
        }
    }

    /**
     * Obtenir la valeur de la RAM à l'adresse donnée
     */
    public static Object obtenirValeur(String adresse) {
        int ligne = convertirHexEnDecimal(adresse);
        return modeleTableau.getValueAt(ligne, 1);
    }

    /**
     * Obtenir la valeur suivante dans la RAM
     */
    public static Object obtenirValeurSuivante(String adresse) {
        int ligne = convertirHexEnDecimal(adresse) + 1;
        return modeleTableau.getValueAt(ligne, 1);
    }

    /**
     * Convertir une valeur hexadécimale en entier
     */
    private static int convertirHexEnDecimal(String hexValue) {
        while (hexValue.length() < 5) {
            hexValue = "0" + hexValue;
        }
        return Integer.parseInt(hexValue, 16);
    }

    /**
     * Convertir un entier en hexadécimal sur 4 caractères
     */
    private static String decimalEnHex(int value) {
        String hex = Integer.toHexString(value).toUpperCase();
        while (hex.length() < 4) {
            hex = "0" + hex;
        }
        return hex;
    }
}
