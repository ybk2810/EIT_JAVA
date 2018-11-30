package MainFunctions;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;


public class EIT_Image extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField EditMax_txt;
	private JTextField EditMin_txt;
	private JTextField MAXORIGINAL_txt;
	private JTextField MINORIGINAL_txt;
	private JLabel ImaheDisIb;
	BufferedImage image;
	
	static Control EIT_Control_Dlg;    //check
	int m_nImageType;
	public int m_nAlgorithm = 0;//check sTDV default
	boolean m_bFixedRange; 
	
	double m_dChangeMax;
	double m_dChangeMin;
	
	double m_dMax;
	double m_dMin;
	int count =0;
	public String DirectoryPath = System.getProperty("user.dir");

//	#define	COLUMN_NUM	40
//	#define	ROW_NUM		40

			int R[]={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,26,51,77,102,128,153,178,204,229,
			255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,239,223,207,191,175,159,143,128,236
			};

			int G[]={0,0,0,0,0,0,0,0,18,36,55,73,91,109,128,146,164,182,200,219,237,255,255,255,255,255,255,255,255,255,255,255,
			255,255,255,255,255,255,255,255,255,255,237,219,200,182,164,146,128,109,91,73,55,36,18,0,0,0,0,0,0,0,0,0, 233   };

			int B[]={143,159,175,191,207,223,239,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,255,
			255,255,255,255,255,255,227,198,170,142,113,85,57,28,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,216};
			
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");   ///  this line for Grid exception may need change later
					
					EIT_Image frame = new EIT_Image(EIT_Control_Dlg);     ///////////////////// //check
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	/**
	 * Create the frame. 
	 * @param control 
	 */
	public EIT_Image(Control control) {     //check
		this.EIT_Control_Dlg = control; //check
		m_nImageType = Definition.IMAGE_MAGNITUDE;
		setTitle("Image");
		setIconImage(Toolkit.getDefaultToolkit().getImage(DirectoryPath+"\\Images\\logo.png"));
	//	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 655, 472);
		setLocation(10, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setBorder(new TitledBorder(null, "Pixel Range", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setBounds(439, 39, 183, 181);
		contentPane.add(panel);
		

		JRadioButton fixed_rad_btn = new JRadioButton("Fixed Range");
		JRadioButton Dynamic_rad_btn = new JRadioButton("Dynamic Range");
		Dynamic_rad_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Dynamic_rad_btn.isSelected())
				{
				m_bFixedRange = false;
				fixed_rad_btn.setSelected(false);
				}
				Dynamic_rad_btn.setSelected(true);
				
			}
		});
		Dynamic_rad_btn.setSelected(true);
		Dynamic_rad_btn.setBounds(17, 25, 109, 23);
		panel.add(Dynamic_rad_btn);
		
		fixed_rad_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Dynamic_rad_btn.isSelected())
				{
				m_bFixedRange = true;
				Dynamic_rad_btn.setSelected(false);
				}
				fixed_rad_btn.setSelected(true);
				
				
			}
		});
		fixed_rad_btn.setBounds(17, 51, 109, 23);
		panel.add(fixed_rad_btn);
		
		EditMax_txt = new JTextField();
		EditMax_txt.setText("0");
		EditMax_txt.setColumns(10);
		EditMax_txt.setBounds(17, 106, 61, 23);
		panel.add(EditMax_txt);
		
		EditMin_txt = new JTextField();
		EditMin_txt.setText("0");
		EditMin_txt.setColumns(10);
		EditMin_txt.setBounds(103, 106, 61, 23);
		panel.add(EditMin_txt);
		
		JLabel label = new JLabel("Max");
		label.setBounds(32, 81, 46, 14);
		panel.add(label);
		
		JLabel label_1 = new JLabel("Min");
		label_1.setBounds(120, 81, 46, 14);
		panel.add(label_1);
		
		JButton chang_btn = new JButton("Change");
		chang_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				m_dChangeMax = Double.parseDouble( EditMax_txt.getText());
				m_dChangeMin = Double.parseDouble( EditMin_txt.getText());
				
				if(m_dChangeMax <= m_dChangeMin)
				{
					m_dChangeMin = m_dChangeMax - 1;
					EditMin_txt.setText(String.valueOf(m_dChangeMin));
				
				}
				DisplayImage(m_dChangeMax,m_dChangeMin);
				
				
				
				
			}
		});
		chang_btn.setBounds(17, 140, 147, 29);
		panel.add(chang_btn);
		
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBorder(new TitledBorder(null, "Recon Algorithm ", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(439, 231, 183, 84);
		contentPane.add(panel_1);
		
		JComboBox<String> m_comboAlgorithm = new JComboBox<String>();
		m_comboAlgorithm.addItem("tSVD");
		m_comboAlgorithm.addItem("GREIT");
		m_comboAlgorithm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				m_nAlgorithm = m_comboAlgorithm.getSelectedIndex();
				//System.out.println(m_nAlgorithm);
			}
		});
		m_comboAlgorithm.setBounds(10, 38, 167, 31);
		panel_1.add(m_comboAlgorithm);
		
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(null);
		panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Original Pixel Range", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_2.setBounds(439, 326, 183, 94);
		contentPane.add(panel_2);
		
		MAXORIGINAL_txt = new JTextField();
		MAXORIGINAL_txt.setText("0");
		MAXORIGINAL_txt.setColumns(10);
		MAXORIGINAL_txt.setBounds(17, 55, 61, 23);
		panel_2.add(MAXORIGINAL_txt);
		
		MINORIGINAL_txt = new JTextField();
		MINORIGINAL_txt.setText("0");
		MINORIGINAL_txt.setColumns(10);
		MINORIGINAL_txt.setBounds(103, 55, 61, 23);
		panel_2.add(MINORIGINAL_txt);
		
		JLabel label_2 = new JLabel("Max");
		label_2.setBounds(32, 30, 46, 14);
		panel_2.add(label_2);
		
		JLabel label_3 = new JLabel("Min");
		label_3.setBounds(120, 30, 46, 14);
		panel_2.add(label_3);
		
      
		  image = new BufferedImage(360, 360, BufferedImage.TYPE_INT_ARGB);
         for (int x = 0; x < 360; x++)
             for (int y = 0; y < 360; y++)
                 image.setRGB(x, y, new Color(255, 255, 255).getRGB());
       ImageIcon ico= new ImageIcon(image) ;
       ImaheDisIb = new JLabel("New label");
       ImaheDisIb.setBounds(20, 40, 360, 360);
       ImaheDisIb.setIcon(ico);
       contentPane.add(ImaheDisIb);
     
        
		JLabel lblImage = new JLabel(" Image :");
		lblImage.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblImage.setBounds(20, 11, 154, 29);
		contentPane.add(lblImage);
	}
	
	int ImageRecon()
	{/// no need for if related to image 2 and remove the arguments

		synchronized (this)
		{
	
		//long start =	System.nanoTime(); 
		EIT_Control_Dlg.EIT_Control .m_bMessageImageReconFinishFlag1 = false;
		for(int i =0 ; i< 256 ; i++)
		{
			EIT_Control_Dlg.EIT_Control .Image_ObjReal[i] = EIT_Control_Dlg.EIT_Control.TempReal[0][i];
			EIT_Control_Dlg.EIT_Control.Image_ObjQuad[i] = EIT_Control_Dlg.EIT_Control.TempQuad[0][i];
			EIT_Control_Dlg.EIT_Control.Image_ObjMag[i] = EIT_Control_Dlg.EIT_Control.TempMagnitude[0][i];
			EIT_Control_Dlg.EIT_Control.Image_ObjTheta[i] = EIT_Control_Dlg.EIT_Control.TempPhase[0][i];
		}
		EIT_Control_Dlg.EIT_Control.IMAGE_DispProc(m_nImageType,Definition.TOTAL_CH,m_nAlgorithm);
		IMAGE_MAxMinSetting();

		if(m_bFixedRange == true)
		{
			DisplayImage(m_dChangeMax,m_dChangeMin);
		}
		else
		{
			DisplayImage(m_dMax,m_dMin);
		}

			EIT_Control_Dlg.EIT_Control.m_bMessageImageReconFinishFlag1 = true;
		return 0;
		}
	}
	
	
	void IMAGE_MAxMinSetting()
	{
		m_dMax = (EIT_Control_Dlg.EIT_Control.Image_dsigma[0]);
		m_dMin = (EIT_Control_Dlg.EIT_Control.Image_dsigma[0]);
		int resol = 40;
		switch (m_nAlgorithm)
		{
		case Definition.ALGORITHM_tSVD:
			for(int i=0; i<resol; i++)
			{
				for(int j=0; j<resol; j++)
				{//condition ? result_if_true : result_if_false
					m_dMax=(m_dMax>(EIT_Control_Dlg.EIT_Control.Image_dsigma[i*resol+j]) )?m_dMax:(EIT_Control_Dlg.EIT_Control.Image_dsigma[i*resol+j]);
					m_dMin=(m_dMin<(EIT_Control_Dlg.EIT_Control.Image_dsigma[i*resol+j]) )?m_dMin:(EIT_Control_Dlg.EIT_Control.Image_dsigma[i*resol+j]);
				}
			}
			break;

		case Definition.ALGORITHM_GREIT:
			for(int i=0; i<EIT_Control_Dlg.EIT_Control.GREIT_TetrahedralMeshNum; i++)
			{
				m_dMax=(m_dMax>(EIT_Control_Dlg.EIT_Control.Image_dsigma[i]) )?m_dMax:(EIT_Control_Dlg.EIT_Control.Image_dsigma[i]);
				m_dMin=(m_dMin<(EIT_Control_Dlg.EIT_Control.Image_dsigma[i]) )?m_dMin:(EIT_Control_Dlg.EIT_Control.Image_dsigma[i]);
			}
			break;
		}

		
		MAXORIGINAL_txt.setText(String.valueOf(m_dMax));
		MINORIGINAL_txt.setText(String.valueOf(m_dMin));
	}

	void DisplayImage(double max,double min)
	{
		//int numtriangle=64; 
		//int numSencol=256; 
		int sqgrid=20; 
		int mulresol=2;
		int resol = sqgrid*mulresol;   //40
		int GREITIndex;
		 Color currentColor=Color.RED;
		int i,j;
		int resol_x=0;
  		int resol_y=0;
		int r,g,b,rgb;
		switch (m_nAlgorithm)
		{
		case Definition.ALGORITHM_tSVD:   //image 40*40
			
			for( i=0; i<resol; i++)
			{
				for( j=0; j<resol; j++)
				{

					if(EIT_Control_Dlg.EIT_Control.Image_dsigma[i*resol+j] >= max)
						rgb=63;
					else if (EIT_Control_Dlg.EIT_Control.Image_dsigma[i*resol+j] <= min)
						rgb=0;
					else
						rgb=(int)(63*(EIT_Control_Dlg.EIT_Control.Image_dsigma[i*resol+j] - min)/(max - min));  

					if((EIT_Control_Dlg.EIT_Control.trinum2[i*resol+j])==-1) 
					{
						rgb=64;
					}
					r=R[rgb];g=G[rgb];b=B[rgb];
					  
				
                	currentColor = new Color(r,g,b); 
                	for(int k = 0; k < 9; k++)
                		{
                			for(int n = 0; n < 9; n++)
                			{
                				image.setRGB(resol_y+k, resol_x+n, currentColor.getRGB());  //opposite direction of tSVD
                			}
                		}
					resol_y+=9; // new cloumn
				}
				resol_y=0;
   				resol_x+=9; // new row
			}
			ImaheDisIb.repaint();
			
			break;

		
		case Definition.ALGORITHM_GREIT:
			GREITIndex = 0;
			for( i=0; i<resol; i++)
			{
				for( j=0; j<resol; j++)
				{
					if((EIT_Control_Dlg.EIT_Control.GREIT_Map[i*resol+j])==0) 
					{
						rgb=64;
					}
					else
					{
						if(EIT_Control_Dlg.EIT_Control.Image_dsigma[GREITIndex] >= max)
							rgb=63;
						else if (EIT_Control_Dlg.EIT_Control.Image_dsigma[GREITIndex] <= min)
							rgb=0;
						else
							
							rgb=(int)(63*(EIT_Control_Dlg.EIT_Control.Image_dsigma[GREITIndex] - min)/(max - min));
						GREITIndex++;
					}
					r=R[rgb];g=G[rgb];b=B[rgb];
				   currentColor = new Color(r, g, b); 
				   for(int k = 0; k < 9; k++)
           		{
           			for(int n = 0; n < 9; n++)
           			{
           				image.setRGB(resol_x+k, resol_y+n, currentColor.getRGB());
           			}
           		}
				resol_y+=9;
			}
			resol_y=0;
				resol_x+=9;
		}
		ImaheDisIb.repaint();

			break;
		}

		
	}
	
}
