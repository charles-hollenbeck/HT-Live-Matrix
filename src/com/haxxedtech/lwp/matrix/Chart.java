package com.haxxedtech.lwp.matrix;

public class Chart {
	private String[][] table;
	private int width;
	private int height;
	private final String[] chars = {"0"," ","1"};
	
	public Chart(int width, int height){
		this.height = height;
		this.width = width;
		this.table = new String[width][height];
		generateArr();
	}
	public void reverse(){
		this.table = new String[height][width];
		generateArr();
	}
	public void generateNewRow(){
	   	for(int i =0;i< this.table[0].length;i++)
	   		this.table[0][i] = chars[(int)Math.round(Math.random() * (chars.length -1))];
    }
    public void generateArr(){
	   	for(int r = 0; r < this.table.length; r++)
		   	 for(int c = 0; c < this.table[r].length;c++)
		   		this.table[r][c] = chars[(int)Math.round(Math.random() * (chars.length -1))];
    }
    public void shiftMatrixDown(){
    	String[][] tmp = new String[this.table.length][this.table[0].length];
    	for(int row = 0;row < this.table.length - 1;row++)
    		for(int col = 0;col<this.table[row].length;col++)
    			tmp[row + 1][col] = this.table[row][col];
    	this.table = tmp;
    	generateNewRow();
    }
    public String[][] getTable(){
    	return this.table;
    }
}
