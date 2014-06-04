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
import java.awt.geom.*;

public class JPdiscreteDyn extends JPanel {

		Red red;
		int[][] st;
		int nP;
		int dX;
		int dY;
		int tMax = 36;

		public JPdiscreteDyn(Red r){
			red = r;
			nP = red.getSize();
			dX = (int)(650.0/(1.0*nP));
			dY = (int)(600.0/(1.0*tMax));
			st = new int[tMax][nP];
			for(int n = 0; n < nP; ++n){
				st[0][n] = red.getNodo(n).getS();
			}
			for(int t = 1; t < tMax; ++t){
				for(int n = 0; n < nP; ++n){
					st[t][n] = -1;
				}
			}
		}
        @Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;

			Rectangle2D r = new Rectangle2D.Double();
			Color c;
			int x,y,m,n;
			String num;

			x = 0;
			for(n = 0; n < red.getSize(); ++n){
				if(red.getNodo(n).isPresent()){
					num = Integer.toString(n);
					g2.drawString(num, x, 20);
					x += dX;
				}
			}

			for(int t = 0; t < tMax; ++t){
                n = 0; // Se incrementa solo si el nodo esta presente
				for(m = 0; m < red.getSize(); ++m){
                    if(red.getNodo(m).isPresent()){
                        if(red.getNodo(m).isBinary()){
                            if(st[t][n] == 1){
                                c = Color.GREEN;
                            }
                            else{
                                if(st[t][n] == 0){
                                    c = Color.BLACK;
                                }
                                else
                                    if(st[t][n] == 2){
                                        c = Color.RED;
                                    }
                                else{
                                    c = Color.WHITE;
                                }
                            }
                        }
                        else{
                            if(st[t][n] == 0){
                                if(n <= 40 & n > 5){
                                    c = Color.BLACK;
                                }
                                else
                                c = Color.BLUE;
                            }
                            else{
                                if(st[t][n] == 1){
                                    if(n <= 40 & n > 6){
                                        c = Color.GREEN;
                                    }
                                    else
                                        c = Color.BLACK;
                                }
                                else{
                                    if(st[t][n] == 2){
                                        c = Color.RED;
                                    }
                                    else{
                                        c = Color.WHITE;
                                       
                                    }
                                }
                            }
                        }
                        g2.setPaint(c);
                        x = n*dX;
                        y = 20 + t*dY;
                        r.setFrame(x, y, dX-2, dY-2);
                        g2.fill(r);
                        ++n;
                    }
				}
			}
		}
		public void iniciaDinamica(){
			int m;
			for(int t = 0; t < tMax; ++t){
				m = 0;
				for(int n = 0; n < red.getSize(); ++n){
					if(red.getNodo(n).isPresent()){
						st[t][m] = red.getNodo(n).getS();
						++m;
					}
				}
                if(t < tMax-1)
                    red.evolveKauffman();
//                    red.evolveKauffmanSwitch(1,1);
			}
			repaint();
		}
		public void resetRandom(){
			red.setRandomStates();
			iniciaDinamica();
		}
		public void setPresentes(boolean[] sl){
			red.setPresentes(sl);
			red.setOriginalStates();
			nP = red.getNumPresentes();
			dX = (int)(650.0/(1.0*nP));
			st = new int[tMax][nP];
			iniciaDinamica();
		}
		public Red getRed(){
			return red;
		}
}