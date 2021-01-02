package com.mygdx.game;

public class GVec {
    public float[] v;
    public int len;

    public GVec(float[] v) {
        this.v = v;
        this.len = v.length;
    }
    public GVec(int n) {
        this.v = new float[n];
        this.len = n;
    }
    public static GVec RandomGVec(int n, float[] scales) {
        float[] v = new float[n];
        for(int i = 0 ; i < n ; i++) {
            v[i] = ((float)Math.random()*2f - 1f)*scales[i];
        }
        return new GVec(v);
    }
    public static GVec RandomGVec(int n) {
        float[] scales = new float[n];
        for(int i = 0 ; i < n ; i++) {
            scales[i] = 1;
        }
        return RandomGVec(n, scales);
    }

    public float get(int i) {
        return this.v[i];
    }
    public float angle() {
        return (float)(Math.atan2(this.v[1], this.v[0]))*180f/(float)Math.PI;
    }
    public GVec add(GVec vec) {
        float[] temp = this.cloneArr();
        for(int i = 0 ; i < this.len ; i++) {
            temp[i] += vec.get(i);
        }
        return new GVec(temp);
    }
    public GVec sub(GVec vec) {
        float[] temp = this.cloneArr();
        for(int i = 0 ; i < this.len ; i++) {
            temp[i] -= vec.get(i);
        }
        return new GVec(temp);
    }
    public GVec div(float den) {
        float[] temp = this.cloneArr();
        for(int i = 0 ; i < this.len ; i++) {
            temp[i] /= den;
        }
        return new GVec(temp);
    }
    public GVec mul(float mul) {
        float[] temp = this.cloneArr();
        for(int i = 0 ; i < this.len ; i++) {
            temp[i] *= mul;
        }
        return new GVec(temp);
    }
    public float abs() {
        float temp = 0;
        for(int i = 0 ; i < this.len ; i++) {
            temp += (float)Math.pow(this.v[i], 2);
        }
        return (float)Math.sqrt(temp);
    }
    public String toString() {
        String temp = "[";
        for(int i = 0 ; i < this.len ; i++) {
            temp += this.v[i] + ",";
        }
        temp += "]";
        return temp;
    }
    public float[] asArray() {
        return this.v;
    }
    public GVec addSingle(float term, int dim) {
        float[] temp = this.cloneArr();
        temp[dim] += term;
        return new GVec(temp);
    }
    public int length() {
        return this.len;
    }
    public float[] cloneArr() {
        float[] clone = new float[this.len];
            for(int i = 0 ; i < len ; i++) {
                clone[i] = this.v[i];
            }
        return clone;
    }
}
