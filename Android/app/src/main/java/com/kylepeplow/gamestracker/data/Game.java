package com.kylepeplow.gamestracker.data;

import java.util.Vector;

public class Game
{
    private String mName = "";
    private String mCode = "";
    private Region mRegion = null;
    private Publisher mPublisher = null;
    private Developer mDeveloper = null;
    private Vector<Platform> mPlatforms = new Vector<Platform>(1,1);
    private boolean mHasBeenUpdated = false;
    public Game()
    {

    }

    public void setName(String name){ mName = name; }
    public void setCode(String code){ mCode = code; }
    public void setPublisher(Publisher publisher){ mPublisher = publisher; }
    public void setDeveloper(Developer developer){ mDeveloper = developer; }

    public void addPlatform(Platform platform)
    {
        //Do some wizardry to not only update platform total
        //Such as Switch: 1 is now Switch: 2
        //We want the user to be able to maintain the number of
        //copies own and whether or not that copy is a CE (Collector's Edition)
        //Maybe leave room for handling various Edition(s) for a given game.
    }

    public void removePlatform(Platform platform)
    {
        //See Add Platform
    }

    public String getName(){ return mName; }
    public String getCode(){ return mCode; }
    public Publisher getPublisher(){ return mPublisher; }
    public Developer getDeveloper(){ return mDeveloper; }
    public Platform[] getPlatforms() { return (Platform[])mPlatforms.toArray();}
}
