package com.simu;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by isen on 13/06/2015.
 */
public class RigidBody {
    public final float LINEAR_DRAG_COEFF = 1.25f;
    public final float RHO = 1.225f;
    public final float MAX_THRUST = 10.0f;
    public final float MIN_THRUST = 0.0f;
    public final float DTHRUST = 0.001f;
    public final float STEERING_FORCE = 3.0f;
    public final float PI = 3.14f;

    protected float _mass;
    protected float _inertia;
    protected float _inertiaInverse;


    protected Vector3 _position;
    protected Vector3 _velocity;
    protected Vector3 _velocityBody;
    protected Vector3 _angularVelocity;

    protected float _speed;
    protected float _orientation;

    protected Vector3 _forces;
    protected Vector3 _moment;

    protected float _thrustForce;
    protected Vector3 _pThrust;
    protected Vector3 _sThrust;

    protected float _width;
    protected float _length;
    protected float _height;

    protected Vector3 _cd;
    protected Vector3 _ct;
    protected Vector3 _cpt;
    protected Vector3 _cst;

    protected float _projectedArea;

    public RigidBody() {
        _mass = 100;
        _inertia = 500;
        _inertiaInverse = 1 / _inertia;
        _position = new Vector3(0, 0, 0);
        _width = 10;
        _length = 20;
        _height = 5;
        _orientation = 0;

        _cd = new Vector3(-0.25f * _length, 0.0f, 0.0f);
        _ct = new Vector3(-0.5f * _length, 0.0f, 0.0f);
        _cpt = new Vector3(0.5f * _length, -0.5f * _width, 0.0f);
        _cst = new Vector3(0.5f * _length, 0.5f * _width, 0.0f);
        _angularVelocity = new Vector3(0, 0, 0);
        _velocityBody = new Vector3(0, 0, 0);
        _velocity = new Vector3(0, 0, 0);


        _projectedArea = (_length + _width) / 2 * _height;
        _thrustForce = 100;
    }
    public void calcLoads(){
        Vector3 fb = new Vector3(0.0f, 0.0f, 0.0f);
        Vector3 mb = new Vector3(0.0f, 0.0f, 0.0f);
        Vector3 thrust = new Vector3(1.0f, 0.0f, 0.0f);

        _forces = new Vector3(0.0f, 0.0f, 0.0f);
        _moment = new Vector3(0.0f, 0.0f, 0.0f);

        thrust.x *= _thrustForce;
        thrust.y *= _thrustForce;
        thrust.z *= _thrustForce;

        Vector3 localVelocity;
        float localSpeed;
        Vector3 dragVector;
        float tmp;
        Vector3 resultant;
        Vector3 vTmp = new Vector3(0.0f, 0.0f, 0.0f);

        vTmp = _angularVelocity.crs(_cd);
        localVelocity = _velocityBody.add(vTmp);
        localSpeed = localVelocity.dst(0, 0, 0);

        if(localSpeed > 1.0f) {
            localVelocity = localVelocity.nor();
            localVelocity.x *= -1;
            localVelocity.y *= -1;
            localVelocity.z *= -1;
            dragVector = localVelocity;

            tmp = 0.5f * RHO * localSpeed * localSpeed * _projectedArea;
            dragVector.x *= LINEAR_DRAG_COEFF * tmp;
            dragVector.y *= LINEAR_DRAG_COEFF * tmp;
            dragVector.z *= LINEAR_DRAG_COEFF * tmp;
            resultant = dragVector;

            fb = fb.add(resultant);

            vTmp = _cd.crs(resultant);
            mb = mb.add(vTmp);
        }

        fb = _pThrust;

        vTmp = _cpt.crs(_pThrust);

        mb = mb.add(vTmp);

        fb = fb.add(_sThrust);

        vTmp = _cst.crs(_sThrust);
        mb = mb.add(vTmp);

        fb = fb.add(thrust);

        _forces = rotate2D(_orientation, fb);

        _moment = _moment.add(mb);

    }
    public void updateBodyEuler(double dt){
        Vector3 a;
        Vector3 dv;
        Vector3 ds;
        float aa;
        float dav;
        float dr;

        calcLoads();

        a = _forces;
        a.x /=  _mass;
        a.y /= _mass;
        a.z /= _mass;

        dv = a;
        dv.x *= dt;
        dv.y *= dt;
        dv.z *= dt;
        _velocity = _velocity.add(dv);

        ds = _velocity;
        ds.x *= dt * 59;
        ds.y *= dt * 59;
        ds.z *= dt * 59;
        _position = _position.add(ds);

        aa = _moment.z / _inertia;

        dav = aa * (float)dt;
        _angularVelocity.z += dav;

        dr = RadiansToDegrees((float) (_angularVelocity.z * dt));
        _orientation += dr;

        _speed = _velocity.dst(0, 0, 0);
        _velocityBody =  rotate2D(_orientation, _velocity);

    }
    public void setThrusters(boolean p, boolean s){
        _pThrust = new Vector3(0, 0, 0);
        _sThrust = new Vector3(0, 0, 0);
        if(p) {
            _pThrust.y = STEERING_FORCE;
        }
        if(s) {
            _sThrust.y = -STEERING_FORCE;
        }
    }
    public void modulateThrust(boolean up){
        double dT = up ? DTHRUST : -DTHRUST;

        _thrustForce += dT;

        if(_thrustForce > MAX_THRUST) {
            _thrustForce = MAX_THRUST;
        }
        if(_thrustForce < MIN_THRUST) {
            _thrustForce = MIN_THRUST;
        }
    }

    public float RadiansToDegrees(float rad) {
        return rad * 180.0f / PI;
    }

    float DegreesToRadians(float deg)
    {
        return deg * PI / 180.0f;
    }

    Vector3 rotate2D( float angle, Vector3 u)
    {
        float x,y;
        x = (float)(u.x * Math.cos(DegreesToRadians(-angle)) +  u.y * Math.sin(DegreesToRadians(-angle)));
        y = (float)(-u.x * Math.sin(DegreesToRadians(-angle)) + u.y * Math.cos(DegreesToRadians(-angle)));
        return new Vector3( x, y, 0);
    }

    public void setPosition(float x, float y, float z) {
        _position.x = x;
        _position.y = y;
        _position.z = z;
    }

    public final Vector3 getPosition() {
        return _position;
    }

    public final float getWidth() {
        return _width;
    }

    public final float getHeight() {
        return _height;
    }

    public final float getOrientation() {
        return _orientation;
    }
}
