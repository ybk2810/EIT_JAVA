package MainFunctions;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;


public class Bio_Siganl_Graphs extends JFrame {

   
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Scanner fin= null;
	int ch_index;
    private static Control EIT_Control_Dlg;
    JFreeChart tmpchart ;
  
    //JFreeChart[] chart_lst = new JFreeChart[Definition.PEV_Max_Ch];
    ChartPanel[] chart_lst = new ChartPanel[Definition.AirTom_Signal_Num];
    TimeSeries[] series = new TimeSeries[Definition.AirTom_Signal_Num];
    String[] signal_name = new String[Definition.AirTom_Signal_Num];

    //public Timer  graph_timer = new Timer();
    public static Timer timer;
    /**
     * Constructs a new dynamic chart application.
     *
     * @param title  the frame title.
     */
  //  @SuppressWarnings("deprecation")
	public Bio_Siganl_Graphs(final String title,Control control) {
        super(title);
        Bio_Siganl_Graphs.setEIT_Control_Dlg(control);
        
        /// Combobox
        JComboBox<String> comboBox = new JComboBox<String>();
        comboBox.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		 ch_index = comboBox.getSelectedIndex();
        		 
        		// ChartPanel panel = new ChartPanel(createCombinedChart(), true, true, true, false, true);
        	        final JPanel panel = createCombinedChart();
        	        Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
        	        panel.setPreferredSize(new java.awt.Dimension(screenSize.width/4, screenSize.height-100));

        	        
        	        final JPanel content = new JPanel(new BorderLayout());
        	        content.add(panel,BorderLayout.CENTER);

        	        content.add(comboBox,BorderLayout.NORTH);
        	       
        	        
        	        
        	        setContentPane(content);
        	        content.revalidate();
        	        content.repaint();
        	}
        });
        
        signal_name[0] = "X-axis Accelerometer";
        signal_name[1] = "Y-axis Accelerometer";
        signal_name[2] = "Z-axis Accelerometer";
        signal_name[3] = "Temperature";
        signal_name[4] = "X-axis Gyroscope";
        signal_name[5] = "Y-axis Gyroscope";
        signal_name[6] = "Z-axis Gyroscope";
        signal_name[7] = "External trigger";
    	comboBox.addItem("All");
        for(int i =0 ; i< Definition.AirTom_Signal_Num ; i++)
        	comboBox.addItem(signal_name[i]);
        comboBox.setSelectedIndex(0);
        
       // ChartPanel panel = new ChartPanel(createCombinedChart(), true, true, true, false, true);
        final JPanel panel = createCombinedChart();

        Dimension screenSize = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
        panel.setPreferredSize(new java.awt.Dimension(screenSize.width/4, screenSize.height-100));

        
        final JPanel content = new JPanel(new BorderLayout());
        content.add(panel,BorderLayout.CENTER);
        content.add(comboBox,BorderLayout.NORTH);
       
        
        
       /* graph_timer.schedule(new TimerTask() {
        	public void run()  {
        	 
        		getEIT_Control_Dlg().EIT_Control.m_bMessageGraphBio_SignalFinishFlag= false;
        		for(int i =0 ; i< 50 ; i++)
        		{
        			Millisecond time = new Millisecond();
        			series[0].addOrUpdate(time, getEIT_Control_Dlg().EIT_Control.Accelometer_X[i]);
        			series[1].addOrUpdate(time, getEIT_Control_Dlg().EIT_Control.Accelometer_Y[i]);
        			series[2].addOrUpdate(time, getEIT_Control_Dlg().EIT_Control.Accelometer_Z[i]);
        			series[3].addOrUpdate(time, getEIT_Control_Dlg().EIT_Control.Tempreture[i]);
        			series[4].addOrUpdate(time, getEIT_Control_Dlg().EIT_Control.Gyro_X[i]);
        			series[5].addOrUpdate(time,  getEIT_Control_Dlg().EIT_Control.Gyro_Y[i]);
        			series[6].addOrUpdate(time, getEIT_Control_Dlg().EIT_Control.Gyro_Z[i]);
        			series[7].addOrUpdate(time, getEIT_Control_Dlg().EIT_Control.External_trigger[i]);
        		}
        		System.out.println(getEIT_Control_Dlg().EIT_Control.Accelometer_X[2]);
        		getEIT_Control_Dlg().EIT_Control.m_bMessageGraphBio_SignalFinishFlag= true;
        		
        		
        	}
        	},1,500);
      */
        setContentPane(content);
        int wdwLeft = screenSize.width-435;
        int wdwTop = screenSize.height -1040;
        this.pack();   
        this.setLocation(wdwLeft, wdwTop);
        //RefineryUtilities.centerFrameOnScreen(this);
      
    }
  
	 
	//	@SuppressWarnings("deprecation")
		JPanel createCombinedChart()
	    {
   		 	JPanel list_chart_panel = new JPanel();
   		 	list_chart_panel.setLayout(new BoxLayout(list_chart_panel, BoxLayout.Y_AXIS));
	    	for(int i =0 ; i< Definition.AirTom_Signal_Num ; i++)
	        {
	        	 this.series[i] = new TimeSeries(signal_name[i], Millisecond.class);
	        	// this.series[i] = new TimeSeries(signal_name[i]);
	             final TimeSeriesCollection dataset = new TimeSeriesCollection(this.series[i]);
	             final JFreeChart chart = createChart(dataset);
	             chart.getLegend().setVisible(false);
        		 ChartPanel chart_panel = new ChartPanel(chart, true, true, true, false, true);
	             chart_lst[i] = chart_panel;
	        }
	       if(ch_index == 0)
	        {
	        	   for (ChartPanel chart : chart_lst) {
	        		   list_chart_panel.add(chart);
	       		}
	        }
	        else
	        	list_chart_panel.add(chart_lst[ch_index-1]);  // -1  because first index to "all" ch 
	        return list_chart_panel;
	    	
	    }

    /**
     * Creates a sample chart.
     *
     * @param dataset  the dataset.
     *
     * @return A sample chart.
     */
    private JFreeChart createChart(final XYDataset dataset) {
    	 XYPlot plot ;
        final JFreeChart result = ChartFactory.createTimeSeriesChart(
            "",//signals
            "",//time
            "",
            dataset,
            true,
            true,
            false
        );
       
        plot = result.getXYPlot();
        plot.setBackgroundPaint(new Color(0xffffe0));
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.lightGray);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.lightGray);
                
        ValueAxis xaxis = plot.getDomainAxis();
        xaxis.setAutoRange(true);
       
        //Domain axis would show data of 60 seconds for a time
        xaxis.setFixedAutoRange(10000);  // 10 seconds
        xaxis.setVerticalTickLabels(true);
        xaxis.setVisible(false);
       
        ValueAxis yaxis = plot.getRangeAxis();
        //yaxis.setRange(-300.0, 110.0);
        yaxis.setAutoRange(true);
       
        LegendTitle legend = new LegendTitle(plot.getRenderer());
        legend.setItemFont(new java.awt.Font("Dialog",java.awt.Font.BOLD,15));
        legend.setPosition(RectangleEdge.TOP);
        result.addLegend(legend); 
       
       /* LegendTitle lt = new LegendTitle(plot);
        lt.setItemFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 12));
        lt.setBackgroundPaint(new Color(200, 200, 255, 100));
        lt.setFrame(new BlockBorder(Color.white));
        lt.setPosition(RectangleEdge.BOTTOM);
        XYTitleAnnotation ta = new XYTitleAnnotation(0.98, 0.02, lt,RectangleAnchor.BOTTOM_RIGHT);

        ta.setMaxWidth(0.48);
        plot.addAnnotation(ta);*/
        return result;
    }
    /**
     * Generates an random entry for a particular call made by time for every 1/4th of a second.
     *
     * @param e  the action event.
     */
  
   
   /* public void GetBioSignalData()
    {
    	
		{
    	getEIT_Control_Dlg().EIT_Control.m_bMessageGraphBio_SignalFinishFlag= false;
		for(int i =0 ; i< 50 ; i++)
		{
			Millisecond time = new Millisecond();
			this.series[0].addOrUpdate(time, getEIT_Control_Dlg().EIT_Control.Accelometer_X[i]);
			this.series[1].addOrUpdate(time, getEIT_Control_Dlg().EIT_Control.Accelometer_Y[i]);
			this.series[2].addOrUpdate(time, getEIT_Control_Dlg().EIT_Control.Accelometer_Z[i]);
			this.series[3].addOrUpdate(time, getEIT_Control_Dlg().EIT_Control.Tempreture[i]);
			this.series[4].addOrUpdate(time, getEIT_Control_Dlg().EIT_Control.Gyro_X[i]);
			this.series[5].addOrUpdate(time,  getEIT_Control_Dlg().EIT_Control.Gyro_Y[i]);
			this.series[6].addOrUpdate(time, getEIT_Control_Dlg().EIT_Control.Gyro_Z[i]);
			this.series[7].addOrUpdate(time, getEIT_Control_Dlg().EIT_Control.External_trigger[i]);
		}
		getEIT_Control_Dlg().EIT_Control.m_bMessageGraphBio_SignalFinishFlag= true;
		}
    }*/
    

    public void GetBioSignalData()
    {
    	  TimerTask timerTask = new TimerTask() {

              @Override
              public void run() {
            	// getEIT_Control_Dlg().EIT_Control.m_bMessageGraphBio_SignalFinishFlag= false;
          		for(int i =0 ; i< 50 ; i++)
          		{
          			series[0].addOrUpdate(new Millisecond(), getEIT_Control_Dlg().EIT_Control.Accelometer_X[i]);
          			series[1].addOrUpdate(new Millisecond(), getEIT_Control_Dlg().EIT_Control.Accelometer_Y[i]);
          			series[2].addOrUpdate(new Millisecond(), getEIT_Control_Dlg().EIT_Control.Accelometer_Z[i]);
          			series[3].addOrUpdate(new Millisecond(), getEIT_Control_Dlg().EIT_Control.Tempreture[i]);
          			series[4].addOrUpdate(new Millisecond(), getEIT_Control_Dlg().EIT_Control.Gyro_X[i]);
          			series[5].addOrUpdate(new Millisecond(),  getEIT_Control_Dlg().EIT_Control.Gyro_Y[i]);
          			series[6].addOrUpdate(new Millisecond(), getEIT_Control_Dlg().EIT_Control.Gyro_Z[i]);
          			series[7].addOrUpdate(new Millisecond(), getEIT_Control_Dlg().EIT_Control.External_trigger[i]);

          		}
          		//getEIT_Control_Dlg().EIT_Control.m_bMessageGraphBio_SignalFinishFlag= true;
          		//System.out.println("inside timerrrrrrrrrrrrrrrrr   "+getEIT_Control_Dlg().EIT_Control.m_bMessageGraphBio_SignalFinishFlag);

                  //update();
              }
          };
          timer = new Timer();
          timer.schedule(timerTask, 1,200);

    }
	public static Control getEIT_Control_Dlg() {
		return EIT_Control_Dlg;
	}

	public static void setEIT_Control_Dlg(Control eIT_Control_Dlg) {
		EIT_Control_Dlg = eIT_Control_Dlg;
	}

	  public void timer_cancel() {
	       /* TimerTask timerTask = new TimerTask() {

	            @Override
	            public void run() {
	                System.out.println("Updated timer");

	            }
	        };*/
	        timer.cancel();
	        /*timer = new Timer();
	        timer.schedule(timerTask, 2000);*/
	    }

}
