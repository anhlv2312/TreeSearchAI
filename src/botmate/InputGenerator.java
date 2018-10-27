package botmate;

import java.text.DecimalFormat;
import java.util.*;

public class InputGenerator {

    public static void main(String[] args){
        //print level

        int level = 1;
        System.out.println(level);
        Random r =new Random();
        DecimalFormat df2=new DecimalFormat(".##");
        int lv=level;
        List<String> terrainPool=new ArrayList<>();
        List<String> terrains=new ArrayList<>();
        List<String> driverPool=new ArrayList<>();
        List<String> carPool=new ArrayList<>();

        //print gamma
        double gamma=0.8+((double)r.nextInt(20))/100;
        System.out.print(df2.format(gamma));
        int slipTime=1+r.nextInt(5);
        int breakTime=1+r.nextInt(5);
        System.out.println(" "+slipTime+" "+ breakTime);


        int N=2;//intialize N as 2 first, then update N by level
        int maxT;

        if (lv==1) {
            N=2+r.nextInt(8);
            terrainPool.add("dirt");
            terrainPool.add("asphalt");
            driverPool.add("driverA");
            driverPool.add("driverB");
            carPool.add("carA");
            carPool.add("carB");
        } else if (lv==2) {
            N=2+r.nextInt(8);
            terrainPool.add("dirt-straight");
            terrainPool.add("dirt-slalom");
            terrainPool.add("asphalt-straight");
            terrainPool.add("asphalt-slalom");
            driverPool.add("driverA");
            driverPool.add("driverB");
            carPool.add("carA");
            carPool.add("carB");
            carPool.add("carC");

        } else if (lv>2) {
            N=10+r.nextInt(21);
            terrainPool.add("dirt-straight-hilly");
            terrainPool.add("dirt-straight-flat");
            terrainPool.add("dirt-slalom-hilly");
            terrainPool.add("dirt-slalom-flat");
            terrainPool.add("asphalt-straight-hilly");
            terrainPool.add("asphalt-straight-flat");
            terrainPool.add("asphalt-slalom-hilly");
            terrainPool.add("asphalt-slalom-flat");
            driverPool.add("driverA");
            driverPool.add("driverB");
            driverPool.add("driverC");
            driverPool.add("driverD");
            driverPool.add("driverE");
            carPool.add("carA");
            carPool.add("carB");
            carPool.add("carC");
            carPool.add("carD");
            carPool.add("carE");

        }

        maxT=2*N+r.nextInt(20);
        //print N and maxT
        System.out.println(N+" "+ maxT);

        for (int i=0;i<N;i++) {
            terrains.add(terrainPool.get(r.nextInt(terrainPool.size())));
        }

        //System.out.println("Terrain list is : "+ terrains.toString());

        HashMap<String,List<Integer>> terrainDict= new HashMap<>();
        int cellNum=1;
        for (String terrain: terrains) {

            if (terrainDict.containsKey(terrain)) {
                terrainDict.get(terrain).add(cellNum);
                terrainDict.put(terrain,terrainDict.get(terrain));
            }else{
                List<Integer> newList=new ArrayList<>();
                newList.add(cellNum);
                terrainDict.put(terrain,newList);
            }
            cellNum++;
        }
        //print terrain
        for (java.util.Map.Entry<String,List<Integer>> entry:terrainDict.entrySet()) {
            System.out.println(entry.getKey()+":"+ entry.getValue().toString().replaceAll("[^,0-9]",""));
        }
        //print car count
        System.out.println(carPool.size());
        //print car probabilities
        for (String car: carPool) {
            System.out.println(car+":"+Arrays.toString(randomDouble()).replaceAll("[^0-9. ]",""));
        }
        //print driver count
        System.out.println(driverPool.size());
        //print driver probabilities
        for (String driver: driverPool) {
            System.out.println(driver+":"+Arrays.toString(randomDouble()).replaceAll("[^0-9. ]",""));
        }
        //print tyre probabilities
        System.out.println("all-terrain:"+Arrays.toString(randomDouble()).replaceAll("[^0-9. ]",""));
        System.out.println("mud:"+Arrays.toString(randomDouble()).replaceAll("[^0-9. ]",""));
        System.out.println("low-profile:"+Arrays.toString(randomDouble()).replaceAll("[^0-9. ]",""));
        System.out.println("performance:"+Arrays.toString(randomDouble()).replaceAll("[^0-9. ]",""));

        int NT_CT=terrainPool.size()*carPool.size();

        //print NT*CT fuel efficiencies
        for (int i=0;i<NT_CT;i++) {
            System.out.print((1+r.nextInt(5))+" ");
        }
        System.out.println("\r");
        //print slip probability at 50% pressure for all terrains
        for (int i=0;i<terrainPool.size();i++) {
            System.out.print(((double)r.nextInt(20)+5)/100+ " ");
        }
    }
    public static double[] randomDouble(){
        int count = 12;
        int sum = 100;
        java.util.Random g = new java.util.Random();

        int vals[] = new int[count];
        double result[]=new double[count];
        sum -= count;

        for (int i = 0; i < count-1; ++i) {
            vals[i] = g.nextInt(sum);
        }
        vals[count-1] = sum;

        java.util.Arrays.sort(vals);
        for (int i = count-1; i > 0; --i) {
            vals[i] -= vals[i-1];
        }
        for (int i = 0; i < count; ++i) { ++vals[i]; }

        for (int i = 0; i < count; ++i) {
            result[i]=(double)vals[i]/100;
            //System.out.printf("%.2f ",x);
        }
        //System.out.printf("\n");
        return result;
    }
}
