package Main;

import java.util.Vector;

public class Task {
    private final double H;
    private final double A;
    private final double B;
    private final double C;
    private final double yc;
    private final double epsilon;
    private double x0_1, y0_1, x0_2, y0_2;
    private double H1, H1_2, H_min;
    private double distance, numOfHeats;

    public Task(double H, double A, double B, double C, double yc, double epsilon) {
        this.H = H;
        this.A = A;
        this.B = B;
        this.C = C;
        this.yc = yc;
        this.epsilon = epsilon;
    }

    public double getEpsilon() {
        return epsilon;
    }

    public double getH_min() {
        return H_min;
    }

    public double getH1() {
        return H1;
    }

    public double getX0_1() {
        return x0_1;
    }

    public double getY0_1() {
        return y0_1;
    }

    public double macheps() {
        double R = 1;
        while ((1 + R) > 1) {
            R = R / 2;
        }
        return R * 2;
    }

    public double Max(double A, double B, double sigma) {
        double temp = Math.max(Math.abs(A), Math.abs(B));
        return Math.max(Math.abs(temp), Math.abs(sigma));
    }

    public double searchMinStepH1(double H, double A, double B) {
        double H1;
        double distance = Math.abs(B - A);
        distance = (double) (Math.round(distance * 1000)) / 1000;
        H = (double) (Math.round(H * 1000)) / 1000;
        double check = distance / H;
        check = (double) (Math.round(check * 1000)) / 1000;
        if (Math.abs(H * Math.floor(check) - distance) < 0.00000000001)
            H1 = H;
        else {
            H1 = H;
            check = distance / H1;
            check = (double) (Math.round(check * 1000)) / 1000;
            boolean flag = true;
            while (flag) {
                if (Math.abs(H1 * Math.floor(check) - distance) < 0.00000000001)
                    flag = false;
                else {
                    H1 = H1 - 0.001;
                    H1 = (double) (Math.round(H1 * 1000)) / 1000;
                    check = distance / H1;
                    check = (double) (Math.round(check * 1000)) / 1000;
                }
            }
        }
        double sigma = 0.00000000000001;
        double H_min = macheps() * Max(A, B, sigma);
        if (H1 < H_min) {
            H1 = H_min;
        }
        return H1;
    }

    public double f(double x, double y) {
        return 120 * x * x * x * x;
        //return 4 * x * x * x - 15 * x * x;
        //return 3 * x * x + 14 * x + 1;
        //return 2 * x + y - x * x;
    }

    public void initializationData() {
        //шаг, который укладывается кратное количетсво раз
        H1 = searchMinStepH1(H, A, B);
        //шаг для половинного деления
        H1_2 = H1 / 2;
        //расстояние между точками
        distance = Math.abs(B - A);
        //количество интервалов
        numOfHeats = Math.abs((int) (distance / H1));
        //х0 и у0
        x0_1 = x0_2 = A;
        y0_1 = y0_2 = yc;
        //если в качества начальной точке будет выбран конец отрезка
        if (C == B) {
            H1 = -H1;
            x0_1 = x0_2 = B;
            H1_2 = -H1_2;
        }
        double sigma = 0.00000000000001;
        H_min = macheps() * Max(A, B, sigma);

    }

    public void reloadData(double globalPogr) {
        H1 = H1_2;
        H1_2 = H1_2 / 2;
        x0_1 = x0_2 = A;
        y0_1 = y0_2 = yc;
        if (C == B) {
            x0_1 = x0_2 = B;
        }
        numOfHeats = Math.abs((int) (distance / H1));
    }

    public Vector<Double> rungeKutt() {
        //коэффициенты для вычисления
        double y1_1, y1_2, K1, K2, K3;
        //локальная погрешность
        double localPogr;
        //массив локальных погрешностей
        Vector<Double> vectorOfLocal = new Vector<>();

        for (int i = 1; i <= numOfHeats; i++) {
            K1 = f(x0_1, y0_1);
            K2 = f(x0_1 + 0.5 * H1, y0_1 + 0.5 * H1 * K1);
            K3 = f(x0_1 + H1, y0_1 - H1 * K1 + 2 * H1 * K2);
            y1_1 = y0_1 + (H1 / 6) * (K1 + 4 * K2 + K3);
            x0_1 = x0_1 + H1;
            y0_1 = y1_1;
            y0_1 = (double) (Math.round(y0_1 * 1000000000)) / 1000000000;


            for (int j = 0; j < 2; j++) {
                K1 = f(x0_2, y0_2);
                K2 = f(x0_2 + 0.5 * H1_2, y0_2 + 0.5 * H1_2 * K1);
                K3 = f(x0_2 + H1_2, y0_2 - H1_2 * K1 + 2 * H1_2 * K2);
                y1_2 = y0_2 + (H1_2 / 6) * (K1 + 4 * K2 + K3);
                x0_2 = x0_2 + H1_2;
                y0_2 = y1_2;
            }
            y0_2 = (double) (Math.round(y0_2 * 1000000000)) / 1000000000;

            localPogr = Math.abs(y0_2 - y0_1) / 0.9375;//0.875
            localPogr = (double) (Math.round(localPogr * 1000000000)) / 1000000000;
            vectorOfLocal.add(localPogr);
            y0_2 = y0_1;
        }
        x0_1 = (double) (Math.round(x0_1 * 1000000000)) / 1000000000;
        return vectorOfLocal;
    }

    public double solveGlobalPogr() {
        Vector<Double> vectorOfLocal = rungeKutt();
        double globalPogr = 0;
        for (Double aDouble : vectorOfLocal) {
            globalPogr += aDouble;
        }
        globalPogr = globalPogr / numOfHeats;
        return globalPogr;
    }
}