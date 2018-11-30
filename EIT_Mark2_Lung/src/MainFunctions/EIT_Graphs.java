package MainFunctions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

/**
 * An example to show how we can create a dynamic chart.
*/
public class EIT_Graphs extends ApplicationFrame  {

    /** The time series data. */
    private TimeSeries series;
   
    Scanner fin= null;
    static Control EIT_Control_Dlg; 
    	  
       
   
    /**
     * Constructs a new dynamic chart application.
     *
     * @param title  the frame title.
     */
    public EIT_Graphs(final String title,Control control) {
    	

        super(title);
        this.EIT_Control_Dlg = control;
        this.series = new TimeSeries("Reciprocity Data", Millisecond.class);
       
        final TimeSeriesCollection dataset = new TimeSeriesCollection(this.series);
        final JFreeChart chart = createChart(dataset);
       
       
       
        //Sets background color of chart
        chart.setBackgroundPaint(Color.LIGHT_GRAY);
       
        //Created JPanel to show graph on screen
      
        final JPanel content = new JPanel(new BorderLayout());
       
        //Created Chartpanel for chart area
        final ChartPanel chartPanel = new ChartPanel(chart);
       
        //Added chartpanel to main panel
        content.add(chartPanel);
        
        //Sets the size of whole window (JPanel)
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 500));
        chartPanel.setRangeZoomable(true);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.restoreAutoBounds();
        //Puts the whole content on a Frame
        setContentPane(content);
     
        
    /*    try {
			 fin  = new Scanner(new File("C://Users//Ghazal//Desktop//test2.txt"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/

        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
     
    }

    /**
     * Creates a sample chart.
     *
     * @param dataset  the dataset.
     *
     * @return A sample chart.
     */
    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
            "Reciprocity Error",
            "Time",
            "Value",
            dataset,
            true,
            true,
            false
        );
       
        final XYPlot plot = result.getXYPlot();
       
        plot.setBackgroundPaint(new Color(0xffffe0));
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.lightGray);
                
        ValueAxis xaxis = plot.getDomainAxis();
        xaxis.setAutoRange(true);
       
        //Domain axis would show data of 60 seconds for a time
        xaxis.setFixedAutoRange(30000.0);  // 60 seconds
        xaxis.setVerticalTickLabels(true);
       
        ValueAxis yaxis = plot.getRangeAxis();
        yaxis.setRange(-300.0, 110.0);
     //   yaxis.setAutoRange(true);
       
        return result;
    }
    /**
     * Generates an random entry for a particular call made by time for every 1/4th of a second.
     *
     * @param e  the action event.
     */
  
    
    public void GetRecipError()
    { synchronized (this)
		{
    	double value;
    	EIT_Control_Dlg.EIT_Control.m_bMessageGraphReconFinishFlag= false;
		//for(int i =0 ; i< 120 ; i++)
		{
			value = EIT_Control_Dlg.EIT_Control.Rec_Error_val;
				//System.out.println(value);
		         final Millisecond now = new Millisecond();
		         this.series.addOrUpdate(new Millisecond(), value);
		         //this.series.add(new Millisecond(), factor);
		}
		EIT_Control_Dlg.EIT_Control.m_bMessageGraphReconFinishFlag= true;
		}
    }

    /**
     * Starting point for the dynamic graph application.
     *
     * @param args  ignored.
     */
  

}  


