package detection.fileOperation;

import java.io.File;
import java.util.List;

import detection.beans.StockName;
import detection.beans.StockRecord;

public interface IFileHelper
{
	public List<File> getFileListFromPath(String path);
	
	public List<StockRecord> getRecordsFromFile(File file);
	
	public List<StockName> getStocksDetailFromFile(File file);

}
