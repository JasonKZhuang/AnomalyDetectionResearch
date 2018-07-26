package com.anomalyDetection;

import java.io.File;
import java.util.List;

import detection.beans.StockRecord;

public interface IFileHelper
{
	public List<File> getFileListFromPath(String path);
	
	public List<StockRecord> getRecordsFromFile(File file);
}
