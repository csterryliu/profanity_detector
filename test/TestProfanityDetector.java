package terry.train.ProfanityDetector.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import static org.junit.Assert.*;
import org.junit.*;

import terry.xpec.train.ProfanityDetector.Detector;

/**
 * 測試髒話篩檢程式
 * @author Terry_Liu
 *
 */
public class TestProfanityDetector {

	private Detector detector;
	
	
	/**
	 * 測試前的初始化。
	 */
	@Before
	public void SetUp(){
		detector = new Detector();
		detector.init();
		try {
			detector.readProfanityData("data\\badLanguages_cht.txt");
			detector.readProfanityData("data\\badLanguages_eng.txt");
		} catch (FileNotFoundException e) {
			System.out.println("The file of profanity data is not found.");
		}
		catch (IOException e) {
			System.out.println("IOException is thrown when reading the file.");
		}
	}
	
	
	/**
	 * 測試完後將物件標記成null，讓GC回收。
	 */
	@After
	public void tearDown(){
		detector = null;
	}
	
	
	/**
	 * 測試字串中只有髒話和可能的標點符號的情形。
	 */
	@Test
	public void testGeneralIssue(){  //一般情形：只打髒話+標點符號
		//沒有標點符號
		String input = "幹你娘";  
		String expected = "***";
		String actual = detector.judge(input);
		assertEquals(expected, actual);
		
		//有標點符號
		input = "操你媽！";  
		expected = "***！";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "王 八 蛋";
		expected = "* * *";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		//前後有空白或多個標點符號
		input = "??!!#$#%$#@$%$@  幹妳老師!!.！，!!!!!!，!!!!!!!!!!!!";   
		expected = "??!!#$#%$#@$%$@  ****!!.！，!!!!!!，!!!!!!!!!!!!";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
	}
	
	/**
	 * 測試髒話與其他字夾雜的情形。
	 */
	@Test
	public void testMixIssue(){  // 髒話與其他字夾雜的情形
		String input = "幹你娘勒 是怎樣";  
		String expected = "***勒 是怎樣";
		String actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "超生氣 操你媽 什麼啦";   
		expected = "超生氣 *** 什麼啦";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		//夾雜多種髒話
		input = "媽的 幹妳老師 是怎樣? 雞掰!";     
		expected = "** **** 是怎樣? **!";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "雞掰 幹你娘";     
		expected = "** ***";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "看我用標點符號隔開 王！八！蛋！";
		expected = "看我用標點符號隔開 *！*！*！";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "王！八！蛋幹幹幹幹幹幹幹幹 雞掰啦！";
		expected = "*！*！********* **啦！";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
	}
	
	
	/**
	 * 測試含有單個髒話字的情形。
	 */
	@Test 
	public void testSingleWord(){
		//單字髒話
		String input = "幹";  
		String expected = "*";
		String actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "幹~";  
		expected = "*~";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		//單字髒話 在句中且前後都有空白或標點
		input = "怎樣啦 操 煩死了";
		expected = "怎樣啦 * 煩死了";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "幹 怎樣啦，幹，煩死了！";
		expected = "* 怎樣啦，*，煩死了！";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "怎樣啦？幹！煩死了...幹";
		expected = "怎樣啦？*！煩死了...*";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "怎樣啦 幹！煩死了...";
		expected = "怎樣啦 *！煩死了...";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		//單字髒話 在句尾
		input = "怎樣啦 幹";
		expected = "怎樣啦 *";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "怎樣啦!幹";
		expected = "怎樣啦!*";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		
		//單字髒話 在句首
		input = "幹 是怎樣";
		expected = "* 是怎樣";
		actual = detector.judge(input);
		assertEquals(expected, actual);
				
		input = "幹....是怎樣啦";
		expected = "*....是怎樣啦";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		//重複多次單字髒話
		input = "幹幹幹幹幹幹幹幹幹幹幹幹";
		expected = "************";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "幹!輸了啦 幹幹幹幹幹幹幹幹";
		expected = "*!輸了啦 ********";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "幹幹幹幹幹 輸了啦 煩";
		expected = "***** 輸了啦 煩";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "什麼鬼？幹幹幹幹幹按幹幹幹幹幹幹幹";   
		expected = "什麼鬼？*****按*******";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
	}
	
	
	/**
	 * 測試英文髒話，包括中英夾雜的情形。
	 */
	@Test
	public void testEnglish(){
		// 測試英文髒話
		String input = "You are a bitch!";  
		String expected = "You are a *****!";
		String actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "It's fucking awesome!";   
		expected = "It's ****ing awesome!";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "motherfucking bitch";   
		expected = "************* *****";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "He's just a motherfucker.";   
		expected = "He's just a ************.";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		// 中英夾雜
		input = "妳這個bitch";   
		expected = "妳這個*****";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "幹！我的心情fucked up!";   
		expected = "*！我的心情****ed up!";
		actual = detector.judge(input);
		assertEquals(expected, actual);
	}
	
	/**
	 * 有些可能的髒話字不是髒話，不應該變成星號。此方法便測試此情形。例如「幹什麼」，不應該變成「*什麼」。
	 */
	@Test
	public void testSafety(){  //一些國字可能不是髒話 不應該變星號
		String input = "幹活去吧";
		String expected = "幹活去吧";
		String actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "無尾熊最喜歡爬樹幹，書上說的";
		expected = "無尾熊最喜歡爬樹幹，書上說的";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "要幹 吃飽了再幹";
		expected = "要幹 吃飽了再幹";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "要幹，吃飽了再幹！";
		expected = "要幹，吃飽了再幹！";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "是否要做體操？";
		expected = "是否要做體操？";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "要做體操嗎？";
		expected = "要做體操嗎？";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "你幹不幹？";
		expected = "你幹不幹？";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
		input = "幹嘛？";
		expected = "幹嘛？";
		actual = detector.judge(input);
		assertEquals(expected, actual);
		
	}
	
	/*public static void main(String[] args) {
		Detector detector = new Detector();
		String input = "王！八！蛋幹幹幹幹幹幹幹幹 雞掰啦！";
		//String input;
		System.out.println("髒話篩檢程式");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			detector.init();
			detector.readProfanityData("data\\badLanguages_cht.txt");
			detector.readProfanityData("data\\badLanguages_eng.txt");
			//while(true){
				//System.out.print("請輸入一句話：");
				//input = br.readLine();
				System.out.println(detector.judge(input));
			//}
		} catch (FileNotFoundException e) {
			System.out.println("The file of profanity data is not found.");
		}
		catch (IOException e) {
			System.out.println("IOException is thrown when reading the file.");
		}
		
		
		
	}*/

}
