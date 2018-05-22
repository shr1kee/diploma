import org.apache.commons.math3.stat.regression.SimpleRegression;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.*;
public class Main {
    static boolean[][] used;
    static double delta = 0.3;
    static double[][] herold;
    static double[][] components;
    static int[][] componentsNum;
    static ArrayList<Double> Dvalue = new ArrayList<>();
    static ArrayList<Double> komp = new ArrayList<>();
    static int width = 4;
    static int height = 3;
    static BufferedImage img;
    static int[][] grayscaleIm;
    static BufferedImage bi;
    static WritableRaster raster;
    static double maxV=0.0;
    static double minV =3;
    static Map<Integer,Integer[]> kDMap;
    public static void main(String[] args) throws IOException {
        File f = new File("C:\\Users\\Acer\\Desktop\\test2.jpg");
        init(f);
        toGray(img);
        Herold(grayscaleIm);
        runBfs();
        kDMap = new HashMap<>(komp.size());
        Integer[] a = new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        for (int i = 0; i < komp.size(); i++) {
            kDMap.put(i,a);
        }
//        for (int i = 0; i <width ; i++) {
//            System.out.print(i+" ");
//        }
//        System.out.println();
//        for (int i = 0; i <height ; i++) {
//            System.out.print(i+" ");
//            for (int j = 0; j <width ; j++) {
//                System.out.print(componentsNum[i][j]+" ");
//            }
//            System.out.println();
//        }
        countSqueres();
        countD();

        System.out.println("Size komp: "+komp.size());
        for (int i = 0; i <height ; i++) {
            for (int j = 0; j <width ; j++) {
                int m = (int) (255*components[i][j]/2);
                raster.setSample(j,i,0,m);
            }
        }
        ImageIO.write(bi,"jpg", new File("C:\\Users\\Acer\\Desktop\\componentsValue.jpg"));
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new XYLineChart(komp,Dvalue).setVisible(true);
            }
        });

    }

    public static void countD(){
        int rmax = 10;
        double[] regX = new double[rmax+1];
        double[] regY = new double[rmax+1];
        for (int i = 0; i <kDMap.size() ; i++) {
            SimpleRegression regression = new SimpleRegression();
            for(int r=1; r<=rmax;r++) {
                regX[r] = Math.log(r * 2 + 1);
               // System.out.println("i "+i+"kDmap "+kDMap.get(i)[r]);
                regY[r] = Math.log(kDMap.get(i)[r]);
                regression.addData(regX[r],regY[r]);
            }
           // System.out.println("k "+komp.get(i)+" slope "+(-regression.getSlope()));
            Dvalue.add(-regression.getSlope());
        }
    }
    public static void checkSq(int num,int r,int i1,int i2,int j1,int j2){
        Set set = new HashSet();
        for (int i = i1; i < i2; i++) {
            for (int j = j1; j <j2 ; j++) {
                int c = componentsNum[i][j];
                if(!set.contains(c)){
                    set.add(c);
                    Integer[] ar = kDMap.get(c).clone();
                    ar[r]+=1;
                    kDMap.put(c,ar);
                }
            }
        }
    }
    public static void countSqueres(){
        int rmax = 10;
        for (int r = 1; r <=rmax ; r++) {
            for (int i = 0; i <height ; i+=r) {
                for (int j = 0; j < width; j+=r) {
                    if(i+r>height&&j+r>width)
                        checkSq(1,r,i,height,j,width);
                    else if(i+r>height)
                        checkSq(2,r,i,height,j,j+r);
                    else if(j+r>width)
                        checkSq(3,r,i,i+r,j,width);
                    else
                        checkSq(4,r,i,i+r,j,j+r);
                }
            }
        }
    }
    public static void runBfs(){
        int k = 0;
        for (int i = 0; i <height ; i++) {
            for (int j = 0; j <width ; j++) {
                if(!used[i][j]){
                    komp.add(herold[i][j]);
                    bfs(k,herold[i][j],i,j);
                    k++;
                }
            }
        }
    }
    public static void init(File f) {
        try {
            img = ImageIO.read(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        width = img.getWidth();
        height = img.getHeight();
        System.out.println("width "+width+" height "+height);
        herold = new double[height][width];
        used = new boolean[height][width];
        components = new double[height][width];
        grayscaleIm = new int[height][width];
        componentsNum = new int[height][width];
        bi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        raster = bi.getRaster();

    }

    public static void toGray(BufferedImage img){
        for(int i =0; i<height; i++)
        {
            for (int j = 0; j <width ; j++){
                int p=img.getRGB(j,i);
                int a = (p>>24)&0xff;
                int r = (p>>16)&0xff;
                int g = (p>>8)&0xff;
                int b = p&0xff;
                int avg = (r+g+b)/3;
                p = (a<<24) | (avg<<16) | (avg<<8) | avg;
                img.setRGB(j, i, p);
                Color mycolor = new Color(img.getRGB(j,i));
                int c= mycolor.getRed();
                grayscaleIm[i][j] =  c;
                used[i][j] = false;
            }
        }
    }
    public static void bfs(int k,double a,int x,int y){
        used[x][y] = true;
        components[x][y] = a;
        componentsNum[x][y] = k;
        int[] xy = new int[]{x,y};
        ArrayDeque<int[]> q = new ArrayDeque<>();
        q.addLast(xy);
        while(!q.isEmpty())
        {
            int[] v = q.pop();


                if(v[0]-1>=0 && !used[v[0]-1][v[1]] &&Math.abs(herold[v[0]-1][v[1]]-a)<=delta)
                {
                    used[v[0]-1][v[1]] = true;
                    components[v[0]-1][v[1]] = a;
                    componentsNum[v[0]-1][v[1]] = k;
                    q.addLast(new int[]{v[0] - 1, v[1]});
                }
                if(v[0]+1<height && !used[v[0]+1][v[1]] &&Math.abs(herold[v[0]+1][v[1]]-a)<=delta)
                {
                    used[v[0]+1][v[1]] = true;
                    components[v[0]+1][v[1]] = a;
                    componentsNum[v[0]+1][v[1]] = k;
                    q.addLast(new int[]{v[0]+1, v[1]});
                }
                if(v[1]-1>=0 && !used[v[0]][v[1]-1] &&Math.abs(herold[v[0]][v[1]-1]-a)<=delta)
                {
                    used[v[0]][v[1]-1] = true;
                    components[v[0]][v[1]-1] = a;
                    componentsNum[v[0]][v[1]-1] = k;
                    q.addLast(new int[]{v[0], v[1]-1});
                }
                if(v[1]+1<width && !used[v[0]][v[1]+1] &&Math.abs(herold[v[0]][v[1]+1]-a)<=delta)
                {
                    used[v[0]][v[1]+1] = true;
                    components[v[0]][v[1]+1] = a;
                    componentsNum[v[0]][v[1]+1] = k;
                    q.addLast(new int[]{v[0], v[1]+1});
                }
        }

    }
    public static void Herold(int[][] im)
    {
        int rmax = 20;
        int h = im.length;
        int w = im[0].length;
        double[] regX = new double[rmax];
        double[] regY = new double[rmax];
        for(int i=0; i<rmax;i++)
        {
            regX[i]=Math.log(i*2+1);
        }
        for (int i = 0; i < h ; i++) {
            for (int j = 0; j < w ; j++) {

            }
        }
        int[] used = new int[256];
        for(int i = 0, iter = 1; i < h; i++)
        {
            for(int j = 0; j < w; j++, iter++)
            {
                regY[0] =1; used[im[i][j]] = iter;
                for(int r =1; r < rmax; r++)
                {
                    regY[r] = regY[r-1];
                    int c;
                    if(i - r >=0){
                        c = im[i-r][j];
                        if(used[c] != iter)
                        {
                            used[c] = iter;
                            //regY[r]++;
                            regY[r]+=c;
                        }
                    }
                    if(j-r>=0){
                        c = im[i][j-r];
                        if(used[c]!=iter){
                            used[c] = iter;
                            regY[r]+=c;
                        }
                    }
                    if(i+r < h){
                        c = im[i+r][j];
                        if (used[c]!=iter){
                            used[c]=iter;
                            regY[r]+=c;
                        }
                    }
                    if(j+r<w){
                        c = im[i][j+r];
                        if (used[c] != iter) {
                            used[c] = iter;
                            regY[r]+=c;
                        }
                    }

                }
                SimpleRegression regression = new SimpleRegression();
                for(int r =0; r<rmax; r++) {
                    regY[r] = Math.log(regY[r]);
                    regression.addData(regX[r],regY[r]);
                }
                herold[i][j]=regression.getSlope();
                if(herold[i][j]>maxV)
                    maxV=herold[i][j];
                if(herold[i][j]<minV)
                    minV = herold[i][j];
            }

        }
    }
}
