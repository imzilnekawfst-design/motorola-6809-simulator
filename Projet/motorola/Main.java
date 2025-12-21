package motorola;


import javax.swing.*;
import javax.swing.border.*;

import java.awt.event.*;
import java.awt.Color;
import java.awt.Font;
 
public class Main  extends JFrame {
	Programme prog = new Programme();
	RAM R = new RAM();
	ROM RO = new ROM();
	Assembleur frame = new Assembleur();
	private boolean edit = true;

	private static final long serialVersionUID = 1L;

	public Main() {
    	setResizable(false);
    	setFont(new Font("Berlin Sans FB", Font.BOLD, 14));
        // Frame initialization
        setTitle("MOTOROLA6809 ");
        setBounds(10, 10, 1240, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         getContentPane().setBackground(Color.PINK);

        // Creating the menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Creating the "editeur" button
        JButton editeurItem = new JButton("Assembleur ");
        editeurItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(true);
                if (!edit) {
                	frame.setVisible(false);
                }
                edit = !edit;
            }
        });
        menuBar.add(editeurItem);

        // Setting the menu bar to the frame
        setJMenuBar(menuBar);
        getContentPane().setLayout(null);
        
        JLabel lblNewLabel = new JLabel();
        lblNewLabel.setBounds(10, 0, 595, 201);
        getContentPane().add(lblNewLabel);
        
        menuBar.add(Box.createHorizontalStrut(5));

        // Creating the "affichage" menu item with checkable items for RAM and ROM
        JMenu affichageMenu = new JMenu("Affichage");
        // Rendre le menu opaque pour afficher le fond
affichageMenu.setOpaque(true);

// Couleur de fond similaire à un JButton
affichageMenu.setBackground(UIManager.getColor("Button.background"));

// Ajouter un bord (cadre)
affichageMenu.setBorder(BorderFactory.createLineBorder(Color.black, 1));



        affichageMenu.setForeground(new Color(0, 0, 0));
        final JCheckBoxMenuItem progItem = new JCheckBoxMenuItem("Programme");
        final JCheckBoxMenuItem ramItem = new JCheckBoxMenuItem("RAM");
        final JCheckBoxMenuItem romItem = new JCheckBoxMenuItem("ROM");

        // Action listeners for RAM and ROM items
        ramItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ramItem.isSelected()) {
					R.setVisible(true);
                }
                if (!ramItem.isSelected()) {
					R.setVisible(false);
                }
            }
        });

        romItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (romItem.isSelected()) {
					RO.setVisible(true);
                }
                if (!romItem.isSelected()) {
					RO.setVisible(false);
                }
            }
        });
        
        progItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (progItem.isSelected()) {
                    prog.setVisible(true);
                }
                if (!progItem.isSelected()) {
                    prog.setVisible(false);
                }
            }
        });
        
        affichageMenu.add(progItem);
        affichageMenu.add(ramItem);
        affichageMenu.add(romItem);
        menuBar.add(affichageMenu);
        
	}

    public static void main(String[] args) {
        new Main().setVisible(true); // Show Menu window
        new CPU().setVisible(true);// Show CPU window

    }
}
