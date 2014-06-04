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

public class JFreset extends JFrame{

	JCheckBox jcb1;
	JCheckBox jcb2;
	public JFreset(final Red red,final JPdiscreteDyn p){
		setSize(400,300);
		setTitle("Reset the parameters of the network");

		JPanel pInst = new JPanel();
		JLabel lb = new JLabel("Do you want to reset to initial values?");
		pInst.add(lb);
		add(pInst,BorderLayout.NORTH);

		JPanel pBoxes = new JPanel();
		pBoxes.setLayout(new GridLayout(2,1));
		jcb1 = new JCheckBox("Reset Initial Condition ", false);
		jcb2 = new JCheckBox("Reset Regulatory Functions ",false);
		pBoxes.add(jcb1);
		pBoxes.add(jcb2);
		add(pBoxes,BorderLayout.CENTER);

		JPanel pButton = new JPanel();
		JButton btOK = new JButton("OK");
		btOK.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(jcb1.isSelected()){
					red.setOriginalStates();
				}
				if(jcb2.isSelected()){
					red.setOriginalFunctions();
				}
				p.iniciaDinamica();
				dispose();
			}
		});
		pButton.add(btOK);
		add(pButton,BorderLayout.SOUTH);
	}

}