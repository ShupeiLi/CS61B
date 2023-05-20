public class Planet {
    public double xxPos;
    public double yyPos;
    public double xxVel;
    public double yyVel;
    public double mass;
    public String imgFileName;
    private static final double G = 6.67e-11;

    public Planet(double xP, double yP, double xV, double yV, double m, String img) {
        xxPos = xP;
        yyPos = yP;
        xxVel = xV;
        yyVel = yV;
        mass = m;
        imgFileName = img;
    }

    public Planet(Planet p) {
        xxPos = p.xxPos;
        yyPos = p.yyPos;
        xxVel = p.xxVel;
        yyVel = p.yyVel;
        mass = p.mass;
        imgFileName = p.imgFileName;
    }

    public double calcDistance(Planet p) {
        double dx = this.xxPos - p.xxPos;
        double dy = this.yyPos - p.yyPos;
        return Math.sqrt(dx * dx + dy *dy);
    }

    public double calcForceExertedBy(Planet p) {
        double r = this.calcDistance(p);
        return (Planet.G * this.mass * p.mass) / (r * r);
    }

    public double calcForceExertedByX(Planet p) {
        double force = this.calcForceExertedBy(p);
        double r = this.calcDistance(p);
        return force * ((p.xxPos - this.xxPos) / r);
    }

    public double calcForceExertedByY(Planet p) {
        double force = this.calcForceExertedBy(p);
        double r = this.calcDistance(p);
        return force * ((p.yyPos - this.yyPos) / r);
    }

    public double calcNetForceExertedByX(Planet[] planets) {
        double netForceX = 0;
        for (Planet planet : planets) {
            if (!this.equals(planet)) {
                netForceX += this.calcForceExertedByX(planet);
            }
        }
        return netForceX;
    }

    public double calcNetForceExertedByY(Planet[] planets) {
        double netForceY = 0;
        for (Planet planet : planets) {
            if (!this.equals(planet)) {
                netForceY += this.calcForceExertedByY(planet);
            }
        }
        return netForceY;
    }

    public void update(double time, double forceX, double forceY) {
        double aX = forceX / this.mass;
        double aY = forceY / this.mass;
        this.xxVel += time * aX;
        this.yyVel += time * aY;
        this.xxPos += time * this.xxVel;
        this.yyPos += time * this.yyVel;
    }

    public void  draw() {
        StdDraw.picture(xxPos, yyPos, "images/" + imgFileName);
    }
}
