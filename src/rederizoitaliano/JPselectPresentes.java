/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rederizoitaliano;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.StringTokenizer;

public class JPselectPresentes extends JPanel{

		boolean[] sel;
		JCheckBox[] cb;
		public JPselectPresentes(final JPdiscreteDyn p){
			// se crean los bordes
			Border b1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
			Border b2 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
			Border b3 = BorderFactory.createCompoundBorder(b1, b2);
			setBorder(b3);

			// se carga la red y se hace el layout del panel
			Red red = p.getRed();
			int N = red.getSize();
			super.setLayout(new GridLayout(N+2,1));
			sel = new boolean[N];
			cb = new JCheckBox[N];


			// Boton para seleccionar a todos los genes
			JButton btSelectAll = new JButton("Select All");
			btSelectAll.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					for(int n = 0; n < cb.length; ++n){
						cb[n].setSelected(true);
						sel[n] = true;
					}
					p.setPresentes(sel);
				}
			});
			add(btSelectAll);

			// Se crean las casillas de seleccion
			String s,sn;
			StringTokenizer st;
			for(int n = 0; n < N; ++n){
				st = new StringTokenizer(red.getNodo(n).getName(),"_");
				sn = st.nextToken();
				sn = st.nextToken();
				s = n + " -> " + sn;
				cb[n] = new JCheckBox(s,true);
				add(cb[n]);
				sel[n] = true;
			}

			// El boton OK para seleccionar a los genes
			JButton btOK = new JButton("O.k.");
			btOK.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					for(int n = 0; n < cb.length; ++n){
						if(cb[n].isSelected()){
							sel[n] = true;
						}
						else{
							sel[n] = false;
						}
					}
					p.setPresentes(sel);
				}
			});
			add(btOK);
		}
}