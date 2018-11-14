package downloadStock;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import detection.utility.DateUtil;

public class DownloadStocks
{
	private static String bootPath = "D:\\RMIT\\Master\\0Minor Thesis Project\\StockData\\Nasdaq\\";
	private String fileName   ="StocksList_LargeMarketCapitalization.xlsx";
	private Date   start_date = new Date();
	private Date   endof_date = new Date();
	private String url_prefix = "https://query1.finance.yahoo.com/v7/finance/download/";
	private String url_suffix = "";
	private int numbersOfStocks = 50;
	private List<UrlArgumentBean> url_arguments = new ArrayList<>();
	
	public DownloadStocks()
	{
		try
		{
			start_date = DateUtil.convertStringToDate("2014-05-19");
			endof_date = DateUtil.convertStringToDate("2018-09-27");
			
			UrlArgumentBean b1 = new UrlArgumentBean();
			b1.setKey("period1");
			b1.setValue(String.valueOf(start_date.getTime()).substring(0, 10));
			url_arguments.add(b1);
			
			UrlArgumentBean b2 = new UrlArgumentBean();
			b2.setKey("period2");
			b2.setValue(String.valueOf(endof_date.getTime()).substring(0, 10));
			url_arguments.add(b2);
			
			UrlArgumentBean b3 = new UrlArgumentBean();
			b3.setKey("interval");
			b3.setValue("1wk");
			url_arguments.add(b3);
			
			UrlArgumentBean b4 = new UrlArgumentBean();
			b4.setKey("events");
			b4.setValue("history");
			url_arguments.add(b4);
			
			UrlArgumentBean b5 = new UrlArgumentBean();
			b5.setKey("crumb");
			b5.setValue("qFIaFfFL5A2");
			url_arguments.add(b5);
			
			for(int i=0;i<url_arguments.size();i++)
			{
				UrlArgumentBean tempBean = url_arguments.get(i);
				url_suffix = url_suffix + tempBean.getKey() + "=" + tempBean.getValue()  +"&";
				//period1=1420030800&
			}
			url_suffix = url_suffix.substring(0,url_suffix.length() - 1);
		}catch(Exception exp)
		{
			;
		}
	}
	
	public static void main(String[] args)
	{
		String sectorName="";
		//sectorName ="Technology";
		//sectorName ="Financial";
		sectorName ="ConsumerGoods";
		//sectorName ="HealthCare";
		//sectorName ="IndustrialGoods";
		
		DownloadStocks selfObject = new DownloadStocks();
		selfObject.orgniseData(sectorName);
		
	}
	
	public void orgniseData(String sectorName)
	{
		//read stocks name
		List<String> lstStocks = readStocks(sectorName);
		
		String file = bootPath + fileName;
		
		String price_sheet_name= "price_" + sectorName;
		
		try
		{
			FileInputStream fis = new FileInputStream(file);
			Workbook workbook = new XSSFWorkbook(fis);
			Sheet sheet = workbook.getSheet(price_sheet_name);
			if(sheet == null)
			{
				sheet = workbook.createSheet(price_sheet_name);
			}
			//Write the first row
			Row first_row = sheet.createRow(0);
			for(int cell_idx=0; cell_idx<lstStocks.size(); cell_idx++)
			{
				if (cell_idx==0)
				{
					first_row.createCell(cell_idx).setCellValue("Date");
				}
				first_row.createCell(cell_idx+1).setCellValue(lstStocks.get(cell_idx));
			}
			
			XSSFCellStyle priceStyle = (XSSFCellStyle) workbook.createCellStyle();
			priceStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
			for(int stock_idx=0; stock_idx<lstStocks.size(); stock_idx++)
			{
				String stockName = lstStocks.get(stock_idx);
				importSingleStock(sectorName, stockName, sheet, stock_idx+1, priceStyle);
			}
		    // Write the output to a file
		    OutputStream outputStream = new FileOutputStream(file);
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
	
	public List<String> readStocks(String sectorName)
	{
		List<String> retValues = new ArrayList<>();
		int i_row = 0;
		int i_cel = 0;
		String file = bootPath + fileName;
		try
		{
			FileInputStream fis = new FileInputStream(file);
			Workbook workbook = new XSSFWorkbook(fis);
			Sheet sheet = workbook.getSheet(sectorName);
			Iterator<Row> row_iterator = sheet.iterator();
			while (row_iterator.hasNext())
			{
				if (i_row > numbersOfStocks)
				{
					break;
				}
				
				Row currentRow = row_iterator.next();
				if (i_row == 0)
				{
					i_row = i_row + 1;
					continue;
				}
				Cell currentCell = currentRow.getCell(0);
				CellType ct = currentCell.getCellType();
				if (ct ==CellType.STRING)
				{
					retValues.add(currentCell.getStringCellValue());
				}
				i_row = i_row + 1;
			}
			workbook.close();
			fis.close();
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		return retValues;
	}
	
	
	private void importSingleStock(String sectorName, String stockName, Sheet arg_sheet, int cellIdx, XSSFCellStyle argStyle)
	{
//		if (stockName.equals("JD"))
//		{
//			System.out.println(stockName);
//		}
		
		int i_row = 1;
		
		String file = bootPath +sectorName + "\\" + stockName +".csv";
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(file));
			// =================================//
			String line;
			line = br.readLine();
			while ((line = br.readLine()) != null)
			{
				String[] data = line.split(",");
				String stockDate = data[0];
				Date   stockDate_d = DateUtil.convertStringToDate(stockDate);
				if (stockDate_d.getTime() < start_date.getTime())
				{
					continue;
				}
				if (data[4].equals("88.19"))
				{
					System.out.println(file);
				}
				//
				//Row argRow = arg_sheet.createRow(i_row);
				Row argRow = arg_sheet.getRow(i_row);
				if (argRow==null)
				{
					argRow = arg_sheet.createRow(i_row);
				}
				
				Cell argCell0   = argRow.getCell(0);
				if (argCell0 == null)
				{
					argCell0 = argRow.createCell(0);
					argCell0.setCellValue(stockDate);
				}
				//
				String idxDate   = argCell0.getStringCellValue();
				String price     = data[4];
				if (idxDate.equals(stockDate))
				{
					Cell argCellIdx = argRow.getCell(cellIdx);
					if (argCellIdx==null)
					{
						argCellIdx = argRow.createCell(cellIdx);
					}
					argCellIdx.setCellType(CellType.NUMERIC);
					argCellIdx.setCellStyle(argStyle);
					BigDecimal bd = new BigDecimal(new Double(price)); 
				    bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP); 
					argCellIdx.setCellValue(bd.doubleValue());
				}
				i_row = i_row + 1;
			}
			// =================================//
		} catch (Exception exp)
		{
			exp.printStackTrace();
		} finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	

	
	public void constructDownLoadUrl(String sectorName)
	{
		List<String> lstStocks = readStocks(sectorName);
		
		String url_file = bootPath + sectorName +  "url.txt";
		//File file = new File(url_file);
		try
		{
			FileWriter writer=new FileWriter(url_file,true);
			String url ="";
			for(int i=0;i<lstStocks.size();i++)
			{
				//https://query1.finance.yahoo.com/v7/finance/download/MSFT?period1=1420030800&period2=1535637600&interval=1wk&events=history&crumb=qFIaFfFL5A2
				url = url_prefix + lstStocks.get(i) + "?" + url_suffix;
				writer.write(url);
				writer.write("\n");
			}
			writer.close();
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
}
