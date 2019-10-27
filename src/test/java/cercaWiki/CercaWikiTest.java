package cercaWiki;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

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
		String[] args = new String[2];
		//Fichero entrada
		args[0] = "C:\\Users\\Sergi_vaio\\AIA\\first10000 (1).txt";
		//Fichero salida
		args[1] = "C:\\Users\\Sergi_vaio\\AIA\\salida_first10000.csv";
		try {
			CercaWiki.main(args);
		}
		catch (Exception e) {
			System.out.println("ERROR: " +e);
		}
	}

}
