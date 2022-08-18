package Main;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class Main {

    public static void main(String[] args) throws IOException {

        printInfo();

        FileWriter fw = new FileWriter("C:\\Users\\tarat\\IdeaProjects\\ЧМ 6 лаба\\src\\Main\\Output.txt");
        FileReader fr = new FileReader("C:\\Users\\tarat\\IdeaProjects\\ЧМ 6 лаба\\src\\Main\\Input.txt");


        Scanner scan = new Scanner(fr);

        double H, A, B, C, yc, epsilon;
        Vector<Double> data = new Vector<>();

        while (scan.hasNextLine()) {
            double k = scan.nextDouble();
            data.add(k);
        }
        fr.close();

        A = data.get(0);
        B = data.get(1);
        C = data.get(2);
        yc = data.get(3);
        H = data.get(4);
        epsilon = data.get(5);

        if (A == B) {
            fw.write("ICOD = 3: A равно B");
            fw.close();
            System.out.println("ICOD = 3: A равно B");
            return;
        }
        if ((C - A) * (C - B) != 0) {
            fw.write("ICOD = 3: Не выполнено условие выбора начальной точки");
            fw.close();
            System.out.println("ICOD = 3: Не выполнено условие выбора начальной точки");
            return;
        }
        if (epsilon < 0) {
            fw.write("ICOD = 3: Эпсилон меньше нуля");
            fw.close();
            System.out.println("ICOD = 3: Эпсилон меньше нуля");
            return;
        }

        int numOfIterations = 0;

        Task koshi = new Task(H, A, B, C, yc, epsilon);

        koshi.initializationData();

        //шаг, который укладывается кратное количетсво раз
        System.out.println("Шаг H1, укладывающийся в промежуток кратное число раз: " + koshi.getH1());
        System.out.println();

        //вычисление глобальной погрешности после первой итерации
        double globalPogr = koshi.solveGlobalPogr();
        numOfIterations++;
        //проверка на решение
        if (globalPogr <= koshi.getEpsilon()) {
            printSolution(globalPogr, koshi, fw, numOfIterations);
            return;
        }

        //цикл итераций для нахождения решения
        while (globalPogr > koshi.getEpsilon() && Math.abs(koshi.getH1()) > koshi.getH_min()) {
            double helpPogr = globalPogr;
            koshi.reloadData(globalPogr);
            globalPogr = koshi.solveGlobalPogr();
            if (globalPogr >= helpPogr) {
                fw.write("ICOD = 1: Решение не получено, погрешность перестала уменьшаться");
                fw.close();
                System.out.println("ICOD = 1: Решение не получено, погрешность перестала уменьшаться");
                return;
            }
            numOfIterations++;
        }

        //проверка на ошибку уменьшения шага
        if (Math.abs(koshi.getH1()) <= koshi.getH_min()) {
            fw.write("ICOD = 2: Решение не получено, шаг интегрирования стал недопустимо малым");
            fw.close();
            System.out.println("ICOD = 2: Решение не получено, шаг интегрирования стал недопустимо малым");
            return;
        }

        //проверка на решение
        if (globalPogr <= koshi.getEpsilon()) {
            printSolution(globalPogr, koshi, fw, numOfIterations);
        }

    }
    
    public static void printInfo() {
        System.out.println("Лабораторная работа №6");
        System.out.println("Цель работы: Численное решение задачи Коши для обыкновенных дифференциальных уравнений методами типа Рунге-Кутта");
        System.out.println("Выполнил: Таратонов Вадим Николаевич");
        System.out.println("Проверила: Шабунина Зоя Александровна");
        System.out.println();
    }

    public static void printSolution(double globalPogr, Task koshi, FileWriter fw, int numOfIterations) throws IOException {
        fw.write("ICOD = 0: Завершено в соответствии с назначением\n");
        fw.write("Вычисленное значение погрешности: " + globalPogr + "\n");
        fw.write("Шаг интегрирования, с которым была получена погрешность: " + koshi.getH1() + "\n");
        fw.write("Координата конца отрезка интегрирования: " + koshi.getX0_1() + "\n");
        fw.write("Полученное в конце отрезка интегрирования значение решения: " + koshi.getY0_1() + "\n");
        fw.write("Потребовалось итераций: " + numOfIterations + "\n");
        fw.close();
        System.out.println("ICOD = 0: Завершено в соответствии с назначением");
        System.out.println("Вычисленное значение погрешности: " + globalPogr);
        System.out.println("Шаг интегрирования, с которым была получена погрешность: " + koshi.getH1());
        System.out.println("Координата конца отрезка интегрирования: " + koshi.getX0_1());
        System.out.println("Полученное в конце отрезка интегрирования значение решения: " + koshi.getY0_1());
        System.out.println("Потребовалось итераций: " + numOfIterations);
    }

}


