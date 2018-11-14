import detection.algorithms.baseLine.AppMain;
import detection.algorithms.kaizhi.MyAlgorithmMain;

public class TempRun
{

	public TempRun()
	{
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args)
	{
		MyAlgorithmMain myMain = new MyAlgorithmMain();
		
		myMain.sheetNameOrig = "2014";
		myMain.sheetNameRank = "Rank" +myMain.sheetNameOrig;
		myMain.processMain2(myMain.fileName);
		
		myMain.sheetNameOrig = "2015";
		myMain.sheetNameRank = "Rank" +myMain.sheetNameOrig;
		myMain.processMain2(myMain.fileName);
		
		myMain.sheetNameOrig = "2016";
		myMain.sheetNameRank = "Rank" +myMain.sheetNameOrig;
		myMain.processMain2(myMain.fileName);
		
		myMain.sheetNameOrig = "2017";
		myMain.sheetNameRank = "Rank" +myMain.sheetNameOrig;
		myMain.processMain2(myMain.fileName);
		
		myMain.sheetNameOrig = "2018";
		myMain.sheetNameRank = "Rank" +myMain.sheetNameOrig;
		myMain.processMain2(myMain.fileName);
		
		//
		
		AppMain kimMain = new AppMain();
		kimMain.sheetNameOrig="2014";
		kimMain.process_new();
		kimMain.sheetNameOrig="2015";
		kimMain.process_new();
		kimMain.sheetNameOrig="2016";
		kimMain.process_new();
		kimMain.sheetNameOrig="2017";
		kimMain.process_new();
		kimMain.sheetNameOrig="2018";
		kimMain.process_new();

	}

}
