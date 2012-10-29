package Building;

import java.util.ArrayList;
import java.util.HashMap;

public class WordLib {
        
	public WordLib() {
                map = new HashMap<Character, ArrayList<String>>();
	}

	/**
	 * @author Yanan Zhang
	 */
	private HashMap<Character, ArrayList<String>> map;

        /**
         * �����������Ӧ��ƴ�����뵽����ֵ��У�������Ҫ������
         * @param word Ҫ����ĵ�������
         * @param pinyin ������ֶ�Ӧ��ƴ��
        **/ 
	public void add(char word, String pinyin) {
		ArrayList<String> list; 
		if (map.containsKey(word)) {
			list = map.get(word);
			list.add(pinyin);
			map.put(word, list);
		} else {
                        list = new ArrayList<String>();
			list.add(pinyin);
			map.put(word, list);
		}
	}
        /**
         * @param word ���뵥���ĺ���
         * return �ú��ֶ�Ӧ��ƴ���б�����Ҳ����ú��ֵĻ�����null�������ַ��ض��ƴ���������ķ���һ��ƴ����
        **/
	public ArrayList<String> getPinyin(char word) {
                ArrayList<String> list = null;
		if (map.containsKey(word)) {
			list = map.get(word);
		}
		return list;
	}
	public static void main(String[] args){
		WordLib l = new WordLib();
		l.add('��', "man");
		l.add('��', "man");
		l.add('��', "hao");
		ArrayList<String> res = l.getPinyin('��');
		for(String s : res){
			System.out.println(s);
		}
		res = l.getPinyin('��');
		for(String s : res){
			System.out.println(s);
		}
	}
}
