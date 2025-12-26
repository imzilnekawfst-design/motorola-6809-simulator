package motorola_6809 ;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

public class CPU extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private static JLabel instru_PC;
	private static JLabel PC;
	private static JLabel S;
	private static JLabel U;
	private static JLabel A;
	private static JLabel B;
	private static JLabel DP;
	private static JLabel X;
	private static JLabel Y;
	private static JLabel E;
	private static JLabel F;
	private static JLabel H;
	private static JLabel I;
	private static JLabel N;
	private static JLabel Z;
	private static JLabel V;
	private static JLabel C;
	private static JLabel D;

	public CPU() {
		setResizable(false);
		setAlwaysOnTop(true);
		setTitle("ARCHITECTURE INTERNE DU 6809");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(10, 120, 226, 420);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
JLabel pc = new JLabel("PC :");
pc.setFont(new Font("Tahoma", Font.BOLD, 18));
pc.setForeground(Color.BLACK);pc.setBounds(40, 15, 40, 26);
contentPane.add(pc );

		PC = new JLabel("0000");
		PC.setFont(new Font("Tahoma", Font.PLAIN, 23));
		PC.setForeground(Color.BLUE);
		PC.setBounds(95, 15, 89, 26);
		contentPane.add(PC);
		

		
		instru_PC = new JLabel("");
		instru_PC.setFont(new Font("Tahoma", Font.PLAIN, 23));
		instru_PC.setForeground(Color.BLUE);
		instru_PC.setBounds(13, 50, 202, 31);
		contentPane.add(instru_PC);
		
JLabel labelS = new JLabel("S :");
labelS.setFont(new Font("Tahoma", Font.BOLD, 18));
labelS.setBounds(10, 85, 30, 30);
contentPane.add(labelS);
	
		S = new JLabel("0000");
		S.setFont(new Font("Tahoma", Font.PLAIN, 23));
		S.setForeground(Color.BLUE);
		S.setBounds(40, 85, 70, 30);
		contentPane.add(S);
JLabel labelU = new JLabel("U :");
labelU.setFont(new Font("Tahoma", Font.BOLD, 18));
labelU.setBounds(115, 85, 30, 30);
contentPane.add(labelU);

		U = new JLabel("0000");
		U.setFont(new Font("Tahoma", Font.PLAIN, 23));
		U.setForeground(Color.BLUE);
		U.setBounds(145, 85, 70, 30);
		contentPane.add(U);
JLabel labelA = new JLabel("A :");
labelA.setFont(new Font("Tahoma", Font.BOLD, 18));
labelA.setBounds(10, 141, 30, 30);
contentPane.add(labelA);		
		A = new JLabel("00");
		A.setFont(new Font("Tahoma", Font.PLAIN, 23));
		A.setForeground(Color.BLUE);
		A.setBounds(40, 141, 46, 30);
		contentPane.add(A);


		// ----- Registre D (entre A et B) -----
JLabel labelD = new JLabel("D :");
labelD.setFont(new Font("Tahoma", Font.BOLD, 18));
labelD.setBounds(10, 183, 30, 30); // position entre A (141) et B (225)
contentPane.add(labelD);

JLabel D = new JLabel("0000"); // D = A:B → 16 bits
D.setFont(new Font("Tahoma", Font.PLAIN, 23));
D.setForeground(Color.BLUE);
D.setBounds(40, 183, 60, 30);
contentPane.add(D);

JLabel labelB = new JLabel("B :");
labelB.setFont(new Font("Tahoma", Font.BOLD, 18));
labelB.setBounds(10, 225, 30, 30);
contentPane.add(labelB);		
		B = new JLabel("00");
		B.setFont(new Font("Tahoma", Font.PLAIN, 23));
		B.setForeground(Color.BLUE);
		B.setBounds(40, 225, 46, 30);
		contentPane.add(B);
JLabel labelDP = new JLabel("DP:");
labelDP.setFont(new Font("Tahoma", Font.BOLD, 18));
labelDP.setBounds(10, 281, 35, 30);
contentPane.add(labelDP);		
		DP = new JLabel("00");
		DP.setFont(new Font("Tahoma", Font.PLAIN, 23));
		DP.setForeground(Color.BLUE);
		DP.setBounds(45, 281, 46, 30);
		contentPane.add(DP);
JLabel labelX = new JLabel("X:");
labelX.setFont(new Font("Tahoma", Font.BOLD, 18));
labelX.setBounds(10, 352, 30, 30);
contentPane.add(labelX);		
		X = new JLabel("0000");
		X.setFont(new Font("Tahoma", Font.PLAIN, 23));
		X.setForeground(Color.BLUE);
		X.setBounds(30, 352, 76, 30);
		contentPane.add(X);
JLabel labelY = new JLabel("Y :");
labelY.setFont(new Font("Tahoma", Font.BOLD, 18));
labelY.setBounds(115, 353, 30, 30);
contentPane.add(labelY);		
		Y = new JLabel("0000");
		Y.setFont(new Font("Tahoma", Font.PLAIN, 23));
		Y.setForeground(Color.BLUE);
		Y.setBounds(145, 353, 76, 30);
		contentPane.add(Y);
		
		E = new JLabel("0");
		E.setForeground(new Color(0, 0, 160));
		E.setFont(new Font("Tahoma", Font.PLAIN, 23));
		E.setBounds(92, 281, 13, 31);
		contentPane.add(E);
		
		F = new JLabel("0");
		F.setForeground(new Color(0, 0, 160));
		F.setFont(new Font("Tahoma", Font.PLAIN, 23));
		F.setBounds(105, 281, 13, 31);
		contentPane.add(F);
		
		H = new JLabel("0");
		H.setForeground(new Color(0, 0, 160));
		H.setFont(new Font("Tahoma", Font.PLAIN, 23));
		H.setBounds(117, 281, 13, 31);
		contentPane.add(H);
		
		I = new JLabel("0");
		I.setForeground(new Color(0, 0, 160));
		I.setFont(new Font("Tahoma", Font.PLAIN, 23));
		I.setBounds(130, 281, 13, 31);
		contentPane.add(I);
		
		N = new JLabel("0");
		N.setForeground(new Color(0, 0, 160));
		N.setFont(new Font("Tahoma", Font.PLAIN, 23));
		N.setBounds(143, 281, 13, 31);
		contentPane.add(N);
		
		Z = new JLabel("1");
		Z.setForeground(new Color(0, 0, 160));
		Z.setFont(new Font("Tahoma", Font.PLAIN, 23));
		Z.setBounds(156, 281, 13, 31);
		contentPane.add(Z);
		
		V = new JLabel("0");
		V.setForeground(new Color(0, 0, 160));
		V.setFont(new Font("Tahoma", Font.PLAIN, 23));
		V.setBounds(169, 281, 13, 31);
		contentPane.add(V);
		
		C = new JLabel("0");
		C.setForeground(new Color(0, 0, 160));
		C.setFont(new Font("Tahoma", Font.PLAIN, 23));
		C.setBounds(182, 281, 13, 31);
		contentPane.add(C);
		

JLabel lE = new JLabel("E");
lE.setBounds(92, 260, 13, 20);
contentPane.add(lE);

JLabel lF = new JLabel("F");
lF.setBounds(105, 260, 13, 20);
contentPane.add(lF);

JLabel lH = new JLabel("H");
lH.setBounds(117, 260, 13, 20);
contentPane.add(lH);

JLabel lI = new JLabel("I");
lI.setBounds(130, 260, 13, 20);
contentPane.add(lI);

JLabel lN = new JLabel("N");
lN.setBounds(143, 260, 13, 20);
contentPane.add(lN);

JLabel lZ = new JLabel("Z");
lZ.setBounds(156, 260, 13, 20);
contentPane.add(lZ);

JLabel lV = new JLabel("V");
lV.setBounds(169, 260, 13, 20);
contentPane.add(lV);

JLabel lC = new JLabel("C");
lC.setBounds(182, 260, 13, 20);
contentPane.add(lC);


JPanel grandRect = new JPanel();
grandRect.setBackground(Color.CYAN); // Couleur du rectangle
grandRect.setBounds(100, 141, 80, 115); // Position et taille pour englober A et B
grandRect.setLayout(new BorderLayout());

// Ajouter le texte "UAL" centré dans le rectangle
JLabel ualLabel = new JLabel("UAL", SwingConstants.CENTER);
ualLabel.setForeground(Color.BLACK);
ualLabel.setFont(new Font("Tahoma", Font.PLAIN, 23));
grandRect.add(ualLabel, BorderLayout.CENTER);

contentPane.add(grandRect);
		
	}
  //  Registre A
	public static void setA(String a) {A.setText(a);}
	public static String getA() { return   A.getText();}
	//  Registre B
	public static void setB(String b) {B.setText(b);}
	public static String getB() {return B.getText();}
	//Registre D 
	public static void setD(String a) { A.setText(a.substring(0, 2)); B.setText(a.substring(2, 4)); }
    public static String getD() { return A.getText() + B.getText(); }

	//  Registre X
	public static void setX(String x) {X.setText(x);}
	public static String get_X() {return  X.getText();}
	//  Registre Y 
public static void setY(String a) { Y.setText(a); }
public static String get_Y() { return Y.getText(); }

//  Registre U 
public static void setU(String a) { U.setText(a); }
public static String getU() { return U.getText(); }

//  Registre S 
public static void setS(String a) { S.setText(a); }
public static String getS() { return S.getText(); }

//  Direct Page 
public static void setDP(String a) { DP.setText(a); }
public static String getDP() { return DP.getText(); }

//  Program Counter 
public static void setPC(String a) { PC.setText(a); }

//  Instruction courante 
public static void setinstru_PC(String a) { instru_PC.setText(a); }

//  Flags 
public static void setC(String c) { C.setText(c); }
public static String getC() { return C.getText(); }

public static void setV(String v) { V.setText(v); }
public static String getV() { return V.getText(); }

public static void setZ(String z) { Z.setText(z); }
public static String getZ() { return Z.getText(); }

public static void setN(String n) { N.setText(n); }
public static String getN() { return N.getText(); }

public static void setI(String i) { I.setText(i); }
public static String getI() { return I.getText(); }

public static void setH(String h) { H.setText(h); }
public static String getH() { return H.getText(); }

public static void setF(String f) { F.setText(f); }
public static String getF() { return F.getText(); }

public static void setE(String e) { E.setText(e); }
public static String getE() { return E.getText(); }

    public static void reinitialiser() {
        // initialiser les  valeur pour tout les  JLabels
        PC.setText("0000");
        instru_PC.setText("");
        S.setText("0000");
        U.setText("0000");
        A.setText("00");
        B.setText("00");
        DP.setText("00");
        X.setText("0000");
        Y.setText("0000");
        E.setText("0");
        F.setText("0");
        H.setText("0");
        I.setText("0");
        N.setText("0");
        Z.setText("1");
        V.setText("0");
        C.setText("0");
    }
	
}