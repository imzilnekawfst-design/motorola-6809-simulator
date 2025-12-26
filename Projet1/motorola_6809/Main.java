package motorola_6809;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class Main extends JFrame {


    private RAM fenetreRAM = new RAM();
    private ROM fenetreROM = new ROM();
    private Assembleur fenetreAssembleur = new Assembleur();
    private boolean assembleurVisible = true;

    private static final long serialVersionUID = 1L;

    public Main() {
        // Configuration de la fenêtre principale
        setTitle("MOTOROLA 6809");
        setBounds(10, 10, 1240, 600);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // La fenêtre principale ferme tout
        getContentPane().setBackground(Color.PINK);
        setFont(new Font("Berlin Sans FB", Font.BOLD, 14));
        getContentPane().setLayout(null);

        // Configuration des fenêtres secondaires pour **ne pas fermer l'application**
       
        fenetreRAM.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        fenetreROM.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        fenetreAssembleur.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        // Barre de menu
        JMenuBar barreMenu = new JMenuBar();
        barreMenu.setBorder(new EmptyBorder(5, 5, 5, 5));
        setJMenuBar(barreMenu);

        // Bouton "Assembleur"
        JButton boutonAssembleur = new JButton("Assembleur");
        boutonAssembleur.addActionListener(e -> {
            fenetreAssembleur.setVisible(assembleurVisible);
            assembleurVisible = !assembleurVisible;
        });
        barreMenu.add(boutonAssembleur);
        barreMenu.add(Box.createHorizontalStrut(5));

        // Menu "Affichage"
        JMenu menuAffichage = new JMenu("Affichage");
        menuAffichage.setOpaque(true);
        menuAffichage.setBackground(UIManager.getColor("Button.background"));
        menuAffichage.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        menuAffichage.setForeground(Color.BLACK);
        //Éléments du menu à case à cocher : 
       
        JCheckBoxMenuItem itemRAM = new JCheckBoxMenuItem("RAM");
        JCheckBoxMenuItem itemROM = new JCheckBoxMenuItem("ROM");

       
        itemRAM.addActionListener(e -> fenetreRAM.setVisible(itemRAM.isSelected()));
        //Cette ligne ajoute un écouteur d’événement à l’élément RAM
        itemROM.addActionListener(e -> fenetreROM.setVisible(itemROM.isSelected()));
        //Cette ligne ajoute un écouteur d’événement à l’élément ROM 

  
        menuAffichage.add(itemRAM);
        menuAffichage.add(itemROM);

        barreMenu.add(menuAffichage);

        // Label placeholder
        JLabel labelPlaceholder = new JLabel();
        labelPlaceholder.setBounds(10, 0, 595, 201);
        getContentPane().add(labelPlaceholder);
    }

    public static void main(String[] args) {
        // Afficher la fenêtre principale et la fenêtre CPU
        new Main().setVisible(true);
        new CPU().setVisible(true); 
    }
}
