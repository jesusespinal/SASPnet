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
import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class JFfunction extends JFrame{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	int cF;
	int K;
	String[] names;
	int[] vF;
	int[] iF;
	JTextField[] jtf;
	Nodo nd;
	JPdiscreteDyn pd;

	public JFfunction(int in, JPdiscreteDyn pd){
		this.pd = pd;
		nd = pd.getRed().getNodo(in);
		int[] ff = nd.getFunc();
		StringTokenizer st;

		// Determina el numero de reguladores
		K = nd.getNumReg();
		names = new String[K];

		int m;
		for(int k = 0; k < nd.getNumReg(); ++k){
			m = nd.getReg(k);
			st = new StringTokenizer(pd.getRed().getNodo(m).getName(),"_");
			names[k] = st.nextToken();
			names[k] = st.nextToken();
		}

		// Determina las configuraciones validas de la funcion reguladora
		cF = 0;
		for(int k = 0; k < ff.length; ++k){
			if((ff[k]==0) || (ff[k] == 1) || (ff[k]) == 2){
				++cF;
			}
		}
		jtf = new JTextField[cF];
		vF = new int[cF];
		iF = new int[cF];
		cF = 0;
		for(int k = 0; k < ff.length; ++k){
			if((ff[k]==0) || (ff[k] == 1) || (ff[k] == 2)){
				iF[cF] = k;
				vF[cF] = ff[k];
				++cF;
			}
		}

		// Determina el tamaño de la ventana
		int sX = 300;
		int sY = 40*cF;
		if(sY > 640)
			sY = 640;
		setSize(sX,sY);
		st = new StringTokenizer(nd.getName(),"_");
		String ss = st.nextToken();
		ss = in + "-" + st.nextToken();
		setTitle(ss);

		// Genera el panel y lo monta
		PanelFuncion p = new PanelFuncion();
		JScrollPane jscp = new JScrollPane(p);
		setContentPane(jscp);
		pack();
	}

	class PanelFuncion extends JPanel{
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public PanelFuncion(){
			super.setLayout(new BorderLayout());
			int[] reg = new int[K];
			String s;
			JLabel lbl;
			JPanel pF = new JPanel();
			pF.setLayout(new GridLayout(cF+1,K+1));
			for(int n = 0; n < names.length; ++n){
				s = names[n] + " ";
				lbl = new JLabel(s);
				pF.add(lbl);
			}
			s = "F";
			lbl = new JLabel(s);
			pF.add(lbl);

			for(int n = 0; n < cF; ++n){
				Methods.intToTernary(iF[n], reg);
				for(int k = 0; k < reg.length; ++k){
					s = reg[k] + " ";
					lbl = new JLabel(s);
					pF.add(lbl);
				}
				jtf[n] = new JTextField(Integer.toString(vF[n]),5);
				pF.add(jtf[n]);
			}
			add(pF,BorderLayout.CENTER);

			JButton btOK = new JButton("OK");
			btOK.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					int v;
					int ncfr = (int)(Math.pow(3, nd.getNumReg()));
					for(int k = 0; k < ncfr; ++k){
						nd.setFuncReg(k, -1);
					}
					for(int k = 0; k < cF; ++k){
						v = Integer.parseInt(jtf[k].getText());
						nd.setFuncReg(iF[k], v);
					}
					pd.iniciaDinamica();
					dispose();
				}
			});
			add(btOK,BorderLayout.SOUTH);
		}
	}

}