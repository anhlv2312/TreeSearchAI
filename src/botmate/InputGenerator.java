package botmate;

import simulator.Step;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class InputGenerator {

    public static void main(String[] args) {
        String outputString = generateInput(5);
        System.out.println(outputString);
    }

    public static void generateInputFile(int level, String fileName) {
        try (BufferedWriter output = new BufferedWriter(new FileWriter(fileName))) {
            String outputString = generateInput(level);
            output.write(outputString);
            System.out.println(outputString);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public static String generateInput(int level) {
        StringBuilder sb = new StringBuilder();

        sb.append(level);
        sb.append("\n");
        Random r =new Random();
        DecimalFormat df2=new DecimalFormat(".##");

        List<String> terrainPool=new ArrayList<>();
        List<String> environment=new ArrayList<>();
        List<String> driverPool=new ArrayList<>();
        List<String> carPool=new ArrayList<>();
        List<String> tirePool=new ArrayList<>();

        //print gamma
        double gamma=0.8+((double)r.nextInt(20))/100;
        sb.append(df2.format(gamma));
        int slipTime=1+r.nextInt(5);
        int breakTime=1+r.nextInt(5);
        sb.append(" ");
        sb.append(slipTime);
        sb.append(" ");
        sb.append(breakTime);
        sb.append("\n");


        int N=2; //intialize N as 2 first, then update N by level
        int maxT;

        if (level==1) {
            N=8+r.nextInt(5);
            terrainPool.add("dirt");
            terrainPool.add("asphalt");
            driverPool.add("driverA");
            driverPool.add("driverB");
            carPool.add("carA");
            carPool.add("carB");
        } else if (level==2) {
            N=8+r.nextInt(5);
            terrainPool.add("dirt-straight");
            terrainPool.add("dirt-slalom");
            terrainPool.add("asphalt-straight");
            terrainPool.add("asphalt-slalom");
            driverPool.add("driverA");
            driverPool.add("driverB");
            carPool.add("carA");
            carPool.add("carB");
            carPool.add("carC");

        } else if (level>2) {
            N=30+r.nextInt(10);
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


        tirePool.add("all-terrain");
        tirePool.add("mud");
        tirePool.add("low-profile");
        tirePool.add("performance");

        maxT=2*N+r.nextInt(10);
        //print N and maxT
        sb.append(N);
        sb.append(" ");
        sb.append(maxT);
        sb.append("\n");

        for (int i=0;i<N;i++) {
            environment.add(terrainPool.get(r.nextInt(terrainPool.size())));
        }

        //System.out.println("Terrain list is : "+ terrains.toString());

        //TODO: make sure the environment has everytype of terrain

        HashMap<String,List<Integer>> terrainDict = new HashMap<>();
        int cellNum=1;
        for (String terrain: environment) {

            if (terrainDict.containsKey(terrain)) {
                terrainDict.get(terrain).add(cellNum);
            }else{
                List<Integer> newList=new ArrayList<>();
                newList.add(cellNum);
                terrainDict.put(terrain,newList);
            }
            cellNum++;
        }
        //print terrain
        for (String terrainType:terrainPool) {

            sb.append(terrainType);
            sb.append(":");
            List<Integer> terrainDictEntry = terrainDict.get(terrainType);
            if (terrainDictEntry != null) {
                sb.append(terrainDict.get(terrainType).toString().replaceAll("[^,0-9]", ""));
            } else {
                sb.append("1");
            }
            sb.append("\n");
        }
        //print car count
        sb.append(carPool.size());
        sb.append("\n");
        //print car probabilities
        for (String car: carPool) {
            sb.append(car);
            sb.append(":");
            sb.append(Arrays.toString(randomDouble()).replaceAll("[^0-9. ]",""));
            sb.append("\n");
        }
        //print driver count
        sb.append(driverPool.size());
        sb.append("\n");
        //print driver probabilities
        for (String driver: driverPool) {
            sb.append(driver);
            sb.append(":");
            sb.append(Arrays.toString(randomDouble()).replaceAll("[^0-9. ]",""));
            sb.append("\n");
        }
        //print tyre probabilities

        for (String tire: tirePool) {
            sb.append(tire);
            sb.append(":");
            sb.append(Arrays.toString(randomDouble()).replaceAll("[^0-9. ]",""));
            sb.append("\n");
        }

        int NT_CT=terrainPool.size()*carPool.size();

        //print NT*CT fuel efficiencies
        for (int i=0;i<NT_CT;i++) {
            sb.append(1 + r.nextInt(5));
            sb.append(" ");
        }
        sb.append("\n");
        //print slip probability at 50% pressure for all terrains
        for (int i=0;i<terrainPool.size();i++) {
            sb.append((double)r.nextInt(10 + (10 * level))/100);
            sb.append(" ");
        }

        return sb.toString();
    }

    private static double[] randomDouble(){
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
