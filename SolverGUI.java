// Sudoku Solver version 1.0
// created by Michal Wozniczka

import java.util.*;
import java.awt.Color;

/**
 *
 * @author Michal
 */
public class SolverGUI extends javax.swing.JFrame {

                              

    public static boolean solveComplete;
    public static int[][] currentBoard = new int [9][9];
    public static int[][][] posBoard = new int [9][9][9];
    public static String[][] sBoard = new String [9][9];
    public static String solveSteps = "";
    public static int stepCount = 1;
    public static boolean solveOk = true;
    
    private void findPos() {
      //record all possible values in each box on board:
        
        //cycle through all boxes
        for(int j = 0; j < 9; j++) {
            for(int k = 0; k < 9; k++) {
                //if current box is empty
                if(currentBoard[j][k] == 0) {
                    //cycle through numbers 1-9
                    for(int i = 1; i < 10; i++) {
                        boolean iPos = true;
                        int l, m;
                        //check for conflicts within row
                        for(l = 0; l < 9 && iPos; l++) {
                            if(currentBoard[l][k] == i) {
                                iPos = false;
                            }
                        }
                        //check for conflicts within column
                        for(l = 0; l < 9 && iPos; l++) {
                            if(currentBoard[j][l] == i) {
                                iPos = false;
                            }
                        }
                        //check for conflicts within 3x3 
                       for(l = 3*(j/3); l < 3*((j/3)+1) && iPos; l++) {
                            for(m = 3*(k/3); m < 3*((k/3)+1) && iPos; m++) {
                                if(currentBoard[l][m] == i) {
                                    iPos = false;
                                }
                            }
                        }
                        //if value is possible in current box, save to posBoard
                        if(iPos) {
                            posBoard[j][k][i-1] = (int)Math.pow(2, i-1);
                        }
                    }
                }
            }
        }
    }
    
    private void solveBoard() {
        solveComplete = true;
        
    //naked pairs: http://www.learn-sudoku.com/naked-pairs.html
        
        //cycle through each box on board
        for(int j = 0; j < 9; j++) {
            for(int k = 0; k < 9; k++) {
            	//if the box currently isn't solved,
                if(currentBoard[j][k] == 0) {
                    int l, m, l1, m1;
                    int sum = sumPos(j, k);
                    ArrayList<Integer> vals = new ArrayList<Integer>();
                    Set<Integer> valSet = new HashSet<Integer>();
                    //cycles through every value in row
                    for(l = 0; l < 9; l++) {
                            int[] box1 = new int[9];
                            int[] box2 = new int[9];
                            vals.clear();
                            //if the two boxes being compared are different and the box hasn't been solved
                        	if(k != l && currentBoard[j][l] == 0) {
                        		//gets list of the hints in the current box
                        		vals = getVals(sum);
                        		//adds list of the hints in the box being compared
                        		vals.addAll(getVals(sumPos(j, l)));
                        		valSet.clear();
                        		//removes duplicates by putting list into set
                        		valSet.addAll(vals);
                        		vals.clear();
                        		//moves contents of set back into list
                        		vals.addAll(valSet);
                        	}
                        	//if there are 2 boxes that share the same 2 numbers then those 2 numbers must go somewhere in those 2 boxes
                        	if(vals.size() == 2) {
                        		for(int i = 0; i < 9; i++) {
                        			box1[i] = posBoard[j][k][i];
                        			box2[i] = posBoard[j][l][i];
                        		}
                        		clearRow(j, k, vals.get(0));
                        		clearRow(j, k, vals.get(1));
                        		for(int i = 0; i < 9; i++) {
                        			posBoard[j][k][i] = box1[i];
                        			posBoard[j][l][i] = box2[i];
                        		}
                        	}
                    }
                    //same as above, but for columns
                    for(l = 0; l < 9; l++) {
                            int[] box1 = new int[9];
                            int[] box2 = new int[9];
                            vals.clear();
                        	if(j != l && currentBoard[l][k] == 0) {
                        		vals = getVals(sum);
                        		vals.addAll(getVals(sumPos(l, k)));
                        		valSet.clear();
                        		valSet.addAll(vals);
                        		vals.clear();
                        		vals.addAll(valSet);
                        	}
                        	if(vals.size() == 2) {
                        		for(int i = 0; i < 9; i++) {
                        			box1[i] = posBoard[j][k][i];
                        			box2[i] = posBoard[l][k][i];
                        		}
                        		clearCol(j, k, vals.get(0));
                        		clearCol(j, k, vals.get(1));
                        		for(int i = 0; i < 9; i++) {
                        			posBoard[j][k][i] = box1[i];
                        			posBoard[l][k][i] = box2[i];
                        		}
                        	}
                    }
                    //same as above, but for 3x3
                    for(l = 3*(j/3); l < 3*((j/3)+1); l++) {
                        for(l1 = 3*(k/3); l1 < 3*((k/3)+1); l1++) {
                    	    int[] box1 = new int[9];
                     	    int[] box2 = new int[9];
                            vals.clear();
                            if(!(j == l && k == l1) && currentBoard[l][l1] == 0) { 
                            	vals = getVals(sum);
            		        	vals.addAll(getVals(sumPos(l, l1)));
                        		valSet.clear();
                       			valSet.addAll(vals);
                        		vals.clear();
                        		vals.addAll(valSet);
                            }
                            if(vals.size() == 2) {
       		                	for(int i = 0; i < 9; i++) {
        	    	        		box1[i] = posBoard[j][k][i];
 		               	    		box2[i] = posBoard[l][l1][i];
                	        	}
                    	    	clear3x3(j, k, vals.get(0));
                        		clear3x3(j, k, vals.get(1));
                    	    	for(int i = 0; i < 9; i++) {
                        			posBoard[j][k][i] = box1[i];
                        			posBoard[l][l1][i] = box2[i];
                    	    	}
                        	}
                        }
                    }
                }
            }
        }
        
    //naked triplets: http://www.learn-sudoku.com/naked-triplets.html
    
    	//cycle through each box on board
    	for(int j = 0; j < 9; j++) {
            for(int k = 0; k < 9; k++) {
            	//if the current box isn't already solved,
                if(currentBoard[j][k] == 0) {
                    int l, m, l1, m1;
                    int sum = sumPos(j, k);
                    ArrayList<Integer> vals = new ArrayList<Integer>();
                    Set<Integer> valSet = new HashSet<Integer>();
                    //cycle through each value in row
                    for(l = 0; l < 9; l++) {
                        for(m = 0; m < 9; m++) {
                            int[] box1 = new int[9];
                            int[] box2 = new int[9];
                            int[] box3 = new int[9];
                            vals.clear();
                            //if all three boxes are unique and none of them are already solved,
                        	if(k != l && k != m && l != m && currentBoard[j][l] == 0 && currentBoard[j][m] == 0) {
                        		//get a list of all hints in current box
                        		vals = getVals(sum);
                        		//add a list of all hints in 1st box being compared
                        		vals.addAll(getVals(sumPos(j, l)));
                        		//add a list of all hints in 2nd box being compard
                        		vals.addAll(getVals(sumPos(j, m)));
                        		valSet.clear();
                        		//move list into set to remove duplicates
                        		valSet.addAll(vals);
                        		vals.clear();
                        		//move set back into list
                        		vals.addAll(valSet);
                        	}
                        	//if there are 3 boxes that share the same 3 numbers then those 3 numbers must go somewhere in those 3 boxes
                        	if(vals.size() == 3) {
                        		for(int i = 0; i < 9; i++) {
                        			box1[i] = posBoard[j][k][i];
                        			box2[i] = posBoard[j][l][i];
                        			box3[i] = posBoard[j][m][i];
                        		}
                        		clearRow(j, k, vals.get(0));
                        		clearRow(j, k, vals.get(1));
                        		clearRow(j, k, vals.get(2));
                        		for(int i = 0; i < 9; i++) {
                        			posBoard[j][k][i] = box1[i];
                        			posBoard[j][l][i] = box2[i];
                        			posBoard[j][m][i] = box3[i];
                        		}
                        	}
                        }
                    }
                    //same as above, but for columns
                    for(l = 0; l < 9; l++) {
                        for(m = 0; m < 9; m++) {
                            int[] box1 = new int[9];
                            int[] box2 = new int[9];
                            int[] box3 = new int[9];
                            vals.clear();
                        	if(j != l && j != m && l != m && currentBoard[l][k] == 0 && currentBoard[m][k] == 0) {
                        		vals = getVals(sum);
                        		vals.addAll(getVals(sumPos(l, k)));
                        		vals.addAll(getVals(sumPos(m, k)));
                        		valSet.clear();
                        		valSet.addAll(vals);
                        		vals.clear();
                        		vals.addAll(valSet);
                        	}
                        	if(vals.size() == 3) {
                        		for(int i = 0; i < 9; i++) {
                        			box1[i] = posBoard[j][k][i];
                        			box2[i] = posBoard[l][k][i];
                        			box3[i] = posBoard[m][k][i];
                        		}
                        		clearCol(j, k, vals.get(0));
                        		clearCol(j, k, vals.get(1));
                        		clearCol(j, k, vals.get(2));
                        		for(int i = 0; i < 9; i++) {
                        			posBoard[j][k][i] = box1[i];
                        			posBoard[l][k][i] = box2[i];
                        			posBoard[m][k][i] = box3[i];
                        		}
                        	}
                        }
                    }
                    //same as above, but for 3x3
                    for(l = 3*(j/3); l < 3*((j/3)+1); l++) {
                        for(l1 = 3*(k/3); l1 < 3*((k/3)+1); l1++) {
                        	for(m = 3*(j/3); m < 3*((j/3)+1); m++) {
                        		for(m1 = 3*(k/3); m1 < 3*((k/3)+1); m1++) {
                    	   		    int[] box1 = new int[9];
                     	    	    int[] box2 = new int[9];
                     	   		    int[] box3 = new int[9];
                                    vals.clear();
                                	if(!(j == l && k == l1) && !(j == m && k == m1) && !(l == m && l1 == m1) && currentBoard[l][l1] == 0 && currentBoard[m][m1] == 0) { 
                                		vals = getVals(sum);
            		              		vals.addAll(getVals(sumPos(l, l1)));
                    		    		vals.addAll(getVals(sumPos(m, m1)));
                        				valSet.clear();
                       			 		valSet.addAll(vals);
                        				vals.clear();
                        				vals.addAll(valSet);
                                	}
                                	if(vals.size() == 3) {
       		                 			for(int i = 0; i < 9; i++) {
        	    	            			box1[i] = posBoard[j][k][i];
 		               	        			box2[i] = posBoard[l][l1][i];
            	        	    			box3[i] = posBoard[m][m1][i];
                	        			}
                    	    			clear3x3(j, k, vals.get(0));
                        				clear3x3(j, k, vals.get(1));
              		        	  		clear3x3(j, k, vals.get(2));
                    	    			for(int i = 0; i < 9; i++) {
                        					posBoard[j][k][i] = box1[i];
                        					posBoard[l][l1][i] = box2[i];
                        					posBoard[m][m1][i] = box3[i];
                    	    			}
                	        		}
                    	    	}
                        	}
                        }
                    }
                }
            }
        }
        
    //hidden singles: http://www.learn-sudoku.com/hidden-singles.html
        
        //cycle through each box on board
        for(int j = 0; j < 9; j++) {
            for(int k = 0; k < 9; k++) {
                //cycle through values 1-9
                for(int i = 0; i < 9; i++) {
                    if(currentBoard[j][k] == 0) {
                        //if current value is possible in this box
                        if(posBoard[j][k][i] != 0) {
                            int l, m;
                            boolean iAdd1 = true;
                            boolean iAdd2 = true;
                            boolean iAdd3 = true;
                            //check to see if value is possible elsewhere in row
                            for(l = 0; l < 9 && iAdd1; l++) {
                                if(posBoard[l][k][i] != 0 && currentBoard[l][k] == 0 && l != j) {
                                    iAdd1 = false;
                                }
                            }
                            //check to see if value is possible elsewhere in column
                            for(l = 0; l < 9 && iAdd2; l++) {
                                if(posBoard[j][l][i] != 0 && currentBoard[j][l] == 0 && l != k) {
                                    iAdd2 = false;
                                }
                            }
                            //check to see if value is possible elsewhere in 3x3
                            for(l = 3*(j/3); l < 3*((j/3)+1) && iAdd3; l++) {
                                for(m = 3*(k/3); m < 3*((k/3)+1) && iAdd3; m++) {
                                    if(posBoard[l][m][i] != 0 && currentBoard[l][m] == 0 && !(l == j && m == k)) {
                                        iAdd3 = false;
                                    }
                                }
                            }
                            //if value is only possible in current box, save to solution
                            if(iAdd1 || iAdd2 || iAdd3) {
                                currentBoard[j][k] = i+1;
                                clearPos(j, k, i);
                                solveSteps += stepCount + ". (" + getLetter(j) + "," + (k+1) + ") :   " + (i+1) + "\n";
                                stepCount++;
                            }
                        }
                    }
                }
            }
            solveComplete = checkDone();
        }
        
    //lone singles: http://www.learn-sudoku.com/lone-singles.html
        
        //cycle through each box on board
        for(int j = 0; j < 9; j++) {
            for(int k = 0; k < 9; k++) {
            	//if current box hasn't been solved,
                if(currentBoard[j][k] == 0) {
                    boolean iAdd = false;
                    int addVal = 0;
                    ArrayList<Integer> vals = new ArrayList<Integer>();
                    //get list of all hints in current box
                    vals = getVals(sumPos(j, k));
                    //if the list is 1 value long, then that value has to go in this box
                    if(vals.size() == 1) {
                    	iAdd = true;
                    	addVal = vals.get(0);
                    }
                    if(iAdd) {
                        currentBoard[j][k] = addVal+1;
                        clearPos(j, k, addVal);
                        solveSteps += stepCount + ". (" + getLetter(j) + "," + (k+1) + ") :   " + (addVal+1) + "\n";
                        stepCount++;
                    }
                }
            }
        }
    }
    
    //returns sum of all possible values in given coords
    private int sumPos(int j, int k) {
        int sum = 0;
        for(int i = 0; i < 9; i++) {
            sum += posBoard[j][k][i];
        }
        return sum;
    }
    
    //return a list of a number's binary exponents plus 1 (ex. 21 = 2^4 + 2^2 + 2^0, return {5, 3, 1})
    private ArrayList<Integer> getVals(int x) {
        ArrayList<Integer> vals = new ArrayList<Integer>();
        int n;
        for(int i = 8; i >= 0; i--) {
            n = (int)Math.pow(2, i);
            if(n <= x) {
                vals.add(i);
                x -= n;
            }
        }
        return vals;
    }
    
    //clears all possible values from given square
    private void clearSq(int j, int k) {
    	for(int i = 0; i < 9; i++) {
    		posBoard[j][k][i] = 0;
    	}
    }
    
    //clears value from possible values in row, column, and 3x3
    private void clearPos(int j, int k, int i) {
        for(int l = 0; l < 9; l++) {
            posBoard[j][l][i] = 0;
        }
        for(int l = 0; l < 9; l++) {
            posBoard[l][k][i] = 0;
        }
        for(int l = 3*(j/3); l < 3*((j/3)+1); l++) {
            for(int m = 3*(k/3); m < 3*((k/3)+1); m++) {
                posBoard[l][m][i] = 0;
            }
        }
    }
    
    //clears value from possible values in row
    private void clearRow(int j, int k, int i) {
        for(int l = 0; l < 9; l++) {
            posBoard[j][l][i] = 0;
        }
    }
    
    //clears value from possible values in column
    private void clearCol(int j, int k, int i) {
        for(int l = 0; l < 9; l++) {
            posBoard[l][k][i] = 0;
        }
    }
    
    //clears value from possible values in 3x3
    private void clear3x3(int j, int k, int i) {
        for(int l = 3*(j/3); l < 3*((j/3)+1); l++) {
            for(int m = 3*(k/3); m < 3*((k/3)+1); m++) {
                posBoard[l][k][i] = 0;
            }
        }
    }
    
    //check to see if puzzle is solved
    private boolean checkDone() {
        boolean done = true;
        for(int j = 0; j < 9 && done; j++) {
            for(int k = 0; k < 9 && done; k++) {
                if(currentBoard[j][k] == 0) {
                    done = false;
                }
            }
        }
        return done;
    }
    
    //converts x-coordinate to corresponding letter
    private String getLetter(int i) {
    	String s = "";
    	switch(i) {
    		case 0: s = "A";
    		        break;
    		case 1: s = "B";
    		        break;
    		case 2: s = "C";
    		        break;
    		case 3: s = "D";
    		        break;
    		case 4: s = "E";
    		        break;
    		case 5: s = "F";
    		        break;
    		case 6: s = "G";
    		        break;
    		case 7: s = "H";
    		        break;
    		case 8: s = "I";
    		        break;
    	}
    	return s;
    }
    
    //fills currentBoard with values entered by user
    private void fillBoard() {
        sBoard[0][0] = box1_1.getText();
        sBoard[0][1] = box1_2.getText();
        sBoard[0][2] = box1_3.getText();
        sBoard[1][0] = box1_4.getText();
        sBoard[1][1] = box1_5.getText();
        sBoard[1][2] = box1_6.getText();
        sBoard[2][0] = box1_7.getText();
        sBoard[2][1] = box1_8.getText();
        sBoard[2][2] = box1_9.getText();
        
        sBoard[0][3] = box2_1.getText();
        sBoard[0][4] = box2_2.getText();
        sBoard[0][5] = box2_3.getText();
        sBoard[1][3] = box2_4.getText();
        sBoard[1][4] = box2_5.getText();
        sBoard[1][5] = box2_6.getText();
        sBoard[2][3] = box2_7.getText();
        sBoard[2][4] = box2_8.getText();
        sBoard[2][5] = box2_9.getText();
        
        sBoard[0][6] = box3_1.getText();
        sBoard[0][7] = box3_2.getText();
        sBoard[0][8] = box3_3.getText();
        sBoard[1][6] = box3_4.getText();
        sBoard[1][7] = box3_5.getText();
        sBoard[1][8] = box3_6.getText();
        sBoard[2][6] = box3_7.getText();
        sBoard[2][7] = box3_8.getText();
        sBoard[2][8] = box3_9.getText();
        
        sBoard[3][0] = box4_1.getText();
        sBoard[3][1] = box4_2.getText();
        sBoard[3][2] = box4_3.getText();
        sBoard[4][0] = box4_4.getText();
        sBoard[4][1] = box4_5.getText();
        sBoard[4][2] = box4_6.getText();
        sBoard[5][0] = box4_7.getText();
        sBoard[5][1] = box4_8.getText();
        sBoard[5][2] = box4_9.getText();
        
        sBoard[3][3] = box5_1.getText();
        sBoard[3][4] = box5_2.getText();
        sBoard[3][5] = box5_3.getText();
        sBoard[4][3] = box5_4.getText();
        sBoard[4][4] = box5_5.getText();
        sBoard[4][5] = box5_6.getText();
        sBoard[5][3] = box5_7.getText();
        sBoard[5][4] = box5_8.getText();
        sBoard[5][5] = box5_9.getText();
        
        sBoard[3][6] = box6_1.getText();
        sBoard[3][7] = box6_2.getText();
        sBoard[3][8] = box6_3.getText();
        sBoard[4][6] = box6_4.getText();
        sBoard[4][7] = box6_5.getText();
        sBoard[4][8] = box6_6.getText();
        sBoard[5][6] = box6_7.getText();
        sBoard[5][7] = box6_8.getText();
        sBoard[5][8] = box6_9.getText();
        
        sBoard[6][0] = box7_1.getText();
        sBoard[6][1] = box7_2.getText();
        sBoard[6][2] = box7_3.getText();
        sBoard[7][0] = box7_4.getText();
        sBoard[7][1] = box7_5.getText();
        sBoard[7][2] = box7_6.getText();
        sBoard[8][0] = box7_7.getText();
        sBoard[8][1] = box7_8.getText();
        sBoard[8][2] = box7_9.getText();
        
        sBoard[6][3] = box8_1.getText();
        sBoard[6][4] = box8_2.getText();
        sBoard[6][5] = box8_3.getText();
        sBoard[7][3] = box8_4.getText();
        sBoard[7][4] = box8_5.getText();
        sBoard[7][5] = box8_6.getText();
        sBoard[8][3] = box8_7.getText();
        sBoard[8][4] = box8_8.getText();
        sBoard[8][5] = box8_9.getText();
        
        sBoard[6][6] = box9_1.getText();
        sBoard[6][7] = box9_2.getText();
        sBoard[6][8] = box9_3.getText();
        sBoard[7][6] = box9_4.getText();
        sBoard[7][7] = box9_5.getText();
        sBoard[7][8] = box9_6.getText();
        sBoard[8][6] = box9_7.getText();
        sBoard[8][7] = box9_8.getText();
        sBoard[8][8] = box9_9.getText();
        
        for(int i = 0; i < 9; i++) {
            for(int j = 0; j < 9; j++) {
                if(!(sBoard[i][j].equals(""))) {
                	int addVal = Integer.parseInt(sBoard[i][j]);
                	if(addVal > 0 && addVal < 10) {
                		currentBoard[i][j] = addVal;
                	}
                    else {
                    	solveSteps = "Please only enter\nvalues from\n0 - 9";
                    	solveComplete = true;
                    	solveOk = false;
                    }
                }
                else {
                    currentBoard[i][j] = 0;
                }
                //System.out.print(currentBoard[i][j] + " ");
            }
            //System.out.println("");
        }
    }
    
    //writes solution to GUI after solving
    private void writeBoard() {
        box1_1.setText(String.valueOf(currentBoard[0][0]));
        box1_2.setText(String.valueOf(currentBoard[0][1]));
        box1_3.setText(String.valueOf(currentBoard[0][2]));
        box1_4.setText(String.valueOf(currentBoard[1][0]));
        box1_5.setText(String.valueOf(currentBoard[1][1]));
        box1_6.setText(String.valueOf(currentBoard[1][2]));
        box1_7.setText(String.valueOf(currentBoard[2][0]));
        box1_8.setText(String.valueOf(currentBoard[2][1]));
        box1_9.setText(String.valueOf(currentBoard[2][2]));
        
        box2_1.setText(String.valueOf(currentBoard[0][3]));
        box2_2.setText(String.valueOf(currentBoard[0][4]));
        box2_3.setText(String.valueOf(currentBoard[0][5]));
        box2_4.setText(String.valueOf(currentBoard[1][3]));
        box2_5.setText(String.valueOf(currentBoard[1][4]));
        box2_6.setText(String.valueOf(currentBoard[1][5]));
        box2_7.setText(String.valueOf(currentBoard[2][3]));
        box2_8.setText(String.valueOf(currentBoard[2][4]));
        box2_9.setText(String.valueOf(currentBoard[2][5]));
        
        box3_1.setText(String.valueOf(currentBoard[0][6]));
        box3_2.setText(String.valueOf(currentBoard[0][7]));
        box3_3.setText(String.valueOf(currentBoard[0][8]));
        box3_4.setText(String.valueOf(currentBoard[1][6]));
        box3_5.setText(String.valueOf(currentBoard[1][7]));
        box3_6.setText(String.valueOf(currentBoard[1][8]));
        box3_7.setText(String.valueOf(currentBoard[2][6]));
        box3_8.setText(String.valueOf(currentBoard[2][7]));
        box3_9.setText(String.valueOf(currentBoard[2][8]));
        
        box4_1.setText(String.valueOf(currentBoard[3][0]));
        box4_2.setText(String.valueOf(currentBoard[3][1]));
        box4_3.setText(String.valueOf(currentBoard[3][2]));
        box4_4.setText(String.valueOf(currentBoard[4][0]));
        box4_5.setText(String.valueOf(currentBoard[4][1]));
        box4_6.setText(String.valueOf(currentBoard[4][2]));
        box4_7.setText(String.valueOf(currentBoard[5][0]));
        box4_8.setText(String.valueOf(currentBoard[5][1]));
        box4_9.setText(String.valueOf(currentBoard[5][2]));
        
        box5_1.setText(String.valueOf(currentBoard[3][3]));
        box5_2.setText(String.valueOf(currentBoard[3][4]));
        box5_3.setText(String.valueOf(currentBoard[3][5]));
        box5_4.setText(String.valueOf(currentBoard[4][3]));
        box5_5.setText(String.valueOf(currentBoard[4][4]));
        box5_6.setText(String.valueOf(currentBoard[4][5]));
        box5_7.setText(String.valueOf(currentBoard[5][3]));
        box5_8.setText(String.valueOf(currentBoard[5][4]));
        box5_9.setText(String.valueOf(currentBoard[5][5]));
        
        box6_1.setText(String.valueOf(currentBoard[3][6]));
        box6_2.setText(String.valueOf(currentBoard[3][7]));
        box6_3.setText(String.valueOf(currentBoard[3][8]));
        box6_4.setText(String.valueOf(currentBoard[4][6]));
        box6_5.setText(String.valueOf(currentBoard[4][7]));
        box6_6.setText(String.valueOf(currentBoard[4][8]));
        box6_7.setText(String.valueOf(currentBoard[5][6]));
        box6_8.setText(String.valueOf(currentBoard[5][7]));
        box6_9.setText(String.valueOf(currentBoard[5][8]));
        
        box7_1.setText(String.valueOf(currentBoard[6][0]));
        box7_2.setText(String.valueOf(currentBoard[6][1]));
        box7_3.setText(String.valueOf(currentBoard[6][2]));
        box7_4.setText(String.valueOf(currentBoard[7][0]));
        box7_5.setText(String.valueOf(currentBoard[7][1]));
        box7_6.setText(String.valueOf(currentBoard[7][2]));
        box7_7.setText(String.valueOf(currentBoard[8][0]));
        box7_8.setText(String.valueOf(currentBoard[8][1]));
        box7_9.setText(String.valueOf(currentBoard[8][2]));
        
        box8_1.setText(String.valueOf(currentBoard[6][3]));
        box8_2.setText(String.valueOf(currentBoard[6][4]));
        box8_3.setText(String.valueOf(currentBoard[6][5]));
        box8_4.setText(String.valueOf(currentBoard[7][3]));
        box8_5.setText(String.valueOf(currentBoard[7][4]));
        box8_6.setText(String.valueOf(currentBoard[7][5]));
        box8_7.setText(String.valueOf(currentBoard[8][3]));
        box8_8.setText(String.valueOf(currentBoard[8][4]));
        box8_9.setText(String.valueOf(currentBoard[8][5]));
        
        box9_1.setText(String.valueOf(currentBoard[6][6]));
        box9_2.setText(String.valueOf(currentBoard[6][7]));
        box9_3.setText(String.valueOf(currentBoard[6][8]));
        box9_4.setText(String.valueOf(currentBoard[7][6]));
        box9_5.setText(String.valueOf(currentBoard[7][7]));
        box9_6.setText(String.valueOf(currentBoard[7][8]));
        box9_7.setText(String.valueOf(currentBoard[8][6]));
        box9_8.setText(String.valueOf(currentBoard[8][7]));
        box9_9.setText(String.valueOf(currentBoard[8][8]));
        
        /*for(int j = 0; j < 9; j++) {
            for(int k = 0; k < 9; k++) {
                currentBoard[j][k] = 0;
            }
        }*/
    }
    
    //clears board
    public void clearBoard() {
    	box1_1.setText("");
        box1_2.setText("");
        box1_3.setText("");
        box1_4.setText("");
        box1_5.setText("");
        box1_6.setText("");
        box1_7.setText("");
        box1_8.setText("");
        box1_9.setText("");
        
        box2_1.setText("");
        box2_2.setText("");
        box2_3.setText("");
        box2_4.setText("");
        box2_5.setText("");
        box2_6.setText("");
        box2_7.setText("");
        box2_8.setText("");
        box2_9.setText("");
        
        box3_1.setText("");
        box3_2.setText("");
        box3_3.setText("");
        box3_4.setText("");
        box3_5.setText("");
        box3_6.setText("");
        box3_7.setText("");
        box3_8.setText("");
        box3_9.setText("");
        
        box4_1.setText("");
        box4_2.setText("");
        box4_3.setText("");
        box4_4.setText("");
        box4_5.setText("");
        box4_6.setText("");
        box4_7.setText("");
        box4_8.setText("");
        box4_9.setText("");
        
        box5_1.setText("");
        box5_2.setText("");
        box5_3.setText("");
        box5_4.setText("");
        box5_5.setText("");
        box5_6.setText("");
        box5_7.setText("");
        box5_8.setText("");
        box5_9.setText("");
        
        box6_1.setText("");
        box6_2.setText("");
        box6_3.setText("");
        box6_4.setText("");
        box6_5.setText("");
        box6_6.setText("");
        box6_7.setText("");
        box6_8.setText("");
        box6_9.setText("");
        
        box7_1.setText("");
        box7_2.setText("");
        box7_3.setText("");
        box7_4.setText("");
        box7_5.setText("");
        box7_6.setText("");
        box7_7.setText("");
        box7_8.setText("");
        box7_9.setText("");
        
        box8_1.setText("");
        box8_2.setText("");
        box8_3.setText("");
        box8_4.setText("");
        box8_5.setText("");
        box8_6.setText("");
        box8_7.setText("");
        box8_8.setText("");
        box8_9.setText("");
        
        box9_1.setText("");
        box9_2.setText("");
        box9_3.setText("");
        box9_4.setText("");
        box9_5.setText("");
        box9_6.setText("");
        box9_7.setText("");
        box9_8.setText("");
        box9_9.setText("");
        makeBlack();
        solveSteps = "";
        jTextArea1.setText(solveSteps);
        clearAllPos();
    }
    
    //clears all possible values from posBoard
    public void clearAllPos() {
    	for(int i = 0; i < 9; i++) {
    		for(int j = 0; j < 9; j++) {
    			for(int k = 0; k < 9; k++) {
    				posBoard[i][j][k] = 0;
    			}
    		}
    	}
    }
    
    //makes solved boxes display magenta colors
    public void makeMag() {
    	if(box1_1.getText().equals(""))
    		box1_1.setForeground(Color.MAGENTA);
    	if(box1_2.getText().equals(""))
    		box1_2.setForeground(Color.MAGENTA);
    	if(box1_3.getText().equals(""))
    		box1_3.setForeground(Color.MAGENTA);
    	if(box1_4.getText().equals(""))
    		box1_4.setForeground(Color.MAGENTA);
    	if(box1_5.getText().equals(""))
    		box1_5.setForeground(Color.MAGENTA);
    	if(box1_6.getText().equals(""))
    		box1_6.setForeground(Color.MAGENTA);
    	if(box1_7.getText().equals(""))
    		box1_7.setForeground(Color.MAGENTA);
    	if(box1_8.getText().equals(""))
    		box1_8.setForeground(Color.MAGENTA);
    	if(box1_9.getText().equals(""))
    		box1_9.setForeground(Color.MAGENTA);
    		
    	if(box2_1.getText().equals(""))
    		box2_1.setForeground(Color.MAGENTA);
    	if(box2_2.getText().equals(""))
    		box2_2.setForeground(Color.MAGENTA);
    	if(box2_3.getText().equals(""))
    		box2_3.setForeground(Color.MAGENTA);
    	if(box2_4.getText().equals(""))
    		box2_4.setForeground(Color.MAGENTA);
    	if(box2_5.getText().equals(""))
    		box2_5.setForeground(Color.MAGENTA);
    	if(box2_6.getText().equals(""))
    		box2_6.setForeground(Color.MAGENTA);
    	if(box2_7.getText().equals(""))
    		box2_7.setForeground(Color.MAGENTA);
    	if(box2_8.getText().equals(""))
    		box2_8.setForeground(Color.MAGENTA);
    	if(box2_9.getText().equals(""))
    		box2_9.setForeground(Color.MAGENTA);
    		
    	if(box3_1.getText().equals(""))
    		box3_1.setForeground(Color.MAGENTA);
    	if(box3_2.getText().equals(""))
    		box3_2.setForeground(Color.MAGENTA);
    	if(box3_3.getText().equals(""))
    		box3_3.setForeground(Color.MAGENTA);
    	if(box3_4.getText().equals(""))
    		box3_4.setForeground(Color.MAGENTA);
    	if(box3_5.getText().equals(""))
    		box3_5.setForeground(Color.MAGENTA);
    	if(box3_6.getText().equals(""))
    		box3_6.setForeground(Color.MAGENTA);
    	if(box3_7.getText().equals(""))
    		box3_7.setForeground(Color.MAGENTA);
    	if(box3_8.getText().equals(""))
    		box3_8.setForeground(Color.MAGENTA);
    	if(box3_9.getText().equals(""))
    		box3_9.setForeground(Color.MAGENTA);
    		
    	if(box4_1.getText().equals(""))
    		box4_1.setForeground(Color.MAGENTA);
    	if(box4_2.getText().equals(""))
    		box4_2.setForeground(Color.MAGENTA);
    	if(box4_3.getText().equals(""))
    		box4_3.setForeground(Color.MAGENTA);
    	if(box4_4.getText().equals(""))
    		box4_4.setForeground(Color.MAGENTA);
    	if(box4_5.getText().equals(""))
    		box4_5.setForeground(Color.MAGENTA);
    	if(box4_6.getText().equals(""))
    		box4_6.setForeground(Color.MAGENTA);
    	if(box4_7.getText().equals(""))
    		box4_7.setForeground(Color.MAGENTA);
    	if(box4_8.getText().equals(""))
    		box4_8.setForeground(Color.MAGENTA);
    	if(box4_9.getText().equals(""))
    		box4_9.setForeground(Color.MAGENTA);
    		
    	if(box5_1.getText().equals(""))
    		box5_1.setForeground(Color.MAGENTA);
    	if(box5_2.getText().equals(""))
    		box5_2.setForeground(Color.MAGENTA);
    	if(box5_3.getText().equals(""))
    		box5_3.setForeground(Color.MAGENTA);
    	if(box5_4.getText().equals(""))
    		box5_4.setForeground(Color.MAGENTA);
    	if(box5_5.getText().equals(""))
    		box5_5.setForeground(Color.MAGENTA);
    	if(box5_6.getText().equals(""))
    		box5_6.setForeground(Color.MAGENTA);
    	if(box5_7.getText().equals(""))
    		box5_7.setForeground(Color.MAGENTA);
    	if(box5_8.getText().equals(""))
    		box5_8.setForeground(Color.MAGENTA);
    	if(box5_9.getText().equals(""))
    		box5_9.setForeground(Color.MAGENTA);
    		
    	if(box6_1.getText().equals(""))
    		box6_1.setForeground(Color.MAGENTA);
    	if(box6_2.getText().equals(""))
    		box6_2.setForeground(Color.MAGENTA);
    	if(box6_3.getText().equals(""))
    		box6_3.setForeground(Color.MAGENTA);
    	if(box6_4.getText().equals(""))
    		box6_4.setForeground(Color.MAGENTA);
    	if(box6_5.getText().equals(""))
    		box6_5.setForeground(Color.MAGENTA);
    	if(box6_6.getText().equals(""))
    		box6_6.setForeground(Color.MAGENTA);
    	if(box6_7.getText().equals(""))
    		box6_7.setForeground(Color.MAGENTA);
    	if(box6_8.getText().equals(""))
    		box6_8.setForeground(Color.MAGENTA);
    	if(box6_9.getText().equals(""))
    		box6_9.setForeground(Color.MAGENTA);
    		
    	if(box7_1.getText().equals(""))
    		box7_1.setForeground(Color.MAGENTA);
    	if(box7_2.getText().equals(""))
    		box7_2.setForeground(Color.MAGENTA);
    	if(box7_3.getText().equals(""))
    		box7_3.setForeground(Color.MAGENTA);
    	if(box7_4.getText().equals(""))
    		box7_4.setForeground(Color.MAGENTA);
    	if(box7_5.getText().equals(""))
    		box7_5.setForeground(Color.MAGENTA);
    	if(box7_6.getText().equals(""))
    		box7_6.setForeground(Color.MAGENTA);
    	if(box7_7.getText().equals(""))
    		box7_7.setForeground(Color.MAGENTA);
    	if(box7_8.getText().equals(""))
    		box7_8.setForeground(Color.MAGENTA);
    	if(box7_9.getText().equals(""))
    		box7_9.setForeground(Color.MAGENTA);
    		
    	if(box8_1.getText().equals(""))
    		box8_1.setForeground(Color.MAGENTA);
    	if(box8_2.getText().equals(""))
    		box8_2.setForeground(Color.MAGENTA);
    	if(box8_3.getText().equals(""))
    		box8_3.setForeground(Color.MAGENTA);
    	if(box8_4.getText().equals(""))
    		box8_4.setForeground(Color.MAGENTA);
    	if(box8_5.getText().equals(""))
    		box8_5.setForeground(Color.MAGENTA);
    	if(box8_6.getText().equals(""))
    		box8_6.setForeground(Color.MAGENTA);
    	if(box8_7.getText().equals(""))
    		box8_7.setForeground(Color.MAGENTA);
    	if(box8_8.getText().equals(""))
    		box8_8.setForeground(Color.MAGENTA);
    	if(box8_9.getText().equals(""))
    		box8_9.setForeground(Color.MAGENTA);
    		
    	if(box9_1.getText().equals(""))
    		box9_1.setForeground(Color.MAGENTA);
    	if(box9_2.getText().equals(""))
    		box9_2.setForeground(Color.MAGENTA);
    	if(box9_3.getText().equals(""))
    		box9_3.setForeground(Color.MAGENTA);
    	if(box9_4.getText().equals(""))
    		box9_4.setForeground(Color.MAGENTA);
    	if(box9_5.getText().equals(""))
    		box9_5.setForeground(Color.MAGENTA);
    	if(box9_6.getText().equals(""))
    		box9_6.setForeground(Color.MAGENTA);
    	if(box9_7.getText().equals(""))
    		box9_7.setForeground(Color.MAGENTA);
    	if(box9_8.getText().equals(""))
    		box9_8.setForeground(Color.MAGENTA);
    	if(box9_9.getText().equals(""))
    		box9_9.setForeground(Color.MAGENTA);
    }
    
    //makes all boxes black
    public void makeBlack() {
    	box1_1.setForeground(Color.BLACK);
    	box1_2.setForeground(Color.BLACK);
    	box1_3.setForeground(Color.BLACK);
    	box1_4.setForeground(Color.BLACK);
    	box1_5.setForeground(Color.BLACK);
    	box1_6.setForeground(Color.BLACK);
    	box1_7.setForeground(Color.BLACK);
    	box1_8.setForeground(Color.BLACK);
    	box1_9.setForeground(Color.BLACK);
    	
    	box2_1.setForeground(Color.BLACK);
    	box2_2.setForeground(Color.BLACK);
    	box2_3.setForeground(Color.BLACK);
    	box2_4.setForeground(Color.BLACK);
    	box2_5.setForeground(Color.BLACK);
    	box2_6.setForeground(Color.BLACK);
    	box2_7.setForeground(Color.BLACK);
    	box2_8.setForeground(Color.BLACK);
    	box2_9.setForeground(Color.BLACK);
    	
    	box3_1.setForeground(Color.BLACK);
    	box3_2.setForeground(Color.BLACK);
    	box3_3.setForeground(Color.BLACK);
    	box3_4.setForeground(Color.BLACK);
    	box3_5.setForeground(Color.BLACK);
    	box3_6.setForeground(Color.BLACK);
    	box3_7.setForeground(Color.BLACK);
    	box3_8.setForeground(Color.BLACK);
    	box3_9.setForeground(Color.BLACK);
    	
    	box4_1.setForeground(Color.BLACK);
    	box4_2.setForeground(Color.BLACK);
    	box4_3.setForeground(Color.BLACK);
    	box4_4.setForeground(Color.BLACK);
    	box4_5.setForeground(Color.BLACK);
    	box4_6.setForeground(Color.BLACK);
    	box4_7.setForeground(Color.BLACK);
    	box4_8.setForeground(Color.BLACK);
    	box4_9.setForeground(Color.BLACK);
    	
    	box5_1.setForeground(Color.BLACK);
    	box5_2.setForeground(Color.BLACK);
    	box5_3.setForeground(Color.BLACK);
    	box5_4.setForeground(Color.BLACK);
    	box5_5.setForeground(Color.BLACK);
    	box5_6.setForeground(Color.BLACK);
    	box5_7.setForeground(Color.BLACK);
    	box5_8.setForeground(Color.BLACK);
    	box5_9.setForeground(Color.BLACK);
    	
    	box6_1.setForeground(Color.BLACK);
    	box6_2.setForeground(Color.BLACK);
    	box6_3.setForeground(Color.BLACK);
    	box6_4.setForeground(Color.BLACK);
    	box6_5.setForeground(Color.BLACK);
    	box6_6.setForeground(Color.BLACK);
    	box6_7.setForeground(Color.BLACK);
    	box6_8.setForeground(Color.BLACK);
    	box6_9.setForeground(Color.BLACK);
    	
    	box7_1.setForeground(Color.BLACK);
    	box7_2.setForeground(Color.BLACK);
    	box7_3.setForeground(Color.BLACK);
    	box7_4.setForeground(Color.BLACK);
    	box7_5.setForeground(Color.BLACK);
    	box7_6.setForeground(Color.BLACK);
    	box7_7.setForeground(Color.BLACK);
    	box7_8.setForeground(Color.BLACK);
    	box7_9.setForeground(Color.BLACK);
    	
    	box8_1.setForeground(Color.BLACK);
    	box8_2.setForeground(Color.BLACK);
    	box8_3.setForeground(Color.BLACK);
    	box8_4.setForeground(Color.BLACK);
    	box8_5.setForeground(Color.BLACK);
    	box8_6.setForeground(Color.BLACK);
    	box8_7.setForeground(Color.BLACK);
    	box8_8.setForeground(Color.BLACK);
    	box8_9.setForeground(Color.BLACK);
    	
    	box9_1.setForeground(Color.BLACK);
    	box9_2.setForeground(Color.BLACK);
    	box9_3.setForeground(Color.BLACK);
    	box9_4.setForeground(Color.BLACK);
    	box9_5.setForeground(Color.BLACK);
    	box9_6.setForeground(Color.BLACK);
    	box9_7.setForeground(Color.BLACK);
    	box9_8.setForeground(Color.BLACK);
    	box9_9.setForeground(Color.BLACK);
    }
    
    //fills boxes with example 1
    public void fillEx1() {
    	box1_1.setText("");
        box1_2.setText("");
        box1_3.setText("8");
        box1_4.setText("3");
        box1_5.setText("");
        box1_6.setText("");
        box1_7.setText("4");
        box1_8.setText("1");
        box1_9.setText("");
        
        box2_1.setText("");
        box2_2.setText("3");
        box2_3.setText("");
        box2_4.setText("4");
        box2_5.setText("");
        box2_6.setText("7");
        box2_7.setText("");
        box2_8.setText("");
        box2_9.setText("8");
        
        box3_1.setText("5");
        box3_2.setText("4");
        box3_3.setText("");
        box3_4.setText("9");
        box3_5.setText("");
        box3_6.setText("");
        box3_7.setText("");
        box3_8.setText("");
        box3_9.setText("2");
        
        box4_1.setText("");
        box4_2.setText("4");
        box4_3.setText("3");
        box4_4.setText("5");
        box4_5.setText("");
        box4_6.setText("");
        box4_7.setText("");
        box4_8.setText("6");
        box4_9.setText("");
        
        box5_1.setText("5");
        box5_2.setText("");
        box5_3.setText("2");
        box5_4.setText("");
        box5_5.setText("");
        box5_6.setText("");
        box5_7.setText("3");
        box5_8.setText("");
        box5_9.setText("9");
        
        box6_1.setText("");
        box6_2.setText("6");
        box6_3.setText("");
        box6_4.setText("");
        box6_5.setText("");
        box6_6.setText("8");
        box6_7.setText("4");
        box6_8.setText("1");
        box6_9.setText("");
        
        box7_1.setText("1");
        box7_2.setText("");
        box7_3.setText("");
        box7_4.setText("");
        box7_5.setText("");
        box7_6.setText("5");
        box7_7.setText("");
        box7_8.setText("2");
        box7_9.setText("9");
        
        box8_1.setText("8");
        box8_2.setText("");
        box8_3.setText("");
        box8_4.setText("6");
        box8_5.setText("");
        box8_6.setText("3");
        box8_7.setText("");
        box8_8.setText("7");
        box8_9.setText("");
        
        box9_1.setText("");
        box9_2.setText("2");
        box9_3.setText("7");
        box9_4.setText("");
        box9_5.setText("");
        box9_6.setText("4");
        box9_7.setText("8");
        box9_8.setText("");
        box9_9.setText("");
    }
    
    //fills boxes with example 2
    public void fillEx2() {
    	box1_1.setText("");
        box1_2.setText("");
        box1_3.setText("7");
        box1_4.setText("");
        box1_5.setText("4");
        box1_6.setText("");
        box1_7.setText("");
        box1_8.setText("");
        box1_9.setText("");
        
        box2_1.setText("9");
        box2_2.setText("");
        box2_3.setText("");
        box2_4.setText("");
        box2_5.setText("2");
        box2_6.setText("");
        box2_7.setText("");
        box2_8.setText("5");
        box2_9.setText("");
        
        box3_1.setText("6");
        box3_2.setText("");
        box3_3.setText("");
        box3_4.setText("");
        box3_5.setText("7");
        box3_6.setText("");
        box3_7.setText("8");
        box3_8.setText("");
        box3_9.setText("");
        
        box4_1.setText("");
        box4_2.setText("3");
        box4_3.setText("1");
        box4_4.setText("8");
        box4_5.setText("");
        box4_6.setText("");
        box4_7.setText("");
        box4_8.setText("");
        box4_9.setText("");
        
        box5_1.setText("");
        box5_2.setText("8");
        box5_3.setText("9");
        box5_4.setText("2");
        box5_5.setText("");
        box5_6.setText("5");
        box5_7.setText("1");
        box5_8.setText("3");
        box5_9.setText("");
        
        box6_1.setText("");
        box6_2.setText("");
        box6_3.setText("");
        box6_4.setText("");
        box6_5.setText("");
        box6_6.setText("3");
        box6_7.setText("9");
        box6_8.setText("8");
        box6_9.setText("");
        
        box7_1.setText("");
        box7_2.setText("");
        box7_3.setText("8");
        box7_4.setText("");
        box7_5.setText("5");
        box7_6.setText("");
        box7_7.setText("");
        box7_8.setText("");
        box7_9.setText("4");
        
        box8_1.setText("");
        box8_2.setText("7");
        box8_3.setText("");
        box8_4.setText("");
        box8_5.setText("4");
        box8_6.setText("");
        box8_7.setText("");
        box8_8.setText("");
        box8_9.setText("8");
        
        box9_1.setText("");
        box9_2.setText("");
        box9_3.setText("");
        box9_4.setText("");
        box9_5.setText("6");
        box9_6.setText("");
        box9_7.setText("5");
        box9_8.setText("");
        box9_9.setText("");
    }
    
    //fills boxes with example 3
    public void fillEx3() {
    	box1_1.setText("");
        box1_2.setText("");
        box1_3.setText("6");
        box1_4.setText("");
        box1_5.setText("5");
        box1_6.setText("");
        box1_7.setText("4");
        box1_8.setText("");
        box1_9.setText("");
        
        box2_1.setText("8");
        box2_2.setText("");
        box2_3.setText("");
        box2_4.setText("");
        box2_5.setText("7");
        box2_6.setText("");
        box2_7.setText("");
        box2_8.setText("");
        box2_9.setText("");
        
        box3_1.setText("");
        box3_2.setText("");
        box3_3.setText("1");
        box3_4.setText("");
        box3_5.setText("2");
        box3_6.setText("");
        box3_7.setText("3");
        box3_8.setText("");
        box3_9.setText("");
        
        box4_1.setText("6");
        box4_2.setText("");
        box4_3.setText("");
        box4_4.setText("");
        box4_5.setText("2");
        box4_6.setText("");
        box4_7.setText("");
        box4_8.setText("");
        box4_9.setText("");
        
        box5_1.setText("");
        box5_2.setText("");
        box5_3.setText("4");
        box5_4.setText("");
        box5_5.setText("5");
        box5_6.setText("");
        box5_7.setText("6");
        box5_8.setText("");
        box5_9.setText("");
        
        box6_1.setText("");
        box6_2.setText("");
        box6_3.setText("");
        box6_4.setText("");
        box6_5.setText("8");
        box6_6.setText("");
        box6_7.setText("");
        box6_8.setText("");
        box6_9.setText("5");
        
        box7_1.setText("");
        box7_2.setText("");
        box7_3.setText("7");
        box7_4.setText("");
        box7_5.setText("8");
        box7_6.setText("");
        box7_7.setText("9");
        box7_8.setText("");
        box7_9.setText("");
        
        box8_1.setText("");
        box8_2.setText("");
        box8_3.setText("");
        box8_4.setText("");
        box8_5.setText("4");
        box8_6.setText("");
        box8_7.setText("");
        box8_8.setText("");
        box8_9.setText("7");
        
        box9_1.setText("");
        box9_2.setText("");
        box9_3.setText("9");
        box9_4.setText("");
        box9_5.setText("5");
        box9_6.setText("");
        box9_7.setText("4");
        box9_8.setText("");
        box9_9.setText("");
    }
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SolverGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SolverGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SolverGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SolverGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SolverGUI().setVisible(true);
            }
        });
    }
    
    /**
     * Creates new form SolverGUI
     */
    public SolverGUI() {
        initComponents();
    }
	
	//@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        box1_1 = new javax.swing.JTextField();
        box1_3 = new javax.swing.JTextField();
        box1_2 = new javax.swing.JTextField();
        box2_1 = new javax.swing.JTextField();
        box1_6 = new javax.swing.JTextField();
        box1_5 = new javax.swing.JTextField();
        box1_7 = new javax.swing.JTextField();
        box1_9 = new javax.swing.JTextField();
        box1_8 = new javax.swing.JTextField();
        box2_4 = new javax.swing.JTextField();
        box2_6 = new javax.swing.JTextField();
        box2_5 = new javax.swing.JTextField();
        box2_7 = new javax.swing.JTextField();
        box2_9 = new javax.swing.JTextField();
        box2_8 = new javax.swing.JTextField();
        box1_4 = new javax.swing.JTextField();
        box2_3 = new javax.swing.JTextField();
        box2_2 = new javax.swing.JTextField();
        box3_4 = new javax.swing.JTextField();
        box3_6 = new javax.swing.JTextField();
        box3_5 = new javax.swing.JTextField();
        box3_7 = new javax.swing.JTextField();
        box3_9 = new javax.swing.JTextField();
        box3_8 = new javax.swing.JTextField();
        box3_1 = new javax.swing.JTextField();
        box3_3 = new javax.swing.JTextField();
        box3_2 = new javax.swing.JTextField();
        box4_4 = new javax.swing.JTextField();
        box4_6 = new javax.swing.JTextField();
        box4_5 = new javax.swing.JTextField();
        box4_7 = new javax.swing.JTextField();
        box4_9 = new javax.swing.JTextField();
        box4_8 = new javax.swing.JTextField();
        box5_4 = new javax.swing.JTextField();
        box4_1 = new javax.swing.JTextField();
        box6_4 = new javax.swing.JTextField();
        box6_6 = new javax.swing.JTextField();
        box6_5 = new javax.swing.JTextField();
        box6_7 = new javax.swing.JTextField();
        box6_9 = new javax.swing.JTextField();
        box6_8 = new javax.swing.JTextField();
        box6_1 = new javax.swing.JTextField();
        box6_3 = new javax.swing.JTextField();
        box6_2 = new javax.swing.JTextField();
        box5_6 = new javax.swing.JTextField();
        box5_5 = new javax.swing.JTextField();
        box5_7 = new javax.swing.JTextField();
        box5_9 = new javax.swing.JTextField();
        box5_8 = new javax.swing.JTextField();
        box5_1 = new javax.swing.JTextField();
        box5_3 = new javax.swing.JTextField();
        box5_2 = new javax.swing.JTextField();
        box4_3 = new javax.swing.JTextField();
        box4_2 = new javax.swing.JTextField();
        box7_4 = new javax.swing.JTextField();
        box7_6 = new javax.swing.JTextField();
        box7_5 = new javax.swing.JTextField();
        box7_7 = new javax.swing.JTextField();
        box7_9 = new javax.swing.JTextField();
        box7_8 = new javax.swing.JTextField();
        box8_4 = new javax.swing.JTextField();
        box7_1 = new javax.swing.JTextField();
        box9_4 = new javax.swing.JTextField();
        box9_6 = new javax.swing.JTextField();
        box9_5 = new javax.swing.JTextField();
        box9_7 = new javax.swing.JTextField();
        box9_9 = new javax.swing.JTextField();
        box9_8 = new javax.swing.JTextField();
        box9_1 = new javax.swing.JTextField();
        box9_3 = new javax.swing.JTextField();
        box9_2 = new javax.swing.JTextField();
        box8_6 = new javax.swing.JTextField();
        box8_5 = new javax.swing.JTextField();
        box8_7 = new javax.swing.JTextField();
        box8_9 = new javax.swing.JTextField();
        box8_8 = new javax.swing.JTextField();
        box8_1 = new javax.swing.JTextField();
        box8_3 = new javax.swing.JTextField();
        box8_2 = new javax.swing.JTextField();
        box7_3 = new javax.swing.JTextField();
        box7_2 = new javax.swing.JTextField();
        solve = new javax.swing.JButton();
        ex1 = new javax.swing.JButton();
        ex2 = new javax.swing.JButton();
        ex3 = new javax.swing.JButton();
        clear = new javax.swing.JButton();
        steps = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        box1_1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box1_1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        box1_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                box1_1ActionPerformed(evt);
            }
        });

        box1_3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box1_3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        box1_3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                box1_3ActionPerformed(evt);
            }
        });

        box1_2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box1_2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box2_1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box2_1.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box1_6.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box1_6.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box1_5.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box1_5.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box1_7.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box1_7.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box1_9.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box1_9.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box1_8.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box1_8.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box2_4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box2_4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box2_6.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box2_6.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box2_5.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box2_5.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box2_7.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box2_7.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box2_9.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box2_9.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box2_8.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box2_8.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box1_4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box1_4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box2_3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box2_3.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box2_2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box2_2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box3_4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box3_4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box3_6.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box3_6.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box3_5.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box3_5.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box3_7.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box3_7.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box3_9.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box3_9.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box3_8.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box3_8.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box3_1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box3_1.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box3_3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box3_3.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box3_2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box3_2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box4_4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box4_4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box4_6.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box4_6.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box4_5.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box4_5.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box4_7.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box4_7.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box4_9.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box4_9.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box4_8.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box4_8.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box5_4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box5_4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box4_1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box4_1.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box6_4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box6_4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box6_6.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box6_6.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box6_5.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box6_5.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box6_7.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box6_7.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box6_9.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box6_9.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box6_8.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box6_8.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box6_1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box6_1.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box6_3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box6_3.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box6_2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box6_2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box5_6.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box5_6.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box5_5.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box5_5.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box5_7.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box5_7.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box5_9.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box5_9.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box5_8.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box5_8.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box5_1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box5_1.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box5_3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box5_3.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box5_2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box5_2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box4_3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box4_3.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box4_2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box4_2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box7_4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box7_4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box7_6.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box7_6.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box7_5.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box7_5.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box7_7.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box7_7.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box7_9.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box7_9.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box7_8.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box7_8.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box8_4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box8_4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box7_1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box7_1.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box9_4.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box9_4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box9_6.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box9_6.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box9_5.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box9_5.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box9_7.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box9_7.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box9_9.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box9_9.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box9_8.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box9_8.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box9_1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box9_1.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box9_3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box9_3.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box9_2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box9_2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box8_6.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box8_6.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box8_5.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box8_5.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box8_7.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box8_7.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box8_9.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box8_9.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box8_8.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box8_8.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box8_1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box8_1.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box8_3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box8_3.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box8_2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box8_2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box7_3.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box7_3.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        box7_2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        box7_2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        solve.setText("Solve!");
        solve.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                solveActionPerformed(evt);
            }
        });

        ex1.setText("Example 1");
        ex1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ex1ActionPerformed(evt);
            }
        });

        ex2.setText("Example 2");
        ex2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ex2ActionPerformed(evt);
            }
        });

        ex3.setText("Example 3");
        ex3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ex3ActionPerformed(evt);
            }
        });

        clear.setText("Clear");
        clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearActionPerformed(evt);
            }
        });

        steps.setText("Show Steps");
        steps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepsActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("A");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("B");

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("I");

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("H");

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("G");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("F");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("E");

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("D");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("C");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("1");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("2");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("3");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("4");

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("5");

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("7");

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("6");

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("8");

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("9");

        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box4_1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box4_2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box4_3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box4_4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box4_5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box4_6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box4_7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box4_8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box4_9, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box5_1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box5_2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box5_3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box5_4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box5_5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box5_6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box5_7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box5_8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box5_9, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box6_1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box6_2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box6_3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box6_7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box6_8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box6_9, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box6_4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box6_5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box6_6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box1_1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box1_2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box1_3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box1_4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box1_5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box1_6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box1_7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box1_8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box1_9, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box2_1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box2_2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box2_3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box2_4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box2_5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box2_6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box2_7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box2_8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box2_9, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box3_1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box3_2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box3_3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box3_4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box3_5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box3_6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box3_7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box3_8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box3_9, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box7_1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box7_2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box7_3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box7_4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box7_5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box7_6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box7_7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box7_8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box7_9, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box8_1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box8_2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box8_3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box8_4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box8_5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box8_6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box8_7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box8_8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box8_9, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box9_1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box9_2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box9_3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box9_4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box9_5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box9_6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(box9_7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box9_8, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(box9_9, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(45, 45, 45)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ex1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(solve, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ex2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ex3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(clear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(steps))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(box3_1)
                            .addComponent(box3_2)
                            .addComponent(box3_3)
                            .addComponent(ex1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(box3_4)
                            .addComponent(box3_5)
                            .addComponent(box3_6)
                            .addComponent(ex2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(box3_7)
                            .addComponent(box3_8)
                            .addComponent(box3_9)
                            .addComponent(ex3)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box1_1)
                                    .addComponent(box1_2)
                                    .addComponent(box1_3)
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box1_5)
                                    .addComponent(box1_6)
                                    .addComponent(box1_4)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box1_7)
                                    .addComponent(box1_8)
                                    .addComponent(box1_9)
                                    .addComponent(jLabel9)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box2_2)
                                    .addComponent(box2_3)
                                    .addComponent(box2_1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box2_4)
                                    .addComponent(box2_5)
                                    .addComponent(box2_6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box2_7)
                                    .addComponent(box2_8)
                                    .addComponent(box2_9))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box4_1)
                                    .addComponent(box4_2)
                                    .addComponent(box4_3)
                                    .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box4_4)
                                    .addComponent(box4_5)
                                    .addComponent(box4_6)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box4_7)
                                    .addComponent(box4_8)
                                    .addComponent(box4_9)
                                    .addComponent(jLabel6)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box5_1)
                                    .addComponent(box5_2)
                                    .addComponent(box5_3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box5_4)
                                    .addComponent(box5_5)
                                    .addComponent(box5_6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box5_7)
                                    .addComponent(box5_8)
                                    .addComponent(box5_9)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box6_1)
                                    .addComponent(box6_2)
                                    .addComponent(box6_3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box6_4)
                                    .addComponent(box6_5)
                                    .addComponent(box6_6)
                                    .addComponent(solve))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box6_7)
                                    .addComponent(box6_8)
                                    .addComponent(box6_9))))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box7_1)
                                    .addComponent(box7_2)
                                    .addComponent(box7_3)
                                    .addComponent(jLabel5))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box7_4)
                                    .addComponent(box7_5)
                                    .addComponent(box7_6)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box7_7)
                                    .addComponent(box7_8)
                                    .addComponent(box7_9)
                                    .addComponent(jLabel3)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box8_1)
                                    .addComponent(box8_2)
                                    .addComponent(box8_3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box8_4)
                                    .addComponent(box8_5)
                                    .addComponent(box8_6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box8_7)
                                    .addComponent(box8_8)
                                    .addComponent(box8_9)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box9_1)
                                    .addComponent(box9_2)
                                    .addComponent(box9_3)
                                    .addComponent(steps))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box9_4)
                                    .addComponent(box9_5)
                                    .addComponent(box9_6)
                                    .addComponent(clear))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(box9_7)
                                    .addComponent(box9_8)
                                    .addComponent(box9_9))))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(24, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>  

	//solves puzzle
    private void solveActionPerformed(java.awt.event.ActionEvent evt) { 
        makeBlack();
        solveOk = true;
        solveComplete = false;
        solveSteps = "";
        stepCount = 1;
        int cycleCount = 0;
        makeMag();
        fillBoard();
        findPos();
        while(!solveComplete && cycleCount < 20) {
            solveBoard();
            cycleCount++;
            System.out.println(cycleCount + " " + solveComplete);
        }
        if(cycleCount >= 20) {
        	solveSteps = "Too hard!";
        	jTextArea1.setText(solveSteps);
        }
        else if (solveOk == false) {
        	jTextArea1.setText(solveSteps);
        }
        else {
        	writeBoard();
        }
    }     
    	
    //fills board with example 1
    private void ex1ActionPerformed(java.awt.event.ActionEvent evt) {       
    	clearBoard();                             
        fillEx1();
    }                                   
	
	//fills board with example 2
    private void ex2ActionPerformed(java.awt.event.ActionEvent evt) {    
    	clearBoard();                                
        fillEx2();
    }                                   

	//fills board with example 3
    private void ex3ActionPerformed(java.awt.event.ActionEvent evt) {                                    
        clearBoard();
        fillEx3();
    }                                   
	
	//clears board
    private void clearActionPerformed(java.awt.event.ActionEvent evt) {                                      
        clearBoard();
    }                      

    private void box1_1ActionPerformed(java.awt.event.ActionEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void box1_3ActionPerformed(java.awt.event.ActionEvent evt) {                                       
        // TODO add your handling code here:
    }            
    
    //displays steps in order	
    private void stepsActionPerformed(java.awt.event.ActionEvent evt) {                                      
        jTextArea1.setText(solveSteps);
    } 

    // Variables declaration - do not modify                     
    private javax.swing.JTextField box1_1;
    private javax.swing.JTextField box1_2;
    private javax.swing.JTextField box1_3;
    private javax.swing.JTextField box1_4;
    private javax.swing.JTextField box1_5;
    private javax.swing.JTextField box1_6;
    private javax.swing.JTextField box1_7;
    private javax.swing.JTextField box1_8;
    private javax.swing.JTextField box1_9;
    private javax.swing.JTextField box2_1;
    private javax.swing.JTextField box2_2;
    private javax.swing.JTextField box2_3;
    private javax.swing.JTextField box2_4;
    private javax.swing.JTextField box2_5;
    private javax.swing.JTextField box2_6;
    private javax.swing.JTextField box2_7;
    private javax.swing.JTextField box2_8;
    private javax.swing.JTextField box2_9;
    private javax.swing.JTextField box3_1;
    private javax.swing.JTextField box3_2;
    private javax.swing.JTextField box3_3;
    private javax.swing.JTextField box3_4;
    private javax.swing.JTextField box3_5;
    private javax.swing.JTextField box3_6;
    private javax.swing.JTextField box3_7;
    private javax.swing.JTextField box3_8;
    private javax.swing.JTextField box3_9;
    private javax.swing.JTextField box4_1;
    private javax.swing.JTextField box4_2;
    private javax.swing.JTextField box4_3;
    private javax.swing.JTextField box4_4;
    private javax.swing.JTextField box4_5;
    private javax.swing.JTextField box4_6;
    private javax.swing.JTextField box4_7;
    private javax.swing.JTextField box4_8;
    private javax.swing.JTextField box4_9;
    private javax.swing.JTextField box5_1;
    private javax.swing.JTextField box5_2;
    private javax.swing.JTextField box5_3;
    private javax.swing.JTextField box5_4;
    private javax.swing.JTextField box5_5;
    private javax.swing.JTextField box5_6;
    private javax.swing.JTextField box5_7;
    private javax.swing.JTextField box5_8;
    private javax.swing.JTextField box5_9;
    private javax.swing.JTextField box6_1;
    private javax.swing.JTextField box6_2;
    private javax.swing.JTextField box6_3;
    private javax.swing.JTextField box6_4;
    private javax.swing.JTextField box6_5;
    private javax.swing.JTextField box6_6;
    private javax.swing.JTextField box6_7;
    private javax.swing.JTextField box6_8;
    private javax.swing.JTextField box6_9;
    private javax.swing.JTextField box7_1;
    private javax.swing.JTextField box7_2;
    private javax.swing.JTextField box7_3;
    private javax.swing.JTextField box7_4;
    private javax.swing.JTextField box7_5;
    private javax.swing.JTextField box7_6;
    private javax.swing.JTextField box7_7;
    private javax.swing.JTextField box7_8;
    private javax.swing.JTextField box7_9;
    private javax.swing.JTextField box8_1;
    private javax.swing.JTextField box8_2;
    private javax.swing.JTextField box8_3;
    private javax.swing.JTextField box8_4;
    private javax.swing.JTextField box8_5;
    private javax.swing.JTextField box8_6;
    private javax.swing.JTextField box8_7;
    private javax.swing.JTextField box8_8;
    private javax.swing.JTextField box8_9;
    private javax.swing.JTextField box9_1;
    private javax.swing.JTextField box9_2;
    private javax.swing.JTextField box9_3;
    private javax.swing.JTextField box9_4;
    private javax.swing.JTextField box9_5;
    private javax.swing.JTextField box9_6;
    private javax.swing.JTextField box9_7;
    private javax.swing.JTextField box9_8;
    private javax.swing.JTextField box9_9;
    private javax.swing.JButton solve;
    private javax.swing.JButton clear;
    private javax.swing.JButton ex1;
    private javax.swing.JButton ex2;
    private javax.swing.JButton ex3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton steps;
    // End of variables declaration                   
}
