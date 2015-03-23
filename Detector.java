package terry.train.ProfanityDetector;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * 髒話篩檢程式
 * @author Terry_Liu
 *
 */
public class Detector {
	private HashMap<String, ArrayList<String>> profanityDic;  // 可能的髒話
	private HashMap<String, Integer> maxSentCount;
	private LinkedList<String> punctuationQueue;
	private ArrayList<String> punctuation;
	
	/**
	 * 進行物件的初始化。
	 */
	public void init(){
		profanityDic = new HashMap<String, ArrayList<String>>();
		maxSentCount = new HashMap<String, Integer>();
		punctuationQueue = new LinkedList<String>();
		punctuation = new ArrayList<String>(Arrays.asList(new String[]{"~", "～", "?", "？", ",", ".", "!", "！", "，", "。", " ", ":", "：", "、"}));
	}
	
	/**
	 * 讀取髒話資料文件，建立profanityDic與maxSentCount。
	 * @param filePath 文件的路徑。
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void readProfanityData(String filePath) throws FileNotFoundException, IOException{
		String line = null;
		BufferedReader reader;
		String nowKeyString = null;
		reader = new BufferedReader(new FileReader(filePath));
		
		while((line = reader.readLine()) != null){
			if(line.charAt(0)=='\t'){
				line = line.replace("\t", "");
				profanityDic.get(nowKeyString).add(line);
				maxSentCount.put(nowKeyString, Math.max(maxSentCount.get(nowKeyString), line.length()));
				Collections.sort(profanityDic.get(nowKeyString)); // for binary search
			}
			else{
				nowKeyString = line;
				profanityDic.put(nowKeyString, new ArrayList<String>());
				maxSentCount.put(nowKeyString, 0);  // maximum size of the list
			}
		}
		
		reader.close();
	}
	
	
	/**
	 * 判斷所輸入的字串是否包含髒話。
	 * @param input 輸入參數是使用者所輸入的字串。
	 * @return 原始字串經過處理後的結果字串。
	 */
	public String judge(String input){
		String possibleToken;
		String output = "";
		
		for(int i=0; i<input.length(); i++){
			possibleToken = "" + input.charAt(i);
			//System.out.println(possibleToken);
			if(profanityDic.containsKey(possibleToken)){
				possibleToken = check(possibleToken, i, input);
				i += (possibleToken.length()-1);
				punctuationQueue.clear();
			}
			output += possibleToken;
		}
		
		return output;
	}
	
	/**
	 * 更進一步搜尋字串是否包含髒話。
	 * @param input 可能的髒話字。
	 * @param idx 此字在原始字串的index。
	 * @param originalStr 原始字串。
	 * @return 處理過後的字串。
	 */
	private String check(String input, int idx, String originalStr){
		int maxLoopCount = maxSentCount.get(input);
		String key = input;

		if(	Collections.binarySearch(profanityDic.get(input), "self") >= 0){
			String restStr = originalStr.substring(idx);
			int lengthAfterReplace = restStr.replaceFirst("^" + input + "{3,}", "*").length();
			//檢查是否有重複出現3次以上的單個髒話
			if(lengthAfterReplace < restStr.length()){
				return getAsterisk(restStr.length() - lengthAfterReplace + 1);
			}
				
			
			//檢查是否為單個國字的髒話。可能性有4種：1. 可能會單用     2. 前後都有空白或者標點符號   3. 其在句尾且前面有空白或標點符號   4. 其在句首且後面有空白或標點符號 
			if(	input.length() == originalStr.length() || 
				((idx > 0 && idx < originalStr.length()-1) && 
						(punctuation.contains(""+originalStr.charAt(idx+1)) && 
								punctuation.contains(""+originalStr.charAt(idx-1)) ) ) ||
				(idx == 0 && punctuation.contains(""+originalStr.charAt(idx+1))) ||
				(idx == originalStr.length()-1 && punctuation.contains(""+originalStr.charAt(idx-1)))
				){
				return getAsterisk(input.length());
			}
		}
		
		// 一般情形。去查Dictionary以判斷是否有髒話
		while(input.length() <= maxLoopCount){
			//把標點符號記起來，最後再拼回去
			if(punctuation.contains("" + input.charAt(input.length()-1))){
				punctuationQueue.set(0, punctuationQueue.getFirst() + input.charAt(input.length()-1));
				input = input.substring(0,input.length()-1);
			}
			else
				punctuationQueue.addFirst("");
			
			if(Collections.binarySearch(profanityDic.get(key), input) >= 0){
				return getAsterisk(input.length());
			}
			if(idx == originalStr.length()-1) break; else input += originalStr.charAt(++idx);
		}

		return key;   //no match
	}
	
	
	/**
	 * 輸出星號字串。
	 * @param numOfStar 要產生的星號數量。
	 * @return 星號字串。
	 */
	private String getAsterisk(int numOfStar){ 
		String output = "";
		for(int i=0; i < numOfStar; i++){
			output += "*";
			if(punctuationQueue.size()!=0)	output += punctuationQueue.removeLast();
		}
		return output;
	}
}
