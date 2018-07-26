package com.anomalyDetection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import detection.beans.StockRecord;

public class FileHelper implements IFileHelper
{

	public List<File> getFileListFromPath(String path)
	{
		List<File> retList = new ArrayList<File>();
		
		File file = null;
	    String[] paths;
		try
		{
			// create new file object
			file = new File(path);

			// array of files and directory
			paths = file.list();

			// for each name in the path array
			for (String item : paths)
			{
				// prints filename and directory name
				//System.out.println(item);
				File f = new File(path+"/"+item);
				retList.add(f);
			}
		} catch (Exception e)
		{
			// if any error occurs
			e.printStackTrace();
		}

		return retList;
	}

	public List<StockRecord> getRecordsFromFile(File file)
	{
		List<StockRecord> retList = new ArrayList<StockRecord>();

		try
		{
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line;
			String arrayLine[] = new String[7];
			bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null)
			{
				arrayLine = line.split(",", -1);
				StockRecord record = new StockRecord();
				String fName = file.getName();
				try 
				{
					record.setStockName(fName.substring(0, fName.length() - 7));
				}catch(Exception exp)
				{
					record.setStockName("");
				}
				
				try 
				{
					record.setTdate(DateUtil.convertStringToDate(arrayLine[0]));
				}catch(Exception exp)
				{
					record.setTdate(null);
				}
				
				try 
				{
					record.setOpen(Float.parseFloat(arrayLine[1]));
				}catch(Exception exp)
				{
					record.setOpen(0);
				}
				
				try 
				{
					record.setHigh(Float.parseFloat(arrayLine[2]));
				}catch(Exception exp)
				{
					record.setHigh(0);
				}
				
				try 
				{
					record.setLow(Float.parseFloat(arrayLine[3]));
				}catch(Exception exp)
				{
					record.setLow(0);
				}
				
				try 
				{
					record.setClose(Float.parseFloat(arrayLine[4]));
				}catch(Exception exp)
				{
					record.setClose(0);
				}
				try 
				{
					record.setVolume(Long.parseLong(arrayLine[5]));
				}catch(Exception exp)
				{
					record.setVolume(0);
				}
				retList.add(record);
			}
			fileReader.close();
			//System.out.println("Reading file finished.");
		}catch (IOException e)
		{
			e.printStackTrace();
		}
		return retList;
	}

	public void ReadStringFromFileLineByLine(String fileName)
	{
		try
		{
			File file = new File(fileName);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null)
			{
				stringBuffer.append(line);
				stringBuffer.append("\n");
			}
			fileReader.close();
			System.out.println("Contents of file:");
			System.out.println(stringBuffer.toString());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
