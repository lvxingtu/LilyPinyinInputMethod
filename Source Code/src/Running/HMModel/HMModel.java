package Running.HMModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;

class NewComparator<ST> implements Comparator<LinkedList<ST>>{
	private HashMap<LinkedList<ST>, Double> maps;
	NewComparator(HashMap<LinkedList<ST>, Double> m){
		this.maps = m;
	}
	@Override
	public int compare(LinkedList<ST> arg0, LinkedList<ST> arg1) {
		// TODO Auto-generated method stub
		double d0 = maps.get(arg0), d1 = maps.get(arg1);
		if(d0 > d1)
			return -1;
		if(d0 < d1)
			return 1;
		return 0;
	}
	
}

/**
 * 
 * @author lxq
 *
 * @param <OT> �۲�ֵ������
 * @param <ST> ״ֵ̬������
 */
public class HMModel<OT, ST> {
	private ArrayList<Layer> layers;
	private TransitionMatrix<ST> tm;
	private ConfusionMatrix<ST, OT> cm;
	private ArrayList<Double> initialVector;
	private double observePInModel;
	
	/**
	 * 
	 * @param t ת�ƾ���
	 * @param c ��������
	 * @param iv ��ʼ����
	 * @param fir ��һ���۲�ֵ
	 * @param firStatuses ��һ���۲�ֵ��Ӧ��״ֵ̬ ע��Ҫ����ʼ����һһ��Ӧ
	 */
	public HMModel(TransitionMatrix<ST> t, ConfusionMatrix<ST, OT> c, ArrayList<Double> iv, OT fir, Iterator<ST> firStatuses){
		this.tm = t;
		this.cm = c;
		this.initialVector = iv;
		this.layers = new ArrayList<Layer>();
		this.layers.add(new Layer(fir, firStatuses));
	}
	public HMModel(HMModel<OT, ST> model){
		this.cm = model.cm;
		this.tm = model.tm;
		this.initialVector = model.initialVector;
		this.layers = new ArrayList<Layer>();
		for(Layer l : model.layers){
			this.layers.add(new Layer(l));
		}
		this.observePInModel = model.observePInModel;
	}
	
	/**
	 * add layers to the HMM
	 * @param obs the new observed value
	 * @param iter  its relative status value 
	 */
	public void addLayer(OT obs, Iterator<ST> iter){
		int index = layers.size();
		layers.add(new Layer(obs, iter, index));
		this.observePInModel = layers.get(layers.size() - 1).sumObsPOfLayer;
	}
		
	public int numOfLayer(){
		return layers.size();
	}
	
	/**
	 * return the optimal sequence in the current model
	 * @return
	 */
	public LinkedList<ST> bestStatusSequence(){
		Layer lastLayer = this.layers.get(this.layers.size() - 1);
		ListIterator<Layer> iter = layers.listIterator(layers.size());
		LinkedList<ST> ret = new LinkedList<ST>();
		int maxIndex = lastLayer.maxViterbiIndex;
		while(iter.hasPrevious()){
			Layer l = iter.previous();
			Layer.StatusNode sn = l.statuses.get(maxIndex);
			ret.addFirst(sn.status);
			maxIndex = sn.inversePointer;
		}
		return ret;
	}
	
	private double getUnionP(Layer.StatusNode sn1, Layer.StatusNode sn2){
		return sn1.observePLocalValue * this.tm.transitionProbability(sn1.status, sn2.status);
	}
	
	/**
	 * ��HMM������ʱ���غ������п���·�������ȼ����� ���������㣬����null
	 * @return
	 */
	public PriorityQueue<LinkedList<ST>> rankedStatusPairsListIn2Layers(){
		if(layers.size() != 2){
			return new PriorityQueue<LinkedList<ST>>();
		}
		Layer l1 = layers.get(0), l2 = layers.get(1);
		HashMap<LinkedList<ST>, Double> maps = new HashMap<LinkedList<ST>, Double>();
		PriorityQueue<LinkedList<ST>> ret = new PriorityQueue<LinkedList<ST>>(
				l1.statuses.size() * l2.statuses.size(), new NewComparator<ST>(maps));
		for(Layer.StatusNode sn1 : l1.statuses){
			for(Layer.StatusNode sn2 : l2.statuses){
				LinkedList<ST> temp = new LinkedList<ST>();
				temp.add(sn1.status);
				temp.add(sn2.status);
				maps.put(temp, getUnionP(sn1, sn2));
				ret.add(temp);
			}
		}
		return ret;
	}
	
	/**
	 * get the probability of the sequence of observed values given this hidden Markov Model.
	 * @return the probability
	 */
	public double observeProbabilityInModel(){
		return this.observePInModel;
	}
	
	class Layer{
		private OT observ;
		private ArrayList<StatusNode> statuses;
		private int layerNum;
		private double sumObsPOfLayer;
		private int maxViterbiIndex;
		
		//֮��Ĳ��ʹ���������
		Layer(OT obs, Iterator<ST> iter, int layerN){
			this.observ = obs;
			this.layerNum = layerN;
			this.statuses = new ArrayList<StatusNode>();
			double max = 0.0;
			int index = 0;
			while(iter.hasNext()){
				ST temp = iter.next();
				StatusNode node = new StatusNode(temp);
				this.sumObsPOfLayer += node.observePLocalValue;
				if(node.viterbiLocalValue > max){
					max = node.viterbiLocalValue;
					this.maxViterbiIndex = index;
				}
				statuses.add(node);
				index++;
			}
		}
		
		//HMM��һ��ʹ�������ʼ��
		Layer(OT obs, Iterator<ST> iter){
			this.layerNum = 0;
			this.observ = obs;
			this.statuses = new ArrayList<StatusNode>();
			int index = 0;
			double max = 0.0;
			while(iter.hasNext()){
				ST temp = iter.next();
				StatusNode node = new StatusNode(temp, HMModel.this.initialVector.get(index));
				this.sumObsPOfLayer += node.observePLocalValue;    
				if(node.viterbiLocalValue > max){
					max = node.viterbiLocalValue;
					this.maxViterbiIndex = index;
				}
				statuses.add(node);
				index++;
			}
		}
		
		Layer(Layer l){
			this.observ = l.observ;
			this.statuses = new ArrayList<StatusNode>();
			for(StatusNode sn : l.statuses){
				this.statuses.add(new StatusNode(sn));
			}
			this.layerNum = l.layerNum;
			this.maxViterbiIndex = l.maxViterbiIndex;
			this.sumObsPOfLayer = l.sumObsPOfLayer;
		}
		class StatusNode{
			private ST status;
			private double observePLocalValue;
			private double viterbiLocalValue;
			private int inversePointer;
			StatusNode(ST st){
				this.status = st;
				if(HMModel.Layer.this.layerNum > 0){
					Layer lastLayer = HMModel.this.layers.get(HMModel.Layer.this.layerNum - 1);
					
					//����۲�ֵ�ľֲ�����
					observePLocalValue = lastLayer.sumObsPOfLayer 
							* HMModel.this.cm.transformProbability(this.status, HMModel.Layer.this.observ);
					this.viterbiLocalValue = 0.0;
					
					//����Viterbi�㷨�ľֲ�����
					int index = 0;
					this.viterbiLocalValue = 0.0;
					for(StatusNode sn : lastLayer.statuses){
						double value = sn.viterbiLocalValue * HMModel.this.tm.transitionProbability(sn.status, this.status) *
								HMModel.this.cm.transformProbability(this.status, HMModel.Layer.this.observ);
						if(value > this.viterbiLocalValue){
							this.viterbiLocalValue = value;
							this.inversePointer = index;
						}
						index++;
					}
				}
			}
			StatusNode(ST st, double pi){
				this.status = st;
				this.observePLocalValue = pi * HMModel.this.cm.transformProbability(this.status, HMModel.Layer.this.observ);
				this.viterbiLocalValue = pi * HMModel.this.cm.transformProbability(this.status, HMModel.Layer.this.observ);
			}
			StatusNode(StatusNode sn){
				this.status = sn.status;
				this.observePLocalValue = sn.observePLocalValue;
				this.viterbiLocalValue = sn.viterbiLocalValue;
				this.inversePointer = sn.inversePointer;
			}
		}
	}
}
