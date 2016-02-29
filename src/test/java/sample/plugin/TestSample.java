package sample.plugin;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class TestSample {
	
	@Test
	@Id("1.2.1")
	@Category (TST2.class)
	@Document("DocID here, Ed 02")
	public void test1() {
		Assert.assertTrue(true);
	}
	
	@Test
	@Id("1.2.2")
	@Document("DocID here, Ed 02")
	@Category (TST.class)
	public void test2() {
		Assert.assertTrue(true);
	}
	
	@Before
	public void before() {
		System.out.println("before");
	}
}
