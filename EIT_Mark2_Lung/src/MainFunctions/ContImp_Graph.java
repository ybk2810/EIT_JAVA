package MainFunctions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;




public class ContImp_Graph extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Control EIT_Control_Dlg;     
	public String DirectoryPath = System.getProperty("user.dir");


	ContImp_Graph(Control control) {
    	super("Contact Impedance Graph");
		ContImp_Graph.setEIT_Control_Dlg(control);

    	/*this.getContentPane().setLayout(new GridLayout(1, 0));
        this.getContentPane().add(createPanel()); // number of panel
        Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
        Dimension windowSize = new Dimension(getPreferredSize());
        int wdwLeft = screenSize.width-200;
        int wdwTop = screenSize.height -200;
        this.pack();   
        this.setLocation(wdwLeft, wdwTop);
       
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(DirectoryPath+"\\Images\\logo.png"));
*/

        this.setIconImage(Toolkit.getDefaultToolkit().getImage(DirectoryPath+"\\Images\\logo.png"));

        this.add(createPanel(), BorderLayout.CENTER);
        Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
        int wdwLeft = screenSize.width-1255;
        int wdwTop = screenSize.height -1070;
        this.pack();   
        this.setLocation(wdwLeft, wdwTop);
    }
	
	 private static JPanel  createPanel() {
	    	JPanel p = new JPanel(new BorderLayout());	 
	    	DefaultCategoryDataset gData = new DefaultCategoryDataset();
	    	 //update(56, 20);
	         JFreeChart chart = ChartFactory.createBarChart("Contact Impedance", // chart title
	                     "Channels", // domain axis label
	                     "Zc [\u03A9]", // range axis label
	                     gData, // data
	                     PlotOrientation.VERTICAL, // orientation
	                     false, // include legend
	                     false, // tooltips?
	                     false // URLs?
	                     );
	              
	          chart.setBackgroundPaint(new Color(0xEEEEEE));
	          Plot plot2 = chart.getPlot();
	          plot2.setBackgroundPaint(Color.white);
	          
	          CategoryPlot categoryPlot = chart.getCategoryPlot();
	          BarRenderer br = (BarRenderer) categoryPlot.getRenderer();
	          br.setItemMargin(-8); // set maximum width to 35% of chart

	          
	          ChartPanel chartPanel = new ChartPanel(chart);
	          chartPanel.setPreferredSize(new Dimension(800, 400));
	             
	          final int INTERVAL = 500;  // update every one second
	          Timer timer = new Timer(INTERVAL,new UpdateAction(gData) );
	          timer.start();
	             
	             
	          p.add(chartPanel,BorderLayout.CENTER);
	          return p;
	            
	 }
	 private static class UpdateAction extends AbstractAction {

		 DefaultCategoryDataset gData1;
	        public UpdateAction(DefaultCategoryDataset gData) {
			// TODO Auto-generated constructor stub
	        	this.gData1 = gData;
		}

			@Override
	        public void actionPerformed(ActionEvent e) {
				double[] Con_Imp = new double[Definition.TOTAL_CH];
				//double[] Con_Imp ={1,2,3,4,5,6,7,8,9,1,2,3,4,5,6,7};
				gData1.clear();
		   		for (int i=0 ; i< Definition.TOTAL_CH;i++)
				{
		   			Con_Imp[i]= getEIT_Control_Dlg().EIT_Control.Zc[i];
	            	gData1.setValue(Con_Imp[i], "Ch "+(i+1),(i+1)+"");

				}
			
	        }
	  
	 }

    
    public static Control getEIT_Control_Dlg() {
		return EIT_Control_Dlg;
	}

	public static void setEIT_Control_Dlg(Control eIT_Control_Dlg) {
		EIT_Control_Dlg = eIT_Control_Dlg;
	}

 /*   public static void main(String args[]) {
        new ContImp_Graph();
    }*/
}