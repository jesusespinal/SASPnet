/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rederizoitaliano;

/**
 *
 * @author max
 */
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class JFdiscreteDyn extends JFrame {

	JPdiscreteDyn pd;
	boolean[] sel;
	JCheckBox[] cb;

	public JFdiscreteDyn(final Red red){
		setSize(900,700);
		setTitle("Signaling Network Evolution Pattern");

		// *************************
		// EL PANEL DE DIBUJO
		pd = new JPdiscreteDyn(red);
		add(pd,BorderLayout.CENTER);

		// *************************
		// EL PANEL DE LOS BOTONES
		JPanel pnlBotones = new JPanel();
		JButton btStart = new JButton("Start/Continue");
		btStart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				pd.iniciaDinamica();
			}
		});
		JButton btRD = new JButton("Reset Network");
		btRD.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JFreset v = new JFreset(red,pd);
				v.setVisible(true);
				v.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		});
		JButton btRR = new JButton("Random Condition");
		btRR.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				pd.resetRandom();
			}
		});
		JButton btFW = new JButton("Change RF's");
		btFW.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JFchangeFuncs fw = new JFchangeFuncs(pd);
				fw.setVisible(true);
				fw.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		});
        JButton btST = new JButton("Select states");
        btST.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JFselectState fst = new JFselectState(red);
                fst.setVisible(true);
                fst.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });

		pnlBotones.add(btStart);
		pnlBotones.add(btRD);
		pnlBotones.add(btRR);
		pnlBotones.add(btFW);
        pnlBotones.add(btST);
		add(pnlBotones,BorderLayout.SOUTH);

		// ***********************************
		// EL PANEL DE LA SELECCION DE NODOS
		JPselectPresentes pnlSelect = new JPselectPresentes(pd);

		add(pnlSelect,BorderLayout.EAST);
	}

}
