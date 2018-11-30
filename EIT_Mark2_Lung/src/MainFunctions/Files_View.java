package MainFunctions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.dnd.Autoscroll;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;


public class Files_View extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	JTextArea textArea_Pr;
	private String str;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Files_View frame = new Files_View();
					frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public Files_View() throws IOException {
		setTitle("EIT Mark 2.5");
		setBounds(100, 100, 990, 447);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		Control control_wind = new Control();
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		scrollPane.setBounds(10, 11, 458, 387);
		contentPane.add(scrollPane);
		
		JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setTabSize(5);
		
		JLabel lblNewLabel = new JLabel("Script File");
		lblNewLabel.setBackground(Color.WHITE);
		scrollPane.setColumnHeaderView(lblNewLabel);
	
		
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setViewportBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		scrollPane_1.setBounds(494, 11, 458, 387);
		contentPane.add(scrollPane_1);
		
		textArea_Pr = new JTextArea();
		textArea_Pr.setTabSize(5);
		textArea_Pr.setLineWrap(true);
		textArea_Pr.setEditable(false);
		textArea_Pr.setCaretPosition(0);
		scrollPane_1.setViewportView(textArea_Pr);
		
		JLabel lblProjectionFile = new JLabel("Projection File");
		lblProjectionFile.setBackground(Color.WHITE);
		scrollPane_1.setColumnHeaderView(lblProjectionFile);
		
		
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(control_wind.getString()))) {
            String line = null;
            String line2 = null;
            //String ProjPath ;
            while ((line = reader.readLine()) != null) {
            	 Scanner sc = new Scanner(line);
            	 if (sc.hasNext())
            	 {
            		 if(sc.next().equals("#include"))
                	 {
            			 String ProjectionFilePath =System.getProperty("user.dir")+"/ScriptFile/";
                		 //ProjPath = sc.next().toString();
                		 StringBuilder ProjPathStr =  new StringBuilder();
                		 ProjPathStr.append(sc.next().toString());
                		 int FirstIndex = ProjPathStr.indexOf("\"");
                		 ProjPathStr.deleteCharAt(FirstIndex);
						 int EndIndex = ProjPathStr.indexOf("\"");
						 ProjPathStr.deleteCharAt(EndIndex);
					
						 ProjPathStr.replace(ProjPathStr.indexOf("\\"),ProjPathStr.indexOf("\\")+1,"/");
                		 BufferedReader reader2 = Files.newBufferedReader(Paths.get(ProjectionFilePath+ProjPathStr)) ;
                		 while ((line2 = reader2.readLine()) != null) 
                	     {
                			 textArea_Pr.append(line2 + "\n");
                	     }
                		 
                	 } 

            	 }
            	 sc.close();
            	
                textArea.append(line + "\n");
                
            }
        } catch (IOException x) {
        	 JOptionPane.showMessageDialog(null, "could not open file", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
            System.err.format("IOException: %s%n", x);
        }
       
		textArea.setCaretPosition(0);
		textArea_Pr.setCaretPosition(0);
		
		
	}
}

