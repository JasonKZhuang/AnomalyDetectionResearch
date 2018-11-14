package detection.algorithms.kaizhi;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Sheet;

public interface IExcelHelper
{
	public List<RecordBean> readExcelByPOI(File file,String sheetName);

	public List<StockBean> readStocksFromExcel(File file);
	
	public void writeExcelByPOI(File file, List<SimilarityBean> lstBeans);
	
	public void writeSimilarityToExcelSheet(File file, String sheetName, List<SimilarityBean> lstBeans);
	
	public void writeSingleDayToExcelSheet(File file, SimilarityBean sBean,String[] dates);

	public void writeKSTestToExcelSheet(File file, List<KSTestStatisticBean> ksListBeans);
	
	public void deleteColumn(Sheet sheet, int columnToDelete);

	public void copyNewExcelWithLessOne(String source_fileName,String removeStockName,int columnIdx);

	public void writSimilarityRecord(File file, String sheetName,
			List<SimilarityRecord> similarityList);

	public void writeExtremeValuesToExcel(File file, String sheetNameRank, Map<CreditBean, List<String>> contribMap);
	

}
