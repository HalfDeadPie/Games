package com.example.simon.gamesshop;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by Simon on 24.3.2016.
 */

public class Game
{
    private int genre;

    private int platform;

    private int count;

    private String image;

    private int price;

    private String description;

    private String name;

    private String producer;

    private String pegi;

    private int rating;

    private String language;

    private String video;

    private String uid;

    private String releaseDate;

    private Bitmap coverImage;

    public Bitmap getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(Bitmap coverImage) {
        this.coverImage = coverImage;
    }

    public String getUID(){ return uid;}

    public void setUID(String UID){ this.uid=UID;}

    public int getGenre ()
    {
        return genre;
    }

    public void setGenre (int genre)
    {
        this.genre = genre;
    }

    public int getPlatform ()
    {
        return platform;
    }

    public void setPlatform (int platform)
    {
        this.platform = platform;
    }

    public int getCount ()
    {
        return count;
    }

    public void setCount (int count)
    {
        this.count = count;
    }

    public String getImage ()
    {
        return image;
    }

    public void setImage (String image)
    {
        this.image = image;
    }

    public int getPrice ()
    {
        return price;
    }

    public void setPrice (int price)
    {
        this.price = price;
    }


    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getProducer ()
    {
        return producer;
    }

    public void setProducer (String producer)
    {
        this.producer = producer;
    }

    public String getPegi ()
    {
        return pegi;
    }

    public void setPegi (String pegi)
    {
        this.pegi = pegi;
    }

    public int getRating ()
    {
        return rating;
    }

    public void setRating (int rating)
    {
        this.rating = rating;
    }

    public String getLanguage ()
    {
        return language;
    }

    public void setLanguage (String language)
    {
        this.language = language;
    }

    public String getVideo ()
    {
        return video;
    }

    public void setVideo (String video)
    {
        this.video = video;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseDate ()
    {
        return releaseDate;
    }

    public void vypis(){
        System.out.println("count: "+count);
        System.out.println("description: "+description);
        System.out.println("genre: "+genre);
        System.out.println("image: "+image);
        System.out.println("language: "+language);
        System.out.println("name: "+name);
        System.out.println("pegi: "+pegi);
        System.out.println("platform: "+platform);
        System.out.println("price: "+price);
        System.out.println("producer: "+producer);
        System.out.println("rating: "+ rating);
        System.out.println("release_date: "+ releaseDate);


    }
}

