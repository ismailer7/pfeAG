package genetic;

import java.util.concurrent.ThreadLocalRandom;

public class AG {


    private String function;
    private double intervalG, intervalD;
    public static final int SIZE = 10;
    public static final double STEP = 0.1;

    public AG(String function, double intervalG, double intervalD) {
        this.function = function;
        this.intervalG = intervalG;
        this.intervalD = intervalD;
    }


    public Point[] populationInitial() {
        // création de la population initiale.
        Point[] init = new Point[SIZE];
        for (int i = 0; i < SIZE; i++) {
            double x = random(intervalG, intervalD);
            double y = random(intervalG, intervalD);
            init[i] = new Point(this.function, x, y);
        }
        return init;
    }

    public Point[] algorithm_genetique(Point[] population) {
        Point[] generationSuivant = new Point[population.length];
        int elm_num = 2;
        Point[] meilleurPoints = elitisme(population, elm_num);
        for (int i = 0; i < meilleurPoints.length; i++) {
            generationSuivant[i] = meilleurPoints[i];
        }

        int c = elm_num;

        do {

            Point p1 = selectionParTournoi(population, 1);
            Point p2 = selectionParTournoi(population, 20);

            Point[] fils = croisement(p1, p2);

            fils[0] = mutation(fils[0]);
            generationSuivant[c] = fils[0];
            c++;
            if(c > SIZE) break;

            fils[1] = mutation(fils[1]);
            generationSuivant[c] = fils[1];
            c++;
            if (c > SIZE) break;

        }while(c < SIZE);

        return generationSuivant;
    }

    public Point[] croisement(Point p1, Point p2) {
        // croisement des deux parent pour générer les deux fils.
        Point[] fils = new Point[2];
        fils[0] = new Point(this.function, p1.getX(), p2.getY());
        fils[1] = new Point(this.function, p2.getX(), p1.getY());
        return fils;
    }

    public Point selectionParTournoi(Point[] population, int tournoiParticipant) {
        // selectionner la plus fort individus dans le tournoi
        Point[] tournoi = new Point[tournoiParticipant];

        for (int i = 0; i < tournoiParticipant; i++) {
            int selected;
            if (tournoiParticipant == 1)
                selected = 0;
            else
                selected = random(0, population.length - 1);

            tournoi[i] = population[selected];
        }

        int selected = 0;
        int j = selected + 1;
        while(j < tournoiParticipant) {
            if(tournoi[selected].getZ() < tournoi[j].getZ()) {
                selected = j;
            }
            j++;
        }


        Point gagnant = tournoi[selected];
        int gagnantIndex = indexApartir(population, gagnant);
        return population[gagnantIndex];

    }



    public Point[] detriureMeilleur(Point[] tempPopulation, int itr) {
        int size_courrant = tempPopulation.length;
        int nouvel_size = size_courrant - 1;
        Point[] det = new Point[nouvel_size];

        int i = 0;
        int j = 0;

        while(i < size_courrant && j < nouvel_size) {
            if(i == itr) {
                i++;
            } else {
                det[j] = tempPopulation[i];
                i++;
                j++;
            }

        }
        return det;
    }

    public Point mutation(Point point) {

        int xm = 2;
        int ym = 5;
        double ancienX = point.getX();
        double ancienY = point.getY();
        int rand = random(1, 10);

        if(xm == rand) {
            // mutation pour le x.
            double nouvel = random(intervalG, intervalD);
            point = new Point(this.function, nouvel, ancienY);
        }
        rand = random(1, 10);
        if (ym == rand) {
            // mutation pour le y.
            double nouvel = random(intervalG, intervalD);
            point = new Point(this.function, ancienX, nouvel);
        }
        return point;

    }



    public Point[] elitisme(Point[] population, int elitismePoucentage) {
        Point[] tempPopulation = population;
        Point[] meilleur = new Point[elitismePoucentage];
        int i = 0;
        while (i < elitismePoucentage) {
            int itr = getMaxFitness(tempPopulation);
            meilleur[i] = tempPopulation[itr];
            tempPopulation = detriureMeilleur(tempPopulation, itr);
            i++;
        }
        return meilleur;
    }



    public int getMaxFitness(Point[] population) {
        double max = -1000;
        int index = 0;
        for (int i = 1; i < population.length; i++) {
            if (population[i].getZ() > max) {
                index = i;
                max = population[i].getZ();
            }
        }
        return index;
    }

    public int indexApartir(Point[] population, Point valeur) {
        for (int i = 0; i < population.length; i++) {
            if(population[i].getX() == valeur.getX() && population[i].getY() == valeur.getY()) {
                return i;
            }
        }
        return -1;
    }


    public double random(double min, double max) { return ThreadLocalRandom.current().nextDouble(min, max); }
    public int random(int min ,int max) { return  ThreadLocalRandom.current().nextInt(min, max); }


}
