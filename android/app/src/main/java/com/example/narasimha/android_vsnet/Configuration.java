package com.example.narasimha.android_vsnet;

/**
 * Created by narasimha on 19/7/17.
 */

public class Configuration {
    //Change this in debug mode, when running on local server
    private String myIP;
    private String myPort;
    private boolean THREADED_OPTMIMIZATION;
    public void setMyIP(String myIP) {
        this.myIP = myIP;
    }

    public String getMyIP() {
        return myIP;
    }

    public void setMyPort(String myPort) {
        this.myPort = myPort;
    }

    public String getMyPort() {
        return myPort;
    }

    public void setTHREADED_OPTMIMIZATION(boolean THREADED_OPTMIMIZATION) {
        this.THREADED_OPTMIMIZATION = THREADED_OPTMIMIZATION;
    }

    public boolean isTHREADED_OPTMIMIZATION() {
        return THREADED_OPTMIMIZATION;
    }
}
