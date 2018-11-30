package USBCommuniction;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


public class USB_Control {
	USB_Functions Usb = new USB_Functions();
	SiUSBXp USB_lib = SiUSBXp.INSTANCE;
	int m_nUSBConnection=0;
	int m_nDeviceNum=0;
	int m_nPortStatus =0;
	byte[] m_nData = new byte[10];
	int[] m_nData_int = new int[10];
	int[] m_nResponse = new int[20];
	public static final int FALSE =  0;
	public static final int TRUE  =  1;
	public static final int NULL  =  0;

	private JFrame frame;
	private JTextField USBConnection_txt;
	private JTextField data1_txt;
	private JTextField data2_txt;
	private JTextField data3_txt;
	private JTextField data4_txt;
	private JTextField data5_txt;
	private JTextField data6_txt;
	private JTextField data7_txt;
	private JTextField data8_txt;
	private JTextField data9_txt;
	private JTextField data10_txt;
	private JTextField open_txt;



	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					USB_Control window = new USB_Control();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public USB_Control() {
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 473, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		if(m_nPortStatus == 0)
		{
			System.out.printf("Port Close");
		}
		else if(m_nPortStatus == 1)
		{
			System.out.printf("Port Open");
		}
		else
		{
			System.out.printf("Port Error");
		}
		
		JButton chk_btn = new JButton("Check");
		chk_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				Usb.FillDeviceList();
				m_nDeviceNum = Usb.m_nNumofDevice;
				Usb.DeviceSelect(m_nDeviceNum-1);
		
				m_nUSBConnection = m_nDeviceNum;
			//	UpdateData(FALSE);
				if (m_nDeviceNum >= 1)
				{
					USBConnection_txt.setText("Connected");
				
				}
				else {
					USBConnection_txt.setText("Disconnect");
				}
			
			}
		});
		chk_btn.setBounds(39, 75, 120, 31);
		frame.getContentPane().add(chk_btn);
	
		
		USBConnection_txt = new JTextField();
		USBConnection_txt.setBounds(39, 33, 120, 31);
		frame.getContentPane().add(USBConnection_txt);
		USBConnection_txt.setColumns(10);
		
		data1_txt = new JTextField();
		data1_txt.setText("0");
		data1_txt.setBounds(39, 161, 51, 20);
		frame.getContentPane().add(data1_txt);
		data1_txt.setColumns(10);
		
		data2_txt = new JTextField();
		data2_txt.setText("0");
		data2_txt.setColumns(10);
		data2_txt.setBounds(100, 161, 51, 20);
		frame.getContentPane().add(data2_txt);
		
		data3_txt = new JTextField();
		data3_txt.setText("0");
		data3_txt.setColumns(10);
		data3_txt.setBounds(161, 161, 51, 20);
		frame.getContentPane().add(data3_txt);
		
		data4_txt = new JTextField();
		data4_txt.setText("0");
		data4_txt.setColumns(10);
		data4_txt.setBounds(222, 161, 51, 20);
		frame.getContentPane().add(data4_txt);
		
		data5_txt = new JTextField();
		data5_txt.setText("0");
		data5_txt.setColumns(10);
		data5_txt.setBounds(283, 161, 51, 20);
		frame.getContentPane().add(data5_txt);
		
		data6_txt = new JTextField();
		data6_txt.setText("0");
		data6_txt.setColumns(10);
		data6_txt.setBounds(39, 192, 51, 20);
		frame.getContentPane().add(data6_txt);
		
		data7_txt = new JTextField();
		data7_txt.setText("0");
		data7_txt.setColumns(10);
		data7_txt.setBounds(100, 192, 51, 20);
		frame.getContentPane().add(data7_txt);
		
		data8_txt = new JTextField();
		data8_txt.setText("0");
		data8_txt.setColumns(10);
		data8_txt.setBounds(161, 192, 51, 20);
		frame.getContentPane().add(data8_txt);
		
		data9_txt = new JTextField();
		data9_txt.setText("0");
		data9_txt.setColumns(10);
		data9_txt.setBounds(222, 192, 51, 20);
		frame.getContentPane().add(data9_txt);
		
		data10_txt = new JTextField();
		data10_txt.setText("0");
		data10_txt.setColumns(10);
		data10_txt.setBounds(283, 192, 51, 20);
		frame.getContentPane().add(data10_txt);
		
		JButton send_btn = new JButton("Send");
		send_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				USB_lib.SI_FlushBuffers(Usb.dev_handle_ref.getValue(),(byte)0,(byte)0);
				try
				{
				m_nData[0]=Byte.valueOf(data1_txt.getText());
				m_nData[1]=Byte.valueOf(data2_txt.getText());
				m_nData[2]=Byte.valueOf(data3_txt.getText());
				m_nData[3]=Byte.valueOf(data4_txt.getText());
				m_nData[4]=Byte.valueOf(data5_txt.getText());
				m_nData[5]=Byte.valueOf(data6_txt.getText());
				m_nData[6]=Byte.valueOf(data7_txt.getText());
				m_nData[7]=Byte.valueOf(data8_txt.getText());
				m_nData[8]=Byte.valueOf(data9_txt.getText());
				m_nData[9]=Byte.valueOf(data10_txt.getText());
				
				Usb.USBComm(m_nData[0],m_nData[1],m_nData[2],m_nData[3],m_nData[4],m_nData[5],m_nData[6],m_nData[7],m_nData[8],m_nData[9]);
				}
				catch(NumberFormatException e1)
				{
					JOptionPane.showMessageDialog(null, "Please enter an integer between 0 and 127", "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
				}
				for (int j =0; j<10;j++)
				{
					m_nResponse[j]= (int) (Usb.buf[j] &0xFF) ;
					//System.out.print(m_nResponse[j]);
				}
				JOptionPane.showMessageDialog(null,m_nResponse[0]+"  "+(int)m_nResponse[1]+"  "+(int)m_nResponse[2]+"  "+ (int)m_nResponse[3]+"  "+(int)m_nResponse[4]+"  "+(int)m_nResponse[5]+"  "+(int)m_nResponse[6]+"  "+(int)m_nResponse[7]+"  "+(int)m_nResponse[8]+"  "+(int)m_nResponse[9], "InfoBox: " + "Result", JOptionPane.INFORMATION_MESSAGE);
			
				
				
				
				
			}
		});
		send_btn.setBounds(358, 161, 89, 51);
		frame.getContentPane().add(send_btn);
		
		open_txt = new JTextField();
		open_txt.setColumns(10);
		open_txt.setBounds(203, 33, 141, 31);
		frame.getContentPane().add(open_txt);
		
		JButton open_btn = new JButton("Open");
		open_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(m_nPortStatus != 1)
				{
					if(Usb.USBPortOpen(m_nDeviceNum-1) != SiUSBXp.SI_SUCCESS)
					{
						//System.out.println(TestFunc.USBPortOpen(m_nDeviceNum-1)); ///SI_DEVICE_NOT_FOUND	
						System.out.println("Port Open Error");
						m_nPortStatus = -1;
					}
					else
					{
						m_nPortStatus = 1;
					}

					if(m_nPortStatus == 0)
					{
						open_txt.setText("Port Close");
					}
					else if(m_nPortStatus == 1)
					{
						open_txt.setText("Port Open");
					}
					else
					{
						open_txt.setText("Port Error");
					}
					//UpdateData(false);
				}
				
			}
		});
		open_btn.setBounds(203, 75, 64, 31);
		frame.getContentPane().add(open_btn);
		
		JButton colse_btn = new JButton("Close");
		colse_btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(m_nPortStatus != 0)
				{
					///JOptionPane.showMessageDialog(null, m_nPortStatus, "InfoBox: " + "Error", JOptionPane.INFORMATION_MESSAGE);
					if(Usb.USBPortClose(m_nDeviceNum-1) != SiUSBXp.SI_SUCCESS)
					{
						open_txt.setText("Port Open Error");
						m_nPortStatus = -1;
					}
					else
					{
						m_nPortStatus = 0;
					}
					

					if(m_nPortStatus == 0)
					{
						open_txt.setText("Port Close");
					}
					else if(m_nPortStatus == 1)
					{
						open_txt.setText("Port Open");
					}
					else
					{
						open_txt.setText("Port Error");
					}
				
				}
				else
				{
					open_txt.setText("Port Already Close");
				}
			}
		});
		colse_btn.setBounds(275, 75, 69, 31);
		frame.getContentPane().add(colse_btn);
		
		
	}
}
