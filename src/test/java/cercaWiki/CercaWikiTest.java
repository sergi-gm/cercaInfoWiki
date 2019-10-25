package cercaWiki;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sgallego.aia.cercaWiki.CercaWiki;

public class CercaWikiTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("EMPIEZA TEST");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("FIN TEST");
	}
	
	@Test
	public void test() {
		String[] args = null;
		CercaWiki.main(args);
	}

}
