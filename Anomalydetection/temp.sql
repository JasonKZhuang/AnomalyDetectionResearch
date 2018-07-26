SELECT * FROM myProject.stock;

SET SQL_SAFE_UPDATES = 0;




select t.stockName , count(t.stockName) as recordCount from myProject.stock t
group by t.stockName
order by recordCount;
