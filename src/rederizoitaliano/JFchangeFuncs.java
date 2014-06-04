/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rederizoitaliano;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class JFchangeFuncs extends JFrame{

	public JFchangeFuncs(final JPdiscreteDyn p){
		setSize(600,300);
		String s = "Press any button to change the corresponding regulatory function";
		JPanel pnlLabel = new JPanel();
		pnlLabel.add(new JLabel(s));
		add(pnlLabel,BorderLayout.NORTH);

		JPanel pnlBot = new JPanel();
		pnlBot.setLayout(new GridLayout(4,6));

		Red red = p.getRed();
		for(int k = 0; k < red.getSize(); ++k){
			JButton bt = makeButton(k,p);
			pnlBot.add(bt);
		}
		JButton btClose = new JButton("C L O S E");
		btClose.setBackground(Color.red);
		btClose.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});
		pnlBot.add(btClose);
		add(pnlBot,BorderLayout.CENTER);
		pack();
	}
	public JButton makeButton(final int k,final JPdiscreteDyn p){
		StringTokenizer st = new StringTokenizer(p.getRed().getNodo(k).getName(),"_");
		String s = st.nextToken();
		s = k + "-" + st.nextToken();
		JButton bt = new JButton(s);
		bt.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JFfunction fv = new JFfunction(k,p);
				fv.setVisible(true);
				fv.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			}
		});
		return bt;
	}

}