package MainFunctions;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @see http://stackoverflow.com/a/20243624/230513
 * @see http://stackoverflow.com/q/11870416/230513
 */
public class MultiSignalGraph extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static XYPlot plot1,plot2,plot3,plot4,plot5,plot6,plot7,plot8;
    private static Control EIT_Control_Dlg;     
	public String DirectoryPath = System.getProperty("user.dir");



    MultiSignalGraph(Control control) {
    	super("Bio-Signals");
		MultiSignalGraph.setEIT_Control_Dlg(control);
       // JFrame frame = new JFrame("U Wave Plot ");
       // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(new GridLayout(1, 0));
        this.getContentPane().add(createPanel()); // number of panel
        Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
        Dimension windowSize = new Dimension(getPreferredSize());
        int wdwLeft = screenSize.width-435;
        int wdwTop = screenSize.height -1070;
        this.pack();   
        this.setLocation(wdwLeft, wdwTop);
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(DirectoryPath+"\\Images\\logo.png"));

        //frame.setVisible(true);
    }

    private static JPanel  createPanel() {
    	JPanel p = new JPanel(new BorderLayout());	 
        XYItemRenderer renderer = new StandardXYItemRenderer();
        
        
         plot1 = new XYPlot(
            generateData(1), null,  new NumberAxis("Mag"), renderer);
        plot2 = new XYPlot(
            generateData(2), null, new NumberAxis("Mag"), renderer);
        plot3 = new XYPlot(
                generateData(3), null, new NumberAxis("Mag"), renderer);
        plot4 = new XYPlot(
                generateData(4), null, new NumberAxis("Mag"), renderer);
        plot5 = new XYPlot(
                generateData(5), null, new NumberAxis("Mag"), renderer);
        plot6 = new XYPlot(
                generateData(6), null, new NumberAxis("Mag"), renderer);
        plot7 = new XYPlot(
                generateData(7), null, new NumberAxis("Mag"), renderer);
        plot8 = new XYPlot(
                generateData(8), null, new NumberAxis("Mag"), renderer);
        
        
        final CombinedDomainXYPlot plot
            = new CombinedDomainXYPlot(new NumberAxis("Channel"));
        plot.add(plot1);
        plot.add(plot2);
        plot.add(plot3);
        plot.add(plot4);
        plot.add(plot5);
        plot.add(plot6);
        plot.add(plot7);
        plot.add(plot8);
     

        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart chart = new JFreeChart(
            "U Wave ", JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        ChartPanel chartPanel = new ChartPanel(chart){

   			private static final long serialVersionUID = 1L;
			@Override
            public Dimension getPreferredSize() {
                return new Dimension(400, 1000);
            }
        };

        /*JPanel controlPanel = new JPanel(); // only for button
        controlPanel.add(new JButton(new UpdateAction(plot, 0)));
        */
       /* controlPanel.add(new JButton(new UpdateAction(plot, 1)));
        controlPanel.add(new JButton(new UpdateAction(plot, 2)));
        controlPanel.add(new JButton(new UpdateAction(plot, 3)));
*/
        
        final int INTERVAL = 500;  // update every one second
        Timer timer = new Timer(INTERVAL,new UpdateAction(plot, 0) );
        timer.start();
        
        
       p.add(chartPanel,BorderLayout.CENTER);
        return p;
    }

    private static class UpdateAction extends AbstractAction {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final XYPlot plot;
	    XYTextAnnotation prev_annotation1 = new XYTextAnnotation(String.valueOf(0), 0, (double) 0);
	    XYTextAnnotation prev_annotation2 = new XYTextAnnotation(String.valueOf(0), 0, (double) 0);
	    XYTextAnnotation prev_annotation3 = new XYTextAnnotation(String.valueOf(0), 0, (double) 0);
	    XYTextAnnotation prev_annotation4 = new XYTextAnnotation(String.valueOf(0), 0, (double) 0);
	    XYTextAnnotation prev_annotation5 = new XYTextAnnotation(String.valueOf(0), 0, (double) 0);
	    XYTextAnnotation prev_annotation6 = new XYTextAnnotation(String.valueOf(0), 0, (double) 0);
	    XYTextAnnotation prev_annotation7 = new XYTextAnnotation(String.valueOf(0), 0, (double) 0);
	    XYTextAnnotation prev_annotation8 = new XYTextAnnotation(String.valueOf(0), 0, (double) 0);

	    
	    
        public UpdateAction(CombinedDomainXYPlot plot, int i) {
            super("Update plot " + (i + 1));
            this.plot = (XYPlot) plot.getSubplots().get(i);   // can make loop to update all
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            plot1.setDataset(MultiSignalGraph.generateData(1));
            prev_annotation1 = getXYTextAnnotation(plot1,1,prev_annotation1);
  
            
            plot2.setDataset(MultiSignalGraph.generateData(2));
            prev_annotation2 = getXYTextAnnotation(plot2,2,prev_annotation2);

            plot3.setDataset(MultiSignalGraph.generateData(3)); 
            prev_annotation3 = getXYTextAnnotation(plot3,3,prev_annotation3);
  
            plot4.setDataset(MultiSignalGraph.generateData(4));
            prev_annotation4 = getXYTextAnnotation(plot4,4,prev_annotation4);
 
            plot5.setDataset(MultiSignalGraph.generateData(5));
            prev_annotation5 = getXYTextAnnotation(plot5,5,prev_annotation5);

            
            plot6.setDataset(MultiSignalGraph.generateData(6));
            prev_annotation6 = getXYTextAnnotation(plot6,6,prev_annotation6);

            plot7.setDataset(MultiSignalGraph.generateData(7));
            prev_annotation7 = getXYTextAnnotation(plot7,7,prev_annotation7);

            plot8.setDataset(MultiSignalGraph.generateData(8));
            prev_annotation8 = getXYTextAnnotation(plot8,8,prev_annotation8);
 
          
        }
        XYTextAnnotation getXYTextAnnotation(XYPlot plot , int num_dataset,XYTextAnnotation Prev_annotation)
        {
        	Number maximum = DatasetUtilities.findMaximumRangeValue(MultiSignalGraph.generateData(num_dataset));
        	XYTextAnnotation annotation = new XYTextAnnotation(String.valueOf(Math.round((double)maximum)), 1, (double) maximum-3000);
            annotation.setFont(new Font("SansSerif", Font.PLAIN, 11));
            plot.removeAnnotation(Prev_annotation);
            plot.addAnnotation(annotation);
            Prev_annotation = annotation;
            return Prev_annotation;
        }
    }

    private static XYSeriesCollection generateData(int num) {  // test
    	XYSeriesCollection data = null ;        
    	for (int i = 0; i < Definition.TOTAL_CH; i++) {
        		 data = new XYSeriesCollection();
                data.addSeries(generateSeries("Projection " + (i + 1),(num-1)));
        }
        return data;
    }

    private static XYSeries generateSeries(String key,int Proj) {
    /*	int[] Mask ={0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,
    			1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,
    			1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,
    			1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0};
    	
   		double[] Mag = new double[Definition.ALL_DATA];
   		for (int i=0 ; i< Definition.ALL_DATA;i++)
		{
			Mag[i]= getEIT_Control_Dlg().EIT_Control.TempMagnitude[0][i] * Mask[i];
		}

        for (int j = 0; j < Definition.TOTAL_CH; j++) {
        	series.add(j+1, Mag[j+(Proj*16)]);        
        	}*/
        XYSeries series = new XYSeries(key);

        return series;
    }

	public static Control getEIT_Control_Dlg() {
		return EIT_Control_Dlg;
	}

	public static void setEIT_Control_Dlg(Control eIT_Control_Dlg) {
		EIT_Control_Dlg = eIT_Control_Dlg;
	}
}