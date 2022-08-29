package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.Scanner;


/** @autor Грачев Антон
 * Программу решил не зацикливать, в задании нет на это четких указаний
 * Не успеваю доделать, прошу учесть при проверке, что к работе я приступил только 28.08.22
 */
public class App
{
    public static void main( String[] args )
    {
        final int latitude = 6;
        final int longitude = 7;
        double[][] Mass,Mass2;
        float[][]distances, distances2;
        float [][] totalDistances = new float[5][2];
        Mass = sort(latitude);
        Mass2 = sort(longitude);
        float userLatitude,userLongitude;
        userLatitude = 0;
        userLongitude = 0;
        boolean flag = true;

        Scanner sc = new Scanner(System.in);
        while(flag) {
            System.out.println("Введите широту:");
            try {
                userLatitude = sc.nextFloat();
                if(userLatitude <= 90 && userLatitude >= -90) {
                    flag = false;
                }else{
                    System.out.println("Значение широты должны принимать значения в пределах -90 и 90");
                }
            } catch (InputMismatchException e){
                System.out.println("Неверный формат ввода");
                sc.nextLine();

            }


        }
        flag = true;
        while(flag) {
            System.out.println("Введите долготу:");
            try {
                userLongitude = sc.nextFloat();
                if(userLongitude <= 180 && userLongitude >= -180) {
                    flag = false;
                }else{
                    System.out.println("Значение долготы должны принимать значения в пределах -180 и 180");
                }
            } catch (InputMismatchException e){
                System.out.println("Неверный формат ввода");
                sc.nextLine();

            }


        }
        sc.close();
        long m = System.currentTimeMillis();

        distances = countDistances(Mass,nearestCoordinate(Mass,userLatitude),userLatitude,userLongitude);

        distances2 = countDistances(Mass,nearestCoordinate(Mass2,userLongitude),userLatitude,userLongitude);
        for (int i= 0; i<totalDistances.length; i++){

            totalDistances[i][0] = 999999999;
        }
        for (int i= 0; i<10; i++){
            flag = true;
            for (int i1 = 0; i1 < totalDistances.length;i1++){
                if (totalDistances[i1][0] > distances[i][0]){
                    if(flag){
                        totalDistances[i1][0] = distances[i][0];
                        totalDistances[i1][1] = distances[i][1];
                        flag = false;
                    }
                }
            }
            flag = true;
            for (int i1 = 0; i1 < totalDistances.length;i1++){
                if (totalDistances[i1][0] > distances2[i][0]){
                    if(flag){
                        totalDistances[i1][0] = distances2[i][0];
                        totalDistances[i1][1] = distances[i][1];
                        flag = false;
                    }
                }
            }

        }

        String line;

        for (int i= 0; i<5; i++){
            try {
                FileInputStream file = new FileInputStream("airports.dat");
                BufferedReader BReader;
                BReader = new BufferedReader(new InputStreamReader(file));
                for (int i1 = 0; i1 < totalDistances[i][1]; i1++) {
                    BReader.readLine();
                }
                line = BReader.readLine();
                String[] arr = line.split(",");
                if (arr.length == 14) {
                    System.out.println(arr[1] + "    Расстояние: " + totalDistances[i][0] + " метров");
                } else {
                    System.out.println(arr[1] + "," + arr[2] + "    Расстояние: " + totalDistances[i][0] + " метров");
                }

                BReader.close();
                file.close();
            } catch (IOException e) {
                System.out.println("IOException 135");
            }
        }



        System.out.println("Время выполнения: " + (double) (System.currentTimeMillis() - m));

    }

    private static float[][] countDistances(double[][] mass, int position, double index, double indexlng) {
    int amountOfOperations = 5, colNum = 6;
    float earthRadius = 6378137;
    double dLatitude,dLongtitude;
    float a,c;    // переменные формул cos(d) = sin(φА)·sin(φB) + cos(φА)·cos(φB)·cos(λА − λB), L = d·R
        String line;
        float[][] col = new float[amountOfOperations * 2][2];
            FileInputStream file;
        BufferedReader BReader;


    if(position < amountOfOperations){
        position =0;
    }else if (position > mass.length - 1 - amountOfOperations){
        position = mass.length -1 - (amountOfOperations * 2);

    }else {
        position = position + 1 -amountOfOperations;
    }
        try{


        for(int i = 0; i<amountOfOperations * 2;i++) {
            col[i][1] = (float) mass[i][1];

            file = new FileInputStream("airports.dat");
            BReader = new BufferedReader(new InputStreamReader(file));
            for(int i1 = 0; i1<mass[position][1]; i1++){
                BReader.readLine();
            }
            position++;

            line = BReader.readLine();

            String[] arr = line.split(",");


            if (arr.length == 14) {

                dLatitude = Math.toRadians(Double.parseDouble(arr[colNum]) - index);
                dLongtitude = Math.toRadians(Double.parseDouble(arr[colNum + 1]) - indexlng);
                a = (float) (Math.sin(dLatitude / 2) * Math.sin(dLatitude / 2) +
                        Math.cos(Math.toRadians(Double.parseDouble(arr[colNum]))) * Math.cos(Math.toRadians(index)) *
                                Math.sin(dLongtitude / 2) * Math.sin(dLongtitude / 2));


            } else {

                dLatitude = Math.toRadians(Double.parseDouble(arr[colNum + 1]) - index);
                dLongtitude = Math.toRadians(Double.parseDouble(arr[colNum + 2]) - indexlng);
                a = (float) (Math.sin(dLatitude / 2) * Math.sin(dLatitude / 2) +
                        Math.cos(Math.toRadians(Double.parseDouble(arr[colNum + 1]))) * Math.cos(Math.toRadians(index)) *
                                Math.sin(dLongtitude / 2) * Math.sin(dLongtitude / 2));

            }
            c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));

            col[i][0] = earthRadius * c;
            file.close();
            BReader.close();

        }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return col;
    }

    private static int nearestCoordinate(double[][] massCoordinate, double index) { //метод возвращает индекс ближайшего меньшего значения, учитываем это в дальнейшем
    boolean flag = true;
        if (massCoordinate[0][0] >= index){
        return 0;
        }else if(massCoordinate[massCoordinate.length-1][0] <= index){
            return massCoordinate.length-2;
        }


        for(int i = (massCoordinate.length-1)/2, i2 = (massCoordinate.length-1)/2; flag;){

        if(index >= massCoordinate[i][0] && index <= massCoordinate[i+1][0]){
            flag = false;

            return i;
        }else if(index <= massCoordinate[i][0] && index >= massCoordinate[i-1][0]){
            flag = false;

            return i-1;
        }else if(index < massCoordinate[i][0]){
            i = i - i2;

        }else if(index > massCoordinate[i][0]){
            i = i + i2;

        }
        if(i2>1){i2 = i2/2;}

    }

    return 0;
    }


    private static double[][] sort(int colNum){
        FileInputStream file;
        String line;
        double[][] col;
        double temp;
        BufferedReader BReader;
        boolean flag = true;

        try {
            file = new FileInputStream("airports.dat");
            col = new double[(int) Files.lines(Paths.get("airports.dat")).count()][2]; // массив выбранной колонки
            BReader = new BufferedReader(new InputStreamReader(file));

            int i = 0;
            while(((line = BReader.readLine())!=null)) {

                String[] arr = line.split(",");
                if(arr.length == 14){
                    col[i][0] = Double.parseDouble(arr[colNum]);
                }else{
                    col[i][0] = Double.parseDouble(arr[colNum + 1]);
                }
                col[i][1] = i;
                i++;


            }
            file.close();
            BReader.close();
        } catch (FileNotFoundException e) {
            col = new double[0][0];
            System.out.println("Файл airports.dat не найден");
        } catch (IOException e) {
            col = new double[0][0];
            System.out.println("Неверный формат входных данных");
        }

        while(flag){
            flag = false;
            for (int i = 0; i < col.length - 1; i++) {
                if(col[i][0]> col[i+1][0]){
                    temp = col[i][0];
                    col[i][0] = col[i+1][0];
                    col[i+1][0] = temp;

                    temp = col[i][1];
                    col[i][1] = col[i+1][1];
                    col[i+1][1] = temp;
                    
                    flag = true;
                }
            }
        }
        return col;
    }


}
