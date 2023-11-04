package com.mangst.gameoflife;


public class Grid {

	private boolean grid[][];


	public Grid(int rows, int cols) {
		grid = new boolean[rows + 2][cols + 2]; //edge cells will always be dead to simplify calculations
	}


	public boolean isAlive(int row, int col) {
		return grid[row + 1][col + 1];
	}


	public void setAlive(int row, int col, boolean alive) {
		grid[row + 1][col + 1] = alive;
	}


	public int getAliveSurrounding(int row, int col) {
		int count = 0;

		if (isAlive(row - 1, col - 1)) count++;
		if (isAlive(row, col - 1)) count++;
		if (isAlive(row + 1, col - 1)) count++;

		if (isAlive(row - 1, col)) count++;
		if (isAlive(row + 1, col)) count++;

		if (isAlive(row - 1, col + 1)) count++;
		if (isAlive(row, col + 1)) count++;
		if (isAlive(row + 1, col + 1)) count++;

		return count;
	}

	public int getRows() {
		return grid.length - 2;
	}


	public int getCols() {
		return grid[0].length - 2;
	}

	@Override
	public String toString() {
		return toStringSerial();
	}


	public String toStringSerial() {
		final String newline = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder(getRows() * (getCols() + newline.length()));

		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getCols(); j++) {
				char c = isAlive(i, j) ? 'x' : ' ';
				sb.append(c);
			}
			sb.append(newline);
		}

		return sb.toString();
	}


	public String toStringConcurrent() {
		int threads = Runtime.getRuntime().availableProcessors();
		
		//decompose data (split the task up into threads)
		ToStringThread[] toStringThreads = new ToStringThread[threads];
		for (int i = 0; i < threads; i++) {
			toStringThreads[i] = new ToStringThread(i, threads);
		}

		//start threads
		for (ToStringThread t : toStringThreads) {
			t.start();
		}

		//wait for threads to finish
		for (ToStringThread t : toStringThreads) {
			try {
				t.join();
			} catch (InterruptedException e) {
			}
		}

		//recombine results
		final String newline = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder(getRows() * (getCols() + newline.length()));
		for (int i = 0; i < getRows(); i++) {
			int mod = i % threads;
			int div = i / threads;
			sb.append(toStringThreads[mod].rows[div]);
			sb.append(newline);
		}

		return sb.toString();
	}


	private class ToStringThread extends Thread {

		private int num;


		private int max;

		public String[] rows;


		public ToStringThread(int num, int max) {
			this.num = num;
			this.max = max;
			this.rows = new String[(int) Math.ceil(getRows() / (double) max)];
		}

		@Override
		public void run() {
			int row = num;
			int i = 0;
			char rowString[] = new char[getCols()];
			while (row < getRows()) {
				for (int j = 0; j < getCols(); j++) {
					rowString[j] = Grid.this.isAlive(row, j) ? 'x' : ' ';
				}
				rows[i++] = new String(rowString);

				row += max;
			}
		}
	}
}
