package MainFunctions;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;





public class EIT_Graph extends JFrame {
	
	 final GraphPanel gPanel;
	 static Control EIT_Control_Dlg; 
	 
	public String DirectoryPath = System.getProperty("user.dir");

	 /** The time series data. */
    private TimeSeries series;
 
	EIT_Graph(Control control) {
		super("Graph");
       
		this.EIT_Control_Dlg = control;
        this.series = new TimeSeries("Reciprocity Error", Millisecond.class);
       
        this.setIconImage(Toolkit.getDefaultToolkit().getImage(DirectoryPath+"\\Images\\logo.png"));

        final TimeSeriesCollection dataset = new TimeSeriesCollection(this.series);
        final JFreeChart Recchart = createChart(dataset);
      //Sets background color of chart
        Recchart.setBackgroundPaint(new Color(0xEEEEEE));
       
        //Created JPanel to show graph on screen
        final JPanel content = new JPanel(new BorderLayout());
       
        //Created Chartpanel for chart area
        final ChartPanel chartPanel = new ChartPanel(Recchart);
        chartPanel.setBackground(new Color(0xEEEEEE));
        chartPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Reciprocity Error Graph", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        
        FlowLayout flowLayout = (FlowLayout) chartPanel.getLayout();
        
       
        //Added chartpanel to main panel
        content.add(chartPanel);
        
        //Sets the size of whole window (JPanel)
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 400));
        chartPanel.setRangeZoomable(true);
        chartPanel.setMouseWheelEnabled(true);
        //chartPanel.restoreAutoBounds();
        
        gPanel = new GraphPanel();
        gPanel.setBorder(new TitledBorder(null, "Contact Impedance Graph", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        gPanel.setBackground(new Color(0xEEEEEE));
        gPanel.create();
        //gPanel.setBackground(Color.LIGHT_GRAY);
		
	
      
        //getContentPane().add(gPanel, BorderLayout.CENTER);  //Contact
        getContentPane().add(content, BorderLayout.NORTH); //recip
    
        Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
        int wdwLeft = screenSize.width-1255;
        int wdwTop = screenSize.height -600;
        this.pack();   
        this.setLocation(wdwLeft, wdwTop);
    }

    private static class GraphPanel extends JPanel 
    {
        private DefaultCategoryDataset gData = new DefaultCategoryDataset();

        void create() {
            //update();
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
            //plot2.setBackgroundPaint(new Color(0xffffe0));
            plot2.setBackgroundPaint(Color.white);

           // plot2.setOutlinePaint(Color.lightGray);
            
            CategoryPlot categoryPlot = chart.getCategoryPlot();
            
            
            BarRenderer br = (BarRenderer) categoryPlot.getRenderer();
            br.setItemMargin(-8); // set maximum width to 35% of chart

            
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(800, 400));
           
            this.add(chartPanel);
     }
        private void update() 
        {
        	EIT_Control_Dlg.EIT_Control.m_bMessageGraphContactImpedanceFinishFlag= false;
            gData.clear();
            for (int i =0 ;i<Definition.MAX_CH;i++)
            {	//int i =0;
            	
            	double Zc = EIT_Control_Dlg.EIT_Control.Zc[i];  //because here start from count 1 
         //   	System.out.println(Zc+ " Ch "+(i+1));
            	gData.setValue(Zc, "Ch "+(i+1),(i+1)+"");
            	
            }
            EIT_Control_Dlg.EIT_Control.m_bMessageGraphContactImpedanceFinishFlag = true ;
        }
    }
    public void GetContactImpedance() 
    {
    	synchronized (this) {
    	 	
            gPanel.update();
            
		}
   
    }
    
    private JFreeChart createChart(final XYDataset dataset) {
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
            "Reciprocity Error",
            "Time",
            "RE (%)",
            dataset,
            true,
            true,
            false
        );
       
        final XYPlot plot = result.getXYPlot();
       
        //plot.setBackgroundPaint(new Color(0xffffe0));
        plot.setBackgroundPaint(Color.white);

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
     // yaxis.setRange(-300.0, 110.0);
        yaxis.setAutoRange(true);
       
        return result;
      
    }
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
 
}
