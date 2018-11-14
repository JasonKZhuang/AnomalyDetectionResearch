package detection.algorithms.baseLine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import detection.beans.StockRecord;
import detection.db.DatabaseHelperImpl;
import detection.db.IDatabaseHelper;
import detection.utility.DateUtil;
import detection.utility.MyConstants;

public class BaseLineExcelFileHelper
{
	protected static IDatabaseHelper dbHelper = new DatabaseHelperImpl();

	private static String bootPath = "D:\\RMIT\\Master\\0Minor Thesis Project\\ExperimentData\\";
	
	public BaseLineExcelFileHelper()
	{
		
	}
	
	public void importStock(String fileName,String sheetName)
	{
		String fullName = bootPath + fileName;
		File file = new File(fullName);
		List<StockRecord> recordList;
		dbHelper.truncateTable(MyConstants.TABLE_STOCK_RECORDS);
		try
		{
			recordList = readStocksFromExcel(file, sheetName);
	        dbHelper.insertRecords(recordList);
		} catch (ParseException e)
		{
			e.printStackTrace();
		}	
	}
	
	@SuppressWarnings("resource")
	public List<StockRecord> readStocksFromExcel(File file,String sheetName) throws ParseException
	{
		List<StockRecord> retList = new ArrayList<>();
		List<String> nameList = new ArrayList<>();
		int i_row = 0;
		int i_cel = 0;
		try
		{
			FileInputStream excelFile = new FileInputStream(file);
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet sheet = workbook.getSheet(sheetName);
			Iterator<Row> row_iterator = sheet.iterator();
			while (row_iterator.hasNext())
			{
				Row currentRow = row_iterator.next();
				Iterator<Cell> cellIterator = currentRow.iterator();
				if (i_row == 0)
				{
					while (cellIterator.hasNext())
					{
						Cell cell = cellIterator.next();
						if (i_cel==0)
						{
							;
						}else
						{
							String name = cell.getStringCellValue();
							nameList.add(name);
						}
						i_cel = i_cel + 1;
					}

				} else
				{
					i_cel = 0;
					Date recordDate = new Date();
					double price =0d;
					while (cellIterator.hasNext())
					{
						Cell currentCell = cellIterator.next();
						CellType ct = currentCell.getCellType();
						if (i_cel == 0)
						{
							if (ct==CellType.STRING)
							{
								recordDate =DateUtil.convertStringToDate(currentCell.getStringCellValue());
							}else
							{
								recordDate = currentCell.getDateCellValue();
							}
						}else
						{
							price = currentCell.getNumericCellValue();
							StockRecord newRecord = new StockRecord();
							newRecord.setStockName(nameList.get(i_cel-1));
							newRecord.setTdate(recordDate);
							newRecord.setClose(price);
							retList.add(newRecord);
						}
						i_cel = i_cel + 1;
					}
				}
				i_row = i_row + 1;
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return retList;
	}
	
	public void writeExtremeValuesToExcel(String fileName,String sheetName,List<PeerGroupRecordBean> topRecord)
	{
		
		String fullName = bootPath + fileName;
		try
		{
			File file = new File(fullName);
			FileInputStream inputStream = new FileInputStream(file);
			Workbook wb = new XSSFWorkbook(inputStream);
			
			Sheet sheet = wb.getSheet(sheetName);
			if (sheet==null)
			{
				sheet = wb.createSheet(sheetName);
			}
			//Write the first row
			Row first_row = sheet.createRow(0);
			first_row.createCell(0).setCellValue("RID");
			first_row.createCell(1).setCellValue("Date");
			first_row.createCell(2).setCellValue("Stock");
			first_row.createCell(3).setCellValue("Orig_Price");
			first_row.createCell(4).setCellValue("Weight_mean");
			first_row.createCell(5).setCellValue("difference");
			first_row.createCell(6).setCellValue("STD");
			first_row.createCell(7).setCellValue("Rank of STD Times");
			Row row = null;
			for(int i=0;i<topRecord.size();i++)
		    {
				row = sheet.createRow(i+1);
		    	PeerGroupRecordBean bb  = topRecord.get(i);
		    	row.createCell(0).setCellValue(bb.getrId());
		    	row.createCell(1).setCellValue(DateUtil.convertDateToString(bb.getTdate()));
		    	row.createCell(2).setCellValue(bb.getSelf_name());
		    	row.createCell(3).setCellValue(bb.getSelf_price());
		    	row.createCell(4).setCellValue(bb.getWeight_mean());
		    	row.createCell(5).setCellValue(bb.getDiffer());
		    	row.createCell(6).setCellValue(bb.getStd());
		    	row.createCell(7).setCellValue(bb.getStd_rank());
		    }
		    // Write the output to a file
		    OutputStream outputStream = new FileOutputStream(file);
		    wb.write(outputStream);
		    wb.close();
		    outputStream.close();
		    inputStream.close();
			
		}catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
