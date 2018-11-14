package detection.fileOperation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import detection.algorithms.kaizhi.RecordBean;
import detection.algorithms.kaizhi.SimilarityBean;
import detection.beans.StockName;
import detection.beans.StockRecord;
import detection.utility.DateUtil;

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
				// System.out.println(item);
				File f = new File(path + "/" + item);
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
				} catch (Exception exp)
				{
					record.setStockName("");
				}

				try
				{
					record.setTdate(
							DateUtil.convertStringToDate(arrayLine[0].trim()));
				} catch (Exception exp)
				{
					record.setTdate(null);
				}

				try
				{
					record.setOpen(Float.parseFloat(arrayLine[1]));
				} catch (Exception exp)
				{
					record.setOpen(0);
				}

				try
				{
					record.setHigh(Float.parseFloat(arrayLine[2]));
				} catch (Exception exp)
				{
					record.setHigh(0);
				}

				try
				{
					record.setLow(Float.parseFloat(arrayLine[3]));
				} catch (Exception exp)
				{
					record.setLow(0);
				}

				try
				{
					record.setClose(Float.parseFloat(arrayLine[4]));
				} catch (Exception exp)
				{
					record.setClose(0);
				}
				try
				{
					record.setVolume(Long.parseLong(arrayLine[5]));
				} catch (Exception exp)
				{
					record.setVolume(0);
				}
				retList.add(record);
			}
			fileReader.close();
			// System.out.println("Reading file finished.");
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return retList;
	}

	@Override
	public List<StockName> getStocksDetailFromFile(File file)
	{
		List<StockName> retList = new ArrayList<>();
		BufferedReader br = null;
		try
		{
			br = new BufferedReader(new FileReader(file));
			// =================================//
			String line;
			br.readLine();
			int i = 0;
			while ((line = br.readLine()) != null)
			{
				// line = line.replace(", Inc.", " Inc.");
				// line = line.replace(", Incorporated.", " Incorporated");

				// line = line.replace("\"", "");
				line = line.substring(1, line.length() - 2);
				String[] data = line.split("\",\"");
				StockName sn = new StockName();
				// 0
				sn.setSymbol(data[0]);
				// 1
				sn.setRealName(data[1]);
				// 2
				// 3
				String marketCap = data[3];
				double d_cap = 0d;
				try
				{
					if (marketCap.equals("n/a"))
					{
						System.out.println("null...n/a");
					}
					d_cap = Double.parseDouble(marketCap);
				} catch (Exception exp)
				{
					exp.printStackTrace();
					d_cap = 0;
				}

				sn.setMarketCap(d_cap);
				// 4
				// 5
				// 6
				sn.setSector(data[6]);
				// 7
				sn.setIndustryGroup(data[7]);
				retList.add(sn);
				i++;
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

    public static void main(String[] args)
    {
            
    }
    
    public static void writeMethod1(String fileName,String contect)
    {
	    try
	    {
	            //使用这个构造函数时，如果存在kuka.txt文件，
	            //则先把这个文件给删除掉，然后创建新的kuka.txt
	            FileWriter writer=new FileWriter(fileName);
	            writer.write(contect);
	            writer.close();
	    } catch (IOException e)
	    {
	            e.printStackTrace();
	    }
    }
    
    public static void writeMethod2(String fileName,String contect)
    {
        try
        {
            //使用这个构造函数时，如果存在kuka.txt文件，
            //则直接往kuka.txt中追加字符串
            FileWriter writer=new FileWriter(fileName,true);
            writer.write(contect);
            writer.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    //注意：上面的例子由于写入的文本很少，使用FileWrite类就可以了。但如果需要写入的
    //内容很多，就应该使用更为高效的缓冲器流类BufferedWriter。
    /**
     * 使用BufferedWriter类写文本文件
     */
    public static void writeMethod3(String fileName,String contect)
    {
        try
        {
            BufferedWriter out=new BufferedWriter(new FileWriter(fileName));
            out.write(contect);
            out.newLine();  //注意\n不一定在各种计算机上都能产生换行的效果
            out.close();
        } catch (IOException e)
        {
                // TODO Auto-generated catch block
                e.printStackTrace();
        }
    }
    /**
     * 使用FileReader类读文本文件
     */
    public static void readMethod1()
    {
            String fileName="C:/kuka.txt";
            int c=0;
            try
            {
                    FileReader reader=new FileReader(fileName);
                    c=reader.read();
                    while(c!=-1)
                    {
                            System.out.print((char)c);
                            c=reader.read();
                    }
                    reader.close();
            } catch (Exception e) {
                    e.printStackTrace();
            }
    }
    
    /**
     * 使用BufferedReader类读文本文件
     */
    public static void readMethod2()
    {
            String fileName="c:/kuka.txt";
            String line="";
            try
            {
                    BufferedReader in=new BufferedReader(new FileReader(fileName));
                    line=in.readLine();
                    while (line!=null)
                    {
                            System.out.println(line);
                            line=in.readLine();
                    }
                    in.close();
            } catch (IOException e)
            {
                    e.printStackTrace();
            }
    }
	
	
}
