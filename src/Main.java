import org.apache.commons.math3.stat.regression.SimpleRegression;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main  {
    static boolean[][] used;
    //0.2 запомни!1111!!!
    static double delta = 0.05;
    static double[][] herold;
    static double[][] components;
    static int[][] componentsNum;
    static ArrayList<Double> Dvalue = new ArrayList<>();
    static ArrayList<Double> komp = new ArrayList<>();
    static ArrayList<Double> finalD = new ArrayList<>();
    static ArrayList<Double> fKomp = new ArrayList<>();

    static int width = 4;
    static int height = 3;
    static BufferedImage img;
    static int[][] grayscaleIm;
    static BufferedImage bi;
    static WritableRaster raster;
    static Map<Integer,Integer[]> kDMap;
    static int rH = 10;//при подсчете Гельдера
    public static void main(String[] args) throws IOException {
//        File f = new File("C:\\Users\\Acer\\Desktop\\test2.jpg");
//        File f = new File("C:\\Users\\Acer\\Desktop\\small.png");
        File f = new File("Moscow.jpg");
        init(f);
        toGray(img);
        Herold(grayscaleIm);
//        for (int i = 0; i <height ; i++) {
//            for (int j = 0; j <width ; j++) {
//                System.out.print(herold[i][j]+" ");
//            }
//            System.out.println();
//        }
        runBfs();
        kDMap = new HashMap<>(komp.size());
        Integer[] a = new Integer[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        for (int i = 0; i < komp.size(); i++) {
            kDMap.put(i,a);
        }
        countSqueres();
        countD();
        System.out.println("Size komp: "+komp.size());
        for (int i = 0; i <height ; i++) {
            for (int j = 0; j < width; j++) {
                int m = (int) (255 * components[i][j] / 2);
                raster.setSample(j, i, 0, m);
            }
        }//        selectD();
        System.out.println("after selection "+finalD.size());
        ImageIO.write(bi,"jpg", new File("C:\\Users\\Acer\\Desktop\\componentsValue.jpg"));
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new XYLineChart(fKomp,finalD).setVisible(true);
            }
        });

    }
    public static void countD(){
        int rmax = 15;
        double[] regX = new double[rmax+1];
        double[] regY = new double[rmax+1];
        for (int i = 0; i <kDMap.size() ; i++) {
            SimpleRegression regression = new SimpleRegression();
//            System.out.println("regY[0] "+regY[0]);
            regY[0] = 100000;
            for(int r=1; r<=rmax;r++) {
                regX[r] = Math.log(r + 1);
               // System.out.println("i "+i+"kDmap "+kDMap.get(i)[r]);
//                System.out.print(kDMap.get(i)[r]+" ");
                regY[r] = kDMap.get(i)[r];

                if(regY[r]<regY[r-1]){
                    double m = Math.log(kDMap.get(i)[r]);
                    System.out.print(kDMap.get(i)[r]+" ");
                    regression.addData(regX[r],m);
                }

            }
            System.out.println();
            if(-regression.getSlope()<3)
            {
                fKomp.add(komp.get(i));
                finalD.add(-regression.getSlope());
            }
        }
    }
    public static void checkSq(int r,int i1,int i2,int j1,int j2){
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
        int rmax = 15;
        for (int r = 1; r <=rmax ; r++) {
            for (int i = rH; i <height-rmax; i+=r) {
                for (int j = rH; j < width-rmax; j+=r) {
                    if(i+r>height&&j+r>width)
                        checkSq(r,i,height,j,width);
                    else if(i+r>height)
                        checkSq(r,i,height,j,j+r);
                    else if(j+r>width)
                        checkSq(r,i,i+r,j,width);
                    else
                        checkSq(r,i,i+r,j,j+r);
                }
            }
        }
    }
    public static void runBfs(){
        int k = 0;
        for (int i = rH; i <height; i++) {
            for (int j = rH; j <width; j++) {
                if(!used[i][j]){
                    komp.add(herold[i][j]);
                    bfs(k,herold[i][j],i,j);
                    k++;
                }
            }
        }

    }
    public static void bfs(int k,double a,int x,int y)
    {
        used[x][y] = true;
        components[x][y] = a;
        componentsNum[x][y] = k;
        for (int i = rH; i < height; i++) {
            for (int j = rH; j < width; j++) {
                if (used[i][j] != true && Math.abs(herold[i][j] - a) < delta) {
                    used[i][j] = true;
                    components[i][j] = a;
                    componentsNum[i][j] = k;
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
    public static void Herold(int[][] im)//im - исходное изображение
    {

        height-=rH;
        width-=rH;
        double[] regX = new double[rH+1];
        double[] regY = new double[rH+1];
        for(int i=0; i<=rH;i++)
            regX[i] = Math.log(i+1);
        for(int i = rH; i < height; i++)
        {
            for(int j = rH; j < width; j++)
            {
                regY[0] = im[i][j];
                if(im[i][j]==0)
                    regY[0] = 1;
                for(int r =1; r <= rH; r++) {
                    regY[r] = regY[r-1];
                    int c;
                    if(i - r >=0) {
                        c = im[i-r][j];
                        regY[r]+=c;
                    }
                    if(j-r>=0){
                        c = im[i][j-r];
                        regY[r]+=c;
                    }
                    if(i+r < height) {
                        c = im[i+r][j];
                        regY[r]+=c;
                    }
                    if(j+r<width) {
                        c = im[i][j+r];
                        regY[r]+=c;
                    }
                }
                SimpleRegression regression = new SimpleRegression();
                for(int r =0; r<=rH; r++) {
                    regY[r] = Math.log(regY[r]);
                    regression.addData(regX[r],regY[r]);
                }
                herold[i][j]=regression.getSlope();
            }

        }

    }



}
