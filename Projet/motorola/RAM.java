package motorola;


import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class RAM extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	static DefaultTableModel model = new DefaultTableModel();

	public RAM() {
		setTitle("RAM");
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(550, 120, 212, 276);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setBackground(Color.PINK );
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(null);
		panel.setLayout(new BorderLayout(0, 0));

		model.addColumn("Address");
        model.addColumn("Donner ");
        
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane);
		
        // Remplir la RAM avec des adresses et valeurs par défaut
    	for (int i = 0; i < 1024; i++) {
                String address = decimalEnHex(i); // Convert decimal to hexadecimal
                model.addRow(new Object[]{address, "00"});
            }
	}
	 // Méthode pour initialiser la RAM
	public static void initialiserRAM() {
        for (int i = 0; i < 1024; i++) {
            String address = decimalEnHex(i);
            model.addRow(new Object[]{address, "00"});
        }
    }
	
	public static void modifierValeur(String t, int i) {
		model.setValueAt(t,i,1);
	}
	
	public static Object obtenirValeur(String adresse) {
		int ligne= convertirHexEnDecimal(adresse);
		return model.getValueAt(ligne, 1);
	}

	private static int  convertirHexEnDecimal(String hexValue) {
		while (hexValue.length() < 5) {
	        hexValue = "0" + hexValue;
	    }

	    // Convert hexadecimal to integer
	    int intValue = Integer.parseInt(hexValue, 16);

	    return intValue;
	}
	private static String decimalEnHex(int value) {
        // Convert to hexadecimal
        String hexValue = Integer.toHexString(value).toUpperCase();

        // Pad with leading zeros to ensure a fixed width of 4 characters
        while (hexValue.length() < 4) {
            hexValue = "0" + hexValue;
        }

        return hexValue;
    }

	public static Object obtenirValeurSuivante(String adresse) {
		int ligne= convertirHexEnDecimal(adresse);
		ligne++;
		return model.getValueAt(ligne, 1);
	}

}