package motorola_6809;

import java.awt.Color;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class Programme extends JFrame {

    private static final long serialVersionUID = 1L;

    private JPanel panneauPrincipal;
    private static JTextArea zoneTexte;
    private static Highlighter surligneur;
    private static Highlighter.HighlightPainter couleurSurlignage;
    protected static int ligneCourante = 0;

    public Programme() {
        // Configuration de la fenêtre
        setTitle("Programme");
        setResizable(false);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // ferme uniquement cette fenêtre
        setBounds(280, 120, 250, 300);

        // Conteneur principal
        panneauPrincipal = new JPanel();
        panneauPrincipal.setLayout(null);
        panneauPrincipal.setBackground(Color.PINK);
        setContentPane(panneauPrincipal);

        // Zone de texte
        zoneTexte = new JTextArea();
        zoneTexte.setEditable(false);

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(zoneTexte);
        scrollPane.setBounds(10, 10, 220, 250);
        panneauPrincipal.add(scrollPane);

        // Surlignage
        surligneur = zoneTexte.getHighlighter();
        couleurSurlignage = new DefaultHighlighter.DefaultHighlightPainter(Color.CYAN);
    }

    /**
     * Surligne la ligne spécifiée dans la zone de texte
     */
    private static void surlignerLigne(final int numeroLigne) {
        SwingUtilities.invokeLater(() -> {
            try {
                int debut = zoneTexte.getLineStartOffset(numeroLigne);
                int fin = zoneTexte.getLineEndOffset(numeroLigne);

                // Supprimer les surlignages précédents
                surligneur.removeAllHighlights();

                // Surligner la ligne
                zoneTexte.setSelectionStart(debut);
                zoneTexte.setSelectionEnd(fin);
                surligneur.addHighlight(debut, fin, couleurSurlignage);

            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Affiche une instruction avec l'adresse ROM
     */
    public static void afficher_Instructions(int idRom, String instruction) {
        String adresse = ROM.obtenirAdresse(idRom);
        zoneTexte.append(" " + adresse + "    " + instruction + "\n");
        surlignerLigne(ligneCourante);
        ligneCourante++;
    }

    /**
     * Affiche une instruction sans adresse
     */
    public static void afficher_Instructions(String instruction) {
        zoneTexte.append("        " + instruction + "\n");
        surlignerLigne(ligneCourante);
        ligneCourante++;
    }

    /**
     * Vide la zone de texte
     */
    public static void viderZoneTexte() {
        zoneTexte.setText("");
        ligneCourante = 0;
    }

    /**
     * Supprime les premières lignes de la zone de texte
     */
    public static void supprimer_Lignes(int nombreDeLignes) {
        String texte = zoneTexte.getText();
        String[] lignes = texte.split("\n");

        // Limiter le nombre de lignes à supprimer
        nombreDeLignes = Math.min(nombreDeLignes, lignes.length);

        // Construire le nouveau texte
        StringBuilder nouveauTexte = new StringBuilder();
        for (int i = nombreDeLignes; i < lignes.length; i++) {
            nouveauTexte.append(lignes[i]).append("\n");
        }

        zoneTexte.setText(nouveauTexte.toString().trim());
        ligneCourante = Math.max(0, ligneCourante - nombreDeLignes);
    }
}
