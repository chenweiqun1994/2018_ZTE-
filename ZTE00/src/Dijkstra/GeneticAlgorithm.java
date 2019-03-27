package Dijkstra;
import java.text.DecimalFormat;
/**
 * @author zyf
 * @Desciption GA to solve problem 
 */
import java.util.*;
public  class GeneticAlgorithm {
	private List<Chromosome> population = new ArrayList<Chromosome>();
	private int popSize = 50;//种群规模
	private int iterationNum = 500; //迭代
	private double mutationRate ;//突变率
	private double mutationRate1  = 0.5;//突变率
	private double mutationRate2 = 0.01;//突变率
	private int maxMutationNum = 10;//突变率
	private double crossoverRate ;//交叉率
	private double crossoverRate1 = 0.9;//交叉率
	private double crossoverRate2 = 0.6;//交叉率
	private int currentGeneration;//当代；当前阶段
	
	private double bestScore;
	private List<Double> bestRouteAndUseage;//最好路线？
	private Chromosome bestChro ; //？？？？
	private double worstScore;
	private double totalScore;//总分？
	private double averageScore;
	
	private int geneI;
	
	private Map <Key,Edge> edges = new HashMap<Key,Edge>();//这到底是什么操作？
	private List<Business> business = new LinkedList<Business>();

	
	public GeneticAlgorithm(Map <Key,Edge> edges,List<Business> business) {//这到底是什么操作？
		this.edges = edges;
		this.business = business;
	}
	
	public void calculate(){// 计算；估计
		System.out.println("Starting....");
		currentGeneration = 1;
		init();
		
		while(currentGeneration < iterationNum){
				evolve();//这到底是什么操作？
				print();//这到底是什么操作？
				currentGeneration++;
		}
	
		System.out.println("--------------------------------");
		System.out.println("The Calculation is over.");
	}

	private void init(){
		population = new ArrayList<Chromosome>();//[生物] 种群，[生物] 群体；全体居民 所有染色体组成的列表？
		
		for (int i = 0; i < popSize ; i++) {//种群规模
			population.add(new Chromosome(edges,business));//不断种群规模？
		}
		 calculateScore();//这到底是什么操作？
	}

	private void print(){
		System.out.println("---------------------------------");
		System.out.println("the currentGeneration is :"+currentGeneration);
		System.out.println("the best bestScore is :"+bestScore);
		System.out.println("the worst fitness is :"+worstScore);
		System.out.println("the average fitness is :"+averageScore);
		System.out.println("the total fitness is :"+totalScore);
		System.out.println("geneI :"+geneI+"\tbestScore:"+bestScore+", 节点："+bestRouteAndUseage.get(0)+"-"+bestRouteAndUseage.get(1));
		
	}
	       
	
	private void evolve(){
		List <Chromosome> childPopulation = new ArrayList<Chromosome>();//子种群
		while(childPopulation.size() < popSize -1){//？？？？
			Chromosome c1 = getParentChromosome();//？？？？
			Chromosome c2 = getParentChromosome();//？？？？
			int hamDis = hammingDis(c1.getGene(), c2.getGene());//？？？？
			double hamD = (double)hamDis/1000;

			crossoverRate = hamD;//交叉概率为汉明距离，汉明距离越近，相似度越高，交叉概率越小
			
//				double score = Math.min(c1.getRouteAndBestUseage().get(2), c2.getRouteAndBestUseage().get(2));
//				if(score < averageScore){
//					crossoverRate = crossoverRate1 - ((crossoverRate1 - crossoverRate2)*(averageScore - score)) / ( averageScore - bestScore);
//				}else{
//					crossoverRate = crossoverRate1;
//				}
				if(Math.random() < crossoverRate){
					List <Chromosome> child = c1.crossover(c2);//？？？？
					for(Chromosome chro : child){//？？？？
						childPopulation.add(chro);
					}
				}
			
		}
		childPopulation.add(bestChro);//？？？？
		List<Chromosome> temp = population;
		population = childPopulation;//？？？？
		temp.clear();
		temp = null;
		mutation();//？？？？
		calculateScore();//？？？？
	}
	//锦标赛选择//？？？？
	private Chromosome getParentChromosome(){//？？？？
		int size = (int)(0.8 * popSize);
		List <Chromosome> selectChro = new ArrayList<Chromosome>();//？？？？
		for (int i = 0; i < size; i++) {
			int index = (int)(Math.random() * popSize);
			selectChro.add(population.get(index));//选择第index个种群
		}
		Chromosome chro = null;
		double bScore = 1;//？？？？
		for (int i = 0; i < selectChro.size(); i++) {
			double score = selectChro.get(i).getRouteAndBestUseage().get(2);//是利用率？
		    if(score < bScore){
		    	chro = selectChro.get(i);
		    	bScore = score;
		    }
		}
		return chro;
	}
	

	private void calculateScore(){
		bestScore = population.get(0).getRouteAndBestUseage().get(2);//初始化？
		worstScore = population.get(0).getRouteAndBestUseage().get(2);
		totalScore = 0;
		for(int i = 0;i < population.size();i++){
			double score = population.get(i).getRouteAndBestUseage().get(2);
			if(score <= bestScore){
				bestScore = score;
				geneI = currentGeneration;//???
			    bestRouteAndUseage = population.get(i).getRouteAndBestUseage();
			    bestChro = population.get(i);
			}
			if(score > worstScore){
				worstScore = score;//???
			}
			totalScore += score;
		}
		
		 averageScore = totalScore / popSize;//??? 总分除以种群规模
		 averageScore = averageScore < bestScore ? bestScore : averageScore; //因为精度问题导致的平均值小于最好值，将平均值设置成最好值   
	}
	
	private void mutation(){//???变异
		for(Chromosome chro : population){//???
			if(chro.equals(bestChro)){
				continue;
			}
			double score = chro.getRouteAndBestUseage().get(2);
			
			if(score < averageScore){
				mutationRate = mutationRate1 - ((mutationRate1 - mutationRate2) * (score - bestScore)) / ( averageScore - bestScore);
			}else{
				mutationRate = mutationRate1;
			}
			
			if(Math.random() < mutationRate){
				chro.mutation(maxMutationNum);//???
			}
		}
	}
//求汉明距离，即两个个体对应位置编码不同的个数
   private int hammingDis(int gene1[],int gene2[]){
	   int hamDis = 0;
	   for (int i = 0; i < 1000; i++) {
		 if(gene1[i] != gene2[i]){
			 hamDis++;
		 }
	   }
	   return hamDis;
   }
 
//   private int hammingRevDis(int gene1[],int gene2[]){
//	   int hamRevDis = 0;
//	   for (int i = 0; i < 1000; i++) {
//		 if(gene1[i] != gene2[999 - i]){
//			 hamRevDis++;
//		 }
//	   }
//	   return hamRevDis;
//   }
	
   public String[] printResult(){//输出结果//???
	   String []result = null ;//???
	   result = new String[2001];//???
	   result[0] = (int)(bestRouteAndUseage.get(0).doubleValue())+" "+(int)(bestRouteAndUseage.get(1).doubleValue())+" "+new DecimalFormat("0.00").format(bestRouteAndUseage.get(2)*100);
	   for (int i = 1; i < 2000; i=i+2) {
		result[i] = business.get(i/2).getBusinessId() + " " + business.get(i/2).getRequestBandwidth();//这是什么？
	        result[i+1] = business.get(i/2).getRouteS().get(bestChro.getGene()[i/2]).toString();//???
 

	   }
	   return result;
   }
   
   public String[] printSelect(){
	   String [] s = new String[1000];
	   for (int i = 0; i < 1000; i++) {
		   s[i] = String.valueOf(bestChro.getGene()[i]);
	   }
	   return s;
   }
}
