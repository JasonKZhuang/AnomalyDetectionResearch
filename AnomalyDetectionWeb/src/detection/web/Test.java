package detection.web;

public class Test
{
	public int a;

	public int b;

	public int getA()
	{
		return a;
	}

	public void setA(int a)
	{
		this.a = a;
	}

	public int getB()
	{
		return b;
	}

	public void setB(int b)
	{
		this.b = b;
	}

	public Test(int a, int b)
	{
		super();
		this.a = a;
		this.b = b;
		System.out.println("=========construct method =========");
	}

	public void init()
	{
		System.out.println("====init====");
	}

	public void destroy()
	{
		System.out.println("====destroy====");
	}

}
