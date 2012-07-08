package cs412.dinghyprop.simulator;

import javax.swing.JFileChooser;
import java.io.*;

public class SimulatorFileTest{
	public static void main(String[] args){
	
		JFileChooser fc = new JFileChooser();
		
		fc.showOpenDialog(null);
		File selFile = fc.getSelectedFile();
		SimulatorFile test = new SimulatorFile(selFile);
	}
}
