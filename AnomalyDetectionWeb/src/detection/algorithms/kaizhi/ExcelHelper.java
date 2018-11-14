package detection.algorithms.kaizhi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import detection.utility.DateUtil;

public class ExcelHelper implements IExcelHelper
{

	public ExcelHelper()
	{
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<RecordBean> readExcelByPOI(File file, String sheetName)
	{
		List<RecordBean> retList = new ArrayList<>();
		int i_row = 0;
		int i_cel = 0;
		int count_cells = 0;
		String[] arrStocks = null;
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
						cellIterator.next();
						count_cells = count_cells + 1;//how many number columns
					}
					arrStocks = new String[count_cells - 1];
					Iterator<Cell> cellIterator2 = currentRow.iterator();
					int arrIdx  = 0;
					while (cellIterator2.hasNext())
					{
						Cell currentCell = cellIterator2.next();
						if (arrIdx==0)
						{
							arrIdx = arrIdx + 1;
							continue;
						}
						arrStocks[arrIdx - 1] = currentCell.getStringCellValue();
						arrIdx = arrIdx + 1;
					}

				} else
				{
					i_cel = 0;
					RecordBean bean = new RecordBean();
					double[] arrPrice = new double[count_cells - 1];
					while (cellIterator.hasNext())
					{
						Cell currentCell = cellIterator.next();
						
						if (i_cel == 0)
						{
							CellType ct = currentCell.getCellType();
							if (ct==CellType.STRING)
							{
								bean.setMyDate(currentCell.getStringCellValue());
							}else
							{
								Date d = currentCell.getDateCellValue();
								bean.setMyDate(DateUtil.convertDateToString(d));
							}
						}else
						{
							arrPrice[i_cel - 1] = currentCell.getNumericCellValue();
						}
						i_cel = i_cel + 1;
					}
					
					bean.setArrPrice(arrPrice);
					bean.setArrStocks(arrStocks);
					retList.add(bean);
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

	public List<StockBean> readStocksFromExcel(File file)
	{
		List<StockBean> retList = new ArrayList<>();
		int i_row = 0;
		int i_cel = 0;
		try
		{
			FileInputStream excelFile = new FileInputStream(file);
			Workbook wb = new XSSFWorkbook(excelFile);
			Sheet sheet = wb.getSheet("price");
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
							String stockName = cell.getStringCellValue();
							StockBean bean = new StockBean(stockName);
							retList.add(bean);
						}
						i_cel = i_cel + 1;
					}
					break;
				} 
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
	
	
	@Override
	public void writeExcelByPOI(File file,List<SimilarityBean> lstBeans)
	{
		try
		{
			FileInputStream inputStream = new FileInputStream(file);
			Workbook workbook = new XSSFWorkbook(inputStream);
			Sheet datatypeSheet = workbook.getSheetAt(2);
			Iterator<Row> iterator = datatypeSheet.iterator();
			int i_row = 0;
			int i_cel = 0;
			while (iterator.hasNext())
			{
				if (i_row == lstBeans.size())
				{
					break;
				}
				Row currentRow = iterator.next();
				SimilarityBean bean = lstBeans.get(i_row);	
				double[] similarity = bean.getSimilarity();
				Iterator<Cell> cellIterator = currentRow.iterator();
				i_cel=0;
				while (cellIterator.hasNext())
				{
					if (i_cel > similarity.length)
					{
						break;
					}
					
					Cell currentCell = cellIterator.next();
					if (i_cel==0)
					{
						currentCell.setCellValue(bean.getBootName());
					}else
					{
						currentCell.setCellValue(similarity[i_cel - 1]);
					}
					i_cel = i_cel + 1;
				}
				
				i_row = i_row + 1;
			}
			
			 inputStream.close();
			 
	         FileOutputStream outputStream = new FileOutputStream(file);
	         workbook.write(outputStream);
	         workbook.close();
	         outputStream.close();
					
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void writeSimilarityToExcelSheet(File file, String sheetName,
			List<SimilarityBean> lstBeans)
	{
		try
		{
			FileInputStream inputStream = new FileInputStream(file);
			Workbook wb = new XSSFWorkbook(inputStream);
			
			Sheet sheet = wb.getSheet(sheetName);
			if (sheet==null)
			{
				sheet = wb.createSheet(sheetName);
			}
			
			//Write the first row
			Row first_row = sheet.createRow(0);
			first_row.createCell(0).setCellValue("NO.");
			first_row.createCell(1).setCellValue("Date");
		    for(int i=0;i<lstBeans.size();i++)
		    {
		    	SimilarityBean sBean  = lstBeans.get(i);
		    	first_row.createCell(2+i).setCellValue(sBean.getBootName());
		    }
			
		    //write the following rows
		    Row row = null;
		    for(int i=0;i<lstBeans.size();i++)
		    {
		    	SimilarityBean sBean  = lstBeans.get(i);
		    	String dateName = sBean.getBootName();
		    	double[] arrValue = sBean.getSimilarity();
		    	
		    	row = sheet.createRow(i+1);
		    	row.createCell(0).setCellValue(i+1);
		    	row.createCell(1).setCellValue(dateName);
		    	for(int j=0;j<arrValue.length;j++)
		    	{
		    		row.createCell(j+2).setCellValue(arrValue[j]);
		    	}
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
	
	@Override
	public void writeSingleDayToExcelSheet(File file
			,SimilarityBean sBean
			,String[] dates)
	{
		try
		{
			FileInputStream inputStream = new FileInputStream(file);
			Workbook wb = new XSSFWorkbook(inputStream);
			Sheet sheet = wb.getSheet(sBean.getBootName());
			if (sheet==null)
			{
				sheet = wb.createSheet(sBean.getBootName());
			}
			XSSFCellStyle numberStyle = (XSSFCellStyle) wb.createCellStyle();
			numberStyle.setDataFormat(wb.createDataFormat().getFormat("0.000"));
			
			double[] arrValue  = sBean.getSimilarity();
			double[] sortValue = Arrays.copyOf(arrValue, dates.length);
			Arrays.sort(sortValue);
			int arrSize = dates.length;
			
			//Write the first row
			Row first_row = sheet.createRow(0);
			first_row.createCell(0).setCellValue("Date");
			first_row.createCell(1).setCellValue(sBean.getBootName());
			first_row.createCell(2).setCellValue(sBean.getBootName()+"(ASC)");
			first_row.createCell(3).setCellValue(sBean.getBootName()+" Frequency[%]");

			//write the following rows
		    for(int i=0;i<dates.length;i++)
		    {
		    	Row row = sheet.createRow(i+1);
		    	//
		    	Cell cell0 = row.createCell(0);
		    	cell0.setCellType(CellType.STRING);
		        cell0.setCellValue(dates[i]);
		        
		        Cell cell1 = row.createCell(1);
		    	cell1.setCellType(CellType.NUMERIC);
		        cell1.setCellValue(arrValue[i]);
		        
		        Cell cell2 = row.createCell(2);
		    	cell2.setCellType(CellType.NUMERIC);
		        cell2.setCellValue(sortValue[i]);
		        
		        Cell cell3 = row.createCell(3);
		        cell3.setCellStyle(numberStyle);
		        String fomular = "(1/" + arrSize + ")";
		        if (i==0)
		        {
		        	cell3.setCellFormula(fomular);
		        }else
		        {
		        	cell3.setCellFormula(fomular + "+D"+(i+1));
		        }
		       
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

	@Override
	public void writeKSTestToExcelSheet(File file, List<KSTestStatisticBean> ksListBeans)
	{
		String sheetName = "KSTestStatistic";
		try
		{
			FileInputStream inputStream = new FileInputStream(file);
			Workbook wb = new XSSFWorkbook(inputStream);
			Sheet sheet = wb.getSheet(sheetName);
			if (sheet==null)
			{
				sheet = wb.createSheet(sheetName);
			}
			XSSFCellStyle numberStyle = (XSSFCellStyle) wb.createCellStyle();
			numberStyle.setDataFormat(wb.createDataFormat().getFormat("0.000"));
			
			//Write the first row
			Row first_row = sheet.createRow(0);
			first_row.createCell(0).setCellValue("Date");
			first_row.createCell(1).setCellValue("Sum_PValue");
			first_row.createCell(2).setCellValue("RejectCount");

			//write the following rows
		    for(int i=0;i<ksListBeans.size();i++)
		    {
		    	KSTestStatisticBean kstBean = ksListBeans.get(i);
		    	Row row = sheet.createRow(i+1);
		    	//
		    	Cell cell0 = row.createCell(0);
		    	cell0.setCellType(CellType.STRING);
		        cell0.setCellValue(kstBean.getDateName());
		        
		        Cell cell1 = row.createCell(1);
		    	cell1.setCellType(CellType.NUMERIC);
		    	cell1.setCellStyle(numberStyle);
		        cell1.setCellValue(kstBean.getSumPValue());
		        
		        Cell cell2 = row.createCell(2);
		    	cell2.setCellType(CellType.NUMERIC);
		    	cell2.setCellStyle(numberStyle);
		        cell2.setCellValue(kstBean.getRejectCount());
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
	
	@Override
	public void copyNewExcelWithLessOne(String source_fileName,
			String removeStockName,int columnIdx)
	{
		int i_row = 0;
		int i_cel_old = 0;
		int i_cel_new = 0;
		try
		{
			FileInputStream inputStream = new FileInputStream(source_fileName);
			Workbook wb_org = new XSSFWorkbook(inputStream);
			Sheet sheet_org = wb_org.getSheet("price");

		    Workbook wb_new = new XSSFWorkbook();
		    Sheet sheet_new = wb_new.createSheet("price");
		    
			Iterator<Row> row_iterator = sheet_org.iterator();
			while (row_iterator.hasNext())
			{
				i_cel_old = 0;
				i_cel_new = 0;
				Row old_row	= row_iterator.next();
				Row new_row = sheet_new.createRow(i_row);
				Iterator<Cell> cellIterator = old_row.iterator();
				while (cellIterator.hasNext())
				{
					Cell old_cell = cellIterator.next();
					if (i_cel_old==columnIdx)
					{
						
					}else
					{
						Cell new_cell = new_row.createCell(i_cel_new);
						cloneCell(new_cell, old_cell);
						i_cel_new = i_cel_new + 1;
					}
					i_cel_old = i_cel_old + 1;
				}
				i_row = i_row + 1;
			}
			
		    // Write the output to a file
			OutputStream outputStream = new FileOutputStream(removeStockName);
			wb_new.write(outputStream);
		    
			wb_new.close();
		    outputStream.close();
		    wb_org.close();
		    inputStream.close();
			
		}catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}		
		
	}
	
	@Override
	public void deleteColumn(Sheet sheet, int columnToDelete) 
	{
	    for (int rId = 0; rId < sheet.getLastRowNum(); rId++) 
	    {
	        Row row = sheet.getRow(rId);
	        for (int cID = columnToDelete; cID < row.getLastCellNum(); cID++) 
	        {
	            Cell cOld = row.getCell(cID);
	            if (cOld != null) 
	            {
	                row.removeCell(cOld);
	            }
	            
	            Cell cNext = row.getCell(cID + 1);
	            
	            if (cNext != null) 
	            {
	                Cell cNew = row.createCell(cID, cNext.getCellType());
	                
	                cloneCell(cNew, cNext);
	                
	                //Set the column width only on the first row.
	                //Other wise the second row will overwrite the original column width set previously.
	                
	                if(rId == 0) 
	                {
	                    sheet.setColumnWidth(cID, sheet.getColumnWidth(cID + 1));
	                }
	            }
	        }
	    }
	}
	
	@Override
	public void writSimilarityRecord(File file, String sheetName,
			List<SimilarityRecord> similarityList)
	{
		System.out.println("Writing top similarity to Excel...");
		try
		{
			FileInputStream inputStream = new FileInputStream(file);
			Workbook wb = new XSSFWorkbook(inputStream);
			
			CreationHelper createHelper = wb.getCreationHelper();
			
			CellStyle cellStyleDate = wb.createCellStyle();
			cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));
			
			XSSFCellStyle numberStyle = (XSSFCellStyle) wb.createCellStyle();
			numberStyle.setDataFormat(wb.createDataFormat().getFormat("0.000000"));
			
			Sheet sheet = wb.getSheet(sheetName);
			if (sheet==null)
			{
				sheet = wb.createSheet(sheetName);
			}
			
			
			//Write the first row
			Row first_row = sheet.createRow(0);
			first_row.createCell(0).setCellValue("T_Date");
			first_row.createCell(1).setCellValue("Sim_Date");
			first_row.createCell(2).setCellValue("Sim_Value");

			//write the following rows
		    for(int i=0;i<similarityList.size();i++)
		    {
		    	SimilarityRecord tempBean = similarityList.get(i);
		    	Row row = sheet.createRow(i+1);
		    	//
		    	Cell cell0 = row.createCell(0);
		        cell0.setCellValue(tempBean.getTdate());
		        cell0.setCellStyle(cellStyleDate);
		        
		        Cell cell1 = row.createCell(1);
		        cell1.setCellValue(tempBean.getSim_date());
		        cell1.setCellStyle(cellStyleDate);
		        
		        Cell cell2 = row.createCell(2);
		    	cell2.setCellType(CellType.NUMERIC);
		    	cell2.setCellStyle(numberStyle);
		        cell2.setCellValue(tempBean.getSim_value());
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
	
	@Override
	public void writeExtremeValuesToExcel(File file, String sheetName,
			Map<CreditBean, List<String>> contribMap)
	{
		try
		{
			FileInputStream inputStream = new FileInputStream(file);
			Workbook wb = new XSSFWorkbook(inputStream);
			
			Sheet sheet = wb.getSheet(sheetName);
			if (sheet==null)
			{
				sheet = wb.createSheet(sheetName);
			}
			//Write the first row
			Row first_row = sheet.createRow(0);
			first_row.createCell(0).setCellValue("Date");
			first_row.createCell(1).setCellValue("Credits");
			first_row.createCell(2).setCellValue("Stock of Contribution");
			
			Row row = null;
			int rowIdx=0;
			for (Map.Entry<CreditBean, List<String>> entry : contribMap.entrySet()) 
			{
				CreditBean cb =  entry.getKey();
			    List<String> stocks  = entry.getValue();
			    for(int j=0;j<stocks.size();j++)
			    {
			    	row = sheet.createRow(rowIdx + 1);
			    	row.createCell(0).setCellValue(cb.gettDate());
			    	row.createCell(1).setCellValue(cb.getCreditValue());
			    	row.createCell(2).setCellValue(stocks.get(j));
			    	rowIdx = rowIdx + 1;
			    }
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

	private void cloneCell(Cell cNew, Cell cOld) 
	{
	    cNew.setCellComment(cOld.getCellComment());
	    //CellStyle cs_Old = cOld.getCellStyle();
	    //CellStyle cs_New = cNew.getCellStyle();
	    //cNew.setCellStyle(cOld.getCellStyle());
	    if (CellType.BOOLEAN == cOld.getCellType()) 
	    {
	        cNew.setCellValue(cOld.getBooleanCellValue());
	    } else if (CellType.NUMERIC == cOld.getCellType()) 
	    {
	        cNew.setCellValue(cOld.getNumericCellValue());
	    } else if (CellType.STRING == cOld.getCellType()) 
	    {
	        cNew.setCellValue(cOld.getStringCellValue());
	    } else if (CellType.ERROR == cOld.getCellType()) 
	    {
	        cNew.setCellValue(cOld.getErrorCellValue());
	    } else if (CellType.FORMULA == cOld.getCellType()) 
	    {
	        cNew.setCellValue(cOld.getCellFormula());
	    }else
	    {
	    	//cNew.setCellValue(cOld.get);
	    }
	}

	
	
}
