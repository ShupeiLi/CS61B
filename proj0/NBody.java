public class NBody {
    public static double readRadius(String fileName) {
        In in = new In(fileName);
        int row = in.readInt();
        return in.readDouble();
    }

    public static Planet[] readPlanets(String fileName) {
        In in = new In(fileName);
        int row = in.readInt();
        Planet[] planets = new Planet[row];
        double radius = in.readDouble();
        for (int i = 0; i < row; i++) {
            planets[i] = new Planet(in.readDouble(), in.readDouble(), in.readDouble(), in.readDouble(), in.readDouble(), in.readString());
        }
        return planets;
    }

    public static void main(String[] args) {
        double T = Double.parseDouble(args[0]);
        double dt = Double.parseDouble(args[1]);
        String filename = args[2];
        Planet[] planets = readPlanets(filename);
        double radius = readRadius(filename);

        StdDraw.enableDoubleBuffering();
        StdDraw.setScale(-radius, radius);
        StdDraw.clear();
        StdDraw.picture(0, 0, "images/starfield.jpg");
        for (Planet planet : planets) {
            planet.draw();
        }
        double time = 0;
        while (time < T) {
            double[] xForces = new double[planets.length];
            double[] yForces = new double[planets.length];
            for (int idx = 0; idx < planets.length; idx++) {
                Planet planet = planets[idx];
                xForces[idx] = planet.calcNetForceExertedByX(planets);
                yForces[idx] = planet.calcNetForceExertedByY(planets);
            }
            for (int idx = 0; idx < planets.length; idx++) {
                Planet planet = planets[idx];
                planet.update(dt, xForces[idx], yForces[idx]);
            }
            StdDraw.clear();
            StdDraw.picture(0, 0, "images/starfield.jpg");
            for (Planet planet : planets) {
                planet.draw();
            }
            StdDraw.show();
            StdDraw.pause(10);
            time += dt;
        }
        StdOut.printf("%d\n", planets.length);
        StdOut.printf("%.2e\n", radius);
        for (Planet planet : planets) {
            StdOut.printf("%11.4e %11.4e %11.4e %11.4e %11.4e %12s\n",
                    planet.xxPos, planet.yyPos, planet.xxVel,
                    planet.yyVel, planet.mass, planet.imgFileName);
        }
    }
}
