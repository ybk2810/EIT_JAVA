package MainFunctions;

public interface Definition {
	/****************************Command*	*******************************/
	/////////////////////////	COMMON COMMAND	/////////////////////////
       public static final int	COMMAND_RESET				=0;
       public static final int	RESPONSE_RESET				=129;
       public static final int	COMMAND_CHECK_IMM			=2;
       public static final int RESPONSE_CHECK_IMM		=	131;
       public static final int COMMAND_AVERAGE			=	4;
       public static final int	RESPONSE_AVERAGE		=	133;
       public static final int	COMMAND_OVERFLOW_RESET	=	6;
       public static final int	RESPONSE_OVERFLOW_RESET	=	135;
       public static final int	COMMAND_OVERFLOW_NUM	=	8;
       public static final int	RESPONSE_OVERFLOW_NUM	=	137;
       public static final int	COMMAND_DAC_SETTING		=	10;
       public static final int	RESPONSE_DAC_SETTING	=	139;
       public static final int	COMMAND_CCS_DELAY_SETTING=	12;
       public static final int	RESPONSE_CCS_DELAY_SETTING=	141;
       public static final int	COMMAND_COMMFPGA_SET	=	14;
       public static final int	RESPONSE_COMMFPGA_SET	=	143;
       public static final int	COMMAND_DATA_READ		=	16;
       public static final int	RESPONSE_DATA_READ		=	145;
       public static final int	COMMAND_TIMESLOT		=	18;
       public static final int	RESPONSE_TIMESLOT		=	147;
       public static final int	COMMAND_FIFO_RESET		=	20;
       public static final int	RESPONSE_FIFO_RESET		=	149;
       public static final int	COMMAND_ANALOG_BP_SW	=	22;
       public static final int	RESPONSE_ANALOG_BP_SW	=	151;
       public static final int	COMMAND_CHANGE_ADC_RAM	=	24;
       public static final int	RESPONSE_CHANGE_ADC_RAM	=	153;
       public static final int	COMMAND_CAL_SW_SETTING	=	26;
       public static final int	RESPONSE_CAL_SW_SETTING	=	155;
       public static final int	COMMAND_IMM_SW_RESET	=	28;
       public static final int	RESPONSE_IMM_SW_RESET	=	157;
       public static final int	COMMAND_COMMFPGA_SW_RESET=	30;
       public static final int	RESPONSE_COMMFPGA_SW_RESET=	159;

/////////////////////////	MANUAL PROJECTION COMMAND	/////////////////////////
       public static final int COMMAND_WG_CHANNEL_SETTING	=32;
       public static final int RESPONSE_WG_CHANNEL_SETTING	=161;
       public static final int COMMAND_SINKCH			=	34;
       public static final int RESPONSE_SINKCH			=	163;
       public static final int	COMMAND_CCS_DIGI_SETTING=	36;
       public static final int	RESPONSE_CCS_DIGI_SETTING=	165;
       public static final int	COMMAND_GIC_SW			=	38;
       public static final int	RESPONSE_GIC_SW			=	167;
       public static final int	COMMAND_GIC1_SETTING	=	40;
       public static final int	RESPONSE_GIC1_SETTING	=	169;
       public static final int	COMMAND_VM_DIGI1_SETTING=	42;
       public static final int	RESPONSE_VM_DIGI1_SETTING=	171;
       public static final int	COMMAND_INJ_FREQ_SETTING=	44;
       public static final int	RESPONSE_INJ_FREQ_SETTING=	173;
       public static final int	COMMAND_ACQ_FREQ_SETTING=	46;
       public static final int	RESPONSE_ACQ_FREQ_SETTING=	175;
       public static final int	COMMAND_DM_FREQ_SETTING	=	48;
       public static final int	RESPONSE_DM_FREQ_SETTING=	177;
       public static final int COMMAND_PROJECTION		=	50;
       public static final int RESPONSE_PROJECTION		=	179;
       public static final int COMMAND_ZERO_INTERVAL	=	52;
       public static final int RESPONSE_ZERO_INTERVAL	=	181;
       public static final int COMMAND_DM_DELAY			=	54;
       public static final int RESPONSE_DM_DELAY		=	183;
       public static final int COMMAND_WG_START			=	56;
       public static final int RESPONSE_WG_START		=	185;
       public static final int COMMAND_WG_STOP			=	58;
       public static final int RESPONSE_WG_STOP			=	187;
       public static final int COMMAND_MANUAL_PROJ_END	=	60;
       public static final int RESPONSE_MANUAL_PROJ_END	=	189;

/////////////////////////	PIPELINE SCAN COMMAND	/////////////////////////
       public static final int	COMMAND_PROJECTIONTABLE	=		64;
       public static final int RESPONXE_PROJECTIONTABLE	=	193;
       public static final int	COMMAND_CCSTABLE		=		66;
       public static final int RESPONXE_CCSTABLE		=		195;
       public static final int	COMMAND_PIPELINESCAN_SETTING	=68;
       public static final int RESPONSE_PIPELINESCAN_SETTING	=197;
       public static final int	COMMAND_PIPELINESCAN_STOP		= 70;
       public static final int RESPONSE_PIPELINESCAN_STOP		= 199;
       public static final int	COMMAND_COMM_SCAN_START_STOP	=72;
       public static final int RESPONSE_COMM_SCAN_START_STOP	=201;

/////////////////////////	NEW SCAN COMMAND	/////////////////////////
       public static final int COMMAND_NEW_SCAN_MODE		=	80;
       public static final int RESPONSE_NEW_SCAN_MODE		=	209;
       public static final int COMMAND_NEW_SCAN_START_STOP	=	82;
       public static final int RESPONSE_NEW_SCAN_START_STOP	=	211;
       public static final int COMMAND_NEW_SCAN_STOP		=	84;
       public static final int RESPONSE_NEW_SCAN_STOP		=	213;

/////////////////////////	ETC		/////////////////////////
       public static final int COMMAND_START_OFFSET_CAL		=	86;
       public static final int RESPONSE_START_OFFSET_CAL	=	215;
       public static final int COMMAND_START_OFFSET_DATA	=	88;
       public static final int RESPONSE_START_OFFSET_DATA	=	217;
       public static final int COMMAND_TIME_INTERVAL_ECG_TRIGGER=90;
       public static final int RESPONSE_TIME_INTERVAL_ECG_TRIGGER=	219;
       public static final int COMMAND_SEND_OFFSET_VAL		=	92;
       public static final int RESPONSE_SEND_OFFSET_VAL		=	221;
       public static final int COMMAND_RPEAK_TRIGGER		=	120;
       public static final int RESPONSE_RPEAK_TRIGGER		=	249;

/////////////////////////	.v1 Command	by « ¡ﬂ	/////////////////////////
       public static final int COMMAND_SOURCESINK		=	13;
       public static final int RESPONSE_SOURCESINK		=	142;


       public static final int COMMAND_GIC2_SETTING		=	33;
       public static final int RESPONSE_GIC2_SETTING	=	162;
       public static final int COMMAND_GIC3_SETTING		=	34;
       public static final int	RESPONSE_GIC3_SETTING	=	163;
       public static final int COMMAND_GIC4_SETTING		=	35;
       public static final int	RESPONSE_GIC4_SETTING	=	164;

       public static final int	COMMAND_CCS_AMP1_SETTING	=	40;
       public static final int	RESPONSE_CCS_AMP1_SETTING	=	169;
       public static final int	COMMAND_CCS_AMP2_SETTING	=	41;
       public static final int	RESPONSE_CCS_AMP2_SETTING	=	170;
       public static final int	COMMAND_CCS_OFFSET_SETTING	=	42;
       public static final int	RESPONSE_CCS_OFFSET_SETTING	=	171;

       public static final int	COMMAND_VM_DIGI2_SETTING	=	49;
       public static final int	RESPONSE_VM_DIGI2_SETTING	=	178;


       public static final int	COMMAND_INJECTION_DELAY		=	64;
       public static final int	RESPONSE_INJECTION_DELAY	=	193;


       public static final int COMMAND_STARTOFFSETCAL	=	104;
       public static final int RESPONSE_STARTOFFSETCAL	=	233;
       public static final int	COMMAND_SENDOFFSETDATA	=	106;
       public static final int	RESPONSE_SENDOFFSETDATA	=	235;

/******************************************/
       public static final int MAX_CH					=	16;
       public static final int TOTAL_CH		 			=	16;
       public static final int NUM_OF_FREQUENCY			=	10;
       public static final int	NUM_OF_MIXED_FREQUENCY	=	4;
       public static final int	VM						=	0;
       public static final int SOURCE					=	1;
       public static final int	SINK					=	2;
       public static final int	ALL_CH					=	255;
       public static final int	ALL_DATA				=	256;

/******Script File*************************/
       public static final int	NOCOMMENT				=	-1;
       public static final int	INCLUDE					=	-2;
       public static final int	SCRIPTLEVEL1			=	 0;
       public static final int	SCRIPTLEVEL2			=	10;
       public static final int	SCRIPTLEVEL2_START		=	11;
       public static final int	SCRIPTLEVEL2_INCLUDE	=	12;
       public static final int	SCRIPTLEVEL3_SETTING	=	20;
       public static final int	SCRIPTLEVEL3_SCAN		=	21;
       public static final int SCRIPTLEVEL3_CALIBRATION	=	22;
       public static final int	SCRIPTLEVEL4_CHANNEL	=	30;
       public static final int	SCRIPTLEVEL4_AVERAGE	=	31;
       public static final int	SCRIPTLEVEL4_DELAY		=	32;
       public static final int	SCRIPTLEVEL4_FREQUENCY	=	33;
       public static final int	SCRIPTLEVEL4_TIMEINFOHIGH=	34;
       public static final int	SCRIPTLEVEL4_TIMEINFOMID=	35;
       public static final int	SCRIPTLEVEL4_TIMEINFOLOW	=	36;
       public static final int SCRIPTLEVEL4_INJ_DELAYHIGH	=	37;
       public static final int SCRIPTLEVEL4_INJ_DELAYLOW	=	38;
       public static final int SCRIPTLEVEL4_CAL_DCOFFSET	=	50;
       public static final int SCRIPTLEVEL4_CAL_OUTPUTIMPEDANCE	=51;
       public static final int SCRIPTLEVEL4_CAL_AMPLITUDE	=	52;
       public static final int SCRIPTLEVEL4_CAL_VOLTMETER	=	53;
       public static final int SCRIPTLEVEL4_CAL_PROTOCOLNAME=	54;

/******************************************/
/*******Projection File********************/
       public static final int PROjECTIONTABLEROW		=	17;

       public static final int PROJECTIONINDEX			=	0;
       public static final int	CHANNELINDEX			=	1;
       public static final int	CHANNELINFO				=	2;
       public static final int	CHANNELCTRL				=	3;
       public static final int	INJECTIONCURRENTFREQUENCY=	4;
       public static final int	AMP1_HIGH				=	5;
       public static final int	AMP1_LOW				=	6;
       public static final int	AMP2_HIGH				=	7;
       public static final int	AMP2_LOW				=	8;
       public static final int	GAIN1					=	9;
//       public static final int	GAIN2				=	10;
       public static final int	ACQUREFREQUENCY_GAP		=	10;
       public static final int	ACQUREFREQUENCY_CNT_HIGH=	11;
       public static final int	ACQUREFREQUENCY_CNT_LOW	=	12;
       public static final int	TOTALDEMODULATIONFREQUENCY	=	13;
       public static final int	DEMODULATIONFREQUENCY1	=	14;
       public static final int	DEMODULATIONFREQUENCY2	=	15;
       public static final int	DEMODULATIONFREQUENCY3	=	16;
       public static final int	DEMODULATIONFREQUENCY4	=	17;
       public static final int	DEMODULATIONFREQUENCY5	=	18;
       
       public static final int	PROJECTIONFILE_CH		=		0;
       public static final int	PROJECTIONFILE_SIGN		=		1;
       public static final int	PROJECTIONFILE_AMP		=		2;
       public static final int	PROJECTIONFILE_FREQUENCY=		3;
       public static final int	PROJECTIONFILE_GAIN		=		4;

       public static final int MIXED_FREQUENCY			=	0;
       public static final int	SINGLE_FREQUENCY		=	1;

/******************************************/
/********CCS Table***********************/
       public static final int CCSTABLEROW				=	17;

       public static final int FREQ_NUM					=	0;
       public static final int	GIC_ON_OFF				=	1;
       public static final int	CLK_CNT_HIGH			=	2;
       public static final int	CLK_CNT_LOW				=	3;
       public static final int	GAP_DATA				=	4;
       public static final int	HCP_CCS_A				=	5;
       public static final int	HCP_CCS_B				=	6;
       public static final int	GIC1A					=	7;
       public static final int	GIC1B					=	8;
       public static final int	GIC2A					=	9;
       public static final int	GIC2B					=	10;
       public static final int	GIC3A					=	11;
       public static final int	GIC3B					=	12;
       public static final int	GIC4A					=	13;
       public static final int	GIC4B					=	14;
       public static final int	DAC_OFFSET_HIGH			=	15;
       public static final int DAC_OFFSET_LOW			=	16;

/*****************************************/
/*******Calibration File*******************/
       public static final int	R1						=	0;
       public static final int	R2						=	1;
       public static final int	C1						=	2;
       public static final int	C4						=	3;

       public static final int	FINE_R					=	0;
       public static final int	COARSE_R				=	1;
       public static final int	FINE_C					=	2;
       public static final int	COARSE_C				=	3;
/******************************************/
/*******Mode*******************************/
       public static final int	MANUAL_MODE				=	1;
       public static final int	PIPELINE_MODE			=	0;

/*******************************************/
/******Image********************************/
       public static final int IMAGE_MAGNITUDE			=	0;
       public static final int	IMAGE_REAL				=	1;
       public static final int	IMAGE_IMAGINARY			=	2;
       public static final int	IMAGE_PHASE				=	3;
/*******************************************/
/*****Algorithm*****************************/
       public static final int ALGORITHM_tSVD			=	0;
       public static final int	ALGORITHM_GREIT			=	1;
       public static final int ALGORITHM_FACTORARIZATION=	2;
/*********************************************/
       public static final int OLD_MODE					=	0;
       public static final int NEW_MODE					=	1;

}
