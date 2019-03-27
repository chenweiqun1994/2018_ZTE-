package Dijkstra;

import java.util.ArrayList;//动态数组
import java.util.HashMap;
import java.util.LinkedList;//双向链表
import java.util.List;
import java.util.Map;

public class Chromosome implements Cloneable{
	private  List<Business> business = new LinkedList<Business>();//什么意思？生成什么？
	private static int businessNum = 1000;
	private  Map <Key,Edge> edges = new HashMap<Key,Edge>();////什么意思？生成的是什么？？？？？？？？？？？？
	private  int gene[] ;
	private  List<Double> routeAndBestUseage;//链路及其利用率，//列表？
	private static List<Integer> mutationIndex = new ArrayList<Integer>();//变异位置,列表
	
        public Chromosome(Map <Key,Edge> edges,List<Business> business) {//这个操作到底有什么功能？，一个表，一个map?
		this.edges = edges;//？？？？
		this.gene = creatGene(businessNum);//任务多少个就有多少个基因
		this.business = business;
		this.routeAndBestUseage = getScore();
	}
   
        private int [] creatGene(int size){//大小，基因列表中存放businessNum个基因
    	    int createGene[] = new int[size];
    	    for (int i = 0; i < size; i++) {
	 		createGene[i] = (int)(Math.random()*3);
            }
	    return createGene;
	}
    
    
        public  Chromosome clone(){//这是什么意思？，Chromosome 下的子类clone
	        if(this == null || this.gene == null){
	        	return null;
	        }
	        Chromosome copy = new Chromosome(edges,business);//这是什么意思？

	        for (int i = 0; i < this.gene.length; i++) {//给这个copy的基因赋值？？？
	        	copy.gene[i] = this.gene[i];
	        }
	        copy.routeAndBestUseage.clear();///应该就是初始化成和被克隆的一模一样的？
	        copy.routeAndBestUseage.addAll(this.routeAndBestUseage);
	        return copy;
        }
	
	public  List<Chromosome> crossover(Chromosome chro1){//交叉
		if(chro1 == null || this == null){
			return null;
		}
		if(chro1.gene == null || this.gene == null){
			return null;
		}
		if(chro1.gene.length != this.gene.length){
			return null;
		}
		Chromosome c1 = chro1.clone();//这两行有什么区别？
		Chromosome c2 = this.clone();
	
		int a = (int)(Math.random()*businessNum);
		int b = (int)(Math.random()*businessNum);
		int start = a > b ? b : a;
		int end = a > b ? a : b;

		for (int i = start;i <= end;i++){//基因交换
			int temp = c2.gene[i];
			c2.gene[i] = c1.gene[i];
			c1.gene[i] = temp;
		}
		c1.setRouteAndBestUseage();//???????????????
		c2.setRouteAndBestUseage();// 看下面的代码this.routeAndBestUseage = getScore();
	
		List<Chromosome> list =  new LinkedList<Chromosome>();//用来存放？？的列表？
		list.add(c1);//增加染色体？
		list.add(c2);
		return list;
	}

	public void mutation(int num){////num是什么 区间数？ 也是变异次数
		int range = 1000 / num;//range是什么 变异区间长度？
		//每次与上次变异位置不同
		for (int i = 0; i < num; i++) {
			int index = (int)( Math.random() * range) + range * i;//将1000个位置分成十份，每一份上一个变异点
			//int index = (int)( Math.random() * 1000 );//每一百个点
			if(mutationIndex.size() < num){//mutationIndex.size 从哪里出来的变量？
				mutationIndex.add(index);
			}else{
				if(mutationIndex.contains(index)){//设置不同变异点
					i = i - 1;
					continue;
				}else{
					mutationIndex.add(i+num, index);
				}
			}
			int number = (int)((3*Math.random()));//number用来干嘛？
			//int number = 2;
			gene[index] = number;//??

		} 
		if(mutationIndex.size() == 2*num){//变异的次数？
			mutationIndex = new ArrayList<Integer>(mutationIndex.subList(num, 2*num));//重置变异数组？
		}
		this.setRouteAndBestUseage();//重置分数
	}
	
	//算链路最大带宽占用率
	public List<Double> getScore(){//双精度的数，
       
		for (int i = 0; i < businessNum; i++) {
			int selcetR[] = business.get(i).getRoute().get(gene[i]);//这个就是算每条链路上的位置？整个路径上的对应基因位置的链路的点还是链路？
		//还是从现在有的三条路径里面选出一条来？
			int requestBandWidth = business.get(i).getRequestBandwidth();//带宽 
			
			
			
			for (int j = 0; j < selcetR.length-1; j++) {
				Key k = new Key(selcetR[j], selcetR[j+1]);//创建一个实例。//key就是一段链路的起点和终点值？？？？？
				Edge e = edges.get(k);//???????/e应该才是 选定的一条edge？对应的链路？edges应该是存放所边的列表？
				e.setBandWidthRest(e.getBandWidthRest() - requestBandWidth);//计算出这条边用了之后的剩下的带宽？
				edges.put(k, e);//???????
		    }
		
	        }
		
		List <Double> maxRateRoute = new LinkedList<Double>(); //应该是存放同一条路径的起点和终点还有利用率？
		maxRateRoute.add((double)0);///初始化？
		maxRateRoute.add((double)0);
		maxRateRoute.add((double)0);
		double maxR = 0;
		for(Key key:edges.keySet()){//edges.keySet()这个函数从哪里来的，怎么用？应该是自带的，所有的key 的集合？还是什么？
		    Edge e = edges.get(key);//按key值从edges里面查找然后获得edge?
			double rate = 1-(double)e.getBandWidthRest()/e.getBandwidth(); 
			e.reset();//重置//这个意义是什么？void reset()，this.bandWidthRest = bandWidth;
		this.bandWidthRest = bandWidth;
			edges.put(key, e);//添加边，因为其实这个key值已经有了，其实就是更新一下这个边 
			if(rate > maxR && rate!= 1.0){
				maxRateRoute.set(0,(double)key.start);//应该是存放同一条路径的起点和终点还有利用率？
				maxRateRoute.set(1,(double)key.end);
				maxRateRoute.set(2,rate);
				maxR = rate;
			}
		}  
	
		return maxRateRoute;//返回最大利用率的链路的信息	
		
	}
	
	public int[] getGene() {
		return gene;//返回基因值
	}

	public void setRouteAndBestUseage() {
		this.routeAndBestUseage = getScore();//这是什么？返回最大利用率的链路的信息？
	}

	public List<Double> getRouteAndBestUseage() {
		return routeAndBestUseage;//什么？
	}

	@Override
	public boolean equals(Object obj) {
		Chromosome chro = (Chromosome)obj;//？
		if(this.routeAndBestUseage.get(2) == chro.getRouteAndBestUseage().get(2)){//比较利用率，作用是什么？
			return true;
		}else{
			return false;
		}
	}
	

}
