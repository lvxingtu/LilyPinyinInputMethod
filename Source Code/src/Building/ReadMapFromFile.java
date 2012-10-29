package Building;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

class Res{
	static WordLib Word2PY = new WordLib();
}

public class ReadMapFromFile {
	/**
     * ���ַ�Ϊ��λ��ȡ�ļ��������ڶ��ı������ֵ����͵��ļ�
     */
    public static void readFileByChars(String fileName) {
        File file = new File(fileName);
        Reader reader = null;
    
        try {
            // һ�ζ�һ���ַ�
            reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            char[] pinyin = new char[10];
            int index = 0;
            String curstr = null;
            while ((tempchar = reader.read()) != -1) {
            	if (((char) tempchar) == '\r' || ((char) tempchar) == '\n' || ((char) tempchar) == ' ')
            	{
            		if (index != 0)
            		{
	            		curstr = (String.valueOf(pinyin)).substring(0, index);
	            		index = 0;
            		}
            	}
            	else if (((char) tempchar) >= 'a' && ((char) tempchar) <= 'z')
            	{
            		pinyin[index] = (char) tempchar;
            		++index;
            	}
            	else if (((char) tempchar) == '\'') {
            		// ��ʼ��ȡ��������
            		while((tempchar = reader.read()) != '\'')
            		{
            			Res.Word2PY.add((char)tempchar, curstr);
            			Process.TREE.add(curstr, (char)tempchar);
            		}
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
	    String fileName = "pinyin_word.txt";
	    ReadMapFromFile.readFileByChars(fileName);
	    ArrayList<String> l = Res.Word2PY.getPinyin('��');
	    for(String s : l){
	    	System.out.println(s + " " + s.length());
	    }
    }
}




