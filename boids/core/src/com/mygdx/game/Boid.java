package com.mygdx.game;

import java.util.HashMap;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Boid {

    protected GVec position, velocity;
    protected float[] wallConstraints;
    private int dimensionality, zone;

    private static HashMap<BoidAttribute, Float> attrMap = new HashMap<BoidAttribute, Float>();
    private static HashMap<BoidAttribute, Float> scaleMap = new HashMap<BoidAttribute, Float>();

    static {
        attrMap.put(BoidAttribute.WIDTH, 15f);
        attrMap.put(BoidAttribute.HEIGHT, 10f);
        attrMap.put(BoidAttribute.MAXSPEED, 7f);
        attrMap.put(BoidAttribute.SEPARATIONSTRENGTH, 1/10f);
        attrMap.put(BoidAttribute.SEPARATIONRANGE, 30f);
        attrMap.put(BoidAttribute.ALIGNMENTSTRENGTH, 1/64f);
        attrMap.put(BoidAttribute.ALIGNMENTRANGE, 100f);
        attrMap.put(BoidAttribute.COHESIONSTRENGTH, 1/100f);
        attrMap.put(BoidAttribute.COHESIONRANGE, 100f);

        scaleMap.put(BoidAttribute.WIDTH, 10f);
        scaleMap.put(BoidAttribute.HEIGHT, 10f);
        scaleMap.put(BoidAttribute.MAXSPEED, 1f);
        scaleMap.put(BoidAttribute.SEPARATIONSTRENGTH, 1/20f);
        scaleMap.put(BoidAttribute.SEPARATIONRANGE, 10f);
        scaleMap.put(BoidAttribute.ALIGNMENTSTRENGTH, 1/128f);
        scaleMap.put(BoidAttribute.ALIGNMENTRANGE, 10f);
        scaleMap.put(BoidAttribute.COHESIONSTRENGTH, 1/100f);
        scaleMap.put(BoidAttribute.COHESIONRANGE, 20f);
    }

    public Boid(float[] wallConstraints) {

        this.dimensionality = wallConstraints.length;
        this.velocity = GVec.RandomGVec(this.dimensionality);
        this.position = GVec.RandomGVec(this.dimensionality, wallConstraints);
        this.wallConstraints = wallConstraints;
        this.setZone();
    }

    public GVec separation(Boid[] boids) {
        GVec separation = new GVec(this.dimensionality);
        for(int i = 0 ; i < boids.length ; i++) {
            if(boids[i] == this || boids[i].getZone() != this.zone) continue;
            GVec diff = boids[i].getPos().sub(this.position);
            if(diff.abs() < attrMap.get(BoidAttribute.SEPARATIONRANGE)) {
                separation = separation.sub(diff).mul(attrMap.get(BoidAttribute.SEPARATIONSTRENGTH));
            }
        }
        return separation;
    }
    public GVec alignment(Boid[] boids) {
        GVec alignment = new GVec(this.dimensionality);
        int count = 0;
        for(int i = 0 ; i < boids.length ; i++) {
            if(boids[i] == this || boids[i].getZone() != this.zone) continue;
            if(this.dist(boids[i]) < attrMap.get(BoidAttribute.ALIGNMENTRANGE)) {
                alignment = alignment.add(boids[i].getVel());
                count++;
            }
        }
        if(count > 0) {
            alignment = alignment.div(Math.max(1, count)).sub(this.velocity).mul(attrMap.get(BoidAttribute.ALIGNMENTSTRENGTH));
        }
        return alignment;
    }
    public GVec cohesion(Boid[] boids) {
        GVec centerOfMass = new GVec(this.dimensionality);
        int count = 0;
        for(int i = 0 ; i < boids.length ; i++) {
            if(boids[i] == this || boids[i].getZone() != this.zone) continue;
            if(this.dist(boids[i]) < attrMap.get(BoidAttribute.COHESIONRANGE)) {
                centerOfMass = centerOfMass.add(boids[i].getPos());
                count++;
            }
            
        }
        if(count > 0) {
            centerOfMass = centerOfMass.div(Math.max(1, count)).sub(this.position).mul(attrMap.get(BoidAttribute.COHESIONSTRENGTH));
        }
        return centerOfMass;
    }

    public GVec wallConstraint() {
        GVec avoidVel = new GVec(this.dimensionality);
        float v = 2;
        for(int i = 0 ; i < this.wallConstraints.length ; i++) {
            if(this.position.get(i) < 50) {
                avoidVel = avoidVel.addSingle(v, i);
            }
            else if(this.position.get(i) > this.wallConstraints[i] - 50) {
                avoidVel = avoidVel.addSingle(-v, i);
            }
        }
        return avoidVel;
    }
    public void update(Boid[] boids) {
        this.setZone();
        GVec sepVec = this.separation(boids);
        GVec aliVec = this.alignment(boids);
        GVec cohVec = this.cohesion(boids);
        GVec walVec = this.wallConstraint();
        this.velocity = this.velocity.add(sepVec).add(aliVec).add(cohVec).add(walVec);
        // Limit the speed:
        if(this.velocity.abs() > attrMap.get(BoidAttribute.MAXSPEED)) {
            this.velocity = this.velocity.div(this.velocity.abs()).mul(attrMap.get(BoidAttribute.MAXSPEED));
        }

        this.position = this.position.add(this.velocity);
    }
    public void draw(ShapeRenderer renderer) {
        float scaleDepth = this.position.get(2)/this.wallConstraints[2];
        float width = scaleDepth*attrMap.get(BoidAttribute.WIDTH);
        float height = scaleDepth*attrMap.get(BoidAttribute.HEIGHT);
        float scaleColor = this.position.get(3)/this.wallConstraints[3];
        renderer.setColor(BoidAttribute.getColor(scaleColor));
        renderer.rect(this.position.get(0)-width/2, this.position.get(1)-height/2, width/2, height/2, width, height, 1f, 1f, this.velocity.angle());
        
    }
    
    public GVec getPos() {
        return this.position;
    }

    public GVec getVel() {
        return this.velocity;
    }

    public float dist(Boid b) {
        return (float)this.position.sub(b.getPos()).abs();
    }

    public float getZone() {
        return this.zone;
    }

    public void setZone() {
        this.zone = 1;
        for(int i = 0 ; i < this.dimensionality ; i++) {
            this.zone *= Math.max(1, Math.round(this.position.get(i)/this.wallConstraints[i]*Consts.ZONE_STEP_LENGTH));
        }
    }

    public static float getScale(BoidAttribute attr) {
        return scaleMap.get(attr);
    }

    public static float getAttr(BoidAttribute attr) {
        return attrMap.get(attr);
    }

    public static void setAttr(BoidAttribute attr, float modifier) {
        modifier = Math.min(modifier, 10);
        modifier = Math.max(modifier, 0);
        attrMap.put(attr, modifier*scaleMap.get(attr));
    }
}