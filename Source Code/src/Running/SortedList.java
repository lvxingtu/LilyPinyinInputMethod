package Running;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * ʼ�ձ���SortedList���򣬰�Word.frequency��������
 */
public class SortedList implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LinkedList<Word> list;
	public SortedList(){
		list = new LinkedList<Word>();
	}
	
	/**
	 * ���list������������ʣ��򽫶�ӦWord����frequency����1����������λ�ã������Ҫ�������list����û��������ʣ����������ʵ���β
	 * @param c
	 */
	public void add(char c){
			Word w;
			int i;
			for (i = 0; i < list.size(); i++) {
				w = list.get(i);
				if (w.getWord() == c) {
					w.increase();
					list.remove(i);
					if (i == 0) {
						list.add(0, w);
						break;
					} else {
						for (int j = i - 1; j >= 0; j--) {
							list.get(j);
							if (list.get(j).getFrequency() >= w.getFrequency()) {
								list.add(j + 1, w);
								break;
							}
							if (j == 0) {
								list.add(0, w);
							}
						}
						break;
					}
				}
			}

			if(i == list.size())
				list.add(new Word(c));
		
	}
	
	public void add(Word w){
		char c = w.getWord();
		int frequency = w.getFrequency();
		for(int i = 0; i < list.size(); i++){
			Word ww = list.get(i);
			if(ww.getWord() == c)
				return;
			if(ww.getFrequency() < frequency){
				list.add(i, w);
				return;
			}
		}
		list.add(w);
	}
	public void clear(){
		list = new LinkedList<Word>();
	}
	public Iterator<Word> iterator(){
		return list.iterator();
	}
	public SortedList(SortedList nl){
		this.list = new LinkedList<Word>(nl.list);
	}
	public int size(){
		return list.size();
	}
	public static void main(String[] args){
		SortedList l = new SortedList();
		l.add('��');
		l.add('��');
		l.add('��');
		l.add('ѧ');
		l.add('ϰ');
		l.add('��');
		l.add('��');
		l.add('��');
		l.add('��');
		l.add('��');
		l.add('��');
		l.add('��');
		l.add('��');
		l.add('��');
		l.add('��');
		l.add('��');
		Iterator<Word> iter = l.iterator();
		while(iter.hasNext()){
			Word w = iter.next();
			System.out.println(w.getWord() + " " + w.getFrequency());
		}
	}
}
