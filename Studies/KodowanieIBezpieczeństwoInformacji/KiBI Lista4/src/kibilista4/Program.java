package kibilista4;

import java.util.Random;

public final class Program {

    static final int MAX = 100000;
    static final int N = 1;
    double[] generated = new double[MAX];
    double[][] comData = new double[5][MAX];//0.1,0.5,0.7,0.8,0.9
    double[] level = {-0.9, 0, 0.9};
    double[] factories = {0.1, 0.5, 0.7, 0.8, 0.9};
    double[][] output = new double[2][5];//średnia kwadratowa, entropia
    double[] quantized = new double[MAX];
    double[] allocation = new double[3];
    double[] probability = new double[MAX];

    Program() {
        generate();
        compute();
        getSolution();
    }

    void generate() {
        Random rand = new Random();
        generated[0] = rand.nextGaussian();
        for (int i = 1; i < MAX; i++) {
            generated[i] = 0.9 * generated[i - 1] + rand.nextGaussian();
        }
    }

    void compute() {
        double factor;
        for (int i = 0; i < 5; i++) {
            factor = factories[i];

            comData[i][0] = 0;
            for (int j = 1; j < MAX; j++) {
                comData[i][j] = factor * generated[j - 1];
            }
            for (int j = 0; j < MAX; j++) {
                comData[i][j] = generated[j] - comData[i][j];
            }
        }
    }

    void getSolution() {
        double rob;
        for (int i = 0; i < 5; i++) {
            rob = 0;
            allocation[0] = 0;
            allocation[1] = 0;
            allocation[2] = 0;
            for (int j = 0; j < MAX; j++) {
                if (comData[i][j] > level[2]) {
                    quantized[j] = level[2];
                    allocation[2]++;
                } else if (comData[i][j] < level[0]) {
                    quantized[j] = level[0];
                    allocation[0]++;
                } else {
                    quantized[j] = level[1];
                    allocation[1]++;
                }
                rob += (quantized[j] - comData[i][j]) * (quantized[j] - generated[j]);
            }
            rob /= MAX;
            output[0][i] += rob / N;
            findProbability();
            getEntropy(i);
        }
    }

    void getEntropy(int index) {
        double entropy = 0;
        for (int i = 0; i < 3; i++) {
            if (probability[i] == 0) {
                continue;
            } else {
                entropy += (probability[i] * (-base2log(probability[i])));
            }
        }
        output[1][index] += entropy / N;
    }

    void findProbability() {
        for (int i = 0; i < 3; i++) {
            probability[i] = allocation[i] / MAX;
        }
    }

    private static double base2log(double input) {
        return Math.log(input) / Math.log(2);
    }
}
