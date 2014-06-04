/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rederizoitaliano;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

public class JFselectState extends JFrame{
    JRadioButton[][] v;
    ButtonGroup[] bg;
    JPanel[] pnlBt;
    
    public JFselectState(final Red red){
        int N = red.getSize();
        setTitle("Select node states");
        setSize(1000,300);

        v = new JRadioButton[N][];
        bg = new ButtonGroup[N];
        pnlBt = new JPanel[N];

        JPanel pnlNodos = new JPanel();
        pnlNodos.setLayout(new GridLayout(1,N));
        Border br = BorderFactory.createLineBorder(Color.BLUE);

        for(int n = 0; n < N; ++n){
            pnlBt[n] = new JPanel();
            pnlBt[n].setLayout(new GridLayout(4,1));
            pnlBt[n].setBorder(br);

            if(red.getNodo(n).isBinary()){
                v[n] = new JRadioButton[2];
            }
            else{
                v[n] = new JRadioButton[3];
            }
            bg[n] = new ButtonGroup();
            for(int m = 0; m < v[n].length; ++m){
                v[n][m] = new JRadioButton(Integer.toString(m));
                v[n][m].addActionListener(new accion(red.getNodo(n),m));
                bg[n].add(v[n][m]);
            }
        }
        int s;
        for(int n = 0; n < N; ++n){
            s = red.getNodo(n).getS();
            v[n][s].setSelected(true);
        }
        for(int n = 0; n < N; ++n){
            pnlBt[n].add(new JLabel(Integer.toString(n)));
        }
        for(int m = 0; m < 3; ++m){
            for(int n = 0; n < N; ++n){
                if(m == 2){
                    if(red.getNodo(n).isBinary()){
                        pnlBt[n].add(new JLabel("*****"));
                    }
                    else{
                        pnlBt[n].add(v[n][m]);
                    }
                }
                else{
                    pnlBt[n].add(v[n][m]);
                }
            }
        }
        for(int n = 0; n < N; ++n){
            pnlNodos.add(pnlBt[n]);
        }
        add(pnlNodos,BorderLayout.CENTER);

        JPanel pnlOK = new JPanel();
        JButton btOK = new JButton("OK");
        btOK.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                dispose();
            }
        });
        pnlOK.add(btOK);
        add(pnlOK,BorderLayout.SOUTH);
        pack();
    }
    class accion implements ActionListener{
        int state;
        Nodo nodo;
        public accion(Nodo nn, int s)
        {
            state = s;
            nodo = nn;
        }
        public void actionPerformed(ActionEvent e){
            nodo.setS(state);
        }
    }
}
